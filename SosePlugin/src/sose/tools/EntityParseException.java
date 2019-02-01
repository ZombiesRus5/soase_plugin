package sose.tools;

public class EntityParseException extends RuntimeException {
	private int lineNumber = 0;
	private String lineContents = null;
	private String problemType = "Entity";
	
	public EntityParseException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EntityParseException(String problemType, String message, int lineNumber, String lineContents, Throwable cause) {
		super(message, cause);
		this.lineContents = lineContents;
		this.lineNumber = lineNumber;
	}

	public EntityParseException(String problemType, String message, int lineNumber, String lineContents) {
		super(message);
		this.problemType = problemType;
		this.lineContents = lineContents;
		this.lineNumber = lineNumber;
	}
	
	public EntityParseException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public EntityParseException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public EntityParseException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getLineContents() {
		return lineContents;
	}

	public void setLineContents(String lineContents) {
		this.lineContents = lineContents;
	}

	public String getProblemType() {
		return problemType;
	}

	public void setProblemType(String problemType) {
		this.problemType = problemType;
	}

}
