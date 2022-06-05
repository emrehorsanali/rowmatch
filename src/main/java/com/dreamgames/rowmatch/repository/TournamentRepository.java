package com.dreamgames.rowmatch.repository;

import com.dreamgames.rowmatch.entity.Ranking;
import com.dreamgames.rowmatch.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    public Optional<Tournament> findFirstByOrderByIdDesc();
}
