package dat.services;

import dat.config.HibernateConfig;
import dat.daos.RecipeDao;
import dat.dtos.PageDTO;
import dat.dtos.RecipeCreateDTO;
import dat.dtos.RecipeResponseDTO;
import dat.entities.Recipe;
import dat.security.entities.User;
import jakarta.persistence.EntityManager;

import java.util.List;

public class RecipeService {
    private final RecipeDao dao = new RecipeDao();

    public PageDTO<RecipeResponseDTO> list(int limit, int offset) {
        List<Recipe> recipes = dao.listPaged(limit, offset);
        long total = dao.countAll();
        List<RecipeResponseDTO> items = recipes.stream().map(this::toDTO).toList();
        return new PageDTO<>(items, total, limit, offset);
    }

    public RecipeResponseDTO get(long id) {
        Recipe r = dao.getWithCollections(id);
        return toDTO(r);
    }

    public RecipeResponseDTO create(String ownerUsername, RecipeCreateDTO in) {
        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            User owner = em.find(User.class, ownerUsername);
            if (owner == null) throw new IllegalArgumentException("Owner not found: " + ownerUsername);

            Recipe r = new Recipe();
            r.setOwner(owner);
            r.setName(in.name());
            r.setKcal(in.kcal());
            r.setProtein(in.protein());
            r.setCarbs(in.carbs());
            r.setFat(in.fat());
            r.setDefaultGrams(in.defaultGrams());
            r.setIngredients(in.ingredients() == null ? List.of() : List.copyOf(in.ingredients()));
            r.setTags(in.tags() == null ? List.of() : List.copyOf(in.tags()));

            em.persist(r);
            em.getTransaction().commit();
            // Ensure collections are initialized
            r.getIngredients().size(); r.getTags().size();
            return toDTO(r);
        } finally {
            em.close();
        }
    }

    public RecipeResponseDTO update(String ownerUsername, long id, RecipeCreateDTO in) {
        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Recipe r = em.find(Recipe.class, id);
            if (r == null) throw new IllegalArgumentException("Recipe not found: " + id);
            if (r.getOwner() == null || !ownerUsername.equals(r.getOwner().getUsername())) {
                throw new SecurityException("Not your recipe");
            }
            r.setName(in.name());
            r.setKcal(in.kcal());
            r.setProtein(in.protein());
            r.setCarbs(in.carbs());
            r.setFat(in.fat());
            r.setDefaultGrams(in.defaultGrams());
            r.setIngredients(in.ingredients() == null ? List.of() : List.copyOf(in.ingredients()));
            r.setTags(in.tags() == null ? List.of() : List.copyOf(in.tags()));

            em.merge(r);
            em.getTransaction().commit();
            r.getIngredients().size(); r.getTags().size();
            return toDTO(r);
        } finally {
            em.close();
        }
    }

    public void delete(String ownerUsername, long id) {
        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Recipe r = em.find(Recipe.class, id);
            if (r != null) {
                if (r.getOwner() == null || !ownerUsername.equals(r.getOwner().getUsername())) {
                    throw new SecurityException("Not your recipe");
                }
                em.remove(r);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private RecipeResponseDTO toDTO(Recipe r) {
        return new RecipeResponseDTO(
                r.getId(),
                r.getName(),
                r.getKcal(),
                r.getProtein(),
                r.getCarbs(),
                r.getFat(),
                List.copyOf(r.getTags() == null ? List.of() : r.getTags()),
                List.copyOf(r.getIngredients() == null ? List.of() : r.getIngredients()),
                r.getSteps(),
                r.getDefaultGrams(),
                r.getOwner() == null ? null : r.getOwner().getUsername()
        );
    }
}
