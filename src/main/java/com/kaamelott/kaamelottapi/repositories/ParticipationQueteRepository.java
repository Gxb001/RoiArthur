package com.kaamelott.kaamelottapi.repositories;

import com.kaamelott.kaamelottapi.entities.ParticipationQuete;
import com.kaamelott.kaamelottapi.entities.ParticipationQueteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationQueteRepository extends JpaRepository<ParticipationQuete, ParticipationQueteId> {
    List<ParticipationQuete> findByQueteId(Integer queteId);

    boolean existsByChevalierIdAndQueteId(Integer chevalierId, Integer queteId);
}