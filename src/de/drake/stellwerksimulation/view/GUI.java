package de.drake.stellwerksimulation.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import de.drake.stellwerksimulation.controller.Controller;
import de.drake.stellwerksimulation.model.Stellwerk;

/**
 * Die GUI der Stellwerksimulation. Bildet sowohl Anlaufstelle für Grafikbefehle und
 * ist selbst Hauptfenster der GUI.
 */
public class GUI extends JFrame {
	
	/**
	 * Die Instanz der GUI.
	 */
	private static GUI guiInstance;
	
	/**
	 * Das Panel, in dem Pausezustand, Zeit u.ä. angezeigt werden.
	 */
	Kontrollpanel kontrollpanel;
	
	/**
	 * Gibt an, ob das Spiel derzeit pausiert ist.
	 */
	private boolean isPaused = true;
	
	/**
	 * Die serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Konstruktor zum Erzeugen eines GUI-Fensters mit den übergebenen Anzeigeparametern.
	 * 
	 * @param streckenbreite
	 * 		Die Breite der Strecken in Pixeln
	 * @param betriebsstellenbreite
	 * 		Die Breite der Betriebsstellen in Pixeln
	 * @param brueckenbreiteOben
	 * 		Die Breite einer Brückenüberführung in Pixeln
	 * @param brueckenbreiteUnten
	 * 		Die Breite einer Brückenunterführung in Pixeln
	 * @param schriftgroesseNetz
	 * 		Die Schriftgröße, die zur Beschriftung im Netzpanel verwendet wird
	 */
	private GUI(final int streckenbreite, final int betriebsstellenbreite,
			final int brueckenbreiteOben, final int brueckenbreiteUnten,
			final int schriftgroesseNetz) {
		super("Stellwerksimulation - " + Stellwerk.getInstance().getBahnhofsname());
		GUI.guiInstance = this;
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setSize(800,600);
		this.setLayout(new BorderLayout());
		
		this.kontrollpanel = new Kontrollpanel();
		this.add(this.kontrollpanel, BorderLayout.NORTH);
		
		this.add(new Netzpanel(streckenbreite, betriebsstellenbreite,
				brueckenbreiteOben, brueckenbreiteUnten, schriftgroesseNetz),
				BorderLayout.CENTER);
		
		Fahrplanpanel fahrplanpanel = new Fahrplanpanel();
		fahrplanpanel.setPreferredSize(new Dimension(0,250));
		this.add(fahrplanpanel, BorderLayout.SOUTH);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		fahrplanpanel.lockSize();
		this.kontrollpanel.lockSize();
		
		//Hotkeys definieren
		JPanel hauptPanel = (JPanel) this.getContentPane();
		hauptPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke("F2"), "togglePause");
		hauptPanel.getActionMap().put("togglePause", new PauseListener());
	}
	
	/**
	 * Erzeugt ein GUI-Fenster mit den übergebenen Anzeigeparametern.
	 * 
	 * @param streckenbreite
	 * 		Die Breite der Strecken in Pixeln
	 * @param betriebsstellenbreite
	 * 		Die Breite der Betriebsstellen in Pixeln
	 * @param brueckenbreiteOben
	 * 		Die Breite einer Brückenüberführung in Pixeln
	 * @param brueckenbreiteUnten
	 * 		Die Breite einer Brückenunterführung in Pixeln
	 * @param schriftgroesseNetz
	 * 		Die Schriftgröße, die zur Beschriftung im Netzpanel verwendet wird
	 */
	public static void createInstance(final int streckenbreite,
			final int betriebsstellenbreite, final int brueckenbreiteUnten,
			final int brueckenbreiteOben, final int schriftgroesseNetz) {
		new GUI(streckenbreite, betriebsstellenbreite, brueckenbreiteUnten,
				brueckenbreiteOben, schriftgroesseNetz);
	}
	
	/**
	 * Wechselt zwischen Pause und nicht Pause hin- und her.
	 */
	void togglePause() {
		if (this.isPaused) {
			this.isPaused = false;
			this.kontrollpanel.setPause(false);
			Controller.getInstance().setPause(false);
		} else {
			this.isPaused = true;
			this.kontrollpanel.setPause(true);
			Controller.getInstance().setPause(true);
		}
	}
	
	/**
	 * Gibt die Instanz der GUI zurück.
	 */
	public static GUI getInstance() {
		return GUI.guiInstance;
	}
}

/**
 * Listener, der überwacht ob "Pause" gedrückt wurde.
 */
class PauseListener extends AbstractAction implements ActionListener {
	
	/**
	 * Die serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Wird ausgelöst, wenn der "Pause" gedrückt wird.
	 */
	public void actionPerformed(ActionEvent e) {
		GUI.getInstance().togglePause();
	}
}