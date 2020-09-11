package de.drake.stellwerksimulation.view;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import de.drake.stellwerksimulation.controller.Controller;
import de.drake.stellwerksimulation.model.Stellwerk;

/**
 * Das Kontrollpanel stellt die Informationen oberhalb des Schienennetzes dar.
 */
class Kontrollpanel extends JPanel {

	/**
	 * Die final static serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Der Teil der Spieloberfläche, der die Spielzeit zeigt.
	 */
	private JLabel zeitanzeige;
	
	/**
	 * Feld, das ggfs. anzeigt, ob das Spiel pausiert ist.
	 */
	private JLabel pauseanzeige;
	
	/**
	 * Feld, das die aktuelle Spielgeschwindigkeit anzeigt.
	 */
	private JLabel geschwindigkeitsanzeige;
	
	/**
	 * Erzeugt ein neues Kontrollpanel.
	 */
	Kontrollpanel() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		
			this.zeitanzeige = new JLabel(" 99:99 ");
			TitledBorder borderZeit = new TitledBorder("Zeit");
			borderZeit.setTitleJustification((TitledBorder.CENTER));
			this.zeitanzeige.setBorder(borderZeit);
			this.add(this.zeitanzeige, gridBagConstraints);
			
			JPanel pausepanel = new JPanel();
			pausepanel.setLayout(new FlowLayout());
			TitledBorder borderPause = new TitledBorder("Pause");
			borderPause.setTitleJustification((TitledBorder.CENTER));
			pausepanel.setBorder(borderPause);
			this.add(pausepanel, gridBagConstraints);

				Button pauseButton = new Button("X");
				pauseButton.setPreferredSize(new Dimension(20,20));
				this.pauseanzeige = new JLabel(" Pause ");
				this.pauseanzeige.setForeground(Color.RED);
				pauseButton.addActionListener(new PauseListener());
				pausepanel.add(pauseButton, BorderLayout.WEST);
				pausepanel.add(this.pauseanzeige, BorderLayout.EAST);
				
			JPanel geschwindigkeitspanel = new JPanel();
			geschwindigkeitspanel.setLayout(new FlowLayout());
			TitledBorder borderGeschwindigkeit = new TitledBorder("Spielgeschwindigkeit");
			borderGeschwindigkeit.setTitleJustification((TitledBorder.CENTER));
			geschwindigkeitspanel.setBorder(borderGeschwindigkeit);
			this.add(geschwindigkeitspanel, gridBagConstraints);
			
				int startskalierung = this.getSkalierungZu(
						Controller.getInstance().getGeschwindigkeit());
				JScrollBar geschwindigkeitsregler = new JScrollBar(JScrollBar.HORIZONTAL,
						startskalierung, 20, 10, 132);
				geschwindigkeitsregler.addAdjustmentListener(
						new GeschwindigkeitsListener(this));
				geschwindigkeitsregler.setPreferredSize(new Dimension(100,20));
				geschwindigkeitspanel.add(geschwindigkeitsregler);
				
				this.geschwindigkeitsanzeige = new JLabel();
				// Um später die Größe der Geschwindigskeitsanzeige einfrieren zu können
				this.showGeschwindigkeit(10000);
				geschwindigkeitspanel.add(this.geschwindigkeitsanzeige);
	}
	
	/**
	 * Gibt an, was beim Auslösen eines Repaint aktualisiert werden soll.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		this.zeitanzeige.setText(" " + Stellwerk.getInstance().getAktuelleZeit().toString()
				+ " ");
		super.paintComponent(g);
	}
	
	/**
	 * Fixiert die Größe des Kontrollpanels.
	 */
	void lockSize() {
		this.pauseanzeige.setPreferredSize(this.pauseanzeige.getSize());
		this.geschwindigkeitsanzeige.setPreferredSize(
				this.geschwindigkeitsanzeige.getSize());
		this.showGeschwindigkeit(Controller.getInstance().getGeschwindigkeit());
	}
	
	/**
	 * Zeigt den derzeitigen Pausezustand an.
	 * 
	 * @param wert
	 * 		true, wenn "Pause" angezeigt werden soll.
	 */
	void setPause(final boolean wert) {
		if (wert == false) {
			this.pauseanzeige.setText("");
		} else {
			this.pauseanzeige.setText(" Pause ");
		}
	}
	
	/**
	 * Stellt die Spielgeschwindigkeit ein.
	 * 
	 * @param skalierung
	 * 		Der Wert des Geschwindigkeitsreglers
	 */
	void setGeschwindigkeit(final int skalierung) {
		int geschwindigkeit = this.getGeschwindigkeitZu(skalierung);
		this.showGeschwindigkeit(geschwindigkeit);
		Controller.getInstance().setGeschwindigkeit(geschwindigkeit);
	}
	
	/**
	 * Zeigt eine bestimmte Spielgeschwindigkeit an.
	 * 
	 * @param geschwindigkeit
	 * 		die anzugeigende Geschwindigkeit
	 */
	private void showGeschwindigkeit(final int geschwindigkeit) {
		this.geschwindigkeitsanzeige.setText(" "
				+ geschwindigkeit + " Bewegungen pro Minute ");
	}
	
	/**
	 * Ermittelt zu einer Spielgeschwindigkeit die passende Position des
	 * Geschwindigkeitsanzeigers.
	 * 
	 * @param geschwindigkeit
	 * 		Die anzuzeigende Spielgeschwindigkeit
	 */
	private int getSkalierungZu(final int geschwindigkeit) {
		if (geschwindigkeit <= 30)
			return geschwindigkeit;
		if (geschwindigkeit <= 100)
			return 30 + ((geschwindigkeit - 30) / 5);
		if (geschwindigkeit <= 300)
			return 30 + 14 + ((geschwindigkeit - 100) / 10);
		if (geschwindigkeit <= 1000)
			return 30 + 14 + 20 + ((geschwindigkeit - 300) / 50);
		if (geschwindigkeit <= 3000)
			return 30 + 14 + 20 + 14 + ((geschwindigkeit - 1000) / 100);
		return 30 + 14 + 20 + 14 + 20 + ((geschwindigkeit - 3000) / 500);
	}
	
	/**
	 * Ermittelt zu der Position des Geschwindigkeitsanzeigers die passende 
	 * Spielgeschwindigkeit.
	 * 
	 * @param skalierung
	 * 		Die position des Geschwindigkeitsanzeigers
	 */
	private int getGeschwindigkeitZu(final int skalierung) {
		if (skalierung <= 30)
			return skalierung;
		if (skalierung <= 30 + 14)
			return 30 + ((skalierung - 30) * 5);
		if (skalierung <= 30 + 14 + 20)
			return 100 + ((skalierung - 30 - 14) * 10);
		if (skalierung <= 30 + 14 + 20 + 14)
			return 300 + ((skalierung - 30 - 14 - 20) * 50);
		if (skalierung <= 30 + 14 + 20 + 14 + 20)
			return 1000 + ((skalierung - 30 - 14 - 20 - 14) * 100);
		return 3000 + ((skalierung - 30 - 14 - 20 - 14 - 20) * 500);
	}
}

/**
 * Listener, der überwacht ob die Spielgeschwindigkeit geändert wurde.
 */
class GeschwindigkeitsListener implements AdjustmentListener {

	/**
	 * Das Panel, das für die Anpassung der Spielgeschwindigkeit zuständig ist.
	 */
	private Kontrollpanel kontrollpanel;
	
	/**
	 * Erzeugt einen neuen Geschwindigkeits-Listener.
	 * 
	 * @param kontrolpanel
	 * 		Das Panel, das für die Anpassung der Spielgeschwindigkeit zuständig ist.
	 */
	GeschwindigkeitsListener(final Kontrollpanel kontrollpanel) {
		this.kontrollpanel = kontrollpanel;
	}
	
	/**
	 * Wird ausgelöst, wenn die Spielgeschwindigkeit geändert wurde.
	 */
	public void adjustmentValueChanged(AdjustmentEvent arg0) {
		this.kontrollpanel.setGeschwindigkeit(arg0.getValue());
	}
}