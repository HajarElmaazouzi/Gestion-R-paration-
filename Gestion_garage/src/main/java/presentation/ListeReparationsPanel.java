package presentation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import dao.*;
import metier.GestionReparation;
import exceptions.GestionException;

public class ListeReparationsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel model;
	private GestionReparation gestion;
	private UserDAO user;
	private MainWindow mainWindow;
	private boolean voirToutes;
	private JCheckBox chkVoirToutes;

	public ListeReparationsPanel(UserDAO user, MainWindow mainWindow, boolean voirToutes) {
		this.user = user;
		this.mainWindow = mainWindow;
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
		headerPanel.setBounds(0, 0, 900, 80);
		add(headerPanel);
		
		// Déterminer le type d'utilisateur une seule fois
		boolean isReparateur = user instanceof dao.ReparateurDAO;
		
		// Titre différent selon le type d'utilisateur
		String title = isReparateur ? "🔧 Mes Réparations" : "👁️ Suivi des Réparations";
		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setBounds(20, 15, 400, 35);
		headerPanel.add(lblTitle);
		
		// Checkbox "Voir toutes" (seulement pour propriétaire)
		if (!isReparateur) {
			chkVoirToutes = new JCheckBox("👁️ Voir toutes les réparations");
			chkVoirToutes.setSelected(voirToutes);
			chkVoirToutes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			chkVoirToutes.setForeground(new Color(255, 255, 255, 220));
			chkVoirToutes.setOpaque(false);
			chkVoirToutes.setBounds(20, 50, 250, 20);
			headerPanel.add(chkVoirToutes);
			
			chkVoirToutes.addActionListener(e -> {
				this.voirToutes = chkVoirToutes.isSelected();
				refreshTable();
			});
		}
		
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
		// Seul le réparateur peut créer/modifier/supprimer des réparations
		JButton btnCreer = createSidebarButton("➕ Créer Réparation", new Color(46, 204, 113));
		btnCreer.setBounds(10, 20, 160, 50);
		btnCreer.setVisible(isReparateur); // Seulement visible pour les réparateurs
		sidebarPanel.add(btnCreer);
		
		JButton btnModifier = createSidebarButton("✏️ Modifier", new Color(52, 152, 219));
		btnModifier.setBounds(10, 80, 160, 50);
		btnModifier.setVisible(isReparateur); // Seulement visible pour les réparateurs
		sidebarPanel.add(btnModifier);
		
		JButton btnSupprimer = createSidebarButton("🗑️ Supprimer", new Color(231, 76, 60));
		btnSupprimer.setBounds(10, 140, 160, 50);
		btnSupprimer.setVisible(isReparateur); // Seulement visible pour les réparateurs
		sidebarPanel.add(btnSupprimer);
		
		JButton btnRefresh = createSidebarButton("🔄 Actualiser", new Color(127, 140, 141));
		// Le bouton Actualiser est positionné différemment selon le type d'utilisateur
		if (isReparateur) {
			btnRefresh.setBounds(10, 200, 160, 50);
		} else {
			// Pour le propriétaire, le bouton Actualiser est en haut
			btnRefresh.setBounds(10, 20, 160, 50);
		}
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
		tablePanel.setBounds(200, 100, 820, 480); // Largeur augmentée pour le tableau
		add(tablePanel);
		
		JLabel lblTableTitle = new JLabel("📊 Liste des Réparations");
		lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTableTitle.setForeground(new Color(44, 62, 80));
		lblTableTitle.setBounds(20, 15, 400, 30);
		tablePanel.add(lblTableTitle);
		
		// Tableau
		String[] columnNames = {"Numero", "Client", "Telephone", "Etat Réparation", "Coût Total (DH)", "Date Creation", "Actions"};
		model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 6; // Seule la colonne Actions est cliquable
			}
		};
		
		table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		table.setRowHeight(35); // Hauteur réduite pour un tableau plus simple
		table.setSelectionBackground(new Color(155, 89, 182, 100));
		table.setSelectionForeground(new Color(44, 62, 80));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Désactiver le redimensionnement automatique
		
		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 13));
		header.setBackground(new Color(155, 89, 182));
		header.setForeground(Color.WHITE);
		header.setPreferredSize(new Dimension(header.getWidth(), 40));
		
		// Configurer les largeurs des colonnes pour afficher tout le texte
		table.getColumnModel().getColumn(0).setPreferredWidth(120); // Numero
		table.getColumnModel().getColumn(1).setPreferredWidth(150); // Client
		table.getColumnModel().getColumn(2).setPreferredWidth(130); // Telephone
		table.getColumnModel().getColumn(3).setPreferredWidth(120); // Etat
		table.getColumnModel().getColumn(4).setPreferredWidth(140); // Coût Total
		table.getColumnModel().getColumn(5).setPreferredWidth(150); // Date Creation
		table.getColumnModel().getColumn(6).setPreferredWidth(120); // Actions
		
		// Renderer personnalisé pour la colonne Coût Total (afficher en vert si > 0)
		table.getColumnModel().getColumn(4).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (value != null) {
					String str = value.toString();
					if (str.contains("DH")) {
						String numStr = str.replace(" DH", "").trim();
						try {
							double cout = Double.parseDouble(numStr);
							if (cout > 0) {
								c.setForeground(new Color(46, 204, 113)); // Vert pour les montants > 0
								((JLabel) c).setFont(new Font("Segoe UI", Font.BOLD, 12));
							} else {
								c.setForeground(new Color(127, 140, 141)); // Gris pour 0
							}
						} catch (NumberFormatException e) {
							// Ignore
						}
					}
				}
				return c;
			}
		});
		
		// Renderer personnalisé pour afficher tout le texte sans troncature
		table.setDefaultRenderer(String.class, new javax.swing.table.DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (c instanceof JLabel) {
					JLabel label = (JLabel) c;
					label.setToolTipText(value != null ? value.toString() : ""); // Tooltip avec texte complet
					// Ne pas tronquer le texte
					label.setText(value != null ? value.toString() : "");
				}
				return c;
			}
		});
		
		table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
		table.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox()));
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(20, 55, 780, 410); // Largeur augmentée pour afficher toutes les colonnes
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Scroll horizontal si nécessaire
		tablePanel.add(scrollPane);
		
		// Menu contextuel pour copier le numéro
		JPopupMenu contextMenu = new JPopupMenu();
		JMenuItem copyItem = new JMenuItem("📋 Copier le numéro");
		copyItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		copyItem.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row != -1) {
				String numero = (String) model.getValueAt(row, 0);
				java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(numero);
				java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
				JOptionPane.showMessageDialog(this, "Numéro copié: " + numero, "Copié", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		contextMenu.add(copyItem);
		
		// Double-clic pour copier le numéro
		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = table.rowAtPoint(e.getPoint());
					int col = table.columnAtPoint(e.getPoint());
					if (row != -1 && col == 0) { // Colonne Numero
						String numero = (String) model.getValueAt(row, 0);
						java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(numero);
						java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
						JOptionPane.showMessageDialog(table, "Numéro copié: " + numero, "✅ Copié", JOptionPane.INFORMATION_MESSAGE);
					}
				} else if (e.isPopupTrigger() || (e.getButton() == java.awt.event.MouseEvent.BUTTON3)) {
					int row = table.rowAtPoint(e.getPoint());
					int col = table.columnAtPoint(e.getPoint());
					if (row != -1 && col == 0) { // Colonne Numero
						table.setRowSelectionInterval(row, row);
						contextMenu.show(table, e.getX(), e.getY());
					}
				}
			}
			
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger()) {
					int row = table.rowAtPoint(e.getPoint());
					int col = table.columnAtPoint(e.getPoint());
					if (row != -1 && col == 0) {
						table.setRowSelectionInterval(row, row);
						contextMenu.show(table, e.getX(), e.getY());
					}
				}
			}
			
			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger()) {
					int row = table.rowAtPoint(e.getPoint());
					int col = table.columnAtPoint(e.getPoint());
					if (row != -1 && col == 0) {
						table.setRowSelectionInterval(row, row);
						contextMenu.show(table, e.getX(), e.getY());
					}
				}
			}
		});
		
		// Charger les données
		refreshTable();
		
		// ========== ACTIONS ==========
		
		btnCreer.addActionListener(e -> {
			FormReparationDialog dialog = new FormReparationDialog(mainWindow, user, null);
			dialog.setVisible(true);
			refreshTable();
		});
		
		btnModifier.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row != -1) {
				Reparation rep = (Reparation) model.getValueAt(row, 5);
				if (rep.getEtat() == EtatReparation.OUVERT) {
					FormReparationDialog dialog = new FormReparationDialog(mainWindow, user, rep);
					dialog.setVisible(true);
					refreshTable();
				} else {
					JOptionPane.showMessageDialog(this, "Seules les reparations ouvertes peuvent etre modifiees", "Info", JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Selectionnez une reparation a modifier");
			}
		});
		
		btnSupprimer.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row != -1) {
				Reparation rep = (Reparation) model.getValueAt(row, 5);
				if (rep.getEtat() == EtatReparation.OUVERT) {
					int confirm = JOptionPane.showConfirmDialog(this, 
						"Voulez-vous vraiment supprimer cette reparation?", 
						"Confirmation", JOptionPane.YES_NO_OPTION);
					
					if (confirm == JOptionPane.YES_OPTION) {
						try {
							gestion.supprimerReparation(rep.getId());
							JOptionPane.showMessageDialog(this, "Reparation supprimee avec succes!");
							refreshTable();
						} catch (GestionException ex) {
							JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
						}
					}
				} else {
					JOptionPane.showMessageDialog(this, "Seules les reparations ouvertes peuvent etre supprimees", "Info", JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Selectionnez une reparation a supprimer");
			}
		});
		
		btnRefresh.addActionListener(e -> refreshTable());
		
		btnRetourHeader.addActionListener(e -> {
			if (user instanceof dao.ReparateurDAO) {
				mainWindow.showReparateurPanel((dao.ReparateurDAO) user);
			} else {
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
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return button;
	}
	
	private JButton createMenuButton(String text) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				
				Color bgColor = new Color(155, 89, 182);
				Color hoverColor = new Color(142, 68, 173);
				
				Color currentBg;
				if (getModel().isPressed()) {
					currentBg = hoverColor.darker();
				} else if (getModel().isRollover()) {
					currentBg = hoverColor;
				} else {
					currentBg = bgColor;
				}
				
				// Fond avec coins arrondis
				g2d.setColor(currentBg);
				g2d.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 5, 5);
				
				// Ombre légère
				g2d.setColor(new Color(0, 0, 0, 20));
				g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 5, 5);
				
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		button.setFont(new Font("Segoe UI", Font.BOLD, 11));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setPreferredSize(new Dimension(120, 32));
		button.setMinimumSize(new Dimension(120, 32));
		button.setMaximumSize(new Dimension(120, 32));
		button.setMargin(new Insets(6, 12, 6, 12));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return button;
	}
	
	private void refreshTable() {
		model.setRowCount(0);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		List<Reparation> reparations;
		if (voirToutes && !(user instanceof dao.ReparateurDAO)) {
			reparations = gestion.listerToutesLesReparations();
		} else {
			reparations = gestion.listerMesReparations(user);
		}
		
		System.out.println("📊 Chargement de " + reparations.size() + " réparations pour affichage");
		
		for (Reparation r : reparations) {
			// Formater le coût total - DEBUG
			String coutTotalStr = "0.00";
			if (r.getCoutTotal() != null) {
				System.out.println("💰 Réparation " + r.getNumero() + " - Coût total: " + r.getCoutTotal() + " DH");
				if (r.getCoutTotal() > 0) {
					coutTotalStr = String.format("%.2f", r.getCoutTotal());
				}
			} else {
				System.out.println("⚠️ Réparation " + r.getNumero() + " - Coût total est NULL");
			}
			
			Object[] row = {
				r.getNumero(),
				r.getClient(),
				r.getTelephone(),
				r.getEtat().toString(),
				coutTotalStr + " DH",
				sdf.format(r.getDateCreation()),
				r // On passe l'objet complet pour la colonne Actions
			};
			model.addRow(row);
		}
	}
	
	// ========== RENDERER ET EDITOR POUR COLONNE ACTIONS ==========
	
	class ButtonRenderer extends JPanel implements TableCellRenderer {
		public ButtonRenderer() {
			setLayout(new BorderLayout());
			setOpaque(true);
			setBackground(Color.WHITE);
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			removeAll();
			setBackground(isSelected ? new Color(155, 89, 182, 30) : Color.WHITE);
			
			if (value instanceof Reparation) {
				JButton btnActions = createMenuButton("Actions ▼");
				add(btnActions, BorderLayout.CENTER);
			}
			
			return this;
		}
	}
	
	class ButtonEditor extends DefaultCellEditor {
		private JPanel panel;
		private Reparation currentReparation;
		private JButton btnActions;
		
		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			panel = new JPanel(new BorderLayout());
			panel.setOpaque(true);
			panel.setBackground(Color.WHITE);
			panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		}
		
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {
			panel.removeAll();
			panel.setBackground(isSelected ? new Color(155, 89, 182, 30) : Color.WHITE);
			
			if (value instanceof Reparation) {
				// Utiliser la réparation depuis le modèle (qui vient de refreshTable avec données fraîches)
				currentReparation = (Reparation) value;
				
				btnActions = createMenuButton("Actions ▼");
				btnActions.addActionListener(e -> showActionsMenu(btnActions));
				panel.add(btnActions, BorderLayout.CENTER);
			}
			
			return panel;
		}
		
		@Override
		public Object getCellEditorValue() {
			return currentReparation;
		}
		
		private void showActionsMenu(JButton button) {
			// Recharger la réparation depuis la base de données pour avoir l'état à jour
			if (currentReparation != null && currentReparation.getId() != null) {
				try {
					Reparation reparationFraiche = gestion.rechercherReparationParId(currentReparation.getId());
					if (reparationFraiche != null) {
						currentReparation = reparationFraiche;
					}
				} catch (Exception e) {
					System.err.println("⚠️ Erreur lors du rechargement de la réparation: " + e.getMessage());
				}
			}
			
			JPopupMenu menu = new JPopupMenu();
			menu.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)
			));
			
			// Seul le réparateur peut effectuer des actions sur les réparations
			// Utiliser la variable de la portée externe (déclarée dans le constructeur)
			boolean isReparateurUser = user instanceof dao.ReparateurDAO;
			
			// Actions selon l'état (seulement pour les réparateurs)
			if (isReparateurUser && currentReparation != null) {
				switch (currentReparation.getEtat()) {
					case OUVERT:
						JMenuItem itemDemarrer = createMenuItem("▶ Démarrer la Réparation", new Color(46, 204, 113));
						itemDemarrer.addActionListener(e -> {
							demarrerReparation();
							menu.setVisible(false);
						});
						menu.add(itemDemarrer);
						break;
						
					case EN_COURS:
						JMenuItem itemTerminer = createMenuItem("✅ Terminer la Réparation", new Color(52, 152, 219));
						itemTerminer.addActionListener(e -> {
							terminerReparation();
							menu.setVisible(false);
						});
						menu.add(itemTerminer);
						
						JMenuItem itemAnnuler = createMenuItem("❌ Annuler la Réparation", new Color(231, 76, 60));
						itemAnnuler.addActionListener(e -> {
							annulerReparation();
							menu.setVisible(false);
						});
						menu.add(itemAnnuler);
						break;
						
					case TERMINE:
					case ANNULE:
						// Pas d'actions pour les réparations terminées/annulées
						break;
				}
				
				// Séparateur
				if (menu.getComponentCount() > 0) {
					menu.addSeparator();
				}
			}
			
			// Bouton Appareils toujours disponible (lecture seule pour propriétaire)
			JMenuItem itemAppareils = createMenuItem("📱 Gérer les Appareils", new Color(155, 89, 182));
			itemAppareils.addActionListener(e -> {
				gererAppareils();
				menu.setVisible(false);
			});
			menu.add(itemAppareils);
			
			// Afficher le menu sous le bouton
			menu.show(button, 0, button.getHeight());
		}
		
		private JMenuItem createMenuItem(String text, Color iconColor) {
			JMenuItem item = new JMenuItem(text);
			item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
			item.setPreferredSize(new Dimension(200, 35));
			item.setCursor(new Cursor(Cursor.HAND_CURSOR));
			
			// Style au survol
			item.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseEntered(java.awt.event.MouseEvent e) {
					item.setBackground(new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 20));
				}
				
				@Override
				public void mouseExited(java.awt.event.MouseEvent e) {
					item.setBackground(Color.WHITE);
				}
			});
			
			return item;
		}
		
		private void demarrerReparation() {
			try {
				gestion.demarrerReparation(currentReparation.getId());
				JOptionPane.showMessageDialog(panel, "Réparation démarrée avec succès!");
				fireEditingStopped();
				refreshTable();
			} catch (GestionException ex) {
				JOptionPane.showMessageDialog(panel, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		private void terminerReparation() {
			fireEditingStopped();
			FormTerminerDialog dialog = new FormTerminerDialog(mainWindow, currentReparation, gestion);
			dialog.setVisible(true);
			// Attendre un peu pour s'assurer que la transaction est commitée
			try {
				Thread.sleep(200); // Augmenter le délai pour s'assurer que la transaction est commitée
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
			// Forcer le rechargement des données depuis la base
			gestion = new GestionReparation(); // Créer une nouvelle instance pour forcer le rechargement
			refreshTable();
		}
		
		private void annulerReparation() {
			String raison = JOptionPane.showInputDialog(panel, "Raison de l'annulation:");
			if (raison != null && !raison.trim().isEmpty()) {
				try {
					gestion.annulerReparation(currentReparation.getId(), raison);
					JOptionPane.showMessageDialog(panel, "Réparation annulée avec succès!");
					fireEditingStopped();
					// Attendre un peu pour s'assurer que la transaction est commitée
					try {
						Thread.sleep(100);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
					}
					refreshTable();
				} catch (GestionException ex) {
					JOptionPane.showMessageDialog(panel, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		private void gererAppareils() {
			fireEditingStopped();
			mainWindow.showAppareilsReparation(user, currentReparation);
		}
	}
}