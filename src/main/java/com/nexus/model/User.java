package com.nexus.model;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.nexus.service.Workspace;
import java.util.stream.Stream;

public class User {
    private final String username;
    private final String email;

    // Regex that eliminates most of the invalid email addresses (not all)
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9'!#$%*+\\-\\./=?^_`{|}~]{1,64}@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";

    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }
        validateEmail(email);
        this.username = username;
        this.email = email;
    }

    private void validateEmail(String email) {
        // Method that validates if a given email follows the pattern, and throws an exception if not

        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email não pode ser vazio.");

        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Email inválido.");
        }
    }

    public String consultEmail() {
        return email;
    }

    public String consultUsername() {
        return username;
    }

    // Gets all tasks from workspace, filters the ones owned by this and that have status "IN_PROGRESS"
    public Stream<Task> getUserTasksInProgress(Workspace workspace){
        return workspace.getTasks().stream()
            .filter(task -> task.getOwner() != null && task.getOwner().equals(this))
            .filter(task -> task.getStatus().equals(TaskStatus.IN_PROGRESS));
        }

    public long calculateWorkload(Workspace workspace) {
        return getUserTasksInProgress(workspace).count();
    }
}