package presentation;

import javax.swing.*;

public class StartFrame extends JFrame {

    public StartFrame() {

        setTitle("Gestion Garage - Accueil");

        JButton btnCreate = new JButton("Créer compte propriétaire");
        JButton btnLoginProp = new JButton("Se connecter (Propriétaire)");
        JButton btnLoginRep = new JButton("Se connecter (Réparateur)");

        // =========================
        // CRÉATION COMPTE PROPRIÉTAIRE
        // =========================
        btnCreate.addActionListener(e -> {
            dispose();
            new CreateBoutiqueProprietaireFrame();
        });

        // =========================
        // LOGIN PROPRIÉTAIRE
        // =========================
        btnLoginProp.addActionListener(e -> {
            dispose();
            new LoginProprietaireFrame("PROPRIETAIRE");
        });

        // =========================
        // LOGIN RÉPARATEUR
        // =========================
        btnLoginRep.addActionListener(e -> {
            dispose();
            new LoginProprietaireFrame("REPARATEUR");
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(btnCreate);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnLoginProp);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnLoginRep);

        add(panel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
