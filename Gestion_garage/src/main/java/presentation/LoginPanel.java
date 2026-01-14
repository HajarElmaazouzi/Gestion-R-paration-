package presentation;

import javax.swing.*;
import java.awt.*;

import dao.CaisseDAO;
import dao.Proprietaire;
import dao.ReparateurDAO;
import dao.UserDAO;
import metier.GestionCaisses;
import metier.GestionUtilisateur;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel loginFormPanel;
    private JPanel mainMenuPanel;
    private MainWindow mainWindow;
    private String expectedUserType; // "Propriétaire" or "Réparateur"

    public LoginPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new CardLayout());
        setBackground(new Color(240, 240, 240));

        // Panel principal avec les trois boutons
        mainMenuPanel = createMainMenuPanel();
        add(mainMenuPanel, "mainMenu");
        
        // Panel de login
        loginFormPanel = createLoginFormPanel();
        add(loginFormPanel, "loginForm");
        
        // Afficher le menu principal par défaut
        ((CardLayout) getLayout()).show(this, "mainMenu");

        // Initialiser les utilisateurs par défaut
        initializeDefaultUsers();
    }
    
    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 248, 255), 0, h, new Color(230, 240, 250));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                g2d.dispose();
            }
        };
        panel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(30, 30, 30, 30);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        
        // Titre avec style moderne
        JLabel title = new JLabel("🔧 Electro 57 Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(44, 62, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title, gbc);
        
        JLabel subtitle = new JLabel("Système de Gestion de Réparation");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(127, 140, 141));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 30, 40, 30);
        panel.add(subtitle, gbc);
        
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(20, 20, 20, 20);
        
        // Bouton Propriétaire avec style moderne
        JButton btnProprietaire = createStyledButton("👤 Propriétaire", 
            new Color(52, 152, 219), new Color(41, 128, 185), "Gérez vos boutiques et réparateurs");
        gbc.gridx = 0;
        panel.add(btnProprietaire, gbc);
        
        // Bouton Réparateur avec style moderne
        JButton btnReparateur = createStyledButton("🔨 Réparateur", 
            new Color(46, 204, 113), new Color(39, 174, 96), "Créez et suivez les réparations");
        gbc.gridx = 1;
        panel.add(btnReparateur, gbc);
        
        // Bouton Client avec style moderne
        JButton btnClient = createStyledButton("📱 Client", 
            new Color(155, 89, 182), new Color(142, 68, 173), "Suivez votre réparation");
        gbc.gridx = 2;
        panel.add(btnClient, gbc);
        
        // Actions des boutons
        btnProprietaire.addActionListener(e -> {
            showLoginForm("Propriétaire");
        });
        
        btnReparateur.addActionListener(e -> {
            showLoginForm("Réparateur");
        });
        
        btnClient.addActionListener(e -> {
            mainWindow.showClientTrackingPanel();
        });
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color hoverColor, String tooltip) {
        JButton button = new JButton("<html><center>" + text + "<br><small style='font-size:10px;font-weight:normal;'>" + tooltip + "</small></center></html>") {
            private Color originalBg = bgColor;
            private Color hoverBg = hoverColor;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(hoverBg.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(hoverBg);
                } else {
                    g2d.setColor(originalBg);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Ombre
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(2, 2, getWidth(), getHeight(), 15, 15);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(220, 120));
        button.setToolTipText(tooltip);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JPanel createLoginFormPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Ombre
                g2d.setColor(new Color(0, 0, 0, 15));
                for (int i = 0; i < 5; i++) {
                    g2d.drawRoundRect(i, i, getWidth() - 2*i, getHeight() - 2*i, 20, 20);
                }
                g2d.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JLabel title = new JLabel("🔐 Connexion");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(44, 62, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUsername = new JLabel("Nom d'utilisateur");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUsername.setForeground(new Color(127, 140, 141));
        lblUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        usernameField = new JTextField(30);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        usernameField.setPreferredSize(new Dimension(350, 40));
        usernameField.setMaximumSize(new Dimension(350, 40));
        
        JLabel lblPassword = new JLabel("Mot de passe");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPassword.setForeground(new Color(127, 140, 141));
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField(30);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        passwordField.setPreferredSize(new Dimension(350, 40));
        passwordField.setMaximumSize(new Dimension(350, 40));

        JButton loginBtn = createModernButton("Se connecter", new Color(66, 133, 244), new Color(52, 152, 219));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setPreferredSize(new Dimension(350, 45));
        loginBtn.setMaximumSize(new Dimension(350, 45));
        
        JButton backBtn = new JButton("← Retour");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backBtn.setBackground(Color.WHITE);
        backBtn.setForeground(new Color(100, 100, 100));
        backBtn.setFocusPainted(false);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.add(title);
        card.add(Box.createVerticalStrut(30));
        card.add(lblUsername);
        card.add(Box.createVerticalStrut(5));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(20));
        card.add(lblPassword);
        card.add(Box.createVerticalStrut(5));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(30));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(15));
        card.add(backBtn);
        
        // Actions
        GestionUtilisateur gu = new GestionUtilisateur();
        
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            System.out.println("🔍 LoginPanel - Tentative de connexion: username='" + username + "', password='" + password + "' (length: " + password.length() + ")");
            System.out.println("🔍 Type d'utilisateur attendu: " + expectedUserType);
            
            UserDAO user = gu.SeConnecter(username, password);
            if (user != null) {
                // Vérifier que le type d'utilisateur correspond à la section choisie
                boolean isValidLogin = false;
                String userTypeName = "";
                
                if (user instanceof ReparateurDAO) {
                    userTypeName = "Réparateur";
                    if ("Réparateur".equals(expectedUserType)) {
                        isValidLogin = true;
                        mainWindow.showReparateurPanel((ReparateurDAO) user);
                    }
                } else if (user instanceof Proprietaire) {
                    userTypeName = "Propriétaire";
                    if ("Propriétaire".equals(expectedUserType)) {
                        isValidLogin = true;
                        mainWindow.showOwnerPanel((UserDAO) user);
                    }
                } else if (user instanceof UserDAO) {
                    // UserDAO de base - traiter comme Proprietaire pour compatibilité
                    userTypeName = "Propriétaire";
                    if ("Propriétaire".equals(expectedUserType)) {
                        isValidLogin = true;
                        mainWindow.showOwnerPanel((UserDAO) user);
                    }
                }
                
                if (!isValidLogin) {
                    JOptionPane.showMessageDialog(
                        this, 
                        "Erreur: Vous êtes connecté en tant que " + userTypeName + 
                        ".\nVeuillez vous connecter depuis la section " + userTypeName + ".", 
                        "Type d'utilisateur incorrect", 
                        JOptionPane.ERROR_MESSAGE
                    );
                    // Réinitialiser les champs
                    usernameField.setText("");
                    passwordField.setText("");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Identifiants incorrects!", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        backBtn.addActionListener(e -> {
            ((CardLayout) getLayout()).show(this, "mainMenu");
        });
        
        return card;
    }
    
    private void showLoginForm(String userType) {
        this.expectedUserType = userType; // Stocker le type d'utilisateur attendu
        ((CardLayout) getLayout()).show(this, "loginForm");
        // Réinitialiser les champs de connexion
        if (usernameField != null) {
            usernameField.setText("");
        }
        if (passwordField != null) {
            passwordField.setText("");
        }
    }
    
    private void initializeDefaultUsers() {
        GestionUtilisateur gu = new GestionUtilisateur();
        GestionCaisses gc = new GestionCaisses();
        
        // Créer l'utilisateur admin par défaut s'il n'existe pas déjà
        if (!gu.userExists("admin")) {
            try {
                CaisseDAO caisse1 = gc.creerCaisse();
                if (caisse1 == null) {
                    System.err.println("❌ Erreur: Impossible de créer la caisse pour l'admin");
                    return;
                }
                
                Proprietaire admin = Proprietaire.builder()
                        .username("admin")
                        .password("admin")
                        .caisse(caisse1)
                        .build();
                
                gu.CreerUser(admin);
                System.out.println("✅ Compte admin créé: username='admin', password='admin'");
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la création du compte admin: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("ℹ️  Compte admin existe déjà");
        }
    }
	private JButton createModernButton(String text, Color bgColor, Color hoverColor) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				if (getModel().isPressed()) {
					g2d.setColor(hoverColor.darker());
				} else if (getModel().isRollover()) {
					g2d.setColor(hoverColor);
				} else {
					g2d.setColor(bgColor);
				}
				
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		
		button.setFont(new Font("Segoe UI", Font.BOLD, 14));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		return button;
	}
}
