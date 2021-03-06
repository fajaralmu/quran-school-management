package com.fajar.schoolmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.schoolmanagement.entity.Menu; 

public interface MenuRepository extends JpaRepository<Menu, Long> {

//	@Query(nativeQuery = true, value = "select * from menu where page like '?1%'") 
	List<Menu> findByMenuPage_code(String code);

	Menu findTop1ByUrl(String url);

	Menu findByCode(String code);

}
