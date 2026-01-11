package presentation;

import dao.Boutique;
import metier.*;

import javax.swing.*;

public class CreateBoutiqueProprietaireFrame extends JFrame {

    public CreateBoutiqueProprietaireFrame() {

        setTitle("Création compte propriétaire");

        // Champs de saisie
        JTextField nomBoutique = new JTextField(15);
        JTextField adrBoutique = new JTextField(15);
        JTextField nomProp = new JTextField(15);
        JTextField emailProp = new JTextField(15);
        JPasswordField pwdProp = new JPasswordField(15);

        // Boutons
        JButton btnCreer = new JButton("Créer");
        JButton btnRetour = new JButton("Retour à la connexion");

        // =========================
        // CRÉATION DU COMPTE
        // =========================
        btnCreer.addActionListener(e -> {
            try {
                // 1️⃣ Création de la boutique
                IBoutiqueMetier boutiqueMetier = new BoutiqueMetierImpl();
                Boutique boutique = boutiqueMetier.creerBoutique(
                        nomBoutique.getText(),
                        adrBoutique.getText()
                );

                // 2️⃣ Création du propriétaire
                IProprietaireMetier proprietaireMetier = new ProprietaireMetierImpl();
                // Attention : notre creerProprietaire prend maintenant le nom de la boutique ou l'objet Boutique
                // Ici on suppose que la méthode accepte l'objet Boutique ou l'ID
                proprietaireMetier.creerProprietaire(
                        nomProp.getText(),
                        emailProp.getText(),
                        new String(pwdProp.getPassword()),
                        boutique.getNom() // ou boutique.getId() selon ta méthode
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Compte propriétaire créé avec succès !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "Erreur lors de la création du compte : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // =========================
        // RETOUR À LA CONNEXION
        // =========================
        btnRetour.addActionListener(e -> {
            dispose(); // ferme ce frame
            new LoginProprietaireFrame("PROPRIETAIRE"); // retourne au login
        });

        // =========================
        // PANEL & LAYOUT
        // =========================
        JPanel panel = new JPanel();
        panel.add(new JLabel("Nom Boutique"));
        panel.add(nomBoutique);
        panel.add(new JLabel("Adresse"));
        panel.add(adrBoutique);
        panel.add(new JLabel("Nom Propriétaire"));
        panel.add(nomProp);
        panel.add(new JLabel("Email"));
        panel.add(emailProp);
        panel.add(new JLabel("Mot de passe"));
        panel.add(pwdProp);
        panel.add(btnCreer);
        panel.add(btnRetour);

        add(panel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Pour tester le frame seul
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CreateBoutiqueProprietaireFrame::new);
    }
}
