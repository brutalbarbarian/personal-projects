package com.lwan.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class Stats {
	public static Number covariance (Collection <? extends Number> col1, Collection <? extends Number> col2) {
		int n = col1.size();
		if (n != col2.size()) {
			throw new IllegalArgumentException();
		}
		double xMean = mean(col1).doubleValue();
		double yMean = mean(col2).doubleValue();
		double xySum = 0;
		Iterator<? extends Number> xI = col1.iterator();
		Iterator<? extends Number> yI = col2.iterator();
		
		for (int i = 0; i < n; i++) {
			xySum += (xI.next().doubleValue()-xMean)*(yI.next().doubleValue()-yMean);
		}
		
		return xySum/(n-1);
	}
	
	public static Number pearsonCorrelation(Collection <? extends Number> col1, Collection<? extends Number> col2) {
		double coVar = covariance (col1, col2).doubleValue();
		double stdx = stdDeviation(col1).doubleValue();
		double stdy = stdDeviation(col2).doubleValue();
		return coVar/(stdx*stdy);
	}
	
	public static Number median (Collection<? extends Number> col) {
		Number[] n = CollectionUtil.toArray(col, Number.class);
		Arrays.sort(n);
		return n[n.length/2];
	}
	
	public static Number stdDeviation (Collection <? extends Number> col) {
		return Math.sqrt(variance(col).doubleValue());
	}
	
	public static Number variance (Collection<? extends Number> col) {
		double mean = mean(col).doubleValue();
		double accumSum = 0;
		for (Number n : col) {
			accumSum += MathUtil.sq(n.doubleValue() - mean);
		}
		accumSum /= col.size();
		return accumSum;
	}
	
	public static Number mean (Collection<? extends Number> col) {
		return sum(col).doubleValue()/col.size();
	}
	
	public static Number sum (Collection<? extends Number> col) {
		double sum = 0;
		for (Number n : col) {
			sum += n.doubleValue();
		}
		return sum;
	}
}
