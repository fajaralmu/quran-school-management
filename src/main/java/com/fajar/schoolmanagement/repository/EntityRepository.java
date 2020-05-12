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
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.entity.BaseEntity;
import com.fajar.schoolmanagement.entity.Capital;
import com.fajar.schoolmanagement.entity.CapitalFlow;
import com.fajar.schoolmanagement.entity.CashBalance;
import com.fajar.schoolmanagement.entity.Cost;
import com.fajar.schoolmanagement.entity.CostFlow;
import com.fajar.schoolmanagement.entity.Menu;
import com.fajar.schoolmanagement.entity.Page;
import com.fajar.schoolmanagement.entity.Profile;
import com.fajar.schoolmanagement.entity.Student;
import com.fajar.schoolmanagement.entity.StudentParent;
import com.fajar.schoolmanagement.entity.Teacher;
import com.fajar.schoolmanagement.entity.User;
import com.fajar.schoolmanagement.entity.setting.EntityManagementConfig;
import com.fajar.schoolmanagement.service.entity.BaseEntityUpdateService;
import com.fajar.schoolmanagement.service.entity.CapitalFlowUpdateService;
import com.fajar.schoolmanagement.service.entity.CommonUpdateService;
import com.fajar.schoolmanagement.service.entity.CostFlowUpdateService;
import com.fajar.schoolmanagement.service.entity.MenuUpdateService;
import com.fajar.schoolmanagement.service.entity.ProfileUpdateService;
import com.fajar.schoolmanagement.service.entity.StudentUpdateService;
import com.fajar.schoolmanagement.service.entity.TeacherUpdateService;
import com.fajar.schoolmanagement.service.entity.UserUpdateService;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Data
public class EntityRepository {

	/**
	 * jpaRepositories
	 */
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private TeacherRepository teacherRepository;
	@Autowired
	private StudentParentRepository studentParentRepository;
	@Autowired
	private ProfileRepository shopProfileRepository;
	@Autowired
	private RegisteredRequestRepository registeredRequestRepository; 
	@Autowired
	private MenuRepository menuRepository; 
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserRoleRepository userRoleRepository; 
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private CostRepository costRepository;
	@Autowired
	private CostFlowRepository CostFlowRepository;
	@Autowired
	private CapitalRepository CapitalRepository;
	@Autowired
	private CapitalFlowRepository CapitalFlowRepository;
	
	/**
	 * end jpaRepositories
	 */
	
	
	@Autowired
	private CommonUpdateService commonUpdateService;
	@Autowired
	private MenuUpdateService menuUpdateService;
	@Autowired
	private UserUpdateService userUpdateService;
	@Autowired
	private ProfileUpdateService schoolProfileUpdateService;
	@Autowired
	private StudentUpdateService studentUpdateService;
	@Autowired
	private TeacherUpdateService teacherUpdateService;
	@Autowired
	private CostFlowUpdateService costFlowUpdateService;
	@Autowired
	private CapitalFlowUpdateService capitalUpdateService;
	@Autowired
	private BaseEntityUpdateService baseEntityUpdateService;
	
	@PersistenceContext
	private EntityManager entityManager;

	@Setter(value = AccessLevel.NONE)
	@Getter(value = AccessLevel.NONE)
	private final Map<String, EntityManagementConfig> entityConfiguration = new HashMap<String, EntityManagementConfig>();

	private void put(Class<? extends BaseEntity> _class , BaseEntityUpdateService updateService) {
		String key = _class.getSimpleName().toLowerCase();
		entityConfiguration.put(key, config( key, _class, updateService));
	}
	
	@PostConstruct
	public void init() {
		entityConfiguration.clear();
		
		/**
		 * commons
		 */ 
		put(Capital.class, commonUpdateService);
		put(Cost.class, commonUpdateService);
		put(Page.class, commonUpdateService);
		put(StudentParent.class, commonUpdateService); 
		
		/**
		 * special
		 */
		put(Student.class, studentUpdateService) ;
		put(Teacher.class, teacherUpdateService) ;  
		put( User.class, userUpdateService) ;
		put( Menu.class, menuUpdateService);
		put(Profile.class, schoolProfileUpdateService);
		put(CostFlow.class, costFlowUpdateService);
		put(CapitalFlow.class, capitalUpdateService);
		/**
		 * unable to update
		 */
		put(CashBalance.class, baseEntityUpdateService); 
	}
	
	public EntityManagementConfig getConfig(String key) {
		return entityConfiguration.get(key);
	}

	private EntityManagementConfig config(String object, Class<? extends BaseEntity> class1,
			BaseEntityUpdateService commonUpdateService2) { 
		return new EntityManagementConfig(object, class1, commonUpdateService2);
	}

	/**
	 * save entity
	 * @param <T>
	 * @param baseEntity
	 * @return
	 */
	public <T> T save(BaseEntity baseEntity) {
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

	public boolean validateJoinColumn(BaseEntity baseEntity) {

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
	 * @param entityClass
	 * @return
	 */
	public JpaRepository findRepo(Class<? extends BaseEntity> entityClass) {

		log.info("will find repo by class: {}", entityClass);

		Class<?> clazz = this.getClass();
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {

			if (field.getAnnotation(Autowired.class) == null) {
				continue;
			}

			Class<?> fieldClass = field.getType();
			Class<?> originalEntityClass = getGenericClassIndexZero(fieldClass);

			if (originalEntityClass != null && originalEntityClass.equals(entityClass)) {
				try {
					return (JpaRepository) field.get(this);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					return null;
				}
			}
		}

		return null;
	}

	/**
	 * find all entity
	 * @param clazz
	 * @return
	 */
	public List findAll(Class<? extends BaseEntity> clazz) {
		JpaRepository repository = findRepo(clazz);
		if (repository == null) {
			return new ArrayList<>();
		}
		return repository.findAll();
	}

	public static <T> T getGenericClassIndexZero(Class clazz) {
		Type[] interfaces = clazz.getGenericInterfaces();

		if (interfaces == null) {
			log.info("interfaces is null");
			return null;
		}

		log.info("interfaces size: {}", interfaces.length);

		for (Type type : interfaces) {

			boolean isJpaRepository = type.getTypeName().startsWith(JpaRepository.class.getCanonicalName());

			if (isJpaRepository) {
				ParameterizedType parameterizedType = (ParameterizedType) type;

				if (parameterizedType.getActualTypeArguments() != null
						&& parameterizedType.getActualTypeArguments().length > 0) {
					return (T) parameterizedType.getActualTypeArguments()[0];
				}
			}
		}

		return null;
	}

	/**
	 * delete entity by id
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
		return  this.entityConfiguration.get(key);
	}

}
