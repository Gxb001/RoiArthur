package com.kaamelott.kaamelottapi.entities;


import jakarta.persistence.*;
import lombok.Data;

@Data // Lombok genere les getters, setters, toString, equals et hashCode
@Entity // Indique que cette classe est une entité JPA
@Table(name = "chevalier") // Nom de la table dans la base de données
public class Chevalier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 50)
    private String titre;

    @Column(name = "caracteristique_principale", length = 50)
    private String caracteristiquePrincipale;

    @Column(name = "niveau_bravoure")
    private Integer niveauBravoure;
}
