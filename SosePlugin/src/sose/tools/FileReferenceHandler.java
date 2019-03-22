package sose.tools;

public interface FileReferenceHandler {
	public String getFileName();
	
	public String getFileExtension();
	
	public void setProperty(String property, String value);
	
	public String getProperty(String property);
}
