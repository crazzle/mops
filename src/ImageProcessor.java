import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageProcessor {

	// Grafikobjekt auf dem gezeichnet wird
	Graphics2D graph;

	// Bild, zum dem das Grafikobjekt gehört
	BufferedImage img;

	public ImageProcessor() {
		// erstellt ein neues Bild mit der Größe 430 Pixel Breite auf 300
		// Pixel Höhe
		img = new BufferedImage(430, 300, BufferedImage.TYPE_INT_RGB);

		// Holt das Grafikobjekt des Bildes und speichert die Referenz in graph
		graph = img.createGraphics();
	}

	// Methode zum erstellen des Diagramms
	public void createDiagram(int[] array) {
		// graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);

		// Setze Farbe zum Füllen (Grau)
		Color c4 = new Color(222, 222, 222);
		graph.setColor(c4);

		// Zeichne ein gefülltes Rechteck
		graph.fillRect(0, 0, 430, 300);

		// Setze Farbe zum Füllen (Weiß)
		graph.setColor(Color.WHITE);

		// Zeichne ein weißes Rechteck im grauen Rechteck
		graph.fillRect(22, 0, 430, 280);

		// Überschrift optional
		// graph.setColor(c4);
		// graph.drawString("MOPS: Intelligente Software" , 20, 13);

		/*
		 * Gitterkreuz optional
		 * 
		 * graph.setColor(Color.LIGHT_GRAY);
		 * 
		 * for (int x = 1; x < 1000; x++) {
		 * 
		 * // senkrecht graph.drawLine(5+(10*x),5,5+(10*x),300);
		 * 
		 * // waagrecht graph.drawLine(5,5+(10*x),485,5+(10*x));
		 * 
		 * }
		 */

		// Graph Farbe und Dicke der Linie

		graph.setStroke(new BasicStroke(3));

		// Test-Array "array" zur Darstellung der vom Mediator übermittelten
		// Werte

		// int [] array =
		// {130,130,140,150,160,170,180,190,200,10,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20,130,130,140,150,160,170,180,190,200,20};
		// int [] array = {130,40,70,80,70,20,170,180,30,100};
		// int [] array =
		// {100,130,120,110,50,90,80,70,60,60,60,160,60,50,50,50,40,10,30,30,32,33,34,35,36,20,30,40,80,180,100,130,120,110,50,90,80,70,60,60,60,160,60,50,50,50,40,10,30,30,32,33,34,35,36,20,30,40,80,180,20};

		// Säulendarstellung

		// es werden immer nur die 20 aktuellsten Werte (Säulen) dargestellt
		int s;

		if (array.length < 21)
			s = array.length;
		else
			s = 20;

		// Farbe der Säulen definieren
		Color c = new Color(145, 162, 184);

		// Säulen zeichnen
		for (int i = array.length - 1; i >= array.length - s; i--) {

			int d = 25 + 20 * (i - (array.length - s));

			graph.setColor(c);
			graph.fill3DRect(d, 280 - (array[i]), 10, array[i], true);

		}

		// Achsenbeschriftung x-Achse

		graph.setColor(Color.BLACK);
		Font f = new Font("sansserif", Font.BOLD, 10);
		graph.setFont(f);

		for (int x = array.length - s; x < array.length; x = x + 2) {
			int e = 23 + 20 * (x - (array.length - s));
			graph.drawString("" + (x + 1), e, 293);
		}

		for (int y = array.length - s; y < array.length; y = y + 2) {
			int e = 49 + 20 * (y - (array.length - s));
			graph.drawString("|", e, 287);
		}

		// Achsenbeschriftung y-Achse

		for (int w = 1; w < 11; w++) {
			graph.drawString("" + w * 20, 1, 285 - w * 20);
		}

		// Optimum und Schlechtimum anzeigen
		int optimum = 0;
		if (array.length > 0)
			optimum = array[0];

		for (int i = 0; i < array.length; i++) {
			if (array[i] < optimum)
				optimum = array[i];
		}

		graph.setStroke(new BasicStroke(2));
		Font fon = new Font("sansserif", Font.ROMAN_BASELINE, 12);
		graph.setFont(fon);
		Color c1 = new Color(107, 125, 16);
		graph.setColor(c1);
		graph.drawLine(24, 280 - optimum, 420, 280 - optimum);

		int pessimum = 0;
		if (array.length > 0)
			pessimum = array[0];

		for (int i = 0; i < array.length; i++) {
			if (array[i] > pessimum)
				pessimum = array[i];
		}

		Color c2 = new Color(140, 36, 66);
		graph.setColor(c2);

		graph.drawLine(24, 280 - pessimum, 420, 280 - pessimum);

		// Infokaestchen "Statistik"

		graph.setColor(Color.BLACK);
		graph.drawRect(300, 20, 120, 55);
		graph.setColor(Color.WHITE);
		graph.fillRect(300, 20, 120, 55);

		Font fo = new Font("sansserif", Font.BOLD, 12);
		graph.setFont(fo);
		graph.setColor(Color.BLACK);
		graph.drawString("Statistic: ", 310, 15);
		graph.setColor(c2);
		graph.drawString("Worst   : " + pessimum, 305, 32);
		graph.setColor(c1);
		graph.drawString("Best    : " + optimum, 305, 52);
		graph.setColor(Color.BLACK);
		graph.drawString("Periods  : " + array.length, 305, 72);

		// Infokaestchen "Gesamtverlauf"

		graph.drawString("Overall Progression: ", 30, 15);
		graph.setColor(Color.BLACK);
		graph.drawRect(27, 20, 190, 55);
		graph.setColor(Color.WHITE);
		graph.fillRect(27, 20, 190, 55);

		// Gesamtverlauf darstellen

		// xs ist der Stauchfaktor in der Breite, um den Gesamtverlauf in den
		// Kasten zu pressen, die 210 beschränken auf die maximale Breite

		int xs = 0;
		if (array.length > 0)
			xs = 200 / array.length;

		int[] ys = new int[array.length];

		for (int i = 0; i < array.length; i++) {
			ys[i] = 30 + xs * i;
		}

		// zs ist der Umkehrfaktor, da der Graph sonst gespiegelt dargestellt
		// würde, 72 sind der Abstand vom Rand und das 1/4 die Stauchung in der
		// Dicke

		int[] zs = new int[array.length];

		for (int i = 0; i < array.length; i++) {
			zs[i] = 72 - (array[i] * 1 / 4);
		}

		// Gesamtverlauf zeichnen

		graph.setColor(c);
		graph.setStroke(new BasicStroke(1));
		graph.drawPolyline(ys, zs, ys.length);

	}

	// Methode zum Speichern des Bildes
	public void saveAsPicture(String file) {
		try {
			ImageIO.write(img, "png", new File(file));
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}

}