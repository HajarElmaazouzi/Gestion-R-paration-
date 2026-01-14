package presentation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import dao.BoutiqueDAO;
import dao.CaisseDAO;
import dao.ReparateurDAO;
import dao.UserDAO;
import metier.GestionBoutique;
import metier.GestionCaisses;
import metier.GestionUtilisateur;



public class MesReparateursPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable table;
	
	private DefaultTableModel model;
	private GestionUtilisateur gestion;   // service métier
	private List<ReparateurDAO> reparateurs;
	private UserDAO owner;  // Stocker le propriétaire connecté


	/**
	 * Create the panel.
	 */
	public MesReparateursPanel(UserDAO owner , MainWindow mainWindow) {
		this.owner = owner;  // Stocker le propriétaire
		setLayout(null);
		setBackground(new Color(240, 248, 255));
		gestion = new GestionUtilisateur();
		
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
		
		JLabel lblTitle = new JLabel("👥 Mes Réparateurs");
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
		
		JButton btnNewButton = createSidebarButton("➕ Créer Réparateur", new Color(46, 204, 113));
		btnNewButton.setBounds(10, 20, 160, 50);
		sidebarPanel.add(btnNewButton);
		
		JButton btnNewButton_1 = createSidebarButton("🗑️ Supprimer", new Color(231, 76, 60));
		btnNewButton_1.setBounds(10, 80, 160, 50);
		sidebarPanel.add(btnNewButton_1);
		
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
		
		JLabel lblTableTitle = new JLabel("📊 Liste des Réparateurs");
		lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTableTitle.setForeground(new Color(44, 62, 80));
		lblTableTitle.setBounds(20, 15, 400, 30);
		tablePanel.add(lblTableTitle);
		
		String[] columnNames = {"ID", "Username", "Pourcentage"};
		model = new DefaultTableModel(columnNames, 0) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		        return false;
		    }
		};
		table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		table.setRowHeight(35);
		table.setSelectionBackground(new Color(46, 204, 113, 100));
		table.setSelectionForeground(new Color(44, 62, 80));
		
		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(new Color(46, 204, 113));
		header.setForeground(Color.WHITE);
		header.setPreferredSize(new Dimension(header.getWidth(), 40));

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(20, 55, 640, 410);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(Color.WHITE);
		tablePanel.add(scrollPane);

		
		
		
		///  creer reparateur
		
		
		btnNewButton.addActionListener(e -> {
			JDialog dialog = new JDialog(mainWindow, "Nouveau Réparateur", true);
		    dialog.setSize(550, 400);
		    dialog.getContentPane().setBackground(new Color(240, 248, 255));
		    dialog.setLocationRelativeTo(this);
		    dialog.setLayout(new GridBagLayout());
		    GridBagConstraints gbc = new GridBagConstraints();
		    gbc.insets = new Insets(15, 15, 15, 15);
		    gbc.fill = GridBagConstraints.HORIZONTAL;

		    JLabel lblDialogTitle = new JLabel("✨ Créer un Nouveau Réparateur");
		    lblDialogTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		    lblDialogTitle.setForeground(new Color(44, 62, 80));
		    gbc.gridx = 0; gbc.gridy = 0;
		    gbc.gridwidth = 2;
		    gbc.anchor = GridBagConstraints.CENTER;
		    dialog.add(lblDialogTitle, gbc);
		    
		    gbc.gridwidth = 1;
		    gbc.anchor = GridBagConstraints.WEST;

		    JLabel lblUsername = new JLabel("Nom d'utilisateur:");
		    lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		    lblUsername.setForeground(new Color(127, 140, 141));
		    JTextField usernameField = new JTextField(25);
		    usernameField.setPreferredSize(new Dimension(350, 35));
		    usernameField.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
		    	BorderFactory.createEmptyBorder(8, 12, 8, 12)
		    ));
		    
		    JLabel lblPassword = new JLabel("Mot de passe:");
		    lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		    lblPassword.setForeground(new Color(127, 140, 141));
		    JPasswordField passwordField = new JPasswordField(25);
		    passwordField.setPreferredSize(new Dimension(350, 35));
		    passwordField.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
		    	BorderFactory.createEmptyBorder(8, 12, 8, 12)
		    ));
		    
		    JLabel lblPourcentage = new JLabel("Pourcentage (%):");
		    lblPourcentage.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		    lblPourcentage.setForeground(new Color(127, 140, 141));
		    JTextField pourcentageField = new JTextField(25);
		    pourcentageField.setPreferredSize(new Dimension(350, 35));
		    pourcentageField.setBorder(BorderFactory.createCompoundBorder(
		    	BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
		    	BorderFactory.createEmptyBorder(8, 12, 8, 12)
		    ));
		    
		    JLabel lblBoutique = new JLabel("Boutique:");
		    lblBoutique.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		    lblBoutique.setForeground(new Color(127, 140, 141));
		    JComboBox<BoutiqueDAO> boutiqueCombo = new JComboBox<>();
		    boutiqueCombo.setPreferredSize(new Dimension(350, 35));
		    boutiqueCombo.setRenderer(new DefaultListCellRenderer() {
		        @Override
		        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
		                boolean isSelected, boolean cellHasFocus) {
		            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		            if (value instanceof BoutiqueDAO) {
		                setText(((BoutiqueDAO) value).getNom());
		            }
		            return this;
		        }
		    });
		    // Charger seulement les boutiques du propriétaire connecté
		    GestionBoutique gestionBoutique = new GestionBoutique();
		    List<BoutiqueDAO> boutiques;
		    if (owner instanceof dao.Proprietaire) {
		    	boutiques = gestionBoutique.afficherBoutiquesParProprietaire((dao.Proprietaire) owner);
		    } else {
		    	boutiques = gestionBoutique.afficherBoutique();
		    }
		    for (BoutiqueDAO b : boutiques) {
		        boutiqueCombo.addItem(b);
		    }
		    
		    JButton createBtn = createModernButton("✅ Créer", new Color(46, 204, 113), new Color(39, 174, 96));
		    createBtn.setPreferredSize(new Dimension(350, 40));

		    gbc.gridx = 0; gbc.gridy = 1;
		    dialog.add(lblUsername, gbc);
		    gbc.gridx = 1;
		    dialog.add(usernameField, gbc);

		    gbc.gridx = 0; gbc.gridy = 2;
		    dialog.add(lblPassword, gbc);
		    gbc.gridx = 1;
		    dialog.add(passwordField, gbc);

		    gbc.gridx = 0; gbc.gridy = 3;
		    dialog.add(lblPourcentage, gbc);
		    gbc.gridx = 1;
		    dialog.add(pourcentageField, gbc);

		    gbc.gridx = 0; gbc.gridy = 4;
		    dialog.add(lblBoutique, gbc);
		    gbc.gridx = 1;
		    dialog.add(boutiqueCombo, gbc);

		    gbc.gridx = 0; gbc.gridy = 5;
		    gbc.gridwidth = 2;
		    gbc.anchor = GridBagConstraints.CENTER;
		    gbc.insets = new Insets(20, 15, 15, 15);
		    dialog.add(createBtn, gbc);

		    // Action du bouton "Créer"
		    createBtn.addActionListener(ev -> {
		        String username = usernameField.getText().trim();
		        String password = new String(passwordField.getPassword()).trim();
		        String pourcentageStr = pourcentageField.getText().trim();
		        BoutiqueDAO selectedBoutique = (BoutiqueDAO) boutiqueCombo.getSelectedItem();

		        if (!username.isEmpty() && !password.isEmpty() && !pourcentageStr.isEmpty() && selectedBoutique != null) {
		            try {
		                int pourcentage = Integer.parseInt(pourcentageStr);

		                // Créer la caisse
		                GestionCaisses gestionCaisses = new GestionCaisses();
		                CaisseDAO caisse = gestionCaisses.creerCaisse();
		                if (caisse == null) {
		                    JOptionPane.showMessageDialog(dialog, "Erreur: Impossible de créer la caisse.", "Erreur", JOptionPane.ERROR_MESSAGE);
		                    return;
		                }

		                // Créer le réparateur avec le builder pattern
		                ReparateurDAO nouveau = ReparateurDAO.builder()
		                        .username(username)
		                        .password(password)
		                        .pourcentage(pourcentage)
		                        .boutique(selectedBoutique)
		                        .caisse(caisse)
		                        .build();
		                
		                // Debug: Verify password before creating
		                System.out.println("🔍 UI - Username: '" + username + "', Password: '" + password + "' (length: " + password.length() + ")");
		                System.out.println("🔍 UI - Réparateur avant création - Username: '" + nouveau.getUsername() + "', Password: '" + nouveau.getPassword() + "'");

		                gestion.CreerReparateur(nouveau);
		                
		                // Small delay to ensure transaction is committed
		                try {
		                    Thread.sleep(100);
		                } catch (InterruptedException ie) {
		                    Thread.currentThread().interrupt();
		                }
		                
		                // Verify reparateur was created
		                if (gestion.userExists(username)) {
		                    JOptionPane.showMessageDialog(dialog, "Réparateur créé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
		                    refreshTable();
		                    dialog.dispose();
		                } else {
		                    JOptionPane.showMessageDialog(dialog, "Erreur: La création du réparateur a peut-être échoué.", "Erreur", JOptionPane.ERROR_MESSAGE);
		                }
		            } catch (Exception ex) {
		                if (ex instanceof NumberFormatException) {
		                    JOptionPane.showMessageDialog(dialog, "Pourcentage invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
		                } else {
		                    JOptionPane.showMessageDialog(dialog, "Erreur lors de la création: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		                    ex.printStackTrace();
		                }
		            }
		        } else {
		            JOptionPane.showMessageDialog(dialog, "Tous les champs sont obligatoires.");
		        }
		    });

		    dialog.setVisible(true);
		});



		
		// supprimer reparateur
		
		btnNewButton_1.addActionListener(e -> {
		    int row = table.getSelectedRow();
		    if (row != -1) {
		        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
		        gestion.SupprimerReparateur(id);
		        refreshTable();
		    } else {
		        JOptionPane.showMessageDialog(this, "Sélectionnez un réparateur à supprimer.");
		    }
		});
		
		btnRefresh.addActionListener(e -> refreshTable());
		
		btnRetourHeader.addActionListener(e -> {
			mainWindow.showOwnerPanel(owner);
		});
		
		refreshTable();
	}
	
	private void refreshTable() {
	    model.setRowCount(0);
	    // Filtrer les réparateurs par le propriétaire connecté
	    if (owner instanceof dao.Proprietaire) {
	    	GestionBoutique gestionBoutique = new GestionBoutique();
	    	reparateurs = gestionBoutique.listerReparateursParProprietaire((dao.Proprietaire) owner);
	    } else {
	    	reparateurs = gestion.ListerReparateurs();
	    }
	    if (reparateurs != null) {
	        for (ReparateurDAO r : reparateurs) {
	            model.addRow(new Object[]{r.getId(), r.getUsername(), r.getPourcentage() + "%"});
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
