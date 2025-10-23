package dat.utils;

import dat.dtos.DayPlanRequest;
import java.util.List;

public class PromptBuilder {
    public static String buildDayPlanPrompt(DayPlanRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("Create a daily meal plan.\n");
        sb.append("Include one meal for each type: ").append(join(req.includeTypes())).append(".\n");

        if (req.targetKcal()!=null) sb.append("Target kcal: ").append(req.targetKcal()).append(". ");
        if (req.targetProtein()!=null) sb.append("Protein target: ").append(req.targetProtein()).append("g. ");
        if (req.targetCarbs()!=null) sb.append("Carbs target: ").append(req.targetCarbs()).append("g. ");
        if (req.targetFat()!=null) sb.append("Fat target: ").append(req.targetFat()).append("g. ");
        sb.append("\n");

        if (req.diet()!=null && !req.diet().isBlank()) sb.append("Diet type: ").append(req.diet()).append(". ");
        if (req.avoid()!=null && !req.avoid().isEmpty()) sb.append("Avoid: ").append(join(req.avoid())).append(". ");
        if (req.pantry()!=null && !req.pantry().isEmpty()) sb.append("Available ingredients: ").append(join(req.pantry())).append(". ");
        return sb.toString();
    }

    private static String join(List<?> list) {
        return (list==null||list.isEmpty()) ? "" : String.join(", ", list.stream().map(Object::toString).toList());
    }
}
