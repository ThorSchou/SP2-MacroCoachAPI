package dat.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "meals")
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "day_id")
    private Day day;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    private Integer grams; // 1..2000
    private String note;

    @Enumerated(EnumType.STRING)
    private MealType type; // optional

    public Long getId() { return id; }
    public Day getDay() { return day; }
    public void setDay(Day d) { this.day = d; }
    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe r) { this.recipe = r; }
    public Integer getGrams() { return grams; }
    public void setGrams(Integer g) { this.grams = g; }
    public String getNote() { return note; }
    public void setNote(String n) { this.note = n; }
    public MealType getType() { return type; }
    public void setType(MealType t) { this.type = t; }
}
