package com.kaamelott.kaamelottapi.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class ParticipationQueteId implements Serializable {
    private Integer chevalier;
    private Integer quete;
}
