package dat.daos;

import dat.entities.Recipe;
import dat.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public class RecipeDao {
    private final EntityManagerFactory emf;
    public RecipeDao(EntityManagerFactory emf) { this.emf = emf; }

    public Recipe saveForOwner(String username, Recipe r) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            User managedUser = em.find(User.class, username);
            if (managedUser == null) {
                throw new IllegalStateException("Cannot save recipe: user '" + username + "' not found");
            }
            r.setOwner(managedUser);

            Recipe managed;
            if (r.getId() == null) {
                em.persist(r);
                managed = r;
            } else {
                managed = em.merge(r);
            }

            em.getTransaction().commit();
            return managed;
        } finally {
            em.close();
        }
    }

    public Optional<Recipe> findById(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Recipe.class, id));
        } finally {
            em.close();
        }
    }

    public List<Recipe> list(int limit, int offset) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Recipe r ORDER BY r.id", Recipe.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long count() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(r) FROM Recipe r", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public void delete(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Recipe r = em.find(Recipe.class, id);
            if (r != null) em.remove(r);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
