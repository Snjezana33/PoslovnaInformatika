package table.property;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ColumnProperties {

	@XmlElement(name = "columnName", required = true)
	private String name;
	@XmlElement(name = "columnLabel", required = true)
	private String label;
	@XmlElement(name = "isPK", required = true)
	private Boolean primaryKey;
	@XmlElement(name = "columnType", required = true)
	private String type;
	
	public ColumnProperties(){
		
	}

	public ColumnProperties(String name, String label,
			Boolean primaryKey, String type) {
		this.name = name;
		this.label = label;
		this.primaryKey = primaryKey;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String columnLabel) {
		this.label = columnLabel;
	}

	public Boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	

	
	
}
