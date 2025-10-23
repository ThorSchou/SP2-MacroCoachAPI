package dat.services;

import dat.dtos.DayPlanRequest;
import dat.dtos.DayPlanResponse;
import dat.dtos.MealType;
import dat.exceptions.ValidationException;
import dat.utils.PromptBuilder;

import java.util.*;

public class AiService {

    public DayPlanResponse dayPlan(DayPlanRequest req) throws ValidationException {
        if (req.includeTypes()==null || req.includeTypes().isEmpty())
            throw new ValidationException("includeTypes must contain at least one meal type");

        // 1) Build prompts (system & user)
        String system = """
      You are a macro nutrition coach.
      Respond with VALID JSON ONLY using:
      {
        "plan": {
          "<type>": {
            "name": "string",
            "type": "BREAKFAST|LUNCH|DINNER|SNACK|DESSERT",
            "kcal": 0, "protein": 0, "carbs": 0, "fat": 0,
            "steps": ["string"], "neededGroceries": ["string"]
          }
        },
        "totals": { "kcal": 0, "protein": 0, "carbs": 0, "fat": 0 }
      }
      Return exactly one meal per requested type. Respect diet/avoid if provided. Prefer pantry items if provided.
      If a target macro is null, do not constrain that macro.
      """;
        String user = PromptBuilder.buildDayPlanPrompt(req);

        // 2) TODO: OpenAI call (temperature=0.2, response_format=json_object)
        // 3) Parse → DayPlanResponse

        // Mock so the endpoint runs:
        Map<MealType, DayPlanResponse.SuggestedMeal> plan = new HashMap<>();
        for (MealType t : req.includeTypes()) {
            plan.put(t, new DayPlanResponse.SuggestedMeal(
                    "Example " + t.name().toLowerCase(), t, 500, 30, 50, 15,
                    List.of("Step 1","Step 2"), List.of()
            ));
        }
        int kcal=0,p=0,c=0,f=0; for (var m:plan.values()) { kcal+=m.kcal(); p+=m.protein(); c+=m.carbs(); f+=m.fat(); }
        return new DayPlanResponse(plan, new DayPlanResponse.Totals(kcal,p,c,f));
    }
}
