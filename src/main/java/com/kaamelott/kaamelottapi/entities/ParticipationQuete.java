package com.kaamelott.kaamelottapi.entities;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "participation_quete")
@IdClass(ParticipationQueteId.class)
public class ParticipationQuete {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_chevalier")
    private Chevalier chevalier;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_quete")
    private Quete quete;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_participation", nullable = false)
    private StatutParticipation statutParticipation;

    @Column(name = "commentaire_roi", length = 100)
    private String commentaireRoi;

    public enum Role {
        CHEF_EXPEDITION, ACCOLYTE, RESERVE
    }

    public enum StatutParticipation {
        EN_COURS, TERMINEE, ECHOUEE_LAMENTABLEMENT, ABANDONNEE_PAR_FLEME
    }
}
