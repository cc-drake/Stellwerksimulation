package de.drake.stellwerksimulation.fahrwegeCreator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.drake.stellwerksimulation.tools.MultiHashMap;
import de.drake.stellwerksimulation.tools.Pair;
import de.drake.stellwerksimulation.tools.Richtung;

/**
 * Kombination aus Strecke und Fahrtrichtung, die die Grundlage für den A*-Algorithmus 
 * bildet.
 */
class Streckenrichtung extends Pair<Strecke, Richtung> {
	
	/**
	 * Eine Zuordnung, mit der Streckenrichtungen über Strecke und Richtung
	 * gefunden werden können
	 */
	private static MultiHashMap<Strecke, Richtung, Streckenrichtung>
			key2Streckenrichtung
			= new MultiHashMap<Strecke, Richtung, Streckenrichtung>();
	
	/**
	 * Eine Zuordnung, mit der Streckenrichtungen gefunden werden können.
	 */
	private static HashMap<Strecke, ArrayList<Streckenrichtung>>
			strecke2Streckenrichtung
			= new HashMap<Strecke, ArrayList<Streckenrichtung>>();
	
	/**
	 * Der g-Wert des Knotens. Hier wird vom A*-Algorithmus jeweils der Abstand
	 * zum Startknoten eingetragen. 
	 */
	private double gWert;
	
	/**
	 * Hier wird vom A*-Algorithmus jeweils der Vorgängerknoten aus Richtung des
	 * Startknotens eingetragen.
	 */
	private Streckenrichtung vorgaengerStrecke;
	
	/**
	 * Der Filewriter, der die Fahrwege erzeugt.
	 */
	private static FileWriter filewriter;
	
	/**
	 * Erzeugt eine neue Streckenrichtung.
	 * 
	 * @param strecke
	 * 		Die Strecke der Streckenrichtung.
	 * @param richtung
	 * 		Die Fahrtrichtung, in der die Strecke verlassen werden soll.
	 * @param gWert
	 * 		Der g-Wert des Knotens. Hier wird vom A*-Algorithmus jeweils der Abstand
	 * 		zum Startknoten eingetragen. 
	 * @param vorgaengerStrecke
	 * 		Hier wird vom A*-Algorithmus jeweils der Vorgängerknoten aus Richtung des
	 * 		Startknotens eingetragen.
	 */
	Streckenrichtung(final Strecke strecke, final Richtung richtung,
			final double gWert, final Streckenrichtung vorgaengerStrecke) {
		super(strecke, richtung);
		this.gWert = gWert;
		this.vorgaengerStrecke = vorgaengerStrecke;
		Streckenrichtung.key2Streckenrichtung.put(strecke, richtung, this);
		if (!Streckenrichtung.strecke2Streckenrichtung.containsKey(strecke))
			Streckenrichtung.strecke2Streckenrichtung.put(
					strecke, new ArrayList<Streckenrichtung>(8));
		Streckenrichtung.strecke2Streckenrichtung.get(strecke).add(this);
	}
	
	/**
	 * Gibt die Strecke der Streckenrichtung zurück.
	 */
	Strecke getStrecke() {
		return this.getFirst();
	}
	
	/**
	 * Gibt die Strecke der Streckenrichtung zurück.
	 */
	Richtung getRichtung() {
		return this.getSecond();
	}
	
	/**
	 * Gibt den aktuellen g-Wert der Strecke zurück.
	 */
	double getGWert() {
		return this.gWert;
	}
	
	/**
	 * Gibt die nächste Strecke in der aktuellen Richtung zurück. Führt die Strecke
	 * ins leere, wird null zurückgegeben.
	 */
	Strecke getNaechsteStrecke() {
		int positionXneu = this.getFirst().getPositionX();
		int positionYneu = this.getFirst().getPositionY();
		switch(this.getRichtung()) {
		case WEST:
		case OST:
			break;
		case NORD:
		case NORDOST:
		case NORDWEST:
			positionYneu--;
			break;
		case SUED:
		case SUEDOST:
		case SUEDWEST:
			positionYneu++;
		}
		switch(this.getRichtung()) {
		case NORD:
		case SUED:
			break;
		case OST:
		case NORDOST:
		case SUEDOST:
			positionXneu++;
			break;
		case WEST:
		case NORDWEST:
		case SUEDWEST:
			positionXneu--;
		}
		if (Strecke.getStrecken(positionXneu, positionYneu) == null)
			return null;
		for (Strecke strecke : Strecke.getStrecken(positionXneu, positionYneu)) {
			if (strecke.getFahrtrichtungen(null).contains(
					this.getRichtung().getGegenrichtung()))
				return strecke;
		}
		return null;
	}
	
	/**
	 * Aktualisiert den g-Wert einer Streckenrichtung, sofern der neue g-Wert
	 * niedriger ist. Ist die Streckenrichtung noch nicht vorhanden, wird eine neue
	 * Streckenrichtung angelegt.
	 * 
	 * @param strecke
	 * 		Die Strecke, die aktualisiert werden soll.
	 * @param richtung
	 * 		Die Richtung der Streckenrichtung, die aktualisiert werden soll.
	 * @param gWert
	 * 		der neue g-Wert
	 * @param vorgaengerStrecke
	 * 		die neue Vorgänger-Strecke.
	 * 
	 * @return die Streckenrichtung, die geupdatet wurde.
	 */
	static Streckenrichtung updateStreckenrichtung(final Strecke strecke,
			final Richtung richtung, final double gWert,
			final Streckenrichtung vorgaengerStrecke) {
		Streckenrichtung streckenrichtung = 
				Streckenrichtung.key2Streckenrichtung.get(strecke, richtung);
		if (streckenrichtung == null)
			return new Streckenrichtung(strecke, richtung, gWert, vorgaengerStrecke);
		if (streckenrichtung.gWert > gWert) {
			streckenrichtung.gWert = gWert;
			streckenrichtung.vorgaengerStrecke = vorgaengerStrecke;
		}
		return streckenrichtung;
	}
	
	/**
	 * Setzt alle Streckenrichtungen für einen Neustart des A*-Algorithmus zurück.
	 */
	static void reset() {
		Streckenrichtung.key2Streckenrichtung.clear();
		Streckenrichtung.strecke2Streckenrichtung.clear();
	}
	
	/**
	 * Stellt die Streckenrichtung als String dar.
	 */
	public String toString() {
		return this.getStrecke().getID() + "$" + this.getRichtung();
	}

	/**
	 * Gibt alle gefundenen Fahrwege aus. 
	 */
	static void printFahrwege() {
		if (Streckenrichtung.filewriter == null) {
			try {
				Streckenrichtung.filewriter = new FileWriter("Data/Fahrwege (neu).csv");
				filewriter.write("Laufweg\r\n");
			} catch (IOException e) {
				throw new Error("Fehler beim Schreiben der Fahrwege!");
			}
		}
		Streckenrichtung besteStreckenrichtung;
		ArrayList<Streckenrichtung> fahrweg = new ArrayList<Streckenrichtung>();
		for (Strecke zielBetriebsstelle : Strecke.getAlleBetriebsstellen()) {
			if (!Streckenrichtung.strecke2Streckenrichtung
					.containsKey(zielBetriebsstelle))
				continue;
			besteStreckenrichtung = null;
			for (Streckenrichtung streckenrichtung: Streckenrichtung
					.strecke2Streckenrichtung.get(zielBetriebsstelle)) {
				if (besteStreckenrichtung == null
						|| streckenrichtung.gWert < besteStreckenrichtung.gWert)
					besteStreckenrichtung = streckenrichtung;
			}
			if (besteStreckenrichtung.gWert == 0)
				continue;
			fahrweg.clear();
			while (besteStreckenrichtung != null) {
				fahrweg.add(besteStreckenrichtung);
				besteStreckenrichtung = besteStreckenrichtung.vorgaengerStrecke;
			}
			try {
				for (int index = fahrweg.size()-1; index >= 1; index--) {
					Streckenrichtung.filewriter.write(fahrweg.get(index) + ";");
				}
				Streckenrichtung.filewriter.write(
						fahrweg.get(0).getStrecke().getID() + "\r\n");
				Streckenrichtung.filewriter.flush();
			} catch (IOException e) {
				throw new Error("Fehler beim Schreiben der Fahrwege!");
			}
		}
	}
}