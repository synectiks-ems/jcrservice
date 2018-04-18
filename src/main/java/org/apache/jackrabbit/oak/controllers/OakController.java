/**
 * 
 */
package org.apache.jackrabbit.oak.controllers;

import java.util.List;

import org.apache.jackrabbit.oak.manager.OakManager;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.synectiks.commons.entities.oak.OakFileNode;
import com.synectiks.commons.interfaces.IApiController;
import com.synectiks.commons.utils.IUtils;

/**
 * @author Rajesh
 */
@Controller
@RequestMapping(path = IApiController.API_PATH
	+ IApiController.URL_OAKREPO, method = RequestMethod.POST)
@CrossOrigin
public class OakController {

	private static final Logger logger = LoggerFactory.getLogger(OakController.class);

	@Autowired
	private OakManager oakRepoManager;

	/**
	 * Service API for uploading a file on server for OakFileNode.
	 * @param file add your uploaded file with 'file' name param
	 * @return {@code ResponseEntity} with absolute file path of file on server
	 */
	@RequestMapping(value = "/upload")
	public ResponseEntity<Object> uploadAttachment(@RequestParam String upPath,
			@RequestPart MultipartFile file) {
		return IUtils.saveUploadedFile(file, upPath);
	}

	/**
	 * Api to get the list of child nodes by absolute node path.
	 * @param path
	 * @return Json string of child node objects
	 */
	@RequestMapping("/list")
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
	 * Api to delete node and its sub-tree by absolute node path.
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
				if (OakFileNode.class.getName().equals(cls)) {
					IUtils.loadFileStreamInNode((OakFileNode) node);
				}
			} else {
				node = new JSONObject(json);
			}
			logger.info("Path: " + parentPath + ", json: " + json + ", cls: " + cls
					+ ", nodeName: " + nodeName);
			node = oakRepoManager.createNode(nodeName, parentPath, node, clazz);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
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
	@RequestMapping("/similar")
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
	@RequestMapping("/suggestions")
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
	@RequestMapping("/spellcheck")
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
	@RequestMapping("/search")
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

}
