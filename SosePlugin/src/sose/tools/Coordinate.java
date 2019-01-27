package sose.tools;

import java.util.StringTokenizer;

public class Coordinate {
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	private double x;
	private double y;
	private double z;
	
	Coordinate(String value) {
		if (!value.startsWith("[")) {
			throw new IllegalArgumentException("expected [ at beginning of value");
		} else if (!value.endsWith("]")) {
			throw new IllegalArgumentException("expected ] at end of value");
		} else {
			try {
				StringTokenizer stk = new StringTokenizer(value, "[], ", false);
				x = Double.parseDouble(stk.nextToken().trim());
				y = Double.parseDouble(stk.nextToken().trim());
				z = Double.parseDouble(stk.nextToken().trim());
				
			} catch(Exception e) {
				throw new IllegalArgumentException("Expected a numeric value");
			}
		}
	}
	
	public boolean equals(double x, double y, double z) {
		boolean result = false;
		if (x > 0) {
			result = (this.x >= (x - 0.05));
		} else if (x < 0) {
			result = (this.x <= (x + 0.05));
		}
		
		if (y > 0) {
			result = (this.y >= (y - 0.05));
		} else if (y < 0) {
			result = (this.y <= (y + 0.05));
		}

		if (z > 0) {
			result = (this.z >= (z - 0.05));
		} else if (z < 0) {
			result = (this.z <= (z + 0.05));
		}
		return result;
	}
	

	public double getMaxBounding() {
		return Math.max(Math.abs(x), Math.max(Math.abs(y), Math.abs(z)));
	}
	
	public String toString() {
		return "" + x + " " + y + " " + z;
	}
}
