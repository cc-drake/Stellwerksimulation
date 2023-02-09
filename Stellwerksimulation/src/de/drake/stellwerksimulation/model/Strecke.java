package de.drake.stellwerksimulation.model;

import java.util.ArrayList;
import java.util.Collections;

import de.drake.stellwerksimulation.tools.Richtung;
import de.drake.stellwerksimulation.tools.TwosidedStack;

/**
 * Eine Strecke beschreibt eine Strecke in der Simulation. Sie kann gleichzeitig
 * auch Brücken, Weichen oder Bahnsteige beinhalten.
 */
class Strecke extends StreckeFuerGUI {
	
	/**
	 * Eine Liste aller Strecken der Simulation.
	 */
	private static ArrayList<Strecke> alleStrecken = new ArrayList<Strecke>();
	
	/**
	 * Die Richtungen, in die von diesem Streckenabschnitt Teilstrecken führen. Die
	 * Richtungen sind sortiert nach Nord-Süd bzw. West-Ost.
	 */
	private ArrayList<Richtung> anschlussrichtungen;
		
	/**
	 * Gibt an, in welchen Richtungen die Strecke derzeit genutzt wird (durch Züge
	 * oder Fahrwege).
	 */
	private ArrayList<Richtung> eingestellteRichtungen;
	
	/**
	 * Gibt an, ob die Strecke derzeit frei ist, d.h. für einen neuen Fahrweg
	 * zur Verfügung steht.
	 */
	private boolean istFrei = true;
	
	/**
	 * Eine Liste aller Züge, die sich auf der entsprechenden Strecke befinden.
	 * Nur Betriebsstellen können mehr als einen Zug gleichzeitig beinhalten.
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
	 * 		Alle Richtungen, in die von diesem Streckenabschnitt Teilstrecken führen.
	 * @param istBrueckeOben
	 * 		Gibt an, ob die Strecke brückenfrei ist (null), aus einem oberen Brückenteil
	 * 		besteht (true) oder aus einem unteren Brückenteil besteht (false).
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
		//Die Anschlussrichtungen nach Nord->Süd und West->Ost sortieren, um
		// bei mehreren Zügen im Gleis ihre Reihenfolge richtig aufzuschreiben.
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
	 * Gibt zurück, ob die Strecke derzeit frei ist und als Fahrweg eingestellt
	 * werden kann.
	 * 
	 * @return
	 * 		true, wenn Strecke frei.
	 */
	public boolean istFrei() {
		return this.istFrei;
	}
	
	/**
	 * Gibt zurück, ob sich auf der Strecke derzeit Züge befinden.
	 */
	public boolean istBefahren() {
		if (this.zuegeAufStrecke.isEmpty())
			return false;
		return true;
	}
	
	/**
	 * Verarbeitet die Auswirkungen eines Zuges, der in diese Strecke einfährt.
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
	 * Verarbeitet die Auswirkungen eines Zuges, der diese Strecke verlässt.
	 * 
	 * @param zug
	 * 		Der Zug, der gerade weiterfährt.
	 */
	void verarbeiteAbfahrendenZug(final Zug zug) {
		this.zuegeAufStrecke.remove(zug);
		if (this.zuegeAufStrecke.isEmpty() && !this.hatEingestellteFahrwege()) {
			this.istFrei = true;
		}
	}
	
	/**
	 * Gibt an, ob Fahrwege zu dieser Strecke bzw. von dieser Strecke
	 * ausgehend eingestellt sind. Ist für "einfache"
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
	 * Strecke zurück.
	 */
	void nehmeFahrwegZurueck() {
		if (!this.zuegeAufStrecke.isEmpty() || this.hatEingestellteFahrwege())
			return;
		this.istFrei = true;
	}
	
	/**
	 * Gibt eine Liste aller Züge zurück, die sich derzeit auf dieser Strecke
	 * bzw. Betriebsstelle befinden.
	 */
	TwosidedStack<Zug, Richtung> getZuegeImGleis() {
		return this.zuegeAufStrecke;
	}
	
	/**
	 * Gibt eine Liste aller Anschlussrichtungen dieser Strecke zurück.
	 */
	public ArrayList<Richtung> getAnschlussrichtungen() {
		return this.anschlussrichtungen;
	}
	
	/**
	 * Gibt die Menge der derzeit eingestellten Richtungen zurück.
	 */
	public ArrayList<Richtung> getEingestellteRichtungen() {
		return this.eingestellteRichtungen;
	}
	
	/**
	 * Gibt eine Liste aller Strecken der Simulation zurück.
	 */
	static ArrayList<Strecke> getAlleStrecken() {
		return Strecke.alleStrecken;
	}
	
	/**
	 * Gibt zurück, ob die Strecke ein Stumpfgleis repräsentiert, d.h. nur
	 * in eine Richtung führt und ansonsten quasi mit einem Prellbock abschließt.
	 */
	private boolean istStumpfgleis() {
		if (this.anschlussrichtungen.size() == 1)
			return true;
		return false;
	}
}