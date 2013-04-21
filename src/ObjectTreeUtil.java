/**
 * Apr 20, 2013
 */
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;


/**
 * @author Jeffeng Wu
 */
public class ObjectTreeUtil {
	
	@Test public void testGetTreeData() {
        List<Node> nodes = getTreeData(Arrays.asList(
                        new Params().add("id", "1").add("text", "A").add("category", "a.b.c"),
                        new Params().add("id", "2").add("text", "B").add("category", "a.b"),
                        new Params().add("id", "3").add("text", "C").add("category", "a"),
                        new Params().add("id", "4").add("text", "D").add("category", "a.b.c")
        ));
        System.out.println(dumpTree(nodes, 0));
        // assert 断言
        assertEquals(1, nodes.size());
	}
	
	public static class Node {
        public String id; // 供树形组件展示的结点ID
        public String text; // 供树形组件展示的文本
        public List<Node> children; // 子结点
	}
	
	public static List<Node> getTreeData(List<?> list) {
	    List<Node> ret = new ArrayList<Node>();
	    Map<String, Node> map = new HashMap<String, Node>();
	    for (Object o : list) {
	    	// 找到对象的父结点
	    	Node parent = getParentNode(ret, map, o);
	    	// 插入对象
	    	Node node = createLeafNode(o);
	    	parent.children.add(node);
	    }
	    return ret;
	}
	
	private static Node getParentNode(List<Node> roots, Map<String, Node> map, Object o) {
		String category = (String)((Params)o).get("category"); // 获取对象类别
		Node ret = map.get(category);
		if (ret == null) {
			ret = createParentNode(roots, map, category);
		}
		return ret;
	}
	
	private static Node createParentNode(List<Node> roots, Map<String, Node> map, String category) {
		int index = category.lastIndexOf(".");
		Node ret = new Node();
		if (index != -1) {
			String parentFullId = category.substring(0, index);
			Node parent = map.get(parentFullId);
			if (parent == null) {
				parent = createParentNode(roots, map, parentFullId);
			}
			parent.children.add(ret);
			ret.id = category; 
			ret.text = category.substring(index + 1); 
			ret.children = new ArrayList<Node>();
		}
		else { // 顶层结点
			ret.id = category;
			ret.text = category;
			ret.children = new ArrayList<Node>();
			roots.add(ret);
		}
		map.put(category, ret);
		return ret;
	}
	
	private static Node createLeafNode(Object o) {
		Node ret = new Node();
		ret.id = (String)((Params)o).get("id"); // 如何从对象中获取结点ID：暂时写死
		ret.text = (String)((Params)o).get("text"); // 如何从对象中获取结点显示文本：暂时写死
		return ret;
	}
	public class Params extends HashMap<String, Object> {
		private static final long serialVersionUID = 2550180325908030442L;
		public Params add(String key, Object value) {
			this.put(key, value);
			return this;
		}
	}
	private static String dumpTree(List<Node> list, int level) {
		StringBuilder ret = new StringBuilder();
		for (Node node : list) {
			ret.append(nspace(level)).append("|--");
			ret.append(node.id).append("-").append(node.text).append("\r\n");
			if (node.children != null) {
				ret.append(dumpTree(node.children, level + 1));
			}
		}
		return ret.toString();
	}
	private static String nspace(int n) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < n; i++) ret.append("+--");
		return ret.toString();
	}
}
