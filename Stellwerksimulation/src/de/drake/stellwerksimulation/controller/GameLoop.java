package de.drake.stellwerksimulation.controller;

/**
 * Der Gameloop verarbeitet sowohl die Trasseneinstellung als auch das
 * Fortschreiten der Spielzeit. Hierbei werden in fest definierten Intervallen
 * Eingaben geprüft und verarbeitet. Die Ausführung des Gameloops erfolgt in einem
 * eigenen Thread.
 */
class GameLoop extends Thread {
	
	/**
	 * Framerate, die die Anzahl der Eingabenprüfungen pro Sekunde steuert
	 */
	private final static int FRAMERATE = 50;
	
	/**
	 * Die Anzahl aller Felder, die Züge pro Minute zurücklegen können.
	 */
	private int bewegungenProMinute;
	
	/**
	 * Speichert, ob das Spiel pausiert ist
	 */
	private boolean isPaused = true;
	
	/**
	 * Der Gameloop an sich; Wird beim Start des Gameloops ausgeführt.
	 */
	@Override
	public void run() {
		Controller controller = Controller.getInstance();
		long startzeit, vergangeneZeit, cyclestart, sleepzeit;
		startzeit = System.nanoTime();
		while(true) {
			cyclestart = System.nanoTime();
			controller.verarbeiteJobs();
			if (this.isPaused) {
				startzeit = System.nanoTime();
			} else {
				vergangeneZeit = System.nanoTime() - startzeit;
				if (vergangeneZeit > 1000000000L / (this.bewegungenProMinute / 60.)) {
					controller.zeitVor();
					startzeit = System.nanoTime();
					continue;
				}
			}
			sleepzeit = (1000000000L / GameLoop.FRAMERATE
					- (System.nanoTime() - cyclestart))/1000000;
			if (sleepzeit < 1)
				sleepzeit = 1;
		    try {
		    	Thread.sleep(sleepzeit);
		    } catch (InterruptedException ex) {
		    	System.out.println(ex);
		    }
		}
	}
	
	/**
	 * Pausiert den Gameloop
	 */
	void pause() {
		this.isPaused = true;
	}
	
	/**
	 * Setzt den Gameloop nach einer Pause fort.
	 */
	void setzeFort() {
		this.isPaused = false;
	}
	
	/**
	 * Modifiziert die Spielgeschwindigkeit.
	 * 
	 * @param geschwindigkeit
	 * 		Die Geschwindigkeit der Simulation (Anzahl Bewegungen pro Minute)
	 */
	void setBewegungenProMinute(final int geschwindigkeit) {
		this.bewegungenProMinute = geschwindigkeit;
	}
	
	/**
	 * Gibt die derzeitige Spielgeschwindigkeit zurück.
	 */
	int getBewegungenProMinute() {
		return this.bewegungenProMinute;
	}
}