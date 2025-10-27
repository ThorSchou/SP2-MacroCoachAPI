package app.dtos;

import app.entities.Day;
import app.entities.Meal;
import app.entities.Recipe;

import java.time.LocalDate;
import java.util.List;

public record DayResponseDTO(
        LocalDate date,
        List<MealResponseDTO> meals,
        Integer totalKcal,
        Integer totalProtein,
        Integer totalCarbs,
        Integer totalFat,
        Integer remainingKcal,
        Integer remainingProtein,
        Integer remainingCarbs,
        Integer remainingFat
) {
    public static DayResponseDTO from(Day d) {
        if (d == null) {
            return new DayResponseDTO(null, List.of(), 0, 0, 0, 0, null, null, null, null);
        }

        List<MealResponseDTO> mealDtos = d.getMeals() == null
                ? List.of()
                : d.getMeals().stream()
                .map(MealResponseDTO::from)
                .toList();

        // Compute totals from Meal + Recipe (scaled by grams/defaultGrams)
        int tKcal = 0, tProtein = 0, tCarbs = 0, tFat = 0;

        if (d.getMeals() != null) {
            for (Meal m : d.getMeals()) {
                Recipe r = m.getRecipe();
                Integer grams = m.getGrams();
                Integer def = (r == null) ? null : r.getDefaultGrams();

                tKcal    += scale((r == null) ? null : r.getKcal(),    grams, def);
                tProtein += scale((r == null) ? null : r.getProtein(), grams, def);
                tCarbs   += scale((r == null) ? null : r.getCarbs(),   grams, def);
                tFat     += scale((r == null) ? null : r.getFat(),     grams, def);
            }
        }

        return new DayResponseDTO(
                d.getDate(),
                mealDtos,
                tKcal, tProtein, tCarbs, tFat,
                null, null, null, null
        );
    }

    private static int scale(Integer valuePerDefault, Integer grams, Integer defaultGrams) {
        if (valuePerDefault == null || grams == null || defaultGrams == null || defaultGrams == 0) return 0;
        return (int) Math.round(valuePerDefault * (grams / (double) defaultGrams));
    }
}
