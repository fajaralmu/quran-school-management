package com.fajar.schoolmanagement.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.dto.WebRequest;
import com.fajar.schoolmanagement.dto.WebResponse;
import com.fajar.schoolmanagement.entity.BaseEntity;

@Service
public class MessagingService {

	public List<BaseEntity> getMessages(String requestId) {
		// TODO Auto-generated method stub
		return new ArrayList<BaseEntity>();
	}

	public WebResponse sendMessage(WebRequest request, HttpServletRequest httpRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	public WebResponse getMessages(HttpServletRequest httpRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	public WebResponse replyMessage(WebRequest request, HttpServletRequest httpRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
