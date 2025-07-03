package com.kaamelott.kaamelottapi.services;

import com.kaamelott.kaamelottapi.entities.ParticipationQuete;
import com.kaamelott.kaamelottapi.entities.Quete;
import com.kaamelott.kaamelottapi.repositories.ParticipationQueteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class QueteService {
    @Autowired
    private ParticipationQueteRepository participationQueteRepository;

    public String determineStatut(Quete quete) { // Détermine le statut de la quête en fonction des dates et des participations
        LocalDate today = LocalDate.now();
        if (today.isBefore(quete.getDateAssignation())) {
            return "À Venir";
        } else if (today.isAfter(quete.getDateEcheance())) {
            return participationQueteRepository.findByQueteId(quete.getId()).stream()
                    .anyMatch(p -> p.getStatutParticipation() == ParticipationQuete.StatutParticipation.TERMINEE)
                    ? "Terminée" : "Ratée";
        } else {
            return "En Cours";
        }
    }
}
