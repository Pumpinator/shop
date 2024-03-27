package com.shop.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 128, nullable = false, unique = true)
    private String name;
    @Column(length = 64, nullable = false, unique = true)
    private String alias;
    @Column(length = 128, nullable = false)
    private String image;
    private boolean enabled;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;
    @OneToMany(mappedBy = "parent")
    private Set<Category> children = new HashSet<>();

    public Category(Integer id) {
        this.id = id;
    }



    public Category(String name, Category parent) {
        this.id = id;
        this.name = name;
        this.image = "default-image.png";
    }

    public static Category copy(Category category) {
        Category copy = new Category();
        copy.setId(category.getId());
        copy.setName(category.getName());
        copy.setAlias(category.getAlias());
        copy.setImage(category.getImage());
        copy.setEnabled(category.isEnabled());
        return copy;
    }

    public static Category copy(Category category, String name) {
        Category copy = new Category();
        copy.setId(category.getId());
        copy.setName(name);
        copy.setAlias(category.getAlias());
        copy.setImage(category.getImage());
        copy.setEnabled(category.isEnabled());
        return copy;
    }


    @Transient
    public String getImagePath() {
        if (id == null || image == null) return "/img/default-image.png";
        return "/category-images/" + this.id + "/" + this.image;
    }
}
