package dat.daos;

import dat.config.HibernateConfig;
import dat.entities.Recipe;
import dat.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class RecipeDao {
    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public long count() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(r) FROM Recipe r", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<Recipe> list(int limit, int offset) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Recipe> list = em.createQuery(
                            "SELECT r FROM Recipe r ORDER BY r.id", Recipe.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();

            // Force-initialize lazy collections & owner inside the open persistence context
            list.forEach(r -> {
                r.getTags().size();
                r.getIngredients().size();
                if (r.getOwner() != null) r.getOwner().getUsername();
            });
            return list;
        } finally {
            em.close();
        }
    }

    public Recipe get(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Recipe r = em.find(Recipe.class, id);
            if (r != null) {
                r.getTags().size();
                r.getIngredients().size();
                if (r.getOwner() != null) r.getOwner().getUsername();
            }
            return r;
        } finally {
            em.close();
        }
    }

    public Recipe saveForOwner(String username, Recipe r) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User owner = em.find(User.class, username);
            r.setOwner(owner);
            em.persist(r);
            em.getTransaction().commit();
            return r;
        } finally {
            em.close();
        }
    }

    public Recipe save(Recipe r) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            r = em.merge(r);
            em.getTransaction().commit();
            return r;
        } finally {
            em.close();
        }
    }

    public void delete(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Recipe ref = em.find(Recipe.class, id);
            if (ref != null) em.remove(ref);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
