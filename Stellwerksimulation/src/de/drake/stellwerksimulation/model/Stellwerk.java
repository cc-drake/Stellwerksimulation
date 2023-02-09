package de.drake.stellwerksimulation.model;

import java.util.ArrayList;
import java.util.Collections;

import de.drake.stellwerksimulation.tools.Zeit;

/**
 * Das Stellwerk der Stellwerksimulation. Ein Stellwerk verwaltet Strecken und
 * Fahrwege und koordiniert die Z�ge.
 */
public class Stellwerk {
	
	/**
	 * Die Instanz des Stellwerks.
	 */
	private static Stellwerk instance;
	
	/**
	 * Die aktuelle Zeit. Wird vom Controller jeweils aktualisiert.
	 */
	private Zeit aktuelleZeit;
	
	/**
	 * Die Anzahl der Ingamesekunden, die die Z�ge f�r eine Bewegung ben�tigen.
	 */
	private int sekundenProZugbewegung;
	
	/**
	 * Der Name des simulierten Bahnhofs.
	 */
	private String bahnhofsname;
	
	/**
	 * Der Anteil an versp�teten Z�gen in Prozent.
	 */
	private int verspaetungsanteil;
	
	/**
	 * Die maximale Versp�tung, die bei Z�gen m�glich ist. Angabe in Minuten.
	 * Gr��ere Versp�tungen sind dabei m�glich, wenn die Einfahrt des versp�teten
	 * Zuges nicht m�glich ist.
	 */
	private int verspaetungMax;
	
	/**
	 * Erzeugt ein neues Stellwerk.
	 * 
	 * @param bahnhofsname
	 * 		Der Name des simulierten Bahnhofs.
	 * 
	 * @param startzeit
	 * 		Die In-Game-Startzeit der Simulation.
	 * 
	 * @param verspaetungsanteil
	 * 		Der Anteil an versp�teten Z�gen in Prozent.
	 * 
	 * @param verspaetungMax
	 * 		Die maximale Versp�tung, die bei Z�gen m�glich ist. Angabe in Minuten.
	 * 		Gr��ere Versp�tungen sind dabei m�glich, wenn die Einfahrt des versp�teten
	 * 		Zuges nicht m�glich ist.
	 * 
	 * @param sekundenProZugbewegung
	 * 		Die Anzahl der Ingamesekunden, die die Z�ge f�r eine Bewegung ben�tigen.
	 */
	private Stellwerk(final String bahnhofsname, final Zeit startzeit,
			final int verspaetungsanteil, final int verspaetungMax,
			final int sekundenProZugbewegung) {
		Stellwerk.instance = this;
		this.bahnhofsname = bahnhofsname;
		this.aktuelleZeit = startzeit;
		this.verspaetungsanteil = verspaetungsanteil;
		this.verspaetungMax = verspaetungMax;
		this.sekundenProZugbewegung = sekundenProZugbewegung;
		Importer.importiereStreckennetz();
		Importer.importiereFahrwege();
		Importer.importiereFahrplan();
		Zug.erzeugeZugvereinigungen();
		this.erzeugeVerspaetungen(this.verspaetungsanteil,
				this.verspaetungMax);
		for (Zug zug : Zug.getZuegeInSimulation()) {
			zug.getStartBetriebsstelle().addErwartetenZug(zug);
		}
	}

	/**
	 * Initialisiert die Instanz des Stellwerks mit den �bergebenen Spielparametern.
	 * 
	 * @param bahnhofsname
	 * 		Der Name des simulierten Bahnhofs.
	 * 
	 * @param startzeit
	 * 		Die In-Game-Startzeit der Simulation.
	 * 
	 * @param verspaetungsanteil
	 * 		Der Anteil an versp�teten Z�gen in Prozent.
	 * 
	 * @param verspaetungMax
	 * 		Die maximale Versp�tung, die bei Z�gen m�glich ist. Angabe in Minuten.
	 * 		Gr��ere Versp�tungen sind dabei m�glich, wenn die Einfahrt des versp�teten
	 * 		Zuges nicht m�glich ist.
	 * 
	 * @param sekundenProZugbewegung
	 * 		Die Anzahl der Ingamesekunden, die die Z�ge f�r eine Bewegung ben�tigen.
	 */
	static void createInstance(final String bahnhofsname, final Zeit startzeit,
			final int verspaetungsanteil, final int verspaetungMax,
			int sekundenProZugbewegung) {
		new Stellwerk(bahnhofsname, startzeit, verspaetungsanteil, verspaetungMax,
				sekundenProZugbewegung);
	}
	
	/**
	 * F�gt den eingelesenen Z�gen zuf�llig Versp�tungen nach vorgegebenen
	 * Parametern hinzu.
	 * 
	 * @param verspaetungsanteil
	 * 		Der Anteil der versp�teten Z�ge in Prozent
	 * @param verspaetungMax
	 * 		Die maximale Versp�tung, mit der ein Zug eintreffen kann, in Minuten.
	 * 		Die tats�chliche Versp�tung kann bei hoher Zugfolge noch
	 * 		geringf�gig h�her ausfallen!
	 */
	private void erzeugeVerspaetungen(final int verspaetungsanteil,
			final int verspaetungMax) {
		// Zuf�llige Versp�tungen erzeugen
		for (Zug zug : Zug.getZuegeInSimulation())
			if (Math.random() < verspaetungsanteil/100.)
				zug.addVerspaetung((int) Math.ceil(Math.random()
						* verspaetungMax));
		// Einfahrten in den einzelnen Herkunftsbetriebsstellen entzerren:
		for (Betriebsstelle betriebsstelle :
				Betriebsstelle.getAlleBetriebsstellen()) {
			//F�r jede Betriebsstelle ankommende Z�ge ermitteln
			ArrayList<Zug> ankommendeZuege = new ArrayList<Zug>();
			for (Zug zug : Zug.getZuegeInSimulation()) {
				if (zug.getStartBetriebsstelle().equals(betriebsstelle))
					ankommendeZuege.add(zug);
			}
			Collections.sort(ankommendeZuege);
			//Und entzerren
			for (int index = 1; index < ankommendeZuege.size(); index++) {
				int zeitdifferenz = Zeit.getZeitdifferenzInMinuten(
						ankommendeZuege.get(index-1).getTatsaechlicheAnkunft(),
						ankommendeZuege.get(index).getTatsaechlicheAnkunft());
				if (zeitdifferenz < 3) {
					ankommendeZuege.get(index).addVerspaetung(3 - zeitdifferenz);
					Collections.sort(ankommendeZuege);
					index--;
				}
			}
		}
		Zug.sortiereZugliste();
	}

	/**
	 * L�sst etwas Zeit vergehen, so dass die Z�ge ein Feld weiter fahren.
	 */
	public void zeitVor() {
		this.aktuelleZeit.addSekunden(this.sekundenProZugbewegung);
		for (Betriebsstelle betriebsstelle :
				Betriebsstelle.getAlleBetriebsstellen()) {
			betriebsstelle.verarbeiteZuegeImGleis();
		}
		for (Zug zug : new ArrayList<Zug>(Zug.getZuegeInSimulation())) {
			if (!zug.getTatsaechlicheAnkunft().istFrueherOderZeitgleichAls(
					Stellwerk.getInstance().getAktuelleZeit()))
				break;
			if (!zug.faehrt())
				continue;
			zug.fahre();
		}
	}

	/**
	 * Gibt die aktuelle Zeit der Simulation zur�ck.
	 */
	public Zeit getAktuelleZeit() {
		return this.aktuelleZeit;
	}
	
	/**
	 * Gibt die aktuelle Zeit der Simulation zur�ck.
	 */
	public String getBahnhofsname() {
		return this.bahnhofsname;
	}
	
	/**
	 * Stellt einen bestimmten Fahrweg ein.
	 * 
	 * @param von
	 * 		Ausgangspunkt des einzustellenden Fahrweges.
	 * @param nach
	 * 		Endpunkt des einzustellenden Fahrweges.
	 */
	public void stelleFahrwegEin(BetriebsstelleFuerGUI von,
			BetriebsstelleFuerGUI nach) {
		von.getAusgehendenFahrweg(nach).stelleEin();
	}
	
	/**
	 * L�st einen bestimmten Fahrweg auf.
	 * 
	 * @param von
	 * 		Ausgangspunkt des aufzul�senden Fahrweges.
	 * @param nach
	 * 		Endpunkt des aufzul�senden Fahrweges.
	 */
	public void loeseFahrwegAuf(BetriebsstelleFuerGUI von,
			BetriebsstelleFuerGUI nach) {
		von.getAusgehendenFahrweg(nach).loeseAuf();
	}
	
	/**
	 * Gibt die Anzahl der Ingamesekunden, die die Z�ge f�r eine Bewegung ben�tigen,
	 * zur�ck.
	 */
	int getSekundenProZugbewegung() {
		return this.sekundenProZugbewegung;
	}
	
	/**
	 * Gibt die Instanz des Stellwerkes zur�ck.
	 */
	public static Stellwerk getInstance() {
		return Stellwerk.instance;
	}
}