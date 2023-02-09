package de.drake.stellwerksimulation.fahrwegeCreator;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.drake.stellwerksimulation.tools.Richtung;

/**
 * Der FahrwegeCreator dient dazu, zu einem vorgegebenen Streckennetz
 * automatisch Fahrwege zu erstellen. Diese werden nach dem Prinzip
 * des kürzesten Weges ermittelt. Die Umsetzung erfolgt mit einer Variante des
 * A*-Algorithmus (siehe http://de.wikipedia.org/wiki/A*-Algorithmus).
 * Da einerseits die Züge keine scharfen Kurven fahren können und andererseits
 * nicht nur der kürzeste Weg zu einem, sondern allen möglichen Zielen auf ein
 * mal berechnet werden sollen, musste der A*-Algorithmus etwas modifiziert
 * werden.
 */
public class FahrwegeCreator {
	
	/**
	 * Die "Open-List" des A*-Algorithmus. Diese beinhaltet Streckenrichtungen,
	 * die noch nicht abschließend untersucht wurden.
	 */
	private ConcurrentLinkedQueue<Streckenrichtung> openList
			= new ConcurrentLinkedQueue<Streckenrichtung>();
	
	/**
	 * Die "Closed-List" des A*-Algorithmus. Diese beinhaltet Streckenrichtungen,
	 * die bereits abschließend untersucht wurden.
	 */
	private HashSet<Streckenrichtung> closedList = new HashSet<Streckenrichtung>();

	/**
	 * Startet den FahrwegeCreator.
	 */
	public static void main(String[] args) {
		new FahrwegeCreator();
	}
	
	/**
	 * Erzeugt eine neue Instanz des FahrwegeCreators.
	 */
	public FahrwegeCreator() {
		this.importiereStreckennetz();
		for (Strecke startBetriebsstelle : Strecke.getAlleBetriebsstellen())
			ermittleFahrwege(startBetriebsstelle);
	}

	/**
	 * Liest die Strecken aus der externen Datei Strecken.csv aus und speichert sie.
	 */
	private void importiereStreckennetz() {
		Scanner scanner, zeilenscanner, detailscanner;
		try {
			FileReader filereader = new FileReader("Data/Strecken.csv");
			scanner = new Scanner(filereader);
		} catch (Exception e) {
			throw new Error("Fehlende Datei: Data\\Strecken.csv");
		}
		scanner.nextLine();
		String id;
		boolean istBetriebsstelle;
		Richtung anschlussrichtung;
		ArrayList<Richtung> anschlussrichtungen;
		int positionX, positionY;
		while (scanner.hasNext()) {
			zeilenscanner = new Scanner(scanner.nextLine());
			zeilenscanner.useDelimiter(";");
			id = zeilenscanner.next();
			positionX = zeilenscanner.nextInt();
			positionY = zeilenscanner.nextInt();
			detailscanner = new Scanner(zeilenscanner.next());
			detailscanner.useDelimiter("/");
			anschlussrichtungen = new ArrayList<Richtung>(8);
			while(detailscanner.hasNext()) {
				anschlussrichtung = Richtung.getRichtung(detailscanner.next());
				if (anschlussrichtung == null) {
					scanner.close();
					zeilenscanner.close();
					detailscanner.close();
					throw new Error("Ungültige Richtungsangabe bei Strecke " + id);
				}
				anschlussrichtungen.add(anschlussrichtung);
			}
			zeilenscanner.nextInt();
			zeilenscanner.nextInt();
			zeilenscanner.next();
			if (zeilenscanner.hasNext() && zeilenscanner.next().equals("x")) {
				istBetriebsstelle = true;
			} else {
				istBetriebsstelle = false;
			}
			new Strecke(id, positionX, positionY, anschlussrichtungen, istBetriebsstelle);
		}
		scanner.close();
	}
	
	/**
	 * Ermittelt alle Fahrwege, die von einer Betriebsstelle ausgehen.
	 * 
	 * @param startBetriebsstelle
	 * 		Die Betriebsstelle, von der ausgehend die Fahrwege ermittelt
	 * 		werden sollen.
	 */
	private void ermittleFahrwege(final Strecke startBetriebsstelle) {
		this.openList.clear();
		this.closedList.clear();
		Streckenrichtung.reset();
		for (Richtung richtung : startBetriebsstelle.getFahrtrichtungen(null)) {
			this.openList.add(new Streckenrichtung(
					startBetriebsstelle, richtung, 0, null));
		}
		Streckenrichtung aktuelleStreckenrichtung, neueStreckenrichtung;
		Strecke naechsteStrecke;
		ArrayList<Richtung> moeglicheFahrtrichtungen;
		//Gibt einen Malus auf den G-Wert, wenn diagonal gefahren wird.
		double umwegMalus;
		while (!this.openList.isEmpty()) {
			aktuelleStreckenrichtung = this.openList.poll();
			this.closedList.add(aktuelleStreckenrichtung);
			naechsteStrecke = aktuelleStreckenrichtung.getNaechsteStrecke();
			//Wenn aktuelle Richtung ins leere führt...
			if (naechsteStrecke == null)
				continue;
			umwegMalus = 0.;
			if (aktuelleStreckenrichtung.getStrecke().getPositionX()
						!= naechsteStrecke.getPositionX()
					&& aktuelleStreckenrichtung.getStrecke().getPositionY()
						!= naechsteStrecke.getPositionY())
				umwegMalus = 0.0000000001;
			moeglicheFahrtrichtungen = naechsteStrecke.getFahrtrichtungen(
					aktuelleStreckenrichtung.getRichtung().getGegenrichtung());
			if (moeglicheFahrtrichtungen.isEmpty()) {
				Streckenrichtung.updateStreckenrichtung(
						naechsteStrecke, null, 
						aktuelleStreckenrichtung.getGWert() + 1. + umwegMalus, 
						aktuelleStreckenrichtung);
			}
			for (Richtung richtung : moeglicheFahrtrichtungen) {
				neueStreckenrichtung = Streckenrichtung.updateStreckenrichtung(
						naechsteStrecke, richtung,
						aktuelleStreckenrichtung.getGWert() + 1. + umwegMalus, 
						aktuelleStreckenrichtung);
				if (!this.closedList.contains(neueStreckenrichtung)
						&& !this.openList.contains(neueStreckenrichtung))
					this.openList.add(neueStreckenrichtung);
			}
		}
		Streckenrichtung.printFahrwege();
	}
}