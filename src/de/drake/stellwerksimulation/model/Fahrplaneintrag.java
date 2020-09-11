package de.drake.stellwerksimulation.model;

import java.util.ArrayList;
import java.util.Vector;

import de.drake.stellwerksimulation.tools.Zeit;

public abstract class Fahrplaneintrag {
	
	/**
	 * Gibt an, dass der Zug derzeit ohne Zeitdruck in einem Bahnhof rumsteht oder
	 * außerhalb der Simulation ist.
	 */
	final static public int ZUG_STEHT = 0;
	
	/**
	 * Gibt an, dass der Zug derzeit unterwegs ist.
	 */
	final static public int ZUG_FAEHRT = 1;
	
	/**
	 * Gibt an, dass der Zug derzeit auf einen Fahrweg wartet.
	 */
	final static public int ZUG_WARTET = 2;
	
	/**
	 * Gibt an, dass der Zug innerhalb der nächsten 3 Minuten abfahren möchte.
	 */
	final static public int ZUG_FAEHRT_BALD_AB = 3;
	
	/**
	 * Eine Liste aller wartenden Züge, die im Fahrplanpanel dargestellt werden sollen.
	 */
	private static Vector<Zug> wartendeZuege = new Vector<Zug>();
	
	/**
	 * Eine Liste aller Züge, die in der Gleisbelegung auftauchen sollen.
	 */
	private static Vector<Zug> zuegeAmBahnsteig = new Vector<Zug>();
	
	/**
	 * Herkunftsbahnhof (z.B. Hamburg-Altona) zur Anzeige im Fahrplanfenster
	 */
	private String herkunftsbahnhof;
	
	/**
	 * Zielbahnhof (z.B. München Hbf) zur Anzeige im Fahrplanfenster
	 */
	private String zielbahnhof;
	
	/**
	 * Die Betriebsstelle (also das Gleis), über das der Zug fahren sollte
	 */
	private Betriebsstelle viaGleis;
	
	/**
	 * Legt einen neuen Fahrplaneintrag an.
	 * 
	 * @param herkunftsbahnhof
	 * 		Herkunftsbahnhof (z.B. Hamburg-Altona) zur Anzeige im Fahrplanfenster
	 * @param zielbahnhof
	 * 		Zielbahnhof (z.B. München Hbf) zur Anzeige im Fahrplanfenster
	 * @param viaGleis
	 * 		Die Betriebsstelle (also das Gleis), über das der Zug fahren sollte
	 */
	Fahrplaneintrag(final String herkunftsbahnhof, final String zielbahnhof,
			final Betriebsstelle viaGleis) {
		this.herkunftsbahnhof = herkunftsbahnhof;
		this.zielbahnhof = zielbahnhof;
		this.viaGleis = viaGleis;
	}
	
	/**
	 * Gibt die Bezeichnung des Zugverbands (z.B. ICE 105/505) als String zurück.
	 */
	@Override
	public String toString() {
		String result = this.getGattung() + " " + this.getZugnummer();
		for (Fahrplaneintrag fluegelzug : this.getVereinigteZuege()) {
			result += "/" + fluegelzug.getZugnummer();
		}
		return result;
	}
	
	/**
	 * Gibt eine kurze Beschreibung des Zuges aus, die für die Auswahlmenüs des
	 * Fahrplanpanels genutzt werden kann.
	 */
	public String getShortDescription() {
		String result = " ";
		result += this.getTatsaechlicheAnkunft();
		result += " " + this.toString();
		if (this.getVerspaetung() > 0)
			result += " (+" + this.getVerspaetung() + ")";
		result += " ";
		return result;
	}
	
	/**
	 * Gibt eine kurze Beschreibung des Zuges aus, die für die Anzeige im
	 * Gleisbelegungsmenü der GUI verwendet werden kann.
	 */
	public String getGleisbelegungsDescription() {
		String result = " ";
		result += this.getPosition().getNameFahrplan();
		result += ": " + this.getTatsaechlicheAbfahrt();
		result += " " + this.toString();
		if (this.rangierenNotwendig()) {
			result += " (Vereinigung mit ";
			boolean ersterEintrag = true;
			for (Fahrplaneintrag zug : this.getVereinigungMit()) {
				if (!ersterEintrag)
					result += "/";
				ersterEintrag = false;
				result += zug.getGattung() + " " + zug.getZugnummer();
			}
			result += ") ";
		} else {
			result += " nach " + this.getZielbahnhoefe();
			result += " (" + this.getZielBetriebsstelle().getNameFahrplan();
			result += ") ";
		}
		return result;
	}
	
	/**
	 * Stellt eine Beschreibung des Zuges für die Detailanzeige in der GUI zusammen.
	 */
	public String getDescription() {
		String result = "";
		result += "Von  " + this.getHerkunftsbahnhoefe();
		if (!this.istInSimulation()) {
			result +=  " (" + this.getStartBetriebsstelle().getNameFahrplan() + ")";
		}
		result += "\n\nNach " + this.getZielbahnhoefe();
		if (this.getZugnummerNeu() == 0 || this.hatFahrgastwechselErledigt()) {
			result +=  " (" + this.getZielBetriebsstelle().getNameFahrplan() + ")";
		}
		if (this.viaGleis != null) {
			result += "\n\nÜber " + this.viaGleis.getNameFahrplan();
			if (this.getZugnummerNeu() == 0 || this.hatFahrgastwechselErledigt())
				result += ", planmäßige Abfahrt " + this.getAbfahrtZugverband();
		}
		if (this.getZugnummerNeu() != 0 && !this.hatFahrgastwechselErledigt()) {
			result += "\n\nFährt um " + this.getAbfahrtZugverband() + " als "
				+ this.getGattungNeu() + " " + this.getZugnummerNeu()
				+ " weiter nach " + this.zielbahnhof + " (" +
				this.getZielBetriebsstelle().getNameFahrplan() + ").";
		}
		if (!this.getVereinigteZuege().containsAll(this.getVereinigungMit())) {
			result += "\n\nWird mit ";
			boolean istErsterZug = true;
			for (Fahrplaneintrag zug : this.getVereinigungMit()) {
				if (this.getVereinigteZuege().contains(zug))
					continue;
				if (!istErsterZug)
					result += "/";
				result += zug.getGattung() + " " + zug.getZugnummer();
				istErsterZug = false;
			}
			result += " vereinigt.";
		}
		for (Fahrplaneintrag fluegelzug : this.getVereinigteZuege()) {
			if (!fluegelzug.fluegelt())
				continue;
			result += "\n\n" + fluegelzug.toString() + " fährt um "
					+ fluegelzug.getAbfahrtZugverband();
			if (fluegelzug.getZugnummerNeu() != 0)
				result +=  " als " + fluegelzug.getGattungNeu() + " "
						+ fluegelzug.getZugnummerNeu();
			if (!fluegelzug.getVereinigungMit().isEmpty()) {
				result += " vereinigt mit ";
				boolean istErsterZug = true;
				for (Fahrplaneintrag zug : fluegelzug.getVereinigungMit()) {
					if (!istErsterZug)
						result += "/";
					result += zug.getGattung() + " " + zug.getZugnummer();
					istErsterZug = false;
				}
			}
			result += " weiter nach " + fluegelzug.zielbahnhof + " (" +
			fluegelzug.getZielBetriebsstelle().getNameFahrplan() + ").";
		}
		if (!this.getVereinigungMit().containsAll(this.getZuegeSelbesGleis())
				&& !this.getVereinigteZuege().containsAll(this.getZuegeSelbesGleis())) {
			result += "\n\nKann sich das Gleis mit ";
			boolean istErsterZug = true;
			for (Fahrplaneintrag zug : this.getZuegeSelbesGleis()) {
				if (!istErsterZug)
					result += "/";
				result += zug.getGattung() + " " + zug.getZugnummer();
				istErsterZug = false;
			}
			result += " teilen.";
		}
		return result;
	}
	
	/**
	 * Gibt die endgültige Abfahrtszeit des Zugverbands an, d.h. nach erfolgter Vereinigung
	 * aller Zugteile. Die endgültige Abfahrtszeit ist das Maximum aller Abfahrtszeiten der
	 * einzelnen Zugteile. Die tatsächliche Abfahrtszeit des Zugteils kann früher erfolgen,
	 * um ggfs. noch ein rangieren an den Carrier zu ermöglichen.
	 */
	private Zeit getAbfahrtZugverband() {
		Zeit result = this.getAbfahrtszeit();
		for (Fahrplaneintrag zug : this.getVereinigungMit()) {
			result = Zeit.max(result, zug.getAbfahrtszeit());
		}
		return result;
	}

	/**
	 * Gibt die Herkunftsbahnhöfe des Zugverbands zurück. Hierbei werden
	 * Zugvereinigungen und Durchbindungen/Wechsel auf die Folgeleistung berücksichtigt.
	 */
	private String getHerkunftsbahnhoefe() {
		ArrayList<String> genannteBahnhoefe = new ArrayList<String>(3);
		genannteBahnhoefe.add(this.herkunftsbahnhof);
		String herkunftsbahnhoefe = this.herkunftsbahnhof;
		for (Fahrplaneintrag fluegelzug : this.getVereinigteZuege()) {
			if (genannteBahnhoefe.contains(fluegelzug.herkunftsbahnhof))
				continue;
			herkunftsbahnhoefe += "/" + fluegelzug.herkunftsbahnhof;
			genannteBahnhoefe.add(fluegelzug.herkunftsbahnhof);
		}
		if (this.getZugnummerNeu() == 0 || !this.hatFahrgastwechselErledigt())
			return herkunftsbahnhoefe;
		return Stellwerk.getInstance().getBahnhofsname();
	}
	
	/**
	 * Gibt die Zielbahnhöfe des Zugverbands zurück. Hierbei werden
	 * Zugvereinigungen und Durchbindungen/Wechsel auf die Folgeleistung berücksichtigt.
	 */
	private String getZielbahnhoefe() {
		ArrayList<String> genannteBahnhoefe = new ArrayList<String>(3);
		genannteBahnhoefe.add(this.zielbahnhof);
		String zielbahnhoefe = this.zielbahnhof;
		for (Fahrplaneintrag fluegelzug : this.getVereinigteZuege()) {
			if (fluegelzug.fluegelt() || genannteBahnhoefe.contains(fluegelzug.zielbahnhof))
				continue;
			zielbahnhoefe += "/" + fluegelzug.zielbahnhof;
			genannteBahnhoefe.add(fluegelzug.zielbahnhof);
		}
		if (this.getZugnummerNeu() == 0 || this.hatFahrgastwechselErledigt())
			return zielbahnhoefe;
		return Stellwerk.getInstance().getBahnhofsname();
	}
	
	/**
	 * Gibt die planmäßige Abfahrtszeit am Bahnsteig zurück.
	 */
	abstract Zeit getAbfahrtszeit();
	
	/**
	 * Gibt die Zugnummer des Zuges zurück.
	 */
	abstract int getZugnummer();
	
	/**
	 * Gibt die Zuggattung des Zuges zurück.
	 */
	abstract String getGattung();
	
	/**
	 * Gibt die künftige Zugnummer des Zuges zurück.
	 */
	abstract int getZugnummerNeu();
	
	/**
	 * Gibt die künftige Zuggattung des Zuges zurück.
	 */
	abstract String getGattungNeu();
	
	/**
	 * Gibt eine Liste aller Züge zurück, die derzeit mit diesem Zug vereinigt sind.
	 */
	abstract ArrayList<Zug> getVereinigteZuege();
	
	/**
	 * Gibt an, ob der Zugteil am simulierten Bahnhof abgekoppelt wird
	 */
	abstract boolean fluegelt();
	
	/**
	 * Gibt eine Liste aller Züge zurück, mit denen dieser Zug vereinigt werden kann.
	 */
	abstract ArrayList<Zug> getVereinigungMit();
	
	/**
	 * Gibt die tatsächliche Abfahrtszeit am Bahnsteig zurück (incl. Verspätung)
	 */
	abstract Zeit getTatsaechlicheAbfahrt();
	
	/**
	 * Gibt die tatsächliche Ankunftszeit an der start-Betriebsstelle der Simulation
	 * an, d.h. inklusive von Verspätung.
	 * 
	 * @return Die tatsächliche Ankunftszeit.
	 */
	abstract Zeit getTatsaechlicheAnkunft();
	
	/**
	 * Gibt die Verspätung des Zuges zurück.
	 */
	abstract int getVerspaetung();
	
	/**
	 * Gibt die aktuelle Position des Zuges zurück.
	 */
	abstract StreckeFuerGUI getPosition();
	
	/**
	 * Gibt die Betriebsstelle zurück, an der der Zug in die Simulation einfährt.
	 * 
	 * @return Die entsprechende Betriebsstelle
	 */
	abstract BetriebsstelleFuerGUI getStartBetriebsstelle();
	
	/**
	 * Gibt die Ziel-Betriebsstelle dieses Zuges zurück, d.h. die Betriebsstelle, über
	 * die der Zug die Simulation verlässt.
	 */
	abstract BetriebsstelleFuerGUI getZielBetriebsstelle();
	
	/**
	 * Gibt zurück, ob der Zug derzeit auf freie Fahrt wartet.
	 */
	abstract boolean wartet();
	
	/**
	 * Gibt zurück, ob dieser Zug bereits den Fahrgastwechsel erledigt hat.
	 * Im Fall eines Güterzuges wird grundsätzlich true zurückgegeben.
	 */
	abstract boolean hatFahrgastwechselErledigt();
	
	/**
	 * Gibt eine Liste aller Züge zurück, mit denen ein Gleis geteilt werden darf.
	 */
	abstract ArrayList<Zug> getZuegeSelbesGleis();
	
	/**
	 * Gibt zurück, ob der Zug innerhalb der nächsten 3 Minuten abfährt.
	 */
	boolean faehrtBaldAb() {
		if (this.faehrt() || this.wartet())
			return false;
		if (!this.istInSimulation()) {
			if (Zeit.getZeitdifferenzInMinuten(this.getTatsaechlicheAnkunft(),
					Stellwerk.getInstance().getAktuelleZeit()) > 3) {
				return false;
			} else {
				return true;
			}
		}
		if (this.hatZielErreicht())
			return false;
		if (Zeit.getZeitdifferenzInMinuten(this.getTatsaechlicheAbfahrt(),
				Stellwerk.getInstance().getAktuelleZeit()) > 3) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Gibt an, ob Zug gerade fährt oder nicht. Fahren bedeutet, dass der Zug sich
	 * in der Simulation befindet und einen eingestellten Fahrweg besitzt.
	 * 
	 * @return true, wenn der Zug fährt.
	 */
	abstract boolean faehrt();
	
	/**
	 * Gibt an, ob dieser Zugteil noch rangiert werden muss.
	 */
	abstract boolean rangierenNotwendig();
	
	/**
	 * Gibt an, welchen Status der Zug derzeit hat. Die verschiedenen Stati
	 * (Stehen, Warten, Bald abfahren oder Fahren) werden als final static
	 * int in Fahrplaneintrag codiert.
	 */
	public int getStatus() {
		if (this.faehrt())
			return Fahrplaneintrag.ZUG_FAEHRT;
		if (this.wartet())
			return Fahrplaneintrag.ZUG_WARTET;
		if (this.faehrtBaldAb())
			return Fahrplaneintrag.ZUG_FAEHRT_BALD_AB;
		return Fahrplaneintrag.ZUG_STEHT;
	}
	
	/**
	 * Gibt zurück, ob sich der Zug bereits in der Simulation befindet oder nicht.
	 */
	abstract boolean istInSimulation();
	
	/**
	 * Gibt an, ob der Zug erfolgreich die Simulation durchfahren hat.
	 */
	abstract boolean hatZielErreicht();

	/**
	 * Gibt eine Liste aller Züge zurück, die im Fahrplanpanel dargestellt werden sollen.
	 * Cast ist safe, da Zug extends Fahrplaneintrag.
	 */
	@SuppressWarnings("unchecked")
	public static Vector<Fahrplaneintrag> getFahrplanInSimulation() {
		return (Vector<Fahrplaneintrag>) (Object) Zug.getZuegeInSimulation();
	}
	
	/**
	 * Gibt die Liste der wartenden Züge der Simulation zurück.
	 * Cast ist safe, da Zug extends Fahrplaneintrag.
	 */
	@SuppressWarnings("unchecked")
	public static Vector<Fahrplaneintrag> getWartendeZuege() {
		return (Vector<Fahrplaneintrag>) (Object) Fahrplaneintrag.wartendeZuege;
	}
	
	/**
	 * Gibt die Liste Züge für die Gleisbelegungsanzeige zurück.
	 * Cast ist safe, da Zug extends Fahrplaneintrag.
	 */
	@SuppressWarnings("unchecked")
	public static Vector<Fahrplaneintrag> getGleisbelegung() {
		return (Vector<Fahrplaneintrag>) (Object) Fahrplaneintrag.zuegeAmBahnsteig;
	}
	
	/**
	 * Befüllt die Liste der wartenden Züge neu.
	 */
	public static void updateWartendeZuege() {
		Fahrplaneintrag.wartendeZuege.clear();
		for (Zug zug : Zug.getZuegeInSimulation()) {
			if (zug.wartet())
				Fahrplaneintrag.wartendeZuege.add(zug);	
		}
	}
	
	/**
	 * Befüllt die Liste der Gleisbelegung neu.
	 */
	public static void updateGleisbelegung() {
		Fahrplaneintrag.zuegeAmBahnsteig.clear();
		for (Betriebsstelle betriebsstelle :
				Betriebsstelle.getAlleBetriebsstellen()) {
			if (!betriebsstelle.hatBahnsteig())
				continue;
			for (Zug zug : betriebsstelle.getZuegeImGleis())
				Fahrplaneintrag.zuegeAmBahnsteig.add(zug);
		}
	}
}