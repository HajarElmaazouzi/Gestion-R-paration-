package presentation;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.util.List;
import metier.GestionBoutique;
import dao.BoutiqueDAO;
import dao.UserDAO;


public class MesBoutiquesPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel model;
	private GestionBoutique gestion;
	private  List<BoutiqueDAO> boutiques;
	private UserDAO user;  // Stocker l'utilisateur connecté


	/**
	 * Create the panel.
	 */
	public MesBoutiquesPanel(UserDAO user, MainWindow mainWindow) {
		this.user = user;  // Stocker l'utilisateur
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
		
		JLabel lblTitle = new JLabel("🏪 Mes Boutiques");
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

	    // Colonnes de la table
	    String[] columnNames = {"ID", "Nom", "Adresse"};
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
	    
	    JTableHeader header = table.getTableHeader();
	    header.setFont(new Font("Segoe UI", Font.BOLD, 13));
	    header.setBackground(new Color(52, 152, 219));
	    header.setForeground(Color.WHITE);
	    header.setPreferredSize(new Dimension(header.getWidth(), 40));

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
		
		JLabel lblTableTitle = new JLabel("📊 Liste des Boutiques");
		lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTableTitle.setForeground(new Color(44, 62, 80));
		lblTableTitle.setBounds(20, 15, 400, 30);
		tablePanel.add(lblTableTitle);

	    JScrollPane scrollPane = new JScrollPane(table);
	    scrollPane.setBounds(20, 55, 640, 410);
	    scrollPane.setBorder(BorderFactory.createEmptyBorder());
	    scrollPane.getViewport().setBackground(Color.WHITE);
	    tablePanel.add(scrollPane);

	    // Service métier
	    gestion = new GestionBoutique();

	    // Charger les boutiques
	    refreshTable();

	    // Boutons avec style moderne
	    JButton btnCreer = createSidebarButton("➕ Créer Boutique", new Color(46, 204, 113));
	    btnCreer.setBounds(10, 20, 160, 50);
	    sidebarPanel.add(btnCreer);

	    JButton btnSupprimer = createSidebarButton("🗑️ Supprimer", new Color(231, 76, 60));
	    btnSupprimer.setBounds(10, 80, 160, 50);
	    sidebarPanel.add(btnSupprimer);
	    
	    JButton btnRefresh = createSidebarButton("🔄 Actualiser", new Color(127, 140, 141));
	    btnRefresh.setBounds(10, 140, 160, 50);
	    sidebarPanel.add(btnRefresh);

	    
	    
	    //-------------------------------------------------------------------------
	    // Action: créer boutique
	    btnCreer.addActionListener(e -> {
	        Window owner = SwingUtilities.getWindowAncestor(this);
	        JDialog dialog = new JDialog(owner, "Nouvelle Boutique", Dialog.ModalityType.APPLICATION_MODAL);
	        dialog.setSize(500, 250);
	        dialog.getContentPane().setBackground(new Color(240, 248, 255));
	        dialog.setLocationRelativeTo(this);
	        dialog.setLayout(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        gbc.insets = new Insets(15, 15, 15, 15);
	        gbc.fill = GridBagConstraints.HORIZONTAL;

	        JLabel lblDialogTitle = new JLabel("✨ Créer une Nouvelle Boutique");
	        lblDialogTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
	        lblDialogTitle.setForeground(new Color(44, 62, 80));
	        gbc.gridx = 0; gbc.gridy = 0;
	        gbc.gridwidth = 2;
	        gbc.anchor = GridBagConstraints.CENTER;
	        dialog.add(lblDialogTitle, gbc);
	        
	        gbc.gridwidth = 1;
	        gbc.anchor = GridBagConstraints.WEST;

	        JLabel lblNom = new JLabel("Nom:");
	        lblNom.setFont(new Font("Segoe UI", Font.PLAIN, 12));
	        lblNom.setForeground(new Color(127, 140, 141));
	        JTextField nomField = new JTextField(25);
	        nomField.setPreferredSize(new Dimension(300, 35));
	        nomField.setBorder(BorderFactory.createCompoundBorder(
	        	BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
	        	BorderFactory.createEmptyBorder(8, 12, 8, 12)
	        ));
	        
	        JLabel lblAdresse = new JLabel("Adresse:");
	        lblAdresse.setFont(new Font("Segoe UI", Font.PLAIN, 12));
	        lblAdresse.setForeground(new Color(127, 140, 141));
	        JTextField adresseField = new JTextField(25);
	        adresseField.setPreferredSize(new Dimension(300, 35));
	        adresseField.setBorder(BorderFactory.createCompoundBorder(
	        	BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
	        	BorderFactory.createEmptyBorder(8, 12, 8, 12)
	        ));
	        
	        JButton createBtn = createModernButton("✅ Créer", new Color(46, 204, 113), new Color(39, 174, 96));
	        createBtn.setPreferredSize(new Dimension(300, 40));

	        gbc.gridx = 0; gbc.gridy = 1;
	        dialog.add(lblNom, gbc);
	        gbc.gridx = 1;
	        dialog.add(nomField, gbc);
	        
	        gbc.gridx = 0; gbc.gridy = 2;
	        dialog.add(lblAdresse, gbc);
	        gbc.gridx = 1;
	        dialog.add(adresseField, gbc);
	        
	        gbc.gridx = 0; gbc.gridy = 3;
	        gbc.gridwidth = 2;
	        gbc.anchor = GridBagConstraints.CENTER;
	        gbc.insets = new Insets(20, 15, 15, 15);
	        dialog.add(createBtn, gbc);

	        createBtn.addActionListener(ev -> {
	            String nom = nomField.getText().trim();
	            String adresse = adresseField.getText().trim();

	            if (!nom.isEmpty() && !adresse.isEmpty()) {
	                BoutiqueDAO nouvelleBoutique = new BoutiqueDAO();
	                nouvelleBoutique.setNom(nom);
	                nouvelleBoutique.setAdresse(adresse);
	                // Associer la boutique au propriétaire connecté
	                if (user instanceof dao.Proprietaire) {
	                    nouvelleBoutique.setProprietaire((dao.Proprietaire) user);
	                }

	                gestion.ajouterBoutique(nouvelleBoutique);
	                refreshTable();
	                dialog.dispose();
	            } else {
	                JOptionPane.showMessageDialog(dialog, "Tous les champs sont obligatoires.");
	            }
	        });

	        dialog.setVisible(true);
	    });

	    // Action: refresh
	    btnRefresh.addActionListener(e -> refreshTable());
	    
	    // Action: retour
	    btnRetourHeader.addActionListener(e -> mainWindow.showOwnerPanel(user));
	    
	    
	    // Action: supprimer boutique
	    
	    btnSupprimer.addActionListener(e -> {
	        int selectedRow = table.getSelectedRow();
	        if (selectedRow >= 0) {
	            int boutiqueId = (int) model.getValueAt(selectedRow, 0);
	            //je supprime la boutique en passant BoutiqueDAO pas l'id mais l'objet
	            
	            
	            gestion.supprimerBoutique(boutiqueId);
	            
	            
	            refreshTable();
	        } else {
	            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une boutique à supprimer.");
	        }
	    });
	    
	    
	    // clique sur une boutique et affiche liste des reparateurs de cette boutique dans un popup
	    
	 // Détection du double-clic sur une ligne
	    table.addMouseListener(new java.awt.event.MouseAdapter() {
	        @Override
	        public void mouseClicked(java.awt.event.MouseEvent e) {
	            if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
	                int row = table.getSelectedRow();
	                Object idValue = model.getValueAt(row, 0);
	                int boutiqueId = Integer.parseInt(String.valueOf(idValue));

	                GestionBoutique gb = new GestionBoutique();
	                @SuppressWarnings("unchecked")
	                java.util.List<dao.ReparateurDAO> reparateurs = (java.util.List<dao.ReparateurDAO>) gb.ListReparateurBoutique(boutiqueId);

	                StringBuilder sb = new StringBuilder("Réparateurs de la boutique " + boutiqueId + ":\n\n");
	                if (reparateurs != null && !reparateurs.isEmpty()) {
	                    for (dao.ReparateurDAO r : reparateurs) {
	                        sb.append("- ").append(r.getUsername())
	                          .append(" (Pourcentage: ").append(r.getPourcentage()).append("%)\n");
	                    }
	                } else {
	                    sb.append("Aucun réparateur trouvé.");
	                }

	                JOptionPane.showMessageDialog(table, sb.toString(), "Réparateurs", JOptionPane.INFORMATION_MESSAGE);
	            }
	        }
	    });


	   
	    
	    
	    
	    
	    
	    
	}
	
	
	private void refreshTable() {
	    model.setRowCount(0);
	    // Filtrer les boutiques par le propriétaire connecté
	    if (user instanceof dao.Proprietaire) {
	    	boutiques = gestion.afficherBoutiquesParProprietaire((dao.Proprietaire) user);
	    } else {
	    	boutiques = gestion.afficherBoutique();
	    }
	    if (boutiques != null) {
	        for (BoutiqueDAO b : boutiques) {
	            model.addRow(new Object[]{b.getId(), b.getNom(), b.getAdresse()});
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
