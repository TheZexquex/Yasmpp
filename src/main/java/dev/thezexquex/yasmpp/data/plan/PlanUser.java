package dev.thezexquex.yasmpp.data.plan;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class PlanUser {
    private final int id;
    private final String name;
    private final UUID uniqueId;
    private final LocalDateTime globalFirstLogin;
    private final HashMap<String, LocalDateTime> serverFirstJoins;
    private final Set<PlaySession> sessions;
    private Duration sessionPlayTime;

    public PlanUser(int id, UUID uniqueId, String name,  LocalDateTime firstLogin) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.name = name;
        this.globalFirstLogin = firstLogin;
        this.serverFirstJoins = new HashMap<>();
        this.sessions = new HashSet<>();
    }

    public void addSession(PlaySession session) {
        this.sessions.add(session);
    }

    public void addFirstLogin(String server, LocalDateTime firstLogin) {
        this.serverFirstJoins.put(server, firstLogin);
    }

    public Set<PlaySession> sessions() {
        return sessions;
    }

    public HashMap<String, LocalDateTime> serverFirstJoins() {
        return serverFirstJoins;
    }

    public Duration sessionPlayTime() {
        return sessionPlayTime;
    }

    public void sessionPlayTime(Duration sessionPlayTime) {
        this.sessionPlayTime = sessionPlayTime;
    }

    public LocalDateTime globalFirstLogin() {
        return globalFirstLogin;
    }

    public String name() {
        return name;
    }

    public UUID uniqueId() {
        return uniqueId;
    }

    public int id() {
        return id;
    }

    public Optional<PlaySession> firstSessionServer(String server) {
        return sessions.stream()
                .filter(playSession -> playSession.serverName().equals(server))
                .max((playSession1, playSession2) -> playSession1.sessionStartTime().isBefore(playSession2.sessionStartTime()) ? 1 : -1);
    }

    public Optional<PlaySession> lastSessionServer(String server) {
        return sessions.stream()
                .filter(playSession -> playSession.serverName().equals(server))
                .max((playSession1, playSession2) -> playSession1.sessionStartTime().isAfter(playSession2.sessionStartTime()) ? 1 : -1);
    }

    public Optional<PlaySession> lastSessionGlobal() {
        return sessions.stream()
                .max((playSession1, playSession2) -> playSession1.sessionStartTime().isAfter(playSession2.sessionStartTime()) ? 1 : -1);
    }

    public Duration getOnTimeTill(LocalDateTime dateTime) {
        return sessions.stream().filter(playSession -> playSession.sessionStartTime().isAfter(dateTime))
                .map(playSession -> Duration.between(playSession.sessionStartTime(), playSession.sessionEndTime()))
                .reduce(Duration::plus).orElse(Duration.ZERO);
    }

    public Duration getOnTimeDay() {
        return getOnTimeTill(LocalDate.now().atTime(0, 1));
    }

    public Duration getOnTimeWeek() {
        return getOnTimeTill(LocalDateTime.now().minusDays(Duration.ofDays(7).toDays()));
    }

    public Duration getOnTimeMonth() {
        return getOnTimeTill(LocalDateTime.now().minusDays(Duration.ofDays(30).toDays()));
    }

    public Duration getOnTimeYear() {
        return getOnTimeTill(LocalDate.ofYearDay(LocalDate.now().getYear(), 1).atTime(0, 0));
    }

    public Duration getOnTimeTotal() {
        return getOnTimeTill(LocalDateTime.MIN);
    }


    public Duration getOnTimeTillServer(LocalDateTime dateTime, String server) {
        return sessions.stream()
                .filter(playSession -> playSession.serverName().equals(server) && playSession.sessionStartTime().isAfter(dateTime))
                .map(playSession -> Duration.between(playSession.sessionStartTime(), playSession.sessionEndTime()))
                .reduce(Duration::plus).orElse(Duration.ZERO);
    }

    public Duration getOnTimeDayServer(String server) {
        return getOnTimeTillServer(LocalDate.now().atTime(0, 1), server);
    }

    public Duration getOnTimeWeekServer(String server) {
        return getOnTimeTillServer(LocalDateTime.now().minusDays(Duration.ofDays(7).toDays()), server);
    }

    public Duration getOnTimeMonthServer(String server) {
        return getOnTimeTillServer(LocalDateTime.now().minusDays(Duration.ofDays(30).toDays()), server);
    }

    public Duration getOnTimeYearServer(String server) {
        return getOnTimeTillServer(LocalDate.ofYearDay(LocalDate.now().getYear(), 1).atTime(0, 0), server);
    }

    public Duration getOnTimeTotalServer(String server) {
        return getOnTimeTillServer(LocalDateTime.MIN, server);
    }
}
