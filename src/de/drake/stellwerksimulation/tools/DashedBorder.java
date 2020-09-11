package de.drake.stellwerksimulation.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.border.LineBorder;

/**
 * Eine gepunktete Border zur Verwendung in AWT/Swing.
 */
public class DashedBorder extends LineBorder {

	/**
	 * Die serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Erzeugt eine neue DashedBorder.
	 * 
	 * @param color
	 * 		Die Farbe der Border.
	 */
	public DashedBorder(final Color color) {
		super(color);
	}
	
	/**
	 * Zeichnet die Border (Wird normalerweise implizit ausgeführt).
	 */
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int
			width, int height) {
		BasicStroke gestrichelt = new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 1, new float[]{1, 1}, 0);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setStroke(gestrichelt);
		super.paintBorder(c, g2d, x, y, width, height);
		g2d.dispose();
	}
}