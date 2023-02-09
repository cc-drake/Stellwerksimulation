package de.drake.stellwerksimulation.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.drake.stellwerksimulation.tools.NaturalOrderComparator;
import de.drake.stellwerksimulation.tools.Richtung;

/**
 * Eine Betriebsstelle beschreibt eine Betriebsstelle in der Simulation. Sie kann
 * ein Bahnhofsgleis oder ein Übergabepunkt für Züge sein. Die durch die
 * Betriebsstelle verlaufende Strecke ist durch Polymorphie in der Betriebsstelle
 * enthalten.
 */
class Betriebsstelle extends BetriebsstelleFuerGUI {
	
	/**
	 * Eine Liste aller Betriebsstellen der Simulation.
	 */
	private static ArrayList<Betriebsstelle> alleBetriebsstellen
			= new ArrayList<Betriebsstelle>();
	
	/**
	 * Gibt an, ob an dieser Betriebsstelle Fahrgastwechsel möglich ist.
	 */
	private boolean hatBahnsteig;
	
	/**
	 * Eine Liste aller möglichen Fahrwege, die ein sich hier befindlicher Zug
	 * nehmen kann.
	 */
	private ArrayList<Fahrweg> ausgehendeFahrwege = new ArrayList<Fahrweg>();
	
	/**
	 * Eine Liste aller Fahrwege, die in diese Betriebsstelle münden
	 */
	private ArrayList<Fahrweg> eingehendeFahrwege = new ArrayList<Fahrweg>();

	/**
	 * Alle Züge, die zünftig hier in das simulierte Streckennetz einfahren werden
	 */
	private ConcurrentLinkedQueue<Zug> erwarteteZuege
			= new ConcurrentLinkedQueue<Zug>();
	
	/**
	 * Erzeugt eine neue Betriebsstelle.
	 * 
	 * @param nameNetz
	 * 		Der Name der Betriebsstelle, wie er in der grafischen Darstellung des
	 * 		Netzes auftauchen soll
	 * @param nameFahrplan
	 * 		Der Name der Betriebsstelle, wie er im Fahrplan auftauchen soll
	 * @param hatBahnsteig
	 * 		Gibt an, ob die Betriebsstelle für Fahrgastwechsel geeignet ist oder nicht
	 * @param positionX
	 * 		Die Position der Betriebsstelle im Koordinatensystem (X-Koordinate)
	 * @param positionY
	 * 		Die Position der Betriebsstelle im Koordinatensystem (Y-Koordinate)
	 * @param anschlussrichtungen
	 * 		Die beiden Richtungen, über die die Betriebsstelle erreicht werden kann.
	 * @param positionZugnummerX
	 * 		Die Position der Zugnummernanzeige in der GUI relativ zur Streckenmitte
	 * 		(X-Koordinate).
	 * @param positionZugnummerY
	 * 		Die Position der Zugnummernanzeige in der GUI relativ zur Streckenmitte
	 * 		(Y-Koordinate).
	 * @param positionBahnhofsnameX
	 * 		Die Position der Betriebsstellen-Namensanzeige in der GUI relativ zur
	 * 		Position der Betriebsstelle (X-Koordinate).
	 * @param positionBahnhofsnameY
	 * 		Die Position der Betriebsstellen-Namensanzeige in der GUI relativ zur
	 * 		Position der Betriebsstelle (Y-Koordinate).
	 */
	Betriebsstelle(final String nameNetz, final String nameFahrplan,
			final boolean hatBahnsteig,
			final int positionX, final int positionY,
			final ArrayList<Richtung> anschlussrichtungen, final int positionZugnummerX,
			final int positionZugnummerY, final int positionBahnhofsnameX,
			final int positionBahnhofsnameY) {
		super(nameNetz, nameFahrplan, positionX, positionY, anschlussrichtungen, positionZugnummerX,
				positionZugnummerY, positionBahnhofsnameX, positionBahnhofsnameY);
		this.hatBahnsteig = hatBahnsteig;
		Betriebsstelle.alleBetriebsstellen.add(this);
		Collections.sort(Betriebsstelle.alleBetriebsstellen,
				new NaturalOrderComparator<Betriebsstelle>());
	}

	/**
	 * Fügt der Betriebsstelle einen Fahrweg hinzu, der die Betriebsstelle
	 * als Ausgangspunkt hat.
	 */
	void addAusgehendenFahrweg(final Fahrweg neuerFahrweg) {
		this.ausgehendeFahrwege.add(neuerFahrweg);
	}
	
	/**
	 * Stellt einen einen Zug, der im Laufe des Tages an der entsprechenden
	 * Betriebsstelle in die Simulation einfahren soll, in die Warteschlange.
	 */
	void addErwartetenZug(final Zug zug) {
		this.erwarteteZuege.add(zug);
	}

	/**
	 * Fertigt ausfahrbereite Züge im Gleis ab. Zudem werden hier neue Züge in
	 * die Simulation hinzugefügt oder "fertige" Züge entfernt.
	 */
	void verarbeiteZuegeImGleis() {
		this.entferneZuegeAusSimulation();
		this.fertigeZuegeAb();
		this.lasseNeueZuegeEintreffen();
	}
	
	/**
	 * Entfernt Züge, die die Simulation vollständig durchfahren haben,
	 * aus der Simulation.
	 */
	private void entferneZuegeAusSimulation() {
		for (Zug zug : new ArrayList<Zug>(this.getZuegeImGleis())) {
			if (zug.hatZielErreicht()) {
				this.verarbeiteAbfahrendenZug(zug);
				zug.verlasseSimulation();
			}
		}
	}

	/**
	 * Fertigt die Züge im Gleis ab, d.h. bei ausfahrbereiten Zügen wird versucht,
	 * einen Fahrweg einzustellen.
	 */
	private void fertigeZuegeAb() {
		if (this.getZuegeImGleis().isEmpty())
			return;
		Zug zug;
		for (Richtung richtung : this.getAnschlussrichtungen()) {
			zug = this.getZuegeImGleis().peek(richtung);
			if (!zug.wartet()) {
				continue;
			}
			for (Fahrweg fahrweg : this.ausgehendeFahrwege) {
				if (fahrweg.istEingestellt()
						&& fahrweg.getFahrtrichtung(this).equals(richtung)
						&& fahrweg.kannBefahrenWerdenVonZug(zug)) {
					fahrweg.befahreDurch(zug);
					zug.setFahrweg(fahrweg);
					break;
				}
			}
		}
	}
	
	/**
	 * Lässt einen neuen Zug in die Simulation einfahren, sofern dessen Ankunftszeit
	 * erreicht ist.
	 */
	private void lasseNeueZuegeEintreffen() {
		if (this.istBefahren() || this.erwarteteZuege.isEmpty()
				|| !this.erwarteteZuege.peek().getTatsaechlicheAnkunft()
				   .istFrueherOderZeitgleichAls(
				   Stellwerk.getInstance().getAktuelleZeit())) {
			return;
		}
		Zug eintreffenderZug = this.erwarteteZuege.peek();
		for (Fahrweg fahrweg : this.ausgehendeFahrwege) {
			if (fahrweg.istEingestellt()
					&& fahrweg.kannBefahrenWerdenVonZug(eintreffenderZug)) {
				fahrweg.befahreDurch(eintreffenderZug);
				eintreffenderZug.setFahrweg(fahrweg);
				this.erwarteteZuege.poll();
				break;
			}
		}
	}
	
	/**
	 * Verarbeitet die Auswirkungen eines Zuges, der in diese Betriebsstelle
	 * einfährt.
	 * 
	 * @param zug
	 * 		Der Zug, der gerade ankommt.
	 * @param ankunftsrichtung
	 * 		Die Richtung, aus der der Zug ankommt.
	 */
	@Override
	void verarbeiteAnkommendenZug(final Zug zug, final Richtung ankunftsrichtung) {
		if (this.hatBahnsteig)
			for (Zug fluegelzug : new ArrayList<Zug>(zug.getVereinigteZuege())) {
				if (fluegelzug.fluegelt() && fluegelzug.fluegeltNachVorne())
					fluegelzug.fluegle();
			}
		Zug vornedranStehenderZug = null;
		if (!this.istFrei()) {
			vornedranStehenderZug = this.getZuegeImGleis().peek(ankunftsrichtung);
		}
		super.verarbeiteAnkommendenZug(zug, ankunftsrichtung);
		if (!this.hatBahnsteig || zug.faehrt()) {
			zug.aktualisiereAbfahrtszeit(false);
			return;
		}
		for (Zug fluegelzug : new ArrayList<Zug>(zug.getVereinigteZuege())) {
			if (fluegelzug.fluegelt())
				fluegelzug.fluegle();
		}
		zug.aktualisiereAbfahrtszeit(!zug.hatFahrgastwechselErledigt());
		zug.wechsleGattungUndZugnummer();
		zug.setFahrgastwechselErledigt();
		for (Zug fluegelzug : zug.getVereinigteZuege()) {
			fluegelzug.wechsleGattungUndZugnummer();
			fluegelzug.setFahrgastwechselErledigt();
		}
		if (vornedranStehenderZug == null)
			return;
		if (zug.getVereinigungMit().contains(vornedranStehenderZug)) {
			zug.vereinigeMit(vornedranStehenderZug);
			vornedranStehenderZug.aktualisiereAbfahrtszeit(true);
			return;
		}
		if (vornedranStehenderZug.getVereinigungMit().contains(zug)) {
			vornedranStehenderZug.vereinigeMit(zug);
			vornedranStehenderZug.aktualisiereAbfahrtszeit(true);
		}
	}
	
	/**
	 * Gibt an, ob Fahrwege zu dieser Betriebsstelle bzw. von dieser Betriebsstelle
	 * ausgehend eingestellt sind.
	 * Wird für strecke.verarbeiteAbfahrendenZug() verwendet.
	 */
	@Override
	boolean hatEingestellteFahrwege() {
		for (Fahrweg fahrweg : this.eingehendeFahrwege) {
			if (fahrweg.istEingestellt() || fahrweg.istBefahren())
				return true;
		}
		for (Fahrweg fahrweg : this.ausgehendeFahrwege) {
			if (fahrweg.istEingestellt())
				return true;
		}
	return false;
	}
	
	/**
	 * Gibt einen Fahrweg zurück, der von dieser Betriebsstelle ausgeht. Existiert
	 * kein Fahrweg mit dem angegebenen Ziel, so wird null zurückgegeben.
	 * 
	 * @param ziel
	 * 		Das Ende der gesuchten Betriebsstelle
	 */
	Fahrweg getAusgehendenFahrweg(final BetriebsstelleFuerGUI ziel) {
		for (Fahrweg fahrweg : this.ausgehendeFahrwege) {
			if (fahrweg.getEndeDesFahrweges().equals(ziel))
				return fahrweg;
		}
		return null;
	}
	
	/**
	 * Gibt alle Fahrwege zurück, die in diese Betriebsstelle münden.
	 */
	ArrayList<Fahrweg> getEingehendeFahrwege() {
		return this.eingehendeFahrwege;
	}
	
	/**
	 * Hinterlegt einen eingehenden eingestellten Fahrweg.
	 * 
	 * @param fahrweg
	 * 		Der Fahrweg, der in diese Betriebsstelle eingestellt wurde.
	 */
	void addEingehendenFahrweg(Fahrweg fahrweg) {
		this.eingehendeFahrwege.add(fahrweg);
	}
	
	/**
	 * Gibt die Liste der Züge zurück, die hier ihre Fahrt beginnen werden.
	 */
	ConcurrentLinkedQueue<Zug> getErwarteteZuege() {
		return this.erwarteteZuege;
	}
	
	/**
	 * Gibt eine Liste aller Betriebsstellen der Simulation zurück.
	 */
	static ArrayList<Betriebsstelle> getAlleBetriebsstellen() {
		return Betriebsstelle.alleBetriebsstellen;
	}
	
	/**
	 * Gibt zurück, ob diese Betriebsstelle einen Bahnsteig für Fahrgastwechsel besitzt.
	 */
	boolean hatBahnsteig() {
		return this.hatBahnsteig;
	}

	/**
	 * Gibt zurück, ob derzeit ein Zug unterwegs zu dieser Betriebsstelle ist.
	 */
	boolean hatEinfahrendenZug() {
		for (Fahrweg fahrweg : this.getEingehendeFahrwege()) {
			if (fahrweg.istBefahren())
				return true;
		}
		return false;
	}
}