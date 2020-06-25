package org.apache.jackrabbit.oak.postgs.controller;

import java.util.HashMap;
import java.util.List;

import org.apache.jackrabbit.oak.postgs.domain.CloudContextPath;
import org.apache.jackrabbit.oak.postgs.service.CloudContextPathService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.synectiks.commons.interfaces.IApiController;
import com.synectiks.commons.utils.IUtils;

@CrossOrigin
@RestController
@RequestMapping(path = IApiController.API_PATH
	+ IApiController.URL_OAKREPO, method = RequestMethod.POST)
public class CloudContextPathController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CloudContextPathService cloudContextPathService;
	
	@RequestMapping(value = "/saveCloudContext")
	public List<CloudContextPath> saveCloudContext(@RequestBody CloudContextPath cloudContextPath) {
		String nodes = "Success";
		logger.info("Saving cloud context data started");
		try {
			cloudContextPathService.saveCloudContextPath(cloudContextPath);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
//			return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
		}
		logger.info("Saving cloud context data completed");
		return listCloudContext();
	}
	
	@RequestMapping(value = "/listCloudContext", method = RequestMethod.GET)
	public List<CloudContextPath> listCloudContext(){
		List<CloudContextPath> list = cloudContextPathService.search(new HashMap<>());
		return list;
	} 
	
}
