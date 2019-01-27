package sose.tools;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.StringTokenizer;

public class Orientation {
	Coordinate x;
	Coordinate y;
	Coordinate z;
	
	Orientation(String value) {
		try {
		LineNumberReader lnr = new LineNumberReader(new StringReader(value));
		x = new Coordinate(lnr.readLine());
		y = new Coordinate(lnr.readLine());
		z = new Coordinate(lnr.readLine());
		} catch (Exception e) {
			// eat it
		}
	}
	
	public boolean isFront() {
		//return x.getX() >= 1.0 && y.getY() >= 1.0 && z.getZ() >= 1.0;
		
		return (x.equals(1, 0, 0) && z.equals(0, 0, 1))
				|| isFrontRight() || isFrontLeft();
		
//		return (x.isXFront() && z.isZFront()) ||
//				(x.isXFrontLeft() && z.isXFrontLeft()) ||
//				(x.isXFrontRight() && z.isZFrontRight());
	}
	
	public boolean isFrontRight() {
		return x.getZ() < 0 && z.getX() > 0;
	}
	
	public boolean isFrontLeft() {
		return x.getZ() > 0 && z.getX() < 0;
	}
	
	public boolean isBackRight() {
		return x.getZ() < 0 && z.getZ() < 0;
	}
	
	public boolean isBackLeft() {
		return x.getZ() > 0 && z.getZ() < 0;
	}
	
	public boolean isBack() {
		return (x.equals(-1, 0, 0) && z.equals(0, 0, -1)) || isBackRight() || isBackLeft();
//		return (x.isXBack() && z.isZBack()) ||
//				(x.isXBackLeft() && z.isXBackLeft()) ||
//				(x.isXBackRight() && z.isZBackRight());
	}
	
	public boolean isRight() {
		
		return (x.equals(0, 0, 1) && z.equals(-1, 0, 0)) || isFrontRight() || isBackRight();
		
//		return (x.isZRight() && z.isXRight()) ||
//		(x.isXBackLeft() && z.isXBackLeft()) ||
//		(x.isXBackRight() && z.isZBackRight());
	}

	public boolean isLeft() {
		
		return (x.equals(0, 0, -1) && z.equals(1, 0, 0)) || isFrontLeft() || isBackLeft();
		
//		return (x.isZLeft() && z.isXLeft()) ||
//		(x.isXFrontLeft() && z.isXFrontLeft()) ||
//		(x.isXFrontRight() && z.isZFrontRight());
	}

	public String getOrientationName() {
		if (isFront()) {
			return "FRONT";
		} else if (isBack()) {
			return "BACK";
		} else if (isRight()) {
			return "RIGHT";
		} else if (isLeft()) {
			return "LEFT";
		}
		return "";
	}
	
	public String toString() {
		String s = "[ " + x + " ]\n" + "[ " + y + " ]\n" + "[ " + z + " ]";
		return s;
	}
}
