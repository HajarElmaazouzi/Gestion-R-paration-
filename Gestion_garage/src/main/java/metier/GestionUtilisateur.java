package metier;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import dao.Appareil;
import dao.CaisseDAO;
import dao.EtatAppareil;
import dao.Proprietaire;
import dao.Reparation;
import dao.ReparateurDAO;
import dao.UserDAO;

public class GestionUtilisateur implements IGestionUser {




	private EntityManager em ; 
	public GestionUtilisateur() {
		 this.em = JPAUtil.getEntityManager();
	}


//try catch 
	@Override
	public void CreerReparateur(ReparateurDAO reparateur) {
		// TODO Auto-generated method stub

		 try {
	            em.getTransaction().begin();
	            
	            // If caisse exists, find it by ID if already persisted, or persist if new
	            if (reparateur.getCaisse() != null) {
	                if (reparateur.getCaisse().getId() != null) {
	                    // Caisse already persisted, merge it to attach to current EntityManager
	                    // Using merge instead of find to handle detached entities from other EntityManagers
	                    CaisseDAO managedCaisse = em.merge(reparateur.getCaisse());
	                    em.flush(); // Flush after merge to ensure caisse is persisted
	                    reparateur.setCaisse(managedCaisse);
	                } else {
	                    // New caisse, persist it
	                    em.persist(reparateur.getCaisse());
	                    em.flush(); // Flush after persist to ensure caisse is persisted
	                }
	            }
	            
	            // Handle boutique if it exists (it might be detached from another EntityManager)
	            if (reparateur.getBoutique() != null) {
	                int boutiqueId = reparateur.getBoutique().getId();
	                if (boutiqueId > 0) {
	                    // Boutique already persisted, merge it to attach to current EntityManager
	                    dao.BoutiqueDAO managedBoutique = em.merge(reparateur.getBoutique());
	                    em.flush(); // Flush after merge to ensure boutique is persisted
	                    reparateur.setBoutique(managedBoutique);
	                } else {
	                    // New boutique, persist it
	                    em.persist(reparateur.getBoutique());
	                    em.flush(); // Flush after persist to ensure boutique is persisted
	                }
	            }

	            // Get password before persist to debug
	            String passwordBeforePersist = reparateur.getPassword();
	            String usernameBeforePersist = reparateur.getUsername();
	            System.out.println("🔍 Avant persist - Username: '" + usernameBeforePersist + "', Password: '" + passwordBeforePersist + "' (length: " + (passwordBeforePersist != null ? passwordBeforePersist.length() : 0) + ")");
	            System.out.println("🔍 Avant persist - Caisse ID: " + (reparateur.getCaisse() != null ? reparateur.getCaisse().getId() : "null"));
	            System.out.println("🔍 Avant persist - Boutique ID: " + (reparateur.getBoutique() != null ? String.valueOf(reparateur.getBoutique().getId()) : "null"));
	            
	            em.persist(reparateur);
	            em.flush(); // Force flush to ensure reparateur is immediately available for queries
	            
	            // Get the ID before commit to verify later
	            Long reparateurId = reparateur.getId();
	            System.out.println("🔍 Après persist - Réparateur ID généré: " + reparateurId);
	            
	            if (reparateurId == null) {
	                System.err.println("❌ ERREUR: L'ID du réparateur est null après persist!");
	                if (em.getTransaction().isActive()) {
	                    em.getTransaction().rollback();
	                }
	                return;
	            }
	            
	            em.getTransaction().commit();
	            System.out.println("✅ Transaction commitée avec succès");
	            
	            // Debug: Verify what was saved
	            em.clear(); // Clear cache to get fresh data
	            ReparateurDAO saved = em.find(ReparateurDAO.class, reparateurId);
	            if (saved != null) {
	                System.out.println("✅ Réparateur créé avec succès (ID: " + reparateurId + ", Username: " + usernameBeforePersist + ")");
	                System.out.println("🔍 Password sauvegardé: '" + saved.getPassword() + "' (length: " + (saved.getPassword() != null ? saved.getPassword().length() : 0) + ")");
	                System.out.println("🔍 Vérification dans DB: userExists('" + usernameBeforePersist + "') = " + userExists(usernameBeforePersist));
	                
	                // Try to login immediately to verify
	                UserDAO loginTest = SeConnecter(usernameBeforePersist, passwordBeforePersist);
	                if (loginTest != null) {
	                    System.out.println("✅ Test de connexion immédiat: SUCCÈS");
	                } else {
	                    System.out.println("❌ Test de connexion immédiat: ÉCHEC");
	                    System.out.println("❌ Password utilisé pour test: '" + passwordBeforePersist + "'");
	                }
	            } else {
	                System.err.println("⚠️ Réparateur créé mais non trouvé après commit (ID: " + reparateurId + ", Username: " + usernameBeforePersist + ")");
	                System.err.println("⚠️ Vérification dans DB: userExists('" + usernameBeforePersist + "') = " + userExists(usernameBeforePersist));
	            }
	        } catch (Exception e) {
	            if (em.getTransaction().isActive()) {
	                em.getTransaction().rollback();
	            }
	            System.err.println("❌ Erreur lors de la création du réparateur : " + e.getMessage());
	            e.printStackTrace();
	        } finally {
	            //em.close();
	        }
	}

	@Override
	public void ModifierReparateur(int id, ReparateurDAO reparateur) {
		// TODO Auto-generated method stub
		 try {
	            em.getTransaction().begin();

	            ReparateurDAO r = em.find(ReparateurDAO.class, (long)id);
	            if (r != null) {
	                r.setUsername(reparateur.getUsername());
	                r.setPassword(reparateur.getPassword());
	                r.setPourcentage(reparateur.getPourcentage());
	                r.setCaisse(reparateur.getCaisse());
	                em.merge(r);
	            }

	            em.getTransaction().commit();
	            System.out.println("✅ Réparateur modifié avec succès");
	        } catch (Exception e) {
	            if (em.getTransaction().isActive()) {
	                em.getTransaction().rollback();
	            }
	            System.err.println("❌ Erreur lors de la modification du réparateur : " + e.getMessage());
	            e.printStackTrace();
	        } finally {
	            //em.close();
	        }


	}

	@Override
	public void SupprimerReparateur(int idReparateur) {
		// TODO Auto-generated method stub

		try {
            em.getTransaction().begin();

            ReparateurDAO r = em.find(ReparateurDAO.class, (long)idReparateur);
            if (r == null) {
                System.err.println("❌ Réparateur non trouvé avec ID: " + idReparateur);
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                return;
            }
            
            System.out.println("🔍 Suppression du réparateur ID: " + idReparateur + ", Username: " + r.getUsername());
            
            // Charger explicitement les réparations associées avec une requête
            List<Reparation> reparations = em.createQuery(
                    "SELECT r FROM Reparation r WHERE r.reparateur.id = :reparateurId",
                    Reparation.class)
                    .setParameter("reparateurId", (long)idReparateur)
                    .getResultList();
            
            // Vérifier et gérer les réparations associées
            if (reparations != null && !reparations.isEmpty()) {
                System.out.println("⚠️ Le réparateur a " + reparations.size() + " réparation(s) associée(s)");
                
                // Supprimer toutes les réparations associées
                // Note: Cela supprime les réparations, mais permet la suppression du réparateur
                for (Reparation reparation : reparations) {
                    System.out.println("🔍 Suppression de la réparation ID: " + reparation.getId());
                    // Remettre les appareils disponibles
                    if (reparation.getAppareils() != null) {
                        for (Appareil appareil : reparation.getAppareils()) {
                            appareil.setEtat(EtatAppareil.DISPONIBLE);
                            em.merge(appareil);
                        }
                    }
                    em.remove(reparation);
                }
                em.flush(); // Flush pour s'assurer que les réparations sont supprimées
            }
            
            // Supprimer le réparateur
            em.remove(r);
            em.flush(); // Flush avant commit pour voir les erreurs plus tôt
            
            em.getTransaction().commit();
            System.out.println("✅ Réparateur supprimé avec succès (ID: " + idReparateur + ")");
            
            // Vérifier que la suppression a fonctionné
            em.clear();
            ReparateurDAO verify = em.find(ReparateurDAO.class, (long)idReparateur);
            if (verify == null) {
                System.out.println("✅ Vérification: Réparateur bien supprimé de la DB");
            } else {
                System.err.println("⚠️ Vérification: Réparateur toujours présent dans la DB!");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("❌ Erreur lors de la suppression du réparateur : " + e.getMessage());
            e.printStackTrace();
        } finally {
            //em.close();
        }

	}


	@Override
	public List<ReparateurDAO> ListerReparateurs() {
		// TODO Auto-generated method stub
		 List<ReparateurDAO> list = null;

	        try {
	            list = em.createQuery("SELECT r FROM ReparateurDAO r", ReparateurDAO.class)
	                     .getResultList();
	        } catch (Exception e) {
	            System.err.println("❌ Erreur lors du chargement des réparateurs : " + e.getMessage());
	            e.printStackTrace();
	        } finally {
	            //em.close();
	        }
		return list;


	}

	@Override
	public UserDAO SeConnecter(String username, String password) {
		// TODO Auto-generated method stub
		UserDAO user = null;

        try {
            // Clear any cached entities to ensure we get fresh data from database
            em.clear();
            
            System.out.println("🔍 Tentative de connexion avec username='" + username + "', password='" + password + "'");
            
            // Try to find user directly in each table (avoiding polymorphic query issues with TABLE_PER_CLASS)
            // First try ReparateurDAO
            try {
                ReparateurDAO reparateur = em.createQuery(
                        "SELECT r FROM ReparateurDAO r WHERE r.username = :u AND r.password = :p",
                        ReparateurDAO.class)
                        .setParameter("u", username)
                        .setParameter("p", password)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                
                if (reparateur != null) {
                    System.out.println("✅ Connexion réussie : " + username + " (Type: ReparateurDAO)");
                    return reparateur;
                }
            } catch (Exception e) {
                System.out.println("⚠️ Erreur lors de la recherche dans ReparateurDAO: " + e.getMessage());
            }
            
            // Then try Proprietaire
            try {
                Proprietaire proprietaire = em.createQuery(
                        "SELECT p FROM Proprietaire p WHERE p.username = :u AND p.password = :p",
                        Proprietaire.class)
                        .setParameter("u", username)
                        .setParameter("p", password)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                
                if (proprietaire != null) {
                    System.out.println("✅ Connexion réussie : " + username + " (Type: Proprietaire)");
                    return proprietaire;
                }
            } catch (Exception e) {
                System.out.println("⚠️ Erreur lors de la recherche dans Proprietaire: " + e.getMessage());
            }
            
            // Finally try UserDAO (base class)
            try {
                user = em.createQuery(
                        "SELECT u FROM UserDAO u WHERE u.username = :u AND u.password = :p",
                        UserDAO.class)
                        .setParameter("u", username)
                        .setParameter("p", password)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);
                
                if (user != null) {
                    System.out.println("✅ Connexion réussie : " + username + " (Type: " + user.getClass().getSimpleName() + ")");
                    return user;
                }
            } catch (Exception e) {
                System.out.println("⚠️ Erreur lors de la recherche dans UserDAO: " + e.getMessage());
            }
            
            // If still not found, try finding by username only to debug and compare passwords manually
            System.out.println("🔍 User non trouvé avec les identifiants fournis. Recherche par username uniquement...");
            
            // Try ReparateurDAO first - use native query to avoid any JPQL issues
            try {
                List<ReparateurDAO> reparateursList = em.createQuery(
                        "SELECT r FROM ReparateurDAO r WHERE r.username = :u",
                        ReparateurDAO.class)
                        .setParameter("u", username)
                        .getResultList();
                
                System.out.println("🔍 Nombre de réparateurs trouvés par username: " + reparateursList.size());
                
                if (!reparateursList.isEmpty()) {
                    ReparateurDAO reparateurByUsername = reparateursList.get(0);
                    // Force refresh to get latest data from database
                    em.refresh(reparateurByUsername);
                    
                    String storedPassword = reparateurByUsername.getPassword();
                    System.out.println("🔍 Reparateur trouvé: " + username + " (Type: ReparateurDAO, ID: " + reparateurByUsername.getId() + ")");
                    System.out.println("🔍 Password stocké: '" + storedPassword + "' (length: " + (storedPassword != null ? storedPassword.length() : 0) + ")");
                    System.out.println("🔍 Password fourni: '" + password + "' (length: " + (password != null ? password.length() : 0) + ")");
                    
                    // Debug: Check character by character if they don't match
                    boolean passwordsMatch = storedPassword != null && storedPassword.equals(password);
                    System.out.println("🔍 Passwords match: " + passwordsMatch);
                    
                    if (!passwordsMatch && storedPassword != null) {
                        System.out.println("🔍 Comparaison caractère par caractère:");
                        int minLen = Math.min(storedPassword.length(), password.length());
                        for (int i = 0; i < minLen; i++) {
                            if (storedPassword.charAt(i) != password.charAt(i)) {
                                System.out.println("  Différence à l'index " + i + ": stocké='" + storedPassword.charAt(i) + "' (" + (int)storedPassword.charAt(i) + "), fourni='" + password.charAt(i) + "' (" + (int)password.charAt(i) + ")");
                            }
                        }
                        if (storedPassword.length() != password.length()) {
                            System.out.println("  Longueurs différentes: stocké=" + storedPassword.length() + ", fourni=" + password.length());
                        }
                    }
                    
                    // Try direct comparison
                    if (passwordsMatch) {
                        System.out.println("✅ Comparaison directe réussie - utilisation du réparateur trouvé");
                        return reparateurByUsername;
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ Erreur lors de la recherche de réparateur par username: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Try Proprietaire
            try {
                List<Proprietaire> proprietairesList = em.createQuery(
                        "SELECT p FROM Proprietaire p WHERE p.username = :u",
                        Proprietaire.class)
                        .setParameter("u", username)
                        .getResultList();
                
                System.out.println("🔍 Nombre de propriétaires trouvés par username: " + proprietairesList.size());
                
                if (!proprietairesList.isEmpty()) {
                    Proprietaire proprietaireByUsername = proprietairesList.get(0);
                    // Force refresh to get latest data from database
                    em.refresh(proprietaireByUsername);
                    
                    String storedPassword = proprietaireByUsername.getPassword();
                    System.out.println("🔍 Proprietaire trouvé: " + username + " (ID: " + proprietaireByUsername.getId() + ")");
                    System.out.println("🔍 Password stocké: '" + storedPassword + "' (length: " + (storedPassword != null ? storedPassword.length() : 0) + ")");
                    System.out.println("🔍 Password fourni: '" + password + "' (length: " + (password != null ? password.length() : 0) + ")");
                    
                    boolean passwordsMatch = storedPassword != null && storedPassword.equals(password);
                    System.out.println("🔍 Passwords match: " + passwordsMatch);
                    
                    if (passwordsMatch) {
                        System.out.println("✅ Comparaison directe réussie - utilisation du proprietaire trouvé");
                        return proprietaireByUsername;
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ Erreur lors de la recherche de proprietaire par username: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("❌ Identifiants incorrects pour: " + username);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la connexion : " + e.getMessage());
            e.printStackTrace();
        } finally {
            //em.close();
        }
        
        return user;
        
	}
	
	public void CreerUser(UserDAO user) {
		// TODO Auto-generated method stub

		 try {
	            em.getTransaction().begin();
	            
	            // If caisse exists, find it by ID if already persisted, or persist if new
	            if (user.getCaisse() != null) {
	                if (user.getCaisse().getId() != null) {
	                    // Caisse already persisted, merge it to attach to current EntityManager
	                    // Using merge instead of find to handle detached entities from other EntityManagers
	                    CaisseDAO managedCaisse = em.merge(user.getCaisse());
	                    em.flush(); // Flush after merge to ensure caisse is persisted
	                    user.setCaisse(managedCaisse);
	                } else {
	                    // New caisse, persist it
	                    em.persist(user.getCaisse());
	                    em.flush(); // Flush after persist to ensure caisse is persisted
	                }
	            }

	            em.persist(user);
	            em.flush(); // Force flush to ensure user is immediately available for queries
	            
	            // Get the ID before commit to verify later
	            Long userId = user.getId();
	            String username = user.getUsername();
	            
	            em.getTransaction().commit();
	            
	            // Debug: Verify what was saved
	            em.clear(); // Clear cache to get fresh data
	            UserDAO saved = em.find(user.getClass(), userId);
	            if (saved != null) {
	                System.out.println("✅ Utilisateur créé avec succès (ID: " + userId + ", Type: " + user.getClass().getSimpleName() + ", Username: " + username + ")");
	                System.out.println("🔍 Password sauvegardé: '" + saved.getPassword() + "' (length: " + (saved.getPassword() != null ? saved.getPassword().length() : 0) + ")");
	                System.out.println("🔍 Vérification dans DB: userExists('" + username + "') = " + userExists(username));
	            } else {
	                System.err.println("⚠️ Utilisateur créé mais non trouvé après commit (ID: " + userId + ", Username: " + username + ")");
	                System.err.println("⚠️ Vérification dans DB: userExists('" + username + "') = " + userExists(username));
	            }
	        } catch (Exception e) {
	            if (em.getTransaction().isActive()) {
	                em.getTransaction().rollback();
	            }
	            System.err.println("❌ Erreur lors de la création de l'utilisateur : " + e.getMessage());
	            System.err.println("❌ Type d'utilisateur: " + (user != null ? user.getClass().getSimpleName() : "null"));
	            System.err.println("❌ Username: " + (user != null ? user.getUsername() : "null"));
	            e.printStackTrace();
	        } finally {
	            //em.close();
	        }
	}
	
	/**
	 * Vérifie si un utilisateur existe déjà avec ce username
	 */
	public boolean userExists(String username) {
		try {
			// Clear cache to ensure we get fresh data from database
			em.clear();
			
			// Try to find user directly in each table (avoiding polymorphic query issues with TABLE_PER_CLASS)
			// First try ReparateurDAO
			ReparateurDAO reparateur = em.createQuery(
					"SELECT r FROM ReparateurDAO r WHERE r.username = :u",
					ReparateurDAO.class)
					.setParameter("u", username)
					.getResultStream()
					.findFirst()
					.orElse(null);
			
			if (reparateur != null) {
				return true;
			}
			
			// Then try Proprietaire
			Proprietaire proprietaire = em.createQuery(
					"SELECT p FROM Proprietaire p WHERE p.username = :u",
					Proprietaire.class)
					.setParameter("u", username)
					.getResultStream()
					.findFirst()
					.orElse(null);
			
			if (proprietaire != null) {
				return true;
			}
			
			// Finally try UserDAO (base class)
			UserDAO user = em.createQuery(
					"SELECT u FROM UserDAO u WHERE u.username = :u",
					UserDAO.class)
					.setParameter("u", username)
					.getResultStream()
					.findFirst()
					.orElse(null);
			
			return user != null;
		} catch (Exception e) {
			System.err.println("❌ Erreur lors de la vérification de l'utilisateur : " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}



	

}


