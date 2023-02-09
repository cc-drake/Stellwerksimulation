package de.drake.stellwerksimulation.controller;

import de.drake.stellwerksimulation.model.BetriebsstelleFuerGUI;

/**
 * Ein Fahrwegsjob dient zum Einstellen oder Aufheben eines Fahrweges.
 */
class Fahrwegsjob {
	
	/**
	 * Beschreibt, ob der Job einen neuen Fahrweg einstellen soll oder einen Fahrweg
	 * aufl�sen.
	 * 
	 * @return
	 * 		true, wenn ein neuer Fahrweg eingestellt werden soll, zum Aufl�sen
	 * 		dagegen false.
	 */
	boolean neuEinstellen;
	
	/**
	 * Ausgangspunkt des zu bearbeitenden Fahrweges.
	 */
	BetriebsstelleFuerGUI von;
	
	/**
	 * Endpunkt des zu bearbeitenden Fahrweges.
	 */
	BetriebsstelleFuerGUI nach;
	
	/**
	 * Erzeugt einen neuen Fahrwegsjob.
	 * 
	 * @param neuEinstellen
	 * 		Beschreibt, ob der Job einen neuen Fahrweg einstellen soll (true) oder
	 * 		einen Fahrweg aufl�sen soll (false)
	 * @param von
	 * 		Ausgangspunkt des zu bearbeitenden Fahrweges.
	 * @param nach
	 * 		Endpunkt des zu bearbeitenden Fahrweges.
	 */
	Fahrwegsjob(final boolean neuEinstellen, final BetriebsstelleFuerGUI von,
			final BetriebsstelleFuerGUI nach) {
		this.neuEinstellen = neuEinstellen;
		this.von = von;
		this.nach = nach;
	}
}
