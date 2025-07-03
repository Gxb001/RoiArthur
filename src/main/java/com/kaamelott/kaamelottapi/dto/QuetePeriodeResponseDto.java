package com.kaamelott.kaamelottapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuetePeriodeResponseDto {
    private String nomQuete;
    private long nombreChevaliers;
    private String statut;
    private long duree;
    private String difficulte;

    public QuetePeriodeResponseDto(String nomQuete, long nombreChevaliers, String statut, long duree, String difficulte) {
        this.nomQuete = nomQuete;
        this.nombreChevaliers = nombreChevaliers;
        this.statut = statut;
        this.duree = duree;
        this.difficulte = difficulte;
    }
}
