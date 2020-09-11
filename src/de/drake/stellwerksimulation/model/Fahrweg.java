package de.drake.stellwerksimulation.model;

import java.util.ArrayList;
import java.util.HashMap;

import de.drake.stellwerksimulation.tools.Richtung;

/**
 * Ein Fahrweg ist ein möglicher Weg durch das Streckennetz von einer AbstrakteBetriebsstelle
 * zu einer anderen. Ein Fahrweg kann frei sein (und damit eingestellt werden)
 * und ein eingestellter Fahrweg kann befahren werden (solange ist der Fahrweg
 * blockiert).
 */
class Fahrweg {

	/**
	 * Eine Liste aller Fahrwege der Simulation.
	 */
	private static ArrayList<Fahrweg> alleFahrwege = new ArrayList<Fahrweg>();
	
	/**
	 * Eine Liste aller Strecken, die zu diesem Fahrweg gehören.
	 */
	private ArrayList<Strecke> streckenliste;
	
	/**
	 * Die Fahrtrichtungen, in denen die Strecken durchfahren werden
	 */
	private HashMap<Strecke, Richtung> fahrtrichtungen;
	
	/**
	 * Eine Liste aller Betriebsstellen, die über diesen Fahrweg erreichbar sind.
	 */
	private ArrayList<Betriebsstelle> moeglicheFahrziele;
	
	/**
	 * Gibt an, ob der Weg zu einem potententiellen Fahrziel dieses Fahrweges über
	 * einen Bahnhof führt (d.h. dass noch Fahrgastwechsel möglich ist).
	 */
	private HashMap<Betriebsstelle, Boolean> fahrzielFuehrtUeberBahnhof;
	
	/**
	 * Gibt an, ob der Fahrweg derzeit eingestellt ist und befahren werden kann.
	 */
	private boolean istEingestellt = false;
	
	/**
	 * Gibt an, welcher Zug derzeit diesen Fahrweg nutzt. Wird der Fahrweg derzeit
	 * nicht befahren, so wird null hinterlegt.
	 */
	private Zug diesenFahrwegNutzenderZug = null;

	/**
	 * Legt einen neuen Fahrweg an.
	 * 
	 * @param streckenliste
	 * 		Eine Liste aller Strecken, die zu diesem Fahrweg gehören.
	 * @param fahrtrichtungen
	 * 		Die Fahrtrichtungen, in denen die Strecken durchfahren werden
	 * @param moeglicheFahrziele
	 * 		Eine Liste aller Betriebsstellen, die über diesen Fahrweg erreichbar sind.
	 * @param fahrzielFuehrtUeberBahnhof
	 * 		Gibt an, ob der Weg zu einem potententiellen Fahrziel dieses Fahrweges über
	 * 		einen Bahnhof führt (d.h. dass noch Fahrgastwechsel möglich ist).
	 */
	Fahrweg(final ArrayList<Strecke> streckenliste,
			final HashMap<Strecke, Richtung> fahrtrichtungen,
			final ArrayList<Betriebsstelle> moeglicheFahrziele,
			final HashMap<Betriebsstelle, Boolean> fahrzielFuehrtUeberBahnhof) {
		Fahrweg.alleFahrwege.add(this);
		this.streckenliste = streckenliste;
		this.fahrtrichtungen = fahrtrichtungen;
		this.moeglicheFahrziele = moeglicheFahrziele;
		this.fahrzielFuehrtUeberBahnhof = fahrzielFuehrtUeberBahnhof;
	}
	
	/**
	 * Gibt an, ob der Fahrweg eingestellt ist und befahren werden kann.
	 * 
	 * @return true, wenn eingestellt
	 */
	boolean istEingestellt() {
		return this.istEingestellt;
	}
	
	/**
	 * Stellt den Fahrweg ein, sofern dieser frei ist.
	 */
	void stelleEin() {
		if (!this.istEinstellbar())
			return;
		this.istEingestellt = true;
		for (int i = 0; i < this.streckenliste.size(); i++) {
			this.streckenliste.get(i).stelleFahrwegEin(
					this.getDurchfahrtsrichtungen(this.streckenliste.get(i)));
		}
		this.getEndeDesFahrweges().addEingehendenFahrweg(this);
	}
	
	/**
	 * Ermittelt die Durchfahrtsrichtungen zu einer Strecke.
	 * 
	 * @param strecke
	 * 		Die Strecke, für die die Durchfahrtsrichtungen ermittelt werden sollen.
	 */
	private ArrayList<Richtung> getDurchfahrtsrichtungen(final Strecke strecke) {
		if (strecke.equals(this.streckenliste.get(0)) ||
				strecke.equals(this.streckenliste.get(this.streckenliste.size()-1))) {
			return strecke.getAnschlussrichtungen();
		}
		ArrayList<Richtung> durchfahrtsrichtungen = new ArrayList<Richtung>(2);
		durchfahrtsrichtungen.add(this.fahrtrichtungen.get(strecke));
		int aktuellerStreckenindex = this.streckenliste.lastIndexOf(strecke);
		durchfahrtsrichtungen.add(this.fahrtrichtungen.get(
				this.streckenliste.get(aktuellerStreckenindex-1)).getGegenrichtung());
		return durchfahrtsrichtungen;
	}
	
	/**
	 * Löst einen eingestellten Fahrweg auf, sofern möglich.
	 */
	void loeseAuf() {
		if (!this.istAufloesbar())
			return;
		this.istEingestellt = false;
		for (int i = 0; i < this.streckenliste.size(); i++) {
			this.streckenliste.get(i).nehmeFahrwegZurueck();
		}
	}
	
	/**
	 * Markiert den Fahrweg als befahren.
	 * 
	 * @param zug 
	 * 		Der Zug, der diesen Fahrweg befahren soll.
	 */
	void befahreDurch(final Zug zug) {
		this.diesenFahrwegNutzenderZug = zug;
		this.istEingestellt = false;
	}
	
	/**
	 * Gibt einen befahrenen Fahrweg wieder frei, nachdem er von einem Zug
	 * vollständig passert wurde.
	 */
	void zugHatFahrwegPassiert() {
		this.diesenFahrwegNutzenderZug = null;
	}
	
	/**
	 * Gibt zu einer Strecke des Fahrwegs den nachfolgenden Abschnitt aus.
	 * Ist die Strecke nicht Teil des Fahrwegs oder ist sie der letzte Abschnitt, 
	 * wird null zurückgegeben.
	 * 
	 * @param strecke
	 * 		Die Strecke, zu der der nachfolgende Streckenabschnitt gesucht
	 * 		ist. Wird null übergeben (d.h. der Zug fährt von außerhalb der Simulation
	 * 		auf diesen Fahrweg), so wird der erste Streckenabschnitt des Fahrweges
	 * 		zurückgegeben.
	 * 
	 * @return der nächste Streckenabschnitt
	 */
	Strecke getNaechstenStreckenabschnitt(final Strecke strecke) {
		if (strecke == null)
			return this.streckenliste.get(0);
		for (int i = 0; i < this.streckenliste.size()-1; i++) {
			if (strecke.equals(this.streckenliste.get(i)))
				return this.streckenliste.get(i+1);
		}
		return null;
	}
	
	/**
	 * Gibt zu einer Strecke des Fahrwegs die Fahrtrichtung des Fahrweges aus.
	 * Ist die Strecke nicht Teil des Fahrwegs, wird null zurückgegeben.
	 * 
	 * @param strecke
	 * 		Die Strecke, zu der die Fahrtrichtung gesucht ist.
	 */
	 Richtung getFahrtrichtung(final Strecke strecke) {
			return this.fahrtrichtungen.get(strecke);
	}
	
	/**
	 * Gibt die Betriebsstelle zurück, an der der Fahrweg endet.
	 * 
	 * @return die entsprechende AbstrakteBetriebsstelle.
	 */
	Betriebsstelle getEndeDesFahrweges() {
		return (Betriebsstelle)
				this.streckenliste.get(this.streckenliste.size()-1);
	}

	/**
	 * Gibt an, ob der Fahrweg derzeit befahren wird.
	 * 
	 * @return die entsprechende AbstrakteBetriebsstelle.
	 */
	boolean istBefahren() {
		if (this.diesenFahrwegNutzenderZug == null)
			return false;
		return true;
	}
	
	/**
	 * Gibt an, ob der Fahrweg derzeit eingestellt werden kann.
	 */
	boolean istEinstellbar() {
		for (int i = 1; i < this.streckenliste.size()-1; i++)
			if (!this.streckenliste.get(i).istFrei())
				return false;
		return true;
	}
	
	/**
	 * Gibt an, ob der Fahrweg derzeit aufgelöst werden kann.
	 */
	boolean istAufloesbar() {
		if (this.istEingestellt && !this.istBefahren())
			return true;
		return false;
	}
	
	/**
	 * Gibt eine Liste aller möglichen Fahrwege zurück.
	 */
	static ArrayList<Fahrweg> getAlleFahrwege() {
		return Fahrweg.alleFahrwege;
	}
	
	/**
	 * Gibt an, ob dieser Fahrweg von einem bestimmten Zug genutzt werden kann.
	 * @param zug
	 * 		Der Zug, der diesen Fahrweg befahren soll.
	 */
	boolean kannBefahrenWerdenVonZug(final Zug zug) {
		// Wenn der Zug derzeit rangiert wird...
		if (zug.rangiert())
			return true;
		//Wenn der Fahrweg nicht zu den Zugzielen führt...
		if (!this.moeglicheFahrziele.containsAll(zug.getZielBetriebsstellen()))
			return false;
		//Wenn der Fahrweg nicht zum Bahnsteig führt, aber der Zug noch
		//Passagiere abgeben muss...
		for (Betriebsstelle zielBetriebsstelle : zug.getZielBetriebsstellen()) {
			if (zug.hatFahrgastwechselErledigt())
				break;
			if (!this.fahrzielFuehrtUeberBahnhof.get(zielBetriebsstelle))
				return false;
		}
		//Wenn das Fahrziel keinen Bahnsteig hat, aber belegt ist (auch, wenn es
		//sich um Züge handelt, mit denen normalerweise ein Gleis geteilt wird!)
		if (!this.getEndeDesFahrweges().hatBahnsteig()
				&& this.getEndeDesFahrweges().istBefahren())
			return false;
		//Wenn das Fahrtwegende bereits durch Züge blockiert ist, mit weder
		//ein Gleis geteilt werden darf noch vereinigt werden soll...
		if (!zug.getZuegeSelbesGleisOderVereinigung().containsAll(
				this.getEndeDesFahrweges().getZuegeImGleis()))
			return false;
		// Wenn andere Züge unterwegs zum Fahrtwegeende sind, mit denen weder ein Gleis
		//geteilt werden darf noch vereinigt werden soll...
		for (Fahrweg fahrweg : this.getEndeDesFahrweges().getEingehendeFahrwege()) {
			if (!fahrweg.istBefahren())
				continue;
			if (!zug.getZuegeSelbesGleisOderVereinigung().contains(
					fahrweg.diesenFahrwegNutzenderZug))
				return false;
		}
		return true;
	}
}