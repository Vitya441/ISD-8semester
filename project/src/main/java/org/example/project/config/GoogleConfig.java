package org.example.project.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class GoogleConfig {

    private static final String APPLICATION_NAME = "EcoMonitoringSystem";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Bean
    public Calendar calendarService() throws Exception {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(httpTransport, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Bean
    public Sheets sheetsService() throws Exception {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Bean
    public Docs docsService() throws Exception {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Docs.Builder(httpTransport, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private HttpRequestInitializer getCredentials() throws IOException {
        InputStream in = getClass().getResourceAsStream("/credentials.json");
        if (in == null) {
            throw new IOException("Файл credentials.json не найден в resources!");
        }

        return new HttpCredentialsAdapter(GoogleCredentials.fromStream(in)
                .createScoped(List.of(
                        "https://www.googleapis.com/auth/calendar",      // CalendarScopes.CALENDAR
                        "https://www.googleapis.com/auth/spreadsheets",  // SheetsScopes.SPREADSHEETS_READONLY
                        "https://www.googleapis.com/auth/documents",     // DocsScopes.DOCUMENTS
                        "https://www.googleapis.com/auth/drive.file"     // DriveScopes.DRIVE_FILE
                )));
    }
}