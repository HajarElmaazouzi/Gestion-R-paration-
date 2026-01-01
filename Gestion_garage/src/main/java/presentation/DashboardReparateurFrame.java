package presentation;

import dao.Reparateur;

import javax.swing.*;

public class DashboardReparateurFrame extends JFrame {

    public DashboardReparateurFrame(Reparateur r) {

        JPanel panel = new JPanel();
        panel.add(new JLabel("Bienvenue réparateur : " + r.getNom()));

        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
