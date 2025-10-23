package dat.services;

import dat.daos.RecipeDao;
import dat.dtos.PageResponse;
import dat.dtos.RecipeCreateDTO;
import dat.dtos.RecipeResponseDTO;
import dat.entities.Recipe;
import dat.exceptions.ApiException;
import dat.exceptions.NotAuthorizedException;
import dat.exceptions.ValidationException;
import dat.security.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class RecipeService {
    private final RecipeDao recipes;
    public RecipeService(RecipeDao recipes) { this.recipes = recipes; }

    public PageResponse<RecipeResponseDTO> list(int limit, int offset) throws ValidationException {
        if (limit < 0 || offset < 0) throw new ValidationException("limit/offset must be >= 0");
        var list = recipes.list(limit, offset).stream().map(this::toDTO).collect(Collectors.toList());
        var total = recipes.count();
        String next = (offset + limit < total) ? "/api/recipes?limit="+limit+"&offset="+(offset+limit) : null;
        return new PageResponse<>(list, total, next);
    }

    public RecipeResponseDTO get(Long id) throws ApiException {
        var r = recipes.findById(id).orElseThrow(() -> new ApiException(404, "Recipe not found"));
        return toDTO(r);
    }

    public RecipeResponseDTO create(User owner, RecipeCreateDTO dto) throws ValidationException {
        validateMacros(dto.kcal(), dto.protein(), dto.carbs(), dto.fat());
        validateName(dto.name());
        var r = new Recipe();
        r.setOwner(owner);
        r.setName(dto.name());
        r.setKcal(dto.kcal()); r.setProtein(dto.protein()); r.setCarbs(dto.carbs()); r.setFat(dto.fat());
        r.setTags(dto.tags()==null? List.of(): dto.tags());
        r.setIngredients(dto.ingredients()==null? List.of(): dto.ingredients());
        r.setSteps(dto.steps()); r.setDefaultGrams(dto.defaultGrams());
        return toDTO(recipes.save(r));
    }

    public RecipeResponseDTO update(User user, boolean isAdmin, Long id, RecipeCreateDTO dto)
            throws ApiException, NotAuthorizedException, ValidationException {
        var r = recipes.findById(id).orElseThrow(() -> new ApiException(404, "Recipe not found"));

        if (!isAdmin && !r.getOwner().getUsername().equals(user.getUsername()))
            throw new NotAuthorizedException(403, "You don't have access to this resource");

        validateMacros(dto.kcal(), dto.protein(), dto.carbs(), dto.fat());
        validateName(dto.name());

        r.setName(dto.name());
        r.setKcal(dto.kcal()); r.setProtein(dto.protein()); r.setCarbs(dto.carbs()); r.setFat(dto.fat());
        r.setTags(dto.tags()==null? List.of(): dto.tags());
        r.setIngredients(dto.ingredients()==null? List.of(): dto.ingredients());
        r.setSteps(dto.steps()); r.setDefaultGrams(dto.defaultGrams());
        return toDTO(recipes.save(r));
    }

    public void delete(User requester, boolean isAdmin, Long id) throws ApiException, NotAuthorizedException {
        var r = recipes.findById(id).orElseThrow(() -> new ApiException(404, "Recipe not found"));
        if (!isAdmin && !r.getOwner().getUsername().equals(requester.getUsername()))
            throw new NotAuthorizedException(403, "You don't have access to this resource");
        recipes.delete(r);
    }

    private RecipeResponseDTO toDTO(Recipe r) {
        return new RecipeResponseDTO(r.getId(), r.getName(), r.getKcal(), r.getProtein(), r.getCarbs(), r.getFat(),
                r.getTags(), r.getIngredients(), r.getSteps(), r.getDefaultGrams(), r.getOwner().getUsername());
    }

    private void validateMacros(Integer kcal, Integer p, Integer c, Integer f) throws ValidationException {
        if (kcal==null || p==null || c==null || f==null) throw new ValidationException("kcal, protein, carbs, fat are required");
        if (kcal<0 || p<0 || c<0 || f<0) throw new ValidationException("macros must be >= 0");
    }

    private void validateName(String name) throws ValidationException {
        if (name == null || name.isBlank()) throw new ValidationException("name must not be blank");
    }
}
