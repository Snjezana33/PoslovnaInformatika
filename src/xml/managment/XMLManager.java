package xml.managment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.activity.InvalidActivityException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import table.property.TableProperties;

public class XMLManager {
	
	public static final String XML_LOCATION = "./xml/";
	public static final String EXTENSION = ".xml";
	
	public static boolean testXMLContent(){
		File file = new File(XML_LOCATION);
		File[] contents = file.listFiles();
		
		if (contents.length == 0) {
			return true;
		}
		
		return false;
	}
	
	public static void generateXMLs(Collection<TableProperties> tablePropertiesCollection){
		for(TableProperties tableProperties : tablePropertiesCollection)
			try {
				 File file = new File(XML_LOCATION + tableProperties.getTableName().toLowerCase() + EXTENSION);
				 JAXBContext jaxbContext = JAXBContext.newInstance(TableProperties.class);
				 Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		 
				 // output pretty printed
				 jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		 
				 jaxbMarshaller.marshal(tableProperties, file);
		 
			 } catch (JAXBException e) {
				  e.printStackTrace();
			 }
		
	}
	
	public static TableProperties getTablePropertiesFromXML(File xmlFile){
		TableProperties tableProperties = null;
		try {
			 //File file = new File(xmlFilePath);
			 JAXBContext jaxbContext = JAXBContext.newInstance(TableProperties.class);
			 Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			 tableProperties = (TableProperties) jaxbUnmarshaller.unmarshal(xmlFile);
			 
		 } catch (JAXBException e) {
			  e.printStackTrace();
		 }
		return tableProperties;
	}
	
	public static Map<String, TableProperties> getAllTablePropertiesFromXMLs(){
		try{
			Map<String, TableProperties> retVal = new HashMap<String, TableProperties>();
			File xmlDirectory = new File(XML_LOCATION);
			if(xmlDirectory.isDirectory()){
				File[] xmlFiles = xmlDirectory.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".xml");
					}
				});
				for(File xmlFile : xmlFiles){
					TableProperties tableProperty = getTablePropertiesFromXML(xmlFile);
					retVal.put(tableProperty.getTableName(), tableProperty);
				}
				return retVal;
			}
			else
				throw new InvalidActivityException(XML_LOCATION + " is not a directory");
		}catch(InvalidActivityException e){
			e.printStackTrace();
		}
		return null;
	}

}
