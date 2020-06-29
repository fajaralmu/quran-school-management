package com.fajar.schoolmanagement.repository;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.Capital;
import com.fajar.schoolmanagement.entity.CapitalFlow;
import com.fajar.schoolmanagement.entity.CashBalance;
import com.fajar.schoolmanagement.entity.Cost;
import com.fajar.schoolmanagement.entity.CostFlow;
import com.fajar.schoolmanagement.entity.DonationMonthly;
import com.fajar.schoolmanagement.entity.DonationOrphan;
import com.fajar.schoolmanagement.entity.DonationThursday;
import com.fajar.schoolmanagement.entity.Menu;
import com.fajar.schoolmanagement.entity.Page;
import com.fajar.schoolmanagement.entity.Profile;
import com.fajar.schoolmanagement.entity.Student;
import com.fajar.schoolmanagement.entity.StudentParent;
import com.fajar.schoolmanagement.entity.StudentQuistionare;
import com.fajar.schoolmanagement.entity.Teacher;
import com.fajar.schoolmanagement.entity.User;
import com.fajar.schoolmanagement.entity.setting.EntityManagementConfig;
import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.service.entity.BaseEntityUpdateService;
import com.fajar.schoolmanagement.service.entity.CommonUpdateService;
import com.fajar.schoolmanagement.service.entity.CostFlowUpdateService;
import com.fajar.schoolmanagement.service.entity.EntityUpdateInterceptor;
import com.fajar.schoolmanagement.service.entity.GeneralFundUpdateService;
import com.fajar.schoolmanagement.service.entity.UserUpdateService;
import com.fajar.schoolmanagement.util.CollectionUtil;
import com.sun.beans.TypeResolver;

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
	private CommonUpdateService commonUpdateService;
	@Autowired
	private UserUpdateService userUpdateService;
	@Autowired
	private CostFlowUpdateService costFlowUpdateService;
	@Autowired
	private GeneralFundUpdateService fundUpdateService;
	@Autowired
	private BaseEntityUpdateService baseEntityUpdateService;

	@PersistenceContext
	private EntityManager entityManager;

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

	/**
	 * put configuration to entityConfiguration without entityUpdateInterceptor
	 * 
	 * @param class1
	 * @param commonUpdateService2
	 */
	private void putConfig(Class<? extends BaseEntity> class1, BaseEntityUpdateService commonUpdateService2) {
		putConfig(class1, commonUpdateService2, null);

	}

	/**
	 * set update service to commonUpdateService and NO update interceptor
	 * 
	 * @param classes
	 */
	private void toCommonUpdateService(Class<? extends BaseEntity>... classes) {
		for (int i = 0; i < classes.length; i++) {
			putConfig(classes[i], commonUpdateService);
		}
	}

	@PostConstruct
	public void init() {
		entityConfiguration.clear();

		/**
		 * commons
		 */
		toCommonUpdateService(Capital.class, Cost.class, Page.class, StudentParent.class, Student.class, Teacher.class,
				Profile.class, DonationOrphan.class, StudentQuistionare.class);

		/**
		 * special
		 */
		putConfig(User.class, userUpdateService);
		putConfig(Menu.class, commonUpdateService, EntityUpdateInterceptor.menuInterceptor());
		putConfig(CostFlow.class, costFlowUpdateService);
		putConfig(CapitalFlow.class, fundUpdateService);
		putConfig(DonationThursday.class, fundUpdateService);
		putConfig(DonationMonthly.class, fundUpdateService);

		/**
		 * unable to update
		 */
		putConfig(CashBalance.class, baseEntityUpdateService);
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
	public <T extends BaseEntity> T save(T baseEntity) {
		log.info("execute method save");

		boolean joinEntityExist = validateJoinColumn(baseEntity);

		if (!joinEntityExist) {

			throw new InvalidParameterException("JOIN COLUMN INVALID");
		}

		try {
			JpaRepository repository = findRepo(baseEntity.getClass());
			log.info("found repo: " + repository);
			return (T) repository.save(baseEntity);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
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
	public < T extends BaseEntity> JpaRepository findRepo(Class<T> entityClass) {

		log.info("will find repo by class: {}", entityClass); 
		
		List<JpaRepository<?, ?>> jpaRepositories = webConfigService.getJpaRepositories();
		int index = 0;
		
		for (JpaRepository<?, ?> jpaObject : jpaRepositories) {
			log.info("{}-Repo : {}", index, jpaObject);
			Class<?> beanType = jpaObject.getClass();
			Type originalEntityClass = getJpaRepositoryFirstTypeArgument(beanType, entityClass);

			if (originalEntityClass != null && originalEntityClass.equals(entityClass)) {

				return (JpaRepository ) jpaObject;

			}
		}

		return null;
	}

	/**
	 * find by id
	 * 
	 * @param clazz
	 * @param ID
	 * @return
	 */
	public <ID, T extends BaseEntity> T findById(Class<T> clazz, ID ID) {
		JpaRepository<T, ID> repository = findRepo(clazz);

		Optional<T> result = repository.findById(ID);
		if (result.isPresent()) {
			return result.get();
		}
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

	public static Type getJpaRepositoryFirstTypeArgument(Class<?> clazz, Class<?> entityClass) {
		Type[] interfaces = clazz.getGenericInterfaces();

		log.debug("Check if {} is the meant repository");
		if (interfaces == null) {
			log.info("{} interfaces is null", clazz);
			return null;
		}

		log.debug("clazz {} interfaces size: {}", clazz, interfaces.length);
		CollectionUtil.printArray(interfaces);
		
		for (Type type : interfaces) {

			boolean isJpaRepository = type.getTypeName().startsWith(Repository.class.getCanonicalName());

			if (isJpaRepository) {
				Type _type = TypeResolver.resolve(clazz, entityClass);
				log.debug("_type: {}", _type);
				if(_type.equals(entityClass)) {
					return _type;
				}
//				ParameterizedType parameterizedType = (ParameterizedType) type;
//
//				if (parameterizedType.getActualTypeArguments() != null
//						&& parameterizedType.getActualTypeArguments().length > 0) {
//					return (T) parameterizedType.getActualTypeArguments()[0];
//				}
				
			}
		}

		return null;
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
