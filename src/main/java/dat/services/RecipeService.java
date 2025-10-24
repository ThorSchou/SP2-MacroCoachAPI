package dat.services;

import dat.daos.RecipeDao;
import dat.dtos.PageDTO;
import dat.dtos.RecipeCreateDTO;
import dat.dtos.RecipeResponseDTO;
import dat.entities.Recipe;
import dat.exceptions.ApiException;
import dat.security.entities.User;

import java.util.List;

public class RecipeService {
    private final RecipeDao recipes;

    public RecipeService(RecipeDao recipes) {
        this.recipes = recipes;
    }

    public RecipeResponseDTO create(User user, RecipeCreateDTO dto) {
        if (user == null || user.getUsername() == null) {
            throw new ApiException(401, "Missing authenticated user");
        }

        Recipe r = new Recipe();
        r.setName(dto.name());
        r.setKcal(dto.kcal());
        r.setProtein(dto.protein());
        r.setCarbs(dto.carbs());
        r.setFat(dto.fat());
        r.setDefaultGrams(dto.defaultGrams());
        r.setIngredients(dto.ingredients() == null ? List.of() : dto.ingredients());
        r.setTags(dto.tags() == null ? List.of() : dto.tags());
        r.setSteps(dto.steps());

        Recipe saved = recipes.saveForOwner(user.getUsername(), r);
        return toDTO(saved);
    }

    public RecipeResponseDTO get(Long id) {
        Recipe r = recipes.findById(id)
                .orElseThrow(() -> new ApiException(404, "Recipe not found"));
        return toDTO(r);
    }

    public PageDTO<RecipeResponseDTO> list(int limit, int offset) {
        List<Recipe> rs = recipes.list(limit, offset);
        long total = recipes.count();
        List<RecipeResponseDTO> items = rs.stream().map(this::toDTO).toList();
        return new PageDTO<>(items, total, limit, offset);
    }

    public RecipeResponseDTO update(User user, boolean isAdmin, Long id, RecipeCreateDTO dto) {
        if (user == null || user.getUsername() == null) {
            throw new ApiException(401, "Missing authenticated user");
        }

        Recipe existing = recipes.findById(id)
                .orElseThrow(() -> new ApiException(404, "Recipe not found"));

        boolean isOwner = existing.getOwner() != null &&
                existing.getOwner().getUsername().equals(user.getUsername());

        if (!isAdmin && !isOwner) {
            throw new ApiException(403, "Forbidden");
        }

        existing.setName(dto.name());
        existing.setKcal(dto.kcal());
        existing.setProtein(dto.protein());
        existing.setCarbs(dto.carbs());
        existing.setFat(dto.fat());
        existing.setDefaultGrams(dto.defaultGrams());
        existing.setIngredients(dto.ingredients() == null ? List.of() : dto.ingredients());
        existing.setTags(dto.tags() == null ? List.of() : dto.tags());
        existing.setSteps(dto.steps());

        Recipe saved = recipes.saveForOwner(
                (existing.getOwner() != null ? existing.getOwner().getUsername() : user.getUsername()),
                existing
        );
        return toDTO(saved);
    }

    public void delete(User user, boolean isAdmin, Long id) {
        if (user == null || user.getUsername() == null) {
            throw new ApiException(401, "Missing authenticated user");
        }

        Recipe existing = recipes.findById(id)
                .orElseThrow(() -> new ApiException(404, "Recipe not found"));

        boolean isOwner = existing.getOwner() != null &&
                existing.getOwner().getUsername().equals(user.getUsername());

        if (!isAdmin && !isOwner) {
            throw new ApiException(403, "Forbidden");
        }

        recipes.delete(id);
    }

    private RecipeResponseDTO toDTO(Recipe r) {
        List<String> tags = r.getTags() == null ? List.of() : List.copyOf(r.getTags());
        List<String> ingredients = r.getIngredients() == null ? List.of() : List.copyOf(r.getIngredients());

        return new RecipeResponseDTO(
                r.getId(),
                r.getName(),
                r.getKcal(),
                r.getProtein(),
                r.getCarbs(),
                r.getFat(),
                tags,
                ingredients,
                r.getSteps(),
                r.getDefaultGrams(),
                r.getOwner() == null ? null : r.getOwner().getUsername()
        );
    }
}
