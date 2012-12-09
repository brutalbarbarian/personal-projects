package com.lwan.javafx.controls.bo;

public class Yaz {
	public static void main(String[] args) {
		String[] factions = {"Heaven", "Necro", "Sanc", "Strong", "Demon"};
		double[] chance = {0.1, 0.1, 0.25, 0.30, 0.25};

		int count = 0;
		while (count == 0) {
			for (int i = 0; i < factions.length; i++) {
				if (Math.random() < chance[i]) {
					count++;
					System.out.println(factions[i]);
				}
			}
		}
	
		
		if (Math.random() > 0.5) {
			System.out.println("blood");
		} else {
			System.out.println("tears");
		}
		
		if (Math.random() > 0.5) {
			System.out.println("might");
		} else {
			System.out.println("magic");
		}
	}
}
