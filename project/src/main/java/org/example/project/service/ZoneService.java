package org.example.project.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.entity.Zone;
import org.example.project.repository.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    @Transactional
    public Zone saveZone(Zone zone) {
        log.info("Registering new monitoring zone: {}", zone.getName());
        return zoneRepository.save(zone);
    }

    @Transactional(readOnly = true)
    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Zone getById(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Зона с ID " + id + " не найдена"));
    }
}