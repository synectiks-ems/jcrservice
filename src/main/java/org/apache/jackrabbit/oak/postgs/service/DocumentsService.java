package org.apache.jackrabbit.oak.postgs.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jackrabbit.oak.postgs.domain.Documents;
import org.apache.jackrabbit.oak.postgs.repository.DocumentsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
public class DocumentsService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    DocumentsRepository documentsRepository;

    public Documents saveDocuments(Documents input) {
    	logger.info("Saving Documents");
    	Documents documents = null;
    	try {
    		documents = documentsRepository.save(input);
        }catch(Exception e) {
        	String msg = "Due to some exception, document could not be saved";
        	logger.error(msg + " Exception : ",e);
    	}
    	logger.info("Documents saved successfully");
    	return documents;
        
    }
    
    public List<Documents> search(Map<String, String> criteriaMap){
    	Documents obj = new Documents();
    	boolean isFilter = false;
    	if(criteriaMap.get("id") != null) {
    		obj.setId(Long.parseLong(criteriaMap.get("id")));
    		isFilter = true;
    	}
    	if(criteriaMap.get("fileName") != null) {
    		obj.setFileName(criteriaMap.get("fileName"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("localFilePath") != null) {
    		obj.setLocalFilePath(criteriaMap.get("localFilePath"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("cloudFilePath") != null) {
    		obj.setCloudFilePath(criteriaMap.get("cloudFilePath"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("clientType") != null) {
    		obj.setClientType(criteriaMap.get("clientType"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("clientId") != null) {
    		obj.setClientId(Long.parseLong(criteriaMap.get("clientId")));
    		isFilter = true;
    	}
    	if(criteriaMap.get("cdn") != null) {
    		obj.setCdn(criteriaMap.get("cdn"));
    		isFilter = true;
    	}
    	if(criteriaMap.get("status") != null) {
    		obj.setStatus(criteriaMap.get("status"));
    		isFilter = true;
    	}
    	List<Documents> list = null;
    	if(isFilter) {
    		list = this.documentsRepository.findAll(Example.of(obj), Sort.by(Direction.DESC, "id"));
    	}else {
    		list = this.documentsRepository.findAll(Sort.by(Direction.DESC, "id"));
    	}
        
    	Collections.sort(list, (o1, o2) -> o1.getId().compareTo(o2.getId()));
    	return list;
    }
    
    
}
