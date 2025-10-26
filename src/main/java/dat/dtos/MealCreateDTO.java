package dat.dtos;

import dat.entities.MealType;

public record MealCreateDTO(
        Long recipeId,
        Integer grams,
        MealType type,
        String note
) {}
