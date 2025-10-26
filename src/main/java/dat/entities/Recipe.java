package dat.entities;

import jakarta.persistence.*;
import java.util.*;

@Entity @Table(name="recipes")
public class Recipe {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="owner_id")
    private dat.security.entities.User owner;

    @Column(nullable=false) private String name;
    @Column(nullable=false) private Integer kcal, protein, carbs, fat;

    @ElementCollection
    @CollectionTable(name="recipe_tags", joinColumns=@JoinColumn(name="recipe_id"))
    @Column(name="tag")
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name="recipe_ingredients", joinColumns=@JoinColumn(name="recipe_id"))
    @Column(name="ingredient")
    private List<String> ingredients = new ArrayList<>();

    @Lob private String steps;
    private Integer defaultGrams;

    public Long getId() { return id; }
    public dat.security.entities.User getOwner() { return owner; }
    public void setOwner(dat.security.entities.User o) { this.owner = o; }
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public Integer getKcal() { return kcal; }
    public void setKcal(Integer v) { this.kcal = v; }
    public Integer getProtein() { return protein; }
    public void setProtein(Integer v) { this.protein = v; }
    public Integer getCarbs() { return carbs; }
    public void setCarbs(Integer v) { this.carbs = v; }
    public Integer getFat() { return fat; }
    public void setFat(Integer v) { this.fat = v; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> t) { this.tags = t; }
    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> i) { this.ingredients = i; }
    public String getSteps() { return steps; }
    public void setSteps(String s) { this.steps = s; }
    public Integer getDefaultGrams() { return defaultGrams; }
    public void setDefaultGrams(Integer g) { this.defaultGrams = g; }
}
