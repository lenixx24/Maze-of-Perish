package ua.edu.ukma.service;

import ua.edu.ukma.model.UserProfile;
import ua.edu.ukma.security.PasswordHasher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class UserStorage {

    private static final int START_GOLD = 0;
    private static final String START_UNLOCKED_LEVELS = "1";

    private final Path filePath;

    public UserStorage() {
        this.filePath = Path.of("data", "users.properties");
    }

    public UserProfile register(String username, String password) {
        username = normalizeUsername(username);
        validateCredentials(username, password);

        Properties users = loadUsers();

        if (users.containsKey(username)) throw new IllegalArgumentException("A user with this username already exists.");

        String salt = PasswordHasher.createSalt();
        String passwordHash = PasswordHasher.hashPassword(password, salt);

        UserProfile profile = new UserProfile(username, START_GOLD, parseUnlockedLevels(START_UNLOCKED_LEVELS), false, false);
        users.setProperty(username, formatUserValue(salt, passwordHash, profile));
        saveUsers(users);

        return profile;
    }

    public UserProfile login(String username, String password) {
        username = normalizeUsername(username);
        validateCredentials(username, password);

        Properties users = loadUsers();
        String savedValue = users.getProperty(username);

        if (savedValue == null) throw new IllegalArgumentException("No user with this username was found.");

        String[] parts = savedValue.split(":", -1);

        if (parts.length != 3 && parts.length != 4 && parts.length != 6) throw new IllegalStateException("The users file is corrupted.");

        String salt = parts[0];
        String passwordHash = parts[1];
        int gold = Integer.parseInt(parts[2]);
        Set<Integer> unlockedLevels = parts.length >= 4 ? parseUnlockedLevels(parts[3]) : parseUnlockedLevels(START_UNLOCKED_LEVELS);
        boolean introSeen = parts.length >= 6 && Boolean.parseBoolean(parts[4]);
        boolean endingCompleted = parts.length >= 6 && Boolean.parseBoolean(parts[5]);

        if (!PasswordHasher.verifyPassword(password, salt, passwordHash)) throw new IllegalArgumentException("Incorrect password.");

        UserProfile profile = new UserProfile(username, gold, unlockedLevels, introSeen, endingCompleted);
        if (parts.length != 6) saveResources(profile);
        return profile;
    }

    public void saveResources(UserProfile userProfile) {
        Properties users = loadUsers();
        String savedValue = users.getProperty(userProfile.getUsername());
        if (savedValue == null) throw new IllegalArgumentException("User was not found.");
        String[] parts = savedValue.split(":", -1);
        if (parts.length != 3 && parts.length != 4 && parts.length != 6) throw new IllegalStateException("The users file is corrupted.");
        users.setProperty(userProfile.getUsername(), formatUserValue(parts[0], parts[1], userProfile));
        saveUsers(users);
    }

    private String formatUserValue(String salt, String passwordHash, UserProfile profile) {
        return salt + ":" + passwordHash + ":" + profile.getGold() + ":" + formatUnlockedLevels(profile.getUnlockedLevels()) + ":" + profile.isIntroSeen() + ":" + profile.isEndingCompleted();
    }

    private Set<Integer> parseUnlockedLevels(String value) {
        if (value == null || value.isBlank()) return Set.of(1);
        Set<Integer> levels = Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(token -> !token.isBlank())
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        levels.add(1);
        return levels;
    }

    private String formatUnlockedLevels(Set<Integer> unlockedLevels) {
        return unlockedLevels.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private Properties loadUsers() {
        Properties users = new Properties();
        if (!Files.exists(filePath)) return users;
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            users.load(inputStream);
            return users;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read the users file.", exception);
        }
    }

    private void saveUsers(Properties users) {
        try {
            Files.createDirectories(filePath.getParent());
            try (OutputStream outputStream = Files.newOutputStream(filePath)) {
                users.store(outputStream, "Maze of Perish users");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to save the users file.", exception);
        }
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim();
    }

    private void validateCredentials(String username, String password) {
        if (username.isBlank()) throw new IllegalArgumentException("Enter a username.");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Enter a password.");
        if (password.length() < 4) throw new IllegalArgumentException("Password must contain at least 4 characters.");
    }
}