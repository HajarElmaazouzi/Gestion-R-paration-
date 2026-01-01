package presentation;

import dao.Proprietaire;
import metier.*;

import javax.swing.*;

public class CreateReparateurFrame extends JFrame {

    public CreateReparateurFrame(Proprietaire p) {

        JTextField nom = new JTextField(15);
        JTextField email = new JTextField(15);
        JPasswordField pwd = new JPasswordField(15);
        JTextField tel = new JTextField(15);
        JTextField spec = new JTextField(15);

        JButton btn = new JButton("Créer");

        btn.addActionListener(e -> {
            new ReparateurMetierImpl().creerReparateur(
                    nom.getText(),
                    email.getText(),
                    new String(pwd.getPassword()),
                    tel.getText(),
                    spec.getText(),
                    p.getBoutique().getId()
            );
            JOptionPane.showMessageDialog(this, "Réparateur créé !");
            dispose();
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Nom")); panel.add(nom);
        panel.add(new JLabel("Email")); panel.add(email);
        panel.add(new JLabel("Mot de passe")); panel.add(pwd);
        panel.add(new JLabel("Téléphone")); panel.add(tel);
        panel.add(new JLabel("Spécialité")); panel.add(spec);
        panel.add(btn);

        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
