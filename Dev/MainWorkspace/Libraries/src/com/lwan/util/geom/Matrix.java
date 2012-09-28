package com.lwan.util.geom;

public class Matrix {
	private double [] grid;
	private int width, height;
	
	/**
	 * Construct matrix with width and height
	 * Each inner section is a column.
	 * 
	 * @param mat
	 * @param width
	 * @param height
	 */
	public Matrix (double [] mat, int width, int height) {
		if (mat.length != width*height) {
			throw new IllegalArgumentException ();
		}
		grid = new double [mat.length];
		System.arraycopy(mat, 0, grid, 0, mat.length);
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Construct matrix
	 * Outter array signifies width,
	 * each inner array a column
	 * 
	 * @param mat
	 */
	public Matrix (double [][] mat) {
		width = mat.length;
		height = mat[0].length;
		grid = new double[width*height];
		for (int i = 0; i < width; i++) {
			System.arraycopy(mat[i], 0, grid, i*height, height);
		}
	}
	
	/**
	 * get value in matrix 'i' across, 'j' down 
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	public double get (int i, int j) {
		if (i < 0 || i >= width || j < 0 || j >= height) {
			throw new IndexOutOfBoundsException ();
		}
		return grid [i*height + j];
	}
	
	public void set (int i, int j, double value) {
		if (i < 0 || i >= width || j < 0 || j >= height) {
			throw new IndexOutOfBoundsException ();
		}
		grid [i*height + j] = value;
	}
	
	public void set (Matrix mat) {
		grid = new double [mat.grid.length];
		width = mat.width;
		height = mat.height;
		System.arraycopy(mat.grid, 0, grid, 0, width*height);
	}
	
	/**
	 * Construct a vector out of a single column of the matrix
	 * Will only work if the matrix is of height 2, 3 or 4
	 * 
	 * @param col
	 * @return
	 */
	public Vector3D toVector (int col) {
		if (col < 0 || col >= width) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
		if (height == 3) {
			return new Vector3D(get(col, 0), get(col, 1), get(col, 2));
		} else {
			throw new IllegalStateException();
		}
	}
}