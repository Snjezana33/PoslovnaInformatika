package xml.managment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import rs.mgifos.mosquito.IMetaLoader;
import rs.mgifos.mosquito.LoadingException;
import rs.mgifos.mosquito.impl.pdm.PDMetaLoader;
import rs.mgifos.mosquito.model.MetaColumn;
import rs.mgifos.mosquito.model.MetaModel;
import rs.mgifos.mosquito.model.MetaTable;
import table.property.ColumnProperties;
import table.property.ExcludeFromTableProperties;
import table.property.LookUpElementProperties;
import table.property.NextElementProperties;
import table.property.NextProperties;
import table.property.TableProperties;
import table.property.ZoomElementProperties;
import table.property.ZoomProperties;
import exceptions.LookUpSyntaxException;

public class TableExtractionManager {

	public static final String PDM_FILE_LOC = "./pdm/banka.pdm";
	public static final String EXCLUDE_PREFIX = "+EXCLUDE+";
	public static final String LOOKUP_PREFIX = "+LOOKUP+";
	
	public static void generateXML(){
		if(XMLManager.testXMLContent()){
			System.out.println("Ekstraktuje...");
			Collection<TableProperties> tableProperties = extractTablePropertiesFromPDM();
			System.out.println("Uspesno ekstraktovao Table Propertije");
			System.out.println("Generise...");
			XMLManager.generateXMLs(tableProperties);
			System.out.println("Uspesno izgenerisao XMLove");
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Ekstraktuje...");
		Collection<TableProperties> tableProperties = extractTablePropertiesFromPDM();
		System.out.println("Uspesno ekstraktovao Table Propertije");
		System.out.println("Generise...");
		XMLManager.generateXMLs(tableProperties);
		System.out.println("Uspesno izgenerisao XMLove");

	}
	
	public static Collection<TableProperties> extractTablePropertiesFromPDM(){
		Map<String, TableProperties> tablesMap = new HashMap<String, TableProperties>();
		try {
			IMetaLoader metaLoader = new PDMetaLoader();
			Properties properties = new Properties();
			properties.put(PDMetaLoader.FILENAME, PDM_FILE_LOC);
			MetaModel model;
			model = metaLoader.getMetaModel(properties);

			for(MetaTable table : model) {
				//tableName
			    TableProperties tableProperty = new TableProperties();
			    tableProperty.setTableName(table.getCode());
			    tableProperty.setTableLabel(table.getName());
			    tablesMap.put(tableProperty.getTableName(), tableProperty);
			    
			    
			}
			//Columns
			for(String tableName : tablesMap.keySet()){
			
				TableProperties tableProperty = tablesMap.get(tableName);
			    Collection<MetaColumn> columnsFromMeta = model.getTableByCode(tableName).cColumns();
			    Vector<ColumnProperties> columnsVector = new Vector<ColumnProperties>();
			    for(MetaColumn column : columnsFromMeta){
			    	ColumnProperties columnProperty = new ColumnProperties();
			    	columnProperty.setName(column.getCode());
			    	columnProperty.setLabel(column.getName());
			    	columnProperty.setType(column.getJClassName());
			    	columnProperty.setPrimaryKey(column.isPartOfPK());
			    	columnsVector.add(columnProperty);
			    	//Read column Comments:
			    	String columnComments = column.getComment();
			    	String[] columnCommentsByLine = columnComments.split("\\r?\\n");
			    	boolean excluded = false;
			    	for(String commentLine : columnCommentsByLine){
			    		//EXCLUDE
			    		if(excluded)
			    			break;
			    		if(commentLine.equals(EXCLUDE_PREFIX)){
			    			System.out.println("Nadjen exclude!");
			    			ExcludeFromTableProperties excludeProperty = new ExcludeFromTableProperties(column.getCode());
			    			tableProperty.getExclude().add(excludeProperty);
			    			excluded = true;
			    		}
			    	}
			    	
			    	//Next and Zoom
			    	if(column.isPartOfFK()){
			    		MetaColumn parentColumn = column.getFkColParent();
			    		TableProperties parentTableProperty = tablesMap.get(parentColumn.getParentTable());
			    		//Next
			    		NextProperties next;
			    		if(parentTableProperty.nextContainsTableName(tableProperty.getTableName())){
							next = parentTableProperty.getNextItemByTableName(tableProperty.getTableName());	
						}
						else{
							next = new NextProperties();
							next.setTable(tableProperty.getTableName());
							next.setClassName(model.getTableByCode(tableProperty.getTableName()).getComment());
							parentTableProperty.getNext().add(next);
						}
						NextElementProperties nextElement = new NextElementProperties();
						nextElement.setFrom(parentColumn.getCode());
						nextElement.setTo(column.getCode());
						next.getNextElements().add(nextElement);
						//Zoom
						ZoomProperties zoom;
						if(tableProperty.zoomContainsTableName(parentTableProperty.getTableName())){
							zoom = tableProperty.getZoomItemByTableName(parentTableProperty.getTableName());
						}
						else{
							zoom = new ZoomProperties();
							zoom.setTable(parentTableProperty.getTableName());
							zoom.setClassName(model.getTableByCode(parentTableProperty.getTableName()).getComment());
							tableProperty.getZoom().add(zoom);
						}
						ZoomElementProperties zoomElement = new ZoomElementProperties();
						zoomElement.setFrom(parentColumn.getCode());
						zoomElement.setTo(column.getCode());
						zoom.getZoomElements().add(zoomElement);
						//LookUp
						for(String commentLine : columnCommentsByLine){
				    		if(commentLine.startsWith(LOOKUP_PREFIX)){
				    			String[] lookUpArguments = commentLine.substring(LOOKUP_PREFIX.length()).split("#");
				    			if(lookUpArguments.length != 2)
				    				throw new LookUpSyntaxException("Invalid lookUp syntax: " + commentLine);
				    			LookUpElementProperties lookUpElement = new LookUpElementProperties();
				    			lookUpElement.setName(lookUpArguments[0]);
				    			lookUpElement.setLabel(lookUpArguments[1]);
				    			zoom.getLookUpElements().add(lookUpElement);
				    		}
						}
			    	}
			    	
			    	
			    }
			    tableProperty.setColumns(columnsVector);
			    
			}
			
			//Za svaku tabelu se postavlja tip LookUpElementa na onaj na koji pokazuje kolona
			//na koju se referencira. Moramo to ovde uraditi jer smo morali cekati da se tabele
			//popune kolonama.
			for(TableProperties tableProperty : tablesMap.values()){
				for(ZoomProperties zoomProperty : tableProperty.getZoom())
					for(LookUpElementProperties lookUpElement : zoomProperty.getLookUpElements())
					{
						TableProperties parentTableProperty = tablesMap.get(zoomProperty.getTable());
						ColumnProperties lookUpParentColumn = parentTableProperty.getColumnByColumnName(lookUpElement.getName());
		    			if(lookUpParentColumn == null)
		    				throw new LookUpSyntaxException("Column in lookUp does not match the column in table of Zoom: " + lookUpElement.getName() + " " + parentTableProperty.getTableName());
		    			lookUpElement.setType(lookUpParentColumn.getType());
						
					}
			}
			
			
			return tablesMap.values();
		} catch (LoadingException e) {
			e.printStackTrace();
		} catch (LookUpSyntaxException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
}
