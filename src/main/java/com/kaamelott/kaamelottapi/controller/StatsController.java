package com.kaamelott.kaamelottapi.controller;

import com.kaamelott.kaamelottapi.dto.RapportActiviteMensuelDto;
import com.kaamelott.kaamelottapi.entities.ParticipationQuete;
import com.kaamelott.kaamelottapi.repositories.ParticipationQueteRepository;
import com.kaamelott.kaamelottapi.repositories.QueteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    @Autowired
    private QueteRepository queteRepository;

    @Autowired
    private ParticipationQueteRepository participationQueteRepository;

    @Operation(summary = "Rapport d'activité mensuel", description = "Retourne le nombre de quêtes initiées, terminées et de chevaliers actifs pour un mois et une année donnés.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rapport mensuel retourné"),
            @ApiResponse(responseCode = "204", description = "Aucune activité trouvée pour la période")
    })
    @GetMapping("/rapport-activite-mensuel")
    public ResponseEntity<RapportActiviteMensuelDto> getRapportActiviteMensuel(
            @RequestParam("mois") Integer mois,
            @RequestParam("annee") Integer annee) {
        YearMonth yearMonth = YearMonth.of(annee, mois);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Initié
        long quetesInitiees = queteRepository.findAll().stream()
                .filter(quete -> quete.getDateAssignation() != null &&
                        !quete.getDateAssignation().isBefore(startDate) &&
                        !quete.getDateAssignation().isAfter(endDate))
                .count();

        // Terminée
        long quetesTerminees = participationQueteRepository.findAll().stream()
                .filter(p -> p.getStatutParticipation() == ParticipationQuete.StatutParticipation.TERMINEE &&
                        p.getQuete().getDateAssignation() != null &&
                        !p.getQuete().getDateAssignation().isBefore(startDate) &&
                        !p.getQuete().getDateAssignation().isAfter(endDate))
                .map(ParticipationQuete::getQuete)
                .distinct()
                .count();

        // chevaliers actifs
        long chevaliersActifs = participationQueteRepository.findAll().stream()
                .filter(p -> p.getQuete().getDateAssignation() != null &&
                        !p.getQuete().getDateAssignation().isBefore(startDate) &&
                        !p.getQuete().getDateAssignation().isAfter(endDate))
                .map(p -> p.getChevalier().getId())
                .collect(Collectors.toSet())
                .size();

        RapportActiviteMensuelDto rapport = new RapportActiviteMensuelDto(
                quetesInitiees,
                quetesTerminees,
                chevaliersActifs
        );

        if (quetesInitiees == 0 && quetesTerminees == 0 && chevaliersActifs == 0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rapport);
    }
}
