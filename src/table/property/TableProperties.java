package table.property;

import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tableProperties")
@XmlAccessorType(XmlAccessType.NONE)
public class TableProperties {

	@XmlElement(name = "tableName" , required = true)
	private String tableName;
	@XmlElement(name = "tableLabel" , required = true)
	private String tableLabel;
	@XmlElementWrapper(name = "columns", required = true)
	@XmlElement(name = "column", required = true)
	private Vector<ColumnProperties> columns = new Vector<ColumnProperties>();
	@XmlElementWrapper(name = "zoom", required = true)
	@XmlElement(name = "zoomItem", required = false)
	private Vector<ZoomProperties> zoom = new Vector<ZoomProperties>();
	@XmlElementWrapper(name = "next", required = true)
	@XmlElement(name = "nextItem", required = false)
	private Vector<NextProperties> next = new Vector<NextProperties>();
	@XmlElementWrapper(name = "excludeFromTable", required = true)
	@XmlElement(name = "excludeColumn", required = false)
	private Vector<ExcludeFromTableProperties> exclude = new Vector<ExcludeFromTableProperties>();

	private Vector<String> nextColumnsName = new Vector<String>();
	
	public TableProperties() {
	
	}

	public TableProperties(String tableName, String tableLabel,
			Vector<ColumnProperties> columns, Vector<ZoomProperties> zoom, 
			Vector<NextProperties> next, Vector<ExcludeFromTableProperties> exclude) {
		super();
		this.tableName = tableName;
		this.tableLabel = tableLabel;
		this.columns = columns;
		this.next = next;
		this.zoom = zoom;
		this.exclude = exclude;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableLabel() {
		return tableLabel;
	}

	public void setTableLabel(String tableLabel) {
		this.tableLabel = tableLabel;
	}

	public Vector<ColumnProperties> getColumns() {
		return columns;
	}

	public void setColumns(Vector<ColumnProperties> columns) {
		this.columns = columns;
	}

	public Vector<NextProperties> getNext() {
		return next;
	}

	public void setNext(Vector<NextProperties> next) {
		this.next = next;
	}

	public Vector<ZoomProperties> getZoom() {
		return zoom;
	}

	public void setZoom(Vector<ZoomProperties> zoom) {
		this.zoom = zoom;
	}
	
	public Vector<ExcludeFromTableProperties> getExclude() {
		return exclude;
	}

	public void setExclude(Vector<ExcludeFromTableProperties> exclude) {
		this.exclude = exclude;
	}

	/**
	 * Metoda koja proverava da li tabela sadrzi next mehanizam na osnovu naziva tabele.
	 * @param tableName
	 * @return
	 */
	public boolean nextContainsTableName(String tableName){
		for(NextProperties nextProperty : next){
			if(nextProperty.getTable().equals(tableName))
				return true;
		}
		return false;
	}
	
	/**
	 * Metoda koja proverava da li tabela sadrzi zoom mehanizam na osnovu naziva tabele.
	 * @param tableName
	 * @return
	 */
	public boolean zoomContainsTableName(String tableName){
		for(ZoomProperties zoomProperty : zoom){
			if(zoomProperty.getTable().equals(tableName))
				return true;
		}
		return false;
	}
	
	/**
	 * Metoda koja vraca next properties na osnovu naziva tabele.
	 * @param tableName
	 * @return
	 */
	public NextProperties getNextItemByTableName(String tableName){
		for(NextProperties nextProperty : next){
			if(nextProperty.getTable().equals(tableName))
				return nextProperty;
		}
		return null;
	}
	
	/**
	 * Metoda koja vraca zoom properties na osnovu naziva tabele.
	 * @param tableName
	 * @return
	 */
	public ZoomProperties getZoomItemByTableName(String tableName){
		for(ZoomProperties zoomProperty : zoom){
			if(zoomProperty.getTable().equals(tableName))
				return zoomProperty;
		}
		return null;
	}
	
	/**
	 * Metoda koja vraca kolonu na osnovu naziva kolone.
	 * @param columnName
	 * @return
	 */
	public ColumnProperties getColumnByColumnName(String columnName){
		for(ColumnProperties columnProperty : columns){
			if(columnProperty.getName().equals(columnName))
				return columnProperty;
		}
		return null;
	}
	
	/**
	 * Metoda koja vraca vektor naziva kolona za postavljanje naziva kolona u tabeli forme.
	 * @return
	 */
	public Vector<String> getColumnsLabel() {
		Vector<String> retVal = new Vector<String>();
		
		for(ColumnProperties cp : getColumns()) {
			retVal.add(cp.getLabel());
			for(ZoomProperties zp : getZoom()) {
				for(ZoomElementProperties zep : zp.getZoomElements()) {
					if(cp.getName().equals(zep.getFrom())) {
						for(LookUpElementProperties lookUp : zp.getLookUpElements())
							retVal.add(lookUp.getLabel());
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Metoda koja vraca vektor indeksa kolona koje ne treba prikazati.
	 * @return
	 */
	public Vector<Integer> getIndexesOfExcludedColumns(){
		Vector<Integer> indexes = new Vector<Integer>();
		// Izbacivanje kolone koju ne zelimo prikazati u formi
				for(ExcludeFromTableProperties e : getExclude()) {
					for(int i = 0; i < columns.size(); i++) {
						ColumnProperties cp = columns.elementAt(i);
						if(e.getColumnName().equalsIgnoreCase(cp.getName()))
							indexes.add(i);
					}
				}
		return indexes;
	}
	
	/**
	 * Metoda koja vraca vektor naziva kolona za popunjavanje tabele forme.
	 * @return
	 */
	public Vector<String> getColumnsNameForForm() {
		Vector<String> retVal = new Vector<String>();
		for(ColumnProperties cp : getColumns()) {
			retVal.add(cp.getName());
			for(ZoomProperties zp : getZoom()) {
				for(ZoomElementProperties zep : zp.getZoomElements()) {
					if(cp.getName().equals(zep.getFrom())) {
						for(LookUpElementProperties lookUp : zp.getLookUpElements())
							retVal.add(lookUp.getName());
					}
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Metoda koja vraca vektor naziva kolona primarnih kljuceva tabele.
	 * @return
	 */
	public Vector<String> getPrimaryKeysFromTable() {
		Vector<String> retVal = new Vector<String>();
		for(ColumnProperties cp : getColumns()) {
			if(cp.isPrimaryKey() == true) {
				retVal.add(cp.getName());
			}
		}
		
		return retVal;
	}
	
	/**
	 * Metoda koja vraca vektor naziva kolona koje predstavljaju primarne kljuceve u tabeli forme.
	 * @return
	 */
	public Vector<String> getPrimaryKeysLabelName() {
		Vector<String> retVal = new Vector<String>();
		for(ColumnProperties cp : getColumns()) {
			if(cp.isPrimaryKey())
				retVal.add(cp.getLabel());
		}
		
		return retVal;
	}
	
	/**
	 * Metoda koja vraca ColumnProperties na onsnovu naziva kolone.
	 * @param columnName
	 * @return
	 */
	public ColumnProperties getColumnProperties(String columnName) {
		ColumnProperties retVal = null;
		for(ColumnProperties cp : getColumns()) {
			if(cp.getName().equals(columnName)) {
				retVal = cp;
				break;
			}
		}
		return retVal;
	}
	
	/**
	 * Metoda koja vraca ColumnProperties na osnovu naziva labele kolone.
	 * @param columnLabelName
	 * @return
	 */
	public ColumnProperties getColumnPropertiesForLabelName(String columnLabelName) {
		ColumnProperties retVal = null;
		for(ColumnProperties cp : getColumns()) {
			if(cp.getLabel().equals(columnLabelName)) {
				retVal = cp;
				break;
			}
		}
		return retVal;
	}
	
	/**
	 * Metoda koja vrsi proveru zoom elemenata tako sto proverava da li postoje zoom elementi sa istim nazivom referencirajuce kolone.
	 * @param zp
	 * @return
	 */
	public int checkZoomElements(ZoomProperties zp) {
		int retVal = 0;
		
		for(int i = 0;i < zp.getZoomElements().size();i++) {
			for(int j = 0;j < zp.getZoomElements().size();j++) {
				if(zp.getZoomElements().get(i).getFrom().equals(zp.getZoomElements().get(j).getFrom()))
					retVal++;
			}
		}
		
		return retVal;
	}
	
	/**
	 * Metoda koja sluzi za proveru da li u next-u postoji vise kolona koje referenciraju istu kolonu.
	 * @param np
	 * @return
	 */
	public int checkNextElements(NextProperties np) {
		int retVal = 0;
		for(int i = 0;i < np.getNextElements().size();i++) {
			for(int j = 0;j < np.getNextElements().size();j++) {
				if(np.getNextElements().get(i).getFrom().equals(np.getNextElements().get(j).getFrom()))
					retVal++;
			}
		}
		
		return retVal;
	}
	
	/**
	 * Metoda za postavljanje naziva kolona nad kojima se radi next.
	 * @param columnsForNext
	 */
	public void setColumnsForNext(Vector<String> columnsForNext) {
		this.nextColumnsName = columnsForNext;
	}
	
	/**
	 * Metoda koja vraca nazive kolona nad kojima se radi next.
	 * @return
	 */
	public Vector<String> getColumnsForNext() {
		return nextColumnsName;
	}
	
	/**
	 * Metoda koja vraca labelu kolone na osnovu prosledjenog naziva kolone.
	 * @param columnName
	 * @return
	 */
	public String getColumnLabel(String columnName) {
		String retVal = "";
		for(ColumnProperties cp : columns) {
			if(cp.getName().equals(columnName))
				retVal = cp.getLabel();
		}
		
		return retVal;
	}
	
	/**
	 * Metoda koja vraca nazive labela kolona za next.
	 * @param tableName
	 * @return
	 */
	public Vector<String> getColumnsLabelForNext(String tableName) {
		Vector<String> retVal = new Vector<String>();
		NextProperties np = getNextItemByTableName(tableName);
		for(NextElementProperties nep : np.getNextElements()) {
			for(ColumnProperties cp : columns) {
				if(cp.getName().equals(nep.getFrom()))
					retVal.add(cp.getLabel());
			}
		}
		
		return retVal;
	}
	
	/**
	 * Vraca vektor naziva svih look-upova u ovoj tabeli.
	 * @return
	 */
	public Vector<String> getLookUpElementNames(){
		Vector<String> retVal = new Vector<String>();
		for(ZoomProperties zoomProp : zoom){
			for(LookUpElementProperties lookUpElement : zoomProp.getLookUpElements()){
				retVal.add(lookUpElement.getName());
			}
		}
		
		return retVal;
	}
	
	public boolean isNextEmpty() {
		return next.isEmpty();
	}

}
