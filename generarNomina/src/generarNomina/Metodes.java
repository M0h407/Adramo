package generarNomina;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.Semaphore;

public class Metodes {
	static Semaphore mutex = new Semaphore(1);

	public static int mes = 0;
	public static String mesFin = "";
	public static int any = 0;
	public static String strAny = "";

	// Crear nom de l'arxiu xml que es generara
	public static String dniXml(String dni) {
		String dirFin = null;
		String dir = "\\\\192.168.1.69\\AdramoNomines\\xml\\" + dni;
		File dirxml = new File(dir);
		if (dirxml.exists()) {
			dirFin = "\\\\192.168.1.69\\AdramoNomines\\xml\\" + dni + "\\" + dni + "_" + LocalDate.now() + ".xml";
			File dirxml1 = new File(dirFin);
			if (dirxml1.exists()) {
				System.out.println("Aquest xml ja està generat");
				dirFin = null;
			} else {
				dirFin = "\\\\192.168.1.69\\AdramoNomines\\xml\\" + dni + "\\" + dni + "_" + LocalDate.now() + ".xml";
			}
		} else {
			dirxml.mkdir();
			dirFin = "\\\\192.168.1.69\\AdramoNomines\\xml\\" + dni + "\\" + dni + "_" + LocalDate.now() + ".xml";
		}
		return dirFin;
	}

	public static int splitNom(String nom) {
		int intNumDia = 1;
		if (nom != null) {
			String[] parts = nom.split("_");
			String no = parts[1];
			String[] parts1 = no.split("-");
			String si = parts1[2];
			String[] partFin = si.split(".");
			String numDia = partFin[0];
			intNumDia = Integer.parseInt(numDia);
		} else {
			System.out.println("No hi ha arxiu");
		}
		return intNumDia;
	}

	public static boolean validarDni(String dni) {
		boolean valid = false;
		Connection con = Connect.connectDatabase();
		String consulta = "SELECT treballador FROM treballador WHERE dni = '" + dni + "'";
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(consulta);
			if (rs.next()) {
				System.out.println("Treballador trobat");
				generarCarpetaXml(dni);
				valid = true;
			} else {
				System.out.println("Aquest treballador no existeix");
				valid = false;
			}
			con.close();
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return valid;
	}

	public static void generarCarpetaXml(String Dni) {
		anyMes();
		String dir = "\\\\192.168.1.69\\AdramoNomines\\Nòmines\\";

		String dirDni = dir + Dni + "\\";
		File filDNI = new File(dirDni);
		filDNI.mkdir();

		String dirAny = dirDni + strAny + "\\";
		File filAny = new File(dirAny);
		filAny.mkdir();

		String dirMes = dirAny + mesFin + "\\";
		File filMes = new File(dirMes);
		filMes.mkdir();

	}

	public static Double calcularNomina(String dni, int id) {
		Double a = 0.0;
		String nom = obtenirUltimArxiu(dni);
		int cock = splitNom(nom);
		int diaFin = diaMesFin();

		Connection con = Connect.connectDatabase();
		String consulta = "SELECT distancia FROM envios WHERE id_treballador = " + id;
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(consulta);
			while (rs.next()) {
				/*
				 * if() {
				 * 
				 * }
				 */
				a = rs.getDouble("distancia");
			}
			con.close();
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return a;
	}

	public static Double calculNomina(int id) {
		Double a = 0.0;
		Connection con = Connect.connectDatabase();
		String consulta = "SELECT SUM(distancia) FROM envios WHERE id_treballador = " + id;
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(consulta);
			if (rs.next()) {
				a = rs.getDouble("SUM");
			}
			con.close();
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return a;
	}

	public static int diaMesFin() {
		Calendar cal = Calendar.getInstance();
		int res = cal.getActualMaximum(Calendar.DATE);
		return res;
	}

	public static String obtenirUltimArxiu(String dni) {

		File directory = new File("\\\\192.168.1.69\\AdramoNomines\\xml\\12345678Z" + dni);

		File[] files = directory.listFiles();

		Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

		File lastFile = files[0];

		System.out.println(lastFile.getName());

		return lastFile.getName();
	}

	// Metode calcular nomina
	public static Double calculNomina(double km) {
		double mins = 1.5;
		double kmsFet = km * mins;
		return kmsFet;
	}

	public static void getMes() {
		Date dt = new Date();
		mes = dt.getMonth();
	}

	public static void getAny() {
		Date dt = new Date();
		any = dt.getYear();
	}

	// Obtenir mes en string
	private static String calcularMes(int mes) {
		String[] mesAny = { "Gener", "Febrer", "Març", "Abril", "Maig", "Juny", "Juliol", "Agost", "Setembre",
				"Octubre", "Novembre", "Desembre" };
		String mesString = mesAny[mes];
		return mesString;
	}

	// Fil obtenir any actual
	public static class CalculAny extends Thread {
		public void run() {
			try {
				mutex.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int finYear = 1900 + any;
			strAny = finYear + "";
			mutex.release();
		}
	}

	// Fil obtenir mes actual
	public static class CalculMes extends Thread {
		public void run() {
			try {
				mutex.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mesFin = calcularMes(mes);
			mutex.release();
		}
	}

	public static void anyMes() {
		try {
			getMes();
			getAny();
			Thread f1 = new CalculAny();
			Thread f2 = new CalculMes();
			f1.start();
			f2.start();
			f1.join();
			f2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}