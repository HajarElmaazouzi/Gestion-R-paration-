package presentation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import dao.*;
import metier.GestionReparation;
import exceptions.GestionException;

public class AppareilsReparationPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel model;
	private GestionReparation gestion;
	private Reparation reparation;
	private UserDAO user;
	private MainWindow mainWindow;
	private JLabel lblInfos; // Label pour afficher les infos de la réparation
	private boolean voirToutes;  // Indique si on vient de OwnerPanel (true) ou ReparateurPanel (false)

	public AppareilsReparationPanel(UserDAO user, MainWindow mainWindow, Reparation reparation, boolean voirToutes) {
		this.user = user;
		this.mainWindow = mainWindow;
		this.reparation = reparation;
		this.voirToutes = voirToutes;
		this.gestion = new GestionReparation();
		
		setLayout(null);
		setBackground(new Color(240, 248, 255));
		
		// Header avec style moderne
		JPanel headerPanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				GradientPaint gp = new GradientPaint(0, 0, new Color(155, 89, 182), 0, getHeight(), new Color(142, 68, 173));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
			}
		};
		headerPanel.setOpaque(false);
		headerPanel.setBounds(0, 0, 900, 100);
		add(headerPanel);
		
		JLabel lblTitle = new JLabel("📱 Appareils de la Réparation");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setBounds(20, 15, 500, 30);
		headerPanel.add(lblTitle);
		
		JLabel lblNumero = new JLabel("Numéro: " + reparation.getNumero());
		lblNumero.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNumero.setForeground(new Color(255, 255, 255, 220));
		lblNumero.setBounds(20, 45, 300, 20);
		headerPanel.add(lblNumero);
		
		lblInfos = new JLabel("Client: " + reparation.getClient() + " | État Réparation: " + reparation.getEtat());
		lblInfos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblInfos.setForeground(new Color(255, 255, 255, 200));
		lblInfos.setBounds(20, 70, 500, 20);
		headerPanel.add(lblInfos);
		
		JButton btnRetourHeader = createModernButton("← Retour", new Color(127, 140, 141), new Color(108, 117, 125));
		btnRetourHeader.setBounds(750, 30, 130, 40);
		headerPanel.add(btnRetourHeader);
		
		// Panel latéral
		JPanel sidebarPanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(Color.WHITE);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 0, 0);
				g2d.setColor(new Color(0, 0, 0, 10));
				for (int i = 0; i < 3; i++) {
					g2d.drawRoundRect(i, i, getWidth() - 2*i, getHeight() - 2*i, 0, 0);
				}
				g2d.dispose();
			}
		};
		sidebarPanel.setOpaque(false);
		sidebarPanel.setBounds(10, 120, 180, 460);
		add(sidebarPanel);
		
		// Boutons
		JButton btnAjouter = createSidebarButton("➕ Ajouter Appareil", new Color(46, 204, 113));
		btnAjouter.setBounds(10, 20, 160, 50);
		// Permettre l'ajout si la réparation est OUVERT ou EN_COURS
		btnAjouter.setEnabled(reparation.getEtat() == EtatReparation.OUVERT || reparation.getEtat() == EtatReparation.EN_COURS);
		sidebarPanel.add(btnAjouter);
		
		JButton btnRetirer = createSidebarButton("➖ Retirer", new Color(231, 76, 60));
		btnRetirer.setBounds(10, 80, 160, 50);
		// Permettre le retrait si la réparation est OUVERT ou EN_COURS
		btnRetirer.setEnabled(reparation.getEtat() == EtatReparation.OUVERT || reparation.getEtat() == EtatReparation.EN_COURS);
		sidebarPanel.add(btnRetirer);
		
		// Panel pour le tableau
		JPanel tablePanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(Color.WHITE);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
				g2d.setColor(new Color(0, 0, 0, 10));
				for (int i = 0; i < 5; i++) {
					g2d.drawRoundRect(i, i, getWidth() - 2*i, getHeight() - 2*i, 15, 15);
				}
				g2d.dispose();
			}
		};
		tablePanel.setOpaque(false);
		tablePanel.setBounds(200, 120, 680, 460);
		add(tablePanel);
		
		JLabel lblTableTitle = new JLabel("📊 Liste des Appareils");
		lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTableTitle.setForeground(new Color(44, 62, 80));
		lblTableTitle.setBounds(20, 15, 400, 30);
		tablePanel.add(lblTableTitle);
		
		// Tableau avec colonne pour changer l'état
		String[] columnNames = {"IMEI", "Marque", "Modele", "Type", "Etat Appareil"};
		model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 4; // Seulement la colonne "Etat" est éditable
			}
			
			@Override
			public Class<?> getColumnClass(int column) {
				if (column == 4) {
					return EtatAppareil.class;
				}
				return String.class;
			}
		};
		
		table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		table.setRowHeight(35);
		table.setSelectionBackground(new Color(155, 89, 182, 100));
		table.setSelectionForeground(new Color(44, 62, 80));
		
		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(new Color(155, 89, 182));
		header.setForeground(Color.WHITE);
		header.setPreferredSize(new Dimension(header.getWidth(), 40));
		
		// Créer un ComboBox pour la colonne Etat
		JComboBox<EtatAppareil> comboEtat = new JComboBox<>(EtatAppareil.values());
		table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(comboEtat));
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(20, 55, 640, 390);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(Color.WHITE);
		tablePanel.add(scrollPane);
		
		// Charger les appareils
		refreshTable();
		
		// ========== ACTIONS ==========
		
		btnAjouter.addActionListener(e -> ajouterAppareil());
		
		btnRetirer.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row != -1) {
				retirerAppareil(row);
			} else {
				JOptionPane.showMessageDialog(this, "Selectionnez un appareil a retirer");
			}
		});
		
		btnRetourHeader.addActionListener(e -> {
			// Utiliser le paramètre voirToutes stocké pour retourner au bon panel
			mainWindow.showListeReparations(user, voirToutes);
		});
		
		// Listener pour sauvegarder le changement d'état
		model.addTableModelListener(e -> {
			if (e.getColumn() == 4 && e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
				int row = e.getFirstRow();
				String imei = (String) model.getValueAt(row, 0);
				EtatAppareil nouvelEtat = (EtatAppareil) model.getValueAt(row, 4);
				
				// Utiliser une variable locale finale pour éviter les problèmes de lambda
				final Long reparationId = AppareilsReparationPanel.this.reparation.getId();
				
				// Recharger la réparation pour avoir les appareils à jour
				Reparation reparationActuelle = gestion.rechercherReparationParId(reparationId);
				if (reparationActuelle == null) {
					JOptionPane.showMessageDialog(AppareilsReparationPanel.this, "Erreur: Réparation non trouvée", "Erreur", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// Trouver l'appareil et modifier son état
				Appareil appareil = reparationActuelle.getAppareils().stream()
					.filter(a -> a.getImei().equals(imei))
					.findFirst()
					.orElse(null);
				
				if (appareil != null) {
					try {
						appareil.setEtat(nouvelEtat);
						gestion.modifierAppareil(appareil);
						
						// Rafraîchir la table locale
						refreshTable();
						
						// Recharger la réparation pour avoir l'état à jour
						AppareilsReparationPanel.this.reparation = gestion.rechercherReparationParId(reparationId);
						
						// Mettre à jour l'affichage de l'état dans le header
						if (AppareilsReparationPanel.this.reparation != null && AppareilsReparationPanel.this.lblInfos != null) {
							AppareilsReparationPanel.this.lblInfos.setText("Client: " + AppareilsReparationPanel.this.reparation.getClient() + " | État Réparation: " + AppareilsReparationPanel.this.reparation.getEtat());
						}
						
						JOptionPane.showMessageDialog(AppareilsReparationPanel.this, "Etat de l'appareil modifie avec succes!");
					} catch (GestionException ex) {
						JOptionPane.showMessageDialog(AppareilsReparationPanel.this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
						refreshTable(); // Recharger en cas d'erreur
					}
				}
			}
		});
	}
	
	private void refreshTable() {
		model.setRowCount(0);
		
		// Recharger la réparation pour avoir les appareils à jour
		reparation = gestion.rechercherReparationParId(reparation.getId());
		
		if (reparation.getAppareils() != null) {
			for (Appareil a : reparation.getAppareils()) {
				Object[] row = {
					a.getImei(),
					a.getMarque(),
					a.getModele(),
					a.getTypeAppareil(),
					a.getEtat()
				};
				model.addRow(row);
			}
		}
	}
	
	private void ajouterAppareil() {
		// Charger les appareils disponibles
		List<Appareil> disponibles = gestion.listerAppareilsDisponibles();
		
		if (disponibles.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Aucun appareil disponible en stock", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		// Créer une liste pour affichage
		String[] options = new String[disponibles.size()];
		for (int i = 0; i < disponibles.size(); i++) {
			Appareil a = disponibles.get(i);
			options[i] = a.getImei() + " - " + a.getMarque() + " " + a.getModele();
		}
		
		String choix = (String) JOptionPane.showInputDialog(
			this,
			"Selectionnez un appareil:",
			"Ajouter Appareil",
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]
		);
		
		if (choix != null) {
			// Extraire l'IMEI du choix
			String imei = choix.split(" - ")[0];
			Appareil appareilSelectionne = disponibles.stream()
				.filter(a -> a.getImei().equals(imei))
				.findFirst()
				.orElse(null);
			
			if (appareilSelectionne != null) {
				try {
					gestion.ajouterAppareilAReparation(reparation.getId(), appareilSelectionne.getId());
					JOptionPane.showMessageDialog(this, "Appareil ajoute avec succes!");
					refreshTable();
					// Recharger la réparation pour mettre à jour l'affichage
					reparation = gestion.rechercherReparationParId(reparation.getId());
					if (reparation != null && lblInfos != null) {
						lblInfos.setText("Client: " + reparation.getClient() + " | État Réparation: " + reparation.getEtat());
					}
				} catch (GestionException ex) {
					JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	private void retirerAppareil(int row) {
		String imei = (String) model.getValueAt(row, 0);
		
		int confirm = JOptionPane.showConfirmDialog(
			this,
			"Voulez-vous retirer cet appareil de la reparation?",
			"Confirmation",
			JOptionPane.YES_NO_OPTION
		);
		
		if (confirm == JOptionPane.YES_OPTION) {
			// Trouver l'appareil par IMEI
			Appareil appareil = reparation.getAppareils().stream()
				.filter(a -> a.getImei().equals(imei))
				.findFirst()
				.orElse(null);
			
			if (appareil != null) {
				try {
					gestion.retirerAppareilDeReparation(reparation.getId(), appareil.getId());
					JOptionPane.showMessageDialog(this, "Appareil retire avec succes!");
					refreshTable();
					// Recharger la réparation pour mettre à jour l'affichage
					reparation = gestion.rechercherReparationParId(reparation.getId());
					if (reparation != null && lblInfos != null) {
						lblInfos.setText("Client: " + reparation.getClient() + " | État Réparation: " + reparation.getEtat());
					}
				} catch (GestionException ex) {
					JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	private JButton createSidebarButton(String text, Color bgColor) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				Color currentBg = getModel().isPressed() ? bgColor.darker() : 
					(getModel().isRollover() ? bgColor.brighter() : bgColor);
				g2d.setColor(currentBg);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		button.setFont(new Font("Segoe UI", Font.BOLD, 13));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return button;
	}
	
	private JButton createModernButton(String text, Color bgColor, Color hoverColor) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				if (getModel().isPressed()) {
					g2d.setColor(hoverColor.darker());
				} else if (getModel().isRollover()) {
					g2d.setColor(hoverColor);
				} else {
					g2d.setColor(bgColor);
				}
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		button.setFont(new Font("Segoe UI", Font.BOLD, 12));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return button;
	}
}