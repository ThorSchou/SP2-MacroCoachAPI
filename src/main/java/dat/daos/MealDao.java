package dat.daos;

import dat.entities.Meal;
import jakarta.persistence.EntityManagerFactory;
import java.util.Optional;

public class MealDao {
    private final EntityManagerFactory emf;
    public MealDao(EntityManagerFactory emf) { this.emf = emf; }

    public Meal save(Meal m) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (m.getId()==null) em.persist(m); else m = em.merge(m);
            em.getTransaction().commit();
            return m;
        } finally { em.close(); }
    }

    public Optional<Meal> findById(Long id) {
        var em = emf.createEntityManager();
        try { return Optional.ofNullable(em.find(Meal.class, id)); }
        finally { em.close(); }
    }

    public void delete(Meal m) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.merge(m));
            em.getTransaction().commit();
        } finally { em.close(); }
    }
}
