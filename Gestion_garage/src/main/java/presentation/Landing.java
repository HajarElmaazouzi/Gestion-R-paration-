package presentation;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import dao.Appareil;
import dao.BoutiqueDAO;
import dao.CaisseDAO;
import dao.EtatAppareil;
import dao.EtatReparation;
import dao.Proprietaire;
import dao.ReparateurDAO;
import dao.Reparation;
import dao.UserDAO;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import metier.GestionBoutique;
import metier.GestionCaisses;
import metier.GestionReparation;
import metier.GestionUtilisateur;

public class Landing extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Landing frame = new Landing();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Landing() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(121, 90, 260, 22);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Username : ");
		lblNewLabel.setBounds(30, 90, 81, 19);
		contentPane.add(lblNewLabel);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(121, 143, 260, 22);
		contentPane.add(passwordField);
		
		JLabel lblNewLabel_1 = new JLabel("Password :");
		lblNewLabel_1.setBounds(37, 144, 58, 18);
		contentPane.add(lblNewLabel_1);
		
		JButton btnNewButton = new JButton("Login");
		btnNewButton.setBounds(202, 198, 84, 20);
		contentPane.add(btnNewButton);
		
		// ========== CODE POUR CRÉER RÉPARATIONS STATIQUEMENT ==========
		try {
			// Initialiser les gestionnaires
			GestionBoutique gb = new GestionBoutique();
			GestionCaisses gc = new GestionCaisses();
			GestionUtilisateur gu = new GestionUtilisateur();
			GestionReparation gr = new GestionReparation();
			
			// Créer boutique
			BoutiqueDAO boutique = new BoutiqueDAO();
			gb.ajouterBoutique(boutique);
			
			// Créer caisses
			CaisseDAO caisse1 = gc.creerCaisse();
			CaisseDAO caisse2 = gc.creerCaisse();
			
			// Créer réparateur (mohamed)
			ReparateurDAO reparateur = new ReparateurDAO();
			reparateur.setUsername("mohamed");
			reparateur.setPassword("1234");
			reparateur.setPourcentage(30);
			reparateur.setCaisse(caisse2);
			reparateur.setBoutique(boutique);
			gu.CreerReparateur(reparateur);
			
			// Créer user admin (utiliser Proprietaire car UserDAO est abstrait avec @SuperBuilder)
			Proprietaire admin = Proprietaire.builder()
					.username("admin")
					.password("admin")
					.caisse(caisse1)
					.build();
			gu.CreerUser(admin);
			
			System.out.println("=== CRÉATION DES APPAREILS ===");
			
			// Créer appareils pour mohamed
			Appareil appareil1Mohamed = new Appareil();
			appareil1Mohamed.setImei("111111111111111");
			appareil1Mohamed.setMarque("Samsung");
			appareil1Mohamed.setModele("S21");
			appareil1Mohamed.setTypeAppareil("Smartphone");
			appareil1Mohamed.setEtat(EtatAppareil.DISPONIBLE);
			
			Appareil appareil2Mohamed = new Appareil();
			appareil2Mohamed.setImei("222222222222222");
			appareil2Mohamed.setMarque("Apple");
			appareil2Mohamed.setModele("iPhone 12");
			appareil2Mohamed.setTypeAppareil("Smartphone");
			appareil2Mohamed.setEtat(EtatAppareil.DISPONIBLE);
			
			// Créer appareils pour admin
			Appareil appareil1Admin = new Appareil();
			appareil1Admin.setImei("333333333333333");
			appareil1Admin.setMarque("Xiaomi");
			appareil1Admin.setModele("Redmi Note 10");
			appareil1Admin.setTypeAppareil("Smartphone");
			appareil1Admin.setEtat(EtatAppareil.DISPONIBLE);
			
			Appareil appareil2Admin = new Appareil();
			appareil2Admin.setImei("444444444444444");
			appareil2Admin.setMarque("Huawei");
			appareil2Admin.setModele("P40 Pro");
			appareil2Admin.setTypeAppareil("Smartphone");
			appareil2Admin.setEtat(EtatAppareil.DISPONIBLE);
			
			// Ajouter les appareils à la base
			gr.ajouterAppareil(appareil1Mohamed);
			gr.ajouterAppareil(appareil2Mohamed);
			gr.ajouterAppareil(appareil1Admin);
			gr.ajouterAppareil(appareil2Admin);
			
			System.out.println("✅ 4 appareils créés");
			
			System.out.println("\n=== CRÉATION RÉPARATION POUR MOHAMED ===");
			
			// Créer réparation pour mohamed
			Reparation repMohamed = new Reparation();
			repMohamed.setClient("Client Mohamed");
			repMohamed.setTelephone("0611111111");
			repMohamed.setDescription("Écran cassé + problème de batterie");
			repMohamed.setEtat(EtatReparation.EN_COURS);
			repMohamed.setDateCreation(new Date());
			
			// Ajouter 2 appareils à la réparation de mohamed
			List<Appareil> appareilsMohamed = new ArrayList<>();
			appareilsMohamed.add(appareil1Mohamed);
			appareilsMohamed.add(appareil2Mohamed);
			repMohamed.setAppareils(appareilsMohamed);
			
			// Créer la réparation dans la base
			Reparation repMohamedSaved = gr.creerReparation(repMohamed, reparateur);
			
			System.out.println("✅ Réparation créée pour mohamed:");
			System.out.println("   - Numéro: " + repMohamedSaved.getNumero());
			System.out.println("   - Client: " + repMohamedSaved.getClient());
			System.out.println("   - Appareils: " + repMohamedSaved.getAppareils().size());
			System.out.println("   - État: " + repMohamedSaved.getEtat());
			
			System.out.println("\n=== CRÉATION RÉPARATION POUR ADMIN ===");
			
			// Créer réparation pour admin
			Reparation repAdmin = new Reparation();
			repAdmin.setClient("Client Admin");
			repAdmin.setTelephone("0622222222");
			repAdmin.setDescription("Problème logiciel + changement caméra");
			repAdmin.setEtat(EtatReparation.OUVERT);
			repAdmin.setDateCreation(new Date());
			
			// Ajouter 2 appareils à la réparation d'admin
			List<Appareil> appareilsAdmin = new ArrayList<>();
			appareilsAdmin.add(appareil1Admin);
			appareilsAdmin.add(appareil2Admin);
			repAdmin.setAppareils(appareilsAdmin);
			
			// Créer la réparation dans la base
			Reparation repAdminSaved = gr.creerReparation(repAdmin, admin);
			
			System.out.println("✅ Réparation créée pour admin:");
			System.out.println("   - Numéro: " + repAdminSaved.getNumero());
			System.out.println("   - Client: " + repAdminSaved.getClient());
			System.out.println("   - Appareils: " + repAdminSaved.getAppareils().size());
			System.out.println("   - État: " + repAdminSaved.getEtat());
			
			System.out.println("\n=== VÉRIFICATION DANS XAMPP ===");
			System.out.println("Connectez-vous à phpMyAdmin et vérifiez:");
			System.out.println("1. Table 'reparations' → 2 réparations");
			System.out.println("2. Table 'appareils' → 4 appareils");
			System.out.println("3. Table 'reparation_appareil' → 4 liens");
			
			// Fermer la connexion
			gr.close();
			
		} catch (Exception e) {
			System.err.println("Erreur lors de la création: " + e.getMessage());
			e.printStackTrace();
		}
		
		// ========== ACTION LISTENER ==========
		btnNewButton.addActionListener(e -> {
			String username = textField.getText();
			String password = new String(passwordField.getPassword());
			
			try {
				GestionUtilisateur gu = new GestionUtilisateur();
				UserDAO user = gu.SeConnecter(username, password);
				
				if (user != null) {
					System.out.println("✅ Connexion réussie pour: " + user.getUsername());
				} else {
					System.out.println("❌ Identifiants incorrects");
				}
			} catch (Exception ex) {
				System.err.println("Erreur: " + ex.getMessage());
			}
		});
	}
}