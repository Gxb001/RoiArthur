package com.kaamelott.kaamelottapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RapportPerformanceDto {
    private long quetesTerminees;
    private long quetesChefExpedition;
    private double tauxSucces;
    private String commentaireFrequent;

    public RapportPerformanceDto(long quetesTerminees, long quetesChefExpedition, double tauxSucces, String commentaireFrequent) {
        this.quetesTerminees = quetesTerminees;
        this.quetesChefExpedition = quetesChefExpedition;
        this.tauxSucces = tauxSucces;
        this.commentaireFrequent = commentaireFrequent;
    }
}