package test;

import com.lwan.util.StringUtil;

public class StringTest {
	public static void main(String[] args) {
		String a = "   abc   ";
		String b = StringUtil.trimRight(a);
		System.out.println(a);
		System.out.println(b);
	}
}
