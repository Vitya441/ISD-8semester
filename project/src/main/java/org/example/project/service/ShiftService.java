package org.example.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.dto.ShiftRequest;
import org.example.project.entity.Shift;
import org.example.project.entity.User;
import org.example.project.entity.Zone;
import org.example.project.repository.ShiftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final GoogleCalendarService googleCalendarService;
    private final UserService userService;
    private final ZoneService zoneService;

    @Transactional
    public Shift createShift(ShiftRequest request) {
        log.info("Creating shift for employee ID: {} in zone: {}", request.getEmployeeId(), request.getZoneId());

        User employee = userService.getById(request.getEmployeeId());
        Zone zone = zoneService.getById(request.getZoneId());

        boolean isOverlapping = shiftRepository.existsOverlappingShift(
                employee.getId(), request.getStartTime(), request.getEndTime());

        if (isOverlapping) {
            throw new IllegalStateException("У данного сотрудника уже есть дежурство в этот интервал времени");
        }

        Shift shift = new Shift();
        shift.setEmployee(employee);
        shift.setZone(zone);
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());

        Shift savedShift = shiftRepository.save(shift);

        try {
            if (employee.getGoogleCalendarId() != null && !employee.getGoogleCalendarId().isBlank()) {
                googleCalendarService.addShiftToCalendar(employee.getGoogleCalendarId(), savedShift);
            }
        } catch (Exception e) {
            log.error("Ошибка синхронизации с Google Calendar для пользователя {}: {}",
                    employee.getEmail(), e.getMessage());
            // Мы не кидаем exception дальше, чтобы дежурство в нашей базе сохранилось
        }

        return savedShift;
    }

    @Transactional(readOnly = true)
    public List<Shift> findShiftsByEmployee(Long employeeId) {
        return shiftRepository.findAllByEmployeeId(employeeId);
    }
}
