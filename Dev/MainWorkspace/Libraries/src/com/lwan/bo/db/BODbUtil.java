package com.lwan.bo.db;

import com.lwan.bo.BOException;
import com.lwan.bo.BusinessObject;
import com.lwan.jdbc.Parameter;
import com.lwan.jdbc.StoredProc;

public class BODbUtil {
	public static void assignParamsFromBO (StoredProc sp, BODbCustomObject obj, boolean allowMissingParam) {
		for (Parameter param : sp.getAllParameters()) {
			BODbAttribute<?> attr = obj.findAttributeByFieldName(param.name());
			if (attr == null && !allowMissingParam) {
				throw new BOException("Attribute missing in assignParamsFromBO where " +
						"allowMissingParam = false", (BusinessObject)obj);
			} else if (attr != null) {
				param.set(attr.getValue());
			}
		}
	}
}
