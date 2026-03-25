package com.nexus.model;
import com.nexus.exception.NexusValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Project {
    private final String name;
    private List<Task> tasks;
    private int totalBudget;
    private int currentBudget; // soma do estimatedEffort de todas as tarefas no projeto

    public Project(String name, int totalBudget) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Nome do projeto não pode ser vazio.");

        this.name = name;
        this.tasks = new ArrayList<>();
        if (totalBudget < 0) {
            throw new IllegalArgumentException("Budget do projeto não pode ser negativo.");
        }
        this.totalBudget = totalBudget;
    }

    /**
     * Adiciona uma tarefa existente ao projeto.
     * Regra: se a operação foi feita, soma o estimatedEffort da nova tarefa ao currentBudget do projeto.
     * @param t
     * @throws NexusValidationException se o estimatedEffort da tarefa + currentBudget do projeto for maior que totalBudget.
     */
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

    // Getters
    public String getName() { return name; }
    public List<Task> getTasks() { return Collections.unmodifiableList(tasks); }
    public int getTotalBudget() { return totalBudget; }
}
