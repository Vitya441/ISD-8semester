package org.example.project.dto;

import lombok.Data;
import org.example.project.entity.enums.Criticality;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class DailyReportDto {
    private LocalDate date;
    // Мапа хранит: "slot_criticality" -> суммарная площадь
    // Например: "1_LOW" (Слот 08-12, Низкая критичность)
    private Map<String, Double> values = new HashMap<>();

    public DailyReportDto(LocalDate date) { this.date = date; }

    public void addArea(int slot, Criticality criticality, Double area) {
        String key = slot + "_" + criticality.name();
        values.put(key, values.getOrDefault(key, 0.0) + area);
    }
}