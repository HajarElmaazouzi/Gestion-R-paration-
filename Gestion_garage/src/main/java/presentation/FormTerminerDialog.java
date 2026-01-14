package presentation;

import javax.swing.*;
import java.awt.*;
import dao.Reparation;
import metier.GestionReparation;
import exceptions.GestionException;

public class FormTerminerDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtCout;
	private JTextField txtAvance;
	private JTextField txtReste;
	private Reparation reparation;
	private GestionReparation gestion;

	public FormTerminerDialog(MainWindow mainWindow, Reparation reparation, GestionReparation gestion) {
		super(mainWindow, "Terminer Reparation: " + reparation.getNumero(), true);
		this.reparation = reparation;
		this.gestion = gestion;
		
		setSize(700, 600);
		setLocationRelativeTo(mainWindow);
		getContentPane().setBackground(new Color(240, 248, 255));
		setLayout(new BorderLayout());
		setResizable(false);
		
		// ========== HEADER AVEC GRADIENT ==========
		JPanel headerPanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				GradientPaint gp = new GradientPaint(0, 0, new Color(46, 204, 113), 0, getHeight(), new Color(39, 174, 96));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
			}
		};
		headerPanel.setOpaque(false);
		headerPanel.setPreferredSize(new Dimension(700, 90));
		
		JLabel lblTitle = new JLabel("✅ Terminer Réparation");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setBounds(20, 15, 400, 35);
		headerPanel.add(lblTitle);
		
		JLabel lblNumero = new JLabel("Numéro: " + reparation.getNumero());
		lblNumero.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNumero.setForeground(new Color(255, 255, 255, 220));
		lblNumero.setBounds(20, 50, 300, 25);
		headerPanel.add(lblNumero);
		
		add(headerPanel, BorderLayout.NORTH);
		
		// ========== CONTENU PRINCIPAL ==========
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBackground(new Color(240, 248, 255));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
		mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(mainPanel, BorderLayout.CENTER);
		
		// ========== SECTION DESCRIPTION ==========
		JLabel lblDescTitle = new JLabel("📝 Description de la Réparation");
		lblDescTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lblDescTitle.setForeground(new Color(44, 62, 80));
		lblDescTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(lblDescTitle);
		mainPanel.add(Box.createVerticalStrut(10));
		
		JTextArea txtDescriptionActuelle = new JTextArea(6, 50);
		txtDescriptionActuelle.setText(reparation.getDescription());
		txtDescriptionActuelle.setEditable(false);
		txtDescriptionActuelle.setLineWrap(true);
		txtDescriptionActuelle.setWrapStyleWord(true);
		txtDescriptionActuelle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		txtDescriptionActuelle.setBackground(new Color(240, 248, 255));
		txtDescriptionActuelle.setForeground(new Color(44, 62, 80));
		txtDescriptionActuelle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		txtDescriptionActuelle.setPreferredSize(new Dimension(640, 150));
		txtDescriptionActuelle.setMaximumSize(new Dimension(640, 150));
		txtDescriptionActuelle.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(txtDescriptionActuelle);
		mainPanel.add(Box.createVerticalStrut(30));
		
		// ========== SECTION INFORMATIONS DE PAIEMENT ==========
		JLabel lblPaiementTitle = new JLabel("💰 Informations de Paiement");
		lblPaiementTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lblPaiementTitle.setForeground(new Color(44, 62, 80));
		lblPaiementTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(lblPaiementTitle);
		mainPanel.add(Box.createVerticalStrut(15));
		
		// Coût total
		JLabel lblCout = new JLabel("Coût total (DH):");
		boolean coutDefini = reparation.getCoutTotal() != null && reparation.getCoutTotal() > 0;
		if (!coutDefini) {
			lblCout.setText("Coût total (DH): *");
			lblCout.setFont(new Font("Segoe UI", Font.BOLD, 15));
			lblCout.setForeground(new Color(231, 76, 60));
		} else {
			lblCout.setFont(new Font("Segoe UI", Font.PLAIN, 15));
			lblCout.setForeground(new Color(44, 62, 80));
		}
		lblCout.setPreferredSize(new Dimension(220, 50));
		lblCout.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(lblCout);
		mainPanel.add(Box.createVerticalStrut(5));
		
		txtCout = new JTextField();
		txtCout.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		txtCout.setPreferredSize(new Dimension(400, 50));
		txtCout.setMaximumSize(new Dimension(400, 50));
		if (coutDefini) {
			txtCout.setText(String.format("%.2f", reparation.getCoutTotal()));
			txtCout.setEditable(false);
			txtCout.setBackground(new Color(240, 248, 255));
			txtCout.setForeground(new Color(44, 62, 80));
		} else {
			txtCout.setText("");
			txtCout.setEditable(true);
			txtCout.setBackground(Color.WHITE);
			txtCout.setForeground(new Color(44, 62, 80));
		}
		txtCout.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		txtCout.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(txtCout);
		mainPanel.add(Box.createVerticalStrut(20));
		
		// Avance payée
		JLabel lblAvance = new JLabel("Avance payée (DH):");
		lblAvance.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblAvance.setForeground(new Color(44, 62, 80));
		lblAvance.setPreferredSize(new Dimension(220, 50));
		lblAvance.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(lblAvance);
		mainPanel.add(Box.createVerticalStrut(5));
		
		txtAvance = new JTextField();
		txtAvance.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		txtAvance.setPreferredSize(new Dimension(400, 50));
		txtAvance.setMaximumSize(new Dimension(400, 50));
		if (reparation.getAvance() != null && reparation.getAvance() > 0) {
			txtAvance.setText(String.format("%.2f", reparation.getAvance()));
		} else {
			txtAvance.setText("0.00");
		}
		txtAvance.setEditable(false);
		txtAvance.setBackground(new Color(240, 248, 255));
		txtAvance.setForeground(new Color(44, 62, 80));
		txtAvance.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		txtAvance.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(txtAvance);
		mainPanel.add(Box.createVerticalStrut(20));
		
		// Reste à payer
		JLabel lblReste = new JLabel("Reste à payer (DH):");
		lblReste.setFont(new Font("Segoe UI", Font.BOLD, 15));
		lblReste.setForeground(new Color(44, 62, 80));
		lblReste.setPreferredSize(new Dimension(220, 50));
		lblReste.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(lblReste);
		mainPanel.add(Box.createVerticalStrut(5));
		
		txtReste = new JTextField();
		txtReste.setFont(new Font("Segoe UI", Font.BOLD, 18));
		txtReste.setPreferredSize(new Dimension(400, 55));
		txtReste.setMaximumSize(new Dimension(400, 55));
		if (reparation.getReste() != null) {
			txtReste.setText(String.format("%.2f", reparation.getReste()));
		} else {
			if (reparation.getCoutTotal() != null && reparation.getAvance() != null) {
				double reste = Math.max(0.0, reparation.getCoutTotal() - reparation.getAvance());
				txtReste.setText(String.format("%.2f", reste));
			} else {
				txtReste.setText("0.00");
			}
		}
		txtReste.setEditable(false);
		txtReste.setBackground(new Color(52, 152, 219));
		txtReste.setForeground(Color.WHITE);
		txtReste.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		txtReste.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(txtReste);
		mainPanel.add(Box.createVerticalStrut(30));
		
		// ========== BOUTONS ==========
		JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		panelBoutons.setOpaque(false);
		panelBoutons.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		
		JButton btnTerminer = createModernButton("✅ Terminer", new Color(46, 204, 113), new Color(39, 174, 96));
		btnTerminer.setPreferredSize(new Dimension(200, 50));
		JButton btnAnnuler = createModernButton("❌ Annuler", new Color(127, 140, 141), new Color(108, 117, 125));
		btnAnnuler.setPreferredSize(new Dimension(200, 50));
		
		panelBoutons.add(btnTerminer);
		panelBoutons.add(btnAnnuler);
		mainPanel.add(panelBoutons);
		
		// ========== ACTIONS ==========
		btnTerminer.addActionListener(e -> {
			if (validerChamps()) {
				terminer();
			}
		});
		
		btnAnnuler.addActionListener(e -> dispose());
		
		// Mettre à jour le reste à payer si le coût total change
		txtCout.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			public void changedUpdate(javax.swing.event.DocumentEvent e) { updateReste(); }
			public void removeUpdate(javax.swing.event.DocumentEvent e) { updateReste(); }
			public void insertUpdate(javax.swing.event.DocumentEvent e) { updateReste(); }
			private void updateReste() {
				try {
					String coutText = txtCout.getText().trim();
					if (!coutText.isEmpty()) {
						double cout = parseDoubleLocalized(coutText);
						double avance = reparation.getAvance() != null ? reparation.getAvance() : 0.0;
						double reste = Math.max(0.0, cout - avance);
						txtReste.setText(String.format("%.2f", reste));
					} else {
						txtReste.setText("0.00");
					}
				} catch (NumberFormatException ex) {
					txtReste.setText("0.00");
				}
			}
		});
	}
	
	private JPanel createSectionPanel(String title) {
		JPanel panel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(Color.WHITE);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
				g2d.setColor(new Color(0, 0, 0, 10));
				for (int i = 0; i < 3; i++) {
					g2d.drawRoundRect(i, i, getWidth() - 2*i, getHeight() - 2*i, 15, 15);
				}
				g2d.dispose();
			}
		};
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
		panel.setPreferredSize(new Dimension(640, 0));
		panel.setMaximumSize(new Dimension(640, Short.MAX_VALUE));
		
		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTitle.setForeground(new Color(44, 62, 80));
		lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(lblTitle);
		panel.add(Box.createVerticalStrut(15));
		
		return panel;
	}
	
	private JPanel createFieldPanel(String labelText, boolean required) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		panel.setOpaque(false);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JLabel label = new JLabel(labelText);
		label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		label.setForeground(new Color(44, 62, 80));
		label.setPreferredSize(new Dimension(220, 50));
		panel.add(label);
		
		return panel;
	}
	
	private JLabel createRequiredLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("Segoe UI", Font.BOLD, 15));
		label.setForeground(new Color(231, 76, 60));
		label.setPreferredSize(new Dimension(220, 50));
		return label;
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
				
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		
		button.setFont(new Font("Segoe UI", Font.BOLD, 14));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		return button;
	}
	
	private boolean validerChamps() {
		String coutText = txtCout.getText().trim();
		if (coutText.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Le cout total est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
			txtCout.requestFocus();
			return false;
		}
		
		try {
			Double cout = parseDoubleLocalized(coutText);
			if (cout < 0) {
				JOptionPane.showMessageDialog(this, "Le cout total ne peut pas etre negatif", "Erreur", JOptionPane.ERROR_MESSAGE);
				txtCout.requestFocus();
				return false;
			}
			if (cout == 0) {
				int response = JOptionPane.showConfirmDialog(
					this, 
					"Le cout total est 0.0 DH. Etes-vous sur de vouloir continuer?", 
					"Confirmation", 
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE
				);
				if (response != JOptionPane.YES_OPTION) {
					txtCout.requestFocus();
					return false;
				}
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Le cout doit etre un nombre valide (ex: 150.50)", "Erreur", JOptionPane.ERROR_MESSAGE);
			txtCout.requestFocus();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Parse un nombre en gérant les formats avec virgule (français) et point (anglais)
	 */
	private Double parseDoubleLocalized(String text) {
		if (text == null || text.trim().isEmpty()) {
			return 0.0;
		}
		// Remplacer la virgule par un point pour le parsing
		String normalized = text.trim().replace(',', '.');
		return Double.parseDouble(normalized);
	}
	
	private void terminer() {
		try {
			// Utiliser les pièces existantes de la réparation ou une chaîne vide
			String pieces = (reparation.getPiecesUtilisees() != null) ? reparation.getPiecesUtilisees() : "";
			Double cout = parseDoubleLocalized(txtCout.getText());
			
			gestion.terminerReparation(reparation.getId(), reparation.getDescription(), pieces, cout);
			JOptionPane.showMessageDialog(this, "Réparation terminée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
			dispose();
		} catch (GestionException ex) {
			JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}
}
