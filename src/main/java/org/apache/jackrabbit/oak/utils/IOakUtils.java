/**
 * 
 */
package org.apache.jackrabbit.oak.utils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;

import com.synectiks.commons.utils.IUtils;

/**
 * @author Rajesh
 */
public interface IOakUtils {

	static String printNodeType(NodeType nt) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		NodeDefinition[] ntDefs = nt.getChildNodeDefinitions();
		if (!IUtils.isNull(ntDefs)) {
			sb.append("\"nodeDefs\": [");
			boolean bFirst = true;
			for (NodeDefinition ntDef : ntDefs) {
				sb.append(bFirst ? "" : ", ");
				sb.append("\"" + ntDef.getName() + "\"");
				bFirst = false;
			}
			sb.append("],");
		}
		PropertyDefinition[] propDefs = nt.getPropertyDefinitions();
		if (!IUtils.isNull(propDefs)) {
			sb.append("\"propDefs\": [");
			boolean bFirst = true;
			for (PropertyDefinition pDef : propDefs) {
				sb.append(bFirst ? "" : ", ");
				sb.append("\"" + pDef.getName() + "\"");
				bFirst = false;
			}
			sb.append("]");
		}
		sb.append("}");
		return sb.toString();
	}

	static void printNode(Node node, StringBuilder sb) throws Exception {
		sb.append("{\"" + getName(node.getName()) + "\": {");
		boolean bFirst = true;
		PropertyIterator props = node.getProperties();
		while (props.hasNext()) {
			Property prop = props.nextProperty();
			sb.append(bFirst ? "" : ", ");
			sb.append("\"" + getName(prop.getName()) + "\": ");
			if (prop.isMultiple()) {
				sb.append("[");
				boolean bf = true;
				for (Value val : prop.getValues()) {
					sb.append(bf ? "" : ", ");
					sb.append(getValue(val));
					bf = false;
				}
				sb.append("]");
			} else {
				sb.append(getValue(prop.getValue()));
			}
			bFirst = false;
		}
		if (node.hasNodes()) {
			sb.append(bFirst ? "" : ", ");
			sb.append("\"childs\": [");
			NodeIterator childs = node.getNodes();
			boolean bf = true;
			while (childs.hasNext()) {
				Node child = childs.nextNode();
				sb.append(bf ? "" : ", ");
				printNode(child, sb);
				bf = false;
			}
			sb.append("]");
		}
		sb.append("}}");
	}

	static String getValue(Value val) {
		String res = null;
		try {
			res = val.getString();
			if (!IUtils.isNullOrEmpty(res)) {
				if (res.contains("\\\\")) {
					IUtils.logger.info(res);
					res = res.replaceAll("\\\\", "\\\\\\\\");
				}
				if (res.contains("\"")) {
					IUtils.logger.info(res);
					res = res.replaceAll("\"", "\\\\\"");
				}
				if (res.contains("\r\n")) {
					res = res.replaceAll("\r\n", "");
				}
				if (res.contains("\n")) {
					res = res.replaceAll("\n", "");
				}
				if (res.contains("\r")) {
					res = res.replaceAll("\r", "");
				}
			}
		} catch (Exception ex) {
			// ignore it.
		}
		res = "\"" + (res == null ? "" : res) + "\"";
		return res;
	}

	static String getName(String name) {
		if (!IUtils.isNullOrEmpty(name)) {
			return name;
		} else {
			return "/";
		}
	}
}
