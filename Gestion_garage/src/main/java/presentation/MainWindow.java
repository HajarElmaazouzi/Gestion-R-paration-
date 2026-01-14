package presentation;

import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import dao.Reparation;
import dao.UserDAO;
import java.awt.CardLayout;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private CardLayout cardLayout;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		cardLayout = new CardLayout();
		contentPane.setLayout(cardLayout);
		setContentPane(contentPane);
		
		LoginPanel loginPanel = new LoginPanel(this);
		contentPane.add(loginPanel, "loginPanel");
		cardLayout.show(contentPane, "loginPanel");
	}
	
	public void showLoginPanel() {
		cardLayout.show(contentPane, "loginPanel");
	}
	
	/**
	 * Affiche l'interface de suivi client (sans authentification)
	 */
	public void showClientTrackingPanel() {
		ClientTrackingPanel panel = new ClientTrackingPanel(this);
		contentPane.add(panel, "clientTracking");
		cardLayout.show(contentPane, "clientTracking");
	}
	
	public void showOwnerPanel(UserDAO user) {
		OwnerPanel ownerPanel = new OwnerPanel(user, this); 
		contentPane.add(ownerPanel, "ownerPanel");
		cardLayout.show(contentPane, "ownerPanel");
	}
	
	public void showReparateurPanel(UserDAO user) {
		ReparateurPanel reparateurPanel = new ReparateurPanel(user, this);
		contentPane.add(reparateurPanel, "reparateurPanel");
		cardLayout.show(contentPane, "reparateurPanel");
	}
	
	public void showMesBoutiquesPanel(UserDAO user) {
	    MesBoutiquesPanel panel = new MesBoutiquesPanel(user, this);
	    contentPane.add(panel, "mesBoutiques");
	    cardLayout.show(contentPane, "mesBoutiques");
	}
	
	public void showMesReparateursPanel(UserDAO user) {
	    MesReparateursPanel panel = new MesReparateursPanel(user, this);
	    contentPane.add(panel, "mesReparateurs");
	    cardLayout.show(contentPane, "mesReparateurs");
	}
	
	// ========== MÉTHODES POUR GESTION RÉPARATIONS ==========
	
	/**
	 * Affiche la liste des réparations de l'utilisateur
	 */
	public void showListeReparations(UserDAO user, boolean voirToutes) {
	    // Retirer l'ancien panel s'il existe pour forcer le rafraîchissement
	    Component oldPanel = null;
	    for (Component comp : contentPane.getComponents()) {
	        if (comp instanceof ListeReparationsPanel) {
	            oldPanel = comp;
	            break;
	        }
	    }
	    if (oldPanel != null) {
	        contentPane.remove(oldPanel);
	    }
	    
	    ListeReparationsPanel panel = new ListeReparationsPanel(user, this, voirToutes);
	    contentPane.add(panel, "listeReparations");
	    cardLayout.show(contentPane, "listeReparations");
	}
	
	/**
	 * Affiche les appareils associés à une réparation
	 * @param voirToutes true si vient de OwnerPanel, false si vient de ReparateurPanel
	 */
	public void showAppareilsReparation(UserDAO user, Reparation reparation, boolean voirToutes) {
	    AppareilsReparationPanel panel = new AppareilsReparationPanel(user, this, reparation, voirToutes);
	    contentPane.add(panel, "appareilsReparation");
	    cardLayout.show(contentPane, "appareilsReparation");
	}
	
	/**
	 * Affiche les appareils associés à une réparation (surcharge pour compatibilité)
	 */
	public void showAppareilsReparation(UserDAO user, Reparation reparation) {
		// Par défaut, déterminer depuis quel panel selon le type d'utilisateur
		boolean voirToutes = !(user instanceof dao.ReparateurDAO);
		showAppareilsReparation(user, reparation, voirToutes);
	}
	
	/**
	 * Affiche la gestion du stock d'appareils
	 * @param fromReparateurPanel true si appelé depuis ReparateurPanel, false si depuis OwnerPanel
	 */
	public void showGestionAppareils(UserDAO user, boolean fromReparateurPanel) {
	    GestionAppareilsPanel panel = new GestionAppareilsPanel(user, this, fromReparateurPanel);
	    contentPane.add(panel, "gestionAppareils");
	    cardLayout.show(contentPane, "gestionAppareils");
	}
	
	/**
	 * Affiche la gestion du stock d'appareils (surcharge pour compatibilité)
	 */
	public void showGestionAppareils(UserDAO user) {
		// Par défaut, déterminer depuis quel panel selon le type d'utilisateur
		boolean fromReparateurPanel = user instanceof dao.ReparateurDAO;
		showGestionAppareils(user, fromReparateurPanel);
	}
}