package app.daos;

import app.entities.Meal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class MealDao {
    private final EntityManagerFactory emf;
    public MealDao(EntityManagerFactory emf) { this.emf = emf; }

    public Meal find(long id) {
        EntityManager em = emf.createEntityManager();
        try { return em.find(Meal.class, id); }
        finally { em.close(); }
    }
}
