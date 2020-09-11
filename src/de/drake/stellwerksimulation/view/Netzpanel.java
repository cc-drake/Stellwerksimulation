package de.drake.stellwerksimulation.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JPanel;

import de.drake.stellwerksimulation.controller.Controller;
import de.drake.stellwerksimulation.model.BetriebsstelleFuerGUI;
import de.drake.stellwerksimulation.model.Fahrplaneintrag;
import de.drake.stellwerksimulation.model.StreckeFuerGUI;
import de.drake.stellwerksimulation.tools.Richtung;

/**
 * Der Teil der GUI, der die Strecken und Betriebsstellen zeigt.
 */
class Netzpanel extends JPanel {
	
    /**
	 * Die serialVersionUID für Netzpanel
	 */
	private final static long serialVersionUID = 1L;
	
    /**
	 * Der Durchmesser einer Betriebsstelle in Pixel
	 */
	private int betriebsstellenbreite;
	
    /**
	 * Die Breite einer Strecke in Pixel
	 */
	private int streckenbreite;

    /**
	 * Die Breite einer Brückenüberführung in Pixel
	 */
	private int brueckenbreiteOben;
	
    /**
	 * Die Breite einer Brückenunterführung in Pixel
	 */
	private int brueckenbreiteUnten;
	
    /**
	 * Die Schriftgröße, die zur Beschriftung im Netzpanel verwendet wird.
	 */
	private int schriftgroesse;
	
	/**
	 * Die Anzahl der Strecken, die horizontal nebeneinander auf den Bildschirm passen
	 * sollen
	 */
	private int groesseStreckennetzHorizontal = 0;
	
	/**
	 * Die Anzahl der Strecken, die vertikal übereinander auf den Bildschirm passen
	 * sollen
	 */
	private int groesseStreckennetzVertikal = 0;
	
	/**
	 * Gibt an, welche Betriebsstelle derzeit selektiert ist.
	 */
	private BetriebsstelleFuerGUI selektierteBetriebsstelle;
	
	/**
	 * Eine Liste aller Streckengrafiken, die im Netzpanel gezeichnet werden sollen.
	 * Wird bei jedem Repaint neu gefüllt.
	 */
	private ArrayList<Teilstreckengrafik> teilstrecken
		= new ArrayList<Teilstreckengrafik>();
	
	/**
	 * Konstruktor für das Netzpanel.
	 * 
	 * @param streckenbreite
	 * 		Die Breite der Strecken in Pixeln
	 * @param betriebsstellenbreite
	 * 		Die Breite der Betriebsstellen in Pixeln
	 * @param brueckenbreiteOben
	 * 		Die Breite einer Brückenüberführung in Pixeln
	 * @param brueckenbreiteUnten
	 * 		Die Breite einer Brückenunterführung in Pixeln
	 */
	Netzpanel(final int streckenbreite, final int betriebsstellenbreite,
			final int brueckenbreiteOben, final int brueckenbreiteUnten,
			final int schriftgroesse) {
		super();
		this.streckenbreite = streckenbreite;
		this.betriebsstellenbreite = betriebsstellenbreite;
		this.brueckenbreiteOben = brueckenbreiteOben;
		this.brueckenbreiteUnten = brueckenbreiteUnten;
		this.schriftgroesse = schriftgroesse;
		this.groesseStreckennetzHorizontal =
			StreckeFuerGUI.getGroesseStreckennetzHorizontal();
		this.groesseStreckennetzVertikal =
			StreckeFuerGUI.getGroesseStreckennetzVertikal();
		this.addMouseListener(new Betriebsstellenlistener(this));
	}
	
	/**
	 * Gibt die Breite einer Betriebsstelle zurück.
	 */
	int getBetriebsstellenbreite() {
		return this.betriebsstellenbreite;
	}

	/**
	 * Zeichnet das Netzpanel neu. Wird nur intern angesteuert, Neuzeichnen kann
	 * über den Befehl repaint() erzwungen werden.
	 * 
	 * @param g
	 * 		Interne Grafikkomponente
	 */
	@Override
	protected void paintComponent(Graphics g) {
		this.erzeugeTeilstreckengrafiken();
    	Graphics2D graphic = (Graphics2D) g;
		graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Hintergrundfarbe
		graphic.setColor(new Color(236, 233, 216));
		graphic.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		this.zeichneTeilstrecken(graphic, Teilstreckengrafik.FREI, false);
		this.zeichneTeilstrecken(graphic, Teilstreckengrafik.FAHRWEG, false);
		this.zeichneTeilstrecken(graphic, Teilstreckengrafik.ZUG, false);
		
		this.zeichneTeilstrecken(graphic, Teilstreckengrafik.FREI, true);
		this.zeichneTeilstrecken(graphic, Teilstreckengrafik.FAHRWEG, true);
		this.zeichneTeilstrecken(graphic, Teilstreckengrafik.ZUG, true);

		graphic.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
    	for (BetriebsstelleFuerGUI betriebsstelle :
    			BetriebsstelleFuerGUI.getAlleBetriebsstellenFuerGUI()) {
       		this.zeichneBetriebsstelle(graphic, betriebsstelle);
    	}
    	this.beschrifteStrecken(graphic);
    }

	/**
	 * Zeichnet alle Teilstrecken einer bestimmten Art.
	 * 
	 * @param graphic
	 * 		das Zeichenobjekt
	 * @param art
	 * 		die Art der zu zeichnenden Strecken, codiert als Teilstreckengrafik-final int
	 * @param zeichneBrueckeOben
	 * 		gibt an, ob nur obere Brückenteile (true) oder alle anderen Strecken (incl.
	 * 		untere Brückenteile) gezeichnet werden sollen.
	 */
	private void zeichneTeilstrecken(final Graphics2D graphic, final int art,
			final boolean zeichneBrueckeOben) {
		switch (art) {
			case Teilstreckengrafik.FREI:
				graphic.setColor(Color.black);
				break;
			case Teilstreckengrafik.FAHRWEG:
				graphic.setColor(Color.green);
				break;
			case Teilstreckengrafik.ZUG:
				graphic.setColor(Color.red);
		}
		int vonX, vonY, nachX, nachY;
		Boolean istBrueckeOben;
		for (Teilstreckengrafik strecke : this.teilstrecken) {
			istBrueckeOben = strecke.istBrueckeOben();
    		if (strecke.getArt() != art)
    			continue;
    		if (istBrueckeOben == null && zeichneBrueckeOben == true)
    			continue;
    		if (istBrueckeOben != null && !istBrueckeOben.equals(zeichneBrueckeOben))
    			continue;
    		if (istBrueckeOben == null)
    			graphic.setStroke(new BasicStroke(this.streckenbreite,
    					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    		else if (zeichneBrueckeOben)
    			graphic.setStroke(new BasicStroke(this.brueckenbreiteOben,
    					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    		else
    			graphic.setStroke(new BasicStroke(this.brueckenbreiteUnten,
    					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    		vonX = this.skaliereX(strecke.getVonX());
    		vonY = this.skaliereY(strecke.getVonY());
    		nachX = this.skaliereX(strecke.getNachX());
    		nachY = this.skaliereY(strecke.getNachY());
    		graphic.drawLine(vonX, vonY,
    				nachX, nachY);
    	}
	}
	
	/**
	 * Zeichnet eine Betriebsstelle.
	 * 
	 * @param graphic
	 * 		das Zeichenobjekt
	 * @param betriebsstelle
	 * 		Die Betriebsstelle, die gezeichnet werden soll.
	 */
	private void zeichneBetriebsstelle(final Graphics2D graphic, 
			BetriebsstelleFuerGUI betriebsstelle) {
		switch (betriebsstelle.getKlickverhalten(
   				this.selektierteBetriebsstelle)) {
   			case BetriebsstelleFuerGUI.UNSELEKTIERT:
   				graphic.setColor(Color.gray);
   				break;
   			case BetriebsstelleFuerGUI.SELEKTIERT:
  				graphic.setColor(Color.blue);
  				break;
   			case BetriebsstelleFuerGUI.FAHRWEG_EINSTELLBAR:
  				graphic.setColor(Color.green);
  				break;
   			case BetriebsstelleFuerGUI.FAHRWEG_AUFLOESBAR:
  				graphic.setColor(Color.red);
  				break;
   			case BetriebsstelleFuerGUI.FAHRWEG_BELEGT:
   				graphic.setColor(Color.yellow);
   		}
   		int durchmesser = this.betriebsstellenbreite;
   		int positionX = this.skaliereX(betriebsstelle.getPositionX());
   		int positionY = this.skaliereY(betriebsstelle.getPositionY());
 		graphic.fillOval(positionX - durchmesser/2, positionY - durchmesser/2,
				durchmesser, durchmesser);
		graphic.setColor(Color.black);
		graphic.drawOval(positionX - durchmesser/2, positionY - durchmesser/2,
				durchmesser, durchmesser);
	}
	
	/**
	 * Beschriftet alle Strecken mit den sich dort befindlichen Zügen sowie
	 * Betriebsstellen zusätzlich mit ihrem Namen.
	 * 
	 * @param graphic
	 * 		das Zeichenobjekt
	 */
	private void beschrifteStrecken(final Graphics2D graphic) {
		graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
		int positionX, positionY, laufendeNummerZug;
		for (StreckeFuerGUI strecke : StreckeFuerGUI.getAlleStreckenFuerGUI()) {
	   		positionX = this.skaliereX(strecke.getPositionX());
	   		positionY = this.skaliereY(strecke.getPositionY());
	   		laufendeNummerZug = 0;
			for (Fahrplaneintrag zug : new ArrayList<Fahrplaneintrag>(strecke.getZuegeAufStrecke())) {
				switch (zug.getStatus()) {
				case Fahrplaneintrag.ZUG_STEHT:
					graphic.setFont(new Font("Serif", Font.PLAIN, this.schriftgroesse));
					graphic.setColor(Color.BLACK);
					break;
				case Fahrplaneintrag.ZUG_FAEHRT:
					graphic.setFont(new Font("Serif", Font.PLAIN, this.schriftgroesse));
					graphic.setColor(new Color(0,128,0));
					break;
				case Fahrplaneintrag.ZUG_WARTET:
					graphic.setFont(new Font("Serif", Font.BOLD, this.schriftgroesse));
					graphic.setColor(Color.RED);
					break;
				case Fahrplaneintrag.ZUG_FAEHRT_BALD_AB:
					graphic.setFont(new Font("Serif", Font.PLAIN, this.schriftgroesse));
					graphic.setColor(Color.BLUE);
				}
				graphic.drawString(zug.toString(),
						positionX + strecke.getpositionZugnummerX(),
						(int) (positionY + strecke.getpositionZugnummerY()
						+ laufendeNummerZug * this.schriftgroesse * 1.25));
				laufendeNummerZug++;
			}
		}
		for (BetriebsstelleFuerGUI betriebsstelle
					: BetriebsstelleFuerGUI.getAlleBetriebsstellenFuerGUI()) {
	   		positionX = this.skaliereX(betriebsstelle.getPositionX());
	   		positionY = this.skaliereY(betriebsstelle.getPositionY());
			graphic.setFont(new Font("Serif", Font.PLAIN, this.schriftgroesse));
			if (betriebsstelle.zuegeErwartet()) {
				graphic.setColor(Color.BLUE);
			} else {
				graphic.setColor(Color.BLACK);
			}
			graphic.drawString(betriebsstelle.getNameNetz(),
					positionX + betriebsstelle.getpositionBahnhofsnameX(),
					positionY + betriebsstelle.getpositionBahnhofsnameY());
	   		laufendeNummerZug = 1;
			graphic.setFont(new Font("Serif", Font.BOLD, this.schriftgroesse));
	   		graphic.setColor(Color.RED);
			for (Fahrplaneintrag zug : betriebsstelle.getWartendeErwarteteZuege()) {
				if (laufendeNummerZug == 4) {
					break;
				}
				graphic.drawString(zug.toString(),
						positionX + betriebsstelle.getpositionBahnhofsnameX(),
						(int) (positionY + betriebsstelle.getpositionBahnhofsnameY()
						+ laufendeNummerZug * this.schriftgroesse * 1.25));
				laufendeNummerZug++;
			}
		}
	}

	/**
	 * Spaltet das Streckennetz des Models zur grafischen Darstellung in
	 * Teilstrecken auf.
	 */
	private void erzeugeTeilstreckengrafiken() {
		float vonX, nachX, vonY, nachY;
		int art;
		this.teilstrecken.clear();
		for (StreckeFuerGUI streckeFuerGUI : StreckeFuerGUI.getAlleStreckenFuerGUI()) {
			for (Richtung richtung : streckeFuerGUI.getAnschlussrichtungen()) {
				int richtungDX, richtungDY;
				switch (richtung) {
					case OST:
					case NORDOST:
					case SUEDOST:
						richtungDX = 1;
						break;
					case WEST:
					case SUEDWEST:
					case NORDWEST:
						richtungDX = -1;
						break;
					default: richtungDX = 0;
				}
				switch (richtung) {
					case NORD:
					case NORDWEST:
					case NORDOST:
						richtungDY = 1;
						break;
					case SUED:
					case SUEDOST:
					case SUEDWEST:
						richtungDY = -1;
						break;
					default: richtungDY = 0;
				}
				vonX = streckeFuerGUI.getPositionX();
				nachX = streckeFuerGUI.getPositionX() + .5f * richtungDX;
				vonY = streckeFuerGUI.getPositionY();
				nachY = streckeFuerGUI.getPositionY() - .5f * richtungDY;
				
				art = Teilstreckengrafik.FREI;
				if (!streckeFuerGUI.istFrei() && streckeFuerGUI.getEingestellteRichtungen()
							.contains(richtung)) {
					if (streckeFuerGUI.istBefahren())
						art = Teilstreckengrafik.ZUG;
					else
						art = Teilstreckengrafik.FAHRWEG;
				}
				this.teilstrecken.add(new Teilstreckengrafik(vonX, nachX, vonY,
						nachY, art, streckeFuerGUI.istBrueckeOben()));
			}
		}
	}
	
	/**
	 * Gibt die derzeit selektierte Betriebsstelle zurück.
	 */
	BetriebsstelleFuerGUI getSelektierteBetriebsstelle() {
		return this.selektierteBetriebsstelle;
	}

	/**
	 * Sorgt dafür, dass keine Betriebsstelle mehr selektiert ist.
	 */
	void deselektiereBetriebsstelle() {
		if (this.selektierteBetriebsstelle == null)
			return;
		this.selektierteBetriebsstelle = null;
		this.repaint();
	}
	
	/**
	 * Markiert eine Betriebsstelle als selektiert.
	 * 
	 * @param betriebsstelle
	 * 		Die Betriebsstelle, die selektiert wurde.
	 */
	void selektiereBetriebsstelle(final BetriebsstelleFuerGUI betriebsstelle) {
		if (this.selektierteBetriebsstelle != null)
			throw new Error("Es wurde versucht, mehr als eine Betriebsstelle zu" +
					"selektieren!");
		this.selektierteBetriebsstelle = betriebsstelle;
		this.repaint();
	}
	
	/**
	 * Skaliert eine X-Koordinate auf die Größe des Netzpanels.
	 * 
	 * @param koordinateX
	 * 		die umzuskalierende Koordinate.
	 */
	int skaliereX(final float koordinateX) {
		return (int) (koordinateX * this.getWidth()
				/ this.groesseStreckennetzHorizontal);
	}
	
	/**
	 * Skaliert eine Y-Koordinate auf die Größe des Netzpanels.
	 * 
	 * @param koordinateY
	 * 		die umzuskalierende Koordinate.
	 */
	int skaliereY(final float koordinateY) {
		return (int) (koordinateY * this.getHeight()
				/ this.groesseStreckennetzVertikal);
	}
}

/**
 * Verarbeitet Mausklicks auf Betriebsstellen.
 */
class Betriebsstellenlistener implements MouseListener {
	
	/**
	 * Das Netzpanel, in das die Teilstrecken gezeichnet werden sollen.
	 */
	private Netzpanel netzpanel;
	
	/**
	 * Erzeugt einen MouseListener, der Klicks auf Betriebsstellen verarbeitet.
	 * 
	 *  @param netzpanel
	 *  		Das Netzpanel, das überwacht wird.
	 */
	Betriebsstellenlistener (final Netzpanel netzpanel) {
		this.netzpanel = netzpanel;
	}
	
	/**
	 * Wird auf ein Objekt geklickt, passiert nichts.
	 */
	public void mouseClicked(MouseEvent mausklick) {
	}

	/**
	 * Bewegt sich die Maus auf das Netzpanel, passiert nichts.
	 */
	public void mouseEntered(MouseEvent mausklick) {
	}

	/**
	 * Wird die Maustaste losgelassen, passiert nichts.
	 */
	public void mouseReleased(MouseEvent mausklick) {
	}
	
	/**
	 * Verlässt die Maus das Netzpanel, passiert nichts.
	 */
	public void mouseExited(MouseEvent mausklick) {
	}

	/**
	 * Wird ausgelöst, wenn die Maustaste gedrückt wird.
	 * 
	 * @param mausklick
	 * 		Erzeugtes MouseEvent. Mit diesem kann der Klick einer Betriebsstelle
	 * 		zugeordnet werden.
	 */
	public void mousePressed(MouseEvent mausklick) {
		if (mausklick.getButton() != MouseEvent.BUTTON1) {
			this.netzpanel.deselektiereBetriebsstelle();
			return;
		}
		BetriebsstelleFuerGUI selektierteBetriebsstelle =
				this.netzpanel.getSelektierteBetriebsstelle();
		BetriebsstelleFuerGUI angeklickteBetriebsstelle = null;
		for (BetriebsstelleFuerGUI betriebsstelle : 
				BetriebsstelleFuerGUI.getAlleBetriebsstellenFuerGUI()) {
			if (this.getDifferenz(betriebsstelle, mausklick)
					< netzpanel.getBetriebsstellenbreite()/2d) {
				angeklickteBetriebsstelle = betriebsstelle;
				break;
			}
		}
		if (angeklickteBetriebsstelle == null)
			return;
		switch (angeklickteBetriebsstelle.getKlickverhalten(
				selektierteBetriebsstelle)) {
			case BetriebsstelleFuerGUI.UNSELEKTIERT:
				if (selektierteBetriebsstelle == null)
					this.netzpanel.selektiereBetriebsstelle(angeklickteBetriebsstelle);
				else
					this.netzpanel.deselektiereBetriebsstelle();
				return;
			case BetriebsstelleFuerGUI.SELEKTIERT:
				this.netzpanel.deselektiereBetriebsstelle();
				return;
			case BetriebsstelleFuerGUI.FAHRWEG_EINSTELLBAR:
			case BetriebsstelleFuerGUI.FAHRWEG_BELEGT:
				Controller.getInstance().neuerFahrweg(
						selektierteBetriebsstelle, angeklickteBetriebsstelle);
				this.netzpanel.deselektiereBetriebsstelle();
				return;
			case BetriebsstelleFuerGUI.FAHRWEG_AUFLOESBAR:
				Controller.getInstance().loeseFahrwegAuf(
						selektierteBetriebsstelle, angeklickteBetriebsstelle);
				this.netzpanel.deselektiereBetriebsstelle();
		}
	}
	
	/**
	 * Berechnet die Differenz zwischen dem Mittelpunkt einer Teilstreckengrafik und
	 * dem Ort, wo der User hingeklickt hat.
	 * 
	 * @param betriebsstelle
	 * 		Die Betriebsstelle, mit der vergleichen werden soll
	 * @param mausklick
	 * 		Das MouseEvent, das durch den Klick erzeugt wurde
	 * 
	 * @return
	 * 		die Differenz, gerundet auf Pixel.
	 */
	private double getDifferenz(BetriebsstelleFuerGUI betriebsstelle,
			MouseEvent mausklick) {
		long positionX = this.netzpanel.skaliereX(betriebsstelle.getPositionX());
		long positionY = this.netzpanel.skaliereY(betriebsstelle.getPositionY());
		long differenzQuadrat =
				  (positionX - mausklick.getX()) * (positionX - mausklick.getX())
				+ (positionY - mausklick.getY()) * (positionY - mausklick.getY());
		return Math.sqrt(differenzQuadrat);
	}
}