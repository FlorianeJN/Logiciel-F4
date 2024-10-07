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

/**
 * Classe utilitaire pour la gestion des fichiers d'exportation, en particulier pour la création et la génération
 * de factures au format PDF.
 */
public class IOUtils {

    /**
     * Lance une boîte de dialogue permettant à l'utilisateur de sélectionner l'emplacement de sauvegarde du fichier PDF.
     * Une facture est générée à l'emplacement choisi.
     *
     * @param facture La facture à exporter en PDF.
     */
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

    /**
     * Crée un fichier PDF à l'emplacement spécifié contenant les informations d'une facture.
     *
     * @param filePath Le chemin du fichier PDF à créer.
     * @param facture  La facture à inclure dans le PDF.
     * @throws IOException En cas d'erreur lors de la création du fichier PDF.
     */
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

    /**
     * Écrit l'en-tête du PDF, incluant le logo de l'entreprise et les informations sur la facture et le partenaire.
     *
     * @param document      Le document PDF en cours de création.
     * @param contentStream Le flux de contenu pour écrire dans le PDF.
     * @param facture       La facture pour laquelle l'en-tête est généré.
     * @param customFont    La police personnalisée utilisée dans le document.
     * @return La position Y après l'écriture de l'en-tête pour continuer le contenu.
     */
    private static float writeHeader(PDDocument document, PDPageContentStream contentStream, Facture facture, PDType0Font customFont) {
        float yPosition = 750;
        float margin = 50;
        float spacing = 12;

        try {
            yPosition = drawLogo(document, contentStream, yPosition, margin);
            writeTitle(contentStream, facture, customFont, yPosition);
            float infoEntrepriseY = writeCompanyInfo(contentStream, customFont, margin, yPosition, spacing);
            writePartnerInfo(contentStream, facture, customFont, margin, yPosition, spacing);
            yPosition = drawSeparatorLine(contentStream, infoEntrepriseY, spacing);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return yPosition;
    }

    /**
     * Dessine le logo de l'entreprise dans l'en-tête du PDF.
     *
     * @param document      Le document PDF.
     * @param contentStream Le flux de contenu pour écrire dans le PDF.
     * @param yPosition     La position Y actuelle.
     * @param margin        La marge pour positionner le logo.
     * @return La nouvelle position Y après avoir dessiné le logo.
     * @throws IOException En cas d'erreur lors de l'écriture du logo.
     */
    private static float drawLogo(PDDocument document, PDPageContentStream contentStream, float yPosition, float margin) throws IOException {
        PDImageXObject logo = PDImageXObject.createFromFile("src/main/resources/Images/logo.jpg", document);
        float logoWidth = 100;
        float logoHeight = 100;
        float logoX = margin;
        float logoY = yPosition - logoHeight + 30; // Ajuster si nécessaire
        contentStream.drawImage(logo, logoX, logoY, logoWidth, logoHeight);
        return yPosition;
    }

    /**
     * Écrit le titre de la facture centré dans l'en-tête.
     *
     * @param contentStream Le flux de contenu pour écrire dans le PDF.
     * @param facture       La facture pour laquelle le titre est généré.
     * @param customFont    La police personnalisée utilisée dans le document.
     * @param yPosition     La position Y actuelle.
     * @throws IOException En cas d'erreur lors de l'écriture du texte.
     */
    private static void writeTitle(PDPageContentStream contentStream, Facture facture, PDType0Font customFont, float yPosition) throws IOException {
        String titreFacture = "FACTURE N° " + facture.getNumFacture();
        contentStream.setFont(customFont, 18);
        float titreWidth = customFont.getStringWidth(titreFacture) / 1000 * 18;
        float titreX = (PDRectangle.LETTER.getWidth() - titreWidth) / 2;
        contentStream.beginText();
        contentStream.newLineAtOffset(titreX, yPosition);
        contentStream.showText(titreFacture);
        contentStream.endText();
    }

    /**
     * Écrit les informations de l'entreprise dans l'en-tête sous le logo.
     *
     * @param contentStream Le flux de contenu pour écrire dans le PDF.
     * @param customFont    La police personnalisée utilisée dans le document.
     * @param margin        La marge à gauche pour positionner le texte.
     * @param yPosition     La position Y actuelle.
     * @param spacing       L'espacement entre les lignes de texte.
     * @return La nouvelle position Y après avoir écrit les informations de l'entreprise.
     * @throws IOException En cas d'erreur lors de l'écriture du texte.
     */
    private static float writeCompanyInfo(PDPageContentStream contentStream, PDType0Font customFont, float margin, float yPosition, float spacing) throws IOException {
        contentStream.setFont(customFont, 10);
        float logoY = yPosition - 100 + 30; // Même ajustement que drawLogo
        float infoEntrepriseY = logoY - spacing;
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, infoEntrepriseY);
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
        return infoEntrepriseY;
    }

    /**
     * Écrit les informations du partenaire dans l'en-tête, à droite.
     *
     * @param contentStream Le flux de contenu pour écrire dans le PDF.
     * @param facture       La facture avec les informations du partenaire.
     * @param customFont    La police personnalisée utilisée dans le document.
     * @param margin        La marge à droite pour positionner le texte.
     * @param yPosition     La position Y actuelle.
     * @param spacing       L'espacement entre les lignes de texte.
     * @throws IOException En cas d'erreur lors de l'écriture du texte ou du dessin du cadre.
     */
    private static void writePartnerInfo(PDPageContentStream contentStream, Facture facture, PDType0Font customFont, float margin, float yPosition, float spacing) throws IOException {
        Partenaire partenaire = facture.getPartenaire();
        if (partenaire != null) {
            contentStream.setFont(customFont, 10);
            float partnerX = PDRectangle.LETTER.getWidth() - margin - 220;
            float logoY = yPosition - 100 + 30; // Même ajustement que drawLogo
            float partnerY = logoY + spacing;

            // Placer "Facturé à:" au-dessus du cadre
            contentStream.beginText();
            contentStream.newLineAtOffset(partnerX, partnerY + 15);
            contentStream.showText("Facturé à:");
            contentStream.endText();

            // Dessiner le cadre
            float cadreWidth = 220;
            float cadreHeight = spacing * 5;
            contentStream.setStrokingColor(Color.BLACK);
            contentStream.setLineWidth(1);
            contentStream.addRect(partnerX - 5, partnerY - cadreHeight - 5, cadreWidth, cadreHeight + 5);
            contentStream.stroke();

            // Informations du partenaire à l'intérieur du cadre
            contentStream.beginText();
            contentStream.newLineAtOffset(partnerX, partnerY - spacing);
            contentStream.showText(partenaire.getNom());
            contentStream.newLineAtOffset(0, -spacing);
            contentStream.showText(partenaire.getAdresseObj().getNumeroCivique() + " " + partenaire.getAdresseObj().getRue());
            contentStream.newLineAtOffset(0, -spacing);
            contentStream.showText(partenaire.getAdresseObj().getVille() + ", " + partenaire.getAdresseObj().getProvince() + " " + partenaire.getAdresseObj().getCodePostal());
            contentStream.newLineAtOffset(0, -spacing);
            contentStream.showText("Téléphone : " + partenaire.getTelephone());
            contentStream.endText();
        }
    }

    /**
     * Dessine une ligne séparatrice grise dans l'en-tête après les informations.
     *
     * @param contentStream Le flux de contenu pour écrire dans le PDF.
     * @param yPosition     La position Y actuelle.
     * @param spacing       L'espacement entre la ligne et le texte précédent.
     * @return La nouvelle position Y après avoir dessiné la ligne.
     * @throws IOException En cas d'erreur lors du dessin de la ligne.
     */
    private static float drawSeparatorLine(PDPageContentStream contentStream, float yPosition, float spacing) throws IOException {
        yPosition -= spacing * 6;
        contentStream.setStrokingColor(Color.GRAY);
        contentStream.setLineWidth(1);
        contentStream.moveTo(50, yPosition);
        contentStream.lineTo(PDRectangle.LETTER.getWidth() - 50, yPosition);
        contentStream.stroke();
        yPosition -= spacing;
        return yPosition;
    }

    /**
     * Écrit le contenu de la facture dans le PDF, incluant les informations sur les quarts et les montants.
     *
     * @param document      Le document PDF en cours de création.
     * @param page          La page actuelle du PDF.
     * @param contentStream Le flux de contenu pour écrire dans le PDF.
     * @param facture       La facture pour laquelle le contenu est généré.
     * @param customFont    La police personnalisée utilisée dans le document.
     * @param yStart        La position Y où commencer l'écriture du contenu.
     * @return Le flux de contenu mis à jour après l'écriture du contenu.
     * @throws IOException En cas d'erreur lors de l'écriture du contenu.
     */
    private static PDPageContentStream writeContent(PDDocument document, PDPage page, PDPageContentStream contentStream, Facture facture, PDType0Font customFont, float yStart) throws IOException {
        float yPosition = yStart;
        float margin = 30;
        float minYPosition = 100; // Position Y minimale avant d'ajouter une nouvelle page

        // Ajuster les largeurs des colonnes
        float[] columnWidths = {70, 120, 50, 50, 50, 50, 60, 70};

        // Dessiner les en-têtes du tableau
        drawTableHeaders(contentStream, margin, yPosition, columnWidths, customFont);
        yPosition -= 30; // Ajuster la position après la ligne

        List<Quart> quarts = facture.getListeQuarts();
        for (Quart quart : quarts) {
            if (yPosition < minYPosition) {
                contentStream = addNewPageWithHeaders(document, contentStream, customFont, columnWidths);
                yPosition = 750 - 30; // Réinitialiser la position Y après les en-têtes
            }

            drawTableRow(contentStream, margin, yPosition, quart, columnWidths, customFont);
            yPosition -= 20;
        }

        // Ajouter un espace avant la section des montants
        yPosition -= 20;

        if (yPosition < minYPosition) {
            contentStream = addNewPage(document, contentStream);
            yPosition = 750;
        }

        // Dessiner la section des montants
        yPosition = drawMontantSection(document, contentStream, facture, customFont, yPosition, minYPosition);

        // Dessiner le pied de page en bas de la dernière page
        drawFooter(document, contentStream);

        return contentStream;
    }

    /**
     * Ajoute une nouvelle page avec les en-têtes des tableaux.
     *
     * @param document      Le document PDF.
     * @param contentStream Le flux de contenu pour écrire dans le PDF.
     * @param customFont    La police personnalisée utilisée dans le document.
     * @param columnWidths  Les largeurs des colonnes pour le tableau.
     * @return Le flux de contenu mis à jour après l'ajout de la nouvelle page.
     * @throws IOException En cas d'erreur lors de l'ajout de la nouvelle page.
     */
    private static PDPageContentStream addNewPageWithHeaders(PDDocument document, PDPageContentStream contentStream, PDType0Font customFont, float[] columnWidths) throws IOException {
        contentStream.close();
        PDPage newPage = new PDPage(PDRectangle.LETTER);
        document.addPage(newPage);
        contentStream = new PDPageContentStream(document, newPage);
        float yPosition = 750;
        float margin = 30;
        drawTableHeaders(contentStream, margin, yPosition, columnWidths, customFont);
        return contentStream;
    }

    /**
     * Ajoute une nouvelle page vide au document PDF.
     *
     * @param document      Le document PDF.
     * @param contentStream Le flux de contenu pour écrire dans le PDF.
     * @return Le flux de contenu mis à jour après l'ajout de la nouvelle page.
     * @throws IOException En cas d'erreur lors de l'ajout de la nouvelle page.
     */
    private static PDPageContentStream addNewPage(PDDocument document, PDPageContentStream contentStream) throws IOException {
        contentStream.close();
        PDPage newPage = new PDPage(PDRectangle.LETTER);
        document.addPage(newPage);
        return new PDPageContentStream(document, newPage);
    }

    /**
     * Écrit la section des montants dans le PDF.
     *
     * @param document      Le document PDF en cours de création.
     * @param contentStream Le flux de contenu pour écrire dans le PDF.
     * @param facture       La facture avec les montants.
     * @param customFont    La police personnalisée utilisée dans le document.
     * @param yPosition     La position Y actuelle.
     * @param minYPosition  La position Y minimale avant d'ajouter une nouvelle page.
     * @return La position Y après avoir écrit la section des montants.
     * @throws IOException En cas d'erreur lors de l'écriture du contenu.
     */
    private static float drawMontantSection(PDDocument document, PDPageContentStream contentStream, Facture facture, PDType0Font customFont, float yPosition, float minYPosition) throws IOException {
        float labelOffset = 350;
        float valueOffset = labelOffset + 120;
        contentStream.setFont(customFont, 10);

        // Montant total HT
        yPosition = writeMontantLine(contentStream, "Montant total HT : ", formatCurrency(facture.getMontantAvantTaxes()), labelOffset, valueOffset, yPosition);
        // Montant TPS
        yPosition = writeMontantLine(contentStream, "Montant TPS (5%) : ", formatCurrency(facture.getTps()), labelOffset, valueOffset, yPosition);
        // Montant TVQ
        yPosition = writeMontantLine(contentStream, "Montant TVQ (9,975%) : ", formatCurrency(facture.getTvq()), labelOffset, valueOffset, yPosition);

        // Vérifier si nous devons ajouter une nouvelle page avant le montant TTC
        if (yPosition < minYPosition) {
            contentStream = addNewPage(document, contentStream);
            yPosition = 750;
        }

        // Montant TTC (en gras)
        contentStream.setFont(customFont, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(labelOffset, yPosition);
        contentStream.showText("Montant TTC : ");
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(valueOffset, yPosition);
        contentStream.showText(formatCurrency(facture.getMontantApresTaxes()));
        contentStream.endText();
        yPosition -= 25;

        return yPosition;
    }

    /**
     * Écrit une ligne pour la section des montants.
     *
     * @param contentStream Le flux de contenu pour écrire dans le PDF.
     * @param label         Le texte du label.
     * @param value         Le texte du montant.
     * @param labelOffset   Le décalage X pour le label.
     * @param valueOffset   Le décalage X pour le montant.
     * @param yPosition     La position Y actuelle.
     * @return La nouvelle position Y après avoir écrit la ligne.
     * @throws IOException En cas d'erreur lors de l'écriture du texte.
     */
    private static float writeMontantLine(PDPageContentStream contentStream, String label, String value, float labelOffset, float valueOffset, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(labelOffset, yPosition);
        contentStream.showText(label);
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(valueOffset, yPosition);
        contentStream.showText(value);
        contentStream.endText();
        return yPosition - 15;
    }

    /**
     * Dessine le pied de page sur chaque page du document PDF.
     *
     * @param document      Le document PDF en cours de création.
     * @param contentStream Le flux de contenu pour écrire dans le PDF.
     * @throws IOException En cas d'erreur lors du dessin du pied de page.
     */
    private static void drawFooter(PDDocument document, PDPageContentStream contentStream) throws IOException {
        PDImageXObject footerImage = PDImageXObject.createFromFile("src/main/resources/Images/footerF4.png", document);
        float footerWidth = PDRectangle.LETTER.getWidth();
        float footerHeight = 50;
        float footerX = 0;
        float footerY = 20;
        contentStream.drawImage(footerImage, footerX, footerY, footerWidth, footerHeight);
    }

    /**
     * Dessine les en-têtes du tableau dans le PDF.
     *
     * @param contentStream Le flux de contenu pour écrire dans le PDF.
     * @param xPosition     La position X où commencer l'écriture des en-têtes.
     * @param yPosition     La position Y où commencer l'écriture des en-têtes.
     * @param columnWidths  Les largeurs des colonnes.
     * @param customFont    La police personnalisée utilisée dans le document.
     * @throws IOException En cas d'erreur lors du dessin des en-têtes.
     */
    private static void drawTableHeaders(PDPageContentStream contentStream, float xPosition, float yPosition, float[] columnWidths, PDType0Font customFont) throws IOException {
        String[] headers = {"Date", "Prestation", "Début", "Fin", "Pause", "Temps", "Taux", "Montant HT"};

        float cellHeight = 20f;
        float textY = yPosition - 15;

        // Dessiner l'arrière-plan de l'en-tête
        contentStream.setNonStrokingColor(new Color(200, 200, 200)); // Gris clair
        contentStream.addRect(xPosition, yPosition - cellHeight, sum(columnWidths), cellHeight);
        contentStream.fill();
        contentStream.setNonStrokingColor(Color.BLACK); // Réinitialiser la couleur

        // Dessiner le texte de l'en-tête centré
        contentStream.setFont(customFont, 12);
        float nextX = xPosition;
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            float textWidth = customFont.getStringWidth(header) / 1000 * 12;
            float textX = nextX + (columnWidths[i] - textWidth) / 2;

            contentStream.beginText();
            contentStream.newLineAtOffset(textX, textY);
            contentStream.showText(header);
            contentStream.endText();

            // Dessiner les bordures des cellules
            contentStream.setStrokingColor(Color.BLACK);
            contentStream.addRect(nextX, yPosition - cellHeight, columnWidths[i], cellHeight);
            contentStream.stroke();

            nextX += columnWidths[i];
        }
    }

    /**
     * Dessine une ligne du tableau avec les informations d'un quart.
     *
     * @param contentStream Le flux de contenu pour écrire dans le PDF.
     * @param xPosition     La position X où commencer l'écriture de la ligne.
     * @param yPosition     La position Y où commencer l'écriture de la ligne.
     * @param quart         Les informations du quart à afficher.
     * @param columnWidths  Les largeurs des colonnes.
     * @param customFont    La police personnalisée utilisée dans le document.
     * @throws IOException En cas d'erreur lors du dessin de la ligne.
     */
    private static void drawTableRow(PDPageContentStream contentStream, float xPosition, float yPosition, Quart quart, float[] columnWidths, PDType0Font customFont) throws IOException {
        float cellHeight = 20f;
        float textY = yPosition - 15;

        // Obtenir les données de la ligne
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
            String cellText = rowData[i];
            float textWidth = customFont.getStringWidth(cellText) / 1000 * 10;
            float textX = nextX + (columnWidths[i] - textWidth) / 2;

            contentStream.beginText();
            contentStream.newLineAtOffset(textX, textY);
            contentStream.showText(cellText);
            contentStream.endText();

            // Dessiner les bordures des cellules
            contentStream.setStrokingColor(Color.BLACK);
            contentStream.addRect(nextX, yPosition - cellHeight, columnWidths[i], cellHeight);
            contentStream.stroke();

            nextX += columnWidths[i];
        }
    }

    /**
     * Formate un montant en devise.
     *
     * @param value Le montant à formater.
     * @return Le montant formaté avec deux décimales.
     */
    private static String formatCurrency(BigDecimal value) {
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.CANADA_FRENCH);
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        return currencyFormat.format(value) + " $";
    }

    /**
     * Calcule la somme des valeurs d'un tableau de float.
     *
     * @param values Un tableau de valeurs float.
     * @return La somme des valeurs.
     */
    private static float sum(float[] values) {
        float total = 0;
        for (float value : values) {
            total += value;
        }
        return total;
    }
}
