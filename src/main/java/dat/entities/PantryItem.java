package dat.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity @Table(name="pantry_items")
public class PantryItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="user_id")
    private dat.security.entities.User user;

    @Column(nullable=false) private String name;
    @Column(nullable=false) private Integer grams;
    private LocalDate expiry;

    public Long getId() { return id; }
    public dat.security.entities.User getUser() { return user; }
    public void setUser(dat.security.entities.User u) { this.user = u; }
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public Integer getGrams() { return grams; }
    public void setGrams(Integer g) { this.grams = g; }
    public LocalDate getExpiry() { return expiry; }
    public void setExpiry(LocalDate e) { this.expiry = e; }
}
