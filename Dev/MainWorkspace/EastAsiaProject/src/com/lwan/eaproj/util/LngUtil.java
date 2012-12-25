package com.lwan.eaproj.util;

import java.util.Iterator;

import com.lwan.eaproj.app.Lng;
import com.lwan.util.CollectionUtil;

public class LngUtil {
	public static Iterable<String> translate (String[] it, String... variables) {
		return translate(CollectionUtil.getIterable(it), variables);
	}
	
	public static Iterable<String> translate (Iterable<String> it, String... variables) {
		return CollectionUtil.getIterable(_translate(it, variables));
	}
	
	public static Iterator<String> _translate (Iterable<String> it, final String...variables) {
		final Iterator<String> iter = it.iterator();
		return new Iterator<String> () {
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public String next() {
				String next = iter.next();
				return next == null? "" : Lng._(next, variables);
			}

			@Override
			public void remove() {
				iter.remove();
			}
		};
	}
}
