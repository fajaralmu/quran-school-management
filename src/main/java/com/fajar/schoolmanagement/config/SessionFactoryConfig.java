package com.fajar.schoolmanagement.config;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.fajar.schoolmanagement.service.WebConfigService;
import com.fajar.schoolmanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SessionFactoryConfig {
	private static SessionFactory factory; 

	@Autowired 
	private DriverManagerDataSource driverManagerDataSource;
	@Autowired
	private EntityManagerFactory entityManagerFactoryBean;
//	@Autowired
	private WebConfigService webConfigService;
	@Autowired
	private ApplicationContext applicationContext;
	
	@Bean 
	public SessionFactory generateSession() {
		webConfigService = (WebConfigService) applicationContext.getBean("webAppConfig");
		
		log.info("=============SESSION FACTORY========== webConfigService:{}",webConfigService);
		try {
			org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();

			configuration.setProperties(hibernateProperties());
			
			addAnnotatedClass(configuration);
			final ServiceRegistry serviceRegistry =  new StandardServiceRegistryBuilder()
					.applySettings( hibernateProperties() )
					.build();
			factory = configuration//./* setInterceptor(new HibernateInterceptor()). */buildSessionFactory();
					.buildSessionFactory(serviceRegistry);
			
			log.info("Session Factory has been initialized");
			return factory;
		} catch (Exception ex) {
			
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}

	}
	
	private void addAnnotatedClass(org.hibernate.cfg.Configuration configuration) {
		
		List<Type> entities = webConfigService.getEntityClassess();
		for (Type type : entities) {
			log.info("addAnnotatedClass: {}", type);
			configuration.addAnnotatedClass(EntityUtil.castObject(type));
		}
		
	}

	
	private Properties hibernateProperties() {
		
		String dialect = entityManagerFactoryBean.getProperties().get("hibernate.dialect").toString();
		String ddlAuto = entityManagerFactoryBean.getProperties().get("hibernate.hbm2ddl.auto").toString();
		String showSql = entityManagerFactoryBean.getProperties().get("hibernate.show_sql").toString();
		
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", dialect);
		properties.setProperty("hibernate.connection.url", driverManagerDataSource.getUrl());
		properties.setProperty("hibernate.connection.username", driverManagerDataSource.getUsername());
		properties.setProperty("hibernate.connection.password", driverManagerDataSource.getPassword());
		
		properties.setProperty("hibernate.connection.driver_class", com.mysql.jdbc.Driver.class.getCanonicalName());
		properties.setProperty("hibernate.current_session_context_class", "thread");
		properties.setProperty("hibernate.show_sql", showSql);
		properties.setProperty("hibernate.connection.pool_size", "1");
		properties.setProperty("hbm2ddl.auto", ddlAuto);
		return properties;
	}

	@Bean
	public Session hibernateSession(SessionFactory sessionFactory) {

		return sessionFactory.openSession();
	}

}
