package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import com.unla.TPGrupal002Hito2Grupo07.entities.Festival;
import com.unla.TPGrupal002Hito2Grupo07.entities.Persona;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorService {

    public byte[] generarReciboSueldoPdf(Persona persona, Festival festival, double sueldoTotal){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try{
            PdfWriter.getInstance(document, out);
            document.open();

            //Encabezado
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Paragraph titulo = new Paragraph("Epicentro Gourmet - Recibo de Sueldo", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph(" "));

            //Datos empleado y evento
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            if(persona != null){
                document.add(new Paragraph("Empleado: " + persona.getNombre() + " " + persona.getApellido()));
                document.add(new Paragraph("DNI: " + persona.getDni()));
                document.add(new Paragraph("Fecha de Ingreso: " + persona.getFechaIngreso()));
            }
            if(festival != null){
                document.add(new Paragraph("Festival: " + festival.getNombre() + " (" + festival.getTemporada() + ")"));
            }
            document.add(new Paragraph(" "));

            //Tabla
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            PdfPCell c1 = new PdfPCell(new Phrase("Concepto", fontBold));
            c1.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(c1);

            PdfPCell c2 = new PdfPCell(new Phrase("Monto Liquido", fontBold));
            c2.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(c2);

            table.addCell("Liquidación por participación en evento");
            table.addCell("$ " + String.format("%.2f", sueldoTotal));

            document.add(table);
            document.add(new Paragraph(" "));

            //total
            Paragraph total = new Paragraph("TOTAL NETO A COBRAR: $ " + String.format("%.2f", sueldoTotal), fontBold);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();
        } catch(Exception e){
            e.printStackTrace();
        }
        return out.toByteArray();
    }
}
