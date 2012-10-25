package com.lwan.eaproj.sp;

import com.lwan.jdbc.StoredProc;

/**
 * Do not use this class for anything as its just an empty stored proc.
 * When creating any new stored procs, just copy paste the code below
 * into the new class, and fill in the arrays.
 * 
 * @author Brutalbarbarian
 *
 */
@Deprecated
public class StoredProcTemplate extends StoredProc{
	private static final String[] PARAMS = {};
	private static final int[] PARAM_TYPES = {};
	private static final String[] STATEMENTS = {};
	
	public StoredProcTemplate () {
		super(PARAMS, PARAM_TYPES, STATEMENTS);
	}
}
