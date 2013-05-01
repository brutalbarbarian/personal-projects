package test;

import java.util.Arrays;

public class ArraySearchTest {
	public static void main(String[]args) {
		char[] array = {'2', 'a', 'c', 'j', 'p'};
		System.out.println(Arrays.binarySearch(array, 'B'));
		int index = - (Arrays.binarySearch(array, 'B') + 1);
		System.out.println(index);
	}
}
