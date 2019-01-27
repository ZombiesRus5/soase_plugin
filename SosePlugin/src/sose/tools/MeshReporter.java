package sose.tools;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class MeshReporter extends ContentHandlerChain {
	ErrorHandler error = null;
	FileReferenceHandler fileReference = null;
	EntityParser parser = null;
	
	private String pointName=null;
	private int pointIndex=0;
	
	List<String> captureMeta = Arrays.asList(new String[] {
			"BoundingRadius",
			"MaxBoundingExtents",
			"MinBoundingExtents"
	}
	);
	
	public MeshReporter(EntityParser parser, FileReferenceHandler fileReference, ContentHandler reporter, ErrorHandler errorHandler) {
		super(reporter);
		this.error = errorHandler;
		this.fileReference = fileReference;
		this.parser = parser;
	}
	
	String currentStructure = null;
	
	@Override
	public void startStructure(String structureName, String structureType, int lineNumber) {
		currentStructure = structureName;
		super.startStructure(structureName, structureType, lineNumber);
	}

	@Override
	public void endStructure() {
		super.endStructure();
	}

	@Override
	public void endEntity() {
		double boundingRadius = Double.parseDouble(parser.getMetaData(fileReference.getFileName(), "BoundingRadius"));
		Coordinate maxExtents = new Coordinate(parser.getMetaData(fileReference.getFileName(), "MaxBoundingExtents"));
		Coordinate minExtents = new Coordinate(parser.getMetaData(fileReference.getFileName(), "MinBoundingExtents"));
		
		if ((boundingRadius + 1) < maxExtents.getMaxBounding()) {
			String message = "MaxBoundingExtents exceeds BoundingRadius";
			error.warn(new EntityParseException(message, 1, "MeshData"));
		}
		if ((boundingRadius + 1) < minExtents.getMaxBounding()) {
			String message = "MinBoundingExtents exceeds BoundingRadius";
			error.warn(new EntityParseException(message, 1, "MeshData"));
		}
		pointName = null;
		super.endEntity();
	}


	@Override
	public void processField(String fieldName, String fieldValue,
			String fieldType, int lineNumber) {
		if (captureMeta.contains(fieldName)) {
			parser.setMetaData(fileReference.getFileName(), fieldName, fieldValue);
		} else
		if (fieldName.equals("DataString")) {
			pointName = fieldValue;
			parser.setMetaData(fileReference.getFileName(), fieldName + "." + fieldValue, "TRUE");
		} else
		if (fieldName.equals("Position")) {
			parser.setMetaData(fileReference.getFileName(), pointName + "[" + pointIndex + "]." + fieldName, fieldValue);
		} else if (fieldName.equals("Orientation")) {
			parser.setMetaData(fileReference.getFileName(), pointName + "[" + pointIndex + "]." + fieldName, fieldValue);
			Orientation o = new Orientation(fieldValue);
			if (o.isFront()) {
				parser.setMetaData(fileReference.getFileName(), pointName + ".FRONT", "TRUE");
			} 
			if (o.isBack()) {
				parser.setMetaData(fileReference.getFileName(), pointName + ".BACK", "TRUE");
			} 
			if (o.isRight()) {
				parser.setMetaData(fileReference.getFileName(), pointName + ".RIGHT", "TRUE");
			} 
			if (o.isLeft()) {
				parser.setMetaData(fileReference.getFileName(), pointName + ".LEFT", "TRUE");
			}
			pointIndex++;
		}
		super.processField(fieldName, fieldValue, fieldType, lineNumber);
	}

	public ErrorHandler getErrorHandler() {
		return error;
	}

	public void setErrorHandler(ErrorHandler error) {
		this.error = error;
	}	
}
