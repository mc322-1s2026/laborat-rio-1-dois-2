package com.nexus.service;

import com.nexus.model.Task;
import com.nexus.model.Project;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Workspace {
    private final List<Task> tasks = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    public List<Project> getProjects() {
        return Collections.unmodifiableList(projects);
    }

    public Project getProjectByName(String name) {
        return projects.stream()
        .filter(p -> p.getName().equals(name))
        .findFirst()
        .orElse(null);
    }

    public Task getTaskById(int id) {
        return tasks.stream()
        .filter(t -> t.getId() == id)
        .findFirst()
        .orElse(null);
    }
}