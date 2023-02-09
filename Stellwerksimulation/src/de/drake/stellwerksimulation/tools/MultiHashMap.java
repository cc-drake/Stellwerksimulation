package de.drake.stellwerksimulation.tools;

import java.util.HashMap;

/**
 * Eine mehrdimensionale HashMap.
 */
public class MultiHashMap<KeyClass1, KeyClass2, ValueClass> 
		extends HashMap<Pair<KeyClass1, KeyClass2>, ValueClass>{
	
	/**
	 * Die serialVersionUID der HultHashMap.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Pr�ft, ob zu einem gegebenen Schl�ssel ein Wert vorhanden ist.
	 * 
	 * @param key1
	 * 		Der erste Teil des Schl�ssels
	 * @param key2
	 * 		Der zweite Teil des Schl�ssels
	 */
	public boolean containsKey(final KeyClass1 key1, final KeyClass2 key2) {
		return super.containsKey(new Pair<KeyClass1, KeyClass2>(key1, key2));
	}
	
	/**
	 * Gibt zu einem Schl�ssel den zugeordneten Wert zur�ck.
	 * 
	 * @param key1
	 * 		Der erste Teil des Schl�ssels
	 * @param key2
	 * 		Der zweite Teil des Schl�ssels
	 */
	public ValueClass get(final KeyClass1 key1, final KeyClass2 key2) {
		return super.get(new Pair<KeyClass1, KeyClass2>(key1, key2));
	}
	
	/**
	 * Erzeugt eine neue Abbildung.
	 * 
	 * @param key1
	 * 		Der erste Teil des Schl�ssels
	 * @param key2
	 * 		Der zweite Teil des Schl�ssels
	 * @param value
	 * 		Der zugeordnete Wert
	 */
	public ValueClass put(final KeyClass1 key1, final KeyClass2 key2,
			final ValueClass value) {
		return super.put(new Pair<KeyClass1, KeyClass2>(key1, key2), value);
	}
	
	/**
	 * Entfernt eine Zuordnung aus der MultiHashMap.
	 * 
	 * @param key1
	 * 		Der erste Teil des Schl�ssels
	 * @param key2
	 * 		Der zweite Teil des Schl�ssels
	 */
	public ValueClass removeKey(final KeyClass1 key1, final KeyClass2 key2) {
		return super.remove(new Pair<KeyClass1, KeyClass2>(key1, key2));
	}
}