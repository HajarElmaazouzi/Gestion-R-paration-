package metier;

import javax.persistence.EntityManager;
import dao.CaisseDAO;
import dao.UserDAO;

public class GestionCaisses implements IGestionCaisses {
    
    private EntityManager em;
    
    public GestionCaisses() {
        this.em = JPAUtil.getEntityManager();
    }
    
    @Override
    public CaisseDAO creerCaisse() {
        try {
            em.getTransaction().begin();
            
            CaisseDAO caisse = CaisseDAO.builder()
                    .soldeActuel(0.0)
                    .totalEncaisse(0.0)
                    .totalRetire(0.0)
                    .build();
            
            em.persist(caisse);
            em.getTransaction().commit();
            
            System.out.println("✅ Caisse créée avec succès (ID: " + caisse.getId() + ")");
            return caisse;
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("❌ Erreur lors de la création de la caisse : " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public void encaisserPaiement(int userId, Double montant, String description) {
        if (montant <= 0) {
            System.err.println("❌ Le montant doit être supérieur à 0");
            return;
        }
        
        try {
            em.getTransaction().begin();
            
            // Convertir int en Long pour correspondre au type de UserDAO.id
            Long userIdLong = Long.valueOf(userId);
            
            // Chercher d'abord l'utilisateur puis sa caisse (plus fiable avec la relation inverse)
            UserDAO user = em.find(UserDAO.class, userIdLong);
            if (user == null) {
                em.getTransaction().rollback();
                System.err.println("❌ Utilisateur non trouvé avec l'ID: " + userId);
                return;
            }
            
            CaisseDAO caisse = user.getCaisse();
            if (caisse == null) {
                em.getTransaction().rollback();
                System.err.println("❌ Caisse non trouvée pour l'utilisateur ID: " + userId);
                return;
            }
            
            Double ancienTotal = caisse.getTotalEncaisse() != null ? caisse.getTotalEncaisse() : 0.0;
            Double nouveauTotal = ancienTotal + montant;
            
            caisse.setSoldeActuel(caisse.getSoldeActuel() + montant);
            caisse.setTotalEncaisse(nouveauTotal);
            caisse.setDateDerniereMaj(new java.util.Date());
            
            em.merge(caisse);
            em.flush(); // Forcer l'écriture en base
            em.getTransaction().commit();
            
            System.out.println(String.format("✅ Paiement encaissé: %.2f DH - %s. Total encaissé: %.2f → %.2f DH, Nouveau solde: %.2f DH", 
                    montant, description, ancienTotal, nouveauTotal, caisse.getSoldeActuel()));
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("❌ Erreur lors de l'encaissement : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void rembourserAvance(int userId, Double montant, String description) {
        if (montant <= 0) {
            System.err.println("❌ Le montant doit être supérieur à 0");
            return;
        }
        
        try {
            em.getTransaction().begin();
            
            CaisseDAO caisse = em.createQuery(
                    "SELECT c FROM CaisseDAO c WHERE c.user.id = :userId", 
                    CaisseDAO.class)
                    .setParameter("userId", userId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            
            if (caisse == null) {
                em.getTransaction().rollback();
                System.err.println("❌ Caisse non trouvée pour l'utilisateur ID: " + userId);
                return;
            }
            
            if (caisse.getSoldeActuel() < montant) {
                em.getTransaction().rollback();
                System.err.println("❌ Solde insuffisant pour le remboursement. Solde actuel: " + caisse.getSoldeActuel() + " DH");
                return;
            }
            
            caisse.setSoldeActuel(caisse.getSoldeActuel() - montant);
            caisse.setTotalRetire(caisse.getTotalRetire() + montant);
            caisse.setDateDerniereMaj(new java.util.Date());
            
            em.merge(caisse);
            em.getTransaction().commit();
            
            System.out.println(String.format("✅ Remboursement effectué: %.2f DH - %s. Nouveau solde: %.2f DH", 
                    montant, description, caisse.getSoldeActuel()));
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("❌ Erreur lors du remboursement : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void effectuerRetrait(int userId, Double montant, String description) {
        if (montant <= 0) {
            System.err.println("❌ Le montant doit être supérieur à 0");
            return;
        }
        
        try {
            em.getTransaction().begin();
            
            // Convertir int en Long pour correspondre au type de UserDAO.id
            Long userIdLong = Long.valueOf(userId);
            
            // Chercher d'abord l'utilisateur puis sa caisse (plus fiable avec la relation inverse)
            UserDAO user = em.find(UserDAO.class, userIdLong);
            if (user == null) {
                em.getTransaction().rollback();
                System.err.println("❌ Utilisateur non trouvé avec l'ID: " + userId);
                return;
            }
            
            CaisseDAO caisse = user.getCaisse();
            if (caisse == null) {
                em.getTransaction().rollback();
                System.err.println("❌ Caisse non trouvée pour l'utilisateur ID: " + userId);
                return;
            }
            
            if (caisse.getSoldeActuel() < montant) {
                em.getTransaction().rollback();
                System.err.println("❌ Solde insuffisant pour le retrait. Solde actuel: " + caisse.getSoldeActuel() + " DH");
                return;
            }
            
            caisse.setSoldeActuel(caisse.getSoldeActuel() - montant);
            caisse.setTotalRetire(caisse.getTotalRetire() + montant);
            caisse.setDateDerniereMaj(new java.util.Date());
            
            em.merge(caisse);
            em.getTransaction().commit();
            
            System.out.println(String.format("✅ Retrait effectué: %.2f DH - %s. Nouveau solde: %.2f DH", 
                    montant, description, caisse.getSoldeActuel()));
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("❌ Erreur lors du retrait : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public CaisseDAO consulterCaisse(int userId) {
        try {
            // Clear cache pour avoir des données fraîches
            em.clear();
            
            // Convertir int en Long pour correspondre au type de UserDAO.id
            Long userIdLong = Long.valueOf(userId);
            
            // Chercher d'abord l'utilisateur puis sa caisse (plus fiable avec la relation inverse)
            UserDAO user = em.find(UserDAO.class, userIdLong);
            if (user == null) {
                System.err.println("❌ Utilisateur non trouvé avec l'ID: " + userId);
                return null;
            }
            
            CaisseDAO caisse = user.getCaisse();
            if (caisse == null) {
                System.err.println("❌ Caisse non trouvée pour l'utilisateur ID: " + userId);
                return null;
            }
            
            // Recharger la caisse depuis la base pour avoir les données à jour
            CaisseDAO caisseFraiche = em.find(CaisseDAO.class, caisse.getId());
            if (caisseFraiche != null) {
                System.out.println(String.format("💰 Solde actuel: %.2f DH | Total encaissé: %.2f DH | Total retiré: %.2f DH", 
                        caisseFraiche.getSoldeActuel(), caisseFraiche.getTotalEncaisse(), caisseFraiche.getTotalRetire()));
                return caisseFraiche;
            }
            
            return caisse;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la consultation de la caisse : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public CaisseDAO consulterCaisseProprietaire(int reparateurId) {
        try {
            // Convertir int en Long pour correspondre au type de UserDAO.id
            Long reparateurIdLong = Long.valueOf(reparateurId);
            
            // Chercher d'abord l'utilisateur puis sa caisse (plus fiable avec la relation inverse)
            UserDAO user = em.find(UserDAO.class, reparateurIdLong);
            if (user == null) {
                System.err.println("❌ Réparateur non trouvé avec l'ID: " + reparateurId);
                return null;
            }
            
            CaisseDAO caisse = user.getCaisse();
            if (caisse == null) {
                System.err.println("❌ Caisse non trouvée pour le réparateur ID: " + reparateurId);
                return null;
            }
            
            System.out.println(String.format("👁️ [Propriétaire] Caisse du réparateur - Solde: %.2f DH (emprunts non inclus)", 
                    caisse.getSoldeActuel()));
            
            return caisse;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la consultation : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Double calculerMontantProprietaire(int reparateurId, double pourcentage) {
        try {
            CaisseDAO caisse = consulterCaisse(reparateurId);
            if (caisse != null) {
                Double montantDu = caisse.getTotalEncaisse() * (pourcentage / 100.0);
                System.out.println(String.format("💵 Montant dû au propriétaire: %.2f DH (%.2f%% de %.2f DH)", 
                        montantDu, pourcentage, caisse.getTotalEncaisse()));
                return montantDu;
            }
            return 0.0;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du calcul du montant propriétaire : " + e.getMessage());
            return 0.0;
        }
    }
}
