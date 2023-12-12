package com.shop.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 40, nullable = false)
    private String name;
    @Column(length = 150, nullable = false)
    private String description;
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
