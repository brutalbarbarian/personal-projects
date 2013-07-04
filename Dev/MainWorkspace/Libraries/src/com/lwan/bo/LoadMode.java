package com.lwan.bo;

/**
 * Load mode of a BOSet
 * 
 * BOSet uses Active as default
 * Active will attempt to set active to all its children upon adding them to the set
 * Passive will load children, but keep them in an unloaded state until those children are directly accessed
 * Cache will not load any children. Instead cache will only load any children upon calling either
 * findByID() or ensureActive().
 * 
 * @author Lu
 */
public enum LoadMode {
	CACHE,	// Implemented from BOSet
	PASSIVE,// Implemented from BOSet
	// Will need to be implemented inside implementation of BOSet in populate attribute
	ACTIVE
}
