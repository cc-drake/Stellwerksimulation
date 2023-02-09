package de.drake.stellwerksimulation.view;

/**
 * Grafik für den Teil einer Strecke, der von der Streckenmitte in eine bestimmte
 * Richtung läuft. Strecken bestehen damit aus bis zu 8 Teilstreckengrafiken.
 */
class Teilstreckengrafik {
	
	/**
	 * Gibt an, dass die Teilstreckengrafik eine freie Strecke repräsentiert.
	 */
	final static int FREI = 0;
	
	/**
	 * Gibt an, dass die Teilstreckengrafik Teil einer eingestellten Trasse ist.
	 */
	final static int FAHRWEG = 1;
	
	/**
	 * Gibt an, dass sich auf der Teilstreckengrafik ein Zug befindet.
	 */
	final static int ZUG = 2;
	
	/**
	 * Gibt an, ob die Strecke brückenfrei ist (null), aus einem oberen Brückenteil
	 * besteht (true) oder aus einem unteren Brückenteil besteht (false).
	 */
	private Boolean istBrueckeOben;
	
	/**
	 * Unkorrigerte X-Koordinate des Ausgangspunktes der Teilstreckengrafik.
	 * Muss noch an die Größe des Netzpanels angepasst werden.
	 */
	private float vonX;
	
	/**
	 * Unkorrigerte X-Koordinate des Endpunktes der Teilstreckengrafik.
	 * Muss noch an die Größe des Netzpanels angepasst werden.
	 */
	private float nachX;
	
	/**
	 * Unkorrigerte Y-Koordinate des Ausgangspunktes der Teilstreckengrafik.
	 * Muss noch an die Größe des Netzpanels angepasst werden.
	 */
	private float vonY;
	
	/**
	 * Unkorrigerte Y-Koordinate des Endpunktes der Teilstreckengrafik.
	 * Muss noch an die Größe des Netzpanels angepasst werden.
	 */
	private float nachY;
	
	/**
	 * Beinhaltet die Art der Teilstrecke, also ob die Teilstrecke als frei,
	 * eingestellt oder befahren gezeichnet werden soll.
	 */
	private int art = Teilstreckengrafik.FREI;
	
	/**
	 * Erzeugt eine neue Teilstreckengrafik.
	 * 
	 *  @param vonX
	 *  	X-Koordinate des Ausgangspunktes der Teilstreckengrafik
	 *  @param nachX
	 *  	X-Koordinate des Endpunktes der Teilstreckengrafik
	 *  @param vonY
	 *  	Y-Koordinate des Ausgangspunktes der Teilstreckengrafik
	 *  @param nachY
	 *  	Y-Koordinate des Endpunktes der Teilstreckengrafik
	 *  @param status
	 *  	Gibt den Status der Teilstreckengrafik an.
	 *  @param istBrueckeOben
	 * 		Gibt an, ob die Strecke brückenfrei ist (null), aus einem oberen Brückenteil
	 * 		besteht (true) oder aus einem unteren Brückenteil besteht (false).
	 */
	Teilstreckengrafik (final float vonX,
			final float nachX, final float vonY, final float nachY, final int art,
			final Boolean istBrueckeOben) {
		this.vonX = vonX;
		this.nachX = nachX;
		this.vonY = vonY;
		this.nachY = nachY;
		this.art = art;
		this.istBrueckeOben = istBrueckeOben;
	}
	
	/**
	 * Gibt die unskalierte X-Koordinate des Ausgangspunktes der Teilstreckengrafik
	 * zurück.
	 * 
	 * @return Die entsprechende Lage.
	 */
	float getVonX() {
		return this.vonX;
	}
	
	/**
	 * Gibt die unskalierte X-Koordinate des Endpunktes der Teilstreckengrafik
	 * zurück.
	 * 
	 * @return Die entsprechende Lage.
	 */
	float getNachX() {
		return this.nachX;
	}
	
	/**
	 * Gibt die unskalierte Y-Koordinate des Ausgangspunktes der Teilstreckengrafik
	 * zurück.
	 * 
	 * @return Die entsprechende Lage.
	 */
	float getVonY() {
		return this.vonY;
	}
	
	/**
	 * Gibt die unskalierte Y-Koordinate des Endpunktes der Teilstreckengrafik
	 * zurück.
	 * 
	 * @return Die entsprechende Lage.
	 */
	float getNachY() {
		return this.nachY;
	}
	
	/**
	 * Gibt an, ob die Teilstreckengrafik als Frei, Eingestellt oder Befahren gezeichnet
	 * werden soll.
	 * 
	 * @return Der entsprechende Status, codiert als Teilstreckengrafik.(Status).
	 */
	int getArt() {
		return this.art;
	}
	
	/**
	 * Gibt an, ob die Strecke brückenfrei ist (null), aus einem oberen Brückenteil
	 * besteht (true) oder aus einem unteren Brückenteil besteht (false).
	 */
	Boolean istBrueckeOben() {
		return this.istBrueckeOben;
	}
}