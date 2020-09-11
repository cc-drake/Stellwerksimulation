package de.drake.stellwerksimulation.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import de.drake.stellwerksimulation.tools.Richtung;
import de.drake.stellwerksimulation.tools.Zeit;

/**
 * Ein simulierter Zug.
 */
class Zug extends Fahrplaneintrag implements Comparable<Zug> {
	
	/**
	 * Eine Liste aller Züge, die im Fahrplanpanel dargestellt werden sollen.
	 */
	private static Vector<Zug> zuegeInSimulation
			= new Vector<Zug>();
	
	/**
	 * Eine Liste aller Züge, die derzeit mit anderen Zügen vereinigt fahren und daher
	 * nicht separat in der Liste "zuegeInSimulation" auftauchen.
	 */
	private static Vector<Zug> virtuelleZuege
			= new Vector<Zug>();
	
	/**
	 * Eine Zuordnung Zugnummer zu Zug, um Züge anhand ihrer Zugnummern finden zu können.
	 */
	private static HashMap<Integer, Zug> zugnummer2Zug
			= new HashMap<Integer, Zug>();
	
	/**
	 * Die Zuggattung des Zuges (z.B. ICE, RE)
	 */
	private String gattung;
	
	/**
	 * Die Zugnummer des Zuges
	 */
	private int zugnummer;
	
	/**
	 * Falls dieser Zug an einen anderen Zug gekoppelt in die Simulation einfährt, steht 
	 * hier die Zugnummer. Andernfalls ist 0 eingetragen.
	 */
	private int haengtAnZugnummer;
	
	/**
	 * Gibt an, ob der Zugteil am simulierten Bahnhof abgekoppelt wird
	 */
	private boolean fluegelt;
	
	/**
	 * Gibt an, in welche Richtung im Falle einer Flügelung der Zugteil abgetrennt wird.
	 */
	private boolean fluegeltNachVorne;
	
	/**
	 * Gibt an, welche Züge derzeit mit diesem Zug vereinigt sind.
	 */
	private ArrayList<Zug> vereinigteZuege = new ArrayList<Zug>(1);
	
	/**
	 * Gibt im Fall eines Gattungswechsels am simulierten Bahnhof die neue Gattung an.
	 * Findet kein Gattungswechsel statt, ist der Inhalt "".
	 */
	private String gattungNeu;
	
	/**
	 * Gibt im Fall eines Zugnummernwechsels am simulierten Bahnhof die neue
	 * Zugnummer an. Findet kein Zugnummernwechsel statt (bzw. nur ein Gattungswechsel),
	 * so ist 0 eingetragen.
	 */
	private int zugnummerNeu;
	
	/**
	 * Die Betriebsstelle, an der der Zug die Simulation betritt
	 */
	private Betriebsstelle startBetriebsstelle;
	
	/**
	 * Die Richtung, aus der die startBetriebsstelle erreicht wird
	 */
	private Richtung herkunftsrichtung;
	
	/**
	 * Die Betriebsstelle, an der der Zug die Simulation verlässt
	 */
	private Betriebsstelle zielBetriebsstelle;
	
	/**
	 * Planmäßige Ankunftszeit an der Start-Betriebsstelle
	 */
	private Zeit ankunft;
	
	/**
	 * Planmäßige Abfahrtszeit am Gleis
	 */
	private Zeit abfahrt;
		
	/**
	 * Die Zeit, die der Zug mindestens für den Fahrgastwechsel benötigt (ist im Fall
	 * von Verspätungen relevant)
	 */
	private int minimalerAufenthalt;
	
	/**
	 * Eine Liste aller Zugnummern, mit denen ein Gleis gemeinsam genutzt werden darf.
	 */
	private ArrayList<Integer> zugnummernSelbesGleis;
	
	/**
	 * Gibt an, wann der Zug nach Einfahrt in die Betriebsstelle rangiert werden soll.
	 * Wird nicht rangiert, so wird null eingetragen.
	 */
	private Zeit rangiertUm;
	
	/**
	 * Eine Liste aller Zugnummern, mit denen dieser Zug vereinigt werden kann
	 */
	private ArrayList<Integer> vereinigungMit;
	
	/**
	 * Gibt die Verspätung des Zuges in Minuten an.
	 */
	private int verspaetung = 0;
	
	/**
	 * Gibt die tatsächliche Abfahrtszeit in die zuletzt eingefahrene Betriebsstelle an.
	 */
	private Zeit tatsaechlicheAbfahrt;
	
	/**
	 * Gibt den vom Zug befahrenen Fahrweg an. Ist derzeit kein Fahrweg eingestellt,
	 * ist der Inhalt null.
	 */
	private Fahrweg befahrenerFahrweg = null;
	
	/**
	 * Gibt die aktuelle Position des Zuges an, auf der der Zug gezeichnet werden
	 * soll.
	 */
	private Strecke position = null;
	
	/**
	 * Gibt die aktuelle Fahrtrichtung des Zuges zurück.
	 */
	private Richtung fahrtrichtung = null;
	
	/**
	 * Gibt an, ob der Zug bereits Fahrgastwechsel am Bahnhof hatte
	 */
	private boolean fahrgastwechselErledigt = false;
	
	/**
	 * Legt einen neuen Fahrplaneintrag an.
	 * 
	 * @param gattung
	 * 		Die Zuggattung des Zuges (z.B. ICE, RE)
	 * @param zugnummer
	 * 		Die Zugnummer des Zuges
	 * @param haengtAnZugnummer
	 * 		Falls dieser Zug an einen anderen Zug gekoppelt in die Simulation einfährt, steht 
	 * 		hier die Zugnummer. Andernfalls ist 0 einzutragen.
	 * @param fluegelt
	 * 		Gibt an, ob der Zugteil am simulierten Bahnhof abgekoppelt wird
	 * @param fluegeltNachVorne
	 * 		Gibt an, in welche Richtung im Falle einer Flügelung der Zugteil abgetrennt wird.
	 * @param herkunftsbahnhof
	 * 		Herkunftsbahnhof (z.B. Hamburg-Altona) zur Anzeige im Fahrplanfenster
	 * @param startBetriebsstelle
	 * 		Die Betriebsstelle, an der der Zug die Simulation betritt
	 * @param herkunftsrichtung
	 * 		Die Richtung, aus der die startBetriebsstelle erreicht wird
	 * @param zielbahnhof
	 * 		Zielbahnhof (z.B. München Hbf) zur Anzeige im Fahrplanfenster
	 * @param zielBetriebsstelle
	 * 		Die Betriebsstelle, an der der Zug die Simulation verlässt
	 * @param viaGleis
	 * 		Die Betriebsstelle (also das Gleis), über das der Zug fahren sollte
	 * @param ankunft
	 * 		Planmäßige Ankunftszeit an der Start-Betriebsstelle
	 * @param abfahrt
	 * 		Planmäßige Abfahrtszeit am Gleis
	 * @param minimalerAufenthalt
	 * 		Die Zeit, die der Zug mindestens für den Fahrgastwechsel benötigt
	 * 		(ist im Fall von Verspätungen relevant)
	 * @param rangiertUm
	 * 		Gibt an, wann der Zug nach Einfahrt in die Betriebsstelle rangiert werden soll. 
	 * 		Wird nicht rangiert, so wird null eingetragen.
	 * @param gattungNeu
	 * 		Gibt im Fall eines Gattungswechsels am simulierten Bahnhof die neue
	 * 		Gattung an. Findet kein Gattungswechsel statt, ist null anzugeben.
	 * @param zugnummerNeu
	 * 		Gibt im Fall eines Zugnummernwechsels am simulierten Bahnhof die neue 
	 * 		Zugnummer an. Findet kein Zugnummernwechsel statt, ist 0 anzugeben.
	 * @param vereinigungMit
	 * 		Eine Liste aller Zugnummern, mit denen dieser Zug vereinigt werden kann
	 * @param zugnummernSelbesGleis
	 * 		Eine Liste aller Zugnummern, mit denen ein Gleis gemeinsam genutzt werden
	 * 		darf.
	 */
	Zug(final String gattung, final int zugnummer, final int haengtAnZugnummer,
			final boolean fluegelt, final boolean fluegeltNachVorne, 
			final String herkunftsbahnhof,
			final Betriebsstelle startBetriebsstelle,
			final Richtung herkunftsrichtung, final String zielbahnhof,
			final Betriebsstelle zielBetriebsstelle, final Betriebsstelle viaGleis,
			final Zeit ankunft,	final Zeit abfahrt,	final int minimalerAufenthalt,
			final Zeit rangiertUm, final String gattungNeu, final int zugnummerNeu,
			final ArrayList<Integer> vereinigungMit,
			final ArrayList<Integer> zugnummernSelbesGleis) {
		super(herkunftsbahnhof, zielbahnhof, viaGleis);
		Zug.zugnummer2Zug.put(zugnummer, this);
		if (this.zugnummerNeu != 0)
			Zug.zugnummer2Zug.put(zugnummerNeu, this);
		if (haengtAnZugnummer == 0) {
			Zug.zuegeInSimulation.add(this);
		} else {
			Zug.virtuelleZuege.add(this);
		}
		this.gattung = gattung;
		this.zugnummer = zugnummer;
		this.haengtAnZugnummer = haengtAnZugnummer;
		this.fluegelt = fluegelt;
		this.fluegeltNachVorne = fluegeltNachVorne;
		this.gattungNeu = gattungNeu;
		this.zugnummerNeu = zugnummerNeu;
		this.startBetriebsstelle = startBetriebsstelle;
		this.herkunftsrichtung = herkunftsrichtung;
		this.zielBetriebsstelle = zielBetriebsstelle;
		this.ankunft = ankunft;
		this.abfahrt = abfahrt;
		this.minimalerAufenthalt = minimalerAufenthalt;
		if (rangiertUm == null || abfahrt.istFrueherOderZeitgleichAls(rangiertUm)) {
			this.rangiertUm = null;
		} else {
			this.rangiertUm = rangiertUm;
		}
		this.vereinigungMit = vereinigungMit;
		this.zugnummernSelbesGleis = zugnummernSelbesGleis;
		//Güterzug erkennen
		if (viaGleis == null)
			this.fahrgastwechselErledigt = true;
	}

	/**
	 * Fügt dem Zug Verspätung hinzu.
	 * 
	 * @param verspaetung 
	 * 		Die zusätzliche Verspätung in Minuten.
	 */
	void addVerspaetung(final int verspaetung) {
		this.verspaetung += verspaetung;
	}

	/**
	 * Gibt die tatsächliche Ankunftszeit an der start-Betriebsstelle der Simulation
	 * an, d.h. inklusive von Verspätung.
	 * 
	 * @return Die tatsächliche Ankunftszeit.
	 */
	Zeit getTatsaechlicheAnkunft() {
		Zeit tatsaechlicheAnkunft = new Zeit(this.ankunft);
		tatsaechlicheAnkunft.addMinuten(this.verspaetung);
		return tatsaechlicheAnkunft;
	}
	
	/**
	 * Berechnet die neue Abfahrtszeit an der aktuellen Position des Zuges.
	 * 
	 * @param mitFahrgastwechsel
	 * 		Gibt an, ob bei der Bestimmung der Abfahrtszeit Zeit für Fahrgastwechsel
	 * 		eingeplant werden soll.
	 */
	void aktualisiereAbfahrtszeit(final boolean mitFahrgastwechsel) {
		this.tatsaechlicheAbfahrt = new Zeit(Stellwerk.getInstance()
				.getAktuelleZeit());
		if (mitFahrgastwechsel == false) {
			this.tatsaechlicheAbfahrt.addSekunden(Stellwerk.getInstance()
					.getSekundenProZugbewegung());
			return;
		}
		this.tatsaechlicheAbfahrt.addMinuten(this.minimalerAufenthalt);
		if (this.rangiert()) {
			return;
		}
		if (this.rangierenNotwendig()) {
			if (this.tatsaechlicheAbfahrt.istFrueherOderZeitgleichAls(this.rangiertUm)) {
				this.tatsaechlicheAbfahrt = this.rangiertUm;
			}
			return;
		}
		if (this.tatsaechlicheAbfahrt.istFrueherOderZeitgleichAls(this.abfahrt))
				this.tatsaechlicheAbfahrt = this.abfahrt;
	}
	
	/**
	 * Gibt die voraussichtliche Abfahrtszeit an der aktuellen Betriebsstelle an
	 * 
	 * @return Die tatsächliche Ankunftszeit.
	 */
	Zeit getTatsaechlicheAbfahrt() {
		return this.tatsaechlicheAbfahrt;
	}
	
	/**
	 * Gibt die Betriebsstelle zurück, an der der Zug in die Simulation einfährt.
	 * 
	 * @return Die entsprechende Betriebsstelle
	 */
	Betriebsstelle getStartBetriebsstelle() {
		return this.startBetriebsstelle;
	}

	/**
	 * Gibt eine Liste aller Ziel-Betriebsstellen des Zugverbands zurück.
	 */
	ArrayList<Betriebsstelle> getZielBetriebsstellen() {
		ArrayList<Betriebsstelle> result = new ArrayList<Betriebsstelle>(3);
		result.add(this.zielBetriebsstelle);
		for (Zug fluegelzug : this.vereinigteZuege) {
			if (!result.contains(fluegelzug.zielBetriebsstelle))
				result.add(fluegelzug.zielBetriebsstelle);
		}
		return result;
	}
	
	/**
	 * Gibt die Ziel-Betriebsstelle dieses Zuges zurück, d.h. die Betriebsstelle, über
	 * die der Zug die Simulation verlässt.
	 */
	BetriebsstelleFuerGUI getZielBetriebsstelle() {
		return this.zielBetriebsstelle;
	}

	/**
	 * Vergleichsmethode, nach der Zuglisten sortiert werden. Der Vergleich läuft so,
	 * dass zunächst nach der tatsächlichen Ankunftszeit in der
	 * Simulation sortiert wird. Stimmt die tatsächliche Ankunftszeit von zwei
	 * Zügen überein, so haben stark verspätete Züge Vorrang
	 * (diese bremsen sozusagen die pünktlichen Züge aus).
	 * 
	 * @param zug
	 * 		Der Zug, mit dem verglichen werden soll
	 * 		
	 * @returns ein Vergleichswert, der (jenachdem ob >0 oder <0) die Relation
	 * 		festlegt
	 */
	public int compareTo(final Zug zug) {
		int zeitdifferenz = 
				this.getTatsaechlicheAnkunft().compareTo(zug.getTatsaechlicheAnkunft());
		if (zeitdifferenz != 0)
			return zeitdifferenz;
		return -(this.ankunft.compareTo(zug.ankunft));
	}
	
	/**
	 * Stellt für den Zug einen Fahrweg ein.
	 * 
	 * @param fahrweg
	 * 		Der gewünschte Fahrweg
	 */
	void setFahrweg(final Fahrweg fahrweg) {
		this.befahrenerFahrweg = fahrweg;
		this.fahrtrichtung = fahrweg.getFahrtrichtung(this.position);
		if (this.fahrtrichtung == null)
			this.fahrtrichtung = this.herkunftsrichtung.getGegenrichtung();
	}
	
	/**
	 * Gibt an, ob Zug gerade fährt oder nicht. Fahren bedeutet, dass der Zug sich
	 * in der Simulation befindet und einen eingestellten Fahrweg besitzt.
	 * 
	 * @return true, wenn der Zug fährt.
	 */
	boolean faehrt() {
		if (this.befahrenerFahrweg == null)
			return false;
		return true;
	}
	
	/**
	 * Gibt an, ob der Zugteil am simulierten Bahnhof abgekoppelt wird.
	 */
	boolean fluegelt() {
		return this.fluegelt;
	}
	
	/**
	 * Gibt an, in welche Richtung im Falle einer Flügelung der Zugteil abgetrennt wird.
	 */
	boolean fluegeltNachVorne() {
		return this.fluegeltNachVorne;
	}
	
	/**
	 * Gibt zurück, ob der Zug derzeit auf freie Fahrt wartet.
	 */
	boolean wartet() {
		if (this.faehrt())
			return false;
		if (this.istInSimulation() &&
				this.getTatsaechlicheAbfahrt().istFrueherOderZeitgleichAls(
				Stellwerk.getInstance().getAktuelleZeit()))
			return true;
		if (!this.istInSimulation() &&
				this.getTatsaechlicheAnkunft().istFrueherOderZeitgleichAls(
				Stellwerk.getInstance().getAktuelleZeit()))
			return true;
		return false;
	}

	/**
	 * Lässt den Zug in den nächsten Streckenabschnitt vorfahren.
	 */
	void fahre() {
		if (this.position != null)
			this.position.verarbeiteAbfahrendenZug(this);
		Betriebsstelle fahrtwegEnde =
				this.befahrenerFahrweg.getEndeDesFahrweges();
		this.position = 
				this.befahrenerFahrweg.getNaechstenStreckenabschnitt(this.position);
		if (this.position.equals(fahrtwegEnde)) {
			this.befahrenerFahrweg.zugHatFahrwegPassiert();
			this.befahrenerFahrweg = null;
			this.position.verarbeiteAnkommendenZug(this,
					this.fahrtrichtung.getGegenrichtung());
		} else {
			this.position.verarbeiteAnkommendenZug(this,
				this.fahrtrichtung.getGegenrichtung());
			this.fahrtrichtung = this.befahrenerFahrweg.getFahrtrichtung(this.position);
		}
	}
	
	/**
	 * Löst diesen Zug von seinem Stammzug.
	 */
	void fluegle() {
		Zug stammzug = Zug.zugnummer2Zug.get(this.haengtAnZugnummer);
		this.position = stammzug.position;
		this.fahrtrichtung = stammzug.fahrtrichtung;
		this.fluegelt = false;
		this.haengtAnZugnummer = 0;
		stammzug.vereinigteZuege.remove(this);
		Zug.zuegeInSimulation.add(Zug.zuegeInSimulation.indexOf(stammzug)+1, this);
		this.position.verarbeiteAnkommendenZug(this, this.fahrtrichtung.getGegenrichtung());
	}
	
	/**
	 * Gibt an, ob der Zug erfolgreich die Simulation durchfahren hat.
	 */
	boolean hatZielErreicht() {
		if (this.fahrgastwechselErledigt
				&& this.position.equals(this.zielBetriebsstelle)
				&& !this.rangiert())
			return true;
		return false;
	}
	
	/**
	 * Setter für die Position des Zuges. Wird nur bei Einfahrt des Zuges in die
	 * Simulation ausgeführt.
	 * 
	 * @param position
	 * 		Die Position des Zuges.
	 */
	void setPosition(final Strecke position) {
		this.position = position;
	}
	
	/**
	 * Markiert den Fahrgastwechsel als durchgeführt.
	 */
	void setFahrgastwechselErledigt() {
		this.fahrgastwechselErledigt = true;
	}
	
	/**
	 * Gibt zurück, ob dieser Zug bereits den Fahrgastwechsel erledigt hat.
	 * Im Fall eines Güterzuges wird grundsätzlich true zurückgegeben.
	 */
	boolean hatFahrgastwechselErledigt() {
		return this.fahrgastwechselErledigt;
	}
	
	/**
	 * Gibt zurück, ob sich der Zug bereits in der Simulation befindet oder nicht.
	 */
	boolean istInSimulation() {
		if (this.position == null)
			return false;
		return true;
	}
	
	/**
	 * Gibt die Zugnummer des Zuges zurück.
	 */
	int getZugnummer() {
		return this.zugnummer;
	}
	
	/**
	 * Gibt die Zuggattung des Zuges zurück.
	 */
	String getGattung() {
		return this.gattung;
	}
	
	/**
	 * Gibt die künftige Zugnummer des Zuges zurück.
	 */
	int getZugnummerNeu() {
		return this.zugnummerNeu;
	}
	
	/**
	 * Gibt die künftige Zuggattung des Zuges zurück.
	 */
	String getGattungNeu() {
		return this.gattungNeu;
	}
	
	/**
	 * Gibt eine Liste aller Züge zurück, mit denen dieser Zug vereinigt werden kann.
	 */
	ArrayList<Zug> getVereinigungMit() {
		ArrayList<Zug> result = new ArrayList<Zug>();
		for (int zugnummer : this.vereinigungMit) {
			if (Zug.zugnummer2Zug.get(zugnummer) != null)
				result.add(Zug.zugnummer2Zug.get(zugnummer));
		}
	return result;
	}
	
	/**
	 * Gibt die aktuelle Position des Zuges zurück.
	 */
	StreckeFuerGUI getPosition() {
		return this.position;
	}
	
	/**
	 * Gibt die Richtung zurück, aus der der Zug in die Start-Betriebsstelle einfährt.
	 */
	Richtung getHerkunftsrichtung() {
		return this.herkunftsrichtung;
	}
	
	/**
	 * Gibt die planmäßige Abfahrtszeit am Bahnsteig zurück.
	 */
	Zeit getAbfahrtszeit() {
		return this.abfahrt;
	}
	
	/**
	 * Gibt die Verspätung des Zuges zurück.
	 */
	int getVerspaetung() {
		return this.verspaetung;
	}
	
	/**
	 * Wechselt Gattung bzw. Zugnummer, sofern für diesen Zug ein Gattungswechsel
	 * bzw. Wechsel der Zugnummer vorgesehen ist und der Wechsel noch nicht vollzogen ist.
	 */
	void wechsleGattungUndZugnummer() {
		if (!this.gattungNeu.equals("")) {
			this.gattung = this.gattungNeu;
		}
		if (this.zugnummerNeu != 0)
			this.zugnummer = this.zugnummerNeu;
	}
	
	/**
	 * Gibt eine Liste aller Züge zurück, mit denen ein Gleis geteilt werden darf.
	 */
	ArrayList<Zug> getZuegeSelbesGleis() {
		ArrayList<Zug> result = new ArrayList<Zug>();
		Zug zug;
		for (Integer zugnummer : this.zugnummernSelbesGleis) {
			zug = Zug.zugnummer2Zug.get(zugnummer);
			if (zug != null)
				result.add(zug);
		}
		return result;
	}
	
	/**
	 * Gibt eine Liste aller Züge zurück, mit denen ein Gleis geteilt werden darf
	 * oder mit denen der Zug vereinigt werden soll.
	 */
	ArrayList<Zug> getZuegeSelbesGleisOderVereinigung() {
		ArrayList<Zug> result = this.getZuegeSelbesGleis();
		result.addAll(this.getVereinigungMit());
		return result;
	}
	
	/**
	 * Gibt eine Liste aller Züge zurück, die derzeit mit diesem Zug vereinigt sind.
	 */
	ArrayList<Zug> getVereinigteZuege() {
		return this.vereinigteZuege;
	}
	
	/**
	 * Vereinigt den Zug mit einem anderen Zug.
	 * 
	 * @param zug
	 * 		der andere Zug, mit dem vereinigt wird
	 */
	void vereinigeMit(final Zug zug) {
		this.haengtAnZugnummer = zug.getZugnummer();
		zug.vereinigteZuege.add(this);
		zug.vereinigteZuege.addAll(this.vereinigteZuege);
		this.vereinigteZuege.clear();
		Zug.zuegeInSimulation.remove(this);
		Zug.virtuelleZuege.add(this);
		this.position.verarbeiteAbfahrendenZug(this);
		zug.abfahrt = Zeit.max(this.abfahrt, zug.abfahrt);
	}
	
	/**
	 * Gibt an, ob dieser Zugteil noch rangiert werden muss.
	 */
	boolean rangierenNotwendig() {
		if (this.rangiertUm == null || this.vereinigungMit.isEmpty()
				|| this.abfahrt.istFrueherOderZeitgleichAls(Stellwerk.getInstance().getAktuelleZeit()))
			return false;
		for (Zug zug : this.getVereinigungMit()) {
			if  (this.haengtAnZugnummer == zug.zugnummer)
				continue;
			if (zug.haengtAnZugnummer == this.zugnummer)
				continue;
			return true;
		}
		return false;
	}
	
	/**
	 * Gibt an, ob dieser Zugteil derzeit rangiert wird (d.h. mit einem Zug vereinigt
	 * werden soll, aber noch nicht vereinigt ist).
	 */
	boolean rangiert() {
		if (!this.rangierenNotwendig())
			return false;
		if (!this.rangiertUm.istFrueherOderZeitgleichAls(Stellwerk.getInstance().getAktuelleZeit()))
			return false;
		return true;
	}

	/**
	 * Sorgt dafür, dass der Zug die Simulation verlässt.
	 */
	void verlasseSimulation() {
		Zug.zuegeInSimulation.remove(this);
		this.position = null;
	}
	
	/**
	 * Gibt eine Liste aller Züge zurück, die die Simulation noch
	 * nicht vollständig durchfahren haben.
	 */
	static Vector<Zug> getZuegeInSimulation() {
		return Zug.zuegeInSimulation;
	}

	/**
	 * Sortiert die Liste aller Züge nach Ankunftszeit in der Simulation.
	 */
	static void sortiereZugliste() {
		Collections.sort(Zug.zuegeInSimulation);
	}

	/**
	 * Verknüpft Züge miteinander, die vereinigt in die Simulation einfahren.
	 */
	static void erzeugeZugvereinigungen() {
		Zug stammzug;
		for (Zug zug : Zug.virtuelleZuege) {
			stammzug = Zug.zugnummer2Zug.get(zug.haengtAnZugnummer);
			stammzug.vereinigteZuege.add(zug);
			zug.verspaetung = stammzug.verspaetung;
		}
	}
}