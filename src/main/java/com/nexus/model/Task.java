package com.nexus.model;

import java.time.LocalDate;
import com.nexus.exception.NexusValidationException;

public class Task {
    // Métricas Globais (Alunos implementam a lógica de incremento/decremento)
    public static int totalTasksCreated = 0;
    public static int totalValidationErrors = 0;
    public static int activeWorkload = 0;

    private static int nextId = 1;

    private final int id;
    private final LocalDate deadline; // Imutável após o nascimento
    private String title;
    private TaskStatus status;
    private User owner;
    private int estimatedEffort;

    public Task(String title, LocalDate deadline, int estimatedEffort) {
        this.id = nextId++;
        this.deadline = deadline;
        this.title = title;
        this.status = TaskStatus.TO_DO;
        if (estimatedEffort < 0) {
            totalValidationErrors++;
            throw new NexusValidationException("Esforço estimado não pode ser negativo.");
        }
        this.estimatedEffort = estimatedEffort;
        
        // Ação do Aluno:
        totalTasksCreated++; 
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído e não estiver BLOCKED.
     */
    public void moveToInProgress(User user) {
        // !importante TODO: Verificar uso de argumento user; Ainda não consegui entender o motivo dessa função receber um parametro user. Achei que a verificação devia ser feita com this.owner.
        
        if (this.owner == null) {
            totalValidationErrors++;
            throw new NexusValidationException("Erro ao mudar status: Tarefa deve ter um dono.");
        }

        if (this.status == TaskStatus.BLOCKED) {
            totalValidationErrors++;
            throw new NexusValidationException("Erro ao mudar status: Tarefa não pode ter status BLOCKED.");
        }

        activeWorkload++;
        this.status = TaskStatus.IN_PROGRESS;
    }

    /**
     * Finaliza a tarefa.
     * Regra: Só pode ser movida para DONE se não estiver BLOCKED.
     */
    public void markAsDone() {
        if (this.status == TaskStatus.BLOCKED) {
            totalValidationErrors++;
            throw new NexusValidationException("Erro ao mudar status: Tarefa não pode ter status BLOCKED.");
        }

        activeWorkload--;
        this.status = TaskStatus.DONE;
    }

    public void setBlocked(boolean blocked) {
        if (blocked) {
            if (this.status == TaskStatus.DONE) {
                totalValidationErrors++;
                throw new NexusValidationException("Erro ao bloquear tarefa: Tarefa não pode ter status DONE.");
            }
            this.status = TaskStatus.BLOCKED;
        } else {
            this.status = TaskStatus.TO_DO; // Simplificação para o Lab
        }
    }

    // Getters
    public int getId() { return id; }
    public TaskStatus getStatus() { return status; }
    public String getTitle() { return title; }
    public LocalDate getDeadline() { return deadline; }
    public User getOwner() { return owner; }
    public int getEstimatedEffort() { return estimatedEffort; }
}