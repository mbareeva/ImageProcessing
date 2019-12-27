// BV Ue4 WS2019/20 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-16

package bv_ws1920;

import bv_ws1920.ImageAnalysisAppController.StatsProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ToneCurve {
	
	private static final int grayLevels = 256;
	
    private GraphicsContext gc;
    
    private int brightness = 0;
    private double gamma = 1.0;
    private double contrast = 0;
    
    private int[] grayTable = new int[grayLevels];

	public ToneCurve(GraphicsContext gc) {
		this.gc = gc;
	}
	
	public void setBrightness(int brightness) {
		this.brightness = brightness;
		updateTable();
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
		updateTable();
	}
	
	public void setContrast(double contrast) {
		this.contrast = contrast;
		updateTable();
	}
	private void updateTable() {
		// TODO: Fill the grayTable[] array to map gray input values to gray output values.
		// It will be used as follows: grayOut = grayTable[grayIn].
		
		int max = 0, min = 0;
		for (int i = 0; i < grayTable.length; i++) {
			int yIn = (int) (contrast * (i + brightness - 128) + 128);
			int grayOut = (int) ((255 * (Math.pow(yIn, (1/gamma)))) / (Math.pow(255, (1/gamma))));
			if(grayOut > 255) {
					grayOut = 255;
				}
			else if(grayOut < 0) {
					grayOut = 0;
				}
			grayTable[i] = grayOut;
		}
	}
	
	public int mappedGray(int inputGray) {
		return grayTable[inputGray];
	}
	
	public void draw() {
		gc.clearRect(0, 0, grayLevels, grayLevels);
		gc.setStroke(Color.BLUE);
		gc.setLineWidth(3);

		// TODO: draw the tone curve into the gc graphic context
		gc.beginPath();
		gc.moveTo(0, 255 - grayTable[0]);
		for(int i = 0; i < grayTable.length; i++) {
			gc.lineTo(i, 255 - grayTable[i]);
		}
		gc.stroke();
	
	}

	
}
