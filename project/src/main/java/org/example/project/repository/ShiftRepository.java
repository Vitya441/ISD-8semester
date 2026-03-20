package org.example.project.repository;

import org.example.project.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    @Query("SELECT s FROM Shift s WHERE s.employee.email = :email " +
            "AND :time BETWEEN s.startTime AND s.endTime")
    Optional<Shift> findActiveShift(String email, LocalDateTime time);

    List<Shift> findAllByEmployeeId(Long employeeId);

    Optional<Shift> findActiveByEmailAndTime(String employeeEmailea, LocalDateTime discoveryTime);

    boolean existsOverlappingShift(Long id, LocalDateTime startTime, LocalDateTime endTime);
}
