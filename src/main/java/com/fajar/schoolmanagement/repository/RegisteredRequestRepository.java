package com.fajar.schoolmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.schoolmanagement.entity.RegisteredRequest;

public interface RegisteredRequestRepository extends JpaRepository<RegisteredRequest, Long>{

	RegisteredRequest findTop1ByRequestId(String requestId);

}
