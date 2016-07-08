package table.property;

import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.NONE)
public class NextProperties {

	@XmlElement(name = "table", required = true)
	private String table;
	@XmlElement(name = "className", required = true)
	private String className;
	@XmlElementWrapper(name = "elements", required = true)
	@XmlElement(name = "nextElement", required = true)
	private Vector<NextElementProperties> nextElements = new Vector<NextElementProperties>();
	
	public NextProperties(){
		
	}
	
	public NextProperties(String table, String className,
			Vector<NextElementProperties> nextElements) {
		this.table = table;
		this.className = className;
		this.nextElements = nextElements;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Vector<NextElementProperties> getNextElements() {
		return nextElements;
	}

	public void setNextElements(Vector<NextElementProperties> nextElements) {
		this.nextElements = nextElements;
	}
	
	
	
	
	
	
}
