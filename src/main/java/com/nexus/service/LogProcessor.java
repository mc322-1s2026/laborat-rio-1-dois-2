package com.nexus.service;
import com.nexus.model.*;
import com.nexus.exception.NexusValidationException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class LogProcessor {

    /**
    * Processa um arquivo de log presente no classpath.
    * 
    * Para cada linha válida do arquivo:
    * - interpreta o comando;
    * - executa a ação correspondente no sistema;
    * - trata erros sem interromper a execução.
    * 
    * Comandos suportados:
    * - CREATE_USER: cria um novo usuário;
    * - CREATE_PROJECT: cria um novo projeto;
    * - CREATE_TASK: cria uma nova tarefa e associa a um projeto;
    * - ASSIGN_USER: atribui um usuário a uma tarefa;
    * - CHANGE_STATUS: altera o status de uma tarefa;
    * - REPORT_STATUS: gera relatórios com métricas do sistema.
    * 
    * Tratamento de erros:
    * - NexusValidationException: erro de regra de negócio;
    * - IllegalArgumentException: erro de entrada inválida;
    * - Exception: erro genérico.
    * 
    * @param fileName nome do arquivo de log (deve estar no classpath).
    * @param workspace workspace onde projetos e tarefas serão manipulados.
    * @param users lista de usuários do sistema.
    */
    public void processLog(String fileName, Workspace workspace, List<User> users) {
        try {
            // Busca o arquivo dentro da pasta de recursos do projeto (target/classes)
            var resource = getClass().getClassLoader().getResourceAsStream(fileName);
            
            if (resource == null) {
                throw new IOException("Arquivo não encontrado no classpath: " + fileName);
            }

            try (java.util.Scanner s = new java.util.Scanner(resource).useDelimiter("\\A")) {
                String content = s.hasNext() ? s.next() : "";
                List<String> lines = List.of(content.split("\\R"));
                
                for (String line : lines) {
                    if (line.isBlank() || line.startsWith("#")) continue;

                    String[] p = line.split(";");
                    String action = p[0];

                    try {
                        switch (action) {

                            case "CREATE_USER" -> {
                                String username = p[1];
                                String email = p[2];

                                users.add(new User(username, email));
                                System.out.println("[LOG] Usuário criado: " + username);
                            }
                            
                            case "CREATE_PROJECT" -> {
                                String projectName = p[1];
                                int budgetHours = Integer.parseInt(p[2]);

                                Project project = new Project(projectName, budgetHours);
                                workspace.addProject(project);

                                System.out.println("[LOG] Projeto criado: " + projectName);
                            }

                            case "CREATE_TASK" -> {
                                String taskName = p[1];
                                LocalDate deadline = LocalDate.parse(p[2]);
                                int effort = Integer.parseInt(p[3]);
                                String projectName = p[4];

                                Task task = new Task(taskName, deadline, effort);
                                workspace.addTask(task);
                                System.out.println("[LOG] Tarefa criada: " + taskName + " ID:" + task.getId());

                                Project project = workspace.getProjectByName(projectName);
                                project.addTask(task);
                            }

                            case "ASSIGN_USER" -> {
                                int taskId = Integer.parseInt(p[1]);
                                String username = p[2];

                                Task task = workspace.getTaskById(taskId);
                                User user = workspace.getUserByUsername(username);

                                task.assignUser(user);

                                System.out.println("[LOG] Usuário " + username + " atribuido à tarefa " + taskId);
                            }

                            case "CHANGE_STATUS" -> {
                                int taskId = Integer.parseInt(p[1]);
                                TaskStatus newStatus = TaskStatus.valueOf(p[2]);

                                Task task = workspace.getTaskById(taskId);
                                task.changeStatus(newStatus);

                                System.out.println("[LOG] Status da tarefa " + taskId + " alterado para " + newStatus);
                            }

                            case "REPORT_STATUS" -> {
                                System.out.println("=== TOP PERFORMERS ===");
                                workspace.topPerformers()
                                    .forEach(user -> System.out.println(user.consultUsername()));
                                
                                System.out.println("=== OVERLOADED USERS ===");
                                workspace.overloadedUsers()
                                    .forEach(user -> System.out.println(user.consultUsername()));

                                System.out.println("=== PROJECT HEALTH ===");
                                workspace.getProjects().forEach(project -> System.out.println(project.getName() + ": " + workspace.projectHealth(project) + "%"));

                                System.out.println("=== GLOBAL BOTTLENECKS ===");
                                System.out.println(workspace.globalBottlenecks());
                            }

                            default -> System.err.println("[WARN] Ação desconhecida: " + action);
                        }
                    } catch (NexusValidationException e){
                        Task.totalValidationErrors++;
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                    } catch (IllegalArgumentException e){
                        Task.totalValidationErrors++;
                        System.err.println("[ERRO DE ENTRADA] Falha no comando '" + line + "': " + e.getMessage());
                    } catch (Exception e){
                        Task.totalValidationErrors++;
                        System.err.println("[ERRO] Falha no comando '" + line + "': " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }
}