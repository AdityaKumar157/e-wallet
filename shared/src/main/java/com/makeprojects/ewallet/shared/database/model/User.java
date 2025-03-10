package com.makeprojects.ewallet.shared.database.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@With
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID userId;

    private String name;

    @Column(unique = true)
    private String phone;

    @Column(nullable = true, unique = true)
    private String email;

    private String password;

    private String role;

    public String getUserName() {
        return this.phone;
    }
}
