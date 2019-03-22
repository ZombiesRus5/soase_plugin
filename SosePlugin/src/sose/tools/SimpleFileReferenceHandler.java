package sose.tools;

public class SimpleFileReferenceHandler implements FileReferenceHandler {
	public SimpleFileReferenceHandler(String fileName, String fileExtension) {
		super();
		this.fileName = fileName;
		this.fileExtension = fileExtension;
	}

	String fileName;
	String fileExtension;
	
	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return fileName;
	}

	@Override
	public String getFileExtension() {
		// TODO Auto-generated method stub
		return fileExtension;
	}

	@Override
	public void setProperty(String property, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getProperty(String property) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

}
