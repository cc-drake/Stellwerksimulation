package de.drake.stellwerksimulation.tools;

import java.util.ArrayList;

/**
 * Repr�sentiert einen "zweiseitigen Stack", bei dem Elemente von zwei Seiten
 * hinzugef�gt oder wieder entfernt werden k�nnen.
 * @param <ContentClass> Klasse der Elemente, die im Stack platziert werden sollen.
 * @param <SeitenClass> Klasse der Elemente, die die beiden Seiten repr�sentieren.
 */
public class TwosidedStack<ContentClass, SeitenClass>
		extends ArrayList<ContentClass>{

	/**
	 * Die serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Identifier f�r die "obere" Seite des Stacks. Diese repr�sentiert technisch
	 * den Kopf der ArrayList.
	 */
	private SeitenClass obereSeite;
	
	/**
	 * Erzeugt einen leeren zweiseitigen Stack.
	 * 
	 * @param obereSeite
	 * 		Element, was die erste Seite des Stacks repr�sentiert ("oben").
	 * 		Wird null �bergeben, so repr�sentieren alle Elemente der SeitenClass
	 * 		die "untere" Seite des Stacks.
	 */
	public TwosidedStack(final SeitenClass obereSeite) {
		this.obereSeite = obereSeite;
	}
	
	/**
	 * F�gt dem Stack ein Element hinzu.
	 * 
	 * @param element
	 * 		Das Element, das dem Stack hinzugef�gt werden soll.
	 * @param seite
	 * 		Gibt an, auf welcher Seite des Stacks das Element hinzugef�gt werden
	 * 		soll. Stimmt die Seite nicht mit dem Identifier der oberen Seite �berein,
	 * 		so wird auf der unteren Seite hinzugef�gt.
	 */
	public void add(final ContentClass element, final SeitenClass seite) {
		if (seite.equals(this.obereSeite)) {
			super.add(0, element);
		} else {
			super.add(element);
		}
	}
	
	/**
	 * Gibt das letzte Element des Stacks zur�ck, ohne es vom Stack zu entfernen.
	 * 
	 * @param seite
	 * 		Gibt an, von welcher Seite das letzte Element bestimmt werden soll.
	 * 		Stimmt die Seite nicht mit dem Identifier der oberen Seite �berein,
	 * 		so wird das letzte Element der unteren Seite zur�ckgegeben.
	 */
	public ContentClass peek(final SeitenClass seite) {
		ContentClass result;
		if (super.isEmpty())
			return null;
		if (seite.equals(this.obereSeite)) {
			result = super.get(0);
		} else {
			result = super.get(super.size()-1);
		}
		return result;
	}
}