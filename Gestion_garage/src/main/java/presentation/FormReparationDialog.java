package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import dao.*;
import metier.GestionReparation;
import exceptions.GestionException;

public class FormReparationDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtClient;
	private JTextField txtTelephone;
	private JTextArea txtDescription;
	private JTextField txtAvance;
	private JTextField txtCoutTotal;
	private JLabel lblReste;
	private JLabel lblPhoto;
	private JButton btnCapturerPhoto;
	private String photoPath;
	private GestionReparation gestion;
	private Reparation reparationExistante;
	private Reparation reparationEnCours; // Pour gérer la réparation créée
	private UserDAO user;
	private MainWindow mainWindow;
	private List<Appareil> appareilsAjoutes; // Liste temporaire des appareils ajoutés
	private DefaultListModel<String> listModelAppareils;
	private JList<String> listeAppareils;

	public FormReparationDialog(MainWindow mainWindow, UserDAO user, Reparation reparation) {
		super(mainWindow, reparation == null ? "✨ Créer une Réparation" : "✏️ Modifier la Réparation", true);
		
		// Permettre aux réparateurs ET aux propriétaires (quand ils sont en mode réparateur)
		// Les propriétaires peuvent aussi créer des réparations
		
		this.mainWindow = mainWindow;
		this.user = user;
		this.reparationExistante = reparation;
		this.gestion = new GestionReparation();
		this.appareilsAjoutes = new ArrayList<>();
		
		setSize(750, 900);
		setLocationRelativeTo(mainWindow);
		getContentPane().setBackground(new Color(240, 248, 255));
		
		// Panel principal avec scroll
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBackground(new Color(240, 248, 255));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		
		JScrollPane scrollPane = new JScrollPane(mainPanel);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(new Color(240, 248, 255));
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		
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
		headerPanel.setPreferredSize(new Dimension(750, 80));
		headerPanel.setMaximumSize(new Dimension(750, 80));
		
		JLabel lblTitle = new JLabel(reparation == null ? "✨ Créer une Nouvelle Réparation" : "✏️ Modifier la Réparation");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setBounds(20, 20, 500, 40);
		headerPanel.add(lblTitle);
		
		mainPanel.add(headerPanel);
		mainPanel.add(Box.createVerticalStrut(20));
		
		// Panel de contenu avec style
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setOpaque(false);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		
		// Section Informations Client
		JPanel sectionClient = createSectionPanel("👤 Informations Client");
		
		JPanel clientRow = createFormRow("Nom du Client:", txtClient = createStyledTextField());
		sectionClient.add(clientRow);
		sectionClient.add(Box.createVerticalStrut(15));
		
		JPanel phoneRow = createFormRow("Téléphone:", txtTelephone = createStyledTextField());
		sectionClient.add(phoneRow);
		sectionClient.add(Box.createVerticalStrut(15));
		
		// Photo Client
		JPanel photoRow = new JPanel(new BorderLayout(10, 10));
		photoRow.setOpaque(false);
		JLabel lblPhotoLabel = new JLabel("📷 Photo du Client:");
		lblPhotoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblPhotoLabel.setForeground(new Color(127, 140, 141));
		photoRow.add(lblPhotoLabel, BorderLayout.NORTH);
		
		JPanel photoContainer = new JPanel(new BorderLayout(10, 10));
		photoContainer.setOpaque(false);
		lblPhoto = new JLabel("Aucune photo");
		lblPhoto.setPreferredSize(new Dimension(180, 180));
		lblPhoto.setMinimumSize(new Dimension(180, 180));
		lblPhoto.setMaximumSize(new Dimension(180, 180));
		lblPhoto.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));
		lblPhoto.setHorizontalAlignment(SwingConstants.CENTER);
		lblPhoto.setOpaque(true);
		lblPhoto.setBackground(Color.WHITE);
		photoContainer.add(lblPhoto, BorderLayout.CENTER);
		
		btnCapturerPhoto = createModernButton("📸 Capturer/Charger Photo", new Color(155, 89, 182), new Color(142, 68, 173));
		btnCapturerPhoto.setPreferredSize(new Dimension(200, 40));
		photoContainer.add(btnCapturerPhoto, BorderLayout.SOUTH);
		photoRow.add(photoContainer, BorderLayout.CENTER);
		sectionClient.add(photoRow);
		
		contentPanel.add(sectionClient);
		contentPanel.add(Box.createVerticalStrut(20));
		
		// Section Description
		JPanel sectionDesc = createSectionPanel("📝 Description de la Réparation");
		JPanel descRow = new JPanel(new BorderLayout(10, 10));
		descRow.setOpaque(false);
		JLabel lblDesc = new JLabel("Description:");
		lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblDesc.setForeground(new Color(127, 140, 141));
		descRow.add(lblDesc, BorderLayout.NORTH);
		txtDescription = new JTextArea(4, 30);
		txtDescription.setLineWrap(true);
		txtDescription.setWrapStyleWord(true);
		txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtDescription.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
			BorderFactory.createEmptyBorder(10, 12, 10, 12)
		));
		JScrollPane scrollDesc = new JScrollPane(txtDescription);
		scrollDesc.setBorder(BorderFactory.createEmptyBorder());
		descRow.add(scrollDesc, BorderLayout.CENTER);
		sectionDesc.add(descRow);
		contentPanel.add(sectionDesc);
		contentPanel.add(Box.createVerticalStrut(20));
		
		// === SECTION PAIEMENT ===
		JPanel sectionPaiement = createSectionPanel("💳 Informations de Paiement");
		
		JPanel coutRow = createFormRow("Coût Total (DH):", txtCoutTotal = createStyledTextField());
		txtCoutTotal.setText("0.0");
		txtCoutTotal.addActionListener(e -> calculerReste());
		sectionPaiement.add(coutRow);
		sectionPaiement.add(Box.createVerticalStrut(15));
		
		JPanel avanceRow = createFormRow("Avance Payé (DH):", txtAvance = createStyledTextField());
		txtAvance.setText("0.0");
		txtAvance.addActionListener(e -> calculerReste());
		sectionPaiement.add(avanceRow);
		sectionPaiement.add(Box.createVerticalStrut(15));
		
		// Reste à payer (affichage spécial)
		JPanel resteRow = new JPanel(new BorderLayout(10, 10));
		resteRow.setOpaque(false);
		JLabel lblResteLabel = new JLabel("Reste à Payer (DH):");
		lblResteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblResteLabel.setForeground(new Color(127, 140, 141));
		resteRow.add(lblResteLabel, BorderLayout.WEST);
		lblReste = new JLabel("0.0");
		lblReste.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblReste.setForeground(new Color(231, 76, 60));
		lblReste.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(231, 76, 60), 2),
			BorderFactory.createEmptyBorder(10, 15, 10, 15)
		));
		lblReste.setOpaque(true);
		lblReste.setBackground(new Color(255, 245, 245));
		lblReste.setHorizontalAlignment(SwingConstants.CENTER);
		resteRow.add(lblReste, BorderLayout.CENTER);
		sectionPaiement.add(resteRow);
		
		contentPanel.add(sectionPaiement);
		contentPanel.add(Box.createVerticalStrut(20));
		
		// Si modification, pré-remplir les champs
		if (reparation != null) {
			txtClient.setText(reparation.getClient());
			txtTelephone.setText(reparation.getTelephone());
			txtDescription.setText(reparation.getDescription());
			txtCoutTotal.setText(reparation.getCoutTotal() != null ? reparation.getCoutTotal().toString() : "0.0");
			txtAvance.setText(reparation.getAvance() != null ? reparation.getAvance().toString() : "0.0");
			calculerReste();
			reparationEnCours = reparation;
			photoPath = reparation.getPhotoPath();
			
			// Charger la photo si elle existe
			if (photoPath != null && !photoPath.isEmpty()) {
				chargerPhoto(photoPath);
			}
			
			// Charger les appareils existants
			if (reparation.getAppareils() != null) {
				appareilsAjoutes.addAll(reparation.getAppareils());
			}
		}
		
		// Action pour le bouton de capture photo
		btnCapturerPhoto.addActionListener(e -> capturerPhoto());
		
		// === SECTION APPAREILS ===
		JPanel sectionAppareils = createSectionPanel("📱 Appareils de cette Réparation");
		
		// Liste des appareils ajoutés
		listModelAppareils = new DefaultListModel<>();
		listeAppareils = new JList<>(listModelAppareils);
		listeAppareils.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		listeAppareils.setSelectionBackground(new Color(155, 89, 182, 100));
		listeAppareils.setSelectionForeground(new Color(44, 62, 80));
		JScrollPane scrollAppareils = new JScrollPane(listeAppareils);
		scrollAppareils.setPreferredSize(new Dimension(700, 150));
		scrollAppareils.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));
		scrollAppareils.getViewport().setBackground(Color.WHITE);
		sectionAppareils.add(scrollAppareils);
		sectionAppareils.add(Box.createVerticalStrut(15));
		
		// Boutons de gestion des appareils
		JPanel panelBoutonsAppareils = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		panelBoutonsAppareils.setOpaque(false);
		
		JButton btnCreerAppareil = createModernButton("➕ Créer Nouvel Appareil", new Color(46, 204, 113), new Color(39, 174, 96));
		btnCreerAppareil.setPreferredSize(new Dimension(200, 40));
		panelBoutonsAppareils.add(btnCreerAppareil);
		
		JButton btnAjouterExistant = createModernButton("📥 Ajouter Existant", new Color(52, 152, 219), new Color(41, 128, 185));
		btnAjouterExistant.setPreferredSize(new Dimension(180, 40));
		panelBoutonsAppareils.add(btnAjouterExistant);
		
		JButton btnRetirerAppareil = createModernButton("➖ Retirer", new Color(231, 76, 60), new Color(192, 57, 43));
		btnRetirerAppareil.setPreferredSize(new Dimension(150, 40));
		panelBoutonsAppareils.add(btnRetirerAppareil);
		
		sectionAppareils.add(panelBoutonsAppareils);
		contentPanel.add(sectionAppareils);
		contentPanel.add(Box.createVerticalStrut(20));
		
		// Actualiser la liste
		actualiserListeAppareils();
		
		// Boutons OK / Annuler
		JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
		panelBoutons.setOpaque(false);
		JButton btnOK = createModernButton("✅ Enregistrer", new Color(46, 204, 113), new Color(39, 174, 96));
		btnOK.setPreferredSize(new Dimension(180, 45));
		JButton btnAnnuler = createModernButton("❌ Annuler", new Color(127, 140, 141), new Color(108, 117, 125));
		btnAnnuler.setPreferredSize(new Dimension(180, 45));
		panelBoutons.add(btnOK);
		panelBoutons.add(btnAnnuler);
		contentPanel.add(panelBoutons);
		
		mainPanel.add(contentPanel);
		
		// ========== ACTIONS ==========
		
		btnOK.addActionListener(e -> {
			if (validerChamps()) {
				enregistrer();
			}
		});
		
		btnAnnuler.addActionListener(e -> dispose());
		
		// NOUVEAU : Créer appareil et l'ajouter automatiquement
		btnCreerAppareil.addActionListener(e -> {
			FormAppareilDialog dialogAppareil = new FormAppareilDialog(mainWindow, null);
			dialogAppareil.setVisible(true);
			
			// Récupérer l'appareil créé directement depuis le dialog
			Appareil appareilCree = dialogAppareil.getAppareilCree();
			
			// Si l'appareil a été créé (pas annulé)
			if (appareilCree != null) {
				// Vérifier s'il n'est pas déjà dans la liste
				boolean dejaAjoute = appareilsAjoutes.stream()
					.anyMatch(a -> a.getImei().equals(appareilCree.getImei()));
				
				if (!dejaAjoute) {
					appareilsAjoutes.add(appareilCree);
					actualiserListeAppareils();
					JOptionPane.showMessageDialog(this, "Appareil cree et ajoute a la reparation!");
				} else {
					JOptionPane.showMessageDialog(this, "Cet appareil est deja dans la liste");
				}
			}
			// Si appareilCree == null, l'utilisateur a annulé, donc on ne fait rien
		});
		
		// Ajouter un appareil existant
		btnAjouterExistant.addActionListener(e -> {
			ajouterAppareilExistant();
		});
		
		// Retirer un appareil
		btnRetirerAppareil.addActionListener(e -> {
			int index = listeAppareils.getSelectedIndex();
			if (index != -1) {
				appareilsAjoutes.remove(index);
				actualiserListeAppareils();
			} else {
				JOptionPane.showMessageDialog(this, "Selectionnez un appareil a retirer");
			}
		});
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
	
	private void calculerReste() {
		try {
			Double coutTotal = parseDoubleLocalized(txtCoutTotal.getText());
			Double avance = parseDoubleLocalized(txtAvance.getText());
			Double reste = Math.max(0.0, coutTotal - avance);
			lblReste.setText(String.format("%.2f", reste));
		} catch (NumberFormatException e) {
			lblReste.setText("0.0");
		}
	}
	
	private void capturerPhoto() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Selectionner ou Capturer une Photo");
		fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) return true;
				String name = f.getName().toLowerCase();
				return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif");
			}
			@Override
			public String getDescription() {
				return "Images (*.jpg, *.jpeg, *.png, *.gif)";
			}
		});
		
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			try {
				// Créer le dossier photos s'il n'existe pas
				File photosDir = new File("photos_clients");
				if (!photosDir.exists()) {
					photosDir.mkdirs();
				}
				
				// Copier la photo dans le dossier photos_clients avec un nom unique
				String fileName = "client_" + System.currentTimeMillis() + "_" + selectedFile.getName();
				Path destination = Paths.get(photosDir.getAbsolutePath(), fileName);
				Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
				
				photoPath = destination.toString();
				chargerPhoto(photoPath);
				
				JOptionPane.showMessageDialog(this, "Photo chargee avec succes!", "Success", JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Erreur lors du chargement de la photo: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void chargerPhoto(String path) {
		try {
			File file = new File(path);
			if (file.exists()) {
				BufferedImage image = ImageIO.read(file);
				if (image != null) {
					ImageIcon icon = new ImageIcon(image.getScaledInstance(150, 150, Image.SCALE_SMOOTH));
					lblPhoto.setIcon(icon);
					lblPhoto.setText("");
				} else {
					lblPhoto.setIcon(null);
					lblPhoto.setText("Photo invalide");
				}
			} else {
				lblPhoto.setIcon(null);
				lblPhoto.setText("Photo non trouvee");
			}
		} catch (IOException ex) {
			lblPhoto.setIcon(null);
			lblPhoto.setText("Erreur chargement");
		}
	}
	
	private void actualiserListeAppareils() {
		listModelAppareils.clear();
		for (Appareil a : appareilsAjoutes) {
			listModelAppareils.addElement(a.getImei() + " - " + a.getMarque() + " " + a.getModele());
		}
	}
	
	private void ajouterAppareilExistant() {
		List<Appareil> disponibles = gestion.listerAppareilsDisponibles();
		
		if (disponibles.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Aucun appareil disponible pour l'instant", "Info", JOptionPane.INFORMATION_MESSAGE);
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
			String imei = choix.split(" - ")[0];
			Appareil appareilSelectionne = disponibles.stream()
				.filter(a -> a.getImei().equals(imei))
				.findFirst()
				.orElse(null);
			
			if (appareilSelectionne != null) {
				// Vérifier s'il n'est pas déjà ajouté
				boolean dejaAjoute = appareilsAjoutes.stream()
					.anyMatch(a -> a.getImei().equals(appareilSelectionne.getImei()));
				
				if (!dejaAjoute) {
					appareilsAjoutes.add(appareilSelectionne);
					actualiserListeAppareils();
				} else {
					JOptionPane.showMessageDialog(this, "Cet appareil est deja dans la liste");
				}
			}
		}
	}
	
	private boolean validerChamps() {
		if (txtClient.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Le nom du client est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (txtDescription.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "La description est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		try {
			parseDoubleLocalized(txtCoutTotal.getText());
			parseDoubleLocalized(txtAvance.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Le cout total et l'avance doivent etre des nombres valides", "Erreur", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	
	private void enregistrer() {
		try {
			if (reparationExistante == null) {
				// Création
				Reparation nouvelle = new Reparation();
				nouvelle.setClient(txtClient.getText().trim());
				nouvelle.setTelephone(txtTelephone.getText().trim());
				nouvelle.setDescription(txtDescription.getText().trim());
				nouvelle.setCoutTotal(parseDoubleLocalized(txtCoutTotal.getText()));
				nouvelle.setAvance(parseDoubleLocalized(txtAvance.getText()));
				calculerReste();
				nouvelle.setReste(parseDoubleLocalized(lblReste.getText()));
				nouvelle.setPhotoPath(photoPath);
				nouvelle.setReparateur(user);
				nouvelle.setAppareils(new ArrayList<>());
				
				Reparation repCreee = gestion.creerReparation(nouvelle, user);
				
				// Ajouter les appareils à la réparation
				for (Appareil app : appareilsAjoutes) {
					gestion.ajouterAppareilAReparation(repCreee.getId(), app.getId());
				}
				
				JOptionPane.showMessageDialog(this, "Reparation creee avec " + appareilsAjoutes.size() + " appareil(s)!");
			} else {
				// Modification
				Reparation modif = new Reparation();
				modif.setClient(txtClient.getText().trim());
				modif.setTelephone(txtTelephone.getText().trim());
				modif.setDescription(txtDescription.getText().trim());
				modif.setCoutTotal(parseDoubleLocalized(txtCoutTotal.getText()));
				modif.setAvance(parseDoubleLocalized(txtAvance.getText()));
				calculerReste();
				modif.setReste(parseDoubleLocalized(lblReste.getText()));
				if (photoPath != null && !photoPath.isEmpty()) {
					modif.setPhotoPath(photoPath);
				}
				
				gestion.modifierReparation(reparationExistante.getId(), modif);
				
				// Gérer les appareils (ajouter nouveaux, retirer absents)
				List<Appareil> appareilsActuels = reparationExistante.getAppareils();
				
				// Ajouter les nouveaux
				for (Appareil app : appareilsAjoutes) {
					boolean existe = appareilsActuels.stream()
						.anyMatch(a -> a.getId().equals(app.getId()));
					if (!existe) {
						gestion.ajouterAppareilAReparation(reparationExistante.getId(), app.getId());
					}
				}
				
				// Retirer ceux qui ne sont plus dans la liste
				for (Appareil app : appareilsActuels) {
					boolean present = appareilsAjoutes.stream()
						.anyMatch(a -> a.getId().equals(app.getId()));
					if (!present) {
						gestion.retirerAppareilDeReparation(reparationExistante.getId(), app.getId());
					}
				}
				
				JOptionPane.showMessageDialog(this, "Reparation modifiee avec succes!");
			}
			dispose();
		} catch (GestionException ex) {
			JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	// Méthodes utilitaires pour créer des composants stylisés
	private JPanel createSectionPanel(String title) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
				title,
				0, 0,
				new Font("Segoe UI", Font.BOLD, 14),
				new Color(44, 62, 80)
			),
			BorderFactory.createEmptyBorder(15, 15, 15, 15)
		));
		panel.setBackground(Color.WHITE);
		panel.setOpaque(true);
		return panel;
	}
	
	private JPanel createFormRow(String labelText, JComponent component) {
		JPanel row = new JPanel(new BorderLayout(10, 10));
		row.setOpaque(false);
		JLabel label = new JLabel(labelText);
		label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		label.setForeground(new Color(127, 140, 141));
		row.add(label, BorderLayout.WEST);
		row.add(component, BorderLayout.CENTER);
		return row;
	}
	
	private JTextField createStyledTextField() {
		JTextField field = new JTextField();
		field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		field.setPreferredSize(new Dimension(300, 40));
		field.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
			BorderFactory.createEmptyBorder(10, 12, 10, 12)
		));
		return field;
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