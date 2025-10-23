package dat.dtos;

import java.util.List;

public record ProfileResponseDTO(
        Long id, Integer targetKcal, Integer targetProtein, Integer targetCarbs, Integer targetFat,
        String diet, List<String> allergies
) {}