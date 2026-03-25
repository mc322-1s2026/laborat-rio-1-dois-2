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
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Título não pode ser vazio.");

        if (deadline == null)
            throw new IllegalArgumentException("Deadline não pode ser nulo.");

        if (estimatedEffort < 0) {
            totalValidationErrors++;
            throw new NexusValidationException("Esforço estimado não pode ser negativo.");
        }

        this.id = nextId++;
        this.deadline = deadline;
        this.title = title;
        this.status = TaskStatus.TO_DO;
        this.estimatedEffort = estimatedEffort;
        
        // Ação do Aluno:
        totalTasksCreated++; 
    }

    public void assignUser(User user){
        if (user == null){
            totalValidationErrors++;
            throw new NexusValidationException("Usuário não existe");
        }
        this.owner = user;
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Se a operação foi feita, incrementa activeWorkload.
     * @throws NexusValidationException se a tarefa não tem owner ou se a tarefa estava bloquada antes da operação
     */
    public void moveToInProgress() {
        
        if (this.owner == null) {
            totalValidationErrors++;
            throw new NexusValidationException("Erro ao mudar status: Tarefa deve ter um dono.");
        }

        if (this.status == TaskStatus.BLOCKED) {
            totalValidationErrors++;
            throw new NexusValidationException("Erro ao mudar status: Tarefa não pode ter status BLOCKED.");
        }
        // Verificação caso seja testado uma tarefa que já está IN_PROGRESS e evitar que o activeWorkload seja incrementado falhamente
        if (this.status != TaskStatus.IN_PROGRESS)
            activeWorkload++;

        this.status = TaskStatus.IN_PROGRESS;
    }

    /**
     * Finaliza a tarefa.
     * Regra: Se a operação foi feita, decrementa activeWorkload.
     * @throws NexusValidationException se tentou marcar como concluída uma tarefa bloqueada.
     */
    public void markAsDone() {
        if (this.status == TaskStatus.BLOCKED) {
            totalValidationErrors++;
            throw new NexusValidationException("Erro ao mudar status: Tarefa não pode ter status BLOCKED.");
        }

        if (this.status == TaskStatus.IN_PROGRESS)
            activeWorkload--;

        this.status = TaskStatus.DONE;
    }

    /**
     * Move a tarefa para BLOCKED.
     * Regra: Se a operação foi feita em uma task que tinha status IN_PROGRESS logo antes da operação, decrementa activeWorkload.
     * @param blocked booleano que bloqueia a task se True e desbloqueia-a se False.
     * @throws NexusValidationException se tentou bloquear uma tarefa já concluída.
     */
    public void setBlocked(boolean blocked) {
        if (blocked) {
            if (this.status == TaskStatus.DONE) {
                totalValidationErrors++;
                throw new NexusValidationException("Erro ao bloquear tarefa: Tarefa não pode ter status DONE.");
            }

            // Decrementa o activeWorkload porque deixa de existir uma tarefa IN_PROGRESS
            if (this.status == TaskStatus.IN_PROGRESS)
                activeWorkload--;

            this.status = TaskStatus.BLOCKED;
        } else {
            this.status = TaskStatus.TO_DO; // Simplificação para o Lab
        }
    }

    /**
     * Move a tarefa para TO_DO.
     * Regra: Se o status for IN_PROGRESS antes da operação, decrementa o activeWorkload.
     */
    public void setToDo() {
        if (this.status == TaskStatus.IN_PROGRESS)
            activeWorkload--;
        this.status = TaskStatus.TO_DO;
    }

    /**
     * Altera o status da tarefa.
     * Regras:
     *  - TO_DO faz uma chamada para {@link #setToDo()}.
     *  - IN_PROGRESS faz uma chamada para {@link #moveToInProgress()}.
     *  - BLOCKED faz uma chamada para {@link #setBlocked(boolean)} com valor true.
     *  - DONE faz uma chamada para {@link #markAsDone()}.
     * @param newStatus novo status da tarefa.
     */
    public void changeStatus(TaskStatus newStatus){
        switch (newStatus){
            case TO_DO -> setToDo();
            case IN_PROGRESS -> moveToInProgress();
            case BLOCKED -> setBlocked(true);   
            case DONE -> markAsDone();
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