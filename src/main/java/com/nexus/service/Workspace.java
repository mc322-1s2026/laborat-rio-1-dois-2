package com.nexus.service;

import com.nexus.model.User;
import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.exception.NexusValidationException;
import com.nexus.model.Project;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;
import com.nexus.Main;

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
            .orElseThrow(() -> new NexusValidationException("Projeto não encontrado: " + name));
    }

    public Task getTaskById(int id) {
        return tasks.stream()
            .filter(t -> t.getId() == id)
            .findFirst()
            .orElseThrow(() -> new NexusValidationException("Tarefa não encontrada: " + id));
    }

    public User getUserByUsername(String username){
        return Main.getUsers().stream()
            .filter(u -> u.consultUsername().equals(username))
            .findFirst()
            .orElseThrow(() -> new NexusValidationException("Usuário não encontrado: " + username));
    }

    public List<User> topPerformers(){
        return tasks.stream()
            .filter(task -> task.getStatus().equals(TaskStatus.DONE))
            .map(Task::getOwner)
            .collect(Collectors.groupingBy(
            user -> user,
            Collectors.counting()
            ))
            .entrySet()
            .stream()
            .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
            .limit(3)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    public List<User> overloadedUsers(){
        return tasks.stream()
            .filter(task -> task.getStatus().equals(TaskStatus.IN_PROGRESS))
            .map(Task::getOwner)
            .collect(Collectors.groupingBy(
            user -> user,
            Collectors.counting()
            ))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 10)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    public double projectHealth(Project project){
        long total = project.getTasks().stream().count();

        if (total == 0) return 0.0;

        long done = project.getTasks().stream()
            .filter(task -> task.getStatus().equals(TaskStatus.DONE))
            .count();
        return (done * 100.0)/total;
    }

    public TaskStatus globalBottlenecks(){
        return tasks.stream()
            .filter(task -> task.getStatus() != TaskStatus.DONE)
            .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()))
            .entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
}