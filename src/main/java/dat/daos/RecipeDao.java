package dat.daos;

import dat.entities.Recipe;
import jakarta.persistence.EntityManagerFactory;
import java.util.*;

public class RecipeDao {
    private final EntityManagerFactory emf;
    public RecipeDao(EntityManagerFactory emf) { this.emf = emf; }

    public Recipe save(Recipe r) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (r.getId()==null) em.persist(r); else r = em.merge(r);
            em.getTransaction().commit();
            return r;
        } finally { em.close(); }
    }

    public Optional<Recipe> findById(Long id) {
        var em = emf.createEntityManager();
        try { return Optional.ofNullable(em.find(Recipe.class, id)); }
        finally { em.close(); }
    }

    public void delete(Recipe r) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.merge(r));
            em.getTransaction().commit();
        } finally { em.close(); }
    }

    public List<Recipe> list(int limit, int offset) {
        var em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Recipe r ORDER BY r.id", Recipe.class)
                    .setMaxResults(limit).setFirstResult(offset).getResultList();
        } finally { em.close(); }
    }

    public long count() {
        var em = emf.createEntityManager();
        try { return em.createQuery("SELECT COUNT(r) FROM Recipe r", Long.class).getSingleResult(); }
        finally { em.close(); }
    }
}
