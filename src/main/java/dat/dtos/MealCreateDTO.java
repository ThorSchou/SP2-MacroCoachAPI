package dat.dtos;

public record MealCreateDTO(
        Long recipeId, Integer grams, String note, MealType type
) {}