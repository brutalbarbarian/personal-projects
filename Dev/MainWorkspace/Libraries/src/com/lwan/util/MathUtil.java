package com.lwan.util;

import java.awt.Point;
import java.awt.Polygon;

/**
 * Maths utlity static library used to store arbitory maths calculations and convenience methods
 * 
 * @author Brutalbarbarian
 *
 */
public class MathUtil {
	/**
	 * Convenience method for int powers.
	 * Power must be a positive number as the returned result is expected to be an integer 
	 * 
	 * @param base
	 * @param power
	 * @return
	 */
	public static int pow(int base, int power) {
		int result = 1;
		for (int i = 0; i < power; i++) {
			result = result * base;
		}
		return result;
	}
	
	public static boolean isOdd (int i) {
		return i%2 == 1;
	}
	
	public static boolean isEven (int i) {
		return i%2 == 0;
	}
	
	/**
	 * Find the maximum value of a list of ints
	 * 
	 * @param values
	 * @return
	 */
	public static int min (int ... values) {
		if (values.length == 0) {
			throw new IllegalArgumentException();
		}
		int min = values[0];
		for (int val : values) {
			if (val < min) {
				min = val;
			}
		}
		return min;
	}
	
	public double round(double d, int percision) {
		double multiplier = Math.pow(10, percision);
		return round(d * multiplier)/multiplier;
	}
	
	/**
	 * Rounds a double value such that it has 0 decimal places.
	 * This is exactly the same as Math.round() except it dosen't add the
	 * extra step of casting into a long, which may cause issues.
	 * 
	 * @param a
	 * @return
	 */
	public static double round(double a) {
		if (a != 0x1.fffffffffffffp-2) { // greatest double value less than 0.5
			return Math.floor(a + 0.5d);
		} else {
			return 0;
		}
	}

	/**
	 * Find the maximum value of a list of doubles
	 * 
	 * @param values
	 * @return
	 */
	public static double min (double ... values) {
		if (values.length == 0) {
			throw new IllegalArgumentException();
		}
		double min = values[0];
		for (double val : values) {
			if (val < min) {
				min = val;
			}
		}
		return min;
	}
	
	/**
	 * Convenience method for squaring ints 
	 * 
	 * @param val
	 * @return
	 */
	public static int sq (int val) {
		return val*val;
	}
	
	/**
	 * Convenience method for squaring doubles
	 * 
	 * @param val
	 * @return
	 */
	public static double sq (double val) {
		return val*val;
	}
	
	/**
	 * Find the maximum value of a list of ints
	 * 
	 * @param values
	 * @return
	 */
	public static int max (int ... values) {
		if (values.length == 0) {
			throw new IllegalArgumentException();
		}
		int max = values[0];
		for (int val : values) {
			if (val > max) {
				max = val;
			}
		}
		return max;
	}
	
	/**
	 * Find the maximum value of a list of doubles
	 * 
	 * @param values
	 * @return
	 */
	public static double max (double ... values) {
		if (values.length == 0) {
			throw new IllegalArgumentException();
		}
		double max = values[0];
		for (double val : values) {
			if (val > max) {
				max = val;
			}
		}
		return max;
	}

	/**
	 * Calculate the factorial of a long.
	 * Note - if input is greater then 20, exception will occur as the value will be 
	 * too large to fit in a long.
	 * 
	 * @param n
	 * @return
	 */
	public static long factorial(long n) {
        if (n <  0) throw new RuntimeException("Underflow error in factorial");
        else if (n > 20) throw new RuntimeException("Overflow error in factorial");
        else if (n == 0) return 1;
        else return n * factorial(n-1);
	}
	
	public static int dotProduct (int x1, int y1, int x2, int y2) {
		return y2*y1 + x2*x1;
	}
	
	public static double dotProduct (double x1, double y1, double x2, double y2) {
		return y2*y1 + x2*x1;
	}
	
	/**
	 * Check if 2 polygons intersect using SAT
	 * Note that this only works on polygons with only extruding angles
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean polygonIntersect (Polygon a, Polygon b) {
		for (int i = 0; i < a.npoints; i++) {
			int prev = i==0? a.npoints-1: i-1;
			int normX = a.ypoints[i] - a.ypoints[prev];
			int normY = -(a.xpoints[i] - a.xpoints[prev]);
			
			int aMax, aMin, bMax, bMin;
			aMin = aMax = dotProduct (a.xpoints[0], a.ypoints[0], normX, normY);
			for (int j = 1; j < a.npoints; j++) {
				int dot = dotProduct (a.xpoints[j], a.ypoints[j], normX, normY);
				if (dot < aMin) {
					aMin = dot;
				}
				if (dot > aMax) {
					aMax = dot;
				}
			}
			bMin = bMax = dotProduct (b.xpoints[0], b.ypoints[0], normX, normY);
			for (int j = 1; j < b.npoints; j++) {
				int dot = dotProduct (b.xpoints[j], b.ypoints[j], normX, normY);
				if (dot < bMin) {
					bMin = dot;
				}
				if (dot > bMax) {
					bMax = dot;
				}
			}
			if (aMin > bMax || bMin > aMax) {
				return false;
			}
		}
		for (int i = 0; i < b.npoints; i++) {
			int prev = i==0? b.npoints-1: i-1;
			int normX = b.ypoints[i] - b.ypoints[prev];
			int normY = -(b.xpoints[i] - b.xpoints[prev]);
			
			int aMax, aMin, bMax, bMin;
			aMin = aMax = dotProduct (a.xpoints[0], a.ypoints[0], normX, normY);
			for (int j = 1; j < a.npoints; j++) {
				int dot = dotProduct (a.xpoints[j], a.ypoints[j], normX, normY);
				if (dot < aMin) {
					aMin = dot;
				}
				if (dot > aMax) {
					aMax = dot;
				}
			}
			bMin = bMax = dotProduct (b.xpoints[0], b.ypoints[0], normX, normY);
			for (int j = 1; j < b.npoints; j++) {
				int dot = dotProduct (b.xpoints[j], b.ypoints[j], normX, normY);
				if (dot < bMin) {
					bMin = dot;
				}
				if (dot > bMax) {
					bMax = dot;
				}
			}
			if (aMin > bMax || bMin > aMax) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Absolutes a number - i.e. get the positive value from a number.
	 * Faster then java.lang.Math.abs()
	 * 
	 * @param i
	 * @return
	 */
	public static int abs (int i) {
		return i<0? -i: i;
	}

	public static double abs (double i) {
		return i<0? -i: i;
	}
	
	/**
	 * Polygon intersect for regular shapes with centre points.
	 * Faster on average compared to SAT, most noticebly as the number
	 * of collisions increases.
	 * Like SAT, this only works when all angles are extruding.
	 * 
	 * @param a
	 * @param b
	 * @param aCent
	 * @param bCent
	 * @return
	 */
	public static boolean polygonIntersect (Polygon a, Polygon b, Point aCent, Point bCent) {
		return polygonIntersect(a, b, aCent.x, aCent.y, bCent.x, bCent.y);
	}
	
	/**
	 * Polygon intersect for regular shapes with centre points.
	 * Faster on average compared to SAT, most noticebly as the number
	 * of collisions increases
	 * Like SAT, this only works when all angles are extruding.
	 * 
	 * @param a
	 * @param b
	 * @param aCent
	 * @param bCent
	 * @return
	 */
	public static boolean polygonIntersect (Polygon a, Polygon b, int x1, int y1, int x2, int y2) {
		//finding the 2 closest points
		int aP = 0, bP = 0;
		for (int i = 1; i < a.npoints; i++) {
			if ((abs(a.xpoints[i] - x2) + abs(a.ypoints[i] - y2)) < (abs(a.xpoints[aP] - x2) + abs(a.ypoints[aP] - y2))) {
				aP = i;
			}
		}
		for (int i = 1; i < b.npoints; i++) {
			if ((abs(b.xpoints[i] - x1) + abs(b.ypoints[i] - y1)) < (abs(b.xpoints[bP] - x1) + abs(b.ypoints[bP] - y1))) {
				bP = i;
			}
		}

		int aMin, aMax, bMin, bMax, normX, normY, oP, dot;
		//test normal a:a-1
		oP = aP==0? a.npoints-1: aP - 1;
		normX = a.ypoints[aP] - a.ypoints[oP];
		normY = -(a.xpoints[aP] - a.xpoints[oP]);
		oP = (aP + 1)%a.npoints;
		aMin = abs(dotProduct (a.xpoints[oP], a.ypoints[oP], normX, normY));
		if ((dot = abs(dotProduct (a.xpoints[aP], a.ypoints[aP], normX, normY))) > aMin) {
			aMax = dot;
		} else {
			aMax = aMin;
			aMin = dot;
		}
		bMin = abs(dotProduct (b.xpoints[bP], b.ypoints[bP], normX, normY));
		oP = bP==0? b.npoints-1: bP-1;
		if ((dot = abs(dotProduct(b.xpoints[oP], b.ypoints[oP], normX, normY))) > bMin) {
			bMax = dot; 
		} else {
			bMax = bMin;
			bMin = dot;
		}
		oP = (bP+1)%b.npoints;
		if ((dot = abs(dotProduct(b.xpoints[oP], b.ypoints[oP], normX, normY))) < bMin) {
			bMin = dot; 
		} else if (dot > bMax){
			bMax = dot;
		}
		if ((aMin < bMin && aMax <= bMin) || (bMin<aMin && bMax <= aMin)) {
			return false;
		}
		
		//test normal a:a+1
		oP = (aP+1)%a.npoints;
		normX = a.ypoints[aP] - a.ypoints[oP];
		normY = -(a.xpoints[aP] - a.xpoints[oP]);
		oP = aP==0? a.npoints-1: aP - 1;
		aMin = abs(dotProduct (a.xpoints[oP], a.ypoints[oP], normX, normY));
		if ((dot = abs(dotProduct (a.xpoints[aP], a.ypoints[aP], normX, normY))) > aMin) {
			aMax = dot;
		} else {
			aMax = aMin;
			aMin = dot;
		}
		bMin = abs(dotProduct (b.xpoints[bP], b.ypoints[bP], normX, normY));
		oP = bP==0? b.npoints-1: bP-1;
		if ((dot = abs(dotProduct(b.xpoints[oP], b.ypoints[oP], normX, normY))) > bMin) {
			bMax = dot; 
		} else {
			bMax = bMin;
			bMin = dot;
		}
		oP = (bP+1)%b.npoints;
		if ((dot = abs(dotProduct(b.xpoints[oP], b.ypoints[oP], normX, normY))) < bMin) {
			bMin = dot; 
		} else if (dot > bMax){
			bMax = dot;
		}
		if ((aMin < bMin && aMax <= bMin) || (bMin<aMin && bMax <= aMin)) {
			return false;
		}
			
		//test normal b:b-1
		oP = bP==0? b.npoints-1: bP - 1;
		normX = b.ypoints[bP] - b.ypoints[oP];
		normY = -(b.xpoints[bP] - b.xpoints[oP]);
		oP = (bP + 1)%b.npoints;
		bMin = abs(dotProduct (b.xpoints[oP], b.ypoints[oP], normX, normY));
		if ((dot = abs(dotProduct (b.xpoints[bP], b.ypoints[bP], normX, normY))) > bMin) {
			bMax = dot;
		} else {
			bMax = bMin;
			bMin = dot;
		}
		aMin = abs(dotProduct (a.xpoints[aP], a.ypoints[aP], normX, normY));
		oP = aP==0? a.npoints-1: aP-1;
		if ((dot = abs(dotProduct(a.xpoints[oP], a.ypoints[oP], normX, normY))) > aMin) {
			aMax = dot; 
		} else {
			aMax = aMin;
			aMin = dot;
		}
		oP = (aP+1)%a.npoints;
		if ((dot = abs(dotProduct(a.xpoints[oP], a.ypoints[oP], normX, normY))) < aMin) {
			aMin = dot; 
		} else if (dot > aMax){
			aMax = dot;
		}
		if ((aMin < bMin && aMax <= bMin) || (bMin<aMin && bMax <= aMin)) {
			return false;
		}
		
		//test normal b:b+1
		oP = (bP+1)%b.npoints;
		normX = b.ypoints[bP] - b.ypoints[oP];
		normY = -(b.xpoints[bP] - b.xpoints[oP]);
		oP = bP==0? b.npoints-1: bP - 1;
		bMin = abs(dotProduct (b.xpoints[oP], b.ypoints[oP], normX, normY));
		if ((dot = abs(dotProduct (b.xpoints[bP], b.ypoints[bP], normX, normY))) > bMin) {
			bMax = dot;
		} else {
			bMax = bMin;
			bMin = dot;
		}
		aMin = abs(dotProduct (a.xpoints[aP], a.ypoints[aP], normX, normY));
		oP = aP==0? a.npoints-1: aP-1;
		if ((dot = abs(dotProduct(a.xpoints[oP], a.ypoints[oP], normX, normY))) > aMin) {
			aMax = dot; 
		} else {
			aMax = aMin;
			aMin = dot;
		}
		oP = (aP+1)%a.npoints;
		if ((dot = abs(dotProduct(a.xpoints[oP], a.ypoints[oP], normX, normY))) < aMin) {
			aMin = dot; 
		} else if (dot > aMax){
			aMax = dot;
		}

		
		if ((aMin < bMin && aMax <= bMin) || (bMin<aMin && bMax <= aMin)) {
			return false;
		}
		
		return true;
	}
	
	/** 
	 * Method that calculates the Least Common Multiple (LCM) of two strictly
	 * positive integer numbers.
	 *
	 * @param x1 First number
	 * @param x2 Second number
	 * */
	public static final int lcm(int x1,int x2) {
		if(x1<=0 || x2<=0) {
			throw new IllegalArgumentException();
		}
		int max,min;
		if (x1>x2) {
			max = x1;
			min = x2;
		} else {
			max = x2;
			min = x1;
		}
		for(int i=1; i<=min; i++) {
			if( (max*i)%min == 0 ) {
				return i*max;
			}
		}
		throw new Error("Cannot find the least common multiple of numbers "+x1+" and "+x2);
	}

	/**
	 * Return the greatest common divisor
	 * 
	 * @param a
	 * @param b
	 */
	public static int gcd(int a, int b) {
		if (b==0) 
			return a;
		else
			return gcd(b, a % b);
	} 

}
