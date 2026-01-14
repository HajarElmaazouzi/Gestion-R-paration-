package metier;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import dao.CaisseDAO;
import dao.EmpruntDAO;
import dao.EmpruntDAO.StatutEmprunt;
import dao.UserDAO;

public class GestionEmprunts implements IGestionEmprunts {
    
    private EntityManager em;
    private GestionCaisses gestionCaisses;
    
    public GestionEmprunts() {
        this.em = JPAUtil.getEntityManager();
        this.gestionCaisses = new GestionCaisses();
    }
    
    @Override
    public EmpruntDAO creerEmprunt(int emprunteurId, Integer preteurId, Double montant, String description) {
        if (montant <= 0) {
            System.err.println("❌ Le montant doit être supérieur à 0");
            return null;
        }
        
        if (description == null || description.trim().isEmpty()) {
            System.err.println("❌ La description est obligatoire");
            return null;
        }
        
        try {
            em.getTransaction().begin();
            
            UserDAO emprunteur = em.find(UserDAO.class, (long)emprunteurId);
            if (emprunteur == null) {
                em.getTransaction().rollback();
                System.err.println("❌ Emprunteur non trouvé");
                return null;
            }
            
            UserDAO preteur = null;
            
            if (preteurId == null) {
                // Cas 1: Emprunt de sa propre caisse
                EmpruntDAO emprunt = EmpruntDAO.builder()
                        .emprunteur(emprunteur)
                        .preteur(null)
                        .montant(montant)
                        .description(description)
                        .statut(StatutEmprunt.EN_COURS)
                        .build();
                
                em.persist(emprunt);
                em.getTransaction().commit();
                
                System.out.println(String.format("✅ Emprunt créé: %.2f DH de la caisse personnelle - %s", 
                        montant, description));
                return emprunt;
                
            } else {
                // Cas 2: Emprunt d'un autre réparateur
                preteur = em.find(UserDAO.class, (long)preteurId);
                if (preteur == null) {
                    em.getTransaction().rollback();
                    System.err.println("❌ Prêteur non trouvé");
                    return null;
                }
                
                CaisseDAO caissePreteur = em.createQuery(
                        "SELECT c FROM CaisseDAO c WHERE c.user.id = :userId", 
                        CaisseDAO.class)
                        .setParameter("userId", preteurId)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                
                if (caissePreteur == null) {
                    em.getTransaction().rollback();
                    System.err.println("❌ Caisse du prêteur non trouvée");
                    return null;
                }
                
                if (caissePreteur.getSoldeActuel() < montant) {
                    em.getTransaction().rollback();
                    System.err.println("❌ Le prêteur n'a pas assez d'argent. Solde: " + caissePreteur.getSoldeActuel() + " DH");
                    return null;
                }
                
                // Réduire la caisse du prêteur
                caissePreteur.setSoldeActuel(caissePreteur.getSoldeActuel() - montant);
                caissePreteur.setTotalRetire(caissePreteur.getTotalRetire() + montant);
                caissePreteur.setDateDerniereMaj(new Date());
                em.merge(caissePreteur);
                
                // Augmenter la caisse de l'emprunteur
                CaisseDAO caisseEmprunteur = em.createQuery(
                        "SELECT c FROM CaisseDAO c WHERE c.user.id = :userId", 
                        CaisseDAO.class)
                        .setParameter("userId", emprunteurId)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                
                if (caisseEmprunteur == null) {
                    em.getTransaction().rollback();
                    System.err.println("❌ Caisse de l'emprunteur non trouvée");
                    return null;
                }
                
                caisseEmprunteur.setSoldeActuel(caisseEmprunteur.getSoldeActuel() + montant);
                caisseEmprunteur.setDateDerniereMaj(new Date());
                em.merge(caisseEmprunteur);
                
                // Créer l'emprunt
                EmpruntDAO emprunt = EmpruntDAO.builder()
                        .emprunteur(emprunteur)
                        .preteur(preteur)
                        .montant(montant)
                        .description(description)
                        .statut(StatutEmprunt.EN_COURS)
                        .build();
                
                em.persist(emprunt);
                em.getTransaction().commit();
                
                System.out.println(String.format("✅ Emprunt créé: %.2f DH de %s vers %s - %s", 
                        montant, preteur.getUsername(), emprunteur.getUsername(), description));
                return emprunt;
            }
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("❌ Erreur lors de la création de l'emprunt : " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public void rembourserEmprunt(Long empruntId) {
        try {
            em.getTransaction().begin();
            
            EmpruntDAO emprunt = em.find(EmpruntDAO.class, empruntId);
            if (emprunt == null) {
                em.getTransaction().rollback();
                System.err.println("❌ Emprunt non trouvé");
                return;
            }
            
            if (emprunt.getStatut() == StatutEmprunt.REMBOURSE) {
                em.getTransaction().rollback();
                System.err.println("❌ Emprunt déjà remboursé");
                return;
            }
            
            if (emprunt.getPreteur() == null) {
                // Cas 1: Remboursement à sa propre caisse
                CaisseDAO caisseEmprunteur = em.createQuery(
                        "SELECT c FROM CaisseDAO c WHERE c.user.id = :userId", 
                        CaisseDAO.class)
                        .setParameter("userId", emprunt.getEmprunteur().getId().intValue())
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                
                if (caisseEmprunteur == null) {
                    em.getTransaction().rollback();
                    System.err.println("❌ Caisse de l'emprunteur non trouvée");
                    return;
                }
                
                caisseEmprunteur.setSoldeActuel(caisseEmprunteur.getSoldeActuel() + emprunt.getMontant());
                caisseEmprunteur.setDateDerniereMaj(new Date());
                em.merge(caisseEmprunteur);
                
                emprunt.setStatut(StatutEmprunt.REMBOURSE);
                emprunt.setDateRemboursement(new Date());
                em.merge(emprunt);
                
                em.getTransaction().commit();
                System.out.println(String.format("✅ Emprunt remboursé: %.2f DH remis dans la caisse personnelle", 
                        emprunt.getMontant()));
                
            } else {
                // Cas 2: Remboursement à un autre réparateur
                CaisseDAO caisseEmprunteur = em.createQuery(
                        "SELECT c FROM CaisseDAO c WHERE c.user.id = :userId", 
                        CaisseDAO.class)
                        .setParameter("userId", emprunt.getEmprunteur().getId().intValue())
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                
                if (caisseEmprunteur == null) {
                    em.getTransaction().rollback();
                    System.err.println("❌ Caisse de l'emprunteur non trouvée");
                    return;
                }
                
                if (caisseEmprunteur.getSoldeActuel() < emprunt.getMontant()) {
                    em.getTransaction().rollback();
                    System.err.println("❌ Solde insuffisant pour rembourser. Solde: " + caisseEmprunteur.getSoldeActuel() + " DH");
                    return;
                }
                
                // Déduire de l'emprunteur
                caisseEmprunteur.setSoldeActuel(caisseEmprunteur.getSoldeActuel() - emprunt.getMontant());
                caisseEmprunteur.setTotalRetire(caisseEmprunteur.getTotalRetire() + emprunt.getMontant());
                caisseEmprunteur.setDateDerniereMaj(new Date());
                em.merge(caisseEmprunteur);
                
                // Ajouter au prêteur
                CaisseDAO caissePreteur = em.createQuery(
                        "SELECT c FROM CaisseDAO c WHERE c.user.id = :userId", 
                        CaisseDAO.class)
                        .setParameter("userId", emprunt.getPreteur().getId().intValue())
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                
                if (caissePreteur == null) {
                    em.getTransaction().rollback();
                    System.err.println("❌ Caisse du prêteur non trouvée");
                    return;
                }
                
                caissePreteur.setSoldeActuel(caissePreteur.getSoldeActuel() + emprunt.getMontant());
                caissePreteur.setTotalEncaisse(caissePreteur.getTotalEncaisse() + emprunt.getMontant());
                caissePreteur.setDateDerniereMaj(new Date());
                em.merge(caissePreteur);
                
                emprunt.setStatut(StatutEmprunt.REMBOURSE);
                emprunt.setDateRemboursement(new Date());
                em.merge(emprunt);
                
                em.getTransaction().commit();
                System.out.println(String.format("✅ Emprunt remboursé: %.2f DH de %s vers %s", 
                        emprunt.getMontant(), emprunt.getEmprunteur().getUsername(), emprunt.getPreteur().getUsername()));
            }
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("❌ Erreur lors du remboursement : " + e.getMessage());
        }
    }
    
    @Override
    public List<EmpruntDAO> listerEmpruntsParUtilisateur(int userId) {
        try {
            List<EmpruntDAO> emprunts = em.createQuery(
                    "SELECT e FROM EmpruntDAO e WHERE e.emprunteur.id = :userId ORDER BY e.date DESC", 
                    EmpruntDAO.class)
                    .setParameter("userId", userId)
                    .getResultList();
            
            System.out.println("📋 Nombre d'emprunts trouvés: " + emprunts.size());
            return emprunts;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des emprunts : " + e.getMessage());
            return List.of();
        }
    }
    
    @Override
    public List<EmpruntDAO> listerEmpruntsEnCours(int userId) {
        try {
            List<EmpruntDAO> emprunts = em.createQuery(
                    "SELECT e FROM EmpruntDAO e WHERE e.emprunteur.id = :userId AND e.statut = :statut ORDER BY e.date DESC", 
                    EmpruntDAO.class)
                    .setParameter("userId", userId)
                    .setParameter("statut", StatutEmprunt.EN_COURS)
                    .getResultList();
            
            System.out.println("📋 Emprunts en cours: " + emprunts.size());
            return emprunts;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des emprunts en cours : " + e.getMessage());
            return List.of();
        }
    }
    
    @Override
    public List<EmpruntDAO> listerTousLesEmprunts() {
        try {
            List<EmpruntDAO> emprunts = em.createQuery(
                    "SELECT e FROM EmpruntDAO e ORDER BY e.date DESC", 
                    EmpruntDAO.class)
                    .getResultList();
            
            System.out.println("📋 Total des emprunts: " + emprunts.size());
            return emprunts;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération de tous les emprunts : " + e.getMessage());
            return List.of();
        }
    }
}
