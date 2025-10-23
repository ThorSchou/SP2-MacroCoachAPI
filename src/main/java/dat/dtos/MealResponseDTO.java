package dat.dtos;

public record MealResponseDTO(
        Long id, Long recipeId, String recipeName, Integer grams, MealType type,
        Integer kcal, Integer protein, Integer carbs, Integer fat, String note
) {}