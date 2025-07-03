package com.kaamelott.kaamelottapi.repositories;

import com.kaamelott.kaamelottapi.entities.Chevalier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChevalierRepository extends JpaRepository<Chevalier, Integer> {
    List<Chevalier> findByCaracteristiquePrincipale(String caracteristiquePrincipale);
}
