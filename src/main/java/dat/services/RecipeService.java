package dat.services;

import dat.daos.RecipeDao;
import dat.dtos.PageDTO;
import dat.dtos.RecipeCreateDTO;
import dat.dtos.RecipeResponseDTO;
import dat.entities.Recipe;
import dat.security.exceptions.ApiException;

import java.util.List;

public class RecipeService {

    private final RecipeDao dao = new RecipeDao();

    public PageDTO<RecipeResponseDTO> list(int limit, int offset) {
        List<Recipe> rows = dao.list(limit, offset); // collections initialized inside DAO
        long total = dao.count();
        List<RecipeResponseDTO> dtos = rows.stream().map(this::toDTO).toList();
        return new PageDTO<>(dtos, total, limit, offset);
    }

    public RecipeResponseDTO get(long id) {
        Recipe r = dao.get(id); // collections initialized inside DAO
        if (r == null) throw new ApiException(404, "Recipe not found");
        return toDTO(r);
    }

    public RecipeResponseDTO create(String username, RecipeCreateDTO dto) {
        Recipe r = new Recipe();
        r.setName(dto.getName());
        r.setKcal(dto.getKcal());
        r.setProtein(dto.getProtein());
        r.setCarbs(dto.getCarbs());
        r.setFat(dto.getFat());
        r.setDefaultGrams(dto.getDefaultGrams());
        r.setIngredients(dto.getIngredients());
        r.setTags(dto.getTags());

        Recipe saved = dao.saveForOwner(username, r);
        return toDTO(saved);
    }

    /**
     * Only the owner or an admin can update/delete.
     */
    public RecipeResponseDTO update(String username, long id, RecipeCreateDTO dto, boolean isAdmin) {
        Recipe existing = dao.get(id); // collections initialized
        if (existing == null) throw new ApiException(404, "Recipe not found");

        String owner = existing.getOwner() != null ? existing.getOwner().getUsername() : null;
        if (!isAdmin && (owner == null || !owner.equals(username))) {
            throw new ApiException(403, "You cannot edit this recipe");
        }

        existing.setName(dto.getName());
        existing.setKcal(dto.getKcal());
        existing.setProtein(dto.getProtein());
        existing.setCarbs(dto.getCarbs());
        existing.setFat(dto.getFat());
        existing.setDefaultGrams(dto.getDefaultGrams());
        existing.setIngredients(dto.getIngredients());
        existing.setTags(dto.getTags());

        Recipe saved = dao.save(existing);
        return toDTO(saved);
    }

    public void delete(String username, long id, boolean isAdmin) {
        Recipe existing = dao.get(id);
        if (existing == null) throw new ApiException(404, "Recipe not found");
        String owner = existing.getOwner() != null ? existing.getOwner().getUsername() : null;

        if (!isAdmin && (owner == null || !owner.equals(username))) {
            throw new ApiException(403, "You cannot delete this recipe");
        }
        dao.delete(id);
    }

    private RecipeResponseDTO toDTO(Recipe r) {
        return new RecipeResponseDTO(
                r.getId(),
                r.getName(),
                r.getKcal(),
                r.getProtein(),
                r.getCarbs(),
                r.getFat(),
                List.copyOf(r.getTags()),
                List.copyOf(r.getIngredients()),
                r.getSteps(),
                r.getDefaultGrams(),
                r.getOwner() != null ? r.getOwner().getUsername() : null
        );
    }
}
