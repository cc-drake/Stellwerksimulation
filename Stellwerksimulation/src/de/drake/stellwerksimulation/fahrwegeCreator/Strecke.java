package de.drake.stellwerksimulation.fahrwegeCreator;

import java.util.ArrayList;

import de.drake.stellwerksimulation.tools.MultiHashMap;
import de.drake.stellwerksimulation.tools.Richtung;

/**
 * Eine Strecke f�r den fahrwegeCreator.
 */
class Strecke {
	
	/**
	 * Eine Liste aller Betriebsstellen, die vom fahrwegeCreator importiert wurden.
	 */
	private static ArrayList<Strecke> alleBetriebsstellen = new ArrayList<Strecke>();
	
	/**
	 * Eine Zuordnung, �ber die Strecken anhand ihrer Koordinaten gefunden
	 * werden k�nnen.
	 */
	private static MultiHashMap<Integer, Integer, ArrayList<Strecke>>
			koordinaten2Strecken
			= new MultiHashMap<Integer, Integer, ArrayList<Strecke>>();
	
	/**
	 * Die Bezeichnung der Strecke in den Importdateien.
	 */
	private String id;
	
	/**
	 * Die Position der Strecke im Matrixsystem (X-Koordinate).
	 */
	private int positionX;
	
	/**
	 * Die Position der Strecke im Matrixsystem (Y-Koordinate).
	 */
	private int positionY;
	
	/**
	 * Die Richtungen, in die von diesem Streckenabschnitt Teilstrecken f�hren.
	 */
	private ArrayList<Richtung> anschlussrichtungen;
	
	/**
	 * Gibt an, ob es sich bei dieser Strecke um eine Betriebsstelle handelt.
	 */
	private boolean istBetriebsstelle;
	
	/**
	 * Erzeugt eine neue Strecke.
	 * 
	 * @param id
	 * 		Die Bezeichnung der Strecke in den Importdateien.
	 * @param positionX
	 * 		Die Position der Strecke im Matrixsystem (X-Koordinate).
	 * @param positionY
	 * 		Die Position der Strecke im Matrixsystem (Y-Koordinate).
	 * @param anschlussrichtungen
	 * 		Die Richtungen, in die von diesem Streckenabschnitt Teilstrecken f�hren.
	 * @param istBetriebsstelle
	 * 		Gibt an, ob es sich bei dieser Strecke um eine Betriebsstelle handelt.
	 */
	Strecke(final String id, final int positionX, final int positionY,
			final ArrayList<Richtung> anschlussrichtungen,
			final boolean istBetriebsstelle) {
		this.id = id;
		this.positionX = positionX;
		this.positionY = positionY;
		this.anschlussrichtungen = anschlussrichtungen;
		this.istBetriebsstelle = istBetriebsstelle;
		if (this.istBetriebsstelle)
			Strecke.alleBetriebsstellen.add(this);
		if (!Strecke.koordinaten2Strecken.containsKey(this.positionX, this.positionY)) {
			Strecke.koordinaten2Strecken.put(
					this.positionX, this.positionY, new ArrayList<Strecke>(2));
		}
		Strecke.koordinaten2Strecken.get(this.positionX, this.positionY).add(this);
	}
	
	/**
	 * Gibt die ID der Strecke als String zur�ck.
	 */
	public String toString() {
		return this.id;
	}
	
	/**
	 * Gibt die X-Koordinate dieser Strecke zur�ck.
	 */
	int getPositionX() {
		return this.positionX;
	}
	
	/**
	 * Gibt die Y-Koordinate dieser Strecke zur�ck.
	 */
	int getPositionY() {
		return this.positionY;
	}
	
	/**
	 * Gibt die ID der Strecke zur�ck.
	 */
	String getID() {
		return this.id;
	}
	
	/**
	 * Gibt zur�ck, ob die Strecke eine Betriebsstelle ist.
	 */
	boolean istBetriebsstelle() {
		return this.istBetriebsstelle;
	}
	
	/**
	 * Gibt zu einer Ankufntsrichtung die m�glichen Fahrtrichtungen zur�ck.
	 * 
	 * @param ankunftsrichtung
	 * 		Die Ankunftsrichtung an dieser Strecke. Wird null �bergeben, so
	 * 		werden alle vorhandenen Anschlussrichtungen zur�ckgegeben.
	 */
	ArrayList<Richtung> getFahrtrichtungen(final Richtung ankunftsrichtung) {
		if (ankunftsrichtung == null)
			return this.anschlussrichtungen;
		ArrayList<Richtung> fahrtrichtungen = new ArrayList<Richtung>(3);
		for (Richtung richtung : this.anschlussrichtungen) {
			if (ankunftsrichtung.getGegenrichtungen().contains(richtung))
				fahrtrichtungen.add(richtung);
		}
		return fahrtrichtungen;
	}
	
	/**
	 * Gibt eine Liste aller Betriebsstellen zur�ck.
	 */
	static ArrayList<Strecke> getAlleBetriebsstellen() {
		return Strecke.alleBetriebsstellen;
	}
	
	/**
	 * Gibt eine Liste aller Strecken zur�ck, die sich an bestimmten
	 * Koordinaten befinden (im Falle von Br�cken kann dies mehr als eine Strecke
	 * sein).
	 */
	static ArrayList<Strecke> getStrecken(final int positionX, final int positionY) {
		return Strecke.koordinaten2Strecken.get(positionX, positionY);
	}
}