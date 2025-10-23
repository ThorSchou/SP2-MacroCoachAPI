package dat.services;

import dat.daos.ProfileDao;
import dat.dtos.ProfileCreateDTO;
import dat.dtos.ProfileResponseDTO;
import dat.entities.Profile;
import dat.security.entities.User;

import java.util.List;

public class ProfileService {
    private final ProfileDao profiles;
    public ProfileService(ProfileDao profiles) { this.profiles = profiles; }

    public ProfileResponseDTO getMine(User user) {
        var p = profiles.findByUsername(user.getUsername()).orElseGet(() -> {
            var np = new Profile(); np.setUser(user); return profiles.save(np);
        });
        return toDTO(p);
    }

    public ProfileResponseDTO replace(User user, ProfileCreateDTO dto) {
        var p = profiles.findByUsername(user.getUsername()).orElseGet(() -> { var np = new Profile(); np.setUser(user); return np; });
        p.setTargetKcal(dto.targetKcal());
        p.setTargetProtein(dto.targetProtein());
        p.setTargetCarbs(dto.targetCarbs());
        p.setTargetFat(dto.targetFat());
        p.setDiet(dto.diet());
        p.setAllergies(dto.allergies()==null? List.of(): dto.allergies());
        return toDTO(profiles.save(p));
    }

    public ProfileResponseDTO patch(User user, ProfileCreateDTO dto) {
        var p = profiles.findByUsername(user.getUsername()).orElseGet(() -> { var np = new Profile(); np.setUser(user); return np; });
        if (dto.targetKcal()!=null) p.setTargetKcal(dto.targetKcal());
        if (dto.targetProtein()!=null) p.setTargetProtein(dto.targetProtein());
        if (dto.targetCarbs()!=null) p.setTargetCarbs(dto.targetCarbs());
        if (dto.targetFat()!=null) p.setTargetFat(dto.targetFat());
        if (dto.diet()!=null) p.setDiet(dto.diet());
        if (dto.allergies()!=null) p.setAllergies(dto.allergies());
        return toDTO(profiles.save(p));
    }

    private ProfileResponseDTO toDTO(Profile p) {
        return new ProfileResponseDTO(p.getId(), p.getTargetKcal(), p.getTargetProtein(), p.getTargetCarbs(), p.getTargetFat(), p.getDiet(), p.getAllergies());
    }
}
