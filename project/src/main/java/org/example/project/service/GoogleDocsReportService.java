package org.example.project.service;

import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.BatchUpdateDocumentRequest;
import com.google.api.services.docs.v1.model.Document;
import com.google.api.services.docs.v1.model.InsertTableRequest;
import com.google.api.services.docs.v1.model.InsertTextRequest;
import com.google.api.services.docs.v1.model.Location;
import com.google.api.services.docs.v1.model.Request;
import com.google.api.services.docs.v1.model.TableCellLocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.dto.DailyReportDto;
import org.example.project.entity.enums.Criticality;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleDocsReportService {

    private final Docs docsService;

    public String generateExternalReport(List<DailyReportDto> data) throws IOException {
        Document document = docsService.documents()
                .create(new Document().setTitle("Отчет по мониторингу растений " + System.currentTimeMillis()))
                .execute();
        String docId = document.getDocumentId();

        List<Request> requests = new ArrayList<>();

        requests.add(new Request().setInsertText(new InsertTextRequest()
                .setText("СВОДНЫЙ ОТЧЕТ ПО РЕЗУЛЬТАТАМ ДЕЖУРСТВ\n\n")
                .setLocation(new Location().setIndex(1))));

        int numRows = data.size() + 1;
        int numCols = 10;
        requests.add(new Request().setInsertTable(new InsertTableRequest()
                .setRows(numRows)
                .setColumns(numCols)
                .setLocation(new Location().setIndex(1))));

        docsService.documents().batchUpdate(docId, new BatchUpdateDocumentRequest().setRequests(requests)).execute();
        requests.clear();

        String[] headers = {"Дата",
                "08-12 (Н)", "08-12 (С)", "08-12 (В)",
                "12-16 (Н)", "12-16 (С)", "12-16 (В)",
                "16-20 (Н)", "16-20 (С)", "16-20 (В)"};

        fillRow(requests, 0, headers);

        // 5. Заполняем данные
        for (int i = 0; i < data.size(); i++) {
            DailyReportDto day = data.get(i);
            String[] rowData = new String[10];
            rowData[0] = day.getDate().toString();

            // Маппинг данных из DailyReportDto в массив колонок
            rowData[1] = formatVal(day, 1, Criticality.LOW);
            rowData[2] = formatVal(day, 1, Criticality.MEDIUM);
            rowData[3] = formatVal(day, 1, Criticality.HIGH);

            rowData[4] = formatVal(day, 2, Criticality.LOW);
            rowData[5] = formatVal(day, 2, Criticality.MEDIUM);
            rowData[6] = formatVal(day, 2, Criticality.HIGH);

            rowData[7] = formatVal(day, 3, Criticality.LOW);
            rowData[8] = formatVal(day, 3, Criticality.MEDIUM);
            rowData[9] = formatVal(day, 3, Criticality.HIGH);

            fillRow(requests, i + 1, rowData);
        }

        // 6. Применяем заполнение
        docsService.documents().batchUpdate(docId, new BatchUpdateDocumentRequest().setRequests(requests)).execute();

        log.info("Google Doc report generated: {}", docId);
        return "https://docs.google.com/document/d/" + docId + "/edit";
    }

    private void fillRow(List<Request> requests, int rowIndex, String[] values) {
        for (int colIndex = 0; colIndex < values.length; colIndex++) {
            String text = (values[colIndex] != null) ? values[colIndex] : "-";

            TableCellLocation cellLoc = new TableCellLocation()
                    .setRowIndex(rowIndex)
                    .setColumnIndex(colIndex);

            cellLoc.set("tableStartLocation", new Location().setIndex(2));

            Location location = new Location();
            location.set("tableCellLocation", cellLoc);

            InsertTextRequest insertText = new InsertTextRequest()
                    .setText(text);

            insertText.set("location", location);

            requests.add(new Request().setInsertText(insertText));
        }
    }

    private String formatVal(DailyReportDto day, int slot, Criticality crit) {
        Double val = day.getValues().get(slot + "_" + crit.name());
        return (val != null && val > 0) ? String.format("%.2f", val) : "-";
    }
}
