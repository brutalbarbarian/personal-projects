package com.lwan.util.wrappers;

public interface CallbackEx<T1, T2, R> {
	public R call(T1 a, T2 b);
}
