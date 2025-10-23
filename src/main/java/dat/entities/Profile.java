package dat.entities;

import jakarta.persistence.*;
import java.util.*;

@Entity @Table(name="profiles")
public class Profile {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;

    @OneToOne(optional=false) @JoinColumn(name="user_id", unique=true)
    private dat.security.entities.User user;

    private Integer targetKcal, targetProtein, targetCarbs, targetFat;
    private String diet; // nullable

    @ElementCollection
    @CollectionTable(name="profile_allergies", joinColumns=@JoinColumn(name="profile_id"))
    @Column(name="allergy")
    private List<String> allergies = new ArrayList<>();

    // getters/setters
    public Long getId() { return id; }
    public dat.security.entities.User getUser() { return user; }
    public void setUser(dat.security.entities.User u) { this.user = u; }
    public Integer getTargetKcal() { return targetKcal; }
    public void setTargetKcal(Integer v) { this.targetKcal = v; }
    public Integer getTargetProtein() { return targetProtein; }
    public void setTargetProtein(Integer v) { this.targetProtein = v; }
    public Integer getTargetCarbs() { return targetCarbs; }
    public void setTargetCarbs(Integer v) { this.targetCarbs = v; }
    public Integer getTargetFat() { return targetFat; }
    public void setTargetFat(Integer v) { this.targetFat = v; }
    public String getDiet() { return diet; }
    public void setDiet(String d) { this.diet = d; }
    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> a) { this.allergies = a; }
}
