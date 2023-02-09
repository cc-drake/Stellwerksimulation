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
	 * Eine Liste aller Z�ge, die im Fahrplanpanel dargestellt werden sollen.
	 */
	private static Vector<Zug> zuegeInSimulation
			= new Vector<Zug>();
	
	/**
	 * Eine Liste aller Z�ge, die derzeit mit anderen Z�gen vereinigt fahren und daher
	 * nicht separat in der Liste "zuegeInSimulation" auftauchen.
	 */
	private static Vector<Zug> virtuelleZuege
			= new Vector<Zug>();
	
	/**
	 * Eine Zuordnung Zugnummer zu Zug, um Z�ge anhand ihrer Zugnummern finden zu k�nnen.
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
	 * Falls dieser Zug an einen anderen Zug gekoppelt in die Simulation einf�hrt, steht 
	 * hier die Zugnummer. Andernfalls ist 0 eingetragen.
	 */
	private int haengtAnZugnummer;
	
	/**
	 * Gibt an, ob der Zugteil am simulierten Bahnhof abgekoppelt wird
	 */
	private boolean fluegelt;
	
	/**
	 * Gibt an, in welche Richtung im Falle einer Fl�gelung der Zugteil abgetrennt wird.
	 */
	private boolean fluegeltNachVorne;
	
	/**
	 * Gibt an, welche Z�ge derzeit mit diesem Zug vereinigt sind.
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
	 * Die Betriebsstelle, an der der Zug die Simulation verl�sst
	 */
	private Betriebsstelle zielBetriebsstelle;
	
	/**
	 * Planm��ige Ankunftszeit an der Start-Betriebsstelle
	 */
	private Zeit ankunft;
	
	/**
	 * Planm��ige Abfahrtszeit am Gleis
	 */
	private Zeit abfahrt;
		
	/**
	 * Die Zeit, die der Zug mindestens f�r den Fahrgastwechsel ben�tigt (ist im Fall
	 * von Versp�tungen relevant)
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
	 * Gibt die Versp�tung des Zuges in Minuten an.
	 */
	private int verspaetung = 0;
	
	/**
	 * Gibt die tats�chliche Abfahrtszeit in die zuletzt eingefahrene Betriebsstelle an.
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
	 * Gibt die aktuelle Fahrtrichtung des Zuges zur�ck.
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
	 * 		Falls dieser Zug an einen anderen Zug gekoppelt in die Simulation einf�hrt, steht 
	 * 		hier die Zugnummer. Andernfalls ist 0 einzutragen.
	 * @param fluegelt
	 * 		Gibt an, ob der Zugteil am simulierten Bahnhof abgekoppelt wird
	 * @param fluegeltNachVorne
	 * 		Gibt an, in welche Richtung im Falle einer Fl�gelung der Zugteil abgetrennt wird.
	 * @param herkunftsbahnhof
	 * 		Herkunftsbahnhof (z.B. Hamburg-Altona) zur Anzeige im Fahrplanfenster
	 * @param startBetriebsstelle
	 * 		Die Betriebsstelle, an der der Zug die Simulation betritt
	 * @param herkunftsrichtung
	 * 		Die Richtung, aus der die startBetriebsstelle erreicht wird
	 * @param zielbahnhof
	 * 		Zielbahnhof (z.B. M�nchen Hbf) zur Anzeige im Fahrplanfenster
	 * @param zielBetriebsstelle
	 * 		Die Betriebsstelle, an der der Zug die Simulation verl�sst
	 * @param viaGleis
	 * 		Die Betriebsstelle (also das Gleis), �ber das der Zug fahren sollte
	 * @param ankunft
	 * 		Planm��ige Ankunftszeit an der Start-Betriebsstelle
	 * @param abfahrt
	 * 		Planm��ige Abfahrtszeit am Gleis
	 * @param minimalerAufenthalt
	 * 		Die Zeit, die der Zug mindestens f�r den Fahrgastwechsel ben�tigt
	 * 		(ist im Fall von Versp�tungen relevant)
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
		//G�terzug erkennen
		if (viaGleis == null)
			this.fahrgastwechselErledigt = true;
	}

	/**
	 * F�gt dem Zug Versp�tung hinzu.
	 * 
	 * @param verspaetung 
	 * 		Die zus�tzliche Versp�tung in Minuten.
	 */
	void addVerspaetung(final int verspaetung) {
		this.verspaetung += verspaetung;
	}

	/**
	 * Gibt die tats�chliche Ankunftszeit an der start-Betriebsstelle der Simulation
	 * an, d.h. inklusive von Versp�tung.
	 * 
	 * @return Die tats�chliche Ankunftszeit.
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
	 * 		Gibt an, ob bei der Bestimmung der Abfahrtszeit Zeit f�r Fahrgastwechsel
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
	 * @return Die tats�chliche Ankunftszeit.
	 */
	Zeit getTatsaechlicheAbfahrt() {
		return this.tatsaechlicheAbfahrt;
	}
	
	/**
	 * Gibt die Betriebsstelle zur�ck, an der der Zug in die Simulation einf�hrt.
	 * 
	 * @return Die entsprechende Betriebsstelle
	 */
	Betriebsstelle getStartBetriebsstelle() {
		return this.startBetriebsstelle;
	}

	/**
	 * Gibt eine Liste aller Ziel-Betriebsstellen des Zugverbands zur�ck.
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
	 * Gibt die Ziel-Betriebsstelle dieses Zuges zur�ck, d.h. die Betriebsstelle, �ber
	 * die der Zug die Simulation verl�sst.
	 */
	BetriebsstelleFuerGUI getZielBetriebsstelle() {
		return this.zielBetriebsstelle;
	}

	/**
	 * Vergleichsmethode, nach der Zuglisten sortiert werden. Der Vergleich l�uft so,
	 * dass zun�chst nach der tats�chlichen Ankunftszeit in der
	 * Simulation sortiert wird. Stimmt die tats�chliche Ankunftszeit von zwei
	 * Z�gen �berein, so haben stark versp�tete Z�ge Vorrang
	 * (diese bremsen sozusagen die p�nktlichen Z�ge aus).
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
	 * Stellt f�r den Zug einen Fahrweg ein.
	 * 
	 * @param fahrweg
	 * 		Der gew�nschte Fahrweg
	 */
	void setFahrweg(final Fahrweg fahrweg) {
		this.befahrenerFahrweg = fahrweg;
		this.fahrtrichtung = fahrweg.getFahrtrichtung(this.position);
		if (this.fahrtrichtung == null)
			this.fahrtrichtung = this.herkunftsrichtung.getGegenrichtung();
	}
	
	/**
	 * Gibt an, ob Zug gerade f�hrt oder nicht. Fahren bedeutet, dass der Zug sich
	 * in der Simulation befindet und einen eingestellten Fahrweg besitzt.
	 * 
	 * @return true, wenn der Zug f�hrt.
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
	 * Gibt an, in welche Richtung im Falle einer Fl�gelung der Zugteil abgetrennt wird.
	 */
	boolean fluegeltNachVorne() {
		return this.fluegeltNachVorne;
	}
	
	/**
	 * Gibt zur�ck, ob der Zug derzeit auf freie Fahrt wartet.
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
	 * L�sst den Zug in den n�chsten Streckenabschnitt vorfahren.
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
	 * L�st diesen Zug von seinem Stammzug.
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
	 * Setter f�r die Position des Zuges. Wird nur bei Einfahrt des Zuges in die
	 * Simulation ausgef�hrt.
	 * 
	 * @param position
	 * 		Die Position des Zuges.
	 */
	void setPosition(final Strecke position) {
		this.position = position;
	}
	
	/**
	 * Markiert den Fahrgastwechsel als durchgef�hrt.
	 */
	void setFahrgastwechselErledigt() {
		this.fahrgastwechselErledigt = true;
	}
	
	/**
	 * Gibt zur�ck, ob dieser Zug bereits den Fahrgastwechsel erledigt hat.
	 * Im Fall eines G�terzuges wird grunds�tzlich true zur�ckgegeben.
	 */
	boolean hatFahrgastwechselErledigt() {
		return this.fahrgastwechselErledigt;
	}
	
	/**
	 * Gibt zur�ck, ob sich der Zug bereits in der Simulation befindet oder nicht.
	 */
	boolean istInSimulation() {
		if (this.position == null)
			return false;
		return true;
	}
	
	/**
	 * Gibt die Zugnummer des Zuges zur�ck.
	 */
	int getZugnummer() {
		return this.zugnummer;
	}
	
	/**
	 * Gibt die Zuggattung des Zuges zur�ck.
	 */
	String getGattung() {
		return this.gattung;
	}
	
	/**
	 * Gibt die k�nftige Zugnummer des Zuges zur�ck.
	 */
	int getZugnummerNeu() {
		return this.zugnummerNeu;
	}
	
	/**
	 * Gibt die k�nftige Zuggattung des Zuges zur�ck.
	 */
	String getGattungNeu() {
		return this.gattungNeu;
	}
	
	/**
	 * Gibt eine Liste aller Z�ge zur�ck, mit denen dieser Zug vereinigt werden kann.
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
	 * Gibt die aktuelle Position des Zuges zur�ck.
	 */
	StreckeFuerGUI getPosition() {
		return this.position;
	}
	
	/**
	 * Gibt die Richtung zur�ck, aus der der Zug in die Start-Betriebsstelle einf�hrt.
	 */
	Richtung getHerkunftsrichtung() {
		return this.herkunftsrichtung;
	}
	
	/**
	 * Gibt die planm��ige Abfahrtszeit am Bahnsteig zur�ck.
	 */
	Zeit getAbfahrtszeit() {
		return this.abfahrt;
	}
	
	/**
	 * Gibt die Versp�tung des Zuges zur�ck.
	 */
	int getVerspaetung() {
		return this.verspaetung;
	}
	
	/**
	 * Wechselt Gattung bzw. Zugnummer, sofern f�r diesen Zug ein Gattungswechsel
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
	 * Gibt eine Liste aller Z�ge zur�ck, mit denen ein Gleis geteilt werden darf.
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
	 * Gibt eine Liste aller Z�ge zur�ck, mit denen ein Gleis geteilt werden darf
	 * oder mit denen der Zug vereinigt werden soll.
	 */
	ArrayList<Zug> getZuegeSelbesGleisOderVereinigung() {
		ArrayList<Zug> result = this.getZuegeSelbesGleis();
		result.addAll(this.getVereinigungMit());
		return result;
	}
	
	/**
	 * Gibt eine Liste aller Z�ge zur�ck, die derzeit mit diesem Zug vereinigt sind.
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
	 * Sorgt daf�r, dass der Zug die Simulation verl�sst.
	 */
	void verlasseSimulation() {
		Zug.zuegeInSimulation.remove(this);
		this.position = null;
	}
	
	/**
	 * Gibt eine Liste aller Z�ge zur�ck, die die Simulation noch
	 * nicht vollst�ndig durchfahren haben.
	 */
	static Vector<Zug> getZuegeInSimulation() {
		return Zug.zuegeInSimulation;
	}

	/**
	 * Sortiert die Liste aller Z�ge nach Ankunftszeit in der Simulation.
	 */
	static void sortiereZugliste() {
		Collections.sort(Zug.zuegeInSimulation);
	}

	/**
	 * Verkn�pft Z�ge miteinander, die vereinigt in die Simulation einfahren.
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