package dat.dtos;

import dat.entities.PantryItem;

public record PantryResponseDTO(
        Long id,
        String name,
        Integer grams,
        String expiry
) {
    public static PantryResponseDTO from(PantryItem p) {
        return new PantryResponseDTO(
                p.getId(),
                p.getName(),
                p.getGrams(),
                p.getExpiry() == null ? null : p.getExpiry().toString()
        );
    }
}
