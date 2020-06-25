package org.apache.jackrabbit.oak.postgs.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jackrabbit.oak.postgs.domain.CloudContextPath;
import org.apache.jackrabbit.oak.postgs.repository.CloudContextPathRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
public class CloudContextPathService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    CloudContextPathRepository cloudContextPathRepository;

    public CloudContextPath saveCloudContextPath(CloudContextPath input) {
    	logger.info("Saving CloudContextPath");
    	CloudContextPath cloudContextPath = null;
    	try {
    			
    		Map<String, String> criteriaMap = new HashMap<>();
    		criteriaMap.put("plugin", input.getPlugin());
    		criteriaMap.put("provider", input.getProvider());
    		
    		List<CloudContextPath> list = search(criteriaMap);
    		if(list.size() > 0) {
    			logger.debug("Updating existing CloudContextPath");
    			cloudContextPath = list.get(0);
    		}else {
    			logger.debug("Adding new CloudContextPath");
    			cloudContextPath = new CloudContextPath();
    		}
    		
    		cloudContextPath.setPlugin(input.getPlugin());
    		cloudContextPath.setPath(input.getPath());
    		cloudContextPath.setProvider(input.getProvider());
    		
    		cloudContextPath = cloudContextPathRepository.save(cloudContextPath);
        	
        	String msg = "ContextPath is %s successfully";
        	if(input.getId() == null) {
        		logger.debug(String.format(msg, "added"));
        	}else {
        		logger.debug(String.format(msg, "updated"));
        	}
        	
        }catch(Exception e) {
        	String msg = "Due to some exception, ContextPath could not be saved";
        	logger.error(msg + " Exception : ",e);
    	}
    	logger.info("ContextPath saved successfully");
    	return cloudContextPath;
        
    }
    
    public List<CloudContextPath> search(Map<String, String> criteriaMap){
    	CloudContextPath obj = new CloudContextPath();
    	boolean isFilter = false;
    	if(criteriaMap.get("id") != null) {
    		obj.setId(Long.parseLong(criteriaMap.get("id")));
    		isFilter = true;
    	}
    	if(criteriaMap.get("plugin") != null) {
    		obj.setPlugin(criteriaMap.get("plugin"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("provider") != null) {
    		obj.setProvider(criteriaMap.get("provider"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("path") != null) {
    		obj.setPath(criteriaMap.get("path"));
    		isFilter = true;
    	}
    	
    	List<CloudContextPath> list = null;
    	if(isFilter) {
    		list = this.cloudContextPathRepository.findAll(Example.of(obj), Sort.by(Direction.DESC, "id"));
    	}else {
    		list = this.cloudContextPathRepository.findAll(Sort.by(Direction.DESC, "id"));
    	}
        
    	Collections.sort(list, (o1, o2) -> o1.getId().compareTo(o2.getId()));
    	return list;
    }
    
    
}
