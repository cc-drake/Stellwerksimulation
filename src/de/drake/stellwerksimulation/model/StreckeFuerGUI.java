package de.drake.stellwerksimulation.model;

import java.util.ArrayList;

import de.drake.stellwerksimulation.tools.Richtung;

/**
 * Erweiterung einer Betriebsstelle, die als Interface für die GUI dient. Im Gegensatz
 * zu einer normalen Strecke beinhaltet Sie auch graphische Informationen, wie z.B.
 * Koordinaten.
 */
public abstract class StreckeFuerGUI {

	/**
	 * Die Position der Strecke im Matrixsystem (X-Koordinate).
	 */
	private int positionX;
	
	/**
	 * Die Position der Strecke im Matrixsystem (Y-Koordinate).
	 */
	private int positionY;
	
	/**
	 * Die Position der Zugnummernanzeige in der GUI relativ zur Streckenmitte
	 * (X-Koordinate).
	 */
	private int positionZugnummerX;
	
	/**
	 * Die Position der Zugnummernanzeige in der GUI relativ zur Streckenmitte
	 * (X-Koordinate).
	 */
	private int positionZugnummerY;
	
	/**
	 * Gibt an, ob die Strecke brückenfrei ist (null), aus einem oberen Brückenteil
	 * besteht (true) oder aus einem unteren Brückenteil besteht (false).
	 */
	private Boolean istBrueckeOben;
	
	/**
	 * Erzeugt eine neue Strecke.
	 * 
	 * @param positionX
	 * 		Die Position der Strecke im Koordinatensystem (X-Koordinate)
	 * @param positionY
	 * 		Die Position der Strecke im Koordinatensystem (Y-Koordinate)
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
	StreckeFuerGUI(final int positionX, final int positionY,
			final Boolean istBrueckeOben,
			final int positionZugnummerX, final int positionZugnummerY) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.istBrueckeOben = istBrueckeOben;
		this.positionZugnummerX = positionZugnummerX;
		this.positionZugnummerY = positionZugnummerY;
	}
	
	/**
	 * Gibt die Position des Streckenmittelpunkts im Koordinatensystem zurück
	 * (X-Koordinate).
	 */
	public float getPositionX() {
		return this.positionX + .5f;
	}
	
	/**
	 * Gibt die Position des Streckenmittelpunkts im Koordinatensystem zurück
	 * (Y-Koordinate).
	 */
	public float getPositionY() {
		return this.positionY + .5f;
	}
	
	/**
	 * Gibt eine Liste aller Strecken der Simulation zurück.
	 * Cast ist safe, da Strecke extends StreckeFuerGUI.
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<StreckeFuerGUI> getAlleStreckenFuerGUI() {
		return (ArrayList<StreckeFuerGUI>) (Object) Strecke.getAlleStrecken();
	}
	
	/**
	 * Gibt die horizontale Größe des Streckennetzes zurück.
	 */
	public static int getGroesseStreckennetzHorizontal() {
		int groesse = 0;
		for (StreckeFuerGUI streckeFuerGUI
				: StreckeFuerGUI.getAlleStreckenFuerGUI()) {
			if (streckeFuerGUI.positionX > groesse) {
				groesse = streckeFuerGUI.positionX;
			}
		}
		return groesse + 1;
	}
	
	/**
	 * Gibt die vertikale Größe des Streckennetzes zurück.
	 */
	public static int getGroesseStreckennetzVertikal() {
		int groesse = 0;
		for (StreckeFuerGUI streckeFuerGUI
				: StreckeFuerGUI.getAlleStreckenFuerGUI()) {
			if (streckeFuerGUI.positionY > groesse) {
				groesse = streckeFuerGUI.positionY;
			}
		}
		return groesse + 1;
	}
	
	/**
	 * Gibt den Namen der Strecke zurück, wie er im Fahrplan auftauchen soll.
	 * Wird von Betriebsstellen überschrieben.
	 */
	String getNameFahrplan() {
		return "";
	}
	
	/**
	 * Gibt an, ob die Strecke brückenfrei ist (null), aus einem oberen Brückenteil
	 * besteht (true) oder aus einem unteren Brückenteil besteht (false).
	 */
	public Boolean istBrueckeOben() {
		return this.istBrueckeOben;
	}
	
	/**
	 * Gibt eine Liste aller Anschlussrichtungen dieser Strecke zurück.
	 */
	public abstract ArrayList<Richtung> getAnschlussrichtungen();

	/**
	 * Gibt die Menge der derzeit eingestellten Richtungen zurück.
	 */
	public abstract ArrayList<Richtung> getEingestellteRichtungen();
	
	/**
	 * Gibt zurück, ob die Strecke derzeit frei ist, d.h. keine Züge oder Fahrwege
	 * eingestellt sind.
	 * 
	 * @return
	 * 		true, wenn Strecke frei.
	 */
	public abstract boolean istFrei();
	
	/**
	 * Gibt zurück, ob sich auf der Strecke derzeit Züge befinden.
	 */
	public abstract boolean istBefahren();

	/**
	 * Gibt die Position der Zugnummernanzeige in der GUI relativ zur Streckenmitte zurück
	 * (X-Koordinate).
	 */
	public int getpositionZugnummerX() {
		return this.positionZugnummerX;
	}
	
	/**
	 * Gibt die Position der Zugnummernanzeige in der GUI relativ zur Streckenmitte zurück
	 * (Y-Koordinate).
	 */
	public int getpositionZugnummerY() {
		return this.positionZugnummerY;
	}
	
	/**
	 * Gibt eine Liste aller Züge zurück, die sich derzeit auf dieser Strecke
	 * befinden.
	 */
	abstract ArrayList<Zug> getZuegeImGleis();
	
	/**
	 * Gibt eine Liste aller Züge zurück, die sich derzeit auf dieser Strecke
	 * befinden.
	 * Cast ist safe, da Zug extends Fahrplaneintrag und TwosidedStack extends ArrayList.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Fahrplaneintrag> getZuegeAufStrecke() {
		return (ArrayList<Fahrplaneintrag>) (Object) this.getZuegeImGleis();
	}
}