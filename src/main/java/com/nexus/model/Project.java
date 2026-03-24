package com.nexus.model;

import java.util.List;

import com.nexus.exception.NexusValidationException;

import java.util.ArrayList;
import java.util.Collections;

public class Project {
    private final String name;
    private List<Task> tasks;
    private int totalBudget;
    private int currentBudget; // sum of the estimatedEfforts of all tasks in project currently

    public Project(String name, int totalBudget) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Nome do projeto não pode ser vazio.");

        this.name = name;
        this.tasks = new ArrayList<>();
        if (totalBudget < 0) {
            throw new IllegalArgumentException("Budget do projeto não pode ser negativo."); // TODO validar lógica de negócio
        }
        this.totalBudget = totalBudget;
    }

    public void addTask(Task t) {
        int newBudget = t.getEstimatedEffort() + currentBudget;
        if (newBudget > totalBudget) {
            throw new NexusValidationException("Adicionar esta tarefa excederia o Budget total. Cancelando operação.");
        }
        else {
            tasks.add(t);
            currentBudget = newBudget;
        }
        
    } 

    public String getName() {
        return name;
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public int getTotalBudget() {
        return totalBudget;
    }
}
