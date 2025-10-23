package dat.dtos;

import java.util.List;

public record DayPlanRequest(
        Integer targetKcal, Integer targetProtein, Integer targetCarbs, Integer targetFat,
        java.util.List<MealType> includeTypes, // required: 1..5
        String diet, java.util.List<String> avoid, java.util.List<String> pantry
) {}