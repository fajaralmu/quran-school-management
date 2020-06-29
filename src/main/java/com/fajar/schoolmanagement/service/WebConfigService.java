package com.fajar.schoolmanagement.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.schoolmanagement.annotation.Dto;
import com.fajar.schoolmanagement.config.LogProxyFactory;
import com.fajar.schoolmanagement.entity.Profile;
import com.fajar.schoolmanagement.repository.ProfileRepository;
import com.fajar.schoolmanagement.util.EntityUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * this class is registered in the XML config
 * 
 * @author Republic Of Gamers
 *
 */
@Dto
@Data
@Slf4j
public class WebConfigService {

	@Autowired
	private ProfileRepository shopProfileRepository;
	@Autowired
	private ApplicationContext applicationContext;

	private String basePage;
	private String uploadedImageRealPath;
	private String uploadedImagePath;
	private String reportPath;
	private String martCode;
	
	private List<JpaRepository> jpaRepositories = new ArrayList<>();

	@PreDestroy
	public void preDestroy() {
		System.out.println("========= will destroy ========");

	}

	public void getJpaReporitoriesBean() {
		log.info("//////////////GET JPA REPO BEANS///////////////");
		this.jpaRepositories.clear();
		String[] beanNames = applicationContext.getBeanNamesForType(JpaRepository.class);
		if (null == beanNames)
			return;
		log.info("JPA REPOSITORIES: " + beanNames.length);
		for (int i = 0; i < beanNames.length; i++) {
			String beanName = beanNames[i];
			JpaRepository beanObject = (JpaRepository) applicationContext.getBean(beanName);
			jpaRepositories.add(beanObject);
			log.info(i + "." + beanName);
		}
	}

	public static String readFile(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			String everything = sb.toString();
			return everything;
		} finally {
			br.close();
		}
	}

	public static void main(String[] args) throws IOException {

	}

	public static String filter(String content) {
		String[] lines = (content.split("\n"));
		String result = "";
		String firstLine = lines[0].trim();
		if (firstLine.startsWith("INSERT") == false) {
			firstLine = firstLine.substring(3, firstLine.length());
			firstLine = firstLine.replace("MMB", "DEVELOPMENT");
		}

		for (String string : lines) {
			if (string.trim().equals(";")) {
				continue;
			}

			String insertStatement = string.substring(3, string.length());
			if (insertStatement.startsWith("INSERT")) {
				string = string.substring(3, string.length());
			}

			string = string.trim();
			if (string.startsWith(",(")) {
				string = string.substring(0, string.length());
				string = firstLine + string;
			}
			if (string.endsWith(")")) {
				string += ";";
			}
			string = string.replace("MMB", "DEVELOPMENT");
			string = string.replace("VALUES,", "VALUES");
			result += "\n" + string;

			System.out.println(string);
		}
		return result;
	}

	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
		Profile dbProfile = shopProfileRepository.findByMartCode(martCode);
		if (null == dbProfile) {
			shopProfileRepository.save(defaultProfile());
		}
		getJpaReporitoriesBean();
	}

	public Profile getProfile() {
		Profile dbProfile = shopProfileRepository.findByMartCode(martCode);

		/* return getShopProfileFromSession(); */ return EntityUtil.validateDefaultValue(dbProfile);
	}

	private Profile defaultProfile() {
		Profile profile = new Profile();
		profile.setName("Universal Good Shop");
		profile.setAddress("Spring Mvc, Java Virtual Machine, Win 10 64");
		profile.setContact("087737666614");
		profile.setWebsite("http://localhost:8080/universal-good-shop");
		profile.setIconUrl("DefaultIcon.BMP");
		profile.setBackgroundUrl("DefaultBackground.BMP");
		profile.setMartCode(martCode);
		profile.setShortDescription("we provide what u need");
		profile.setColor("green");
		profile.setAbout("Nam libero tempore.");
		return profile;
	}

}
