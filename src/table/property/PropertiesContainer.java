package table.property;

import java.util.Map;

import xml.managment.XMLManager;

public class PropertiesContainer {

	private static PropertiesContainer instance = null;
	private Map<String, TableProperties> tablesMap;
	
	public static PropertiesContainer getInstance(){
		if(instance == null)
			instance = new PropertiesContainer();
		return instance;
	}
	
	private PropertiesContainer(){
		tablesMap = XMLManager.getAllTablePropertiesFromXMLs();
		
	}

	public Map<String, TableProperties> getTablesMap() {
		return tablesMap;
	}
	
	
	
}
