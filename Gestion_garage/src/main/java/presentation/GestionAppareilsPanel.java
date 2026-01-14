package presentation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import dao.*;
import metier.GestionReparation;
import exceptions.GestionException;

public class GestionAppareilsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel model;
	private GestionReparation gestion;
	private UserDAO user;
	private MainWindow mainWindow;
	private boolean fromReparateurPanel;  // Indique si on vient du ReparateurPanel

	public GestionAppareilsPanel(UserDAO user, MainWindow mainWindow, boolean fromReparateurPanel) {
		this.user = user;
		this.mainWindow = mainWindow;
		this.fromReparateurPanel = fromReparateurPanel;
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
				GradientPaint gp = new GradientPaint(0, 0, new Color(241, 196, 15), 0, getHeight(), new Color(243, 156, 18));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
			}
		};
		headerPanel.setOpaque(false);
		headerPanel.setBounds(0, 0, 900, 80);
		add(headerPanel);
		
		JLabel lblTitle = new JLabel("📱 Gestion du Stock d'Appareils");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
		lblTitle.setForeground(new Color(44, 62, 80));
		lblTitle.setBounds(20, 15, 500, 35);
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
		
		JButton btnModifier = createSidebarButton("✏️ Modifier", new Color(52, 152, 219));
		btnModifier.setBounds(10, 20, 160, 50);
		sidebarPanel.add(btnModifier);
		
		JButton btnSupprimer = createSidebarButton("🗑️ Supprimer", new Color(231, 76, 60));
		btnSupprimer.setBounds(10, 80, 160, 50);
		sidebarPanel.add(btnSupprimer);
		
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
		
		JLabel lblTableTitle = new JLabel("📊 Liste des Appareils");
		lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTableTitle.setForeground(new Color(44, 62, 80));
		lblTableTitle.setBounds(20, 15, 400, 30);
		tablePanel.add(lblTableTitle);
		
		// Tableau
		String[] columnNames = {"ID", "IMEI", "Marque", "Modele", "Type", "Etat", "Date Ajout"};
		model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		table.setRowHeight(35);
		table.setSelectionBackground(new Color(241, 196, 15, 100));
		table.setSelectionForeground(new Color(44, 62, 80));
		
		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(new Color(241, 196, 15));
		header.setForeground(new Color(44, 62, 80));
		header.setPreferredSize(new Dimension(header.getWidth(), 40));
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(20, 55, 640, 410);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(Color.WHITE);
		tablePanel.add(scrollPane);
		
		// Charger les données
		refreshTable();
		
		// ========== ACTIONS ==========
		
		btnModifier.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row != -1) {
				Long id = (Long) model.getValueAt(row, 0);
				Appareil appareil = gestion.rechercherAppareilParId(id);
				if (appareil != null) {
					FormAppareilDialog dialog = new FormAppareilDialog(mainWindow, appareil);
					dialog.setVisible(true);
					refreshTable();
				}
			} else {
				JOptionPane.showMessageDialog(this, "Selectionnez un appareil a modifier", "Info", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		btnSupprimer.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row != -1) {
				Long id = (Long) model.getValueAt(row, 0);
				int confirm = JOptionPane.showConfirmDialog(
					this,
					"Voulez-vous vraiment supprimer cet appareil?",
					"Confirmation",
					JOptionPane.YES_NO_OPTION
				);
				
				if (confirm == JOptionPane.YES_OPTION) {
					try {
						gestion.supprimerAppareil(id);
						JOptionPane.showMessageDialog(this, "Appareil supprime avec succes!");
						refreshTable();
					} catch (GestionException ex) {
						JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
					}
				}
			} else {
				JOptionPane.showMessageDialog(this, "Selectionnez un appareil a supprimer", "Info", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		btnRefresh.addActionListener(e -> refreshTable());
		
		btnRetourHeader.addActionListener(e -> {
			// Utiliser fromReparateurPanel pour déterminer où retourner
			if (fromReparateurPanel) {
				// Vient de ReparateurPanel → retourner au ReparateurPanel
				mainWindow.showReparateurPanel(user);
			} else {
				// Vient de OwnerPanel → retourner au OwnerPanel
				mainWindow.showOwnerPanel(user);
			}
		});
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
		button.setForeground(new Color(44, 62, 80));
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return button;
	}
	
	private void refreshTable() {
		model.setRowCount(0);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		// Charger tous les appareils
		List<Appareil> appareils = gestion.listerTousLesAppareils();
		
		for (Appareil a : appareils) {
			Object[] row = {
				a.getId(),
				a.getImei(),
				a.getMarque(),
				a.getModele(),
				a.getTypeAppareil(),
				a.getEtat().toString(),
				sdf.format(a.getDateAjout())
			};
			model.addRow(row);
		}
	}
}