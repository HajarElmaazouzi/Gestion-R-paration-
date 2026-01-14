package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import dao.*;
import metier.GestionReparation;

public class ClientTrackingPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField txtCodeUnique;
	private JLabel lblInfo;
	private JTextArea txtDetails;
	private JLabel lblPhoto;
	private GestionReparation gestion;
	private MainWindow mainWindow;

	public ClientTrackingPanel(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		this.gestion = new GestionReparation();
		
		setLayout(null);
		setBackground(new Color(240, 248, 255));
		
		// Fond avec gradient
		setBackground(new Color(240, 248, 255));
		
		// Titre avec style moderne
		JLabel lblTitre = new JLabel("📱 Suivi de Réparation");
		lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 32));
		lblTitre.setForeground(new Color(44, 62, 80));
		lblTitre.setBounds(300, 30, 500, 40);
		add(lblTitre);
		
		JLabel lblSousTitre = new JLabel("Entrez votre code unique pour suivre l'état de votre réparation");
		lblSousTitre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblSousTitre.setForeground(new Color(127, 140, 141));
		lblSousTitre.setBounds(250, 75, 600, 25);
		add(lblSousTitre);
		
		// Panel de recherche avec style moderne
		JPanel searchPanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(Color.WHITE);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
				
				// Ombre
				g2d.setColor(new Color(0, 0, 0, 10));
				for (int i = 0; i < 3; i++) {
					g2d.drawRoundRect(i, i, getWidth() - 2*i, getHeight() - 2*i, 15, 15);
				}
				g2d.dispose();
			}
		};
		searchPanel.setOpaque(false);
		searchPanel.setBounds(200, 120, 500, 100);
		add(searchPanel);
		
		// Section recherche par code unique
		JLabel lblCode = new JLabel("🔑 Code Unique de Réparation:");
		lblCode.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblCode.setForeground(new Color(44, 62, 80));
		lblCode.setBounds(20, 15, 300, 25);
		searchPanel.add(lblCode);
		
		txtCodeUnique = new JTextField(20);
		txtCodeUnique.setBounds(20, 45, 350, 40);
		txtCodeUnique.setFont(new Font("Segoe UI", Font.BOLD, 16));
		txtCodeUnique.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
			BorderFactory.createEmptyBorder(10, 15, 10, 15)
		));
		txtCodeUnique.setBackground(new Color(250, 250, 250));
		searchPanel.add(txtCodeUnique);
		
		JButton btnRechercher = createModernButton("🔍 Rechercher", new Color(155, 89, 182), new Color(142, 68, 173));
		btnRechercher.setBounds(380, 45, 100, 40);
		searchPanel.add(btnRechercher);
		
		// Label pour les messages avec style moderne
		lblInfo = new JLabel("");
		lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblInfo.setForeground(new Color(231, 76, 60));
		lblInfo.setBounds(200, 230, 500, 30);
		lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblInfo);
		
		// Panel pour afficher les informations avec style moderne
		JPanel infoPanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(Color.WHITE);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				
				// Ombre
				g2d.setColor(new Color(0, 0, 0, 15));
				for (int i = 0; i < 5; i++) {
					g2d.drawRoundRect(i, i, getWidth() - 2*i, getHeight() - 2*i, 20, 20);
				}
				g2d.dispose();
			}
		};
		infoPanel.setOpaque(false);
		infoPanel.setBounds(200, 280, 700, 380);
		add(infoPanel);
		
		// Titre du panel d'info
		JLabel lblInfoTitle = new JLabel("📋 Informations de la Réparation");
		lblInfoTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblInfoTitle.setForeground(new Color(44, 62, 80));
		lblInfoTitle.setBounds(20, 15, 400, 30);
		infoPanel.add(lblInfoTitle);
		
		// Zone de texte pour les détails avec style
		txtDetails = new JTextArea();
		txtDetails.setEditable(false);
		txtDetails.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtDetails.setBackground(new Color(250, 250, 250));
		txtDetails.setForeground(new Color(44, 62, 80));
		txtDetails.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
			BorderFactory.createEmptyBorder(15, 15, 15, 15)
		));
		txtDetails.setBounds(20, 55, 450, 200);
		txtDetails.setLineWrap(true);
		txtDetails.setWrapStyleWord(true);
		infoPanel.add(txtDetails);
		
		// Label pour la photo avec style
		JLabel lblPhotoTitle = new JLabel("📷 Photo Client:");
		lblPhotoTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblPhotoTitle.setForeground(new Color(44, 62, 80));
		lblPhotoTitle.setBounds(490, 55, 180, 25);
		infoPanel.add(lblPhotoTitle);
		
		lblPhoto = new JLabel("Aucune photo disponible");
		lblPhoto.setBounds(490, 85, 180, 170);
		lblPhoto.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));
		lblPhoto.setHorizontalAlignment(SwingConstants.CENTER);
		lblPhoto.setVerticalAlignment(SwingConstants.CENTER);
		lblPhoto.setBackground(new Color(250, 250, 250));
		lblPhoto.setOpaque(true);
		lblPhoto.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblPhoto.setForeground(new Color(127, 140, 141));
		infoPanel.add(lblPhoto);
		
		// Bouton retour avec style moderne
		JButton btnRetour = createModernButton("← Retour", new Color(127, 140, 141), new Color(108, 117, 125));
		btnRetour.setBounds(200, 680, 120, 40);
		add(btnRetour);
		
		// Actions
		btnRechercher.addActionListener(e -> rechercherReparation());
		txtCodeUnique.addActionListener(e -> rechercherReparation());
		
		btnRetour.addActionListener(e -> {
			mainWindow.showLoginPanel();
		});
	}
	
	private void rechercherReparation() {
		String codeUnique = txtCodeUnique.getText().trim();
		
		if (codeUnique.isEmpty()) {
			lblInfo.setText("⚠️ Veuillez entrer un code unique!");
			lblInfo.setForeground(new Color(231, 76, 60));
			txtDetails.setText("");
			lblPhoto.setIcon(null);
			lblPhoto.setText("Aucune photo disponible");
			lblPhoto.setForeground(new Color(127, 140, 141));
			return;
		}
		
		try {
			Reparation reparation = gestion.rechercherReparationParNumero(codeUnique);
			
			if (reparation == null) {
				lblInfo.setText("❌ Aucune réparation trouvée avec ce code unique!");
				lblInfo.setForeground(new Color(231, 76, 60));
				txtDetails.setText("");
				lblPhoto.setIcon(null);
				lblPhoto.setText("Aucune photo disponible");
				lblPhoto.setForeground(new Color(127, 140, 141));
				return;
			}
			
			// Afficher les informations avec style
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			StringBuilder details = new StringBuilder();
			details.append("🔑 Code Unique: ").append(reparation.getNumero()).append("\n\n");
			details.append("👤 Client: ").append(reparation.getClient()).append("\n");
			details.append("📞 Téléphone: ").append(reparation.getTelephone() != null ? reparation.getTelephone() : "N/A").append("\n\n");
			details.append("📝 Description:\n").append(reparation.getDescription()).append("\n\n");
			
			// État de la réparation avec icône selon le statut
			String etatIcon = "";
			String etatText = "";
			switch(reparation.getEtat().toString()) {
				case "OUVERT": 
					etatIcon = "🟡"; 
					etatText = "OUVERT (En attente de traitement)";
					break;
				case "EN_COURS": 
					etatIcon = "🔵"; 
					etatText = "EN COURS (En réparation)";
					break;
				case "TERMINE": 
					etatIcon = "✅"; 
					etatText = "TERMINE (Réparation terminée)";
					break;
				case "ANNULE": 
					etatIcon = "❌"; 
					etatText = "ANNULE (Réparation annulée)";
					break;
				default:
					etatText = reparation.getEtat().toString();
			}
			details.append(etatIcon).append(" État de la Réparation: ").append(etatText).append("\n");
			details.append("📅 Date Création: ").append(sdf.format(reparation.getDateCreation())).append("\n");
			
			if (reparation.getDateFin() != null) {
				details.append("🏁 Date Fin: ").append(sdf.format(reparation.getDateFin())).append("\n");
			}
			
			if (reparation.getCoutTotal() != null && reparation.getCoutTotal() > 0) {
				details.append("\n💰 Informations de Paiement:\n");
				details.append("   • Coût Total: ").append(String.format("%.2f", reparation.getCoutTotal())).append(" DH\n");
				if (reparation.getAvance() != null && reparation.getAvance() > 0) {
					details.append("   • Avance: ").append(String.format("%.2f", reparation.getAvance())).append(" DH\n");
				}
				if (reparation.getReste() != null && reparation.getReste() > 0) {
					details.append("   • Reste à Payer: ").append(String.format("%.2f", reparation.getReste())).append(" DH\n");
				}
			}
			
			if (reparation.getPiecesUtilisees() != null && !reparation.getPiecesUtilisees().isEmpty()) {
				details.append("\n🔧 Pièces Utilisées:\n").append(reparation.getPiecesUtilisees());
			}
			
			txtDetails.setText(details.toString());
			
			// Charger la photo si elle existe
			if (reparation.getPhotoPath() != null && !reparation.getPhotoPath().isEmpty()) {
				chargerPhoto(reparation.getPhotoPath());
			} else {
				lblPhoto.setIcon(null);
				lblPhoto.setText("<html><center>Aucune photo<br>disponible</html>");
				lblPhoto.setForeground(new Color(127, 140, 141));
			}
			
			lblInfo.setText("✅ Réparation trouvée avec succès!");
			lblInfo.setForeground(new Color(46, 204, 113));
			
		} catch (Exception ex) {
			lblInfo.setText("❌ Erreur lors de la recherche: " + ex.getMessage());
			lblInfo.setForeground(new Color(231, 76, 60));
			txtDetails.setText("");
			lblPhoto.setIcon(null);
			lblPhoto.setText("Aucune photo disponible");
			lblPhoto.setForeground(new Color(127, 140, 141));
		}
	}
	
	private void chargerPhoto(String path) {
		try {
			File file = new File(path);
			if (file.exists()) {
				BufferedImage image = ImageIO.read(file);
				if (image != null) {
					ImageIcon icon = new ImageIcon(image.getScaledInstance(170, 170, Image.SCALE_SMOOTH));
					lblPhoto.setIcon(icon);
					lblPhoto.setText("");
					lblPhoto.setForeground(null);
				} else {
					lblPhoto.setIcon(null);
					lblPhoto.setText("<html><center>Photo<br>invalide</html>");
					lblPhoto.setForeground(new Color(231, 76, 60));
				}
			} else {
				lblPhoto.setIcon(null);
				lblPhoto.setText("<html><center>Photo non<br>trouvée</html>");
				lblPhoto.setForeground(new Color(231, 76, 60));
			}
		} catch (Exception ex) {
			lblPhoto.setIcon(null);
			lblPhoto.setText("<html><center>Erreur<br>chargement</html>");
			lblPhoto.setForeground(new Color(231, 76, 60));
		}
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
		
		button.setFont(new Font("Segoe UI", Font.BOLD, 13));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		return button;
	}
}
