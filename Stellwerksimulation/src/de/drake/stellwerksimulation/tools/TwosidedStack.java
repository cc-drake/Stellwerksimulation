package de.drake.stellwerksimulation.tools;

import java.util.ArrayList;

/**
 * Repräsentiert einen "zweiseitigen Stack", bei dem Elemente von zwei Seiten
 * hinzugefügt oder wieder entfernt werden können.
 * @param <ContentClass> Klasse der Elemente, die im Stack platziert werden sollen.
 * @param <SeitenClass> Klasse der Elemente, die die beiden Seiten repräsentieren.
 */
public class TwosidedStack<ContentClass, SeitenClass>
		extends ArrayList<ContentClass>{

	/**
	 * Die serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Identifier für die "obere" Seite des Stacks. Diese repräsentiert technisch
	 * den Kopf der ArrayList.
	 */
	private SeitenClass obereSeite;
	
	/**
	 * Erzeugt einen leeren zweiseitigen Stack.
	 * 
	 * @param obereSeite
	 * 		Element, was die erste Seite des Stacks repräsentiert ("oben").
	 * 		Wird null übergeben, so repräsentieren alle Elemente der SeitenClass
	 * 		die "untere" Seite des Stacks.
	 */
	public TwosidedStack(final SeitenClass obereSeite) {
		this.obereSeite = obereSeite;
	}
	
	/**
	 * Fügt dem Stack ein Element hinzu.
	 * 
	 * @param element
	 * 		Das Element, das dem Stack hinzugefügt werden soll.
	 * @param seite
	 * 		Gibt an, auf welcher Seite des Stacks das Element hinzugefügt werden
	 * 		soll. Stimmt die Seite nicht mit dem Identifier der oberen Seite überein,
	 * 		so wird auf der unteren Seite hinzugefügt.
	 */
	public void add(final ContentClass element, final SeitenClass seite) {
		if (seite.equals(this.obereSeite)) {
			super.add(0, element);
		} else {
			super.add(element);
		}
	}
	
	/**
	 * Gibt das letzte Element des Stacks zurück, ohne es vom Stack zu entfernen.
	 * 
	 * @param seite
	 * 		Gibt an, von welcher Seite das letzte Element bestimmt werden soll.
	 * 		Stimmt die Seite nicht mit dem Identifier der oberen Seite überein,
	 * 		so wird das letzte Element der unteren Seite zurückgegeben.
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