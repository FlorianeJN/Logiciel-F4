package com.f4.logicielf4.Utilitaire;

import com.f4.logicielf4.Models.Facture;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class IOUtils {

    public static void commencerSauvegarde(Facture facture) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF");
        fileChooser.setSelectedFile(new File("Facture_" + facture.getNumFacture() + ".pdf"));
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            // Ensure the file ends with .pdf extension
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }

            // Create the PDF
            try {
                createPDF(filePath, facture);
                Dialogs.showMessageDialog("Facture exportée avec succès", "SUCCÈS EXPORTATION");
                System.out.println("PDF created successfully at: " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createPDF(String filePath, Facture facture) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Load the custom font
        PDType0Font customFont = PDType0Font.load(document, new File("src/main/resources/Fonts/Helvetica.ttf"));

        writeHeader(contentStream,facture,customFont);

        writeContent(page,contentStream,facture,customFont);

        writeFooter();

        // Footer section: Totals
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 200); // Move towards the bottom
        contentStream.showText("Montant total HT: $" + formatBigDecimal(facture.getMontantAvantTaxes()));
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Montant TPS (5%): $" + formatBigDecimal(facture.getTps()));
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Montant TVQ (9,975%): $" + formatBigDecimal(facture.getTvq()));
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Montant TTC: $" + formatBigDecimal(facture.getMontantApresTaxes()));
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("TREIZE MILLE DEUX CENT SOIXANTE SIX DOLLARS CANADIENS ET DOUZE CENTIMES");
        contentStream.endText();

        // Add footer image (optional)
        // Uncomment and adjust the path and position
        // PDImageXImage footerImage = PDImageXImage.load(document, new File("src/main/resources/Images/footer.png"));
        // contentStream.drawImage(footerImage, 50, 50, footerImage.getWidth() / 2, footerImage.getHeight() / 2); // Adjust size and position

        contentStream.close();
        document.save(filePath);
        document.close();
    }

    private static void writeHeader(PDPageContentStream contentStream,Facture facture, PDType0Font customFont){

        try{
            // Header section
            contentStream.beginText();
            contentStream.setFont(customFont, 16);
            contentStream.newLineAtOffset(50, 750); // Adjust position
            contentStream.showText("Facture N° " + facture.getNumFacture());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Date: " + facture.getDateFacture().toString());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText(facture.getNomPartenaire());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Adresse de l'entreprise"); // Replace with actual address
            contentStream.endText();


            // Add company logo (optional)
            // Uncomment and adjust the path and position
            // PDImageXImage logo = PDImageXImage.load(document, new File("src/main/resources/Images/logo.png"));
            // contentStream.drawImage(logo, 50, 730, logo.getWidth() / 2, logo.getHeight() / 2); // Adjust size and position


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static void writeContent(PDPage page,PDPageContentStream contentStream,Facture facture, PDType0Font customFont ){
        // Draw table structure for the invoice items
        float margin = 50;
        float yStart = 680;
        float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
        float yPosition = yStart;

        try{
            contentStream.setFont(customFont, 12);
            drawTableHeaders(contentStream, margin, yPosition, tableWidth, customFont);
            yPosition -= 20; // Adjust position after headers

            // Draw the rows (replace with actual data)
            // for (Quart quart : facture.getQuarts()) {
            //     drawTableRow(contentStream, yPosition, quart, columnWidths, customFont);
            //     yPosition -= 20; // Move down for the next row
            // }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void writeFooter(PDPageContentStream contentStream,Facture facture, PDType0Font customFont){
        try{
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 200); // Move towards the bottom
            contentStream.showText("Montant total HT: $" + formatBigDecimal(facture.getMontantAvantTaxes()));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Montant TPS (5%): $" + formatBigDecimal(facture.getTps()));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Montant TVQ (9,975%): $" + formatBigDecimal(facture.getTvq()));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Montant TTC: $" + formatBigDecimal(facture.getMontantApresTaxes()));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("TREIZE MILLE DEUX CENT SOIXANTE SIX DOLLARS CANADIENS ET DOUZE CENTIMES");
            contentStream.endText();

            // Add footer image (optional)
            // Uncomment and adjust the path and position
            // PDImageXImage footerImage = PDImageXImage.load(document, new File("src/main/resources/Images/footer.png"));
            // contentStream.drawImage(footerImage, 50, 50, footerImage.getWidth() / 2, footerImage.getHeight() / 2); // Adjust size and position
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    // Helper method to draw table headers with custom font
    private static void drawTableHeaders(PDPageContentStream contentStream, float margin, float y, float tableWidth, PDType0Font customFont) throws IOException {
        float[] columnWidths = {100, 120, 100, 80, 80, 80, 100}; // Adjust as per requirement

        String[] headers = {"Date", "Prestation", "Lieu", "Début du quart", "Fin du quart", "Pause", "Montant HT"};
        contentStream.beginText();
        contentStream.setFont(customFont, 12); // Use custom font

        float textY = y;
        float nextX = margin;

        for (int i = 0; i < headers.length; i++) {
            contentStream.newLineAtOffset(nextX, textY);
            contentStream.showText(headers[i]);
            nextX += columnWidths[i];
        }
        contentStream.endText();
    }

    private static String formatBigDecimal(java.math.BigDecimal value) {
        return String.format("%.2f", value.doubleValue());
    }
}
