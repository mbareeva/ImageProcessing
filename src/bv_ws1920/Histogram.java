// BV Ue4 WS2019/20 Vorgabe
//
// Copyright (C) 2019 by Klaus Jung
// All rights reserved.
// Date: 2019-05-12

package bv_ws1920;

import java.util.Arrays;

import com.sun.org.apache.xalan.internal.utils.XMLSecurityPropertyManager.Property;

import bv_ws1920.ImageAnalysisAppController.StatsProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Histogram {

	private static final int grayLevels = 256;

	private GraphicsContext gc;
	private int maxHeight;
	
	private int[] histogram = new int[grayLevels];
	
	//initialize min and max for autocontrast
	public double A;
	public double B;

	public Histogram(GraphicsContext gc, int maxHeight) {
		this.gc = gc;
		this.maxHeight = maxHeight;
	}

	
	public double getA() {
		return A;
	}
	
	public double getB() {
		return B;
	}
	public void update(RasterImage image, Point2D ellipseCenter, Dimension2D ellipseSize, int selectionMax,
			ObservableList<StatsProperty> statsData) {
		// TODO: calculate histogram[] out of the gray values of the image for pixels
		// inside the given ellipse
		for (int i = 0; i < histogram.length; i++) {
			histogram[i] = 0;
		}
		double horizR = ellipseSize.getWidth() / 2;
		double verticR = ellipseSize.getHeight() / 2;
		for (int y = 0; y < image.height; y++) {
			for (int x = 0; x < image.width; x++) {
				int pixel = (int) (Math.pow((x / horizR), 2) + Math.pow((y / verticR), 2));
				if (pixel <= 1) {
					int value = image.argb[y * image.width + x];
					int r = (value >> 16) & 0xff;
					image.argb[y * image.width + x] = (0xFF << 24) | (r << 16) | (r << 8) | r;
					histogram[r]++;
				}
			}
		}
		double allPixels = 0.0;
		int median;
		double level, mean = 0.0, pi = 0.0, summe = 0.0, Entropie = 0;

		// der Anzahl der Pixel
		for (int i = 0; i < histogram.length; i++) {
			allPixels += histogram[i];
		}
		// Wahrscheinlichkeit berechnen
		for (int z = 0; z < selectionMax; z++) {
			summe += histogram[z];
		}

		// level
		level = (double) summe / (double) allPixels;

		// mean
		// alternative Berechnungsformel über Wahrscheinlichkeitsdichtverteilung j * p(j)
		// jede p(j) Wert soll durch den Anzahl der alle Elemente geteilt werden.
		for (int i = 0; i < histogram.length; i++) {
			if (histogram[i] != 0)
				mean += i * (double)histogram[i];
		}
		mean /= allPixels;

		// median
		int[] copy = histogram.clone();
		Arrays.sort(copy);
		if (copy.length % 2 == 0) {
			median = ((copy[(copy.length) / 2]) + (copy[(copy.length / 2) - 1])) / 2;
		} else {
			median = copy[(copy.length - 1) / 2];
		}
		// Max
		for (int i = 255; i >= 0; i--) {
			if (histogram[i] != 0) {
				B = i;
				break;
			}
		}
		// Min
		for (int i = 0; i < 256; i++) {
			if (histogram[i] != 0) {
				A = i;
				break;
			}
		}

		// Entropy berechnen
		for (int i = 0; i < 256; i++) {
			pi = histogram[i] / (double) allPixels; // !
			if (pi != 0) // !
				Entropie += (double) pi * (Math.log10(pi) / Math.log10(2.0));
		}
		Entropie *= -1;

		// varianz
		double variance = 0;
		double x = 0;

		for (int i = 0; i < histogram.length; i++) {
			pi = (double) histogram[i] / (double) allPixels;
			x = (Math.pow((i - mean), 2) ) * pi;
			variance += x;

		}

		// set values for madeian, mean and so on.
		for (StatsProperty property : statsData) {
			switch (property) {
			case Entropy:
				property.setValue(Entropie);
				break;
			case Level:
				property.setValueInPercent(level);
				break;
			case Maximum:
				property.setValue(B);
				break;
			case Mean:
				property.setValue(mean);
				break;
			case Median:
				property.setValue(median);
				break;
			case Minimum:
				property.setValue(A);
				break;
			case Variance:
				property.setValue(variance);
				break;
			}
		}

	}

	public void draw() {
		gc.clearRect(0, 0, grayLevels, maxHeight);
		gc.setLineWidth(1);
		gc.setStroke(Color.CORAL);

		// TODO: draw histogram[] into the gc graphic context
		double shift = 0.5;
		int[] copyArray = histogram.clone();
		Arrays.sort(copyArray);
		double s = (double) maxHeight / copyArray[copyArray.length - 1];
		for (int i = 0; i < histogram.length; i++) {
			gc.strokeLine(i + shift, maxHeight + shift, i + shift, (maxHeight + shift) - (s * histogram[i]));
		}

	}

}
