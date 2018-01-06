/**
 * 
 */
package org.apache.jackrabbit.oak.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.oak.plugins.index.IndexConstants;
import org.apache.jackrabbit.oak.plugins.index.lucene.IndexFormatVersion;
import org.apache.jackrabbit.oak.plugins.index.lucene.LuceneIndexConstants;
import org.apache.jackrabbit.oak.utils.IOakUtils;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synectiks.commons.entities.oak.OakEntity;
import com.synectiks.commons.entities.oak.OakFileNode;
import com.synectiks.commons.utils.IUtils;

/**
 * @author Rajesh
 */
@Component
public class OakManager {

	private static final Logger logger = LoggerFactory.getLogger(OakManager.class);

	/** Application root node */
	private static final String basePath = "/synectiks";

	@Autowired
	private Repository repository;
	private Session session;

	private ObjectContentManager ocmManager;

	private void login() {
		try {
			if (IUtils.isNull(session)) {
				session = repository
						.login(new SimpleCredentials("admin", "admin".toCharArray()));
				configureLuceneIndex();
				configureOCM();
				setup();
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void check() throws Exception {
		login();
		if (IUtils.isNull(session)) {
			logger.error("Failed to get repository session");
			throw new Exception("Failed to get repository session");
		}
	}

	private void configureLuceneIndex() throws Exception {
		String indexPath = "/oak:index/lucene";
		Node lucene = JcrUtils.getOrCreateByPath(indexPath, JcrConstants.NT_UNSTRUCTURED,
				"oak:QueryIndexDefinition", session, false);
		lucene.setProperty("async", "async");
		lucene.setProperty(IndexConstants.TYPE_PROPERTY_NAME,
				LuceneIndexConstants.TYPE_LUCENE);
		lucene.setProperty(LuceneIndexConstants.EVALUATE_PATH_RESTRICTION, true);
		// lucene.setProperty(LuceneIndexConstants.INDEX_PATH, indexPath);
		lucene.setProperty(LuceneIndexConstants.COMPAT_MODE,
				IndexFormatVersion.V2.getVersion());

		if (!lucene.hasNode(LuceneIndexConstants.INDEX_RULES)) {
			Node indexRules = lucene.addNode(LuceneIndexConstants.INDEX_RULES,
					JcrConstants.NT_UNSTRUCTURED);
			Node ntBaseRule = indexRules.addNode(JcrConstants.NT_BASE);

			// Fulltext index only includes property of type String and Binary
			ntBaseRule.setProperty(LuceneIndexConstants.INCLUDE_PROPERTY_TYPES,
					new String[] { PropertyType.TYPENAME_BINARY,
							PropertyType.TYPENAME_STRING });

			Node propNode = ntBaseRule.addNode(LuceneIndexConstants.PROP_NODE);

			Node allPropNode = propNode.addNode("allProps");
			allPropNode.setProperty(LuceneIndexConstants.PROP_ANALYZED, true);
			allPropNode.setProperty(LuceneIndexConstants.PROP_NODE_SCOPE_INDEX, true);
			allPropNode.setProperty(LuceneIndexConstants.PROP_NAME,
					LuceneIndexConstants.REGEX_ALL_PROPS);
			allPropNode.setProperty(LuceneIndexConstants.PROP_IS_REGEX, true);
			allPropNode.setProperty(LuceneIndexConstants.PROP_USE_IN_SPELLCHECK, true);
		}
		// Create aggregates for nt:file
		if (!lucene.hasNode(LuceneIndexConstants.AGGREGATES)) {
			Node aggNode = lucene.addNode(LuceneIndexConstants.AGGREGATES);

			Node aggFile = aggNode.addNode(JcrConstants.NT_FILE);
			aggFile.addNode("include0").setProperty(LuceneIndexConstants.AGG_PATH,
					JcrConstants.JCR_CONTENT);
		}
		logger.info("Created fulltext index definition at {}", indexPath);
	}

	private void configureOCM() {
		// Register the persistent classes
		@SuppressWarnings("rawtypes")
		List<Class> classes = new ArrayList<>();
		classes.add(OakFileNode.class);
		Mapper mapper = new AnnotationMapperImpl(classes);
		ocmManager = new ObjectContentManagerImpl(session, mapper);
	}

	/**
	 * Method to register different node types in repository
	 * @throws RepositoryException
	 */
	private void setup() throws RepositoryException {
		if (!IUtils.isNull(session)) {
			NodeTypeManager ntpMgr = session.getWorkspace().getNodeTypeManager();
			NodeTypeIterator nttypes = ntpMgr.getAllNodeTypes();
			while (nttypes.hasNext()) {
				NodeType nt = nttypes.nextNodeType();
				logger.info(
						"NodeType: " + nt.getName() + ": " + IOakUtils.printNodeType(nt));
			}
			// Add parent node if not exists
			Node root = session.getRootNode();
			if (!root.hasNode(basePath.substring(1))) {
				root.addNode(basePath.substring(1));
				session.save();
			}
		}
	}

	/**
	 * Method to generate full node hierarchy json
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public String listNodes(String path) throws Exception {
		check();
		StringBuilder sb = new StringBuilder();
		if (!IUtils.isNullOrEmpty(path)) {
			Node node = null;
			if (path.equals("/")) {
				node = session.getRootNode();
			} else {
				node = session.getNode(path);
			}
			IOakUtils.printNode(node, sb);
		}
		logger.info("Res: " + sb.toString());
		return sb.toString();
	}

	/**
	 * Create new node in repository at specified path
	 * @param nodeName
	 * @param nodePath
	 * @param node
	 * @return
	 * @throws Throwable
	 */
	public <T> T createNode(String nodeName, String nodePath, T node, Class<?> clazz)
			throws Throwable {
		try {
			check();
			if (node instanceof OakEntity) {
				OakEntity oakEnt = ((OakEntity) node);
				// verify to check if jcrPath has set
				if (IUtils.isNull(oakEnt.getJcrPath()) && !IUtils.isNull(nodePath)) {
					if (session.nodeExists(nodePath)) {
						if (!IUtils.isNullOrEmpty(nodeName)) {
							nodePath += (nodePath.endsWith("/") ? nodeName
									: "/" + nodeName);
						} else {
							logger.warn("Node at '" + nodePath + "' already exists.");
						}
					} else {
						createParentPath(nodePath, nodeName);
					}
					// set jcr path if not set
					if (IUtils.isNullOrEmpty(oakEnt.getJcrPath())) {
						oakEnt.setJcrPath(nodePath);
					}
					// Update oak file node stream from file system.
					if (node instanceof OakFileNode) {
						IUtils.updateFileStream((OakFileNode) node);
					}
				}
				logger.info("Node: " + node);
				if (ocmManager.objectExists(oakEnt.getJcrPath())
						&& !IUtils.isNull(clazz)) {
					Object nd = ocmManager.getObject(clazz, oakEnt.getJcrPath());
					logger.info("Existing node: " + nd);
					BeanUtils.copyProperties(node, nd);
					logger.info("AfterUpdate node: " + nd);
					ocmManager.update(nd);
				} else {
					ocmManager.insert(node);
				}
				ocmManager.save();
			} else {
				String jcrName = nodeName;
				if (session.nodeExists(nodePath)) {
					if (IUtils.isNullOrEmpty(jcrName)) {
						logger.warn("Node at '" + nodePath + "' already exists.");
						jcrName = UUID.randomUUID().toString();
					}
				} else {
					int indx = nodePath.lastIndexOf("/");
					jcrName = nodePath.substring(indx + 1);
					nodePath = nodePath.substring(0, indx);
				}
				createParentPath(nodePath, jcrName);
				JSONObject json = (JSONObject) node;
				Node parent = session.getNode(nodePath);
				Node entity = null;
				boolean isNew = true;
				if (parent.hasNode(jcrName)) {
					isNew = false;
					entity = parent.getNode(jcrName);
				} else {
					entity = parent.addNode(jcrName);
				}
				JSONArray keys = json.names();
				if (IUtils.isNull(keys)) {
					throw new Exception("Node should not be empty.");
				}
				for (int i = 0; i < keys.length(); i++) {
					String key = keys.optString(i);
					entity.setProperty(key, json.optString(key));
				}
				// update common properties
				if (isNew) {
					if (!entity.hasProperty("createdAt")) {
						entity.setProperty("createdAt", new Date().getTime());
					}
				}
				entity.setProperty("updatedAt", new Date().getTime());
				logger.info("Node: " + node);
				session.save();
			}
			return node;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Method to make sure if parent path exists
	 * @param parentPath
	 * @param nodeName
	 * @throws Exception
	 */
	private void createParentPath(String parentPath, String nodeName) throws Exception {
		if (IUtils.isNullOrEmpty(nodeName)) {
			parentPath = parentPath.substring(0, parentPath.lastIndexOf("/"));
		}
		logger.info("Create: " + parentPath + ", nodeName: " + nodeName);
		if (!session.nodeExists(parentPath)) {
			String pPath = "";
			List<String> paths = IUtils.getListFromString(parentPath, "/");
			for (int i = 0; i < paths.size(); i++) {
				String nName = paths.get(i);
				if (!session.nodeExists(pPath + "/" + nName)) {
					Node node = session.getNode(pPath.equals("") ? "/" : pPath)
							.addNode(nName);
					node.setPrimaryType("nt:unstructured");
					session.save();
				}
				pPath += "/" + nName;
				logger.info(pPath + " node exists");
			}
		}
		logger.info("Parent Path created");
	}

	/**
	 * Rename or move a node in repository at destination path
	 * @param srcPath
	 * @param destPath
	 * @return
	 * @throws Throwable
	 */
	public String renameOrMove(String srcPath, String destPath) throws Throwable {
		String res = "{\"Result\": \"Success\"}";
		try {
			check();
			if (session.nodeExists(srcPath)) {
				session.move(srcPath, destPath);
				session.save();
			} else {
				throw new Exception(srcPath + " not exists.");
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return res;
	}

	/**
	 * Rename or move a node in repository at destination path
	 * @param path
	 * @return
	 * @throws Throwable
	 */
	public String delete(String path) throws Throwable {
		String res = "{\"Result\": \"Success\"}";
		try {
			check();
			if (session.nodeExists(path)) {
				session.removeItem(path);
				session.save();
			} else {
				throw new Exception(path + " not exists.");
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return res;
	}

	/**
	 * Method returns suggestions according to input
	 * @param input
	 * @return
	 * @throws Throwable
	 */
	public List<String> getSuggestions(String input) throws Throwable {
		List<String> list = new ArrayList<>();
		try {
			check();
			QueryManager qm = session.getWorkspace().getQueryManager();
			String xpath = "/jcr:root[rep:suggest('" + input + " ')]/(rep:suggest())";
			@SuppressWarnings("deprecation")
			QueryResult result = qm.createQuery(xpath, Query.XPATH).execute();
			RowIterator it = result.getRows();
			if (it.hasNext()) {
				Row row = it.nextRow();
				list.add(row.getValue("rep:suggest()").getString());
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return list;
	}

	/**
	 * Method returns spell check suggestions for input
	 * @param input
	 * @return
	 * @throws Throwable
	 */
	public List<String> spellCheck(String input) throws Throwable {
		List<String> list = new ArrayList<>();
		try {
			check();
			QueryManager qm = session.getWorkspace().getQueryManager();
			String xpath = "/jcr:root[rep:spellcheck('" + input
					+ "')]/(rep:spellcheck())";
			@SuppressWarnings("deprecation")
			QueryResult result = qm.createQuery(xpath, Query.XPATH).execute();
			RowIterator it = result.getRows();
			if (it.hasNext()) {
				Row row = it.nextRow();
				list.add(row.getValue("rep:spellcheck()").getString());
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return list;
	}

	/**
	 * Method returns similar nodes like input path
	 * @param path
	 * @return
	 * @throws Throwable
	 */
	public List<String> getSimilarNodes(String path) throws Throwable {
		List<String> list = new ArrayList<>();
		try {
			check();
			QueryManager qm = session.getWorkspace().getQueryManager();
			String xpath = "//element(*, nt:base)[rep:similar(., '" + path + "')]";
			@SuppressWarnings("deprecation")
			QueryResult result = qm.createQuery(xpath, Query.XPATH).execute();
			RowIterator it = result.getRows();
			if (it.hasNext()) {
				Row row = it.nextRow();
				list.add(IUtils.OBJECT_MAPPER.writeValueAsString(row));
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return list;
	}

	/*
	 * /jcr:root/content//element(*, nt:file)[jcr:contains(., 'test')]
	 * option(index tag x)
	 * 
	 * select * from [nt:file] where ischildnode('/content') and contains(*,
	 * 'test') option(index tag [x])
	 */
	/**
	 * Method to run full text query on indexes to get match list nodes.
	 * @param path
	 * @param cols
	 * @param query
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	public List<String> fullTextQuery(String path, String cols, String query,
			String orderBy) throws Exception {
		List<String> nodes = null;
		try {
			check();
			String jcrPath = IUtils.getJcrPath(path);
			String jcrCols = IUtils.getJcrColumns(cols);
			String xpath = jcrPath + "//element(*, nt:base)[jcr:contains(" + jcrCols
					+ ", " + query + ")] ";
			if (!IUtils.isNullOrEmpty(orderBy)) {
				xpath += "order by " + orderBy;
			}
			QueryManager qm = session.getWorkspace().getQueryManager();
			@SuppressWarnings("deprecation")
			QueryResult result = qm.createQuery(xpath, Query.XPATH).execute();
			RowIterator it = result.getRows();
			nodes = new ArrayList<>();
			while (it.hasNext()) {
				Row row = it.nextRow();
				nodes.add(IUtils.OBJECT_MAPPER.writeValueAsString(row));
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw ex;
		}
		return nodes;
	}

	/**
	 * Method to remove a node from path
	 * @param absPath
	 * @throws Exception
	 */
	public void removeNode(String absPath) throws Exception {
		check();
		if (!IUtils.isNullOrEmpty(absPath)) {
			if (session.nodeExists(absPath)) {
				session.removeItem(absPath);
			}
		}
	}

}
