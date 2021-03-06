package generarNomina;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class GenerarPdf {
	public static void llegirXml(String dir) {
		try {
			JAXBContext context = JAXBContext.newInstance(Treball.class);
			Unmarshaller unmarsheller = context.createUnmarshaller();
			Treball treball = (Treball) unmarsheller.unmarshal(new File(dir));

			System.out.println("Nombre: " + treball.getNombre());

			ArrayList<Treballador> treballador = treball.getTreballadors();

			for (Treballador l : treballador) {
				int id = l.getId();
				String dni = l.getDni();
				String nom = l.getNom();
				String cognom = l.getCognom();
				String correu = l.getCorreu();
				double nomina = l.getNomina();
				System.out.println(
						"ID: " + id + ". Dni: " + dni + ". Nom: " + nom + " " + cognom + ". Correu: " + correu + ".");
				String direct = Metodes.generarCarpetaNomina(dni);
				generarPdf(id, dni, nom, cognom, correu, nomina, direct);

			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static void generarPdf(int id, String dni, String nom, String cognom, String correu, double nomina,
			String dir) {
		String[] parts = dir.split("_");
		String dirFin = parts[0];
		String numNomin = parts[1];
		int numFinn = Integer.parseInt(numNomin);
		
		String mes = Metodes.fnMes();
		int any = Metodes.fnAny();

		try {
			File pdff = new File(dirFin);
			if (!(pdff.exists())) {
				Document pdfDOC = new Document();
				PdfWriter writer;
				writer = PdfWriter.getInstance(pdfDOC, new FileOutputStream(dirFin));
				pdfDOC.open();
				pdfDOC.add(new Paragraph("___________Adramo___________"));
				pdfDOC.add(new Paragraph("  "));
				pdfDOC.add(new Paragraph("Id producte : " + id));
				pdfDOC.add(new Paragraph("DNI receptor : " + dni));
				pdfDOC.add(new Paragraph("Nom receptor : " + nom));
				pdfDOC.add(new Paragraph("Direcci? receptor : " + cognom));
				pdfDOC.add(new Paragraph("Nom de producte : " + correu));
				pdfDOC.add(new Paragraph("Nom de producte : " + nomina));
				pdfDOC.add(new Paragraph("___________Adramo___________"));

				pdfDOC.close();
				writer.close();
				
				Metodes.insertNomina(dni, any, mes, numFinn, any);
			} else {
				System.out.println("Aquesta nomina ja est? generada");
			}
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}

	}
}
