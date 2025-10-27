package app.dtos;

import java.util.List;

public record DayPlanRequest(
        String prompt,
        Integer targetKcal,
        Integer targetProtein,
        Integer targetCarbs,
        Integer targetFat,
        Integer meals,
        Integer snacks,
        String diet,
        List<String> allergies,
        List<String> avoid,
        List<String> includeTypes,
        List<String> pantry
) {}
