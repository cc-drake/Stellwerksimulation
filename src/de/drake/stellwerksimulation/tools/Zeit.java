package de.drake.stellwerksimulation.tools;

import java.util.Scanner;

public class Zeit implements Comparable<Zeit> {
	private int stunde;
	
	private int minute;
	
	private int sekunde;
	
	public Zeit(final int stunde, final int minute, final int sekunde) {
		this.stunde = stunde;
		this.minute = minute;
		this.sekunde = sekunde;
	}
	
	public Zeit(final String zeit) {
		Scanner scanner = new Scanner(zeit);
		scanner.useDelimiter(":");
		if (!scanner.hasNext()) {
			scanner.close();
			throw new IllegalArgumentException();
		}
		this.stunde = scanner.nextInt();
		this.minute = scanner.nextInt();
		if (scanner.hasNext()) {
			this.sekunde = scanner.nextInt();
		} else {
			this.sekunde = 0;
		}
		scanner.close();
	}
	
	public Zeit(final Zeit zeit) {
		this.stunde = zeit.stunde;
		this.minute = zeit.minute;
		this.sekunde = zeit.sekunde;
	}
	
	public boolean equals(final Object object) {
		if (!(object instanceof Zeit))
			return false;
		Zeit zeit = (Zeit) object;
		if (this.stunde == zeit.stunde && this.minute == zeit.minute
				&& this.sekunde == zeit.sekunde)
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		String result = "";
		if (this.stunde < 10)
			result += 0;
		result += this.stunde + ":";
		if (this.minute < 10)
			result += 0;
		result += this.minute;
		return result;
	}
	
	@Override
	public Object clone() {
		return new Zeit(this.stunde, this.minute, this.sekunde);
	}

	public int compareTo(final Zeit zeit) {
		return this.stunde*3600 + this.minute*60 + this.sekunde
				-(zeit.stunde*3600 + zeit.minute*60 + zeit.sekunde);
	}

	public Zeit addMinuten(final int minuten) {
		this.stunde += (this.minute + minuten)/60;
		this.minute = (this.minute + minuten) % 60;
		return this;
	}
	
	public Zeit addSekunden(final int sekunden) {
		this.minute += (this.sekunde + sekunden)/60;
		this.sekunde = (this.sekunde + sekunden) % 60;
		this.stunde += this.minute / 60;
		this.minute = this.minute % 60;
		this.stunde = this.stunde % 24;
		return this;
	}
	
	public static int getZeitdifferenzInMinuten(final Zeit zeit, final Zeit zeit2) {
		return (int) Math.ceil(Math.abs(zeit2.stunde*60. + zeit2.minute + zeit2.sekunde/60.
				- (zeit.stunde*60. + zeit.minute + zeit.sekunde/60.)));
	}
	
	public boolean istFrueherOderZeitgleichAls(final Zeit zeit) {
		if (this.stunde*3600 + this.minute*60 + this.sekunde
				<= zeit.stunde*3600 + zeit.minute*60 + zeit.sekunde)
			return true;
		return false;
	}
	
	public static Zeit max(final Zeit zeit1, final Zeit zeit2) {
		if (zeit1.istFrueherOderZeitgleichAls(zeit2))
			return zeit2;
		return zeit1;
	}
	
	public static Zeit min(final Zeit zeit1, final Zeit zeit2) {
		if (zeit1.istFrueherOderZeitgleichAls(zeit2))
			return zeit1;
		return zeit2;
	}
}
