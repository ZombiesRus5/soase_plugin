package sose.tools.xml;
/*
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of this source, but changing it is not allowed.
 */

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * This type was created in VisualAge.
 */
public class NodeListIterator implements java.util.Iterator<Node> {
	NodeList list;
	int currentIndex=0;
/**
 * NodeListEnumeration constructor comment.
 */
public NodeListIterator(NodeList aList) {
	list = aList;
}
public boolean hasNext() {
	return currentIndex < list.getLength();
}
public Node next() {
	return list.item(currentIndex++);
}
public void remove() {
	throw new RuntimeException("not supported!");
}
}
