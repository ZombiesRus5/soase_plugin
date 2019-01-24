package sose.tools;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;

public class StringInfo {
	Map<String, String> stringInfos = new HashMap<String, String>();
	
	public static void main(String[] args) throws Exception {
		EntityParser parser = new EntityParser();
		
		parser.setDebug(false);
		parser.setWarn(false);
		
		parser.setIgnoreCaseOnFiles(true);
		parser.setDebug(true);
		parser.setup();
		
		parser.setStrictValidation("Rebellion");

		InputStream stringInfoStream = new FileInputStream("C:/Users/LaffoonGame/AppData/Local/Ironclad Games/Sins of a Solar Empire/Mods-Diplomacy v1.34/Test/String/English.str");
		StringInfo stringInfo = new StringInfo(parser, stringInfoStream);

	}
	
	public class StringInfoVisitor extends DefaultHandler {
		Map<String, String> map = null;
		String name = null;
		String description = null;
		
		public StringInfoVisitor(Map<String, String> stringInfo) {
			map = stringInfo;
			
		}
		@Override
		public void endEntity() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void endStructure() {
			map.put(name, description);
		}

		@Override
		public void processField(String fieldName, String fieldValue,
				String fieldType, int lineNumber) {
			if ("ID".equals(fieldName)) {
				name = fieldValue;
			} else {
				description = fieldValue;
			}
		}

		@Override
		public void startEntity(String entityType, int lineNumber) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void startStructure(String structureName, String structureType, int lineNumber) {
			name = null;
			description = null;
		}
		
	}
	
	public StringInfo(EntityParser p, InputStream stringInfoStream) {
		StringInfoVisitor visitor = new StringInfoVisitor(stringInfos);
		p.setContentHandler(visitor);
		p.validate(new LineNumberReader(new InputStreamReader(stringInfoStream)), "Str");
	}
	
	public String getValue(String id) {
		return stringInfos.get(id);
	}
}
