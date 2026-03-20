package org.example.project.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import lombok.RequiredArgsConstructor;
import org.example.project.entity.Shift;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private final Calendar calendarService;

    public String addShiftToCalendar(String userCalendarId, Shift shift) throws IOException {
        Event event = new Event()
                .setSummary("Дежурство: " + shift.getZone().getName())
                .setDescription("Мониторинг растений в рамках проекта EcoMonitoring");

        DateTime startDateTime = new DateTime(shift.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        event.setStart(new EventDateTime().setDateTime(startDateTime));

        DateTime endDateTime = new DateTime(shift.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        event.setEnd(new EventDateTime().setDateTime(endDateTime));

        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(List.of(
                        new EventReminder().setMethod("popup").setMinutes(1440),
                        new EventReminder().setMethod("popup").setMinutes(60)
                ));
        event.setReminders(reminders);

        Event createdEvent = calendarService.events().insert(userCalendarId, event).execute();
        return createdEvent.getId();
    }
}
