package de.drake.stellwerksimulation.model;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.drake.stellwerksimulation.tools.Richtung;

/**
 * Erweiterung einer Betriebsstelle, die als Interface für die GUI dient. 
 */
public abstract class BetriebsstelleFuerGUI extends Strecke {
	
	/**
	 * Beschreibt, dass eine Betriebsstelle derzeit "selektiert" ist, d.h. vom 
	 * Benutzer per Mausklick für eine Aktion ausgewählt wurde.
	 */
	public final static int SELEKTIERT = 0;
	
	/**
	 * Beschreibt, dass eine Betriebsstelle derzeit "unselektiert" ist. Beim Klick
	 * auf eine unselektierte Betriebsstelle soll diese selektiert werden, es sei
	 * denn dass bereits eine andere Betriebsstelle selektiert wurde - dann wird
	 * die Selektion aufgehoben.
	 */
	public final static int UNSELEKTIERT = 1;
	
	/**
	 * Beschreibt, dass derzeit mit Mausklick auf eine Betriebsstelle ein Fahrweg 
	 * zu dieser eingestellt werden kann, ausgehend von der selektierten Betriebstelle.
	 */
	public final static int FAHRWEG_EINSTELLBAR = 2;
	
	/**
	 * Beschreibt, dass derzeit ein Fahrweg in eine Betriebsstelle verläuft, der
	 * mit Mausklick aufgelöst werden kann.
	 */
	public final static int FAHRWEG_AUFLOESBAR = 3;
	
	/**
	 * Beschreibt, dass derzeit mit Mausklick auf eine Betriebsstelle ein Fahrweg 
	 * zu dieser eingestellt werden kann, ausgehend von der selektierten Betriebstelle.
	 * Die Betriebsstelle ist jedoch durch einen anderen Zug belegt (bzw. ein anderer 
	 * Zug fährt bereits in die Betriebsstelle ein).
	 */
	public final static int FAHRWEG_BELEGT = 4;
	
	/**
	 * Der Name der Betriebsstelle, wie er in der grafischen Darstellung des
	 * Netzes auftauchen soll.
	 */
	private String nameNetz;
	
	/**
	 * Der Name der Betriebsstelle, wie er im Fahrplan auftauchen soll.
	 */
	private String nameFahrplan;
	
	/**
	 * Die Position der Betriebsstellen-Namensanzeige in der GUI relativ zur
	 * Position der Betriebsstelle (X-Koordinate).
	 */
	private int positionBahnhofsnameX;
	
	/**
	 * Die Position der Betriebsstellen-Namensanzeige in der GUI relativ zur
	 * Position der Betriebsstelle (Y-Koordinate).
	 */
	private int positionBahnhofsnameY;

	/**
	 * Erzeugt eine neue Betriebsstelle.
	 * 
	 * @param nameNetz
	 * 		Der Name der Betriebsstelle, wie er in der grafischen Darstellung des
	 * 		Netzes auftauchen soll
	 * @param nameFahrplan
	 * 		Der Name der Betriebsstelle, wie er im Fahrplan auftauchen soll
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
	BetriebsstelleFuerGUI(final String nameNetz, final String nameFahrplan,
			final int positionX, final int positionY,
			final ArrayList<Richtung> anschlussrichtungen,
			final int positionZugnummerX, final int positionZugnummerY,
			final int positionBahnhofsnameX, final int positionBahnhofsnameY) {
		super(positionX, positionY, anschlussrichtungen, null,
				positionZugnummerX, positionZugnummerY);
		this.nameNetz = nameNetz;
		this.nameFahrplan = nameFahrplan;
		this.positionBahnhofsnameX = positionBahnhofsnameX;
		this.positionBahnhofsnameY = positionBahnhofsnameY;
	}

	/**
	 * Gibt eine Liste aller Betriebsstellen zurück. Der Cast ist safe, da
	 * Betriebsstelle extends BetriebsstelleFuerGUI.
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<BetriebsstelleFuerGUI>
			getAlleBetriebsstellenFuerGUI() {
		return (ArrayList<BetriebsstelleFuerGUI>) (Object)
				Betriebsstelle.getAlleBetriebsstellen();
	}
	
	/**
	 * Gibt zurück, was beim Klick auf diese Betriebsstelle geschehen soll.
	 * Hat auch Auswirkungen auf die grafische Darstellung in der GUI.
	 * 
	 * @param selektierteBetriebsstelle
	 * 		Die Betriebsstelle, die zuletzt vom Benutzer selektiert ist.
	 * 		Ist keine Betriebsstelle selektiert, wird null übergeben.
	 */
	public int getKlickverhalten(final BetriebsstelleFuerGUI selektierteBetriebsstelle) {
		if (selektierteBetriebsstelle == null)
			return BetriebsstelleFuerGUI.UNSELEKTIERT;
		if (selektierteBetriebsstelle.equals(this))
			return BetriebsstelleFuerGUI.SELEKTIERT;
		Fahrweg fahrweg = selektierteBetriebsstelle.getAusgehendenFahrweg(this);
		if (fahrweg == null)
			return BetriebsstelleFuerGUI.UNSELEKTIERT;
		if (fahrweg.istEinstellbar()) {
			if (fahrweg.getEndeDesFahrweges().istBefahren()
					|| fahrweg.getEndeDesFahrweges().hatEinfahrendenZug()) {
				return BetriebsstelleFuerGUI.FAHRWEG_BELEGT;
			} else {
				return BetriebsstelleFuerGUI.FAHRWEG_EINSTELLBAR;
			}
		}
		if (fahrweg.istAufloesbar())
			return BetriebsstelleFuerGUI.FAHRWEG_AUFLOESBAR;
		return BetriebsstelleFuerGUI.UNSELEKTIERT;
	}
	
	/**
	 * Gibt an, ob innerhalb der nächsten 3 Minuten Züge an dieser Betriebsstelle 
	 * erwartet werden.
	 */
	public boolean zuegeErwartet() {
		for (Zug zug : this.getErwarteteZuege()) {
			if (zug.wartet())
				continue;
			if (zug.faehrtBaldAb())
				return true;
			return false;
		}
		return false;
	}
	
	/**
	 * Gibt die Liste der Züge zurück, die hier ihre Fahrt beginnen werden.
	 */
	abstract ConcurrentLinkedQueue<Zug> getErwarteteZuege();
	
	/**
	 * Gibt die Liste der Züge zurück, die an dieser Betriebsstelle erwartet werden und
	 * die eigentlich schon da sein sollten, aber noch auf freie Einfahrt warten.
	 */
	public ArrayList<Fahrplaneintrag> getWartendeErwarteteZuege() {
		ArrayList<Fahrplaneintrag> result = new ArrayList<Fahrplaneintrag>();
		for (Fahrplaneintrag zug : this.getErwarteteZuege()) {
			if (!zug.wartet())
				break;
			result.add(zug);
		}
		return result;
	}
	
	/**
	 * Gibt den Namen der Betriebsstelle zurück, wie er in der grafischen
	 * Darstellung des Netzes auftauchen soll.
	 */
	public String getNameNetz() {
		return this.nameNetz;
	}
	
	/**
	 * Der Name der Betriebsstelle, wie er im Fahrplan auftauchen soll.
	 */
	String getNameFahrplan() {
		return this.nameFahrplan;
	}
	
	/**
	 * Der Name der Betriebsstelle, wie er zur Sortierung der Betriebsstellen
	 * genutzt werden soll.
	 */
	public String toString() {
		return this.nameFahrplan;
	}
	
	/**
	 * Gibt die Position der Betriebsstellen-Namensanzeige in der GUI relativ zur
	 * Position der Betriebsstelle zurück (X-Koordinate).
	 */
	public int getpositionBahnhofsnameX() {
		return this.positionBahnhofsnameX;
	}
	
	/**
	 * Gibt die Position der Betriebsstellen-Namensanzeige in der GUI relativ zur
	 * Position der Betriebsstelle zurück (Y-Koordinate).
	 */
	public int getpositionBahnhofsnameY() {
		return this.positionBahnhofsnameY;
	}
	
	/**
	 * Gibt einen Fahrweg zurück, der von dieser Betriebsstelle ausgeht. Existiert
	 * kein Fahrweg mit dem angegebenen Ziel, so wird null zurückgegeben.
	 * 
	 * @param ziel
	 * 		Das Ende der gesuchten Betriebsstelle
	 */
	abstract Fahrweg getAusgehendenFahrweg(final BetriebsstelleFuerGUI ziel);
}