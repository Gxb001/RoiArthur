package com.kaamelott.kaamelottapi.controller;

import com.kaamelott.kaamelottapi.entities.Chevalier;
import com.kaamelott.kaamelottapi.repositories.ChevalierRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chevaliers")
public class ChevalierController {

    @Autowired
    private ChevalierRepository chevalierRepository;

    @Operation(summary = "Récupérer tous les chevaliers", description = "Retourne la liste de tous les chevaliers. Retourne 204 si la liste est vide.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des chevaliers récupérée avec succès"),
            @ApiResponse(responseCode = "204", description = "Aucun chevalier trouvé")
    })
    @GetMapping
    public ResponseEntity<List<Chevalier>> getAllChevaliers() {
        List<Chevalier> chevaliers = chevalierRepository.findAll();
        if (chevaliers.isEmpty()) {
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
        Chevalier savedChevalier = chevalierRepository.save(chevalier);
        return ResponseEntity.ok(savedChevalier);
    }
}
