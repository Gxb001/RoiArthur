package com.kaamelott.kaamelottapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter// utilisation de Lombok pour générer les getters et setters automatiquement
public class ParticipationQueteRequestDto {
    @NotNull(message = "L'ID du chevalier est requis")
    private Integer idChevalier;

    @NotNull(message = "Le rôle est requis")
    private String role;

    @NotNull(message = "Le statut de participation est requis")
    private String statutParticipation;

    private String commentaireRoi;

    public ParticipationQueteRequestDto() {
        // Constructeur par défaut requis pour la désérialisation JSON
    }
}
