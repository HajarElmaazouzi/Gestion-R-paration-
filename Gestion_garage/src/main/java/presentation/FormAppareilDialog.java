package presentation;

import javax.swing.*;
import java.awt.*;
import dao.Appareil;
import dao.EtatAppareil;
import metier.GestionReparation;
import exceptions.GestionException;

public class FormAppareilDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField txtIMEI;
	private JTextField txtMarque;
	private JTextField txtModele;
	private JTextField txtType;
	private GestionReparation gestion;
	private Appareil appareilExistant;
	private Appareil appareilCree; // Appareil créé (null si annulé)

	public FormAppareilDialog(MainWindow mainWindow, Appareil appareil) {
		super(mainWindow, appareil == null ? "Creer Appareil" : "Modifier Appareil", true);
		this.appareilExistant = appareil;
		this.gestion = new GestionReparation();
		
		setSize(450, 350);
		setLocationRelativeTo(mainWindow);
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		// IMEI
		gbc.gridx = 0; gbc.gridy = 0;
		add(new JLabel("IMEI:"), gbc);
		gbc.gridx = 1;
		txtIMEI = new JTextField(20);
		
		// Limiter la saisie à 15 caractères numériques uniquement
		txtIMEI.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt) {
				char c = evt.getKeyChar();
				// Autoriser uniquement les chiffres
				if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
					evt.consume();
				}
				// Limiter à 15 caractères
				if (txtIMEI.getText().length() >= 15 && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
					evt.consume();
				}
			}
		});
		
		add(txtIMEI, gbc);
		
		// Marque
		gbc.gridx = 0; gbc.gridy = 1;
		add(new JLabel("Marque:"), gbc);
		gbc.gridx = 1;
		txtMarque = new JTextField(20);
		add(txtMarque, gbc);
		
		// Modèle
		gbc.gridx = 0; gbc.gridy = 2;
		add(new JLabel("Modele:"), gbc);
		gbc.gridx = 1;
		txtModele = new JTextField(20);
		add(txtModele, gbc);
		
		// Type
		gbc.gridx = 0; gbc.gridy = 3;
		add(new JLabel("Type:"), gbc);
		gbc.gridx = 1;
		txtType = new JTextField(20);
		add(txtType, gbc);
		
		// Si modification, pré-remplir les champs
		if (appareil != null) {
			txtIMEI.setText(appareil.getImei());
			txtMarque.setText(appareil.getMarque());
			txtModele.setText(appareil.getModele());
			txtType.setText(appareil.getTypeAppareil());
			txtIMEI.setEnabled(false); // IMEI non modifiable
		}
		
		// Boutons
		gbc.gridx = 0; gbc.gridy = 4;
		gbc.gridwidth = 2;
		JPanel panelBoutons = new JPanel(new FlowLayout());
		JButton btnOK = new JButton("Enregistrer");
		JButton btnAnnuler = new JButton("Annuler");
		panelBoutons.add(btnOK);
		panelBoutons.add(btnAnnuler);
		add(panelBoutons, gbc);
		
		// ========== ACTIONS ==========
		
		btnOK.addActionListener(e -> {
			if (validerChamps()) {
				enregistrer();
			}
		});
		
		btnAnnuler.addActionListener(e -> dispose());
	}
	
	private boolean validerChamps() {
		if (txtIMEI.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "L'IMEI est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (txtMarque.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "La marque est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (txtModele.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Le modele est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		// Vérifier format IMEI (15 chiffres exactement)
		String imei = txtIMEI.getText().trim();
		
		// Vérifier que c'est bien 15 caractères
		if (imei.length() != 15) {
			JOptionPane.showMessageDialog(this, 
				"L'IMEI doit contenir exactement 15 caracteres (actuellement: " + imei.length() + ")", 
				"Erreur", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		// Vérifier que ce sont bien des chiffres
		if (!imei.matches("\\d{15}")) {
			JOptionPane.showMessageDialog(this, 
				"L'IMEI doit contenir uniquement des chiffres", 
				"Erreur", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return true;
	}
	
	private void enregistrer() {
		try {
			if (appareilExistant == null) {
				// Création
				Appareil nouveau = new Appareil();
				nouveau.setImei(txtIMEI.getText().trim());
				nouveau.setMarque(txtMarque.getText().trim());
				nouveau.setModele(txtModele.getText().trim());
				nouveau.setTypeAppareil(txtType.getText().trim());
				nouveau.setEtat(EtatAppareil.DISPONIBLE);
				
				gestion.ajouterAppareil(nouveau);
				appareilCree = nouveau; // Stocker l'appareil créé
				JOptionPane.showMessageDialog(this, "Appareil cree avec succes!");
			} else {
				// Modification
				appareilExistant.setMarque(txtMarque.getText().trim());
				appareilExistant.setModele(txtModele.getText().trim());
				appareilExistant.setTypeAppareil(txtType.getText().trim());
				
				gestion.modifierAppareil(appareilExistant);
				appareilCree = appareilExistant; // Retourner l'appareil modifié
				JOptionPane.showMessageDialog(this, "Appareil modifie avec succes!");
			}
			dispose();
		} catch (GestionException ex) {
			JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Retourne l'appareil créé/modifié, ou null si le dialog a été annulé
	 */
	public Appareil getAppareilCree() {
		return appareilCree;
	}
}