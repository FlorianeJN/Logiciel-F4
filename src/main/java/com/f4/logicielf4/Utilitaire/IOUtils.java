package com.f4.logicielf4.Utilitaire;

import com.f4.logicielf4.Models.Facture;
import com.f4.logicielf4.Models.Partenaire;
import com.f4.logicielf4.Models.Quart;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class IOUtils {

    public static void commencerSauvegarde(Facture facture) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer la facture en PDF");
        fileChooser.setSelectedFile(new File("Facture_" + facture.getNumFacture() + ".pdf"));
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            // S'assurer que le fichier se termine par l'extension .pdf
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }

            // Créer le PDF
            try {
                createPDF(filePath, facture);
                JOptionPane.showMessageDialog(null, "Facture exportée avec succès", "EXPORTATION RÉUSSIE", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("PDF créé avec succès à l'emplacement : " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Le fichier que vous souhaitez remplacer est ouvert. Veuillez le fermer.", "ERREUR D'EXPORTATION", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void createPDF(String filePath, Facture facture) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.LETTER);
        document.addPage(page);

        PDType0Font customFont = PDType0Font.load(document, new File("src/main/resources/Fonts/Helvetica.ttf"));

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        float yPosition = writeHeader(document, contentStream, facture, customFont);
        contentStream.close();

        contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
        contentStream = writeContent(document, page, contentStream, facture, customFont, yPosition);
        writeFooter(contentStream, facture, customFont);
        contentStream.close();

        document.save(filePath);
        document.close();
    }

    private static float writeHeader(PDDocument document, PDPageContentStream contentStream, Facture facture, PDType0Font customFont) {
        float yPosition = 0;
        try {
            // Variables pour les positions
            float margin = 50;
            yPosition = 750;
            float spacing = 12;

            // Logo à gauche
            PDImageXObject logo = PDImageXObject.createFromFile("src/main/resources/Images/logo.jpg", document);
            float logoWidth = 100;
            float logoHeight = 100;
            float logoX = margin;
            float logoY = yPosition - logoHeight + 30; // Ajuster si nécessaire
            contentStream.drawImage(logo, logoX, logoY, logoWidth, logoHeight);

            // Titre de la facture centré (incluant le numéro de facture)
            String titreFacture = "FACTURE N° " + facture.getNumFacture();
            contentStream.setFont(customFont, 18);
            float titreWidth = customFont.getStringWidth(titreFacture) / 1000 * 18;
            float titreX = (PDRectangle.LETTER.getWidth() - titreWidth) / 2;
            float titreY = yPosition;
            contentStream.beginText();
            contentStream.newLineAtOffset(titreX, titreY);
            contentStream.showText(titreFacture);
            contentStream.endText();

            // Informations de l'entreprise à gauche, sous le logo
            contentStream.setFont(customFont, 10);
            float infoEntrepriseX = margin;
            float infoEntrepriseY = logoY - spacing;
            contentStream.beginText();
            contentStream.newLineAtOffset(infoEntrepriseX, infoEntrepriseY);
            contentStream.showText("F4 SANTÉ INC.");
            contentStream.newLineAtOffset(0, -spacing);
            contentStream.showText("215 Rue Laure-Conan");
            contentStream.newLineAtOffset(0, -spacing);
            contentStream.showText("Varennes, Québec J3X 1W9");
            contentStream.newLineAtOffset(0, -spacing);
            contentStream.showText("Téléphone : 514-797-6357");
            contentStream.newLineAtOffset(0, -spacing);
            contentStream.showText("Email : info@f4santeinc.com");
            contentStream.endText();

            // Informations du partenaire encadrées et alignées à droite
            Partenaire partenaire = facture.getPartenaire();
            if (partenaire != null) {
                contentStream.setFont(customFont, 10);
                float partnerX = PDRectangle.LETTER.getWidth() - margin - 200; // Ajuster la valeur 200 si nécessaire
                float partnerY = logoY + spacing; // Aligné avec le logo

                // Dessiner le cadre
                float cadreWidth = 200;
                float cadreHeight = 60; // Ajuster en fonction de la hauteur souhaitée
                contentStream.setStrokingColor(Color.BLACK);
                contentStream.setLineWidth(1);
                contentStream.addRect(partnerX - 5, partnerY - cadreHeight - 10, cadreWidth, cadreHeight);
                contentStream.stroke();

                contentStream.beginText();
                contentStream.newLineAtOffset(partnerX, partnerY);
                contentStream.showText("Facturé à: ");
                contentStream.newLineAtOffset(0, -spacing);
                contentStream.newLineAtOffset(0, -spacing);
                contentStream.showText(partenaire.getNom());
                contentStream.newLineAtOffset(0, -spacing);
                contentStream.showText(partenaire.getAdresseObj().getNumeroCivique() + " " + partenaire.getAdresseObj().getRue());
                contentStream.newLineAtOffset(0, -spacing);
                contentStream.showText(partenaire.getAdresseObj().getVille()+ ","+partenaire.getAdresseObj().getProvince() + " " + partenaire.getAdresseObj().getCodePostal());
                contentStream.newLineAtOffset(0, -spacing);
                contentStream.showText("Téléphone : " + partenaire.getTelephone());
                contentStream.endText();
            }

            // Ligne séparatrice
            yPosition = infoEntrepriseY - spacing * 6; // Plus bas pour dégager le contenu
            contentStream.setStrokingColor(Color.GRAY);
            contentStream.setLineWidth(1);
            contentStream.moveTo(margin, yPosition);
            contentStream.lineTo(PDRectangle.LETTER.getWidth() - margin, yPosition);
            contentStream.stroke();

            yPosition -= spacing;

            return yPosition;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return yPosition;
    }

    private static PDPageContentStream writeContent(PDDocument document, PDPage page, PDPageContentStream contentStream, Facture facture, PDType0Font customFont, float yStart) throws IOException {
        float yPosition = yStart;
        float margin = 30;

        // Ajustement des largeurs des colonnes
        float[] columnWidths = {70, 120, 50, 50, 50, 50, 60, 70};

        drawTableHeaders(contentStream, margin, yPosition, columnWidths, customFont);
        yPosition -= 30; // Ajustement de la position du tableau après la ligne

        List<Quart> quarts = facture.getListeQuarts();
        for (Quart quart : quarts) {
            if (yPosition < 100) {
                contentStream.close();
                page = new PDPage(PDRectangle.LETTER);
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                yPosition = yStart;

                drawTableHeaders(contentStream, margin, yPosition, columnWidths, customFont);
                yPosition -= 30;
            }

            drawTableRow(contentStream, margin, yPosition, quart, columnWidths, customFont);
            yPosition -= 20;
        }

        return contentStream;
    }

    private static void writeFooter(PDPageContentStream contentStream, Facture facture, PDType0Font customFont) {
        try {
            // Ligne séparatrice
            contentStream.setStrokingColor(Color.GRAY);
            contentStream.setLineWidth(1);
            contentStream.moveTo(30, 120);
            contentStream.lineTo(580, 120);
            contentStream.stroke();

            // Détails du paiement
            contentStream.beginText();
            contentStream.setFont(customFont, 12);
            contentStream.newLineAtOffset(30, 100);
            contentStream.showText("Montant total HT : " + formatCurrency(facture.getMontantAvantTaxes()));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Montant TPS (5%) : " + formatCurrency(facture.getTps()));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Montant TVQ (9,975%) : " + formatCurrency(facture.getTvq()));
            contentStream.newLineAtOffset(0, -15);
            contentStream.setFont(customFont, 14);
            contentStream.showText("Montant TTC : " + formatCurrency(facture.getMontantApresTaxes()));
            contentStream.endText();

            // Message de remerciement
            contentStream.beginText();
            contentStream.setFont(customFont, 12);
            contentStream.newLineAtOffset(30, 40);
            contentStream.showText("Merci pour votre confiance.");
            contentStream.endText();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour dessiner les en-têtes du tableau
    private static void drawTableHeaders(PDPageContentStream contentStream, float xPosition, float yPosition, float[] columnWidths, PDType0Font customFont) throws IOException {
        String[] headers = {"Date", "Prestation", "Début", "Fin", "Pause", "Temps", "Taux", "Montant HT"};

        float cellHeight = 20f;
        float textY = yPosition - 15;

        // Dessiner le fond des en-têtes
        contentStream.setNonStrokingColor(new Color(200, 200, 200)); // Gris clair
        contentStream.addRect(xPosition, yPosition - cellHeight, sum(columnWidths), cellHeight);
        contentStream.fill();
        contentStream.setNonStrokingColor(Color.BLACK); // Réinitialiser la couleur

        // Dessiner le texte des en-têtes
        contentStream.setFont(customFont, 12);
        float nextX = xPosition;
        for (int i = 0; i < headers.length; i++) {
            float textX = nextX + 5; // Petite marge à gauche de la cellule
            contentStream.beginText();
            contentStream.newLineAtOffset(textX, textY);
            contentStream.showText(headers[i]);
            contentStream.endText();

            // Dessiner les bordures des cellules
            contentStream.setStrokingColor(Color.BLACK);
            contentStream.addRect(nextX, yPosition - cellHeight, columnWidths[i], cellHeight);
            contentStream.stroke();

            nextX += columnWidths[i];
        }
    }

    // Méthode pour dessiner une ligne du tableau
    private static void drawTableRow(PDPageContentStream contentStream, float xPosition, float yPosition, Quart quart, float[] columnWidths, PDType0Font customFont) throws IOException {
        float cellHeight = 20f;
        float textY = yPosition - 15;

        String dateQuart = quart.getDateQuart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String prestation = quart.getStringPrestation();
        String debut = quart.getDebutQuart().toString();
        String fin = quart.getFinQuart().toString();
        String pause = quart.getPause().toString();
        String tempsTotal = quart.getTempsTotal();
        String tauxHoraire = formatCurrency(BigDecimal.valueOf(quart.getTauxHoraire()));
        String montantHT = formatCurrency(BigDecimal.valueOf(quart.getMontantTotal()));

        String[] rowData = {dateQuart, prestation, debut, fin, pause, tempsTotal, tauxHoraire, montantHT};

        contentStream.setFont(customFont, 10);
        float nextX = xPosition;
        for (int i = 0; i < rowData.length; i++) {
            float textX = nextX + 5; // Petite marge à gauche de la cellule

            // Aligner à droite les valeurs numériques
            if (i >= 5) { // Les colonnes "Temps", "Taux", "Montant HT"
                float stringWidth = customFont.getStringWidth(rowData[i]) / 1000 * 10; // Taille de police 10
                textX = nextX + columnWidths[i] - stringWidth - 5; // Ajuster pour aligner à droite
            }

            contentStream.beginText();
            contentStream.newLineAtOffset(textX, textY);
            contentStream.showText(rowData[i]);
            contentStream.endText();

            // Dessiner les bordures des cellules
            contentStream.setStrokingColor(Color.BLACK);
            contentStream.addRect(nextX, yPosition - cellHeight, columnWidths[i], cellHeight);
            contentStream.stroke();

            nextX += columnWidths[i];
        }
    }

    // Méthode pour formater les montants en devise
    private static String formatCurrency(BigDecimal value) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.CANADA_FRENCH);
        return currencyFormat.format(value);
    }

    // Méthode pour sommer les valeurs d'un tableau de float
    private static float sum(float[] values) {
        float total = 0;
        for (float value : values) {
            total += value;
        }
        return total;
    }
}
