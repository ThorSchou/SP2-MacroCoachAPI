package dat.dtos;

import java.util.Map;
import java.util.List;

public record DayPlanResponse(
        java.util.Map<MealType, SuggestedMeal> plan,
        Totals totals
) {
    public static record SuggestedMeal(
            String name, MealType type,
            Integer kcal, Integer protein, Integer carbs, Integer fat,
            java.util.List<String> steps, java.util.List<String> neededGroceries
    ) {}
    public static record Totals(Integer kcal, Integer protein, Integer carbs, Integer fat) {}
}