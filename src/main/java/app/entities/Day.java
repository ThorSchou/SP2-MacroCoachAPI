package app.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name="days", uniqueConstraints=@UniqueConstraint(columnNames={"user_id","date"}))
public class Day {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="user_id")
    private app.security.entities.User user;

    @Column(nullable=false) private LocalDate date;

    @OneToMany(mappedBy="day", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Meal> meals = new ArrayList<>();

    public Long getId() { return id; }
    public app.security.entities.User getUser() { return user; }
    public void setUser(app.security.entities.User u) { this.user = u; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate d) { this.date = d; }
    public List<Meal> getMeals() { return meals; }
    public void setMeals(List<Meal> m) { this.meals = m; }
}
