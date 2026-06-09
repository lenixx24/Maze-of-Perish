package ua.edu.ukma.service;

import ua.edu.ukma.model.UserProfile;
import ua.edu.ukma.security.PasswordHasher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class UserStorage {

    private static final int START_GOLD = 0;

    private final Path filePath;

    public UserStorage() {
        this.filePath = Path.of("data", "users.properties");
    }

    public UserProfile register(String username, String password) {
        username = normalizeUsername(username);
        validateCredentials(username, password);

        Properties users = loadUsers();

        if (users.containsKey(username)) throw new IllegalArgumentException("Користувач з таким ім'ям вже існує.");

        String salt = PasswordHasher.createSalt();
        String passwordHash = PasswordHasher.hashPassword(password, salt);

        users.setProperty(username, salt + ":" + passwordHash + ":" + START_GOLD);
        saveUsers(users);

        return new UserProfile(username, START_GOLD);
    }

    public UserProfile login(String username, String password) {
        username = normalizeUsername(username);
        validateCredentials(username, password);

        Properties users = loadUsers();
        String savedValue = users.getProperty(username);

        if (savedValue == null) throw new IllegalArgumentException("Користувача з таким ім'ям не знайдено.");

        String[] parts = savedValue.split(":");

        if (parts.length != 3) throw new IllegalStateException("Файл користувачів пошкоджено.");

        String salt = parts[0];
        String passwordHash = parts[1];
        int gold = Integer.parseInt(parts[2]);

        if (!PasswordHasher.verifyPassword(password, salt, passwordHash)) throw new IllegalArgumentException("Неправильний пароль.");

        return new UserProfile(username, gold);
    }

    public void saveResources(UserProfile userProfile) {
        Properties users = loadUsers();
        String savedValue = users.getProperty(userProfile.getUsername());
        if (savedValue == null) throw new IllegalArgumentException("Користувача не знайдено.");
        String[] parts = savedValue.split(":");
        if (parts.length != 3) throw new IllegalStateException("Файл користувачів пошкоджено.");
        users.setProperty(userProfile.getUsername(), parts[0] + ":" + parts[1] + ":" + userProfile.getGold());
        saveUsers(users);
    }

    private Properties loadUsers() {
        Properties users = new Properties();
        if (!Files.exists(filePath)) return users;
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            users.load(inputStream);
            return users;
        } catch (IOException exception) {
            throw new IllegalStateException("Не вдалося прочитати файл користувачів.", exception);
        }
    }

    private void saveUsers(Properties users) {
        try {
            Files.createDirectories(filePath.getParent());
            try (OutputStream outputStream = Files.newOutputStream(filePath)) {
                users.store(outputStream, "Maze of Perish users");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Не вдалося зберегти файл користувачів.", exception);
        }
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim();
    }

    private void validateCredentials(String username, String password) {
        if (username.isBlank()) throw new IllegalArgumentException("Введіть ім'я користувача.");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Введіть пароль.");
        if (password.length() < 4) throw new IllegalArgumentException("Пароль має містити хоча б 4 символи.");
    }
}