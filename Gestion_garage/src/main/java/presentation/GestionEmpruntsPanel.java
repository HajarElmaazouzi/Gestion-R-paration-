package presentation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import dao.*;
import metier.GestionEmprunts;
import metier.GestionUtilisateur;

public class GestionEmpruntsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel model;
	private GestionEmprunts gestionEmprunts;
	private UserDAO user;
	private MainWindow mainWindow;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	public GestionEmpruntsPanel(UserDAO user, MainWindow mainWindow) {
		this.user = user;
		this.mainWindow = mainWindow;
		this.gestionEmprunts = new GestionEmprunts();
		
		setLayout(null);
		setBackground(new Color(240, 248, 255));
		
		// Header avec style moderne
		JPanel headerPanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				GradientPaint gp = new GradientPaint(0, 0, new Color(230, 126, 34), 0, getHeight(), new Color(211, 84, 0));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
			}
		};
		headerPanel.setOpaque(false);
		headerPanel.setBounds(0, 0, 900, 80);
		add(headerPanel);
		
		JLabel lblTitle = new JLabel("💰 Gestion des Emprunts");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setBounds(20, 15, 400, 35);
		headerPanel.add(lblTitle);
		
		JButton btnRetourHeader = createModernButton("← Retour", new Color(127, 140, 141), new Color(108, 117, 125));
		btnRetourHeader.setBounds(750, 20, 130, 40);
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
		sidebarPanel.setBounds(10, 100, 180, 480);
		add(sidebarPanel);
		
		// Boutons d'action
		JButton btnCreer = createSidebarButton("➕ Créer Emprunt", new Color(46, 204, 113));
		btnCreer.setBounds(10, 20, 160, 50);
		sidebarPanel.add(btnCreer);
		
		JButton btnRembourser = createSidebarButton("💵 Rembourser", new Color(52, 152, 219));
		btnRembourser.setBounds(10, 80, 160, 50);
		sidebarPanel.add(btnRembourser);
		
		JButton btnRefresh = createSidebarButton("🔄 Actualiser", new Color(127, 140, 141));
		btnRefresh.setBounds(10, 140, 160, 50);
		sidebarPanel.add(btnRefresh);
		
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
		tablePanel.setBounds(200, 100, 680, 480);
		add(tablePanel);
		
		JLabel lblTableTitle = new JLabel("📋 Liste des Emprunts");
		lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTableTitle.setForeground(new Color(44, 62, 80));
		lblTableTitle.setBounds(20, 15, 400, 30);
		tablePanel.add(lblTableTitle);
		
		String[] columnNames = {"ID", "Montant", "Date", "Prêteur", "Statut", "Date Remboursement"};
		model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		table.setRowHeight(35);
		table.setSelectionBackground(new Color(230, 126, 34, 100));
		table.setSelectionForeground(new Color(44, 62, 80));
		table.setGridColor(new Color(230, 230, 230));
		table.setShowGrid(true);
		
		// Style du header du tableau
		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(new Color(230, 126, 34));
		header.setForeground(Color.WHITE);
		header.setPreferredSize(new Dimension(header.getWidth(), 40));
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(20, 55, 640, 410);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(Color.WHITE);
		tablePanel.add(scrollPane);
		
		// Charger les emprunts
		refreshTable();
		
		// ========== ACTIONS ==========
		
		btnCreer.addActionListener(e -> {
			creerEmprunt();
		});
		
		btnRembourser.addActionListener(e -> {
			rembourserEmprunt();
		});
		
		btnRefresh.addActionListener(e -> {
			refreshTable();
		});
		
		btnRetourHeader.addActionListener(e -> {
			if (user instanceof Proprietaire) {
				mainWindow.showOwnerPanel(user);
			} else {
				mainWindow.showReparateurPanel(user);
			}
		});
	}
	
	private void refreshTable() {
		model.setRowCount(0);
		try {
			List<EmpruntDAO> emprunts = gestionEmprunts.listerEmpruntsParUtilisateur(user.getId().intValue());
			
			if (emprunts != null && !emprunts.isEmpty()) {
				for (EmpruntDAO emprunt : emprunts) {
					String preteur = emprunt.getPreteur() != null ? emprunt.getPreteur().getUsername() : "Caisse personnelle";
					String statut = emprunt.getStatut().toString();
					String dateRemboursement = emprunt.getDateRemboursement() != null ? 
						dateFormat.format(emprunt.getDateRemboursement()) : "-";
					String dateEmprunt = emprunt.getDate() != null ? 
						dateFormat.format(emprunt.getDate()) : "-";
					
					Object[] row = {
						emprunt.getId(),
						String.format("%.2f DH", emprunt.getMontant()),
						dateEmprunt,
						preteur,
						statut,
						dateRemboursement
					};
					model.addRow(row);
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, 
				"Erreur lors du chargement des emprunts: " + e.getMessage(), 
				"Erreur", 
				JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void creerEmprunt() {
		JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Créer un Emprunt", true);
		dialog.setSize(500, 400);
		dialog.setLocationRelativeTo(this);
		dialog.setLayout(new BorderLayout());
		
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		
		// Montant
		gbc.gridx = 0; gbc.gridy = 0;
		formPanel.add(new JLabel("Montant (DH):"), gbc);
		JTextField txtMontant = new JTextField(20);
		gbc.gridx = 1;
		formPanel.add(txtMontant, gbc);
		
		// Description
		gbc.gridx = 0; gbc.gridy = 1;
		formPanel.add(new JLabel("Description:"), gbc);
		JTextArea txtDescription = new JTextArea(3, 20);
		txtDescription.setLineWrap(true);
		JScrollPane scrollDesc = new JScrollPane(txtDescription);
		gbc.gridx = 1;
		formPanel.add(scrollDesc, gbc);
		
		// Prêteur (optionnel)
		gbc.gridx = 0; gbc.gridy = 2;
		formPanel.add(new JLabel("Prêteur (optionnel):"), gbc);
		JComboBox<String> comboPreteur = new JComboBox<>();
		comboPreteur.addItem("Caisse personnelle");
		try {
			GestionUtilisateur gu = new GestionUtilisateur();
			List<ReparateurDAO> reparateurs = gu.ListerReparateurs();
			if (reparateurs != null) {
				for (ReparateurDAO r : reparateurs) {
					if (!r.getId().equals(user.getId())) {
						comboPreteur.addItem(r.getUsername() + " (ID: " + r.getId() + ")");
					}
				}
			}
		} catch (Exception e) {
			// Ignore
		}
		gbc.gridx = 1;
		formPanel.add(comboPreteur, gbc);
		
		// Boutons
		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton btnValider = new JButton("Valider");
		JButton btnAnnuler = new JButton("Annuler");
		buttonPanel.add(btnValider);
		buttonPanel.add(btnAnnuler);
		
		dialog.add(formPanel, BorderLayout.CENTER);
		dialog.add(buttonPanel, BorderLayout.SOUTH);
		
		btnValider.addActionListener(e -> {
			try {
				double montant = Double.parseDouble(txtMontant.getText());
				String description = txtDescription.getText().trim();
				
				if (montant <= 0) {
					JOptionPane.showMessageDialog(dialog, "Le montant doit être supérieur à 0", "Erreur", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if (description.isEmpty()) {
					JOptionPane.showMessageDialog(dialog, "La description est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				Integer preteurId = null;
				String selected = (String) comboPreteur.getSelectedItem();
				if (selected != null && !selected.equals("Caisse personnelle")) {
					// Extraire l'ID du prêteur depuis le texte
					String idStr = selected.substring(selected.indexOf("ID: ") + 4, selected.indexOf(")"));
					preteurId = Integer.parseInt(idStr);
				}
				
				EmpruntDAO emprunt = gestionEmprunts.creerEmprunt(
					user.getId().intValue(), 
					preteurId, 
					montant, 
					description
				);
				
				if (emprunt != null) {
					JOptionPane.showMessageDialog(dialog, "Emprunt créé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
					dialog.dispose();
					refreshTable();
				} else {
					JOptionPane.showMessageDialog(dialog, "Erreur lors de la création de l'emprunt", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(dialog, "Montant invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(dialog, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		btnAnnuler.addActionListener(e -> dialog.dispose());
		
		dialog.setVisible(true);
	}
	
	private void rembourserEmprunt() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Veuillez sélectionner un emprunt à rembourser", "Information", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		Long empruntId = (Long) model.getValueAt(selectedRow, 0);
		String statut = (String) model.getValueAt(selectedRow, 4);
		
		if (statut.equals("REMBOURSE")) {
			JOptionPane.showMessageDialog(this, "Cet emprunt est déjà remboursé", "Information", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		int confirm = JOptionPane.showConfirmDialog(this, 
			"Voulez-vous vraiment rembourser cet emprunt?", 
			"Confirmation", 
			JOptionPane.YES_NO_OPTION);
		
		if (confirm == JOptionPane.YES_OPTION) {
			try {
				gestionEmprunts.rembourserEmprunt(empruntId);
				JOptionPane.showMessageDialog(this, "Emprunt remboursé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
				refreshTable();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Erreur lors du remboursement: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private JButton createSidebarButton(String text, Color bgColor) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				Color currentBg;
				if (getModel().isPressed()) {
					currentBg = bgColor.darker();
				} else if (getModel().isRollover()) {
					currentBg = bgColor.brighter();
				} else {
					currentBg = bgColor;
				}
				
				g2d.setColor(currentBg);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		
		Color textColor;
		if (bgColor.getRed() > 200 && bgColor.getGreen() > 200) {
			textColor = new Color(44, 62, 80);
		} else {
			textColor = Color.WHITE;
		}
		
		button.setFont(new Font("Segoe UI", Font.BOLD, 13));
		button.setForeground(textColor);
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
