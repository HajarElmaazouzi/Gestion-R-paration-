package metier;

import dao.Appareil;
import dao.EtatReparation;
import dao.Reparation;
import dao.UserDAO;
import exceptions.GestionException;
import java.util.List;

public interface IGestionReparation {
    
    // === GESTION DES APPAREILS ===
    Appareil ajouterAppareil(Appareil appareil) throws GestionException;
    Appareil modifierAppareil(Appareil appareil) throws GestionException;
    boolean supprimerAppareil(Long id) throws GestionException;
    Appareil rechercherAppareilParId(Long id);
    Appareil rechercherAppareilParIMEI(String imei);
    List<Appareil> listerAppareilsDisponibles();
    
    // === GESTION DES RÉPARATIONS (POUR L'UTILISATEUR CONNECTÉ) ===
    Reparation creerReparation(Reparation reparation, UserDAO userConnecte) throws GestionException;
    Reparation modifierReparation(Long reparationId, Reparation nouvellesDonnees) throws GestionException;
    boolean supprimerReparation(Long reparationId) throws GestionException;
    Reparation rechercherReparationParId(Long reparationId);
    Reparation rechercherReparationParNumero(String numero);
    List<Reparation> listerMesReparations(UserDAO userConnecte);
    
    // === CHANGEMENT D'ÉTAT ===
    Reparation demarrerReparation(Long reparationId) throws GestionException;
    Reparation terminerReparation(Long reparationId, String diagnostic, 
                                  String piecesUtilisees, Double coutTotal) throws GestionException;
    Reparation annulerReparation(Long reparationId, String raison) throws GestionException;
    
    // === GESTION DES APPAREILS DANS RÉPARATION ===
    Reparation ajouterAppareilAReparation(Long reparationId, Long appareilId) throws GestionException;
    Reparation retirerAppareilDeReparation(Long reparationId, Long appareilId) throws GestionException;
    
    // === STATISTIQUES POUR L'UTILISATEUR ===
    long compterMesReparations(UserDAO userConnecte);
    long compterMesReparationsParEtat(UserDAO userConnecte, EtatReparation etat);
	/**
	 * Liste TOUTES les réparations (pour le propriétaire)
	 */
	List<Reparation> listerToutesLesReparations();
	/**
	 * Liste TOUS les appareils (pas seulement disponibles)
	 */
	List<Appareil> listerTousLesAppareils();
}