package dat.dtos;

import java.util.List;

public record RecipeCreateDTO(
        String name, Integer kcal, Integer protein, Integer carbs, Integer fat,
        java.util.List<String> tags, java.util.List<String> ingredients, String steps, Integer defaultGrams
) {}