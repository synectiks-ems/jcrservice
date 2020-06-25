package org.apache.jackrabbit.oak.postgs.controller;

import java.util.HashMap;
import java.util.List;

import org.apache.jackrabbit.oak.postgs.domain.CloudProviderConfig;
import org.apache.jackrabbit.oak.postgs.service.CloudProviderConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.synectiks.commons.interfaces.IApiController;

@CrossOrigin
@RestController
@RequestMapping(path = IApiController.API_PATH
	+ IApiController.URL_OAKREPO, method = RequestMethod.POST)
public class CloudProviderConfigController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CloudProviderConfigService cloudProviderConfigService;
	
	@RequestMapping(value = "/saveCloudProviderConfig")
	public List<CloudProviderConfig> saveCloudProviderConfig(@RequestBody CloudProviderConfig cloudProviderConfig) {
		logger.info("Saving cloud provider config started");
		try {
			cloudProviderConfigService.saveCloudProviderConfig(cloudProviderConfig);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
		}
		logger.info("Saving cloud provider config completed");
		return listCloudProviderConfig();
	}
	
	@RequestMapping(value = "/listCloudProviderConfig", method = RequestMethod.GET)
	public List<CloudProviderConfig> listCloudProviderConfig(){
		List<CloudProviderConfig> list = cloudProviderConfigService.search(new HashMap<>());
		return list;
	} 
	
}
