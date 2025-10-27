package app.services;

import app.daos.ProfileDao;
import app.dtos.ProfileCreateDTO;
import app.dtos.ProfileResponseDTO;
import app.entities.Profile;
import app.security.entities.User;

import java.util.List;

public class ProfileService {
    private final ProfileDao profiles;

    public ProfileService(ProfileDao profiles) {
        this.profiles = profiles;
    }

    public ProfileResponseDTO getMine(User user) {
        if (user == null) throw new IllegalStateException("Missing authenticated user");
        String username = user.getUsername();

        var p = profiles.findByUsername(username)
                .orElseGet(() -> profiles.saveForUsername(username, new Profile()));

        return toDTO(p);
    }

    public ProfileResponseDTO replace(User user, ProfileCreateDTO dto) {
        if (user == null) throw new IllegalStateException("Missing authenticated user");
        String username = user.getUsername();

        var p = profiles.findByUsername(username)
                .orElseGet(Profile::new);

        p.setTargetKcal(dto.targetKcal());
        p.setTargetProtein(dto.targetProtein());
        p.setTargetCarbs(dto.targetCarbs());
        p.setTargetFat(dto.targetFat());
        p.setDiet(dto.diet());
        p.setAllergies(dto.allergies() == null ? List.of() : dto.allergies());

        return toDTO(profiles.saveForUsername(username, p));
    }

    public ProfileResponseDTO patch(User user, ProfileCreateDTO dto) {
        if (user == null) throw new IllegalStateException("Missing authenticated user");
        String username = user.getUsername();

        var p = profiles.findByUsername(username)
                .orElseGet(Profile::new);

        if (dto.targetKcal()    != null) p.setTargetKcal(dto.targetKcal());
        if (dto.targetProtein() != null) p.setTargetProtein(dto.targetProtein());
        if (dto.targetCarbs()   != null) p.setTargetCarbs(dto.targetCarbs());
        if (dto.targetFat()     != null) p.setTargetFat(dto.targetFat());
        if (dto.diet()          != null) p.setDiet(dto.diet());
        if (dto.allergies()     != null) p.setAllergies(dto.allergies());

        return toDTO(profiles.saveForUsername(username, p));
    }

    private ProfileResponseDTO toDTO(Profile p) {
        var allergies = p.getAllergies() == null
                ? java.util.List.<String>of()
                : java.util.List.copyOf(p.getAllergies());
        return new ProfileResponseDTO(
                p.getId(),
                p.getTargetKcal(),
                p.getTargetProtein(),
                p.getTargetCarbs(),
                p.getTargetFat(),
                p.getDiet(),
                allergies
        );
    }
}
