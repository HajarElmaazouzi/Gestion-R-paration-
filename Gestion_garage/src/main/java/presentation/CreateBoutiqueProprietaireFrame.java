package presentation;

import dao.Boutique;
import metier.*;

import javax.swing.*;

public class CreateBoutiqueProprietaireFrame extends JFrame {

    public CreateBoutiqueProprietaireFrame() {

        setTitle("Création compte propriétaire");

        JTextField nomBoutique = new JTextField(15);
        JTextField adrBoutique = new JTextField(15);
        JTextField nomProp = new JTextField(15);
        JTextField emailProp = new JTextField(15);
        JPasswordField pwdProp = new JPasswordField(15);

        JButton btnCreer = new JButton("Créer");
        JButton btnRetour = new JButton("Retour à la connexion");

        // =========================
        // CRÉATION DU COMPTE
        // =========================
        btnCreer.addActionListener(e -> {

            IBoutiqueMetier boutiqueMetier = new BoutiqueMetierImpl();
            Boutique boutique = boutiqueMetier.creerBoutique(
                    nomBoutique.getText(),
                    adrBoutique.getText()
            );

            IProprietaireMetier proprietaireMetier = new ProprietaireMetierImpl();
            proprietaireMetier.creerProprietaire(
                    nomProp.getText(),
                    emailProp.getText(),
                    new String(pwdProp.getPassword()),
                    boutique.getId()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Compte propriétaire créé avec succès !",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // =========================
        // RETOUR À LA CONNEXION
        // =========================
        btnRetour.addActionListener(e -> {
            dispose();
            new LoginProprietaireFrame("PROPRIETAIRE");
        });

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
        setVisible(true);
    }
}
