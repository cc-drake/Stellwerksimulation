package de.drake.stellwerksimulation.tools;

/**
 * Ein Pair realisiert ein Tupel aus zwei Elementen.
 */
public class Pair<Class1, Class2> {
	
	/**
	 * Das erste Element des Tupels.
	 */
	private Class1 element1;
	
	/**
	 * Das zweite Element des Tupels.
	 */
	private Class2 element2;
	
	/**
	 * Erzeugt ein neues Pair.
	 * 
	 * @param element1
	 * 		Das erste Element des Pairs.
	 * @param element2
	 * 		Das zweite Element des Pairs.
	 */
	public Pair(final Class1 element1, final Class2 element2) {
		this.element1 = element1;
		this.element2 = element2;
	}
	
	/**
	 * Gibt den Inhalt des Pairs als String zurück.
	 */
	public String toString() {
		return "(" + this.element1.toString() + ", " + this.element2.toString() + ")";
	}
	
	/**
	 * Vergleicht zwei Paare auf Identität.
	 * 
	 * @param object
	 * 		Das Objekt, mit dem auf Identität verglichen werden soll.
	 */
	public boolean equals(final Object object) {
		if (!(object instanceof Pair))
			return false;
		@SuppressWarnings("unchecked")
		Pair<Class1, Class2> objectPair = (Pair<Class1, Class2>) object;
		if (objectPair.element1.equals(this.element1)
				&& objectPair.element2.equals(this.element2))
			return true;
		return false;
	}
	
	/**
	 * Erzeugt einen HashCode für das Pair.
	 */
	public int hashCode() {
		return 31*this.element1.hashCode() + this.element2.hashCode();
	}
	
	/**
	 * Gibt das erste Element des Pairs zurück.
	 */
	public Class1 getFirst() {
		return this.element1;
	}
	
	/**
	 * Gibt das zweite Element des Pairs zurück.
	 */
	public Class2 getSecond() {
		return this.element2;
	}
}