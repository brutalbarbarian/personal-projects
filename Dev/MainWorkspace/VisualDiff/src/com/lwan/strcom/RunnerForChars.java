package com.lwan.strcom;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.lwan.strcom.gui.GSettings;
import com.lwan.util.MathUtil;
import com.lwan.util.containers.Pair;

/**
 * 
 * @author Brutalbarbarian
 *
 */
public class RunnerForChars {
//	private char [] c1, c2;
	private List<String> s1, s2;
	private List<DiffInfo> res;
	private List<Pair<Integer, Integer>> s1Ref,s2Ref;
	
	private int [][] grid;
	
	private boolean ignoreWhiteSpace;
	
	private RunnerForChars (String f1, String f2) {
//		c1 = f1.toCharArray();
//		c2 = f2.toCharArray();
		
		//tokenise the char arrays
		s1Ref = new Vector<>();
		s2Ref = new Vector<>();
		s1 = new Vector<>();
		s2 = new Vector<>();
		
		ignoreWhiteSpace = GSettings.ignoreWhiteSpace().getValue();
		s1 = tokenise(f1.toCharArray(), s1, s1Ref);
		s2 = tokenise(f2.toCharArray(), s2, s2Ref);
	}
	
	protected boolean isNoChange (int pos1, int pos2) {
		return pos1 >= 0 && pos2 >= 0 && (s1.get(pos1).equals(pos2));
	}
	
	protected boolean sameType (char c1, char c2) {
		return (Character.isLetter(c1) && Character.isLetter(c2)) ||
				(Character.isDigit(c1) && Character.isDigit(c2)) ||
				// Only clump white space together if we're ignoring them
				(ignoreWhiteSpace && Character.isWhitespace(c1) && Character.isWhitespace(c2));
				//we don't want non letter/numbers to bind together into one token
				// ||
				//!(Character.isLetterOrDigit(c1) || Character.isLetterOrDigit(c2));
	}
	
	protected List<String> tokenise(char[] chars, List<String> res, List<Pair<Integer,Integer>> ref) {
		int pos = 0;
		int i = 0;
		while (pos < chars.length) {
			for (i = pos + 1; (i < chars.length && sameType(chars[pos], chars[i])); i++) {}
			String val = String.copyValueOf(chars, pos, i - pos);
			// if ignore white space is on...treat all white space as a single white space
			if (ignoreWhiteSpace && val.trim().length() == 0) {
				val = " ";	// need at least one character...
			}
			res.add(val);
			//need to keep reference to pos and i
			ref.add(new Pair<>(pos, i)); 
			pos = i;
		}
		return res;
	}
	
	protected int getUpdateCost (String s1, String s2, boolean prevWasNoChange) {
		if (s1.equals(s2)) {
			return 0;
//			return prevWasNoChange? -1 : 0;	//cheaper to go no change if prev token was also no change
		//assume both strings have length > 0 (should be the case after the tokenise method)
		} else if (sameType(s1.charAt(0), s2.charAt(0))) {
//			return Math.abs(s2.length() - s1.length()) + 1;
			return 1;
		} else {
			return Short.MAX_VALUE;	//different types
		}
	}
	
	protected int getInsertCost (String s) {
//		return s.length();	//costs length of s
		return 1;
	}
	
	protected int getDeleteCost (String s) {
//		return s.length();	//costs length of s
		return 1;
	}
	
	private void run () {
		grid = new int [s1.size()+1][s2.size()+1];
		//compute
		for (int i = 0; i <= s1.size(); i++) {
			for (int j = 0; j <= s2.size(); j++) {
				if (i == 0 && j == 0) {
					grid[i][j] = 0;
				} else if (i == 0) {
					grid[i][j] = grid[i][j-1] + getInsertCost(s2.get(j-1));
				} else if (j == 0) {
					grid[i][j] = grid[i-1][j] + getDeleteCost(s1.get(i-1));
				} else {
					grid[i][j] = MathUtil.min(	grid[i][j-1] + getInsertCost(s2.get(j-1)), 
												grid[i-1][j] + getDeleteCost(s1.get(i-1)), 
												grid[i-1][j-1] + getUpdateCost(s1.get(i-1), s2.get(j-1), isNoChange(i-2, j-2)));
				}
			}
		}
		
//		printGrid();
//		CollectionUtil.printV(s1, "^");
//		CollectionUtil.printV(s2, "^");
//		
		res = new LinkedList <> ();
		//backtrack
		int i = s1.size(), j = s2.size();
		int starti, startj, prePath, nxtPath;
		prePath = nxtPath = getBestPrePath(i,j);
		while (true) {	//while still not home
			starti = i;
			startj = j;
			//while still following same path
			while (prePath == nxtPath) {
				switch (nxtPath) {
				case DiffInfo.TYPE_INSERT:
					j--;
					break;
				case DiffInfo.TYPE_DELETE:
					i--;
					break;
				case DiffInfo.TYPE_UPDATE:
				case DiffInfo.TYPE_NO_CHANGE:
					i --;
					j--;
				}
				if(i == 0 && j == 0) break;	//breakout if hit home
				nxtPath = getBestPrePath(i,j);
			}
			//ether hit home or end of 'same in a row'
			switch(prePath) {
			case DiffInfo.TYPE_INSERT:
				res.add(0, 	new DiffInfo(prePath, null, 
						new PairLocale(s2Ref.get(startj == j? j-1 : j).a, s2Ref.get(startj - 1).b)));
				break;
			case DiffInfo.TYPE_DELETE:
				res.add(0, 	new DiffInfo(prePath, new PairLocale(s1Ref.get(starti == i? i-1 : i).a, s1Ref.get(starti-1).b), null));
				break;
			default:
				res.add(0, 	new DiffInfo(prePath, new PairLocale(s1Ref.get(starti == i? i-1 : i).a, s1Ref.get(starti-1).b), 
						new PairLocale(s2Ref.get(startj == j? j-1 : j).a, s2Ref.get(startj - 1).b)));					
				break;
			}
			
			//set prepath to be same as nxtpath
			prePath = nxtPath;
			if (i == 0 && j == 0) break;//finish if hit home
		}
	}
	
	protected int getBestPrePath (int i, int j) {
		if (i == 0) {
			return DiffInfo.TYPE_INSERT;
		} else if (j == 0) {
			return DiffInfo.TYPE_DELETE;
		} else if (grid[i][j] == grid[i-1][j-1] + getUpdateCost(s1.get(i-1), s2.get(j-1), isNoChange(i-2, j-2))) {
			if (grid[i][j] == grid[i-1][j-1]) {
				return DiffInfo.TYPE_NO_CHANGE;
			} else {
				return DiffInfo.TYPE_UPDATE;
			}
		} else if (grid[i][j] == grid[i-1][j] + getInsertCost(s1.get(i-1))) {
			return DiffInfo.TYPE_DELETE;			
		} else if (grid[i][j] == grid[i][j-1] + getDeleteCost(s2.get(j-1))) {
			return DiffInfo.TYPE_INSERT;
		}
		return 0;
	}
	
	protected void printGrid () {
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[0].length; j++) {
				System.out.printf("%3d ", grid[i][j]);
			}
			System.out.println();
		}
	}
	
	
	public static List<DiffInfo> run(String f1, String f2) {
		RunnerForChars r = new RunnerForChars(f1, f2);
		r.run();
		
		return r.res;
	}
	
//	public static void main(String[] args) {
//		String s1 = "import java.awt.Insets;\n" +
//					"import java.awt.event.MouseEvent;\n" +
//					"import java.awt.event.MouseListener;\n" +
//					"import java.awt.event.MouseMotionListener;";
//		String s2 = "import java.awt.Point;\n" +
//					"import java.awt.Rectangle;\n" +
//					"import java.awt.event.AdjustmentEvent;\n" +
//					"import java.awt.event.AdjustmentListener;\n" +
//					"import java.awt.event.ComponentEvent;\n" +
//					"import java.awt.event.ComponentListener;\n" +
//					"import java.awt.event.MouseWheelEvent;\n" +
//					"import java.awt.event.MouseWheelListener;\n" +
//					"import java.beans.PropertyChangeEvent;\n" +
//					"import java.beans.PropertyChangeListener;";
//
//		CollectionUtil.printV(run(s1, s2), "\n");
////		CollectionUtil.printV(run("Hello World{\n	System.out.println();\n}", "Blue World{\n	System.out.print(\"Blue\");\n}"), "\n");;;
//	}
}
