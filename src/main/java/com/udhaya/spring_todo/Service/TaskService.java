package com.udhaya.spring_todo.Service;


import com.udhaya.spring_todo.DTO.TaskDTO;
import com.udhaya.spring_todo.entity.Tasks;
import com.udhaya.spring_todo.entity.User;
import com.udhaya.spring_todo.models.TaskDao;
import com.udhaya.spring_todo.repository.TaskRepository;
import com.udhaya.spring_todo.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private UserRepo userRepo;


    public Tasks addTask(Integer id,TaskDao dao){
        User user = userRepo.findById(id.longValue()).orElse(null);
        if(user == null){
            return null;
        }
        Tasks tasks = new Tasks();
        tasks.setTitle(dao.getTitle());
        tasks.setDescription(dao.getDescription());
        tasks.setDueDate(dao.getDueDate());
        tasks.setCreatedAt(LocalDateTime.now());
        tasks.setCompleted(false);
        tasks.setUpdatedAt(null);
        tasks.setUser(user);
        return taskRepository.save(tasks);
    }

    public List<TaskDTO> getAllTask(){
        List<Tasks> tasks = taskRepository.findAllByOrderByCreatedAtDesc();
        return tasks.stream().map(task -> {
            TaskDTO dto = new TaskDTO();
            dto.setId(task.getId());
            dto.setTitle(task.getTitle());
            dto.setDescription(task.getDescription());
            dto.setDueDate(task.getDueDate());
            dto.setCompleted(task.isCompleted());
            dto.setCreatedAt(task.getCreatedAt());
            dto.setUpdatedAt(task.getUpdatedAt());
            dto.setUserId(task.getUser() != null ? task.getUser().getId() : null);
            return dto;
        }).toList();
    }


    public Optional<Tasks> getById(Integer id){
        return taskRepository.findById(id);
    }


    public boolean deleteById(Integer userId,int id){
        User user = userRepo.findById(userId.longValue()).orElse(null);
        if(user == null)
            return false;
        taskRepository.deleteById(id);
        return true;
    }

    public Tasks updateTask(Integer id,Tasks tasks){
        Optional<Tasks> existingTaskOptional = taskRepository.findById(id);
        if (existingTaskOptional.isEmpty()){
            return null;
        }
        Tasks existingTask = existingTaskOptional.get();
        existingTask.setTitle(tasks.getTitle());
        existingTask.setDescription(tasks.getDescription());
        existingTask.setDueDate(tasks.getDueDate());
        existingTask.setCreatedAt(tasks.getCreatedAt());
        existingTask.setUpdatedAt(LocalDateTime.now());
        existingTask.setCompleted(tasks.isCompleted());

        return existingTask;
    }

    public List<Tasks> findByCompleted(Integer userId,boolean completed){
        return taskRepository.findByUserIdAndCompleted(userId.longValue(),completed);
    }

    public List<Tasks> findByUser(User user){
        return taskRepository.findByUser(user);
    }

}
