package presentation;

import dao.Proprietaire;

import javax.swing.*;

public class DashboardProprietaireFrame extends JFrame {

    public DashboardProprietaireFrame(Proprietaire proprietaire) {

        JLabel lblNom = new JLabel("Propriétaire : " + proprietaire.getNom());
        JLabel lblBoutique = new JLabel("Boutique : " + proprietaire.getBoutique().getNom());

        JButton btnCreateRep = new JButton("Créer réparateur");
        JButton btnLogout = new JButton("Déconnexion");

        btnCreateRep.addActionListener(e -> {
            new CreateReparateurFrame(proprietaire);
        });

        btnLogout.addActionListener(e -> {
            dispose();
            new StartFrame();
        });

        JPanel panel = new JPanel();
        panel.add(lblNom);
        panel.add(lblBoutique);
        panel.add(btnCreateRep);
        panel.add(btnLogout);

        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
