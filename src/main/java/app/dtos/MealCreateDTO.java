package app.dtos;

import app.entities.MealType;

public record MealCreateDTO(
        Long recipeId,
        Integer grams,
        MealType type,
        String note
) {}
