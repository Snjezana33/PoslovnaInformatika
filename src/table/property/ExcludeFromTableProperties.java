package table.property;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Oznacava kolone koje ne treba da se prikazu u tabeli u formama, ali ce se
 * pojaviti njegovi textfieldovi. </br> VAZNO: Da bi kolonu oznacili kao
 * Exclude, u PDM-u morate u komentarime te kolone napisati sledecu
 * sintaksu:</br> +EXCLUDE+
 * 
 * @author Kosan
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ExcludeFromTableProperties {

	@XmlElement(name = "columnName", required = true)
	private String columnName;

	public ExcludeFromTableProperties() {

	}

	public ExcludeFromTableProperties(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

}
