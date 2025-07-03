package com.kaamelott.kaamelottapi.controller;

import com.kaamelott.kaamelottapi.dto.ParticipationQueteRequestDto;
import com.kaamelott.kaamelottapi.dto.QuetePeriodeResponseDto;
import com.kaamelott.kaamelottapi.entities.Chevalier;
import com.kaamelott.kaamelottapi.entities.ParticipationQuete;
import com.kaamelott.kaamelottapi.entities.Quete;
import com.kaamelott.kaamelottapi.exceptions.BadRequestException;
import com.kaamelott.kaamelottapi.exceptions.ResourceNotFoundException;
import com.kaamelott.kaamelottapi.repositories.ChevalierRepository;
import com.kaamelott.kaamelottapi.repositories.ParticipationQueteRepository;
import com.kaamelott.kaamelottapi.repositories.QueteRepository;
import com.kaamelott.kaamelottapi.services.QueteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/quetes")
public class QueteController {

    @Autowired
    private ParticipationQueteRepository participationQueteRepository;

    @Autowired
    private ChevalierRepository chevalierRepository;

    @Autowired
    private QueteRepository queteRepository;

    @Autowired
    private QueteService queteService;

    // anotation swagger pour la documentation de l'API
    @Operation(
            summary = "Récupère les participants d'une quête",
            description = "Retourne la liste des participations (chevaliers et rôles) pour une quête donnée par son ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des participants trouvée"),
            @ApiResponse(responseCode = "204", description = "Aucun participant trouvé pour cette quête"),
            @ApiResponse(responseCode = "404", description = "Quête non trouvée")
    })
    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipationQuete>> getParticipantsByQueteId(@PathVariable Integer id) {
        List<ParticipationQuete> participants = participationQueteRepository.findByQueteId(id);
        if (participants.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(participants);
    }

    // anotation swagger pour la documentation de l'API
    @Operation(summary = "Assigne un chevalier à une quête", description = "Crée une nouvelle participation pour un chevalier à une quête spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Participation créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou chevalier déjà assigné à la quête"),
            @ApiResponse(responseCode = "404", description = "Quête ou chevalier non trouvé")
    })
    @PostMapping("/{idQuete}/assigner-chevalier")
    public ResponseEntity<ParticipationQuete> assignerChevalier(
            @PathVariable Integer idQuete,
            @Valid @RequestBody ParticipationQueteRequestDto request) {
        try {
            // check l'existence de la quête
            Quete quete = queteRepository.findById(idQuete)
                    .orElseThrow(() -> new ResourceNotFoundException("Quête non trouvée avec l'ID : " + idQuete));

            // check l'existence du chevalier
            Chevalier chevalier = chevalierRepository.findById(request.getIdChevalier())
                    .orElseThrow(() -> new ResourceNotFoundException("Chevalier non trouvé avec l'ID : " + request.getIdChevalier()));

            // check si le chevalier est déjà assigné à la quête
            if (participationQueteRepository.existsByChevalierIdAndQueteId(request.getIdChevalier(), idQuete)) {
                throw new IllegalArgumentException("Le chevalier est déjà assigné à cette quête.");
            }

            // creation
            ParticipationQuete participation = new ParticipationQuete();
            participation.setChevalier(chevalier);
            participation.setQuete(quete);
            participation.setRole(ParticipationQuete.Role.valueOf(request.getRole().toUpperCase()));
            participation.setStatutParticipation(ParticipationQuete.StatutParticipation.valueOf(request.getStatutParticipation().toUpperCase()));
            participation.setCommentaireRoi(request.getCommentaireRoi());

            // save
            ParticipationQuete savedParticipation = participationQueteRepository.save(participation);
            return ResponseEntity.ok(savedParticipation);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Erreur : " + e.getMessage());
        }
    }

    @Operation(summary = "Récupère les quêtes aberrantes non terminées", description = "Retourne les quêtes avec difficulté ABERANTE qui n'ont pas encore commencé ou sont en cours.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des quêtes aberrantes retournée"),
            @ApiResponse(responseCode = "204", description = "Aucune quête aberrante trouvée")
    })
    @GetMapping("/difficulte-aberrante")
    public ResponseEntity<List<Quete>> getQuetesAberrantes() {
        List<Quete> quetes = queteRepository.findByDifficulteAndDateAssignationAfterOrDateAssignation(
                Quete.Difficulte.ABERANTE, LocalDate.now());
        if (quetes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(quetes);
    }

    @Operation(summary = "Récupère les quêtes avec effectif manquant", description = "Retourne les quêtes ayant moins de chevaliers assignés que le minimum spécifié.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des quêtes avec effectif manquant retournée"),
            @ApiResponse(responseCode = "204", description = "Aucune quête avec effectif manquant trouvée")
    })
    @GetMapping("/effectif-manquant")
    public ResponseEntity<List<Quete>> getQuetesEffectifManquant(@RequestParam("min") Integer min) {
        List<Quete> quetes = queteRepository.findAll().stream()
                .filter(quete -> participationQueteRepository.countByQueteId(quete.getId()) < min)
                .toList();
        if (quetes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(quetes);
    }

    @Operation(summary = "Récupère les quêtes les plus longues", description = "Retourne les X quêtes les plus longues basées sur la durée entre date_assignation et date_echeance, triées par ordre décroissant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des quêtes les plus longues retournée"),
            @ApiResponse(responseCode = "204", description = "Aucune quête trouvée")
    })
    @GetMapping("/les-plus-longues")
    public ResponseEntity<List<Quete>> getQuetesLesPlusLongues(@RequestParam("limit") Integer limit) {
        List<Quete> quetes = queteRepository.findAll().stream()
                .filter(quete -> quete.getDateAssignation() != null && quete.getDateEcheance() != null)
                .sorted(Comparator.comparingLong((Quete quete) ->
                        ChronoUnit.DAYS.between(quete.getDateAssignation(), quete.getDateEcheance())).reversed())
                .limit(limit)
                .toList();
        if (quetes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(quetes);
    }

    @Operation(summary = "Récupère les quêtes dans une période donnée", description = "Retourne les quêtes dont la période d'activité se chevauche avec la période spécifiée, avec leur nom, nombre de chevaliers, statut, durée et difficulté.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des quêtes retournée"),
            @ApiResponse(responseCode = "204", description = "Aucune quête trouvée")
    })
    @GetMapping("/periode")
    public ResponseEntity<List<QuetePeriodeResponseDto>> getQuetesByPeriode(
            @RequestParam("date_debut") String dateDebut,
            @RequestParam("date_fin") String dateFin) {
        LocalDate startDate = LocalDate.parse(dateDebut);
        LocalDate endDate = LocalDate.parse(dateFin);

        List<QuetePeriodeResponseDto> quetes = queteRepository.findAll().stream()
                .filter(quete -> quete.getDateAssignation() != null && quete.getDateEcheance() != null &&
                        !quete.getDateAssignation().isAfter(endDate) && !quete.getDateEcheance().isBefore(startDate))
                .map(quete -> {
                    long nombreChevaliers = participationQueteRepository.countByQueteId(quete.getId());
                    String statut = queteService.determineStatut(quete);
                    long duree = ChronoUnit.DAYS.between(quete.getDateAssignation(), quete.getDateEcheance());
                    return new QuetePeriodeResponseDto(
                            quete.getNomQuete(),
                            nombreChevaliers,
                            statut,
                            duree,
                            quete.getDifficulte().toString()
                    );
                })
                .collect(Collectors.toList());

        if (quetes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(quetes);
    }

}
