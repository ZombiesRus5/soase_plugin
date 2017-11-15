package sose.tools;

public interface ErrorHandler {
	public void error(EntityParseException e);
	public void warn(EntityParseException e);
	public void fatal(EntityParseException e);
}
