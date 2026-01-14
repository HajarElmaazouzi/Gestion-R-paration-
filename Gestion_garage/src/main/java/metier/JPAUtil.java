package metier;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {
    private static final String PERSISTENCE_UNIT_NAME = "StockApp";
    private static EntityManagerFactory factory;
    
    static {
        try {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            System.out.println("✅ Connexion établie avec la base de données: stockApp");
        } catch (Exception e) {
            System.err.println("❌ Erreur de connexion à la base stockApp: " + e.getMessage());
            throw new RuntimeException("Impossible de se connecter à la base de données stockApp", e);
        }
    }
    
    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }
    
    public static void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
            System.out.println("Connexion à stockApp fermée");
        }
    }
}
