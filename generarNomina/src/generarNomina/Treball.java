package generarNomina;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="treball")
@XmlType(propOrder = { "nombre", "treballadors" })
public class Treball {
	private String nombre;
	private ArrayList<Treballador> treballadors = new ArrayList<Treballador>();
	
	public Treball() {
	}
	
	@XmlElementWrapper(name="treballadors")
	@XmlElement(name="treballador")
	public ArrayList<Treballador> getTreballadors() {
		return treballadors;
	}

	public void setTreballadors(ArrayList<Treballador> treballadors) {
		this.treballadors = treballadors;
	}
	
	@XmlElement(name="nombre")
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
}
