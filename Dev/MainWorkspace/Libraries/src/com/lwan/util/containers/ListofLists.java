package com.lwan.util.containers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * List of lists collection class. </br>
 * Iterator is equivlent to flatening the outter list such that it goes through each item in the first list, followed by the second, and so on.
 * 
 * 
 * @author brutalbarbarian
 *
 * @param <T>
 */
public class ListofLists<T> implements Collection<T> {
	private Vector<List<T>> list;
	
	public ListofLists () {
		list = new Vector<>();
	}

	public List<T> get(int i) {
		return list.get(i);
	}
	
	public void add (List<T> l) {
		list.add(l);
	}
	
	public void set (int index, List<T> l) {
		list.set(index, l);
	}
	
	public List<T> remove (int index) {
		return list.remove(index);
	}
	
	public int size() {
		int size = 0;
		for (List<T> l : list) {
			size += l.size();
		}
		return size;
	}

	public boolean isEmpty() { 
		return size()==0;
	}

	public boolean contains(Object o) {
		for (List<T>l : list) {
			if (l.contains(o)) return true;
		}
		return false;
	}
	
	private class It implements Iterator<T> {
		Iterator<List<T>> outer;
		Iterator<T> inner;
		T next;
		
		It () {
			outer = list.iterator();
			setNext();
			
		}
		
		void setNext() {
			if (inner == null || !inner.hasNext()) {
				if (outer.hasNext()) {
					inner = outer.next().iterator();
					setNext(); 
				} else {
					next = null;
				}
			} else {
				next = inner.next();
			}
		}
		
		public boolean hasNext() {
			return next != null;
		}

		public T next() {
			T ret = next;
			setNext();
			return ret;
		}

		@Deprecated
		public void remove() {
			throw new UnsupportedOperationException ();
		}
		
	}

	public Iterator<T> iterator() {
		return new It();
	}

	public Object[] toArray() {
		Object[] a = new Object[size()];
		int off = 0;
		for (List<T>l:list) {
			System.arraycopy(l.toArray(), 0, a, off, l.size());
			off += l.size();
		}
		return a;
	}

	@SuppressWarnings("hiding")
	public <T> T[] toArray(T[] a) {
		int size = size();
		if(a.length < size)  {
			a = Arrays.copyOf(a, size);
		}
		int off = 0;
		for (List<?> l:list) {
			System.arraycopy(l.<T>toArray(Arrays.copyOf(a, l.size())), 0, a, off, l.size());
			off += l.size();
		}
		return a;
	}

	/**
	 * Cannot add generically. Must add to individual lists
	 */
	@Deprecated
	public boolean add(T e) {
		return false;
	}

	public boolean remove(Object o) {
		for (List<T>l : list) {
			if (l.remove(o)) return true;
		}
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		for (Object e : c) {
			if (!contains(e)) {
				return false;
			}
		}
		return true;
	}

	
	@Deprecated
	public boolean addAll(Collection<? extends T> c) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for (Object o : c) {
			if (remove(o)){
				modified = true;
			}
		}
		return modified;
	}

	@Override
	
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		list.clear();
	}
}
