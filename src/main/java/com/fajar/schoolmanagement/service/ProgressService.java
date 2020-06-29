package com.fajar.schoolmanagement.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.schoolmanagement.config.LogProxyFactory;

@Service
public class ProgressService {
	
	@Autowired
	private RealtimeService2 realtimeService;
	
	private double currentProgress=  0.0;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}
	
	public void init(String requestId) {
		currentProgress = 0.0;
		realtimeService.sendProgress(1,requestId);
	}
	
	/**
	 * 
	 * @param taskProgress    progressPoportion for current task
	 * @param maxProgressOfCurrentTask totalProportion for current task
	 * @param overallProcessProportion     task Proportion for whole request
	 * @param newProgress
	 * @param requestId
	 */
	public void sendProgress(double taskProgress, double maxProgressOfCurrentTask, double overallProcessProportion, boolean newProgress, String requestId) {
		if(newProgress) {
			currentProgress = 0.0;
		}
		currentProgress+=(taskProgress/maxProgressOfCurrentTask);
		System.out.println("| | | | |  PROGRESS: "+currentProgress+" adding :"+taskProgress+"/"+maxProgressOfCurrentTask+", portion: "+overallProcessProportion+" ==> "+ currentProgress*overallProcessProportion);
		realtimeService.sendProgress(currentProgress*overallProcessProportion, requestId);
	}

	public void sendComplete(String requestId) {
		System.out.println("________COMPLETE PROGRESS________");
		realtimeService.sendProgress(98, requestId);
		realtimeService.sendProgress(99, requestId);
		realtimeService.sendProgress(100, requestId);
		
	}

}
