package com.lwan.strcom;

public class PairLocale {
	//Properties
	public int Start;
	public int End;
	
	public PairLocale (int start, int end) {
		Start = start;
		End = end;
	}
	
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append('(').append(Start).append("->").append(End).append(')');
		return str.toString();
	}
}