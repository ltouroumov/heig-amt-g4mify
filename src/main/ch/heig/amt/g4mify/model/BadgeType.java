package ch.heig.amt.g4mify.model;

import javax.persistence.*;
import java.util.List;

/**
 * @author ldavid
 * @created 11/14/16
 */
@Entity
@Table(name = "badge_types")
public class BadgeType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String color;

    private String image;

    @ManyToOne
    private Domain domain;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private List<Badge> badges;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Badge> getBadges() {
        return badges;
    }
}