package org.example.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.dto.DailyReportDto;
import org.example.project.service.GoogleDocsReportService;
import org.example.project.service.GoogleSheetsService;
import org.example.project.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class AdminReportController {

    private final ReportService reportService;
    private final GoogleDocsReportService googleDocsReportService;
    private final GoogleSheetsService googleSheetsService;

    @GetMapping("/json")
    public ResponseEntity<List<DailyReportDto>> getJsonReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(reportService.generateReport(start, end));
    }

    @GetMapping("/google-doc")
    public ResponseEntity<String> getGoogleDocReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        List<DailyReportDto> reportData = reportService.generateReport(start, end);
        try {
            String link = googleDocsReportService.generateExternalReport(reportData);
            return ResponseEntity.ok(link);
        } catch (IOException e) {
            log.error("Failed to generate Google Doc", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/sync-sheets")
    public ResponseEntity<String> syncWithSheets(@RequestParam String spreadsheetId) {
        try {
            googleSheetsService.syncObservations(spreadsheetId, "Ответы на форму (1)!A1:G500");
            return ResponseEntity.ok("Sync completed successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Sync failed: " + e.getMessage());
        }
    }
}
