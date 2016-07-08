package table.property;

import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.NONE)
public class ZoomProperties {

	@XmlElement(name = "table", required = true)
	private String table;
	@XmlElement(name = "className", required = true)
	private String className;
	@XmlElementWrapper(name = "zoomElements", required = true)
	@XmlElement(name = "zoomElement", required = true)
	private Vector<ZoomElementProperties> zoomElements = new Vector<ZoomElementProperties>();
	@XmlElementWrapper(name = "lookUpElements", required = true)
	@XmlElement(name = "lookUpElement", required = false)
	private Vector<LookUpElementProperties> lookUpElements = new Vector<LookUpElementProperties>();
	
	public ZoomProperties() {
		
	}

	public ZoomProperties(String table, String className,
			Vector<ZoomElementProperties> zoomElements,
			Vector<LookUpElementProperties> lookUpElements) {
		super();
		this.table = table;
		this.className = className;
		this.zoomElements = zoomElements;
		this.lookUpElements = lookUpElements;
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

	public Vector<ZoomElementProperties> getZoomElements() {
		return zoomElements;
	}

	public void setZoomElements(Vector<ZoomElementProperties> zoomElements) {
		this.zoomElements = zoomElements;
	}

	public Vector<LookUpElementProperties> getLookUpElements() {
		return lookUpElements;
	}

	public void setLookUpElements(Vector<LookUpElementProperties> lookUpElements) {
		this.lookUpElements = lookUpElements;
	}
	
	
	
}
