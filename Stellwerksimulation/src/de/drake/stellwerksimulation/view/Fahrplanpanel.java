package de.drake.stellwerksimulation.view;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.drake.stellwerksimulation.model.Fahrplaneintrag;
import de.drake.stellwerksimulation.tools.DashedBorder;

/**
 * Ein Fahrplanpanel erlaubt die grafische Darstellung bestimmter Züge.
 */
class Fahrplanpanel extends JPanel {
	
	/**
	 * Die serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Beinhaltet den derzeit angezeigten Fahrplaneintrag.
	 */
	private Fahrplaneintrag selektierterFahrplaneintrag = null;
	
	/**
	 * Der Teil des Panels, der die Gleisbelegung anzeigt
	 */
	private JList<Fahrplaneintrag> gleisbelegung;

	/**
	 * Der Teil des Panels, in dem Züge ausgewählt werden können.
	 */
	private JList<Fahrplaneintrag> auswahlmenue;
	
	/**
	 * Der Teil des Panels, in dem der ausgewählte Zug angezeigt wird.
	 */
	private JTextArea zuganzeige;
	
	/**
	 * Der Teil des Panels, in dem wartende Züge ausgewählt werden können.
	 */
	private JList<Fahrplaneintrag> wartendeZuege;
	
	Fahrplanpanel() {
		super();
		this.setLayout(new GridLayout(1,2));
		
		JPanel linkeSeite = new JPanel();
		linkeSeite.setLayout(new BorderLayout());
		this.add(linkeSeite);
		
			JPanel gleisPanel = new JPanel();
			gleisPanel.setLayout(new BorderLayout());
			TitledBorder borderGleis = new TitledBorder("Gleisbelegung");
			borderGleis.setTitleJustification((TitledBorder.CENTER));
			gleisPanel.setBorder(borderGleis);
			linkeSeite.add(gleisPanel, BorderLayout.CENTER);
			
				this.gleisbelegung = new JList<Fahrplaneintrag>(
						Fahrplaneintrag.getGleisbelegung());
				this.gleisbelegung.setCellRenderer(new FahrplanCellRenderer(true));
				this.gleisbelegung.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				this.gleisbelegung.getSelectionModel().addListSelectionListener(
						new FahrplanSelectionListener(this, this.gleisbelegung));
				gleisPanel.add(new JScrollPane(this.gleisbelegung));
		
			JPanel auswahlPanel = new JPanel();
			auswahlPanel.setLayout(new BorderLayout());
			TitledBorder borderGesamt = new TitledBorder("Gesamtfahrplan");
			borderGesamt.setTitleJustification((TitledBorder.CENTER));
			auswahlPanel.setBorder(borderGesamt);
			linkeSeite.add(auswahlPanel, BorderLayout.EAST);
				
				this.auswahlmenue = new JList<Fahrplaneintrag>(
						Fahrplaneintrag.getFahrplanInSimulation());
				this.auswahlmenue.setCellRenderer(new FahrplanCellRenderer(false));
				this.auswahlmenue.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				this.auswahlmenue.getSelectionModel().addListSelectionListener(
						new FahrplanSelectionListener(this, this.auswahlmenue));
				auswahlPanel.add(new JScrollPane(this.auswahlmenue));
			
		JPanel rechteSeite = new JPanel();
		rechteSeite.setLayout(new BorderLayout());
		this.add(rechteSeite);
			
			JPanel zugPanel = new JPanel();
			zugPanel.setLayout(new BorderLayout());
			TitledBorder borderZug = new TitledBorder("Details");
			borderZug.setTitleJustification((TitledBorder.CENTER));
			zugPanel.setBorder(borderZug);
			rechteSeite.add(zugPanel, BorderLayout.CENTER);
			
				this.zuganzeige = new JTextArea();
				this.zuganzeige.setFont(new Font("Monospaced", Font.PLAIN, 12));
				this.zuganzeige.setBackground(this.getBackground());
				this.zuganzeige.setEditable(false);
				this.zuganzeige.setLineWrap(true);
				this.zuganzeige.setWrapStyleWord(true);
				this.zuganzeige.setFocusable(false);
				JScrollPane zugScroll = new JScrollPane(this.zuganzeige);
				zugScroll.setBorder(null);
				zugPanel.add(zugScroll);
			
			JPanel wartePanel = new JPanel();
			wartePanel.setLayout(new BorderLayout());
			TitledBorder borderWarte = new TitledBorder("Wartende Züge");
			borderWarte.setTitleJustification((TitledBorder.CENTER));
			wartePanel.setBorder(borderWarte);
			rechteSeite.add(wartePanel, BorderLayout.EAST);
			
				this.wartendeZuege = new JList<Fahrplaneintrag>(
						Fahrplaneintrag.getWartendeZuege());
				this.wartendeZuege.setCellRenderer(new FahrplanCellRenderer(false));
				this.wartendeZuege.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				this.wartendeZuege.getSelectionModel().addListSelectionListener(
						new FahrplanSelectionListener(this, this.wartendeZuege));
				wartePanel.add(new JScrollPane(this.wartendeZuege));

	}
	
	/**
	 * Zeichnet das Fahrplanpanel neu.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Fahrplaneintrag.updateWartendeZuege();
		Fahrplaneintrag.updateGleisbelegung();
		// JLists aktualisieren, damit beispielsweise neue Einträge abgebildet werden
		// und bei Wegfall von Einträgen die Scrollbars aktualisiert werden.
		this.wartendeZuege.updateUI();
		this.auswahlmenue.updateUI();
		this.gleisbelegung.updateUI();
		this.aktualisiereZuganzeige();
		//Selektion in den JLists aktualisieren, damit weiterhin das richtige
		//Element selektiert ist
		this.setSelektion();
	}

	/**
	 * Wird ausgelöst, wenn sich eine Selektion geändert hat.
	 * 
	 * @param quelle
	 * 		Die JList, in der sich die Selektion geändert hat.
	 */
	void selectionChanged(final JList<Fahrplaneintrag> quelle) {
		if (quelle.equals(this.wartendeZuege) && (quelle.getSelectedValue() == null))
			return;
		if (quelle.equals(this.gleisbelegung) && (quelle.getSelectedValue() == null))
			return;
		this.selektierterFahrplaneintrag = quelle.getSelectedValue();
		this.setSelektion();
		this.aktualisiereZuganzeige();
	}
	
	/**
	 * Aktualisiert die detaillierte Zugbeschreibung in der Zuganzeige.
	 */
	private void aktualisiereZuganzeige() {
		if (this.selektierterFahrplaneintrag != null) {
			this.zuganzeige.setText(this.selektierterFahrplaneintrag.getDescription());
		} else {
			this.zuganzeige.setText("");
		}
	}
	
	/**
	 * Selektiert den selektierten Fahrplaneintrag in den Auswahllisten.
	 */
	private void setSelektion() {
		if (!Fahrplaneintrag.getFahrplanInSimulation().contains(
				this.selektierterFahrplaneintrag))
			this.selektierterFahrplaneintrag = null;
		if (this.selektierterFahrplaneintrag == null) {
			this.auswahlmenue.clearSelection();
			this.wartendeZuege.clearSelection();
			this.gleisbelegung.clearSelection();
			return;
		}
		this.auswahlmenue.setSelectedIndex(Fahrplaneintrag.getFahrplanInSimulation()
				.lastIndexOf(this.selektierterFahrplaneintrag));
		if (Fahrplaneintrag.getWartendeZuege()
				.contains(this.selektierterFahrplaneintrag)) {
			this.wartendeZuege.setSelectedIndex(Fahrplaneintrag.getWartendeZuege()
					.lastIndexOf(this.selektierterFahrplaneintrag));
		} else {
			this.wartendeZuege.clearSelection();
		}
		if (Fahrplaneintrag.getGleisbelegung()
				.contains(this.selektierterFahrplaneintrag)){
			this.gleisbelegung.setSelectedIndex(Fahrplaneintrag.getGleisbelegung()
					.lastIndexOf(this.selektierterFahrplaneintrag));
		} else {
			this.gleisbelegung.clearSelection();
		}
	}
	
	/**
	 * Setzt und fixiert die Größe der Fahrplanpanels.
	 */
	void lockSize() {
		this.auswahlmenue.getParent().setPreferredSize(
				this.auswahlmenue.getParent().getSize());
		this.wartendeZuege.getParent().setPreferredSize(
				this.auswahlmenue.getParent().getSize());
	}
}

/**
 * Der FahrplanListCellRenderer beschreibt, wie Züge im Auswahlmenü des Fahrplanfensters
 * dargestellt werden sollen.
 */
class FahrplanCellRenderer implements ListCellRenderer<Fahrplaneintrag> {

	/**
	 * Gibt an, ob die Formatierung Gleisangaben beinhalten soll oder nicht.
	 */
	private boolean gleisanzeige;
	
	/**
	 * Erzeugt einen neuen FahrplancCellRenderer.
	 * 
	 * @param gleisanzeige 
	 * 		Gibt an, ob die Formatierung Gleisangaben beinhalten soll oder nicht.
	 */
	FahrplanCellRenderer(final boolean gleisanzeige) {
		super();
		this.gleisanzeige = gleisanzeige;
	}
	
	/**
	 * Erzeugt das JLabel, das zu einem entsprechenden Fahrplaneintrag angezeigt
	 * werden soll.
	 * 
	 * @param list
	 * 		Die Liste, in der der Fahrplaneintrag angezeigt werden soll.
	 * @param zug
	 * 		Der Fahrplaneintrag, der formatiert werden soll.
	 * @param index
	 * 		Die Position des Fahrplaneintrags in der gesamten Liste.
	 * @param isSelected
	 * 		Gibt an, ob der Eintrag derzeit selektiert ist.
	 * @param cellHasFocus
	 * 		Gibt an, ob das Fenster derzeit im Vordergrund ist.
	 */
	public Component getListCellRendererComponent(
			final JList<? extends Fahrplaneintrag> list,
			final Fahrplaneintrag zug,
			final int index, final boolean isSelected, final boolean cellHasFocus) {
		JLabel result = new JLabel();
		result.setFont(new Font("Monospaced", Font.PLAIN, 12));
		if (this.gleisanzeige == true) {
			result.setText(zug.getGleisbelegungsDescription());
		} else {
			result.setText(zug.getShortDescription());
		}
		result.setOpaque(true);
		Color foregroundColor = null;
		Color backgroundColor = null;
		Color borderColor = null;
		if (isSelected){
			foregroundColor = Color.WHITE;
			switch (zug.getStatus()) {
				case Fahrplaneintrag.ZUG_STEHT:
					backgroundColor = Color.BLACK;
					break;
				case Fahrplaneintrag.ZUG_FAEHRT:
					backgroundColor = new Color(0,128,0);
					break;
				case Fahrplaneintrag.ZUG_WARTET:
					backgroundColor = Color.RED;
					break;
				case Fahrplaneintrag.ZUG_FAEHRT_BALD_AB:
					backgroundColor = Color.BLUE;
			}
			if (cellHasFocus) {
				borderColor = Color.WHITE;
			} else {
				borderColor = backgroundColor;
			}
		} else {
	    	backgroundColor = Color.WHITE;
			switch (zug.getStatus()) {
				case Fahrplaneintrag.ZUG_STEHT:
					foregroundColor = Color.BLACK;
					break;
				case Fahrplaneintrag.ZUG_FAEHRT:
					foregroundColor = new Color(0,128,0);
					break;
				case Fahrplaneintrag.ZUG_WARTET:
					foregroundColor = Color.RED;
					break;
				case Fahrplaneintrag.ZUG_FAEHRT_BALD_AB:
					foregroundColor = Color.BLUE;
			}
			borderColor = backgroundColor;
	    }
		result.setBackground(backgroundColor);
		result.setForeground(foregroundColor);
		result.setBorder(new DashedBorder(borderColor));
		return result;
	}
}

/**
 * Überwacht Fahrplan-JLists auf Anklicken. 
 */
class FahrplanSelectionListener implements ListSelectionListener {

	/**
	 * Das Panel, welches für die Verwaltung der Selektion verantwortlich ist.
	 */
	private Fahrplanpanel fahrplanpanel;
	
	/**
	 * Die JList, die überwacht wird.
	 */
	private JList<Fahrplaneintrag> list;
	
	/**
	 * Erzeugt einen neuen FahrplanSelectionListener.
	 * 
	 * @param fahrplanpanel
	 * 		Das Panel, welches die Selektion der integrierten JList verwaltet.
	 * @param list
	 * 		Die Liste, die überwacht wird.
	 */
	FahrplanSelectionListener(final Fahrplanpanel fahrplanpanel,
			final JList<Fahrplaneintrag> list) {
		super();
		this.fahrplanpanel= fahrplanpanel;
		this.list = list;
	}
	
	/**
	 * Stellt eine gänderte Auswahl eines Fahrplaneintrags im rechten Anzeigefenster dar.
	 * 
	 * @param event
	 * 		Event, welches Information über die geänderte Selektion beinhaltet.
	 */
	public void valueChanged(ListSelectionEvent event) {
		this.fahrplanpanel.selectionChanged(this.list);
	}
}