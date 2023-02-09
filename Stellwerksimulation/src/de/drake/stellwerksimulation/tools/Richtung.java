package de.drake.stellwerksimulation.tools;

import java.util.ArrayList;

/**
 * Alle Richtungen, in die eine Strecke verlaufen kann.
 */
public enum Richtung {
	
	/**
	 * Repr�sentiert die Himmelsrichtung Nord.
	 */
	NORD,

	/**
	 * Repr�sentiert die Himmelsrichtung Nordwest.
	 */
	NORDWEST,
	
	/**
	 * Repr�sentiert die Himmelsrichtung Nordost.
	 */
	NORDOST,
	
	/**
	 * Repr�sentiert die Himmelsrichtung West.
	 */
	WEST,
	
	/**
	 * Repr�sentiert die Himmelsrichtung Ost.
	 */
	OST,
	
	/**
	 * Repr�sentiert die Himmelsrichtung S�dwest.
	 */
	SUEDWEST,
	
	/**
	 * Repr�sentiert die Himmelsrichtung S�dost.
	 */
	SUEDOST,
	
	/**
	 * Repr�sentiert die Himmelsrichtung S�d.
	 */
	SUED;

	/**
	 * Stellt eine Richtung als String dar. Inhalt des Strings ist der Name der
	 * entsprechenden Himmelsrichtung.
	 * 
	 * @return der erstellte String
	 */
	@Override
	public String toString() {
		switch (this) {
		case NORD: return "N";
		case NORDOST: return "NO";
		case OST: return "O";
		case SUEDOST: return "SO";
		case SUED: return "S";
		case SUEDWEST: return "SW";
		case WEST: return "W";
		case NORDWEST: return "NW";
		}
		return "";
	}
	
	/**
	 * Gibt zu einer Kurzform einer Himmelsrichtung die Richtung zur�ck.
	 * 
	 * @param name
	 * 		Der (abgek�rzte) Name der Himmelsrichtung, z.B. "O" f�r Ost.
	 * 
	 * @return Die Richtung, die dem Text entspricht.
	 */
	public static Richtung getRichtung(final String name) {
		if (name.equals("N"))
			return Richtung.NORD;
		if (name.equals("NO"))
			return Richtung.NORDOST;
		if (name.equals("O"))
			return Richtung.OST;
		if (name.equals("SO"))
			return Richtung.SUEDOST;
		if (name.equals("S"))
			return Richtung.SUED;
		if (name.equals("SW"))
			return Richtung.SUEDWEST;
		if (name.equals("W"))
			return Richtung.WEST;
		if (name.equals("NW"))
			return Richtung.NORDWEST;
		return null;
	}
	
	/**
	 * Gibt zur aktuellen Richtung die Gegenrichtung zur�ck.
	 */
	public Richtung getGegenrichtung() {
		switch (this) {
		case NORD: return Richtung.SUED;
		case NORDOST: return Richtung.SUEDWEST;
		case OST: return Richtung.WEST;
		case SUEDOST: return Richtung.NORDWEST;
		case SUED: return Richtung.NORD;
		case SUEDWEST: return Richtung.NORDOST;
		case WEST: return Richtung.OST;
		case NORDWEST: return Richtung.SUEDOST;
		}
		return null;
	}
	
	/**
	 * Gibt zur aktuellen Richtung die Gegenrichtungen zur�ck.
	 */
	public ArrayList<Richtung> getGegenrichtungen() {
		ArrayList<Richtung> gegenrichtungen = new ArrayList<Richtung>(3);
		switch (this) {
		case NORD:
			gegenrichtungen.add(Richtung.SUEDOST);
			gegenrichtungen.add(Richtung.SUED);
			gegenrichtungen.add(Richtung.SUEDWEST);
			break;
		case NORDOST:
			gegenrichtungen.add(Richtung.SUED);
			gegenrichtungen.add(Richtung.SUEDWEST);
			gegenrichtungen.add(Richtung.WEST);
			break;
		case OST:
			gegenrichtungen.add(Richtung.SUEDWEST);
			gegenrichtungen.add(Richtung.WEST);
			gegenrichtungen.add(Richtung.NORDWEST);
			break;
		case SUEDOST:
			gegenrichtungen.add(Richtung.WEST);
			gegenrichtungen.add(Richtung.NORDWEST);
			gegenrichtungen.add(Richtung.NORD);
			break;
		case SUED:
			gegenrichtungen.add(Richtung.NORDWEST);
			gegenrichtungen.add(Richtung.NORD);
			gegenrichtungen.add(Richtung.NORDOST);
			break;
		case SUEDWEST:
			gegenrichtungen.add(Richtung.NORD);
			gegenrichtungen.add(Richtung.NORDOST);
			gegenrichtungen.add(Richtung.OST);
			break;
		case WEST:
			gegenrichtungen.add(Richtung.NORDOST);
			gegenrichtungen.add(Richtung.OST);
			gegenrichtungen.add(Richtung.SUEDOST);
			break;
		case NORDWEST:
			gegenrichtungen.add(Richtung.OST);
			gegenrichtungen.add(Richtung.SUEDOST);
			gegenrichtungen.add(Richtung.SUED);
		}
		return gegenrichtungen;
	}

	/**
	 * Gibt zur aktuellen Richtung an, ob diese in einem Kontext
	 * "Oben-Unten" als Unten angesehen werden kann.
	 */
	public boolean entsprichtUnten() {
		switch (this) {
			case NORD:
			case NORDWEST:
			case NORDOST:
			case WEST: return false;
		default:
			break;
		}
		return true;
	}
}