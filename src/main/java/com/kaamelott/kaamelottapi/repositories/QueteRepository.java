package com.kaamelott.kaamelottapi.repositories;

import com.kaamelott.kaamelottapi.entities.Quete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface QueteRepository extends JpaRepository<Quete, Integer> {
    @Query("SELECT q FROM Quete q WHERE q.difficulte = :difficulte AND (q.dateAssignation >= :date OR EXISTS (SELECT p FROM ParticipationQuete p WHERE p.quete = q AND p.statutParticipation = 'EN_COURS'))")
        // requete pour les quêtes de la difficulté spécifiée et celles en cours
    List<Quete> findByDifficulteAndDateAssignationAfterOrDateAssignation(Quete.Difficulte difficulte, LocalDate date);
}
