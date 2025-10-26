package dat.daos;

import dat.config.HibernateConfig;
import dat.entities.Recipe;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RecipeDao {

    public List<Recipe> listPaged(int limit, int offset) {
        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();
        try {
            List<Long> ids = em.createQuery(
                            "SELECT r.id FROM Recipe r ORDER BY r.id", Long.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();

            if (ids.isEmpty()) return List.of();

            List<Recipe> result = em.createQuery(
                            "SELECT DISTINCT r FROM Recipe r " +
                                    "LEFT JOIN FETCH r.tags " +
                                    "LEFT JOIN FETCH r.ingredients " +
                                    "LEFT JOIN FETCH r.owner " +
                                    "WHERE r.id IN :ids", Recipe.class)
                    .setParameter("ids", ids)
                    .getResultList();

            // Keep paging order
            result.sort(Comparator.comparing(Recipe::getId));
            return result;
        } finally {
            em.close();
        }
    }

    public long countAll() {
        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(r) FROM Recipe r", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public Recipe getWithCollections(long id) {
        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();
        try {
            List<Recipe> list = em.createQuery(
                            "SELECT DISTINCT r FROM Recipe r " +
                                    "LEFT JOIN FETCH r.tags " +
                                    "LEFT JOIN FETCH r.ingredients " +
                                    "LEFT JOIN FETCH r.owner " +
                                    "WHERE r.id=:id", Recipe.class)
                    .setParameter("id", id)
                    .getResultList();
            if (list.isEmpty()) throw new IllegalArgumentException("Recipe not found: " + id);
            return list.get(0);
        } finally {
            em.close();
        }
    }
}
