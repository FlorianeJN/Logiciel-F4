package com.f4.logicielf4.IO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class PDFCreator {

    public static void main(String[] args) {
        // Open JFileChooser to select file save location
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF");

        // Set default file name
        fileChooser.setSelectedFile(new File("myDocument.pdf"));

        // Show save dialog
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
                createPDF(filePath);
                System.out.println("PDF created successfully at: " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to create a simple PDF
    public static void createPDF(String filePath) throws IOException {
        // Create a new document
        PDDocument document = new PDDocument();

        // Add a blank page
        PDPage page = new PDPage();
        document.addPage(page);

        // Load the custom font
        PDType0Font customFont = PDType0Font.load(document, new File("src/main/resources/Fonts/Helvetica.ttf"));
// Start writing content to the page
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.setFont(customFont, 12); // Use custom font
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText("Hello, PDF World!");
        contentStream.endText();
        contentStream.close();

        // Save the document
        document.save(filePath);

        // Close the document
        document.close();
    }
}