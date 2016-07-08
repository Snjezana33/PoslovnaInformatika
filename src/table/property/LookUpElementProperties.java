package table.property;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Prikazuje LookUp elemente za tabele . </br>
 * VAZNO: Da bi oznacili element za lookUp, u PDM-u, u koloni stranog kljuca, u komentarima napisite ovakvu sintaksu:
 * </br> +LOOKUP+IME_KOLONE_IZ_REFERENCIRANE_TABELE#NAZIV_HEADER_KOLONE_U_TABELI_ZA_PRIKAZ
 * @author Kosan
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class LookUpElementProperties {

	@XmlElement(name = "name", required = true)
	private String name;
	@XmlElement(name = "label", required = true)
	private String label;
	@XmlElement(name = "type", required = true)
	private String type;
	
	public LookUpElementProperties() {
		
	}

	public LookUpElementProperties(String name, String label, String type) {
		this.name = name;
		this.label = label;
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

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
