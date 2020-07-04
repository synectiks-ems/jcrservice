/**
 * 
 */
package org.apache.jackrabbit.oak.controllers;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.jackrabbit.oak.OakServer;
import org.apache.jackrabbit.oak.manager.OakManager;
import org.apache.jackrabbit.oak.postgs.domain.CloudContextPath;
import org.apache.jackrabbit.oak.postgs.domain.CloudInfo;
import org.apache.jackrabbit.oak.postgs.domain.CloudProviderConfig;
import org.apache.jackrabbit.oak.postgs.domain.Documents;
import org.apache.jackrabbit.oak.postgs.service.CloudContextPathService;
import org.apache.jackrabbit.oak.postgs.service.CloudProviderConfigService;
import org.apache.jackrabbit.oak.postgs.service.DocumentsService;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.synectiks.commons.constants.IConsts;
import com.synectiks.commons.entities.oak.OakFileNode;
import com.synectiks.commons.interfaces.IApiController;
import com.synectiks.commons.utils.IUtils;

/**
 * @author Rajesh
 */
@CrossOrigin
@RestController
@RequestMapping(path = IApiController.API_PATH
	+ IApiController.URL_OAKREPO, method = RequestMethod.POST)
public class OakController {

	private static final Logger logger = LoggerFactory.getLogger(OakController.class);

	@Autowired
	private OakManager oakRepoManager;

	@Autowired
	private CloudContextPathService cloudContextPathService;
	
	@Autowired
	private CloudProviderConfigService cloudProviderConfigService;
	
	@Autowired
	DocumentsService documentsService;
	
	@Value("${kafka.url}")
    private String kafkaUrl;
	
	/**
	 * Service API for uploading a file on server for OakFileNode.
	 * @param file add your uploaded file with 'file' name param. CMS Plugin/Module name to get the cloud context information. This module name is used as a kafka topic
	 * @return {@code ResponseEntity} with absolute file path of file on server
	 */
	@RequestMapping(value = "/upload")
	public ResponseEntity<Object> uploadAttachment(@RequestParam String upPath, @RequestParam String module,
			@RequestPart MultipartFile file) {
		logger.info("Saving file to local server");
		ResponseEntity<Object> obj = IUtils.saveUploadedFile(file, upPath);
		logger.info("File saved in local server");
		Documents doc = saveDocument(upPath, module, file);
		sendMessageToKafka(doc);
		return obj;
	}

	private void sendMessageToKafka(Documents doc) {
		CloudInfo info = new CloudInfo();
		
		try {
			BeanUtils.copyProperties(info, doc);
//			info.setFilename(doc.getLocalFilePath()+File.separatorChar+doc.getFileName());
			info.setLocalFile(doc.getLocalFilePath()+File.separatorChar+doc.getFileName());
			BeanUtils.copyProperties(info, doc.getCloudContextPath());
			info.setContextPath(doc.getCloudContextPath().getPath());
			
			Map<String, String> criteriaMap = new HashMap<>();
			criteriaMap.put("provider", doc.getCloudContextPath().getProvider());
			List<CloudProviderConfig> list = cloudProviderConfigService.search(criteriaMap);
			if(list.size() > 0) {
				BeanUtils.copyProperties(info, list.get(0));
			}
			
			logger.info("Converting object to JSON: "+info);
			String jsonString = IUtils.OBJECT_MAPPER.writeValueAsString(info);
			logger.debug("Sending json message to kafka : "+jsonString);
			fireEvent(jsonString);
			logger.debug("Json message sent to kafka : ");
		}catch (Throwable ex) {
			logger.error("Due to some exception, cloud context message cannot be sent to kafka: ",ex);
			ex.printStackTrace();
		}
	}

	private Documents saveDocument(String upPath, String module, MultipartFile file) {
		logger.info("Saving file information in documents");
		Documents doc = null;
		try {
			Map<String, String> criteriaMap = new HashMap<>();
			criteriaMap.put("plugin", module);
			List<CloudContextPath> list = cloudContextPathService.search(criteriaMap);
			if(list.size() >0) {
				doc = new Documents();
				doc.setFileName(file.getOriginalFilename());
				doc.setLocalFilePath(upPath);
				doc.setCloudContextPath(list.get(0));
				doc.setStatus("SUCCESS");
				doc = documentsService.saveDocuments(doc);
			}
		}catch (Throwable ex) {
			doc = new Documents();
			doc.setFileName(file.getOriginalFilename());
			doc.setLocalFilePath(upPath);
			doc.setStatus("PENDING");
			doc = documentsService.saveDocuments(doc);
			logger.error("Exception: ",ex);
			ex.printStackTrace();
		}
		logger.info("File information saved in documents");
		return doc;
	}
	/**
	 * Api to get the list of child nodes by absolute node path.
	 * @param path
	 * @return Json string of child node objects
	 */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ResponseEntity<String> downloadNodeFile(@RequestParam String path,
			HttpServletResponse response) {
		logger.info("download request: " + path);
		try {
			OakFileNode node = oakRepoManager.getFileNode(path);
			if (!IUtils.isNull(node) && !IUtils.isNull(node.getData())) {
				response.setHeader("Access-Control-Expose-Headers",
						"X-Suggested-Filename, Content-Disposition, Content-Type");
				response.setHeader("Access-Control-Allow-Headers",
					"X-Suggested-Filename, Content-Disposition, Content-Type");
				response.setContentType(node.getContentType());
				response.setHeader(IConsts.CONT_TYPE, node.getContentType());
				response.setHeader("X-Suggested-Filename",node.getName());
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + node.getName() + "\"");
				logger.info("Found: " + node.getName() + ", with content type: "
						+ node.getContentType());
				ServletOutputStream out = response.getOutputStream();
				byte[] buffer = new byte[2048];
				InputStream is = node.getData();
				while ((is.read(buffer)) > 0) {
					out.write(buffer);
				}
				is.close();
				out.flush();
			}
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(IUtils.getFailedResponse(ex).toString(),
					HttpStatus.PRECONDITION_FAILED);
		}
		return null;
	}

	/**
	 * Api to get the list of child nodes by absolute node path.
	 * @param path
	 * @return Json string of child node objects
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<String> listNodes(@RequestParam String path) {
		String nodes = null;
		try {
			nodes = oakRepoManager.listNodes(path);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(IUtils.getFailedResponse(ex).toString(),
					HttpStatus.PRECONDITION_FAILED);
		}
		return new ResponseEntity<>(nodes, HttpStatus.OK);
	}

	/**
	 * Api to get the delete a node and its sub tree by absolute node path.
	 * @param path
	 * @return Json string of child node objects
	 */
	@RequestMapping("/delete")
	public ResponseEntity<String> removeNode(@RequestParam String absPath) {
		String nodes = "Success";
		try {
			oakRepoManager.removeNode(absPath);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(IUtils.getFailedResponse(ex).toString(),
					HttpStatus.PRECONDITION_FAILED);
		}
		return new ResponseEntity<>(nodes, HttpStatus.OK);
	}

	/**
	 * Api to create a new entity in jackrabbit repository at specified
	 * path. if path exists then node get created as child with new random id.
	 * @param parentPath node path or node parent path to create node at
	 * @param json json string with node properties to create new node.
	 * @param cls specify fully qualified class name for json to type cast in
	 * @param nodeName optional specify if don't want random id as node name
	 * @return json of newly created object in repository.
	 */
	@RequestMapping("/createNode")
	public ResponseEntity<String> createNode(@RequestParam String parentPath,
			@RequestParam String json, @RequestParam(required = false) String cls,
			@RequestParam(required = false) String nodeName) {
		Object node = null;
		try {
			Class<?> clazz = IUtils.loadClass(cls);
			if (!IUtils.isNull(clazz)) {
				node = IUtils.OBJECT_MAPPER.readValue(json, clazz);
			} else {
				node = new JSONObject(json);
			}
			logger.info("Path: " + parentPath + ", json: " + json + ", cls: " + cls
					+ ", nodeName: " + nodeName);
			node = oakRepoManager.createNode(nodeName, parentPath, node, clazz);
		} catch (Throwable ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage(), IUtils.getFailedResponse(ex));
			return new ResponseEntity<>(IUtils.getFailedResponse(ex).toString(),
					HttpStatus.PRECONDITION_FAILED);
		}
		return new ResponseEntity<>(node.toString(), HttpStatus.OK);
	}

	/**
	 * Api to move or rename node or tree from one to another path.
	 * Make sure both path must exists
	 * @param src source node path
	 * @param dest destination node path
	 * @return moved node
	 */
	@RequestMapping("/moveNode")
	public ResponseEntity<Object> moveNode(@RequestParam String src,
			@RequestParam String dest) {
		String res = null;
		try {
			res = oakRepoManager.renameOrMove(src, dest);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(IUtils.getFailedResponse(ex),
					HttpStatus.PRECONDITION_FAILED);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	/**
	 * Api to find similar nodes as input node path
	 * @param absPath
	 * @return list of similar nodes
	 */
	@RequestMapping(value = "/similar", method = RequestMethod.GET)
	public ResponseEntity<Object> findSimilarNodes(@RequestParam String absPath) {
		List<String> res = null;
		try {
			res = oakRepoManager.getSimilarNodes(absPath);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(IUtils.getFailedResponse(ex),
					HttpStatus.PRECONDITION_FAILED);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	/**
	 * Api to get suggestions for input string
	 * @param input
	 * @return
	 */
	@RequestMapping(value = "/suggestions", method = RequestMethod.GET)
	public ResponseEntity<Object> getSuggestions(@RequestParam String input) {
		List<String> res = null;
		try {
			res = oakRepoManager.getSuggestions(input);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(IUtils.getFailedResponse(ex),
					HttpStatus.PRECONDITION_FAILED);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	/**
	 * Api to find correct spelling for input string
	 * @param input
	 * @return
	 */
	@RequestMapping(value = "/spellcheck", method = RequestMethod.GET)
	public ResponseEntity<Object> getSpellcheck(@RequestParam String input) {
		List<String> res = null;
		try {
			res = oakRepoManager.spellCheck(input);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(IUtils.getFailedResponse(ex),
					HttpStatus.PRECONDITION_FAILED);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	/**
	 * Api to search input query in jackrabbit repository.
	 * @param path absolute node path to search in repository.
	 * @param cols fields to be searched for query
	 * @param query string to search
	 * @param orderBy comma separate list of columns for result sorting.
	 * @return json List object of search result nodes as string
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public ResponseEntity<Object> search(
			@RequestParam String path, @RequestParam String cols,
			@RequestParam String query, @RequestParam String orderBy) {
		List<String> res = null;
		try {
			res = oakRepoManager.fullTextQuery(path, cols, query, orderBy);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(IUtils.getFailedResponse(ex),
					HttpStatus.PRECONDITION_FAILED);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	private void fireEvent(String jsonStr) {
		String PRM_TOPIC = "topic";
		String PRM_MSG = "msg";
		String kafkaTopic = "topic.file.upload";
    	RestTemplate restTemplate = OakServer.getBean(RestTemplate.class);
    	String res = null;
		try {
			res = IUtils.sendGetRestRequest(
					restTemplate, 
					kafkaUrl,
					IUtils.getRestParamMap(PRM_TOPIC, kafkaTopic, PRM_MSG, jsonStr), 
					String.class);
			logger.debug("Message sent to kafka. Kafka response : "+res);
		} catch(Exception ex) {
			logger.error(ex.getMessage(), ex);
			res = null;
		}
	}
}
