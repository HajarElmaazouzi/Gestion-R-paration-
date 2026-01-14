package presentation;

import javax.swing.*;
import java.awt.*;
import dao.Reparation;
import metier.GestionReparation;
import exceptions.GestionException;

public class FormTerminerDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextArea txtPieces;
	private JTextField txtCout;
	private JTextField txtAvance;
	private JTextField txtReste;
	private Reparation reparation;
	private GestionReparation gestion;

	public FormTerminerDialog(MainWindow mainWindow, Reparation reparation, GestionReparation gestion) {
		super(mainWindow, "Terminer Reparation: " + reparation.getNumero(), true);
		this.reparation = reparation;
		this.gestion = gestion;
		
		setSize(650, 700);
		setLocationRelativeTo(mainWindow);
		getContentPane().setBackground(new Color(240, 248, 255));
		setLayout(new BorderLayout());
		
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
		headerPanel.setPreferredSize(new Dimension(650, 90));
		
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
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		JScrollPane scrollPane = new JScrollPane(mainPanel);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(new Color(240, 248, 255));
		add(scrollPane, BorderLayout.CENTER);
		
		// ========== SECTION DESCRIPTION ==========
		JPanel sectionDesc = createSectionPanel("📝 Description de la Réparation");
		JTextArea txtDescriptionActuelle = new JTextArea(3, 30);
		txtDescriptionActuelle.setText(reparation.getDescription());
		txtDescriptionActuelle.setEditable(false);
		txtDescriptionActuelle.setLineWrap(true);
		txtDescriptionActuelle.setWrapStyleWord(true);
		txtDescriptionActuelle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtDescriptionActuelle.setBackground(new Color(250, 250, 250));
		txtDescriptionActuelle.setForeground(new Color(44, 62, 80));
		txtDescriptionActuelle.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
			BorderFactory.createEmptyBorder(10, 10, 10, 10)
		));
		JScrollPane scrollDesc = new JScrollPane(txtDescriptionActuelle);
		scrollDesc.setBorder(BorderFactory.createEmptyBorder());
		scrollDesc.setPreferredSize(new Dimension(600, 80));
		sectionDesc.add(scrollDesc);
		mainPanel.add(sectionDesc);
		mainPanel.add(Box.createVerticalStrut(15));
		
		// ========== SECTION INFORMATIONS DE PAIEMENT ==========
		JPanel sectionPaiement = createSectionPanel("💰 Informations de Paiement");
		
		// Coût total
		JPanel panelCout = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		panelCout.setOpaque(false);
		panelCout.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		boolean coutDefini = reparation.getCoutTotal() != null && reparation.getCoutTotal() > 0;
		JLabel lblCout = coutDefini ? 
			new JLabel("Cout total (DH):") : 
			createRequiredLabel("Cout total (DH): *");
		lblCout.setPreferredSize(new Dimension(180, 30));
		panelCout.add(lblCout);
		
		txtCout = new JTextField(20);
		txtCout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		if (coutDefini) {
			txtCout.setText(String.format("%.2f", reparation.getCoutTotal()));
			txtCout.setEditable(false);
			txtCout.setBackground(new Color(240, 240, 240));
			txtCout.setForeground(new Color(127, 140, 141));
		} else {
			txtCout.setText("");
			txtCout.setEditable(true);
			txtCout.setBackground(Color.WHITE);
		}
		txtCout.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
			BorderFactory.createEmptyBorder(8, 12, 8, 12)
		));
		panelCout.add(txtCout);
		sectionPaiement.add(panelCout);
		sectionPaiement.add(Box.createVerticalStrut(10));
		
		// Avance payée
		JPanel panelAvance = createFieldPanel("Avance payee (DH):", false);
		txtAvance = new JTextField(20);
		txtAvance.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		if (reparation.getAvance() != null && reparation.getAvance() > 0) {
			txtAvance.setText(String.format("%.2f", reparation.getAvance()));
		} else {
			txtAvance.setText("0.00");
		}
		txtAvance.setEditable(false);
		txtAvance.setBackground(new Color(240, 240, 240));
		txtAvance.setForeground(new Color(127, 140, 141));
		txtAvance.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
			BorderFactory.createEmptyBorder(8, 12, 8, 12)
		));
		panelAvance.add(txtAvance);
		sectionPaiement.add(panelAvance);
		sectionPaiement.add(Box.createVerticalStrut(10));
		
		// Reste à payer
		JPanel panelReste = createFieldPanel("Reste a payer (DH):", false);
		txtReste = new JTextField(20);
		txtReste.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
		txtReste.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
			BorderFactory.createEmptyBorder(8, 12, 8, 12)
		));
		panelReste.add(txtReste);
		sectionPaiement.add(panelReste);
		
		mainPanel.add(sectionPaiement);
		mainPanel.add(Box.createVerticalStrut(15));
		
		// ========== SECTION PIÈCES UTILISÉES ==========
		JPanel sectionPieces = createSectionPanel("🔧 Pièces Utilisées");
		txtPieces = new JTextArea(5, 30);
		if (reparation.getPiecesUtilisees() != null && !reparation.getPiecesUtilisees().trim().isEmpty()) {
			txtPieces.setText(reparation.getPiecesUtilisees());
		}
		txtPieces.setLineWrap(true);
		txtPieces.setWrapStyleWord(true);
		txtPieces.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtPieces.setBackground(Color.WHITE);
		txtPieces.setForeground(new Color(44, 62, 80));
		txtPieces.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
			BorderFactory.createEmptyBorder(10, 10, 10, 10)
		));
		JScrollPane scrollPieces = new JScrollPane(txtPieces);
		scrollPieces.setBorder(BorderFactory.createEmptyBorder());
		scrollPieces.setPreferredSize(new Dimension(600, 120));
		sectionPieces.add(scrollPieces);
		mainPanel.add(sectionPieces);
		
		// ========== BOUTONS ==========
		JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
		panelBoutons.setOpaque(false);
		panelBoutons.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		
		JButton btnTerminer = createModernButton("✅ Terminer", new Color(46, 204, 113), new Color(39, 174, 96));
		btnTerminer.setPreferredSize(new Dimension(180, 45));
		JButton btnAnnuler = createModernButton("❌ Annuler", new Color(127, 140, 141), new Color(108, 117, 125));
		btnAnnuler.setPreferredSize(new Dimension(180, 45));
		
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
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panel.setPreferredSize(new Dimension(600, 0));
		panel.setMaximumSize(new Dimension(600, Short.MAX_VALUE));
		
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
		label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		label.setForeground(new Color(44, 62, 80));
		label.setPreferredSize(new Dimension(180, 30));
		panel.add(label);
		
		return panel;
	}
	
	private JLabel createRequiredLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("Segoe UI", Font.BOLD, 13));
		label.setForeground(new Color(231, 76, 60));
		label.setPreferredSize(new Dimension(180, 30));
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
			String pieces = txtPieces.getText().trim();
			Double cout = parseDoubleLocalized(txtCout.getText());
			
			gestion.terminerReparation(reparation.getId(), reparation.getDescription(), pieces, cout);
			JOptionPane.showMessageDialog(this, "Reparation terminee avec succes!", "Succès", JOptionPane.INFORMATION_MESSAGE);
			dispose();
		} catch (GestionException ex) {
			JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}
}
