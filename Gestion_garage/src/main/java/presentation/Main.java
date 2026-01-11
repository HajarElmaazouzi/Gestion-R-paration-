package presentation;

import dao.Boutique;
import dao.Proprietaire;
import dao.Reparateur;
import metier.*;

import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== GESTION GARAGE =====");
            System.out.println("1. Créer compte propriétaire");
            System.out.println("2. Se connecter (Propriétaire)");
            System.out.println("3. Se connecter (Réparateur)");
            System.out.println("0. Quitter");
            System.out.print("Votre choix : ");

            int choix = sc.nextInt();
            sc.nextLine(); // consomme le retour à la ligne

            switch (choix) {
                case 1:
                    creerCompteProprietaire();
                    break;
                case 2:
                    connexionProprietaire();
                    break;
                case 3:
                    connexionReparateur();
                    break;
                case 0:
                    System.out.println("Au revoir !");
                    System.exit(0);
                    break;
                default:
                    System.out.println("❌ Choix invalide !");
            }
        }
    }

    // ===========================
    // Création compte propriétaire
    // ===========================
    static void creerCompteProprietaire() {
        System.out.print("Nom : ");
        String nom = sc.nextLine();
        System.out.print("Email : ");
        String email = sc.nextLine();
        System.out.print("Mot de passe : ");
        String password = sc.nextLine();
        System.out.print("Nom de la boutique : ");
        String nomBoutique = sc.nextLine();
        System.out.print("Adresse de la boutique : ");
        String adresseBoutique = sc.nextLine();

        try {
            // 1️⃣ Création boutique
            IBoutiqueMetier boutiqueMetier = new BoutiqueMetierImpl();
            Boutique boutique = boutiqueMetier.creerBoutique(nomBoutique, adresseBoutique);

            // 2️⃣ Création propriétaire
            IProprietaireMetier proprietaireMetier = new ProprietaireMetierImpl();
            proprietaireMetier.creerProprietaire(nom, email, password, boutique.getId());

            System.out.println("✅ Compte propriétaire créé avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors de la création du propriétaire/boutique");
        }
    }

    // ===========================
    // Connexion propriétaire
    // ===========================
    static void connexionProprietaire() {
        System.out.print("Email : ");
        String email = sc.nextLine();
        System.out.print("Mot de passe : ");
        String password = sc.nextLine();

        ProprietaireMetierImpl metier = new ProprietaireMetierImpl();
        Proprietaire p = metier.login(email, password);

        if (p == null) {
            System.out.println("❌ Connexion échouée !");
            return;
        }

        System.out.println("Bienvenue " + p.getNom());
        menuProprietaire(p);
    }

    // ===========================
    // Connexion réparateur
    // ===========================
    static void connexionReparateur() {
        System.out.print("Email : ");
        String email = sc.nextLine();
        System.out.print("Mot de passe : ");
        String password = sc.nextLine();

        ReparateurMetierImpl metier = new ReparateurMetierImpl();
        Reparateur r = metier.login(email, password);

        if (r == null) {
            System.out.println("❌ Connexion échouée !");
            return;
        }

        System.out.println("Bienvenue " + r.getNom());
        menuReparateur(r);
    }

    // ===========================
    // Menu propriétaire
    // ===========================
    static void menuProprietaire(Proprietaire p) {
        while (true) {
            System.out.println("\n--- MENU PROPRIETAIRE ---");
            System.out.println("1. Créer réparateur");
            System.out.println("0. Déconnexion");
            System.out.print("Votre choix : ");

            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1:
                    creerReparateur(p);
                    break;
                case 0:
                    System.out.println("Déconnexion...");
                    return;
                default:
                    System.out.println("❌ Choix invalide !");
            }
        }
    }

    // ===========================
    // Menu réparateur
    // ===========================
    static void menuReparateur(Reparateur r) {
        System.out.println("--- MENU REPARATEUR ---");
        System.out.println("Fonctionnalités du réparateur ici...");
    }

    // ===========================
    // Création réparateur
    // ===========================
    static void creerReparateur(Proprietaire p) {
        try {
            // Récupération de toutes les boutiques existantes
            IBoutiqueMetier boutiqueMetier = new BoutiqueMetierImpl();
            List<Boutique> boutiques = boutiqueMetier.listerBoutiques();

            if (boutiques.isEmpty()) {
                System.out.println("❌ Aucune boutique existante. Créez d'abord une boutique.");
                return;
            }

            System.out.println("Sélectionnez la boutique pour le réparateur :");
            for (int i = 0; i < boutiques.size(); i++) {
                System.out.println((i + 1) + ". " + boutiques.get(i).getNom() + " (" + boutiques.get(i).getAdresse() + ")");
            }
            System.out.print("Votre choix : ");
            int choix = sc.nextInt() - 1;
            sc.nextLine();

            if (choix < 0 || choix >= boutiques.size()) {
                System.out.println("❌ Choix invalide !");
                return;
            }

            Boutique boutiqueChoisie = boutiques.get(choix);

            // Saisie infos réparateur
            System.out.print("Nom : ");
            String nom = sc.nextLine();
            System.out.print("Email : ");
            String email = sc.nextLine();
            System.out.print("Mot de passe : ");
            String pwd = sc.nextLine();
            System.out.print("Téléphone : ");
            String tel = sc.nextLine();
            System.out.print("Spécialité : ");
            String spec = sc.nextLine();

            // Création réparateur
            IReparateurMetier reparateurMetier = new ReparateurMetierImpl();
            reparateurMetier.creerReparateur(nom, email, pwd, tel, spec, boutiqueChoisie.getId());

            System.out.println("✅ Réparateur créé avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors de la création du réparateur");
        }
    }
}
