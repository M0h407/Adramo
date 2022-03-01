package generarNomina;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class Main extends Thread {

	public static void main(String[] args) throws JAXBException, IOException, InterruptedException {
		Scanner teclat = new Scanner(System.in);
		System.out.println("__________________________________________");
		System.out.println("");
		System.out.println("--------Generar nomina treballador--------");
		System.out.println("__________________________________________");
		System.out.println("");
		System.out.println("Selecciona una opció.");
		System.out.println("");
		System.out.println("1.- Introdueix un DNI: ");
		String denei = teclat.next();
		// GenNomin.generarCarpetaXml(denei);
		// GenNomin.validarDni(denei);
		// validarTreballador(denei);
		// Metodes.obtenirUltimArxiu(denei);
		// Metodes.splitNom(denei);
		// getTreballador(denei);
		GenerarPdf.llegirXml(denei);
	}

	public static void getTreballador(String dni) throws JAXBException, IOException {
		boolean valida = Metodes.validarDni(dni);
		if (valida == true) {
			String xmlDir = Metodes.dniXml(dni);
			if (xmlDir != null) {
				Connection con = Connect.connectDatabase();
				String consulta = "SELECT * FROM treballador WHERE dni = '" + dni + "'";
				Statement stmt;
				ResultSet rs;
				try {
					stmt = con.createStatement();
					rs = stmt.executeQuery(consulta);
					while (rs.next()) {
						if (xmlDir != null) {
							Integer id = rs.getInt("id_treballador");
							System.out.println(id);
							String Dni = rs.getString("dni");
							System.out.println(Dni);
							String Nom = rs.getString("nom");
							System.out.println(Nom);
							String cognom = rs.getString("cognom");
							System.out.println(cognom);
							String Correu = rs.getString("correu");
							System.out.println(Correu);

							Double dist = Metodes.calcularNomina(Dni, id);

							JAXBContext context = JAXBContext.newInstance(Treball.class);
							Marshaller marshaller = context.createMarshaller();

							Treball treball = new Treball();
							treball.setNombre("Treballador");

							ArrayList<Treballador> treballadors = new ArrayList<Treballador>();

							Treballador treballador = new Treballador();
							treballador.setId(id);
							treballador.setDni(Dni);
							treballador.setNom(Nom);
							treballador.setCognom(cognom);
							treballador.setCorreu(Correu);
							treballador.setNomina(dist);
							treballadors.add(treballador);
							treball.setTreballadors(treballadors);

							// marshaller.marshal(treball, System.out);

							marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
							marshaller.marshal(treball, new FileWriter(xmlDir));
							
							GenerarPdf.llegirXml(xmlDir);

						} else {
							System.out.println("Aquesta nomina ja està generada");
						}
					}
					con.close();
					stmt.close();
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Aquesta nomina ja està generada");
			}

		} else {
			System.out.println("Aquest treballador no existeix");
		}

	}

}
