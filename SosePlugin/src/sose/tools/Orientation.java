package sose.tools;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class Orientation {
	public static void main(String[] args) throws Exception {
		toDegrees(front);
		toDegrees(back);
		toDegrees(left);
		toDegrees(right);
		
		toDegrees(new Orientation(
				"[ 1.0 0.000000 0.0 ]\n[ 0.000000 0.000000 -1.000000 ]\n[ -0.0 -0.000000 1.0 ]\n"
				));
		toDegrees(new Orientation(
				//"[ 0.707107 0.000000 0.707107 ]\n[ 0.000000 1.000000 -0.000000 ]\n[ -0.707107 -0.000000 0.707107 ]\n"
				"[ 0.707107 0.000000 -0.707107 ]\n[ -0.269835 0.924326 -0.269835 ]\n[ 0.653597 0.381604 0.653597 ]"
				));
		toDegrees(new Orientation(
				"[ 1.000000 0.000000 -0.000000 ]\n			 [ 0.000000 0.707107 0.707107 ]\n			 [ -0.000000 -0.707107 0.707107 ]"
				));
		toDegrees(new Orientation(
				"[ -0.000000 0.000000 -1.000000 ]\n			 [ 0.707107 0.707107 -0.000000 ]\n			 [ 0.707107 -0.707107 -0.000000 ]"
				));

	// Front Right
	toDegrees(new Orientation(
			 "[ 0.707107 0.000000 -0.707107 ]\n[ 0.000000 1.000000 -0.000000 ]\n[ 0.707107 -0.000000 0.707107 ]"
			));
	
//		DataString "FrontLeft"
	toDegrees(new Orientation(
			 "[ 0.707107 0.000000 0.707107 ]\n[ 0.000000 1.000000 -0.000000 ]\n[ -0.707107 -0.000000 0.707107 ]"
			));

//		DataString "BackLeft"
	toDegrees(new Orientation(
			 "[ -0.707107 0.000000 0.707107 ]\n[ 0.000000 1.000000 -0.000000 ]\n[ -0.707107 -0.000000 -0.707107 ]"
			));

//		DataString "BackRight"
	toDegrees(new Orientation(
			"[ -0.707107 0.000000 -0.707107 ]\n			 [ 0.000000 1.000000 -0.000000 ]\n			 [ 0.707107 -0.000000 -0.707107 ]"
			));

	}

	private static void toDegrees(Orientation o) {
		o.axisAngle = o.toAxisAngle(o.matrix);
		System.out.println(o.toAxisAngle(o.matrix));
		System.out.println(o);
		System.out.println(Math.toDegrees(o.heading));
	}
//	Coordinate x;
//	Coordinate y;
//	Coordinate z;
	double[][] matrix = new double[3][3];
	private static double delta = 0.000100;
	private double heading;
	private double attitude;
	private double bank;
	private AxisAngle axisAngle;
	
	private double headingDegree;
	private double attitudeDegree;
	private double bankDegree;
	
	public class AxisAngle {
		public AxisAngle(double angle, double x, double y, double z) {
			super();
			this.angle = angle;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		private double angle;
		private double x;
		private double y;
		private double z;
		public double getAngle() {
			return angle;
		}
		public void setAngle(double angle) {
			this.angle = angle;
		}
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
		@Override
		public String toString() {
			return "AxisAngle [angle=" + angle + ", x=" + x + ", y=" + y + ", z=" + z + "]";
		}
		
	}
	public static Orientation front = new Orientation(
			"[ 1.000000 0.000000 0.000000 ]\n" +
			"[ 0.000000 1.000000 0.000000 ]\n" +
			"[ 0.000000 0.000000 1.000000 ]\n");
	public static Orientation right = new Orientation(
			"[ 0.000000 0.000000 -1.000000 ]\n" +
			"[ 0.000000 1.000000 0.000000 ]\n" +
			"[ 1.000000 0.000000 0.000000 ]\n");
	public static Orientation left = new Orientation(
			"[ 0.000000 0.000000 1.000000 ]\n" +
			"[ 0.000000 1.000000 0.000000 ]\n" +
			"[ -1.000000 0.000000 0.000000 ]\n");
	public static Orientation back = new Orientation(
			"[ -1.000000 0.000000 0.000000 ]\n" +
			"[ 0.000000 1.000000 0.000000 ]\n" +
			"[ 0.000000 -0.000000 -1.000000 ]\n");
	
	Orientation() {
		
	}
	
	public Orientation(String value) {
		try {
		LineNumberReader lnr = new LineNumberReader(new StringReader(value));
		
		String line = lnr.readLine();
		StringTokenizer stk = new StringTokenizer(line.trim(), "[], ", false);
		matrix[0][0] = Double.parseDouble(stk.nextToken().trim());
		matrix[0][1] = Double.parseDouble(stk.nextToken().trim());
		matrix[0][2] = Double.parseDouble(stk.nextToken().trim());
		
		line = lnr.readLine();
		stk = new StringTokenizer(line.trim(), "[], ", false);
		matrix[1][0] = Double.parseDouble(stk.nextToken().trim());
		matrix[1][1] = Double.parseDouble(stk.nextToken().trim());
		matrix[1][2] = Double.parseDouble(stk.nextToken().trim());
		
		line = lnr.readLine();
		stk = new StringTokenizer(line.trim(), "[], ", false);
		matrix[2][0] = Double.parseDouble(stk.nextToken().trim());
		matrix[2][1] = Double.parseDouble(stk.nextToken().trim());
		matrix[2][2] = Double.parseDouble(stk.nextToken().trim());
		
//		if (matrix[1][1] == 0.0) {
//			throw new EntityParseException("Rotation[1][1] is zero, is this intended?");
//		}
//		
		axisAngle = toAxisAngle(matrix);
		toEuler(axisAngle);
		headingDegree = Math.toDegrees(heading);
		} catch (EntityParseException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isRotationMatrix(double[][] m) {
		double epsilon = 0.01; // margin to allow for rounding errors
		if (Math.abs(m[0][0]*m[0][1] + m[0][1]*m[1][1] + m[0][2]*m[1][2]) > epsilon) return false;
		if (Math.abs(m[0][0]*m[2][0] + m[0][1]*m[2][1] + m[0][2]*m[2][2]) > epsilon) return false;
		if (Math.abs(m[1][0]*m[2][0] + m[1][1]*m[2][1] + m[1][2]*m[2][2]) > epsilon) return false;
		if (Math.abs(m[0][0]*m[0][0] + m[0][1]*m[0][1] + m[0][2]*m[0][2] - 1) > epsilon) return false;
		if (Math.abs(m[1][0]*m[1][0] + m[1][1]*m[1][1] + m[1][2]*m[1][2] - 1) > epsilon) return false;
		if (Math.abs(m[2][0]*m[2][0] + m[2][1]*m[2][1] + m[2][2]*m[2][2] - 1) > epsilon) return false;
		return (Math.abs(matrixDeterminant(m)-1) < epsilon);
		// det is defined here:
		// https://www.euclideanspace.com/maths/algebra/matrix/functions/determinant/threeD/
	}
	
	public AxisAngle toAxisAngle(double[][] m) {
		  double angle,x,y,z; // variables for result
			double epsilon = 0.01; // margin to allow for rounding errors
			double epsilon2 = 0.1; // margin to distinguish between 0 and 180 degrees
			// optional check that input is pure rotation, 'isRotationMatrix' is defined at:
			// https://www.euclideanspace.com/maths/algebra/matrix/orthogonal/rotation/
			assert isRotationMatrix(m) : "not valid rotation matrix" ;// for debugging
			if ((Math.abs(m[0][1]-m[1][0])< epsilon)
			  && (Math.abs(m[0][2]-m[2][0])< epsilon)
			  && (Math.abs(m[1][2]-m[2][1])< epsilon)) {
				// singularity found
				// first check for identity matrix which must have +1 for all terms
				//  in leading diagonaland zero in other terms
				if ((Math.abs(m[0][1]+m[1][0]) < epsilon2)
				  && (Math.abs(m[0][2]+m[2][0]) < epsilon2)
				  && (Math.abs(m[1][2]+m[2][1]) < epsilon2)
				  && (Math.abs(m[0][0]+m[1][1]+m[2][2]-3) < epsilon2)) {
					// this singularity is identity matrix so angle = 0
					return new AxisAngle(0,1,0,0); // zero angle, arbitrary axis
				}
				// otherwise this singularity is angle = 180
				angle = Math.PI;
				double xx = (m[0][0]+1)/2;
				double yy = (m[1][1]+1)/2;
				double zz = (m[2][2]+1)/2;
				double xy = (m[0][1]+m[1][0])/4;
				double xz = (m[0][2]+m[2][0])/4;
				double yz = (m[1][2]+m[2][1])/4;
				if ((xx > yy) && (xx > zz)) { // m[0][0] is the largest diagonal term
					if (xx< epsilon) {
						x = 0;
						y = 0.7071;
						z = 0.7071;
					} else {
						x = Math.sqrt(xx);
						y = xy/x;
						z = xz/x;
					}
				} else if (yy > zz) { // m[1][1] is the largest diagonal term
					if (yy< epsilon) {
						x = 0.7071;
						y = 0;
						z = 0.7071;
					} else {
						y = Math.sqrt(yy);
						x = xy/y;
						z = yz/y;
					}	
				} else { // m[2][2] is the largest diagonal term so base result on this
					if (zz< epsilon) {
						x = 0.7071;
						y = 0.7071;
						z = 0;
					} else {
						z = Math.sqrt(zz);
						x = xz/z;
						y = yz/z;
					}
				}
				return new AxisAngle(angle,x,y,z); // return 180 deg rotation
			}
			// as we have reached here there are no singularities so we can handle normally
			double s = Math.sqrt((m[2][1] - m[1][2])*(m[2][1] - m[1][2])
				+(m[0][2] - m[2][0])*(m[0][2] - m[2][0])
				+(m[1][0] - m[0][1])*(m[1][0] - m[0][1])); // used to normalise
			if (Math.abs(s) < 0.001) s=1; 
				// prevent divide by zero, should not happen if matrix is orthogonal and should be
				// caught by singularity test above, but I've left it in just in case
			angle = Math.acos(( m[0][0] + m[1][1] + m[2][2] - 1)/2);
			x = (m[2][1] - m[1][2])/s;
			y = (m[0][2] - m[2][0])/s;
			z = (m[1][0] - m[0][1])/s;
		   return new AxisAngle(angle,x,y,z);
		}
	
	public void toEuler(AxisAngle axisAngle) {
		double x = axisAngle.x;
		double y = axisAngle.y;
		double z = axisAngle.z;
		double angle = axisAngle.angle;
		double s=Math.sin(angle);
		double c=Math.cos(angle);
		double t=1-c;
		//  if axis is not already normalised then uncomment this
		// double magnitude = Math.sqrt(x*x + y*y + z*z);
		// if (magnitude==0) throw error;
		// x /= magnitude;
		// y /= magnitude;
		// z /= magnitude;
		if ((x*y*t + z*s) > 0.998) { // north pole singularity detected
			heading = 2*Math.atan2(x*Math.sin(angle/2),Math.cos(angle/2));
			attitude = Math.PI/2;
			bank = 0;
			return;
		}
		if ((x*y*t + z*s) < -0.998) { // south pole singularity detected
			heading = -2*Math.atan2(x*Math.sin(angle/2),Math.cos(angle/2));
			attitude = -Math.PI/2;
			bank = 0;
			return;
		}
		heading = Math.atan2(y * s- x * z * t , 1 - (y*y+ z*z ) * t);
		attitude = Math.asin(x * y * t + z * s) ;
		bank = Math.atan2(x * s - y * z * t , 1 - (x*x + z*z) * t);
	}
	
	/**
	 * Method that calculates determinant of given matrix
	 *
	 * @param matrix matrix of which we need to know determinant
	 *
	 * @return determinant of given matrix
	 */
	public static double matrixDeterminant (double[][] matrix) {
		double temporary[][];
		double result = 0;

		if (matrix.length == 1) {
			result = matrix[0][0];
			return (result);
		}

		if (matrix.length == 2) {
			result = ((matrix[0][0] * matrix[1][1]) - (matrix[0][1] * matrix[1][0]));
			return (result);
		}

		for (int i = 0; i < matrix[0].length; i++) {
			temporary = new double[matrix.length - 1][matrix[0].length - 1];

			for (int j = 1; j < matrix.length; j++) {
				for (int k = 0; k < matrix[0].length; k++) {
					if (k < i) {
						temporary[j - 1][k] = matrix[j][k];
					} else if (k > i) {
						temporary[j - 1][k - 1] = matrix[j][k];
					}
				}
			}

			result += matrix[0][i] * Math.pow (-1, (double) i) * matrixDeterminant (temporary);
		}
		return (result);
	}

	
//	public Orientation normalizeRotation(String axis) {
//		Orientation e = new Orientation();
//		if ("y".equalsIgnoreCase(axis)) {
//			double ay = -Math.asin(matrix[0][2]); // angles without front or back positioning
//			double ax = Math.asin(matrix[1][2]/Math.cos(ay)); // angles without front or back positioning
//			double az = Math.asin(matrix[0][1]/Math.cos(ax)); // angles without front or back positioning
//
//			if (matrix[2][2] == 1.0) {
//				ax = Math.atan2(-matrix[2][1], matrix[2][2]);
//			}
//			
////			System.out.println(Math.toDegrees(ax));
////			System.out.println(Math.toDegrees(ay));
////			System.out.println(Math.toDegrees(az));
////			
////			System.out.println(Math.toDegrees(Math.acos(Math.cos(ay))));
//			
//			e.matrix[0][0] = matrix[0][0]/Math.cos(az);
//			e.matrix[0][1] = 0; 
//			e.matrix[0][2] = matrix[0][2];
//			e.matrix[1][0] = 0;
//			e.matrix[1][1] = 1;
//			e.matrix[1][2] = 0;
//			e.matrix[2][0] = (matrix[2][0] - (Math.sin(ax) * Math.sin(az)))/(Math.cos(ax) * Math.cos(az));
//			e.matrix[2][1] = 0;
//			e.matrix[2][2] = matrix[2][2]/Math.cos(ax);
//			
////			System.out.println(Math.cos(90.0));;
////			System.out.println(Math.cos(ay));
//			
//			validate(matrix[0][0], Math.cos(ay) * Math.cos(az));
//			validate(matrix[0][1], Math.cos(ay) * Math.sin(az));
//			validate(matrix[0][2], -Math.sin(ay));
//			validate(matrix[1][0], Math.sin(ax) * Math.sin(ay) * Math.cos(az) - Math.cos(ax) * Math.sin(az));
//			validate(matrix[1][1], Math.sin(ax) * Math.sin(ay) * Math.sin(az) + Math.cos(ax) * Math.cos(az));
//			validate(matrix[1][2], Math.sin(ax) * Math.cos(ay));
//			validate(matrix[2][0], Math.cos(ax) * Math.sin(ay) * Math.cos(az) + Math.sin(ax) * Math.sin(az));
//			validate(matrix[2][1], Math.cos(ax) * Math.sin(ay) * Math.sin(az) - Math.sin(ax) * Math.cos(az));
//			validate(matrix[2][2], Math.cos(ax) * Math.cos(ay));
//			//System.out.println(e);
//		} else {
//			// throw new IllegalArgumentException(axis + " not supported!");
//			e = this;
//		}
//		return e;
//	}
	
//	private void validate(double d, double e) {
//		if (!(d == e || isRelativelyEqual(d, e))) {
//			throw new EntityParseException("Invalid rotation");
//		}
//	}
//
//	private static boolean isRelativelyEqual(double d1, double d2) {
//	    return Math.abs(Math.round(d1 * 1000000d) / 1000000d) == Math.abs(Math.round(d2 * 1000000d)/ 1000000d);
//	}
	
	public boolean equals(Orientation rhs) {
		double threshold = 0.05;
		
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (matrix[row][col] > 0) {
					if (matrix[row][col] != (rhs.matrix[row][col])) {
						return false;
					}
				} else
				if (matrix[row][col] < 0) {
					if (matrix[row][col] != (rhs.matrix[row][col])) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean isFront() {
		//return x.getX() >= 1.0 && y.getY() >= 1.0 && z.getZ() >= 1.0;

		return this.equals(front) || isAngleFront();
			
//		return (x.equals(1, 0, 0) && y.equals(0, 1, 0) && z.equals(0, 0, 1))
//				|| isFrontRight() || isFrontLeft();
		
//		return (x.isXFront() && z.isZFront()) ||
//				(x.isXFrontLeft() && z.isXFrontLeft()) ||
//				(x.isXFrontRight() && z.isZFrontRight());
	}
	
	public boolean isAngleFront() {
		return (headingDegree >= 0 && headingDegree < 90) || (headingDegree <= 0 && headingDegree > -90);
	}
	public boolean isAngleBack() {
		return headingDegree < -90 || headingDegree > 90;
	}
	public boolean isAngleRight() {
		return headingDegree < 0 && headingDegree > -180;
	}
	public boolean isAngleLeft() {
		return headingDegree > 0 && headingDegree < 180;
	}
	
	public boolean isBack() {
		return this.equals(back) || isAngleBack();
	}
	
	public boolean isRight() {
		
		return this.equals(right) || isAngleRight();
		
//		return (x.isZRight() && z.isXRight()) ||
//		(x.isXBackLeft() && z.isXBackLeft()) ||
//		(x.isXBackRight() && z.isZBackRight());
	}

	public boolean isLeft() {
		
		return this.equals(left) || isAngleLeft();
		
//		return (x.isZLeft() && z.isXLeft()) ||
//		(x.isXFrontLeft() && z.isXFrontLeft()) ||
//		(x.isXFrontRight() && z.isZFrontRight());
	}

	public String getOrientationNames() {
		List<String> orientationNames = new ArrayList<String>();
		if (isFront()) {
			orientationNames.add("FRONT");
		} 

		if (isBack()) {
			orientationNames.add("BACK");
		} 

		if (isRight()) {
			orientationNames.add("RIGHT");
		} 

		if (isLeft()) {
			orientationNames.add("LEFT");
		}
		return orientationNames.toString();
	}
	
	@Override
	public String toString() {
		return "Orientation [matrix=" + Arrays.toString(matrix) + ", heading=" + heading + ", attitude=" + attitude
				+ ", bank=" + bank + ", axisAngle=" + axisAngle + "]";
	}

	public AxisAngle getAxisAngle() {
		return axisAngle;
	}

	public void setAxisAngle(AxisAngle axisAngle) {
		this.axisAngle = axisAngle;
	}

	public double getHeadingDegree() {
		return headingDegree;
	}

	public void setHeadingDegree(double headingDegree) {
		this.headingDegree = headingDegree;
	}

	public double getAttitudeDegree() {
		return attitudeDegree;
	}

	public void setAttitudeDegree(double attitudeDegree) {
		this.attitudeDegree = attitudeDegree;
	}

	public double getBankDegree() {
		return bankDegree;
	}

	public void setBankDegree(double bankDegree) {
		this.bankDegree = bankDegree;
	}

}
