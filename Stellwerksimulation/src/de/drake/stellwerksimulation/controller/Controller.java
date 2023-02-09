package de.drake.stellwerksimulation.controller;

import java.util.concurrent.ConcurrentLinkedQueue;

import de.drake.stellwerksimulation.model.BetriebsstelleFuerGUI;
import de.drake.stellwerksimulation.model.Importer;
import de.drake.stellwerksimulation.model.Stellwerk;
import de.drake.stellwerksimulation.view.GUI;

/**
 * Controller der Stellwerksimulation. Hier laufen Spielaktionen zusammen
 * und werden entsprechend weiterverarbeitet.
 * 
 * @ToDo:
 * - Wenden an Betriebsstellen ohne Bahnsteig verbieten (Kurswagen ausgenommen)
 * - Betriebsstellen mit mehr als zwei Strecken ermöglichen
 * - Highscore
 */
public class Controller {
	
	/**
	 * Die Instanz des Controllers.
	 */
	private static Controller instance; 
	
	/**
	 * Der Gameloop verarbeitet sowohl die Trasseneinstellung als auch das
	 * Fortschreiten der Spielzeit. Hierbei werden in fest definierten Intervallen
	 * Eingaben geprüft und verarbeitet. Die Ausführung des Gameloops erfolgt in einem
	 * eigenen Thread.
	 */
	private GameLoop gameLoop;
	
	/**
	 * Liste aller neu einzustellenden oder aufzuhebender Fahrwege. Wird regelmäßig
	 * abgearbeitet. Die Jobliste ist notwendig, um Kollisionen mit dem Model
	 * auszuschließen.
	 */
	private ConcurrentLinkedQueue<Fahrwegsjob> jobliste =
			new ConcurrentLinkedQueue<Fahrwegsjob>();
	
	/**
	 * Main-Methode; Startet das Programm.
	 */
	public static void main(String[] args) {
		new Controller();
	}
	
	/**
	 * Initialisiert den Controller, Stellwerk und GUI und startet das Spiel.
	 */
	private Controller() {
		Controller.instance = this;
		this.gameLoop = new GameLoop();
		Importer.importiereKonfig();
		this.gameLoop.start();
	}
	
	/**
	 * Wird in regelmäßigen Abständen ausgeführt und lässt die Züge in den nächsten
	 * Abschnitt vorfahren. 
	 */
	void zeitVor() {
		Stellwerk.getInstance().zeitVor();
		GUI.getInstance().repaint();
	}
	
	/**
	 * Pausiert das Spiel bzw. setzt nach einer Pause fort.
	 * 
	 * @param pause
	 * 		gibt an, ob pausiert werden soll (true) oder fortgesetzt werden soll (false)
	 */
	public void setPause(final boolean pause) {
		if (pause) {
			this.gameLoop.pause();
		} else {
			this.gameLoop.setzeFort();
		}
	}

	/**
	 * Legt einen Job zum Einstellen eines neuen Fahrweges an.
	 * 
	 * @param von
	 * 		Ausgangspunkt des neuen Fahrweges.
	 * @param nach
	 * 		Endpunkt des neuen Fahrweges.
	 */
	public void neuerFahrweg(final BetriebsstelleFuerGUI von,
			final BetriebsstelleFuerGUI nach) {
		this.jobliste.add(new Fahrwegsjob(true, von, nach));
	}
	
	/**
	 * Legt einen Job zum Einstellen eines neuen Fahrweges an.
	 * 
	 * @param von
	 * 		Ausgangspunkt des neuen Fahrweges.
	 * @param nach
	 * 		Endpunkt des neuen Fahrweges.
	 */
	public void loeseFahrwegAuf(final BetriebsstelleFuerGUI von,
			final BetriebsstelleFuerGUI nach) {
		this.jobliste.add(new Fahrwegsjob(false, von, nach));
	}
	
	/**
	 * Arbeitet alle Jobs zum Einstellen oder Auflösen neuer Fahrwege ab.
	 */
	void verarbeiteJobs() {
		Stellwerk stellwerk = Stellwerk.getInstance();
		Fahrwegsjob job;
		boolean jobsVorhanden = false;
		while (!this.jobliste.isEmpty()) {
			jobsVorhanden = true;
			job = this.jobliste.poll();
			if (job.neuEinstellen) {
				stellwerk.stelleFahrwegEin(job.von, job.nach);
			} else {
				stellwerk.loeseFahrwegAuf(job.von, job.nach);
			}
		}
		if (jobsVorhanden)
			GUI.getInstance().repaint();
	}

	/**
	 * Modifiziert die Spielgeschwindigkeit.
	 * 
	 * @param geschwindigkeit
	 * 		Die Geschwindigkeit der Simulation (Anzahl Bewegungen pro Minute)
	 */
	public void setGeschwindigkeit(final int geschwindigkeit) {
		this.gameLoop.setBewegungenProMinute(geschwindigkeit);
	}
	
	/**
	 * Gibt die derzeit eingestellte Spielgeschwindigkeit zurück (in Bewegungen pro Minute)
	 */
	public int getGeschwindigkeit() {
		return this.gameLoop.getBewegungenProMinute();
	}
	
	/**
	 * Gibt die Instanz des Controllers zurück.
	 */
	public static Controller getInstance() {
		return Controller.instance;
	}
}