package org.apache.jackrabbit.oak.postgs.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jackrabbit.oak.postgs.domain.CloudProviderConfig;
import org.apache.jackrabbit.oak.postgs.repository.CloudProviderConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
public class CloudProviderConfigService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    CloudProviderConfigRepository cloudProviderConfigRepository;

    public CloudProviderConfig saveCloudProviderConfig(CloudProviderConfig input) {
    	logger.info("Saving CloudProviderConfig");
    	CloudProviderConfig cloudProviderConfig = null;
    	try {
    			
    		Map<String, String> criteriaMap = new HashMap<>();
    		criteriaMap.put("provider", input.getProvider());
    		List<CloudProviderConfig> list = search(criteriaMap);
    		if(list.size() > 0) {
    			logger.debug("Updating existing CloudProviderConfig");
    			cloudProviderConfig = list.get(0);
    		}else {
    			logger.debug("Adding new CloudProviderConfig");
    			cloudProviderConfig = new CloudProviderConfig();
    		}
    		cloudProviderConfig.setProvider(input.getProvider());
    		cloudProviderConfig.setAccessKey(input.getAccessKey());
    		cloudProviderConfig.setSecrateKey(input.getSecrateKey());
    		cloudProviderConfig.setEndPoint(input.getEndPoint());
    		
    		cloudProviderConfig = cloudProviderConfigRepository.save(cloudProviderConfig);
        	
        	String msg = "CloudProviderConfig is %s successfully";
        	if(list.size() == 0) {
        		logger.debug(String.format(msg, "added"));
        	}else {
        		logger.debug(String.format(msg, "updated"));
        	}
        	
        }catch(Exception e) {
        	String msg = "Due to some exception, CloudProviderConfig could not be saved";
        	logger.error(msg + " Exception : ",e);
    	}
    	logger.info("CloudProviderConfig saved successfully");
    	return cloudProviderConfig;
        
    }
    
    public List<CloudProviderConfig> search(Map<String, String> criteriaMap){
    	CloudProviderConfig obj = new CloudProviderConfig();
    	boolean isFilter = false;
    	if(criteriaMap.get("id") != null) {
    		obj.setId(Long.parseLong(criteriaMap.get("id")));
    		isFilter = true;
    	}
    	if(criteriaMap.get("provider") != null) {
    		obj.setProvider(criteriaMap.get("provider"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("accessKey") != null) {
    		obj.setAccessKey(criteriaMap.get("accessKey"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("secrateKey") != null) {
    		obj.setSecrateKey(criteriaMap.get("secrateKey"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("bucket") != null) {
    		obj.setBucket(criteriaMap.get("bucket"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("endPoint") != null) {
    		obj.setEndPoint(criteriaMap.get("endPoint"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("permissionMode") != null) {
    		obj.setPermissionMode(criteriaMap.get("permissionMode"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("permissionModeValue") != null) {
    		obj.setPermissionModeValue(criteriaMap.get("permissionModeValue"));
    		isFilter = true;
    	}
    	List<CloudProviderConfig> list = null;
    	if(isFilter) {
    		list = this.cloudProviderConfigRepository.findAll(Example.of(obj), Sort.by(Direction.DESC, "id"));
    	}else {
    		list = this.cloudProviderConfigRepository.findAll(Sort.by(Direction.DESC, "id"));
    	}
        
    	Collections.sort(list, (o1, o2) -> o1.getId().compareTo(o2.getId()));
    	return list;
    }
    
    
}
