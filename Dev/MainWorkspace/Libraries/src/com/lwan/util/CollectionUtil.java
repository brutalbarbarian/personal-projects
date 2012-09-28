package com.lwan.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import com.sun.javafx.collections.ObservableListWrapper;

import javafx.collections.ObservableList;

/**
 * Util class for managing collections, iterators, and arrays.
 * 
 * @author Brutalbarbarian
 *
 */
public class CollectionUtil {
	
	@SafeVarargs
	/**
	 * Same as Arrays.asList(items) but wraps the resultant list in
	 * an ObservableList
	 * 
	 * @param items
	 * @return
	 */
	public static <T> ObservableList<T> asObservableList(T...items) {
		return new ObservableListWrapper<T>(Arrays.asList(items));
	}
	
	/**
	 * Print out all values in the order that the iterator returns them
	 * Each value is seperated by the passed in seperator
	 * Note toString() is called for each item. This may potentially be slow for non-trivial
	 * data types.
	 * 
	 * @param list
	 * @param sep
	 */
	public static final void printV(Collection<?> list, String sep) {
		Iterator <?> it = list.iterator(); 
		while (it.hasNext()) {
			Object item = it.next();
			System.out.append(item.toString());
			if (it.hasNext()) {
				System.out.append(sep);
			}
		}
		System.out.println();
	}
	
	/**
	 * Convience method for wrapping an iterator into an iterable.
	 * This is used mainly for using of a foreach loop with an iterator
	 * 
	 * @param it
	 * @return
	 */
	public static <T> Iterable<T> getIterable(final Iterator<T> it) {
		return new Iterable<T>(){
			public Iterator<T> iterator() {
				return it;
			}
		};
	}
	
	/**
	 * Convience method for sorting a list by ether ascending or descending
	 * Ascending is exactly the same as calling Collections.sort(list)
	 * Descending is exactly the same as calling Collections.sort(list, Collections.reverseOrder())
	 * 
	 * @param list
	 * @param isAscending
	 */
	public static <T extends Comparable<? super T>> void sort (List<T> list, boolean isAscending) {
		if (isAscending) {
			Collections.sort(list);
		} else {	//is dec
			Collections.sort(list, Collections.reverseOrder());
		}
	}
	
	/**
	 * Runs the same method over each item in a collection,
	 * 
	 */
	public static <T, R> R Map (Collection<T> col, MapRunner<T,R> runner) {
		R res = runner.getBaseInstance();
		for (T item : col) {
			runner.run(item, res);
		}
		return res;
	}
	
	/**
	 * Runs the same method over each item in a list, starting from index 'start',
	 * up to index 'end'.
	 * 
	 * @param col
	 * @param start
	 * @param end
	 * @param runner
	 * @return
	 */
	public static <T, R> R Map (List<T> col, int start, int end, MapRunner<T, R> runner) {
		if (start == 0 && end == col.size()) {
			return Map(col, runner);
		} else {
			return Map(col.subList(start, end), runner);
		}
	}
	
	
	/**
	 * Collapses a string list down to a single string, seperated by 'sep'.
	 * This method allows the specifying of which range of strings of the list
	 * should be collapsed.
	 * 
	 * @param text
	 * @param start
	 * @param end
	 * @param sep
	 * @return
	 */
	public static String CollapseStringList (List<String> text, int start, int end, final String sep) {
		if (start == end) return "";	//nothing to process
		StringBuffer buf = Map(text, start, end, new MapRunner<String, StringBuffer>() {
			public void run(String item, StringBuffer result) {
				result.append(item).append(sep);				
			}

			public StringBuffer getBaseInstance() {
				return new StringBuffer();
			}
		});
		//guranteed to have at least one line
		buf.delete(buf.length() - sep.length(), buf.length());
		
		return buf.toString();
	}
	
	/**
	 * Collapses a string list down to a single string, seperated by 'sep.
	 * 
	 * @param text
	 * @param sep
	 * @return
	 */
	public static String CollapseStringList (List<String> text, String sep) {
		return CollapseStringList (text, 0, text.size(), sep);
	}
	
	public static interface MapRunner<T, R> {
		public void run(T item, R result);
		public R getBaseInstance();
	}
	
	/**
	 * Convert a collection to an array
	 * 
	 * @param col
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray (Collection<T>col, Class<? super T> c) {
		int size = col.size();
		T[] array = (T[])Array.newInstance(c, size);
		col.toArray(array);
		
		return array;
	}
	
	public static <T> Iterator <T> getReverseIterator (final ListIterator<T> iter) {
		return new Iterator<T>(){
			public boolean hasNext() {
				return iter.hasPrevious();
			}

			public T next() {
				return iter.previous();
			}

			public void remove() {
				iter.remove();
			}
		};
	}
	
	/**
	 * Gets the reverse iterable of an Iterable (i.e. a collection).
	 * If this collection implements ListIterator, then this operation is far more efficent.
	 * Otherwise, the iterable is iterated through, where the results are stored, before
	 * returning a iterable that iterates through the resulting list.
	 * 
	 * @param it
	 * @return
	 */
	public static <T> Iterable<T> getReverse (Iterable<T> it) {
		if (it instanceof List) {	//list takes up less computation and memory - use built in list iterator
			final ListIterator<T> iter = ((List<T>)it).listIterator(((List<T>) it).size()); 
			return new Iterable<T>() {
				public Iterator<T> iterator() {
					return getReverseIterator (iter);
				}
			};
		} 
		
		int size = 10;
		if (it instanceof Collection){	//if not a list, but is a collection
			size = ((Collection<T>)it).size();
		}
		List<T> list = new Vector<>(size);
		for (T t : it) {
			list.add(t);
		}
		final ListIterator<T> iter = list.listIterator(list.size());

		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return getReverseIterator (iter);
			}
		};
	}
	
	/**
	 * Generate an instance of Iterable for an array.
	 * This may be used in a foreach loop.
	 * 
	 * @param array
	 * @return
	 */
	public static <T> Iterable <T> getIterable (final T[] array) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return getIterator(array);
			}
		};
	}
	
	/**
	 * Generate an iterator for an array
	 * 
	 * @param array
	 * @return
	 */
	public static <T> Iterator<T> getIterator (final T [] array) {
		return new Iterator<T>() {
			private int i = 0;
			public boolean hasNext() {
				return i < array.length;
			}

			public T next() {
				return array[i++];
			}

			public void remove() {
				throw new UnsupportedOperationException (); 
			}			
		};
	}

	/**
	 * Merge the passed in arrays into a single array.
	 * The returned array will be of type of the closest common
	 * superclass
	 * 
	 * @param array
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SafeVarargs
	public static <T> T[] mergeArrays  (T[]...array) {
		if (array.length == 0) {
			return null;
		} else {
			// array of all classes
			Class[] cs = new Class[array.length];
			for (int i = 0; i < array.length; i++) {
				cs[i] = array[i].getClass().getComponentType();
			}
			// common superclass
			Class c= ClassUtil.FindCommonSuperclass(cs);
			
			int size = 0;
			for (T[] a : array) {
				size += a.length;
			}
			
			T[] res = (T[])java.lang.reflect.Array.newInstance(
            		c, size);
			int start = 0;
            for (T[] a : array) {
            	System.arraycopy(a, 0, res, start, a.length);
            	start += a.length;
            }
			
            return res;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static<T> T[] removeAll(T[] a, T[] b, boolean usesEquals) {
		if (a.length == 0) {
			return a;
		} else {
			T[] result = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), a.length);
			int i = 0;
			for (T t : a) {
				if (!exists(b, t, usesEquals)) {
					result[i++] = t;
				}
			}
			if (i < result.length) {
				T[] res = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), i);
				System.arraycopy(result, 0, res, 0, i);
				return res;
			} else {
				return result;	
			}
		}
	}
	
	/**
	 * Generic method for checking if an item exists within an array.</br>
	 * useEquals dictates to use '=' or '.equals()'.</br>
	 * true represents '.equals'</br>
	 * false represents '='
	 * 
	 * @param array
	 * @param useEquals
	 * @return
	 */
	public static <T> boolean exists (T[] array, T item, boolean useEquals) {
		for (T t : array) {
			if ((useEquals && t.equals(item)) || (!useEquals && t ==item)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Converts an iterable into a list by iterating through the
	 * iterable 
	 * 
	 * @param values
	 * @return
	 */
	public static <T> List<T> toList(Iterable<T> values) {
		List<T> res = new Vector<>();
		for (T item : values) {
			res.add(item);
		}
		return res;
	}

	/**
	 * Get a collec
	 * 
	 * @param selectedIndices
	 * @return
	 */
	public static <T> Collection<T> getAllDistinct(Collection<T> selectedIndices) {
		return new HashSet<T>(selectedIndices);
	}
}
