package com.lwan.javafx.property;

import java.util.Set;

import javafx.beans.value.ObservableValue;

public class Validation {
	public static final double DBL_NO_LIMIT = Double.NaN;
	public static final int INT_NO_LIMIT = Integer.MIN_VALUE; 
	public static final int STR_NO_LIMIT = -1;
	
	public static class DoubleValidator implements ValidationListener<Double> {
		double min, max;
		public DoubleValidator (double minValue, double maxValue) {
			min = minValue;
			max = maxValue;
		}
		
		@Override
		public boolean validate(ObservableValue<Double> value, Double oldValue,
				Double newValue) {
			return (min == DBL_NO_LIMIT || newValue >= min) &&
					(max == DBL_NO_LIMIT || newValue <= max);
		}
	}
	
	public static class IntegerValidator implements ValidationListener<Integer> {
		int min, max;
		public IntegerValidator (int minValue, int maxValue) {
			min = minValue;
			max = maxValue;
			
		}
		
		@Override
		public boolean validate(ObservableValue<Integer> value, Integer oldValue,
				Integer newValue) {
			return (min == INT_NO_LIMIT || newValue >= min) &&
					(max == INT_NO_LIMIT || newValue <= max);
		}

	}
	
	public static class StringValidator implements ValidationListener<String> {
		int min, max;
		boolean letters, numeric, white, line, symbols;
		boolean needCheck;
		Set<Character>invalid;
		
		public StringValidator(int minLength, int maxLength, boolean allowLetters,
				boolean allowNumeric, boolean allowWhiteSpace, boolean allowNewLine,
				boolean allowSymbols, Set<Character>invalidChars) {
			min = minLength;
			max = maxLength;
			letters = allowLetters;
			numeric = allowNumeric;
			white = allowWhiteSpace;
			line = allowNewLine;
			symbols = allowSymbols;
			invalid = invalidChars;
			
			// optimize the check to only check if actually needed
			needCheck = !(letters && numeric && white && line && symbols && 
					(invalid == null || invalid.size() == 0));
		}

		public boolean validate(ObservableValue<String> value, String oldValue,
				String newValue) {
			if (newValue == null) return true;	// Don't care if its null
			
			int length = newValue.length();
			if (!((min == STR_NO_LIMIT || length >= min) && (max == STR_NO_LIMIT || length <= max))) {
				return false;
			}
			
			// This check is slow...only do it if needed
			if (needCheck) {
				char[] chars = newValue.toCharArray();
				for (char c : chars) {
					// if it fails any of the tests... return false
					if (!(	(letters || !Character.isLetter(c)) &&
							(numeric || !Character.isDigit(c)) &&
							(white || !Character.isWhitespace(c)) &&
							(line || !(c == '\n' || c == '\r')) &&
							// non letter, digits and white space, must be a symbol
							(symbols || (Character.isLetterOrDigit(c) || Character.isWhitespace(c))) &&
							(invalid == null || !invalid.contains(c)))) {
						return false;
					}
				}
			}
			
			return true;
		}
		
	}
}
