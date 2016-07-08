package table.property;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class NextElementProperties{
	
	@XmlElement(name = "from", required = true)
	private String from;
	@XmlElement(name = "to", required = true)
	private String to;
	@XmlElement(name = "type", required = false)
	private String type;
	
	public NextElementProperties(){
		
	}
	
	public NextElementProperties(String from, String to, String type) {
		this.from = from;
		this.to = to;
		this.type = type;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
	
}