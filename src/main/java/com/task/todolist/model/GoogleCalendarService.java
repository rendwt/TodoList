package com.task.todolist.model;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.List;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.api.services.calendar.CalendarScopes;
import java.io.IOException;
import java.util.Collections;

public class GoogleCalendarService {
    private Calendar calendarService;
    private static final String APPLICATION_NAME = "TodoList";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tkns";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        return credential;
    }

    public GoogleCalendarService(){
        final NetHttpTransport HTTP_TRANSPORT;
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Credential credential = getCredentials(HTTP_TRANSPORT);
            credential = refreshExpiredToken(credential);
            calendarService =
                    new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                            .setApplicationName(APPLICATION_NAME)
                            .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Event createEvent(Event event, String calendarId) throws IOException {
        if (calendarService != null) {
            return calendarService.events().insert(calendarId, event).execute();
        } else {
            throw new IllegalStateException("Calendar service is not initialized.");
        }
    }


    public List<Event> getEvents(String calendarId) throws IOException {
        if (calendarService != null) {
            Events events = calendarService.events().list(calendarId).execute();
            return events.getItems();
        } else {
            throw new IllegalStateException("Calendar service is not initialized.");
        }
    }

    public Event updateEvent(Event event, String calendarId) throws IOException {
        return calendarService.events().update(calendarId, event.getId(), event).execute();
    }

    public void deleteEvent(String eventId, String calendarId) throws IOException {
        calendarService.events().delete(calendarId, eventId).execute();
    }
    public Credential refreshExpiredToken(Credential credential) throws IOException {
        if (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 60) {
            credential.refreshToken();
        }
        return credential;
    }
}