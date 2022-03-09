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

	public static Double calcularNomina(String dni, int id) {
		Double a = 0.0;
		String nom = obtenirUltimArxiu(dni);
		int diaNomin = splitNom(nom);
		int diaAct = diaActual();

		if (diaNomin != 1) {
			Connection con = Connect.connectDatabase();
			String consulta = "SELECT distancia, data_envio, intents FROM envios WHERE id_treballador = " + id;
			Statement stmt;
			ResultSet rs;
			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery(consulta);
				while (rs.next()) {

					if (diaAct != 1) {
						String data = rs.getString("data_envio");
						String[] spliDat = data.split("-");
						String datAnter = spliDat[2];
						int datAnr = Integer.parseInt(datAnter);
						if (diaNomin < diaAct && datAnr > diaNomin) {
							double i = rs.getDouble("distancia");
							int intens = rs.getInt("intents");
							Double ifin = i * intens;

							a = a + ifin;
						}
					}
				}
				con.close();
				stmt.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Double salari = calculNomina(a);
		return salari;
	}

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
			String[] partFin = si.split("\\.");
			String numDia = partFin[0];
			intNumDia = Integer.parseInt(numDia);
		} else {
			System.out.println("No hi ha arxiu");
		}
		System.out.println(intNumDia);
		return intNumDia;
	}

	public static void insertNomina(String dni, int any, String mes, int num_nomina, int salari) {
		Connection con = Connect.connectDatabase();
		String sql = "INSERT INTO nomina (dni,year,mes,num_nomina,salari) " + "VALUES ('" + dni + "', " + any + ", '"
				+ mes + "', " + num_nomina + ", " + salari + ");";
		Statement stmt;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
			con.close();
			stmt.close();
			System.out.println(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	public static String generarCarpetaNomina(String Dni) {
		anyMes();
		String dir = "\\\\192.168.1.69\\AdramoNomines\\Nomines\\";
		String dirFinal = "\\\\192.168.1.69\\AdramoNomines\\Nomines\\" + Dni + "\\" + strAny + "\\" + mesFin + "\\";
		String drFinn = null;
		System.out.println(mesFin);

		File dirFin = new File(dirFinal);
		if (!(dirFin.exists())) {
			String dirDni = dir + Dni + "\\";
			File filDNI = new File(dirDni);
			filDNI.mkdir();

			String dirAny = dirDni + strAny + "\\";
			File filAny = new File(dirAny);
			filAny.mkdir();

			String dirMes = dirAny + mesFin + "\\";
			File filMes = new File(dirMes);
			filMes.mkdir();

			System.out.println("pero nodsnodnawdw");
			drFinn = dirFinal + Dni + "-" + mesFin + "-" + strAny + "-" + 1 + ".pdf" + "_" + 1;
		} else {
			File[] llista = dirFin.listFiles();
			int numArch = llista.length + 1;
			drFinn = dirFinal + Dni + "-" + mesFin + "-" + strAny + "-" + numArch + ".pdf" + "?" + numArch;
		}

		return drFinn;
	}

	public static String obtenirUltimArxiu(String dni) {
		String ultArxiu = null;

		File directory = new File("\\\\192.168.1.69\\AdramoNomines\\xml\\" + dni);
		File[] files = directory.listFiles();
		if (files.length != 0) {
			Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

			File lastFile = files[0];

			ultArxiu = lastFile.getName();
		}
		return ultArxiu;
	}

	public static int diaActual() {
		LocalDate todaysDate = LocalDate.now();
		String diaAvui = todaysDate.toString();
		String[] split = diaAvui.split("-");
		String fin = split[2];
		int numFin = Integer.parseInt(fin);
		return numFin;
	}

	// Metode calcular nomina
	public static Double calculNomina(double km) {
		double mins = 1.5;
		double minsFet = km * mins;
		double eurs = minsFet * 0.15;
		return eurs;
	}

	public static String fnMes() {
		Date dt = new Date();
		int mmes = dt.getMonth();
		String fnMes = calcularMes(mmes);
		return fnMes;
	}

	public static int fnAny() {
		Date dt = new Date();
		int anio = dt.getYear();
		int finYear = 1900 + anio;
		return finYear;
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