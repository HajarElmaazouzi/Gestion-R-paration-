package presentation;

import dao.*;
import metier.*;

import javax.swing.*;

public class LoginProprietaireFrame extends JFrame {

    public LoginProprietaireFrame(String typeConnexion) {

        setTitle("Connexion - " + typeConnexion);

        JTextField email = new JTextField(15);
        JPasswordField pwd = new JPasswordField(15);
        JButton btn = new JButton("Connexion");

        btn.addActionListener(e -> {

            UtilisateurMetierImpl metier = new UtilisateurMetierImpl();
            Object u = metier.login(
                    email.getText(),
                    new String(pwd.getPassword())
            );

            if (u instanceof Proprietaire) {
                new DashboardProprietaireFrame((Proprietaire) u);
                dispose();
            } 
            else if (u instanceof Reparateur) {
                new DashboardReparateurFrame((Reparateur) u);
                dispose();
            } 
            else {
                JOptionPane.showMessageDialog(this,
                        "Email ou mot de passe incorrect",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel p = new JPanel();
        p.add(new JLabel("Email"));
        p.add(email);
        p.add(new JLabel("Mot de passe"));
        p.add(pwd);
        p.add(btn);

        add(p);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
