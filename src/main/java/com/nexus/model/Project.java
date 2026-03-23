package com.nexus.model;

import java.util.List;

import java.util.ArrayList;

public class Project {
    public String name;
    public List<Task> tasks;
    public int totalBudget;

    public Project(String name, int totalBudget) {
        this.name = name;
        this.tasks = new ArrayList<>();
        if (totalBudget < 0) {
            throw new IllegalArgumentException("Budget do projeto não pode ser negativo"); // TODO validar lógica de negócio
        }
        this.totalBudget = totalBudget;
    }

    public void addTask(Task t) {
        // TODO: Este método deve validar se a soma das horas de todas as tarefas atuais + a nova tarefa excede o totalBudget do projeto. Se exceder, lance NexusValidationException.
        // TODO: Antes de implementar esse método, deve-se implementar o campo estimatedEffort às tasks.
    }
}
