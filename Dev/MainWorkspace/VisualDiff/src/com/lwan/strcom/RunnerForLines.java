package com.lwan.strcom;

import java.util.LinkedList;
import java.util.List;

import com.lwan.strcom.gui.GSettings;
import com.lwan.util.MathUtil;

/**
 * 
 * @author Brutalbarbarian
 *
 */
public class RunnerForLines {
	protected List<String> oDoc, nDoc;
	protected List<DiffInfo> res;
	protected int [][] grid;
	
	protected RunnerForLines (List<String> f1, List<String> f2) {
		oDoc = f1; 
		nDoc = f2;
	}
	
	protected int getInsertCost () {
		return 10;
	}
	
	protected int getDeleteCost () {
		return 10;
	}
	
	protected int getUpdateCost (String sOld, String sNew) {
		if (GSettings.ignoreWhiteSpace().getValue()) {
			if (equalsIgnoreWhiteSpace(sOld, sNew)) return 0;
		} else {
			if (sOld.equals(sNew)) return 0;
		}
		return 10;	//dosen't really matter as we're combining diffs anyway
	}
	
	protected boolean equalsIgnoreWhiteSpace(String sOld, String sNew) {
		char[] c1 = sOld.toCharArray();
		char[] c2 = sNew.toCharArray();
		
		boolean iSpace, jSpace;
		int i = 0, j = 0;
		while (true) {
			// we really should be checking if one side has white space and the other dosen't....
			// find next non-white space character on c1
			iSpace = jSpace = false;
			while(i < c1.length && Character.isWhitespace(c1[i])) {
				iSpace = true;
				i++;
			}
			// find next non-white space character on c2
			while(j < c2.length && Character.isWhitespace(c2[j])) {
				jSpace = true;
				j++;
			}
			// if both reached end without finding non white space char..
			if((i == c1.length) && (j == c2.length)) {
				return true;
			// left has reached end...keep checking if right is white space
			} else if (i == c1.length) {
				if (Character.isWhitespace(c2[j])) {
					j++;
				} else {
					return false;	
				}
			// right has reached end... keep checking if left is white space
			} else if (j == c2.length) {
				if (Character.isWhitespace(c1[i])) {
					i++;
				} else {
					return false;
				}
			} else if (iSpace != jSpace && i > 0 && j > 0) {	//if isn't first and either left only has space or right only has space
				return false;
			} else if(c1[i] != c2[j]) {
				// unequal characters...obviously not equal
				return false;
			} else {
				// equal characters... so keep searching
				i++;
				j++;
			}
		}
	}
	
	//take two file streams
	//returns - list of all differences
	//pairs- a: type of change, b: from line 'l1' to line 'l2'
	//a is ether 0: Left insert(deleted), 1: Right insert(new), 2: changed
	protected void run () {
//		long time = System.currentTimeMillis();
		grid = new int [oDoc.size()+1][nDoc.size()+1];
		//compute
		for (int i = 0; i <= oDoc.size(); i++) {
			for (int j = 0; j <= nDoc.size(); j++) {
				if (i == 0 && j == 0) {
					grid[i][j] = 0;
				} else if (i == 0) {
					grid[i][j] = grid[i][j-1] + getInsertCost();
				} else if (j == 0) {
					grid[i][j] = grid[i-1][j] + getDeleteCost();
				} else {
					grid[i][j] = MathUtil.min(	grid[i][j-1] + getInsertCost(), 
												grid[i-1][j] + getDeleteCost(), 
												grid[i-1][j-1] + getUpdateCost(oDoc.get(i-1), nDoc.get(j-1)));
				}
			}
		}
//		time = System.currentTimeMillis() - time;
//		System.out.println("grid:"+time);
//		time = System.currentTimeMillis();
		res = new LinkedList <> ();
		//backtrack
		int i = oDoc.size(), j = nDoc.size();
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
			res.add(0, new DiffInfo(prePath, new PairLocale(starti == i? i-1 : i, starti-1), 
					new PairLocale(startj == j? j-1 : j, startj - 1)));
			
			//set prepath to be same as nxtpath
			prePath = nxtPath;
			if (i == 0 && j == 0) break;//finish if hit home
		}
//		time = System.currentTimeMillis() - time;
//		System.out.println("done:"+time);
//		System.out.println("done");
	}
	
	protected void printGrid () {
		for(int i = 0; i < grid.length; i++) {
			for(int j = 0; j < grid[0].length; j++) {
				System.out.printf("%3d ", grid[i][j]);
			}
			System.out.println();
		}
	}
	
	protected int getBestPrePath (int i, int j) {
		if (i == 0) {
			return DiffInfo.TYPE_INSERT;
		} else if (j == 0) {
			return DiffInfo.TYPE_DELETE;
		} else if (grid[i][j] == grid[i-1][j-1] + getUpdateCost(oDoc.get(i-1), nDoc.get(j-1))) {
			if (grid[i][j] == grid[i-1][j-1]) {
				return DiffInfo.TYPE_NO_CHANGE;
			} else {
				return DiffInfo.TYPE_UPDATE;
			}
		} else if (grid[i][j] == grid[i-1][j] + getDeleteCost()) {
			return DiffInfo.TYPE_DELETE;			
		} else if (grid[i][j] == grid[i][j-1] + getInsertCost()) {
			return DiffInfo.TYPE_INSERT;
		}
		return 0;
	}
	
	public static List<DiffInfo> run(List<String> f1, List<String> f2) {
		RunnerForLines r = new RunnerForLines(f1, f2);
		r.run();
		//r.printGrid();
		return r.res;
	}

}