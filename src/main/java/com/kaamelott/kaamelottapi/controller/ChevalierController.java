package com.kaamelott.kaamelottapi.controller;

import com.kaamelott.kaamelottapi.dto.RapportPerformanceDto;
import com.kaamelott.kaamelottapi.entities.Chevalier;
import com.kaamelott.kaamelottapi.entities.ParticipationQuete;
import com.kaamelott.kaamelottapi.entities.ParticipationQueteId;
import com.kaamelott.kaamelottapi.entities.Quete;
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
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chevaliers")
public class ChevalierController {

    @Autowired
    private ChevalierRepository chevalierRepository;

    @Autowired
    private ParticipationQueteRepository participationQueteRepository;

    @Autowired
    private QueteRepository queteRepository;

    @Operation(summary = "Récupérer tous les chevaliers", description = "Retourne la liste de tous les chevaliers. Retourne 204 si la liste est vide.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des chevaliers récupérée avec succès"),
            @ApiResponse(responseCode = "204", description = "Aucun chevalier trouvé")
    })
    @GetMapping
    public ResponseEntity<List<Chevalier>> getAllChevaliers() {
        List<Chevalier> chevaliers = chevalierRepository.findAll();
        if (chevaliers.isEmpty()) { // Vérifie si la liste est vide
            return ResponseEntity.noContent().build(); // 204 si la liste est vide
        }
        return ResponseEntity.ok(chevaliers); // 200 avec la liste
    }

    @Operation(summary = "Créer un nouveau chevalier", description = "Crée un nouveau chevalier et le retourne. Retourne une erreur si l'ID existe déjà.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chevalier créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Un chevalier avec cet ID existe déjà ou données invalides")
    })
    @PostMapping
    public ResponseEntity<?> createChevalier(@Valid @RequestBody Chevalier chevalier) {
        if (chevalier.getId() != null && chevalierRepository.existsById(chevalier.getId())) {
            return ResponseEntity.badRequest().body("Un chevalier avec cet ID existe déjà.");
        }
        // Vérifie que la caractéristique principale est valide
        Chevalier savedChevalier = chevalierRepository.save(chevalier);
        return ResponseEntity.ok(savedChevalier);
    }

    @Operation(summary = "Récupère les quêtes en cours d'un chevalier", description = "Retourne la liste des quêtes avec statut EN_COURS pour un chevalier spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des quêtes en cours retournée"),
            @ApiResponse(responseCode = "204", description = "Aucune quête en cours trouvée"),
            @ApiResponse(responseCode = "404", description = "Chevalier non trouvé")
    })
    @GetMapping("/{idChevalier}/quetes-en-cours")
    public ResponseEntity<List<Quete>> getQuetesEnCours(@PathVariable Integer idChevalier) {
        // check l'existence du chevalier
        chevalierRepository.findById(idChevalier)
                .orElseThrow(() -> new ResourceNotFoundException("Chevalier non trouvé avec l'ID : " + idChevalier));

        // récupère les participations en cours du chevalier
        List<ParticipationQuete> participations = participationQueteRepository
                .findByChevalierIdAndStatutParticipation(idChevalier, ParticipationQuete.StatutParticipation.EN_COURS);

        // map les participations pour obtenir la liste des quêtes
        List<Quete> quetes = participations.stream()
                .map(ParticipationQuete::getQuete)
                .collect(Collectors.toList());

        if (quetes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(quetes);
    }

    @Operation(summary = "Retire un chevalier d'une quête", description = "Supprime la participation d'un chevalier à une quête spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Participation supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Chevalier, quête ou participation non trouvée")
    })
    @DeleteMapping("/{idChevalier}/retirer-quete/{idQuete}")
    public ResponseEntity<Void> retirerChevalierDeQuete(@PathVariable Integer idChevalier, @PathVariable Integer idQuete) {
        // check l'existence du chevalier
        chevalierRepository.findById(idChevalier)
                .orElseThrow(() -> new ResourceNotFoundException("Chevalier non trouvé avec l'ID : " + idChevalier));

        // check l'existence de la quête
        queteRepository.findById(idQuete)
                .orElseThrow(() -> new ResourceNotFoundException("Quête non trouvée avec l'ID : " + idQuete));

        // check l'existence de la participation
        ParticipationQueteId participationId = new ParticipationQueteId();
        participationId.setChevalier(idChevalier);
        participationId.setQuete(idQuete);

        if (!participationQueteRepository.existsById(participationId)) {
            throw new ResourceNotFoundException("Participation non trouvée pour le chevalier " + idChevalier + " et la quête " + idQuete);
        }

        // delete la participation
        participationQueteRepository.deleteById(participationId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Recherche les chevaliers par caractéristique principale", description = "Retourne la liste des chevaliers ayant la caractéristique principale spécifiée.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des chevaliers retournée"),
            @ApiResponse(responseCode = "204", description = "Aucun chevalier trouvé pour cette caractéristique")
    })
    @GetMapping("/caracteristique/{caracteristique}")
    public ResponseEntity<List<Chevalier>> getChevaliersByCaracteristique(@PathVariable String caracteristique) {
        List<Chevalier> chevaliers = chevalierRepository.findByCaracteristiquePrincipale(caracteristique); // Utilise la méthode du repository pour trouver par caractéristique
        if (chevaliers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(chevaliers);
    }

    @GetMapping("/rapport-performance/{idChevalier}")
    public ResponseEntity<RapportPerformanceDto> getRapportPerformance(@PathVariable Integer idChevalier) {
        // check l'existence du chevalier
        chevalierRepository.findById(idChevalier)
                .orElseThrow(() -> new ResourceNotFoundException("Chevalier non trouvé avec l'ID : " + idChevalier));

        // recu les participations du chevalier
        List<ParticipationQuete> participations = participationQueteRepository.findByChevalierId(idChevalier);

        // calcuils
        long quetesTerminees = participations.stream()
                .filter(p -> p.getStatutParticipation() == ParticipationQuete.StatutParticipation.TERMINEE)
                .count();
        long quetesChefExpedition = participations.stream()
                .filter(p -> p.getRole() == ParticipationQuete.Role.CHEF_EXPEDITION)
                .count();
        long quetesEnCoursOuTerminees = participations.stream()
                .filter(p -> p.getStatutParticipation() == ParticipationQuete.StatutParticipation.EN_COURS ||
                        p.getStatutParticipation() == ParticipationQuete.StatutParticipation.TERMINEE)
                .count();
        double tauxSucces = quetesEnCoursOuTerminees > 0 ? (double) quetesTerminees / quetesEnCoursOuTerminees * 100 : 0.0;

        // commentaire du roi le plus fréquent
        String commentaireFrequent = participations.stream()
                .filter(p -> p.getCommentaireRoi() != null && !p.getCommentaireRoi().isEmpty())
                .collect(Collectors.groupingBy(
                        ParticipationQuete::getCommentaireRoi,
                        Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Aucun commentaire");

        // DTO
        RapportPerformanceDto rapport = new RapportPerformanceDto(
                quetesTerminees,
                quetesChefExpedition,
                tauxSucces,
                commentaireFrequent
        );

        return ResponseEntity.ok(rapport);
    }
}
