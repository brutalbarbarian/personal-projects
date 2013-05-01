package com.lwan.util.containers;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import com.lwan.util.StringUtil;

/**
 * Data structure for fast lookup of string keys with minimal storage space.
 * Its slightly slower and takes up bi more memory then the default hashmap,
 * but has the capibility of doing nearest prefix search.
 * 
 * @author Lu
 *
 * @param <T>
 */
public class TrieMap<T> implements Map<String, T>{
	private static final int BUFFER_BULK = 10;
	
	private Node root;
	private boolean ignoreCase;
	private int count;
	private int bulkUpdate;
	
	public TrieMap() {
		this(true);
	}
	
	public TrieMap (boolean ignoreCase) {
		root = new Node("", null, 0);
		this.ignoreCase = ignoreCase;
		count = 0;
		bulkUpdate = 0;
	}
	
	public T put(String key, T item) {
		if (StringUtil.isNullOrBlank(key)) {
			return null;	// don't even bother
		}
		
		Node closest = get(root, key, false, true);
		
		String remainder = key.substring(closest.start);
		
		// 3 cases
		if (StringUtil.equals(closest.remainder, remainder, ignoreCase)) {
			// closest key = key
			T prevLink = closest.link;
			closest.link = item;
			
			if (prevLink != null) {
				return prevLink;	// => Only case that will return anything	
			}
		} else if (StringUtil.beginsWith(remainder, closest.remainder, ignoreCase)) {
			// closest key = prefix of key, safe to add remainder to closes
			remainder = remainder.substring(closest.remainder.length());
			closest.addDirectChild(new Node(remainder, item, closest.start + closest.remainder.length()));
		} else {
			// beginning of closest key = prefix of key, need to split the closest at point of breaking
			int index = StringUtil.indexOfFirstDifference(remainder, closest.remainder, ignoreCase);
			if (index == remainder.length()) {
				// chop the node
				closest.splitAtIndex(item, index);				
			} else {
				// split the node
				remainder = remainder.substring(index);
				closest.addAtIndex(new Node(remainder, item, closest.start + index), index);
			}
		}
				
		count++;	// This won't happen if we replaced an existing item
		return null;
	}
	
	@Override
	public T get(Object key) {
		Node n = get(root, (String)key, true, false);
		return n == null? null : n.link;
	}
	
	public void trimTree() {
		trim(root);
	}
	
	protected boolean trim(Node n) {
		// recurse on children
		for (int i = n.count-1; i >= 0; i--) {
			if (!trim(n.children[i])) {
				// Move everything down
				n.count --;
				System.arraycopy(n.children, i + 1, n.children, i, n.count - i);
				System.arraycopy(n.chars, i + 1, n.chars, i, n.count - i);
			}
		}
		
		if (n != root && n.count < 2 && n.link == null) {
			if (n.count == 0) {
				return false;
			} else {
				// a mid-branch breakage
				// claim all children of the only child
				n.remainder = n.remainder + n.children[0].remainder;
				n.link = n.children[0].link;
				n.chars = n.children[0].chars;
				n.children = n.children[0].children;
			}
		} else if (n.count == 0) {
			// a leaf... make sure the arrays are null
			n.chars = null;
			n.children = null;
		}
		// compress the arrays if possible
		if (n.children != null &&  n.children.length > n.count) {
			n.chars = Arrays.copyOf(n.chars, n.count);
			n.children = Arrays.copyOf(n.children, n.count);
		}

		
		return true;
	}
	
	protected Node get(Node n, String remainder, boolean isExact, boolean acceptBreaks) {
		// we know that if it made it here... it at least prefix matches on the first character...
		// we don't know if the entire prefix will match however..
		if (StringUtil.equals(remainder, n.remainder, ignoreCase)) {
			if (n.link == null) {
				// => dead end... continued at bottom
			} else {
				// found exact
				return n;
			}
		} else if (StringUtil.beginsWith(remainder, n.remainder, ignoreCase)) {
			// full prefix match... need to check children
			char nextChar = remainder.charAt(n.remainder.length());
			Node nextChild = n.getChild(nextChar);
			if (nextChild == null) {
				// => dead end... continued at bottom
			} else {
				String nextRemainder = remainder.substring(n.remainder.length());
				return get(nextChild, nextRemainder, isExact, acceptBreaks);
			}
		} else if (!StringUtil.beginsWith(n.remainder, remainder, ignoreCase)) {
			// => dead end half way through when we're trying to find nearest...
			if (!isExact && !acceptBreaks) {
				return null;
			}
		} else {
			// => dead end... continued at bottom
		}
		
		// if we reach down here... we know that we've hit a dead end.
		if (acceptBreaks) {
			return n;
		} else if (isExact) {
			return null;
		} else if (n != root){
			if (remainder.length() > n.remainder.length()) {
				return null;	// the node's remainder is a prefix... defintly not a match
			}
			
			int shortest = Integer.MAX_VALUE;
			Node result = null;
			
			// shortest is the shortest solution so far...
			LinkedList<Node> queue = new LinkedList<>();
			queue.add(n);
			while (!queue.isEmpty()) {
				Node node = queue.pop();
				if (node.start + node.remainder.length() < shortest) {
					if (node.link != null) {
						result = node;
						shortest = node.start + node.remainder.length();
					} else if (node.hasChildren()){
						for (int i = 0; i < node.count; i++) {
							queue.addLast(node.children[i]);
						}
					} // => not sure how this is possible...
				} // => else ignore...
			}
			
			return result;
		} else {
			// just return the root if we found no match from the very start,
			// and we're attempting to find the nearest match
			return root;
		}
	}
	
	public T getNearest(String key) {
		Node n = get(root, key, false, false);
		return n == null? null : n.link;
	}
	
	public void beginBulkUpdate() {
		bulkUpdate ++;
	}
	
	public void endBulkUpdate() {
		bulkUpdate --;
		if (bulkUpdate == 0) {
			trimTree();
		}
	}
	
	protected boolean isBulkUpdating() {
		return bulkUpdate > 0;
	}
	
	protected int getBufferSize(int pref) {
		if (isBulkUpdating()) {
			return BUFFER_BULK;
		} else {
			return pref;
		}
	}
	
	protected class Node {
		String remainder;
		T link;
		
		int start;
		
		char[] chars;
		Node[] children;
		int count;
		
		Node(String remainder, T link, int start) {
			count = 0;
			this.remainder = remainder;
			this.link = link;
			this.start = start;
		}
		
		boolean hasChildren() {
			return count > 0;
		}
		
		Node getChild(char nxtChar) {
			if (hasChildren()) {
				int index = Arrays.binarySearch(chars, 0, count, 					
						ignoreCase? Character.toLowerCase(nxtChar) : nxtChar);
					
				return index < 0? null : children[index];
			} else {
				return null;
			}
		}
		
		@SuppressWarnings("unchecked")
		void addDirectChild(Node child) {
			// enlarge (or create) the childrens array if needed
			if (children == null) {
				children = new TrieMap.Node[getBufferSize(1)];
				chars = new char[getBufferSize(1)];
			} else if (children.length == count) {
				children = Arrays.copyOf(children, count + getBufferSize(1));
				chars = Arrays.copyOf(chars, count + getBufferSize(1));
			}
			
			char c = child.remainder.charAt(0);
			c = ignoreCase? Character.toLowerCase(c) : c;
			// get the insertion point
			int index = - (Arrays.binarySearch(chars, 0, count, c) + 1);
			// move existing items across if needed
			if (index < count) {
 				System.arraycopy(children, index, children, index + 1, count - index);
				System.arraycopy(chars, index, chars, index + 1, count - index);
			}
			
			// add the child in the correct position
			chars[index] = c;
			children[index] = child;
			
			// finally increment the count
			count++;
		}
		
		void splitAtIndex(T item, int index) {
			String newRemains = remainder.substring(index);
			remainder = remainder.substring(0, index);
			
			// Migrate the data across to the new node
			Node n = new Node(newRemains, link, start + index);
			n.chars = chars;
			n.children = children;
			n.count = count;
			n.link = link;
			
			link = item;
			count = 0;
			chars = null;
			children = null;
			
			addDirectChild(n);
		}
		
		@SuppressWarnings("unchecked")
		void addAtIndex(Node child, int index) {
			String newRemains = remainder.substring(index);	// remainder of the new node with all this child's children
			remainder = remainder.substring(0, index);
			
			// Migrate the data across to the new node
			Node n = new Node(newRemains, link, start + index);
			n.chars = chars;
			n.children = children;
			n.count = count;
			n.link = link;
			
			link = null;
			count = 0;
			chars = new char[getBufferSize(2)];
			children = new TrieMap.Node[getBufferSize(2)];
			
			addDirectChild(n);
			addDirectChild(child);
		}
	}


	@Override
	public int size() {
		return count;
	}

	@Override
	public boolean isEmpty() {
		return count == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return get(key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		return containsValue(value, root);
	}
	
	protected boolean containsValue(Object value, Node n) {
		if (n.link == value) {
			// stopping condition...found
			return true;
		} else {
			for (int i = 0; i < n.count; i++) {
				// depth first search
				if (containsValue(value, n.children[i])) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public T remove(Object key) {
		if (StringUtil.isNullOrBlank(key)) {
			return null;	// root value
		}
		return remove((String)key, root);
	}
	
	protected T remove(String remainder, Node n) {
		// assume this node isn't what we're looking for...
		// remainder is the remainder after removing the previous string up to where this node ends
		char c = remainder.charAt(0);
		Node child = n.getChild(c);
		if (child == null) {
			// reached a dead end
		} else if (StringUtil.equals(remainder, child.remainder, ignoreCase)) {
			// we found what we want to remove.
			T result = child.link;
			if (result != null) {
				child.link = null;
				count--;
				trim(n);
			}
			return result;			
		} else if (StringUtil.beginsWith(remainder, child.remainder, ignoreCase)) {
			// this path might lead us to what we're trying to find
			return remove(remainder.substring(child.remainder.length()), child);
		} else {
			// reached a dead end
			return null;
		}
		
		return null;
	}
	

	@Override
	public void putAll(Map<? extends String, ? extends T> m) {
		beginBulkUpdate();
		try {
			for (Entry<? extends String, ? extends T> e : m.entrySet()) {
				put(e.getKey(), e.getValue());
			}
		} finally {
			endBulkUpdate();
		}
		
	}

	@Override
	public void clear() {
		root.children = null;
		root.chars = null;
		root.count = 0;
		count = 0;
	}

	@Override
	public Set<String> keySet() {
		Set<String> set = new TreeSet<>();
		getKeys(set, "", root);
		
		return set;
	}
	
	protected void getKeys(Set<String> col, String prefix, Node n) {
		String str = prefix + n.remainder;
		if (n.link != null) {	// we found a key
			col.add(str);
		}
		for (int i = 0; i < n.count; i++) {
			getKeys(col, str, n.children[i]);
		}
	}

	@Override
	public Collection<T> values() {
		Vector<T> result = new Vector<>(count);
		getValues(result, root);
		
		return result;
	}
	
	protected void getValues(List<T> col, Node n) {
		if (n.link != null) {
			col.add(n.link);
		}
		for (int i = 0; i < n.count; i++) {
			getValues(col, n.children[i]);
		}
	}

	@Override
	public Set<Map.Entry<String, T>> entrySet() {
		
		return null;
	}
	
}
