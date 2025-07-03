package com.kaamelott.kaamelottapi.controller;

import com.kaamelott.kaamelottapi.dto.ParticipationQueteRequestDto;
import com.kaamelott.kaamelottapi.entities.Chevalier;
import com.kaamelott.kaamelottapi.entities.ParticipationQuete;
import com.kaamelott.kaamelottapi.entities.Quete;
import com.kaamelott.kaamelottapi.exceptions.BadRequestException;
import com.kaamelott.kaamelottapi.exceptions.ResourceNotFoundException;
import com.kaamelott.kaamelottapi.repositories.ChevalierRepository;
import com.kaamelott.kaamelottapi.repositories.ParticipationQueteRepository;
import com.kaamelott.kaamelottapi.repositories.QueteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/quetes")
public class QueteController {

    @Autowired
    private ParticipationQueteRepository participationQueteRepository;

    @Autowired
    private ChevalierRepository chevalierRepository;

    @Autowired
    private QueteRepository queteRepository;

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
}
