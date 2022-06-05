package com.dreamgames.rowmatch.repository;

import com.dreamgames.rowmatch.entity.Group;
import com.dreamgames.rowmatch.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    public Optional<Group> findByTournamentAndIsFullAndMaxLevel(Tournament tournament,
                                                                Boolean isFull,
                                                                Integer maxLevel);
}
