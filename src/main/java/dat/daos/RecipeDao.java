package dat.daos;

import dat.config.HibernateConfig;
import dat.entities.Recipe;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class RecipeDao {
    private final EntityManagerFactory emf;

    public RecipeDao() {
        this(HibernateConfig.getEntityManagerFactory());
    }

    public RecipeDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<Recipe> listPaged(int limit, int offset) {
        try (EntityManager em = emf.createEntityManager()) {
            var list = em.createQuery(
                            "SELECT r FROM Recipe r ORDER BY r.id", Recipe.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();

            list.forEach(r -> {
                r.getIngredients().size();
                r.getTags().size();
            });
            return list;
        }
    }

    public long countAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT COUNT(r) FROM Recipe r", Long.class)
                    .getSingleResult();
        }
    }

    public Recipe getWithCollections(long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Recipe r = em.find(Recipe.class, id);
            if (r != null) {
                r.getIngredients().size();
                r.getTags().size();
            }
            return r;
        }
    }

    public Recipe create(Recipe r) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(r);
            em.getTransaction().commit();

            r.getIngredients().size();
            r.getTags().size();
            return r;
        }
    }

    public Recipe update(long id, Recipe patch) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Recipe r = em.find(Recipe.class, id);
            if (r == null) {
                em.getTransaction().rollback();
                return null;
            }

            r.setName(patch.getName());
            r.setKcal(patch.getKcal());
            r.setProtein(patch.getProtein());
            r.setCarbs(patch.getCarbs());
            r.setFat(patch.getFat());
            r.setDefaultGrams(patch.getDefaultGrams());
            r.setSteps(patch.getSteps());

            r.getIngredients().clear();
            r.getIngredients().addAll(patch.getIngredients());
            r.getTags().clear();
            r.getTags().addAll(patch.getTags());

            em.getTransaction().commit();

            r.getIngredients().size();
            r.getTags().size();
            return r;
        }
    }

    public boolean delete(long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Recipe r = em.find(Recipe.class, id);
            if (r == null) {
                em.getTransaction().rollback();
                return false;
            }
            em.remove(r);
            em.getTransaction().commit();
            return true;
        }
    }
}
