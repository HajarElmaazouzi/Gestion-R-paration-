package metier;

import dao.Appareil;
import dao.EtatAppareil;
import dao.EtatReparation;
import dao.Reparation;
import dao.ReparateurDAO;
import dao.UserDAO;
import exceptions.GestionException;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

public class GestionReparation implements IGestionReparation {
    
    private EntityManager em;
    
    public GestionReparation() {
        this.em = JPAUtil.getEntityManager();
    }
    
    // ========== GESTION DES APPAREILS ==========
    
    @Override
    public Appareil ajouterAppareil(Appareil appareil) throws GestionException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            // Vérifier IMEI unique
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM Appareil a WHERE a.imei = :imei", Long.class);
            query.setParameter("imei", appareil.getImei());
            Long count = query.getSingleResult();
            
            if (count > 0) {
                throw new GestionException("IMEI déjà existant: " + appareil.getImei());
            }
            
            em.persist(appareil);
            tx.commit();
            return appareil;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new GestionException("Erreur ajout appareil", e);
        }
    }
    
    @Override
    public Appareil modifierAppareil(Appareil appareil) throws GestionException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Appareil existant = em.find(Appareil.class, appareil.getId());
            if (existant == null) {
                throw new GestionException("Appareil non trouvé ID: " + appareil.getId());
            }
            
            // Vérifier si IMEI modifié
            if (!existant.getImei().equals(appareil.getImei())) {
                TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(a) FROM Appareil a WHERE a.imei = :imei AND a.id != :id", Long.class);
                query.setParameter("imei", appareil.getImei());
                query.setParameter("id", appareil.getId());
                Long count = query.getSingleResult();
                
                if (count > 0) {
                    throw new GestionException("Nouvel IMEI déjà utilisé: " + appareil.getImei());
                }
            }
            
            existant.setImei(appareil.getImei());
            existant.setMarque(appareil.getMarque());
            existant.setModele(appareil.getModele());
            existant.setTypeAppareil(appareil.getTypeAppareil());
            
            Appareil modifie = em.merge(existant);
            tx.commit();
            return modifie;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new GestionException("Erreur modification appareil", e);
        }
    }
    
    @Override
    public boolean supprimerAppareil(Long id) throws GestionException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Appareil appareil = em.find(Appareil.class, id);
            if (appareil == null) {
                throw new GestionException("Appareil non trouvé ID: " + id);
            }
            
            // Vérifier si appareil est dans une réparation
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(r) FROM Reparation r JOIN r.appareils a WHERE a.id = :appareilId", Long.class);
            query.setParameter("appareilId", appareil.getId());
            Long count = query.getSingleResult();
            
            if (count > 0) {
                throw new GestionException("Impossible de supprimer: appareil dans " + count + " réparation(s)");
            }
            
            em.remove(appareil);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new GestionException("Erreur suppression appareil", e);
        }
    }
    
    @Override
    public Appareil rechercherAppareilParId(Long id) {
        try {
            return em.find(Appareil.class, id);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public Appareil rechercherAppareilParIMEI(String imei) {
        try {
            TypedQuery<Appareil> query = em.createQuery(
                "SELECT a FROM Appareil a WHERE a.imei = :imei", Appareil.class);
            query.setParameter("imei", imei);
            return query.getResultStream().findFirst().orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public List<Appareil> listerAppareilsDisponibles() {
        try {
            TypedQuery<Appareil> query = em.createQuery(
                "SELECT a FROM Appareil a WHERE a.etat IN ('DISPONIBLE', 'REPARE') ORDER BY a.dateAjout DESC, a.marque, a.modele", 
                Appareil.class);
            return query.getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }
    /**
     * Liste TOUS les appareils (pas seulement disponibles)
     */
    @Override
    public List<Appareil> listerTousLesAppareils() {
        try {
            TypedQuery<Appareil> query = em.createQuery(
                "SELECT a FROM Appareil a ORDER BY a.marque, a.modele", 
                Appareil.class);
            return query.getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }
    // ========== GESTION DES RÉPARATIONS ==========
    
    @Override
    public Reparation creerReparation(Reparation reparation, UserDAO userConnecte) throws GestionException {
        // Permettre aux réparateurs ET aux propriétaires de créer des réparations
        // Les propriétaires peuvent aussi agir comme réparateurs
        
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            // L'utilisateur connecté est automatiquement le réparateur (peut être ReparateurDAO ou Proprietaire)
            reparation.setReparateur(userConnecte);
            
            em.persist(reparation);
            tx.commit();
            return reparation;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new GestionException("Erreur création réparation", e);
        }
    }
    
    @Override
    public Reparation modifierReparation(Long reparationId, Reparation nouvellesDonnees) throws GestionException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Reparation existant = em.find(Reparation.class, reparationId);
            if (existant == null) {
                throw new GestionException("Réparation non trouvée ID: " + reparationId);
            }
            
            // Vérifier si la réparation peut être modifiée (seulement si ouverte)
            if (existant.getEtat() != EtatReparation.OUVERT) {
                throw new GestionException("Seules les réparations ouvertes peuvent être modifiées");
            }
            
            // Mettre à jour les champs
            if (nouvellesDonnees.getClient() != null) {
                existant.setClient(nouvellesDonnees.getClient());
            }
            if (nouvellesDonnees.getTelephone() != null) {
                existant.setTelephone(nouvellesDonnees.getTelephone());
            }
            if (nouvellesDonnees.getDescription() != null) {
                existant.setDescription(nouvellesDonnees.getDescription());
            }
            if (nouvellesDonnees.getCoutTotal() != null) {
                existant.setCoutTotal(nouvellesDonnees.getCoutTotal());
            }
            if (nouvellesDonnees.getAvance() != null) {
                existant.setAvance(nouvellesDonnees.getAvance());
            }
            if (nouvellesDonnees.getReste() != null) {
                existant.setReste(nouvellesDonnees.getReste());
            }
            if (nouvellesDonnees.getPhotoPath() != null) {
                existant.setPhotoPath(nouvellesDonnees.getPhotoPath());
            }
            
            Reparation modifie = em.merge(existant);
            tx.commit();
            return modifie;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new GestionException("Erreur modification réparation", e);
        }
    }
    
    @Override
    public boolean supprimerReparation(Long reparationId) throws GestionException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Reparation reparation = em.find(Reparation.class, reparationId);
            if (reparation == null) {
                throw new GestionException("Réparation non trouvée ID: " + reparationId);
            }
            
            // Vérifier si la réparation peut être supprimée (seulement si ouverte)
            if (reparation.getEtat() != EtatReparation.OUVERT) {
                throw new GestionException("Seules les réparations ouvertes peuvent être supprimées");
            }
            
            // Pour soft delete: on change l'état en ANNULE et on garde en base
            reparation.setEtat(EtatReparation.ANNULE);
            reparation.setDateFin(new Date());
            
            // Remettre les appareils disponibles
            if (reparation.getAppareils() != null) {
                for (Appareil appareil : reparation.getAppareils()) {
                    appareil.setEtat(EtatAppareil.DISPONIBLE);
                    em.merge(appareil);
                }
            }
            
            em.merge(reparation);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new GestionException("Erreur suppression réparation", e);
        }
    }
    
    @Override
    public Reparation rechercherReparationParId(Long reparationId) {
        try {
            // Clear cache to ensure we get fresh data from database
            em.clear();
            return em.find(Reparation.class, reparationId);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public Reparation rechercherReparationParNumero(String numero) {
        try {
            TypedQuery<Reparation> query = em.createQuery(
                "SELECT r FROM Reparation r WHERE r.numero = :numero", Reparation.class);
            query.setParameter("numero", numero);
            return query.getResultStream().findFirst().orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public List<Reparation> listerMesReparations(UserDAO userConnecte) {
        try {
            // Clear cache to ensure we get fresh data from database
            em.clear();
            TypedQuery<Reparation> query = em.createQuery(
                "SELECT r FROM Reparation r WHERE r.reparateur.id = :userId AND r.etat != 'ANNULE' ORDER BY r.dateCreation DESC", 
                Reparation.class);
            query.setParameter("userId", userConnecte.getId());
            return query.getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Reparation> listerToutesLesReparations() {
        try {
            // Clear cache to ensure we get fresh data from database
            em.clear();
            TypedQuery<Reparation> query = em.createQuery(
                "SELECT r FROM Reparation r WHERE r.etat != 'ANNULE' ORDER BY r.dateCreation DESC", 
                Reparation.class);
            return query.getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }
    
    // ========== CHANGEMENT D'ÉTAT ==========
    
    @Override
    public Reparation demarrerReparation(Long reparationId) throws GestionException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Reparation reparation = em.find(Reparation.class, reparationId);
            if (reparation == null) {
                throw new GestionException("Réparation non trouvée");
            }
            
            // Vérifier que la réparation est bien ouverte
            if (reparation.getEtat() != EtatReparation.OUVERT) {
                throw new GestionException("Seules les réparations ouvertes peuvent démarrer");
            }
            
            // Changer l'état de la réparation à EN_COURS
            reparation.setEtat(EtatReparation.EN_COURS);
            
            // IMPORTANT: Mettre à jour l'état des appareils à EN_REPARATION
            if (reparation.getAppareils() != null) {
                for (Appareil appareil : reparation.getAppareils()) {
                    appareil.setEtat(EtatAppareil.EN_REPARATION);
                    em.merge(appareil);
                    System.out.println("✓ Appareil " + appareil.getImei() + " → EN_REPARATION");
                }
            }
            
            Reparation modifie = em.merge(reparation);
            tx.commit();
            
            System.out.println("✓ Réparation " + reparation.getNumero() + " démarrée");
            
            return modifie;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new GestionException("Erreur démarrage réparation: " + e.getMessage(), e);
        }
    }
    
    //Les appareils passent par REPARE puis redeviennent DISPONIBLES
    @Override
    public Reparation terminerReparation(Long reparationId, String diagnostic, 
                                       String piecesUtilisees, Double coutTotal) throws GestionException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Reparation reparation = em.find(Reparation.class, reparationId);
            if (reparation == null) {
                throw new GestionException("Réparation non trouvée");
            }
            
            if (reparation.getEtat() != EtatReparation.EN_COURS) {
                throw new GestionException("Seules les réparations en cours peuvent être terminées");
            }
            
            // Mettre à jour les informations de la réparation
            reparation.setDescription(diagnostic);
            reparation.setPiecesUtilisees(piecesUtilisees);
            reparation.setCoutTotal(coutTotal);
            
            // Calculer le reste à payer si nécessaire
            Double avance = reparation.getAvance() != null ? reparation.getAvance() : 0.0;
            Double reste = Math.max(0.0, coutTotal - avance);
            reparation.setReste(reste);
            
            reparation.setDateFin(new Date());
            reparation.setEtat(EtatReparation.TERMINE);
            
            System.out.println("💰 Coût total défini: " + coutTotal + " DH pour réparation " + reparation.getNumero());
            System.out.println("💰 Avance: " + avance + " DH, Reste: " + reste + " DH");
            
            // IMPORTANT: Les appareils passent à l'état REPARE
            // Ils pourront être réutilisés pour d'autres réparations
            if (reparation.getAppareils() != null) {
                for (Appareil appareil : reparation.getAppareils()) {
                    appareil.setEtat(EtatAppareil.REPARE);
                    em.merge(appareil);
                    System.out.println("✓ Appareil " + appareil.getImei() + " → REPARE");
                }
            }
            
            Reparation modifie = em.merge(reparation);
            em.flush(); // Forcer l'écriture en base avant le commit
            tx.commit();
            
            // Vérifier que le coût total a bien été sauvegardé
            em.clear(); // Clear cache pour forcer le rechargement
            Reparation verif = em.find(Reparation.class, reparation.getId());
            if (verif != null) {
                System.out.println("✅ Vérification: Coût total sauvegardé = " + verif.getCoutTotal() + " DH");
            }
            
            // Enregistrer le paiement dans la caisse du réparateur
            if (reparation.getReparateur() != null && reparation.getReparateur().getCaisse() != null) {
                try {
                    GestionCaisses gestionCaisses = new GestionCaisses();
                    // Le montant à encaisser est le coût total de la réparation
                    // (le réparateur gagne le montant total, même si le client n'a pas encore tout payé)
                    Double coutTotalRep = coutTotal != null ? coutTotal : 0.0;
                    
                    System.out.println("💰 Calcul encaissement pour réparation " + reparation.getNumero() + ":");
                    System.out.println("   - Coût total: " + coutTotalRep + " DH");
                    System.out.println("   - Avance: " + avance + " DH");
                    System.out.println("   - Reste à payer: " + reste + " DH");
                    System.out.println("   - Montant à encaisser: " + coutTotalRep + " DH");
                    
                    if (coutTotalRep > 0) {
                        gestionCaisses.encaisserPaiement(
                            reparation.getReparateur().getId().intValue(),
                            coutTotalRep,
                            "Paiement réparation " + reparation.getNumero()
                        );
                        System.out.println("✅ Montant encaissé avec succès dans la caisse du réparateur");
                    } else {
                        System.out.println("⚠️ Coût total est 0, aucun encaissement effectué");
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Erreur lors de l'enregistrement du paiement dans la caisse: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("⚠️ Réparateur ou caisse non trouvée pour la réparation " + reparation.getNumero());
            }
            
            System.out.println("✓ Réparation " + reparation.getNumero() + " terminée");
            
            return modifie;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new GestionException("Erreur fin réparation: " + e.getMessage(), e);
        }
    }
    
    
    
    @Override
    public Reparation annulerReparation(Long reparationId, String raison) throws GestionException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Reparation reparation = em.find(Reparation.class, reparationId);
            if (reparation == null) {
                throw new GestionException("Réparation non trouvée");
            }
            
            // Changer l'état de la réparation
            reparation.setEtat(EtatReparation.ANNULE);
            reparation.setDateFin(new Date());
            reparation.setDescription(raison);
            
            // IMPORTANT: Les appareils redeviennent DISPONIBLES (pas REPARE car non réparés)
            if (reparation.getAppareils() != null) {
                for (Appareil appareil : reparation.getAppareils()) {
                    appareil.setEtat(EtatAppareil.DISPONIBLE);
                    em.merge(appareil);
                    System.out.println("✓ Appareil " + appareil.getImei() + " → DISPONIBLE");
                }
            }
            
            Reparation modifie = em.merge(reparation);
            tx.commit();
            
            System.out.println("✓ Réparation " + reparation.getNumero() + " annulée");
            
            return modifie;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new GestionException("Erreur annulation réparation: " + e.getMessage(), e);
        }
    }
    
    // ========== GESTION DES APPAREILS DANS RÉPARATION ==========
    
    @Override
    public Reparation ajouterAppareilAReparation(Long reparationId, Long appareilId) throws GestionException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            // Recharger la réparation depuis la base
            Reparation reparation = em.find(Reparation.class, reparationId);
            if (reparation == null) {
                throw new GestionException("Réparation non trouvée");
            }
            
            // Recharger l'appareil depuis la base
            Appareil appareil = em.find(Appareil.class, appareilId);
            if (appareil == null) {
                throw new GestionException("Appareil non trouvé");
            }
            
            // Vérifier si la réparation est modifiable (ouverte ou en cours)
            if (reparation.getEtat() != EtatReparation.OUVERT && reparation.getEtat() != EtatReparation.EN_COURS) {
                throw new GestionException("Seules les réparations ouvertes ou en cours peuvent être modifiées");
            }
            
            // IMPORTANT: Autoriser DISPONIBLE ET REPARE (un appareil réparé peut être réutilisé)
            if (appareil.getEtat() != EtatAppareil.DISPONIBLE && appareil.getEtat() != EtatAppareil.REPARE) {
                throw new GestionException("Appareil non disponible. État actuel: " + appareil.getEtat());
            }
            
            // Initialiser la liste d'appareils si null
            if (reparation.getAppareils() == null) {
                reparation.setAppareils(new java.util.ArrayList<>());
            }
            
            // Vérifier si appareil déjà dans cette réparation
            boolean dejaPresent = false;
            for (Appareil a : reparation.getAppareils()) {
                if (a.getId().equals(appareil.getId())) {
                    dejaPresent = true;
                    break;
                }
            }
            
            if (dejaPresent) {
                throw new GestionException("Cet appareil est déjà dans cette réparation");
            }
            
            // Ajouter l'appareil à la réparation
            reparation.getAppareils().add(appareil);
            
            // Changer l'état de l'appareil à EN_REPARATION
            appareil.setEtat(EtatAppareil.EN_REPARATION);
            em.merge(appareil);
            
            // Sauvegarder la réparation
            Reparation modifie = em.merge(reparation);
            tx.commit();
            
            System.out.println("✓ Appareil " + appareil.getImei() + " ajouté à la réparation " + reparation.getNumero());
            
            return modifie;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new GestionException("Erreur ajout appareil à réparation: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Reparation retirerAppareilDeReparation(Long reparationId, Long appareilId) throws GestionException {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Reparation reparation = em.find(Reparation.class, reparationId);
            if (reparation == null) {
                throw new GestionException("Réparation non trouvée");
            }
            
            Appareil appareil = em.find(Appareil.class, appareilId);
            if (appareil == null) {
                throw new GestionException("Appareil non trouvé");
            }
            
            // Vérifier si la réparation est modifiable (ouverte ou en cours)
            if (reparation.getEtat() != EtatReparation.OUVERT && reparation.getEtat() != EtatReparation.EN_COURS) {
                throw new GestionException("Seules les réparations ouvertes ou en cours peuvent être modifiées");
            }
            
            // Vérifier si appareil est dans la réparation
            if (!reparation.getAppareils().contains(appareil)) {
                throw new GestionException("Cet appareil n'est pas dans cette réparation");
            }
            
            // Retirer l'appareil
            reparation.getAppareils().remove(appareil);
            
            // IMPORTANT: L'appareil redevient DISPONIBLE (ou reste REPARE s'il l'était avant)
            // Pour simplifier, on remet toujours DISPONIBLE
            appareil.setEtat(EtatAppareil.DISPONIBLE);
            em.merge(appareil);
            
            Reparation modifie = em.merge(reparation);
            tx.commit();
            
            System.out.println("✓ Appareil " + appareil.getImei() + " retiré de la réparation");
            
            return modifie;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new GestionException("Erreur retrait appareil de réparation: " + e.getMessage(), e);
        }
    }
    
    // ========== STATISTIQUES POUR L'UTILISATEUR ==========
    
    @Override
    public long compterMesReparations(UserDAO userConnecte) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(r) FROM Reparation r WHERE r.reparateur.id = :userId", Long.class);
            query.setParameter("userId", userConnecte.getId());
            return query.getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    public long compterMesReparationsParEtat(UserDAO userConnecte, EtatReparation etat) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(r) FROM Reparation r WHERE r.reparateur.id = :userId AND r.etat = :etat", Long.class);
            query.setParameter("userId", userConnecte.getId());
            query.setParameter("etat", etat);
            return query.getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
    public void close() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}