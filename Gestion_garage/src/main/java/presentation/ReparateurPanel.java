package presentation;

import javax.swing.*;
import java.awt.*;
import dao.UserDAO;

public class ReparateurPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public ReparateurPanel(UserDAO user, MainWindow mainWindow) {
		setLayout(null);
		setBackground(new Color(240, 248, 255));
		
		// Header avec style moderne
		JPanel headerPanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				GradientPaint gp = new GradientPaint(0, 0, new Color(46, 204, 113), 0, getHeight(), new Color(39, 174, 96));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
			}
		};
		headerPanel.setOpaque(false);
		headerPanel.setBounds(0, 0, 900, 80);
		add(headerPanel);
		
		JLabel lblTitle = new JLabel("🔨 Espace Réparateur");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setBounds(20, 15, 400, 35);
		headerPanel.add(lblTitle);
		
		JLabel lblWelcome = new JLabel("Bienvenue, " + user.getUsername() + " 👋");
		lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblWelcome.setForeground(new Color(255, 255, 255, 200));
		lblWelcome.setBounds(20, 50, 300, 20);
		headerPanel.add(lblWelcome);
		
		// Bouton déconnexion dans le header
		JButton btnLogout = createModernButton("🚪 Déconnexion", new Color(231, 76, 60), new Color(192, 57, 43));
		btnLogout.setBounds(750, 20, 130, 40);
		headerPanel.add(btnLogout);
		
		// Panel latéral pour les boutons de navigation
		JPanel sidebarPanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(Color.WHITE);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 0, 0);
				
				// Ombre
				g2d.setColor(new Color(0, 0, 0, 10));
				for (int i = 0; i < 3; i++) {
					g2d.drawRoundRect(i, i, getWidth() - 2*i, getHeight() - 2*i, 0, 0);
				}
				g2d.dispose();
			}
		};
		sidebarPanel.setOpaque(false);
		sidebarPanel.setBounds(10, 100, 180, 480);
		add(sidebarPanel);
		
		// ========== BOUTONS DE NAVIGATION ==========
		
		JButton btnMesReparations = createSidebarButton("🔧 Mes Réparations", new Color(155, 89, 182));
		btnMesReparations.setBounds(10, 20, 160, 60);
		sidebarPanel.add(btnMesReparations);
		
		JButton btnGererAppareils = createSidebarButton("📱 Stock Appareils", new Color(241, 196, 15));
		btnGererAppareils.setBounds(10, 90, 160, 60);
		sidebarPanel.add(btnGererAppareils);
		
		// Panel de bienvenue au centre
		JPanel welcomePanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(Color.WHITE);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				
				// Ombre
				g2d.setColor(new Color(0, 0, 0, 10));
				for (int i = 0; i < 5; i++) {
					g2d.drawRoundRect(i, i, getWidth() - 2*i, getHeight() - 2*i, 20, 20);
				}
				g2d.dispose();
			}
		};
		welcomePanel.setOpaque(false);
		welcomePanel.setBounds(200, 100, 680, 480);
		add(welcomePanel);
		
		JLabel lblWelcomeTitle = new JLabel("📊 Tableau de Bord");
		lblWelcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblWelcomeTitle.setForeground(new Color(44, 62, 80));
		lblWelcomeTitle.setBounds(30, 30, 400, 35);
		welcomePanel.add(lblWelcomeTitle);
		
		JLabel lblInfo = new JLabel("<html><div style='text-align: center; padding: 20px;'>" +
			"<p style='font-size: 16px; color: #7f8c8d;'>Bienvenue dans votre espace de travail</p>" +
			"<p style='font-size: 14px; color: #95a5a6; margin-top: 20px;'>" +
			"Utilisez les boutons à gauche pour accéder aux différentes fonctionnalités :<br><br>" +
			"• <b>Mes Réparations</b> : Gérez vos réparations en cours<br>" +
			"• <b>Stock Appareils</b> : Consultez et gérez le stock d'appareils</p>" +
			"</div></html>");
		lblInfo.setBounds(50, 100, 580, 300);
		welcomePanel.add(lblInfo);
		
		// ========== ACTIONS ==========
		
		btnMesReparations.addActionListener(e -> {
			mainWindow.showListeReparations(user, false);
		});
		
		btnGererAppareils.addActionListener(e -> {
			mainWindow.showGestionAppareils(user, true);  // true = vient de ReparateurPanel
		});
		
		btnLogout.addActionListener(e -> {
			int confirm = JOptionPane.showConfirmDialog(this, 
				"Voulez-vous vraiment vous déconnecter?", 
				"Déconnexion", 
				JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				mainWindow.showLoginPanel();
			}
		});
	}
	
	private JButton createSidebarButton(String text, Color bgColor) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				Color currentBg;
				if (getModel().isPressed()) {
					currentBg = bgColor.darker();
				} else if (getModel().isRollover()) {
					currentBg = bgColor.brighter();
				} else {
					currentBg = bgColor;
				}
				
				g2d.setColor(currentBg);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		
		Color textColor;
		if (bgColor.getRed() > 200 && bgColor.getGreen() > 200) {
			textColor = new Color(44, 62, 80);
		} else {
			textColor = Color.WHITE;
		}
		
		button.setFont(new Font("Segoe UI", Font.BOLD, 13));
		button.setForeground(textColor);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		return button;
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
		
		button.setFont(new Font("Segoe UI", Font.BOLD, 12));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		return button;
	}
}