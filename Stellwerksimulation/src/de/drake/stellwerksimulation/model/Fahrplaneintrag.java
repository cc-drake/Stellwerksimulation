package de.drake.stellwerksimulation.model;

import java.util.ArrayList;
import java.util.Vector;

import de.drake.stellwerksimulation.tools.Zeit;

public abstract class Fahrplaneintrag {
	
	/**
	 * Gibt an, dass der Zug derzeit ohne Zeitdruck in einem Bahnhof rumsteht oder
	 * au�erhalb der Simulation ist.
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
	 * Gibt an, dass der Zug innerhalb der n�chsten 3 Minuten abfahren m�chte.
	 */
	final static public int ZUG_FAEHRT_BALD_AB = 3;
	
	/**
	 * Eine Liste aller wartenden Z�ge, die im Fahrplanpanel dargestellt werden sollen.
	 */
	private static Vector<Zug> wartendeZuege = new Vector<Zug>();
	
	/**
	 * Eine Liste aller Z�ge, die in der Gleisbelegung auftauchen sollen.
	 */
	private static Vector<Zug> zuegeAmBahnsteig = new Vector<Zug>();
	
	/**
	 * Herkunftsbahnhof (z.B. Hamburg-Altona) zur Anzeige im Fahrplanfenster
	 */
	private String herkunftsbahnhof;
	
	/**
	 * Zielbahnhof (z.B. M�nchen Hbf) zur Anzeige im Fahrplanfenster
	 */
	private String zielbahnhof;
	
	/**
	 * Die Betriebsstelle (also das Gleis), �ber das der Zug fahren sollte
	 */
	private Betriebsstelle viaGleis;
	
	/**
	 * Legt einen neuen Fahrplaneintrag an.
	 * 
	 * @param herkunftsbahnhof
	 * 		Herkunftsbahnhof (z.B. Hamburg-Altona) zur Anzeige im Fahrplanfenster
	 * @param zielbahnhof
	 * 		Zielbahnhof (z.B. M�nchen Hbf) zur Anzeige im Fahrplanfenster
	 * @param viaGleis
	 * 		Die Betriebsstelle (also das Gleis), �ber das der Zug fahren sollte
	 */
	Fahrplaneintrag(final String herkunftsbahnhof, final String zielbahnhof,
			final Betriebsstelle viaGleis) {
		this.herkunftsbahnhof = herkunftsbahnhof;
		this.zielbahnhof = zielbahnhof;
		this.viaGleis = viaGleis;
	}
	
	/**
	 * Gibt die Bezeichnung des Zugverbands (z.B. ICE 105/505) als String zur�ck.
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
	 * Gibt eine kurze Beschreibung des Zuges aus, die f�r die Auswahlmen�s des
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
	 * Gibt eine kurze Beschreibung des Zuges aus, die f�r die Anzeige im
	 * Gleisbelegungsmen� der GUI verwendet werden kann.
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
	 * Stellt eine Beschreibung des Zuges f�r die Detailanzeige in der GUI zusammen.
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
			result += "\n\n�ber " + this.viaGleis.getNameFahrplan();
			if (this.getZugnummerNeu() == 0 || this.hatFahrgastwechselErledigt())
				result += ", planm��ige Abfahrt " + this.getAbfahrtZugverband();
		}
		if (this.getZugnummerNeu() != 0 && !this.hatFahrgastwechselErledigt()) {
			result += "\n\nF�hrt um " + this.getAbfahrtZugverband() + " als "
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
			result += "\n\n" + fluegelzug.toString() + " f�hrt um "
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
	 * Gibt die endg�ltige Abfahrtszeit des Zugverbands an, d.h. nach erfolgter Vereinigung
	 * aller Zugteile. Die endg�ltige Abfahrtszeit ist das Maximum aller Abfahrtszeiten der
	 * einzelnen Zugteile. Die tats�chliche Abfahrtszeit des Zugteils kann fr�her erfolgen,
	 * um ggfs. noch ein rangieren an den Carrier zu erm�glichen.
	 */
	private Zeit getAbfahrtZugverband() {
		Zeit result = this.getAbfahrtszeit();
		for (Fahrplaneintrag zug : this.getVereinigungMit()) {
			result = Zeit.max(result, zug.getAbfahrtszeit());
		}
		return result;
	}

	/**
	 * Gibt die Herkunftsbahnh�fe des Zugverbands zur�ck. Hierbei werden
	 * Zugvereinigungen und Durchbindungen/Wechsel auf die Folgeleistung ber�cksichtigt.
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
	 * Gibt die Zielbahnh�fe des Zugverbands zur�ck. Hierbei werden
	 * Zugvereinigungen und Durchbindungen/Wechsel auf die Folgeleistung ber�cksichtigt.
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
	 * Gibt die planm��ige Abfahrtszeit am Bahnsteig zur�ck.
	 */
	abstract Zeit getAbfahrtszeit();
	
	/**
	 * Gibt die Zugnummer des Zuges zur�ck.
	 */
	abstract int getZugnummer();
	
	/**
	 * Gibt die Zuggattung des Zuges zur�ck.
	 */
	abstract String getGattung();
	
	/**
	 * Gibt die k�nftige Zugnummer des Zuges zur�ck.
	 */
	abstract int getZugnummerNeu();
	
	/**
	 * Gibt die k�nftige Zuggattung des Zuges zur�ck.
	 */
	abstract String getGattungNeu();
	
	/**
	 * Gibt eine Liste aller Z�ge zur�ck, die derzeit mit diesem Zug vereinigt sind.
	 */
	abstract ArrayList<Zug> getVereinigteZuege();
	
	/**
	 * Gibt an, ob der Zugteil am simulierten Bahnhof abgekoppelt wird
	 */
	abstract boolean fluegelt();
	
	/**
	 * Gibt eine Liste aller Z�ge zur�ck, mit denen dieser Zug vereinigt werden kann.
	 */
	abstract ArrayList<Zug> getVereinigungMit();
	
	/**
	 * Gibt die tats�chliche Abfahrtszeit am Bahnsteig zur�ck (incl. Versp�tung)
	 */
	abstract Zeit getTatsaechlicheAbfahrt();
	
	/**
	 * Gibt die tats�chliche Ankunftszeit an der start-Betriebsstelle der Simulation
	 * an, d.h. inklusive von Versp�tung.
	 * 
	 * @return Die tats�chliche Ankunftszeit.
	 */
	abstract Zeit getTatsaechlicheAnkunft();
	
	/**
	 * Gibt die Versp�tung des Zuges zur�ck.
	 */
	abstract int getVerspaetung();
	
	/**
	 * Gibt die aktuelle Position des Zuges zur�ck.
	 */
	abstract StreckeFuerGUI getPosition();
	
	/**
	 * Gibt die Betriebsstelle zur�ck, an der der Zug in die Simulation einf�hrt.
	 * 
	 * @return Die entsprechende Betriebsstelle
	 */
	abstract BetriebsstelleFuerGUI getStartBetriebsstelle();
	
	/**
	 * Gibt die Ziel-Betriebsstelle dieses Zuges zur�ck, d.h. die Betriebsstelle, �ber
	 * die der Zug die Simulation verl�sst.
	 */
	abstract BetriebsstelleFuerGUI getZielBetriebsstelle();
	
	/**
	 * Gibt zur�ck, ob der Zug derzeit auf freie Fahrt wartet.
	 */
	abstract boolean wartet();
	
	/**
	 * Gibt zur�ck, ob dieser Zug bereits den Fahrgastwechsel erledigt hat.
	 * Im Fall eines G�terzuges wird grunds�tzlich true zur�ckgegeben.
	 */
	abstract boolean hatFahrgastwechselErledigt();
	
	/**
	 * Gibt eine Liste aller Z�ge zur�ck, mit denen ein Gleis geteilt werden darf.
	 */
	abstract ArrayList<Zug> getZuegeSelbesGleis();
	
	/**
	 * Gibt zur�ck, ob der Zug innerhalb der n�chsten 3 Minuten abf�hrt.
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
	 * Gibt an, ob Zug gerade f�hrt oder nicht. Fahren bedeutet, dass der Zug sich
	 * in der Simulation befindet und einen eingestellten Fahrweg besitzt.
	 * 
	 * @return true, wenn der Zug f�hrt.
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
	 * Gibt zur�ck, ob sich der Zug bereits in der Simulation befindet oder nicht.
	 */
	abstract boolean istInSimulation();
	
	/**
	 * Gibt an, ob der Zug erfolgreich die Simulation durchfahren hat.
	 */
	abstract boolean hatZielErreicht();

	/**
	 * Gibt eine Liste aller Z�ge zur�ck, die im Fahrplanpanel dargestellt werden sollen.
	 * Cast ist safe, da Zug extends Fahrplaneintrag.
	 */
	@SuppressWarnings("unchecked")
	public static Vector<Fahrplaneintrag> getFahrplanInSimulation() {
		return (Vector<Fahrplaneintrag>) (Object) Zug.getZuegeInSimulation();
	}
	
	/**
	 * Gibt die Liste der wartenden Z�ge der Simulation zur�ck.
	 * Cast ist safe, da Zug extends Fahrplaneintrag.
	 */
	@SuppressWarnings("unchecked")
	public static Vector<Fahrplaneintrag> getWartendeZuege() {
		return (Vector<Fahrplaneintrag>) (Object) Fahrplaneintrag.wartendeZuege;
	}
	
	/**
	 * Gibt die Liste Z�ge f�r die Gleisbelegungsanzeige zur�ck.
	 * Cast ist safe, da Zug extends Fahrplaneintrag.
	 */
	@SuppressWarnings("unchecked")
	public static Vector<Fahrplaneintrag> getGleisbelegung() {
		return (Vector<Fahrplaneintrag>) (Object) Fahrplaneintrag.zuegeAmBahnsteig;
	}
	
	/**
	 * Bef�llt die Liste der wartenden Z�ge neu.
	 */
	public static void updateWartendeZuege() {
		Fahrplaneintrag.wartendeZuege.clear();
		for (Zug zug : Zug.getZuegeInSimulation()) {
			if (zug.wartet())
				Fahrplaneintrag.wartendeZuege.add(zug);	
		}
	}
	
	/**
	 * Bef�llt die Liste der Gleisbelegung neu.
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