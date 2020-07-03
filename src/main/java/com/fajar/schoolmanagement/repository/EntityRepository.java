package com.fajar.schoolmanagement.repository;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.persistence.JoinColumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.setting.EntityManagementConfig;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.service.entity.BaseEntityUpdateService;
import com.fajar.schoolmanagement.service.entity.EntityUpdateInterceptor;
import com.fajar.schoolmanagement.util.EntityUtil;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Data
public class EntityRepository {

	@Autowired
	private WebConfigService webConfigService;

	@Autowired
	private RepositoryCustomImpl repositoryCustom;
	@Autowired
	private ApplicationContext applicationContext;

	@Setter(value = AccessLevel.NONE)
	@Getter(value = AccessLevel.NONE)
	private final Map<String, EntityManagementConfig> entityConfiguration = new HashMap<String, EntityManagementConfig>();

	/**
	 * put configuration to entityConfiguration map
	 * 
	 * @param _class
	 * @param updateService
	 * @param updateInterceptor
	 */
	private void putConfig(Class<? extends BaseEntity> _class, BaseEntityUpdateService updateService,
			EntityUpdateInterceptor updateInterceptor) {
		String key = _class.getSimpleName().toLowerCase();
		entityConfiguration.put(key, config(key, _class, updateService, updateInterceptor));
	}
 

	@PostConstruct
	public void init() throws Exception {
		entityConfiguration.clear();

//		/**
//		 * commons
//		 */
//		toCommonUpdateService(Capital.class, Cost.class, Page.class, StudentParent.class, Student.class, Teacher.class,
//				Profile.class, DonationOrphan.class, StudentQuistionare.class);
//
//		/**
//		 * special
//		 */
//		putConfig(User.class, userUpdateService);
//		putConfig(Menu.class, commonUpdateService);//, EntityUpdateInterceptor.menuInterceptor());
//		putConfig(CostFlow.class, costFlowUpdateService);
//		putConfig(CapitalFlow.class, fundUpdateService);
//		putConfig(DonationThursday.class, fundUpdateService);
//		putConfig(DonationMonthly.class, fundUpdateService);
//
//		/**
//		 * unable to update
//		 */
//		putConfig(CashBalance.class, baseEntityUpdateService);

		putEntitiesConfig();
	}

	private void putEntitiesConfig() throws Exception {

		List<Type> persistenceClasses = webConfigService.getEntityClassess();
		for (Type type : persistenceClasses) {
			try {
				Class<? extends BaseEntity> entityClass = (Class<? extends BaseEntity>) type;
				Dto dtoInfo = EntityUtil.getClassAnnotation(entityClass, Dto.class);
				if (null == dtoInfo) {
					continue;
				}
				Class<? extends BaseEntityUpdateService> updateServiceClass = dtoInfo.updateService();
				BaseEntityUpdateService updateServiceBean = applicationContext.getBean(updateServiceClass);
				EntityUpdateInterceptor updateInterceptor = ((BaseEntity) entityClass.newInstance())
						.updateInterceptor();

				log.info("Registering entity config: {}, updateServiceBean: {}", entityClass, updateServiceBean);

				putConfig(entityClass, updateServiceBean, updateInterceptor);
			} catch (Exception e) {
				log.error("Error registering entity: {}", type);
				e.printStackTrace();
			}

		}
	}

	/**
	 * get entity configuration from map by entity code
	 * 
	 * @param key
	 * @return
	 */
	public EntityManagementConfig getConfig(String entityCode) {
		return entityConfiguration.get(entityCode);
	}

	/**
	 * construct EntityManagementConfig object
	 * 
	 * @param object
	 * @param class1
	 * @param commonUpdateService2
	 * @param updateInterceptor
	 * @return
	 */
	private EntityManagementConfig config(String object, Class<? extends BaseEntity> class1,
			BaseEntityUpdateService commonUpdateService2, EntityUpdateInterceptor updateInterceptor) {
		return new EntityManagementConfig(object, class1, commonUpdateService2, updateInterceptor);
	}

	/**
	 * save entity
	 * 
	 * @param <T>
	 * @param baseEntity
	 * @return
	 */
	public <T extends BaseEntity, ID> T save(T baseEntity) {
		log.info("execute method save");

		boolean joinEntityExist = validateJoinColumn(baseEntity);

		if (!joinEntityExist) {

			throw new InvalidParameterException("JOIN COLUMN INVALID");
		}

		try {
			return savev2(baseEntity);
//			JpaRepository<T, ID> repository = (JpaRepository<T, ID>) findRepo(baseEntity.getClass());
//			log.info("found repo: " + repository);
//			return (T) repository.save((T) baseEntity);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	public <T extends BaseEntity> T savev2(T entity) {
		return repositoryCustom.saveObject(entity);

	}

	public <T extends BaseEntity> boolean validateJoinColumn(T baseEntity) {

		List<Field> joinColumns = getJoinColumn(baseEntity.getClass());

		if (joinColumns.size() == 0) {
			return true;
		}

		for (Field field : joinColumns) {

			try {
				field.setAccessible(true);
				Object value = field.get(baseEntity);
				if (value == null || (value instanceof BaseEntity) == false) {
					continue;
				}

				BaseEntity entity = (BaseEntity) value;
				JpaRepository repository = findRepo(entity.getClass());
				Optional result = repository.findById(entity.getId());

				if (result.isPresent() == false) {
					return false;
				}

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}

		return true;
	}

	public List<Field> getJoinColumn(Class<? extends BaseEntity> clazz) {

		List<Field> joinColumns = new ArrayList<>();
		Field[] fields = clazz.getFields();

		for (Field field : fields) {
			if (field.getAnnotation(JoinColumn.class) != null) {
				joinColumns.add(field);
			}
		}

		return joinColumns;
	}

	/**
	 * find suitable repository (declared in this class) for given entity object
	 * 
	 * @param entityClass
	 * @return
	 */
	public <T extends BaseEntity> JpaRepository findRepo(Class<T> entityClass) {

		JpaRepository repository = webConfigService.getJpaRepository(entityClass);

		return repository;
	}

	/**
	 * find by id
	 * 
	 * @param clazz
	 * @param ID
	 * @return
	 */
	public <ID, T extends BaseEntity> T findById(Class<T> clazz, ID ID) {
		log.info("find {} By Id: {}", clazz.getSimpleName(), ID);
		JpaRepository<T, ID> repository = findRepo(clazz);

		log.info("found repo : {} for {}", repository.getClass(), clazz);

		Optional<T> result = repository.findById(ID);
		if (result.isPresent()) {
			return result.get();
		}
		log.debug("{} is NULL", clazz.getSimpleName());
		return null;
	}

	/**
	 * find all entity
	 * 
	 * @param clazz
	 * @return
	 */
	public <T extends BaseEntity> List<T> findAll(Class<T> clazz) {
		JpaRepository repository = findRepo(clazz);
		if (repository == null) {
			return new ArrayList<>();
		}
		return repository.findAll();
	}

	/**
	 * delete entity by id
	 * 
	 * @param id
	 * @param class1
	 * @return
	 */
	public boolean deleteById(Long id, Class<? extends BaseEntity> class1) {
		log.info("Will delete entity: {}, id: {}", class1.getClass(), id);

		try {

			JpaRepository repository = findRepo(class1);
			repository.deleteById(id);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public EntityManagementConfig getConfiguration(String key) {
		return this.entityConfiguration.get(key);
	}

}
