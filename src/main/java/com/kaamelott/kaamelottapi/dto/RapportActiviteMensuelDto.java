package com.kaamelott.kaamelottapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RapportActiviteMensuelDto {
    private long quetesInitiees;
    private long quetesTerminees;
    private long chevaliersActifs;

    public RapportActiviteMensuelDto(long quetesInitiees, long quetesTerminees, long chevaliersActifs) {
        this.quetesInitiees = quetesInitiees;
        this.quetesTerminees = quetesTerminees;
        this.chevaliersActifs = chevaliersActifs;
    }
}
