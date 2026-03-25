package com.nexus.model;
import com.nexus.service.Workspace;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Stream;

public class User {
    private final String username;
    private final String email;

    // Regex que elimina a maioria dos emails inválidos
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9'!#$%*+\\-\\./=?^_`{|}~]{1,64}@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";

    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }
        validateEmail(email);
        this.username = username;
        this.email = email;
    }

    /**
     * Verifica se um email é válido.
     * @param email endereço a ser verificado.
     * @throws IllegalArgumentException se o email é inválido.
     */
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

    // Gets all tasks from workspace, filters the ones owned by this and that have status "IN_PROGRESS"
    /**
     * Filtra todas as tasks do workspace atual e seleciona apenas aquelas que tem status IN_PROGRESS e contém esse objeto como owner.
     * @param workspace workspace atual.
     * @return Stream com todas as tarefas correspondentes.
     */
    public Stream<Task> getUserTasksInProgress(Workspace workspace){
        return workspace.getTasks().stream()
            .filter(task -> task.getOwner() != null && task.getOwner().equals(this))
            .filter(task -> task.getStatus().equals(TaskStatus.IN_PROGRESS));
        }

    public long calculateWorkload(Workspace workspace) {
        // Retorna a quantidade de tarefas em progresso associadas a esse usuário
        return getUserTasksInProgress(workspace).count();
    }

    // Getters
    public String consultEmail() { return email; }
    public String consultUsername() { return username; }
}