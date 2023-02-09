package de.drake.stellwerksimulation.model;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import de.drake.stellwerksimulation.controller.Controller;
import de.drake.stellwerksimulation.tools.Richtung;
import de.drake.stellwerksimulation.tools.Zeit;
import de.drake.stellwerksimulation.view.GUI;

/**
 * Der Importer liest die Konfigurationsdateien aus und initialisiert die
 * einzelnen Objekte des Models.
 */
public class Importer {
	
	/**
	 * Eine Zuordnung Streckenname auf Streckenobjekt, um auf Strecken über ihre
	 * ID zugreifen zu können (ID=die Bezeichnung, die in der Fahrwege.csv 
	 * bzw. Fahrplan.csv verwendet wird. Muss eindeutig sein!).
	 */
	private static HashMap<String, Strecke> id2strecke
			= new HashMap<String, Strecke>();
	
	/**
	 * Liest die Konfigurationsparameter aus der Datei "Konfig.csv" aus und übergibt
	 * sie Controller, Stellwerk und GUI. Hierbei werden Stellwerk und GUI entsprechend
	 * den Parametern initialisiert.
	 */
	public static void importiereKonfig() {
		Scanner scanner, zeilenscanner;
		try {
			FileReader filereader = new FileReader("Data/Konfig.csv");
			scanner = new Scanner(filereader);
		} catch (Exception e) {
			throw new Error("Fehlende Datei: Data\\Konfig.csv");
		}
		scanner.nextLine();
		String attribut;
		String bahnhofsname = "Bitte Bahnhofsnamen in Konfig.csv angeben!";
		Zeit startzeit = null;
		int verspaetungsanteil = 0;
		int verspaetungMax = 0;
		int geschwindigkeit = 30;
		int sekundenProZugbewegung = 15;
		int streckenbreite = 10;
		int betriebsstellenbreite = 30;
		int brueckenbreiteOben = 4;
		int brueckenbreiteUnten = 14;
		int schriftgroesseNetz = 16;
		while (scanner.hasNext()) {
			zeilenscanner = new Scanner(scanner.nextLine());
			zeilenscanner.useDelimiter(";");
			attribut = zeilenscanner.next();

			if (attribut.equals("Bahnhofsname")) {
				bahnhofsname = zeilenscanner.next();
				continue;
			}
			if (attribut.equals("Startzeit")) {
				startzeit = new Zeit(zeilenscanner.next());
				continue;
			}
			if (attribut.equals("Anteil verspäteter Züge (%)")) {
				verspaetungsanteil = zeilenscanner.nextInt();
				continue;
			}
			if (attribut.equals("Maximale Verspätung (min)")) {
				verspaetungMax = zeilenscanner.nextInt();
				continue;
			}
			if (attribut.equals(
					"Startgeschwindigkeit (Aktionen pro Echtzeit-Minute)")) {
				geschwindigkeit = zeilenscanner.nextInt();
				continue;
			}
			if (attribut.equals("Sekunden pro Zugbewegung")) {
				sekundenProZugbewegung = zeilenscanner.nextInt();
				continue;
			}
			if (attribut.equals("Streckenbreite (Pixel)")) {
				streckenbreite = zeilenscanner.nextInt();
				continue;
			}
			if (attribut.equals("Betriebsstellenbreite (Pixel)")) {
				betriebsstellenbreite = zeilenscanner.nextInt();
				continue;
			}
			if (attribut.equals("Brückenbreite oben (Pixel)")) {
				brueckenbreiteOben = zeilenscanner.nextInt();
				continue;
			}
			if (attribut.equals("Brückenbreite unten (Pixel)")) {
				brueckenbreiteUnten = zeilenscanner.nextInt();
				continue;
			}
			if (attribut.equals("Schriftgröße Netz")) {
				schriftgroesseNetz = zeilenscanner.nextInt();
				continue;
			}
		}
		scanner.close();
		Controller.getInstance().setGeschwindigkeit(geschwindigkeit);
		Stellwerk.createInstance(bahnhofsname, startzeit, verspaetungsanteil,
				verspaetungMax, sekundenProZugbewegung);
		GUI.createInstance(streckenbreite, betriebsstellenbreite, brueckenbreiteOben,
				brueckenbreiteUnten, schriftgroesseNetz);
	}
	
	/**
	 * Liest die Strecken aus der externen Datei Strecken.csv aus und speichert sie.
	 */
	static void importiereStreckennetz() {
		Scanner scanner, zeilenscanner, detailscanner;
		try {
			FileReader filereader = new FileReader("Data/Strecken.csv");
			scanner = new Scanner(filereader);
		} catch (Exception e) {
			throw new Error("Fehlende Datei: Data\\Strecken.csv");
		}
		scanner.nextLine();
		String id, nameNetz, nameFahrplan, bruecke;
		boolean hatBahnsteig;
		Boolean istBrueckeOben;
		Richtung anschlussrichtung;
		ArrayList<Richtung> anschlussrichtungen;
		int positionX, positionY, positionZugX, positionZugY,
				positionNameX, positionNameY;
		while (scanner.hasNext()) {
			zeilenscanner = new Scanner(scanner.nextLine());
			zeilenscanner.useDelimiter(";");
			id = zeilenscanner.next();
			System.out.println(id);
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
			positionZugX = zeilenscanner.nextInt();
			positionZugY = zeilenscanner.nextInt();
			bruecke = zeilenscanner.next();
			istBrueckeOben = null;
			if (bruecke.equals("oben"))
				istBrueckeOben = true;
			else if (bruecke.equals("unten"))
				istBrueckeOben = false;
			if (!zeilenscanner.hasNext() || !zeilenscanner.next().equals("x")) {
				Importer.id2strecke.put(id, new Strecke(
						positionX, positionY, anschlussrichtungen,
						istBrueckeOben, positionZugX, positionZugY));
				continue;
			}
			hatBahnsteig = false;
			if (zeilenscanner.next().equals("x"))
				hatBahnsteig = true;
			nameFahrplan = zeilenscanner.next();
			nameNetz = zeilenscanner.next();
			positionNameX = zeilenscanner.nextInt();
			positionNameY = zeilenscanner.nextInt();
			Importer.id2strecke.put(id, new Betriebsstelle(
					nameNetz, nameFahrplan, hatBahnsteig,
					positionX, positionY, anschlussrichtungen,
					positionZugX, positionZugY, positionNameX, positionNameY));
		}
		scanner.close();
	}
	
	/**
	 * Liest die Fahrwege aus der externen Datei Fahrwege.csv aus und speichert sie.
	 */
	static void importiereFahrwege() {
		Scanner scanner, trassenscanner, streckenscanner;
		try {
			FileReader filereader = new FileReader("Data/Fahrwege.csv");
			scanner = new Scanner(filereader);
		} catch (Exception e) {
			throw new Error("Fehlende Datei: Data\\Strecken.csv");
		}
		ArrayList<Strecke> durchfahreneStrecken;
		HashMap<Strecke, Richtung> fahrtrichtungen;
		ArrayList<Betriebsstelle> alleFahrziele = new ArrayList<Betriebsstelle>();
		ArrayList<Betriebsstelle> moeglicheFahrziele;
		HashMap<Betriebsstelle, Boolean> fahrzielFuehrtUeberBahnhof;
		String name;
		Strecke strecke;
		Fahrweg neuerFahrweg = null;
		Richtung letzteFahrtrichtung = null;
		Betriebsstelle start, ziel;
		Scanner ueberschriftenscanner = new Scanner(scanner.nextLine());
		ueberschriftenscanner.useDelimiter(";");
		while (ueberschriftenscanner.hasNext()) {
			name = ueberschriftenscanner.next();
			if (name.equals("Laufweg"))
				break;
			alleFahrziele.add(Importer.getBetriebsstelle(name));
			if (Importer.getStrecke(name) == null) {
				ueberschriftenscanner.close();
				scanner.close();
				throw new Error("Ungültiges Fahrziel in Fahrwegen: " + name);
			}
		}
		ueberschriftenscanner.close();
		while (scanner.hasNext()) {
			// "%" voranstellen, da der Scanner sonst nicht mit einem leeren Feld am
			// Anfang klarkommt
			trassenscanner = new Scanner("%" + scanner.nextLine());
			if (!trassenscanner.hasNext())
				continue;
			moeglicheFahrziele = new ArrayList<Betriebsstelle>();
			fahrzielFuehrtUeberBahnhof = new HashMap<Betriebsstelle, Boolean>();
			durchfahreneStrecken = new ArrayList<Strecke>();
			fahrtrichtungen = new HashMap<Strecke, Richtung>();
			trassenscanner.useDelimiter(";");
			for (int fahrziel = 0; fahrziel < alleFahrziele.size(); fahrziel++) {
				name = trassenscanner.next();
				if (name.equals("b") || name.equals("%b")) {
					moeglicheFahrziele.add(alleFahrziele.get(fahrziel));
					fahrzielFuehrtUeberBahnhof.put(alleFahrziele.get(fahrziel), true);
				} else if (name.equals("x") || name.equals("%x")) {
					moeglicheFahrziele.add(alleFahrziele.get(fahrziel));
					fahrzielFuehrtUeberBahnhof.put(alleFahrziele.get(fahrziel), false);
				} else if (!name.equals("") && !name.equals("%")) {
					scanner.close();
					trassenscanner.close();
					throw new Error("Unzulässiger Eintrag in Fahrwegen. Es sind nur x " +
							"und b erlaubt!");
				}
			}
			while (trassenscanner.hasNext()) {
				streckenscanner = new Scanner(trassenscanner.next());
				streckenscanner.useDelimiter("\\$");
				if (!streckenscanner.hasNext())
					break;
				name = streckenscanner.next();
				if (name.startsWith("%"))
					name = name.replace("%", "");
				strecke = Importer.getStrecke(name);
				if (strecke == null) {
					scanner.close();
					streckenscanner.close();
					trassenscanner.close();
					throw new Error("Ungültige Strecke in Fahrwegen: " + name);
				}
				durchfahreneStrecken.add(strecke);
				if (durchfahreneStrecken.size() == 1 && !streckenscanner.hasNext()) {
					scanner.close();
					streckenscanner.close();
					trassenscanner.close();
					throw new Error ("Richtungsangabe am Startbahnhof "
							+ name + " fehlt!");
				}
				if (streckenscanner.hasNext()) {
					letzteFahrtrichtung = Richtung.getRichtung(streckenscanner.next());
					fahrtrichtungen.put(strecke, letzteFahrtrichtung);
					continue;
				}
				for (Richtung richtung : strecke.getAnschlussrichtungen()) {
					if (!richtung.equals(letzteFahrtrichtung.getGegenrichtung())) {
						letzteFahrtrichtung = richtung;
						break;
					}
				}
				fahrtrichtungen.put(strecke, letzteFahrtrichtung);
			}
			neuerFahrweg = new Fahrweg(durchfahreneStrecken, fahrtrichtungen,
					moeglicheFahrziele, fahrzielFuehrtUeberBahnhof);
			start = (Betriebsstelle)
					durchfahreneStrecken.get(0);
			ziel = (Betriebsstelle)
					durchfahreneStrecken.get(durchfahreneStrecken.size()-1);
			start.addAusgehendenFahrweg(neuerFahrweg);
			ziel.addEingehendenFahrweg(neuerFahrweg);
		}
		scanner.close();
	}
	
	/**
	 * Liest den Fahrplan (also die Züge) aus der externen Datei
	 * Fahrplan.csv aus und speichert die Züge ab.
	 */
	static void importiereFahrplan() {
		Scanner scanner, zugscanner, detailscanner;
		try {
			FileReader filereader = new FileReader("Data/Fahrplan.csv");
			scanner = new Scanner(filereader);
		} catch (Exception e) {
			throw new Error("Fehlende Datei: Data\\Fahrplan.csv");
		}
		scanner.nextLine();
		int zugnummer, haengtAnZugnummer, zugnummerNeu, minimalerAufenthalt;
		boolean fluegelt, fluegeltNachVorne;
		ArrayList<Integer> zuegeSelbesGleis, vereinigungMit;
		String gattung, vonBahnhof, vonStreckeString, nachBahnhof, nachStreckeString,
				viaString, gattungNeu, fluegelrichtung;
		Richtung ausRichtung;
		Betriebsstelle vonStrecke, nachStrecke, viaGleis;
		Zeit ankunft, abfahrt, rangiertUm;
		while (scanner.hasNext()) {
			zugscanner = new Scanner(scanner.nextLine());
			zugscanner.useDelimiter(";");
			gattung = zugscanner.next();
			zugnummer = zugscanner.nextInt();
			detailscanner = new Scanner(zugscanner.next());
			detailscanner.useDelimiter("\\$");
			haengtAnZugnummer = 0;
			fluegelt = false;
			fluegeltNachVorne = false;
			if (detailscanner.hasNext()) {
				haengtAnZugnummer = detailscanner.nextInt();
				if (detailscanner.hasNext()) {
					fluegelt = true;
					fluegelrichtung = detailscanner.next();
					if (fluegelrichtung.equals("v")) {
						fluegeltNachVorne = true;
					} else if (fluegelrichtung.equals("h")) {
						fluegeltNachVorne = false;
					} else {
						scanner.close();
						detailscanner.close();
						zugscanner.close();
						throw new Error ("Ungültige Flügelrichtung " +
								"bei Zug: " + zugnummer);
					}
				}
			}
			vonBahnhof = zugscanner.next();
			vonStreckeString = zugscanner.next();
			try {
				vonStrecke = Importer.getBetriebsstelle(vonStreckeString);
			} catch (Exception e) {
				scanner.close();
				zugscanner.close();
				detailscanner.close();
				throw new Error("Ungültige Strecke im Fahrplan: " + vonStreckeString);
			}
			ausRichtung = Richtung.getRichtung(zugscanner.next());
			if (ausRichtung.equals(null)) {
				scanner.close();
				zugscanner.close();
				detailscanner.close();
				throw new Error("Ungültige Richtungsangabe bei Zug " + zugnummer);
			}
			nachBahnhof = zugscanner.next();
			nachStreckeString = zugscanner.next();
			try {
				nachStrecke = Importer.getBetriebsstelle(nachStreckeString);
			} catch (Exception e) {
				scanner.close();
				zugscanner.close();
				detailscanner.close();
				throw new Error("Ungültige Strecke im Fahrplan: " + nachStreckeString);
			}
			viaString = zugscanner.next();
			viaGleis = Importer.getBetriebsstelle(viaString);
			ankunft = new Zeit(zugscanner.next());
			// Güterzug erkennen
			if (viaGleis == null) {
				zugscanner.next();
				zugscanner.next();
				abfahrt = null;
				minimalerAufenthalt = 0;
			} else {
				abfahrt = new Zeit(zugscanner.next());
				minimalerAufenthalt = zugscanner.nextInt();
			}
			try {
				rangiertUm = new Zeit(zugscanner.next());
			} catch (IllegalArgumentException e) {
				rangiertUm = null;
			}
			gattungNeu = "";
			zugnummerNeu = 0;
			detailscanner.close();
			detailscanner = new Scanner(zugscanner.next());
			if (detailscanner.hasNext()) {
				gattungNeu = detailscanner.next();
				zugnummerNeu = detailscanner.nextInt();
				if (zugnummerNeu == zugnummer)
					zugnummerNeu = 0;
			}
			vereinigungMit = new ArrayList<Integer>();
			detailscanner.close();
			detailscanner = new Scanner(zugscanner.next());
			if (detailscanner.hasNext()) {
				detailscanner.useDelimiter("\\$");
				while (detailscanner.hasNext())
					vereinigungMit.add(detailscanner.nextInt());
			}
			zuegeSelbesGleis = new ArrayList<Integer>();
			if (zugscanner.hasNext()) {
				detailscanner.close();
				detailscanner = new Scanner(zugscanner.next());
				detailscanner.useDelimiter("\\$");
				while(detailscanner.hasNext())
					zuegeSelbesGleis.add(detailscanner.nextInt());
			}
			detailscanner.close();
			new Zug(gattung, zugnummer, haengtAnZugnummer, fluegelt, fluegeltNachVorne,
					vonBahnhof, vonStrecke,
					ausRichtung, nachBahnhof, nachStrecke, viaGleis, ankunft, abfahrt,
					minimalerAufenthalt, rangiertUm, gattungNeu, zugnummerNeu,
					vereinigungMit, zuegeSelbesGleis);
		}
		scanner.close();
	}
	
	/**
	 * Gibt zu einer ID das entsprechende erzeugte Streckenobjekt zurück.
	 * 
	 * @param id
	 * 		Die eindeutige Bezeichnung, die in der Fahrwege.csv 
	 * 		bzw. Fahrplan.csv verwendet wird.
	 */
	private static Strecke getStrecke(final String id) {
		return Importer.id2strecke.get(id);
	}
	
	/**
	 * Gibt zu einer ID das entsprechende erzeugte Betriebsstellenobjekt zurück.
	 * 
	 * @param id
	 * 		Die eindeutige Bezeichnung, die in der Fahrwege.csv 
	 * 		bzw. Fahrplan.csv verwendet wird.
	 */
	private static Betriebsstelle getBetriebsstelle(final String id) {
		return (Betriebsstelle) Importer.id2strecke.get(id);
	}
}