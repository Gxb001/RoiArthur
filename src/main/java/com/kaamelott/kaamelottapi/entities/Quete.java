package com.kaamelott.kaamelottapi.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "quete")
public class Quete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nom_quete", nullable = false, length = 100)
    private String nomQuete;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulte difficulte;

    @Column(name = "date_assignation", nullable = false)
    private LocalDate dateAssignation;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    public enum Difficulte {
        FACILE, MOYENNE, ABERANTE
    }
}
