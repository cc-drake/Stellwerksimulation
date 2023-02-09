package de.drake.stellwerksimulation.model;

import java.util.ArrayList;
import java.util.Collections;

import de.drake.stellwerksimulation.tools.Richtung;
import de.drake.stellwerksimulation.tools.TwosidedStack;

/**
 * Eine Strecke beschreibt eine Strecke in der Simulation. Sie kann gleichzeitig
 * auch Br�cken, Weichen oder Bahnsteige beinhalten.
 */
class Strecke extends StreckeFuerGUI {
	
	/**
	 * Eine Liste aller Strecken der Simulation.
	 */
	private static ArrayList<Strecke> alleStrecken = new ArrayList<Strecke>();
	
	/**
	 * Die Richtungen, in die von diesem Streckenabschnitt Teilstrecken f�hren. Die
	 * Richtungen sind sortiert nach Nord-S�d bzw. West-Ost.
	 */
	private ArrayList<Richtung> anschlussrichtungen;
		
	/**
	 * Gibt an, in welchen Richtungen die Strecke derzeit genutzt wird (durch Z�ge
	 * oder Fahrwege).
	 */
	private ArrayList<Richtung> eingestellteRichtungen;
	
	/**
	 * Gibt an, ob die Strecke derzeit frei ist, d.h. f�r einen neuen Fahrweg
	 * zur Verf�gung steht.
	 */
	private boolean istFrei = true;
	
	/**
	 * Eine Liste aller Z�ge, die sich auf der entsprechenden Strecke befinden.
	 * Nur Betriebsstellen k�nnen mehr als einen Zug gleichzeitig beinhalten.
	 */
	private TwosidedStack<Zug, Richtung> zuegeAufStrecke;
	
	/**
	 * Erzeugt eine neue Strecke.
	 * 
	 * @param positionX
	 * 		Die Position der Strecke im Koordinatensystem (X-Koordinate)
	 * @param positionY
	 * 		Die Position der Strecke im Koordinatensystem (Y-Koordinate)
	 * @param anschlussrichtungen
	 * 		Alle Richtungen, in die von diesem Streckenabschnitt Teilstrecken f�hren.
	 * @param istBrueckeOben
	 * 		Gibt an, ob die Strecke br�ckenfrei ist (null), aus einem oberen Br�ckenteil
	 * 		besteht (true) oder aus einem unteren Br�ckenteil besteht (false).
	 * @param positionZugnummerX
	 * 		Die Position der Zugnummernanzeige in der GUI relativ zur Streckenmitte
	 * 		(X-Koordinate).
	 * @param positionZugnummerY
	 * 		Die Position der Zugnummernanzeige in der GUI relativ zur Streckenmitte
	 * 		(Y-Koordinate).
	 */
	Strecke(final int positionX, final int positionY,
			final ArrayList<Richtung> anschlussrichtungen, final Boolean istBrueckeOben,
			final int positionZugnummerX, final int positionZugnummerY) {
		super(positionX, positionY, istBrueckeOben, positionZugnummerX,
				positionZugnummerY);
		Strecke.alleStrecken.add(this);
		//Die Anschlussrichtungen nach Nord->S�d und West->Ost sortieren, um
		// bei mehreren Z�gen im Gleis ihre Reihenfolge richtig aufzuschreiben.
		Collections.sort(anschlussrichtungen);
		this.anschlussrichtungen = anschlussrichtungen;
		this.eingestellteRichtungen = anschlussrichtungen;
		if (this.istStumpfgleis() && this.anschlussrichtungen.get(0).entsprichtUnten()) {
			this.zuegeAufStrecke = new TwosidedStack<Zug, Richtung>(null);
		} else {
			this.zuegeAufStrecke = new TwosidedStack<Zug, Richtung>(
					this.anschlussrichtungen.get(0));
		}
	}
	
	/**
	 * Gibt zur�ck, ob die Strecke derzeit frei ist und als Fahrweg eingestellt
	 * werden kann.
	 * 
	 * @return
	 * 		true, wenn Strecke frei.
	 */
	public boolean istFrei() {
		return this.istFrei;
	}
	
	/**
	 * Gibt zur�ck, ob sich auf der Strecke derzeit Z�ge befinden.
	 */
	public boolean istBefahren() {
		if (this.zuegeAufStrecke.isEmpty())
			return false;
		return true;
	}
	
	/**
	 * Verarbeitet die Auswirkungen eines Zuges, der in diese Strecke einf�hrt.
	 * 
	 * @param zug
	 * 		Der Zug, der gerade ankommt.
	 * @param ankunftsrichtung
	 * 		Die Richtung, aus der der Zug ankommt.
	 */
	void verarbeiteAnkommendenZug(final Zug zug, final Richtung ankunftsrichtung) {
		this.istFrei = false;
		this.zuegeAufStrecke.add(zug, ankunftsrichtung);
	}
	
	/**
	 * Verarbeitet die Auswirkungen eines Zuges, der diese Strecke verl�sst.
	 * 
	 * @param zug
	 * 		Der Zug, der gerade weiterf�hrt.
	 */
	void verarbeiteAbfahrendenZug(final Zug zug) {
		this.zuegeAufStrecke.remove(zug);
		if (this.zuegeAufStrecke.isEmpty() && !this.hatEingestellteFahrwege()) {
			this.istFrei = true;
		}
	}
	
	/**
	 * Gibt an, ob Fahrwege zu dieser Strecke bzw. von dieser Strecke
	 * ausgehend eingestellt sind. Ist f�r "einfache"
	 * Strecken immer false, an Betriebsstellen jedoch nicht. Muss daher von
	 * Betriebsstellen overridet werden.
	 */
	boolean hatEingestellteFahrwege() {
		return false;
	}

	/**
	 * Stellt auf dieser Strecke einen Fahrweg ein.
	 * 
	 * @param durchfahrtsrichtungen
	 * 		Die Richtung, die eingestellt werden soll.
	 */
	void stelleFahrwegEin(final ArrayList<Richtung> durchfahrtsrichtungen) {
		this.eingestellteRichtungen = durchfahrtsrichtungen;
		this.istFrei = false;
	}
	
	/**
	 * Nimmt einen zuvor eingestellten, aber noch nicht befahrenen Fahrweg auf dieser
	 * Strecke zur�ck.
	 */
	void nehmeFahrwegZurueck() {
		if (!this.zuegeAufStrecke.isEmpty() || this.hatEingestellteFahrwege())
			return;
		this.istFrei = true;
	}
	
	/**
	 * Gibt eine Liste aller Z�ge zur�ck, die sich derzeit auf dieser Strecke
	 * bzw. Betriebsstelle befinden.
	 */
	TwosidedStack<Zug, Richtung> getZuegeImGleis() {
		return this.zuegeAufStrecke;
	}
	
	/**
	 * Gibt eine Liste aller Anschlussrichtungen dieser Strecke zur�ck.
	 */
	public ArrayList<Richtung> getAnschlussrichtungen() {
		return this.anschlussrichtungen;
	}
	
	/**
	 * Gibt die Menge der derzeit eingestellten Richtungen zur�ck.
	 */
	public ArrayList<Richtung> getEingestellteRichtungen() {
		return this.eingestellteRichtungen;
	}
	
	/**
	 * Gibt eine Liste aller Strecken der Simulation zur�ck.
	 */
	static ArrayList<Strecke> getAlleStrecken() {
		return Strecke.alleStrecken;
	}
	
	/**
	 * Gibt zur�ck, ob die Strecke ein Stumpfgleis repr�sentiert, d.h. nur
	 * in eine Richtung f�hrt und ansonsten quasi mit einem Prellbock abschlie�t.
	 */
	private boolean istStumpfgleis() {
		if (this.anschlussrichtungen.size() == 1)
			return true;
		return false;
	}
}