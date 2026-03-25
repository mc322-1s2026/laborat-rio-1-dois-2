package com.nexus.service;

import com.nexus.model.User;
import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.exception.NexusValidationException;
import com.nexus.model.Project;
import com.nexus.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    /**
    * Retorna os 3 usuários com maior número de tarefas concluídas.
    * Utiliza Stream API para agrupar e ordenar os usuários com base
    * na quantidade de tarefas no status DONE.
    *
    * @return lista com até 3 usuários mais produtivos, em ordem decrescente.
    */
    public List<User> topPerformers(){
        return tasks.stream()
            .filter(task -> task.getStatus().equals(TaskStatus.DONE))
            .filter(task -> task.getOwner() != null)
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

    /**
    * Retorna os usuários com carga de trabalho excessiva.
    * Regra: usuários com mais de 10 tarefas no status IN_PROGRESS.
    * A contagem é feita utilizando Stream API.
    *
    * @return lista de usuários sobrecarregados.
    */
    public List<User> overloadedUsers(){
        return tasks.stream()
            .filter(task -> task.getStatus().equals(TaskStatus.IN_PROGRESS))
            .filter(task -> task.getOwner() != null)
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

    /**
    * Calcula o percentual de conclusão de um projeto.
    * Regra: (tarefas DONE / total de tarefas) * 100.
    * Caso o projeto não possua tarefas, retorna 0.0.
    *
    * @param project projeto a ser analisado.
    * @return percentual de tarefas concluídas no projeto.
    */
    public double projectHealth(Project project){
        long total = project.getTasks().stream().count();

        if (total == 0) return 0.0;

        long done = project.getTasks().stream()
            .filter(task -> task.getStatus().equals(TaskStatus.DONE))
            .count();
        return (done * 100.0)/total;
    }

    /**
    * Identifica o principal gargalo do sistema.
    * Regra: retorna o status com maior número de tarefas,
    * desconsiderando tarefas com status DONE.
    * Utiliza Stream API para agrupamento e contagem.
    *
    * @return status mais frequente entre tarefas não concluídas,
    * ou null se não houver tarefas pendentes.
    */
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