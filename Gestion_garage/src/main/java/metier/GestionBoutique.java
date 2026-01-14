package metier;
import java.util.List;

import javax.persistence.EntityManager;

import dao.BoutiqueDAO;
import dao.ReparateurDAO;

public class GestionBoutique implements IGestionBoutique {

	
	private EntityManager em;
	public GestionBoutique() {
		 
		this.em = JPAUtil.getEntityManager();
		
	}
	
	@Override
	public void ajouterBoutique(BoutiqueDAO boutique) {
		// TODO Auto-generated method stub
		try {
            em.getTransaction().begin();

            em.persist(boutique);

            em.getTransaction().commit();
            System.out.println("✅ Boutique créé avec succès");
        } catch (Exception e) {
        	if (em.getTransaction().isActive()) {
        		em.getTransaction().rollback();
        	}
            System.err.println("❌ Erreur lors de la création de la boutique : " + e.getMessage());
            
        }
		
		
		
	}

	

	@Override
	public List<BoutiqueDAO> afficherBoutique() {
		
		// TODO Auto-generated method stub
		
		List<BoutiqueDAO> boutiques = null;
		try {
			em.getTransaction().begin();

			boutiques = em.createQuery("SELECT b FROM BoutiqueDAO b", BoutiqueDAO.class).getResultList();

			em.getTransaction().commit();
			System.out.println("✅ Boutiques récupérées avec succès");
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			System.err.println("❌ Erreur lors de la récupération des boutiques : " + e.getMessage());
		}
		return boutiques;
		
	}

	@Override
	public void supprimerBoutique(int idBoutique) {
		// TODO Auto-generated method stub
		
		try {
			em.getTransaction().begin();

			BoutiqueDAO b = em.find(BoutiqueDAO.class, idBoutique);
			if (b != null) {
				em.remove(b);
			}

			em.getTransaction().commit();
			System.out.println("✅ Boutique supprimée avec succès");
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			System.err.println("❌ Erreur lors de la suppression de la boutique : " + e.getMessage());
		}
		
	}


	@Override
	public void removeReparateurBoutique(ReparateurDAO reparateur, int idBoutique) {
		// TODO Auto-generated method stub
		try {
            em.getTransaction().begin();

            ReparateurDAO r = em.find(ReparateurDAO.class, reparateur.getId());
            if (r != null) {
            		// je veux pas le supprimer de la base mais juste le dissocier de la boutique
                
            		BoutiqueDAO b = em.find(BoutiqueDAO.class, idBoutique);
				if (b != null) {
					b.getReparateurs().remove(r);
				}
            }

            em.getTransaction().commit();
            System.out.println("✅ Réparateur supprimé avec succès");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
            	em.getTransaction().rollback();
            }
            System.err.println("❌ Erreur lors de la suppression du réparateur : " + e.getMessage() );
        }
		
	}

	@Override
	public List ListReparateurBoutique(int idBoutique) {
		
		// TODO Auto-generated method stub
		
		try {
			em.getTransaction().begin();

			BoutiqueDAO b = em.find(BoutiqueDAO.class, idBoutique);
			if (b != null) {
				List<ReparateurDAO> reparateurs = b.getReparateurs();
				em.getTransaction().commit();
				System.out.println("✅ Réparateurs récupérés avec succès");
				return reparateurs;
			} else {
				em.getTransaction().commit();
				System.out.println("❌ Boutique non trouvée");
				return null;
			}
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			System.err.println("❌ Erreur lors de la récupération des réparateurs : " + e.getMessage());
			return null;
		}
		
	}

}
