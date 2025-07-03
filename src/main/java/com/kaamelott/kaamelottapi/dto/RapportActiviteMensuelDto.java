package com.kaamelott.kaamelottapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RapportActiviteMensuelDto {
    private long quetesInitiees;
    private long quetesTerminees;
    private long chevaliersActifs;
    private String nomQueteEchouee;
    private String chevaliersEchouee;

    public RapportActiviteMensuelDto(long quetesInitiees, long quetesTerminees, long chevaliersActifs,
                                     String nomQueteEchouee, String chevaliersEchouee) {
        this.quetesInitiees = quetesInitiees;
        this.quetesTerminees = quetesTerminees;
        this.chevaliersActifs = chevaliersActifs;
        this.nomQueteEchouee = nomQueteEchouee;
        this.chevaliersEchouee = chevaliersEchouee;
    }
}
