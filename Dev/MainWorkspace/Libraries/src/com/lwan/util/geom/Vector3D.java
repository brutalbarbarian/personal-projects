package com.lwan.util.geom;

public final class Vector3D {
	public final double x, y, z;
	
	public Vector3D (double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3D (Vector3D other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}
	
	public Vector3D normalise() {
		double length = length();
		if (length == 0) throw new ArithmeticException ("Length is 0");
		if (length == 1) return this;
		length = 1/length;
		return new Vector3D (x*length, y*length, z*length);
	}
	
	public Vector3D minus (Vector3D v) {
		return new Vector3D (x - v.x, y - v.y, z - v.z);
	}
	
	public Vector3D add (Vector3D v) {
		return new Vector3D (x + v.x, y + v.y, z + v.z);
	}
	
	public Vector3D scale (double scale) {
		return new Vector3D (x * scale, y * scale, z * scale);
	}
	
	public Vector3D cross (Vector3D v) {	
		Vector3D ret = new Vector3D(y * v.z - z * v.y,
									z * v.x - x * v.z,
									x * v.y - y * v.x);
		
		return ret;
	}
	
	public double dot (Vector3D v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public String toString () {
		return "(" + x + "," + y + "," + z+ ")";
	}

	
	public boolean equals (Object other) {
		if (other instanceof Vector3D) {
			Vector3D v = (Vector3D)other;
			return v.x == x && v.y == y && v.z == z;
		} else {
			return false;
		}
		
	}

	
	public double length () {
		return Math.sqrt(x*x+y*y+z*z);
	}
}
 