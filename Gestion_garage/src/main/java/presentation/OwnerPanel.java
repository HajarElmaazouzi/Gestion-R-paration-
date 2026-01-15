package presentation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import dao.UserDAO;
import dao.ReparateurDAO;
import dao.Reparation;
import dao.EtatReparation;
import metier.GestionUtilisateur;
import metier.GestionReparation;

public class OwnerPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel model;
	private UserDAO owner;  // Stocker le propriétaire connecté

	public OwnerPanel(UserDAO owner, MainWindow mainWindow) {
		this.owner = owner;  // Stocker le propriétaire
		setLayout(null);
		setBackground(new Color(240, 248, 255));
		
		// Header avec style moderne
		JPanel headerPanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				GradientPaint gp = new GradientPaint(0, 0, new Color(52, 152, 219), 0, getHeight(), new Color(41, 128, 185));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
			}
		};
		headerPanel.setOpaque(false);
		headerPanel.setBounds(0, 0, 900, 80);
		add(headerPanel);
		
		JLabel lblTitle = new JLabel("🔧 Electro Rabat - Mol Chi");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setBounds(20, 15, 400, 35);
		headerPanel.add(lblTitle);
		
		JLabel lblWelcome = new JLabel("Bienvenue, " + owner.getUsername() + " 👋");
		lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblWelcome.setForeground(new Color(255, 255, 255, 200));
		lblWelcome.setBounds(20, 50, 300, 20);
		headerPanel.add(lblWelcome);
		
		// Bouton déconnexion dans le header
		JButton btnLogout = createModernButton("🚪 Déconnexion", new Color(231, 76, 60), new Color(192, 57, 43));
		btnLogout.setBounds(750, 20, 130, 40);
		headerPanel.add(btnLogout);
		
		// Panel latéral pour les boutons de navigation
		JPanel sidebarPanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(Color.WHITE);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 0, 0);
				
				// Ombre
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
		
		// ========== BOUTONS DE NAVIGATION ==========
		
		JButton btnBoutiques = createSidebarButton("🏪 Mes Boutiques", new Color(52, 152, 219));
		btnBoutiques.setBounds(10, 20, 160, 50);
		sidebarPanel.add(btnBoutiques);
		
		JButton btnReparateurs = createSidebarButton("👥 Mes Réparateurs", new Color(46, 204, 113));
		btnReparateurs.setBounds(10, 80, 160, 50);
		sidebarPanel.add(btnReparateurs);
		
		JButton btnMesReparations = createSidebarButton("🔧 Mes Réparations", new Color(155, 89, 182));
		btnMesReparations.setBounds(10, 140, 160, 50);
		sidebarPanel.add(btnMesReparations);
		
		JButton btnStockAppareils = createSidebarButton("📱 Les Appareils", new Color(241, 196, 15));
		btnStockAppareils.setBounds(10, 200, 160, 50);
		sidebarPanel.add(btnStockAppareils);
		
		JButton btnEmprunts = createSidebarButton("💰 Emprunts", new Color(230, 126, 34));
		btnEmprunts.setBounds(10, 260, 160, 50);
		sidebarPanel.add(btnEmprunts);
		
		JButton btnRefresh = createSidebarButton("🔄 Actualiser", new Color(127, 140, 141));
		btnRefresh.setBounds(10, 320, 160, 50);
		sidebarPanel.add(btnRefresh);
		
		// ========== TABLEAU DES RÉPARATEURS ==========
		
		JPanel tablePanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(Color.WHITE);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
				
				// Ombre
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
		
		JLabel lblTableTitle = new JLabel("📊 Vue d'ensemble des Réparateurs");
		lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTableTitle.setForeground(new Color(44, 62, 80));
		lblTableTitle.setBounds(20, 15, 400, 30);
		tablePanel.add(lblTableTitle);
		
		String[] columnNames = {"Username", "Num Boutique", "Total Caisse"};
		model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		table.setRowHeight(35);
		table.setSelectionBackground(new Color(52, 152, 219, 100));
		table.setSelectionForeground(new Color(44, 62, 80));
		table.setGridColor(new Color(230, 230, 230));
		table.setShowGrid(true);
		
		// Style du header du tableau
		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(new Color(52, 152, 219));
		header.setForeground(Color.WHITE);
		header.setPreferredSize(new Dimension(header.getWidth(), 40));
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(20, 55, 640, 410);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(Color.WHITE);
		tablePanel.add(scrollPane);
		
		// Charger les réparateurs
		loadReparateurs();
		
		// ========== ACTIONS ==========
		
		btnBoutiques.addActionListener(e -> {
			mainWindow.showMesBoutiquesPanel(owner);
		});
		
		btnReparateurs.addActionListener(e -> {
			mainWindow.showMesReparateursPanel(owner);
		});
		
		btnMesReparations.addActionListener(e -> {
			mainWindow.showListeReparations(owner, true);
		});
		
		btnStockAppareils.addActionListener(e -> {
			mainWindow.showGestionAppareils(owner, false);  // false = vient de OwnerPanel
		});
		
		btnEmprunts.addActionListener(e -> {
			mainWindow.showGestionEmprunts(owner);
		});
		
		btnRefresh.addActionListener(e -> {
			loadReparateurs();
		});
		
		btnLogout.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(this, 
				"Voulez-vous vraiment vous déconnecter?", 
				"Déconnexion", 
				JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				mainWindow.showLoginPanel();
			}
		});
	}
	
	private void loadReparateurs() {
		model.setRowCount(0);
		GestionUtilisateur gu = new GestionUtilisateur();
		GestionReparation gestionReparation = new GestionReparation();
		// Filtrer les réparateurs par le propriétaire connecté
		List<ReparateurDAO> reparateurs;
		if (owner instanceof dao.Proprietaire) {
			metier.GestionBoutique gestionBoutique = new metier.GestionBoutique();
			reparateurs = gestionBoutique.listerReparateursParProprietaire((dao.Proprietaire) owner);
		} else {
			reparateurs = gu.ListerReparateurs();
		}
		
		if (reparateurs != null && !reparateurs.isEmpty()) {
			for (ReparateurDAO r : reparateurs) {
				Double totalEncaisse = 0.0;
				
				// Méthode 1: Récupérer depuis la caisse (si disponible)
				if (r.getCaisse() != null && r.getCaisse().getId() != null) {
					try {
						metier.GestionCaisses gestionCaisses = new metier.GestionCaisses();
						dao.CaisseDAO caisseFraiche = gestionCaisses.consulterCaisse(r.getId().intValue());
						if (caisseFraiche != null) {
							totalEncaisse = caisseFraiche.getTotalEncaisse() != null ? caisseFraiche.getTotalEncaisse() : 0.0;
						}
					} catch (Exception e) {
						System.err.println("⚠️ Erreur lors du chargement de la caisse pour " + r.getUsername() + ": " + e.getMessage());
					}
				}
				
				// Méthode 2: Calculer à partir des réparations terminées (source de vérité)
				// Si le total de la caisse est 0 ou suspect, on recalcule depuis les réparations
				try {
					List<Reparation> reparationsTerminees = gestionReparation.listerMesReparations(r);
					Double totalDepuisReparations = 0.0;
					
					for (Reparation rep : reparationsTerminees) {
						if (rep.getEtat() == EtatReparation.TERMINE && rep.getCoutTotal() != null && rep.getCoutTotal() > 0) {
							totalDepuisReparations += rep.getCoutTotal();
						}
					}
					
					// Utiliser le maximum entre les deux valeurs pour être sûr d'avoir le bon total
					// (en cas de réparations terminées avant l'encaissement automatique)
					if (totalDepuisReparations > totalEncaisse) {
						totalEncaisse = totalDepuisReparations;
						System.out.println("💰 Réparateur " + r.getUsername() + " - Total calculé depuis réparations: " + totalEncaisse + " DH");
					} else {
						System.out.println("💰 Réparateur " + r.getUsername() + " - Total encaissé (caisse): " + totalEncaisse + " DH");
					}
				} catch (Exception e) {
					System.err.println("⚠️ Erreur lors du calcul depuis les réparations pour " + r.getUsername() + ": " + e.getMessage());
				}
				
				Object[] row = {
					r.getUsername(),
					r.getBoutique() != null ? r.getBoutique().getId() : "N/A",
					String.format("%.2f", totalEncaisse) + " DH"
				};
				model.addRow(row);
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
		
		// Déterminer la couleur du texte selon la luminosité du fond
		// Si le fond est clair (jaune), utiliser du texte foncé, sinon blanc
		Color textColor;
		if (bgColor.getRed() > 200 && bgColor.getGreen() > 200) {
			// Fond clair (jaune) - utiliser texte foncé
			textColor = new Color(44, 62, 80);
		} else {
			// Fond foncé - utiliser texte blanc
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