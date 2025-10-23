package dat.dtos;

import java.util.List;

public record ProfileCreateDTO(
        Integer targetKcal, Integer targetProtein, Integer targetCarbs, Integer targetFat,
        String diet, List<String> allergies
) {}