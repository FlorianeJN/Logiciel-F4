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
                float partnerX = PDRectangle.LETTER.getWidth() - margin - 220; // Largeur ajustée pour agrandir le cadre
                float partnerY = logoY + spacing; // Aligné avec le logo

                // Placer "Facturé à:" au-dessus du cadre
                contentStream.beginText();
                contentStream.newLineAtOffset(partnerX, partnerY + 15); // Positionner au-dessus du cadre
                contentStream.showText("Facturé à:");
                contentStream.endText();

                // Dessiner le cadre
                float cadreWidth = 220; // Largeur du cadre augmentée
                float cadreHeight = spacing * 5; // Calculer la hauteur en fonction du nombre de lignes de texte
                contentStream.setStrokingColor(Color.BLACK);
                contentStream.setLineWidth(1);
                contentStream.addRect(partnerX - 5, partnerY - cadreHeight - 5, cadreWidth, cadreHeight + 5); // Ajuster le cadre
                contentStream.stroke();

                // Informations du partenaire à l'intérieur du cadre
                contentStream.beginText();
                contentStream.newLineAtOffset(partnerX, partnerY - spacing); // Déplacer légèrement vers le bas
                contentStream.showText(partenaire.getNom());
                contentStream.newLineAtOffset(0, -spacing);
                contentStream.showText(partenaire.getAdresseObj().getNumeroCivique() + " " + partenaire.getAdresseObj().getRue());
                contentStream.newLineAtOffset(0, -spacing);
                contentStream.showText(partenaire.getAdresseObj().getVille() + ", " + partenaire.getAdresseObj().getProvince() + " " + partenaire.getAdresseObj().getCodePostal());
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
        float minYPosition = 100; // Minimum y position to switch to a new page

        // Adjust column widths
        float[] columnWidths = {70, 120, 50, 50, 50, 50, 60, 70};

        // Draw table headers
        drawTableHeaders(contentStream, margin, yPosition, columnWidths, customFont);
        yPosition -= 30; // Adjust position after the line

        List<Quart> quarts = facture.getListeQuarts();
        for (Quart quart : quarts) {
            if (yPosition < minYPosition) { // Check if we need a new page
                // Draw footer before adding a new page
                drawFooter(document, contentStream);

                contentStream.close();
                page = new PDPage(PDRectangle.LETTER);
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);

                // Reset yPosition to start table at the top of the new page
                yPosition = 750; // Set to a value near the top of the new page
                drawTableHeaders(contentStream, margin, yPosition, columnWidths, customFont);
                yPosition -= 30;
            }

            drawTableRow(contentStream, margin, yPosition, quart, columnWidths, customFont);
            yPosition -= 20;
        }

        // Add space before the amount section
        yPosition -= 20;

        // Check if we need a new page before the amounts section
        if (yPosition < minYPosition) {
            drawFooter(document, contentStream);

            contentStream.close();
            page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            contentStream = new PDPageContentStream(document, page);

            // Reset yPosition to start content at the top of the new page
            yPosition = 750;
        }

        // Adjust the position of labels and amounts
        float labelOffset = 350; // Move labels further left
        float valueOffset = labelOffset + 120; // Adjust for value position

        contentStream.setFont(customFont, 10); // Reduce font size to 10

        // Montant total HT
        contentStream.beginText();
        contentStream.newLineAtOffset(labelOffset, yPosition);
        contentStream.showText("Montant total HT : ");
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(valueOffset, yPosition);
        contentStream.showText(formatCurrency(facture.getMontantAvantTaxes()));
        contentStream.endText();
        yPosition -= 15;

        // Montant TPS
        contentStream.beginText();
        contentStream.newLineAtOffset(labelOffset, yPosition);
        contentStream.showText("Montant TPS (5%) : ");
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(valueOffset, yPosition);
        contentStream.showText(formatCurrency(facture.getTps()));
        contentStream.endText();
        yPosition -= 15;

        // Montant TVQ
        contentStream.beginText();
        contentStream.newLineAtOffset(labelOffset, yPosition);
        contentStream.showText("Montant TVQ (9,975%) : ");
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(valueOffset, yPosition);
        contentStream.showText(formatCurrency(facture.getTvq()));
        contentStream.endText();
        yPosition -= 15; // Adjusted space

        // Check if we need a new page before the TTC amount
        if (yPosition < minYPosition) {
            drawFooter(document, contentStream);

            contentStream.close();
            page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            contentStream = new PDPageContentStream(document, page);
            yPosition = 750; // Reset yPosition for the new page
        }

        // Montant TTC (en gras)
        contentStream.setFont(customFont, 12); // Slightly larger font for TTC
        contentStream.beginText();
        contentStream.setNonStrokingColor(Color.BLACK); // Text color black
        contentStream.setLineWidth(1.5f); // Increase line width for bold effect
        contentStream.newLineAtOffset(labelOffset, yPosition);
        contentStream.showText("Montant TTC : ");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setLineWidth(1.5f); // Increase line width for bold effect
        contentStream.newLineAtOffset(valueOffset, yPosition);
        contentStream.showText(formatCurrency(facture.getMontantApresTaxes()));
        contentStream.endText();
        yPosition -= 25; // Slightly reduced space

        // Draw footer at the bottom of the last page
        drawFooter(document, contentStream);

        return contentStream;
    }


    // Method to draw the footer on each page
    private static void drawFooter(PDDocument document, PDPageContentStream contentStream) throws IOException {
        // Draw footer image
        PDImageXObject footerImage = PDImageXObject.createFromFile("src/main/resources/Images/footerF4.png", document); // Ensure correct image path
        float footerWidth = PDRectangle.LETTER.getWidth(); // Full page width
        float footerHeight = 50; // Adjust height if necessary
        float footerX = 0; // Start from left margin
        float footerY = 20; // Position near bottom
        contentStream.drawImage(footerImage, footerX, footerY, footerWidth, footerHeight);
    }

    // Call the drawFooter method for each page in the document
    public static void addFootersToAllPages(PDDocument document) throws IOException {
        for (PDPage page : document.getPages()) {
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
            drawFooter(document, contentStream);
            contentStream.close();
        }
    }




    // Méthode pour convertir le montant en lettres
    private static String montantEnLettres(BigDecimal amount) {
        int dollars = amount.intValue(); // Partie entière des dollars
        return numberToWords(dollars);
    }

    // Méthode pour obtenir les cents en lettres
    private static String getCentsInWords(BigDecimal amount) {
        BigDecimal cents = amount.remainder(BigDecimal.ONE).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
        return numberToWords(cents.intValue());
    }

    // Méthode pour convertir un nombre en lettres (gère les grands nombres)
    private static String numberToWords(int number) {
        if (number == 0) {
            return "zéro";
        }

        String[] units = {"", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf"};
        String[] teens = {"dix", "onze", "douze", "treize", "quatorze", "quinze", "seize", "dix-sept", "dix-huit", "dix-neuf"};
        String[] tens = {"", "", "vingt", "trente", "quarante", "cinquante", "soixante"};
        String[] tensSpecial = {"soixante", "quatre-vingt"};

        StringBuilder words = new StringBuilder();

        if (number < 0) {
            words.append("moins ");
            number = -number;
        }

        if (number >= 1000000) {
            int millions = number / 1000000;
            words.append(numberToWords(millions)).append(" million");
            if (millions > 1) {
                words.append("s");
            }
            number %= 1000000;
            if (number > 0) {
                words.append(" ");
            }
        }

        if (number >= 1000) {
            int thousands = number / 1000;
            if (thousands > 1) {
                words.append(numberToWords(thousands)).append(" ");
            }
            words.append("mille");
            number %= 1000;
            if (number > 0) {
                words.append(" ");
            }
        }

        if (number >= 100) {
            int hundreds = number / 100;
            if (hundreds > 1) {
                words.append(units[hundreds]).append(" cent");
            } else {
                words.append("cent");
            }
            number %= 100;
            if (number > 0) {
                words.append(" ");
            } else if (hundreds > 1 && number == 0) {
                words.append("s");
            }
        }

        if (number >= 20) {
            int tensIndex = number / 10;
            int unit = number % 10;
            if (tensIndex <= 6) {
                words.append(tens[tensIndex]);
                if (unit == 1) {
                    words.append(" et un");
                } else if (unit > 1) {
                    words.append("-").append(units[unit]);
                }
            } else {
                if (tensIndex == 7) {
                    words.append(tensSpecial[0]);
                    if (unit == 1) {
                        words.append(" et onze");
                    } else if (unit > 1) {
                        words.append("-").append(teens[unit]);
                    } else {
                        words.append("-dix");
                    }
                } else if (tensIndex == 8) {
                    words.append(tensSpecial[1]);
                    if (unit > 0) {
                        words.append("-").append(units[unit]);
                    }
                } else if (tensIndex == 9) {
                    words.append(tensSpecial[1]);
                    if (unit == 1) {
                        words.append(" et onze");
                    } else if (unit > 1) {
                        words.append("-").append(teens[unit]);
                    } else {
                        words.append("-dix");
                    }
                }
            }
        } else if (number >= 10) {
            words.append(teens[number - 10]);
        } else if (number > 0) {
            words.append(units[number]);
        }

        return words.toString().trim();
    }

    // Méthode pour dessiner les en-têtes du tableau
    private static void drawTableHeaders(PDPageContentStream contentStream, float xPosition, float yPosition, float[] columnWidths, PDType0Font customFont) throws IOException {
        String[] headers = {"Date", "Prestation", "Début", "Fin", "Pause", "Temps", "Taux", "Montant HT"};

        float cellHeight = 20f;
        float textY = yPosition - 15;

        // Draw header background
        contentStream.setNonStrokingColor(new Color(200, 200, 200)); // Light gray
        contentStream.addRect(xPosition, yPosition - cellHeight, sum(columnWidths), cellHeight);
        contentStream.fill();
        contentStream.setNonStrokingColor(Color.BLACK); // Reset color

        // Draw header text centered
        contentStream.setFont(customFont, 12);
        float nextX = xPosition;
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            float textWidth = customFont.getStringWidth(header) / 1000 * 12; // Font size is 12
            float textX = nextX + (columnWidths[i] - textWidth) / 2; // Center the text within the cell

            contentStream.beginText();
            contentStream.newLineAtOffset(textX, textY);
            contentStream.showText(header);
            contentStream.endText();

            // Draw cell borders
            contentStream.setStrokingColor(Color.BLACK);
            contentStream.addRect(nextX, yPosition - cellHeight, columnWidths[i], cellHeight);
            contentStream.stroke();

            nextX += columnWidths[i];
        }
    }


    private static void drawTableRow(PDPageContentStream contentStream, float xPosition, float yPosition, Quart quart, float[] columnWidths, PDType0Font customFont) throws IOException {
        float cellHeight = 20f;
        float textY = yPosition - 15;

        // Get the row data
        String dateQuart = quart.getDateQuart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String prestation = quart.getStringPrestation();
        String debut = quart.getDebutQuart().toString();
        String fin = quart.getFinQuart().toString();
        String pause = quart.getPause().toString();
        String tempsTotal = quart.getTempsTotal();
        String tauxHoraire = formatCurrency(BigDecimal.valueOf(quart.getTauxHoraire()));
        String montantHT = formatCurrency(BigDecimal.valueOf(quart.getMontantTotal()));

        String[] rowData = {dateQuart, prestation, debut, fin, pause, tempsTotal, tauxHoraire, montantHT};

        contentStream.setFont(customFont, 10); // Font size 10 for table rows
        float nextX = xPosition;
        for (int i = 0; i < rowData.length; i++) {
            String cellText = rowData[i];
            float textWidth = customFont.getStringWidth(cellText) / 1000 * 10; // Font size is 10
            float textX = nextX + (columnWidths[i] - textWidth) / 2; // Center the text within the cell

            contentStream.beginText();
            contentStream.newLineAtOffset(textX, textY);
            contentStream.showText(cellText);
            contentStream.endText();

            // Draw cell borders
            contentStream.setStrokingColor(Color.BLACK);
            contentStream.addRect(nextX, yPosition - cellHeight, columnWidths[i], cellHeight);
            contentStream.stroke();

            nextX += columnWidths[i];
        }
    }

    // Méthode pour formater les montants en devise
    private static String formatCurrency(BigDecimal value) {
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.CANADA_FRENCH); // Utiliser le format de nombre sans le signe $ par défaut
        currencyFormat.setMinimumFractionDigits(2); // Toujours afficher deux décimales
        currencyFormat.setMaximumFractionDigits(2);
        return currencyFormat.format(value) + " $"; // Ajouter le signe $ à la fin
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
