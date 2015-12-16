package com.afstd.geom;

/**
 * Simple class for storing 2D coordinates, mimicing the java.awt.Point2D class.
 * @author dlegland
 *
 */
public class Point2D {

	double x;
	double y;
	
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point2D() {
		this(0, 0);
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
}
