package app.dtos;

import app.entities.Meal;
import app.entities.Recipe;

public record MealResponseDTO(
        Long id,
        Long recipeId,
        String recipeName,
        Integer grams,
        String type,
        String note
) {
    public static MealResponseDTO from(Meal m) {
        if (m == null) return null;
        Recipe r = m.getRecipe();
        Long rid = (r != null) ? r.getId() : null;
        String rname = (r != null) ? r.getName() : null;

        String t = (m.getType() == null) ? null : m.getType().toString();

        return new MealResponseDTO(
                m.getId(),
                rid,
                rname,
                m.getGrams(),
                t,
                m.getNote()
        );
    }
}
