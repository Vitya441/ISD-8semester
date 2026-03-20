package org.example.project.repository;

import org.example.project.entity.PlantObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PlantObservationRepository extends JpaRepository<PlantObservation, Long> {

    List<PlantObservation> findAllByDiscoveryTimeBetween(LocalDateTime start, LocalDateTime end);
}
