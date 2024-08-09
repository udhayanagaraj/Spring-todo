package com.udhaya.spring_todo.controller;


import com.udhaya.spring_todo.Service.TaskService;
import com.udhaya.spring_todo.entity.Tasks;
import com.udhaya.spring_todo.entity.User;
import com.udhaya.spring_todo.models.TaskDao;
import com.udhaya.spring_todo.repository.TaskRepository;
import com.udhaya.spring_todo.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/user/task")
public class TaskController {

    @Autowired private TaskRepository taskRepository;
    @Autowired private TaskService taskService;
    @Autowired private UserRepo userRepo;


    @PostMapping("/{user_id}")
    public ResponseEntity<String> addTask(@PathVariable("user_id") Integer userId,@RequestBody TaskDao dao){
        try {
            Tasks tasks = taskService.addTask(userId,dao);
            if(tasks == null){
                return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Task created", HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>("Task creation failed due to data integrity issues", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/userId/{id}")
    public ResponseEntity<?> getTasksByUserId(@PathVariable("id") Integer id){
        try{
            User user = userRepo.findById(id.longValue()).orElse(null);
            if(user == null){
                return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
            }
            List<Tasks> tasks = taskService.findByUser(user);
            return new ResponseEntity<>(tasks,HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //this method is used to get all tasks without a relationship with the user
    @GetMapping()
    public ResponseEntity<?> getTasks(){
        try{
            return new ResponseEntity<>(taskService.getAllTask(),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //this method is used to get single task without a relationship with the user
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable("id") Integer id){
        try{
            Optional<Tasks> tasks = taskService.getById(id);
            if(tasks.isEmpty()){
                return new ResponseEntity<>("No tasks available",HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(tasks.get(),HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @PutMapping("/update/{user_id}/{id}")
    public ResponseEntity<?> updateTask(@PathVariable("user_id") Integer user_id,@PathVariable("id") Integer id,@RequestBody Tasks tasks){
        try{
            User user = userRepo.findById(user_id.longValue()).orElse(null);
            if(user == null){
                return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
            }
            Tasks existingTask = taskService.updateTask(id,tasks);
            if(existingTask == null){
                return new ResponseEntity<>("Task with id: "+id+" not found",HttpStatus.NOT_FOUND);
            }
            taskRepository.save(existingTask);
            return new ResponseEntity<>(existingTask,HttpStatus.ACCEPTED);
        }catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>("Task update failed due to data integrity issues", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{user_id}/completed")
    public ResponseEntity<?> getCompletedTask(@PathVariable("user_id") Integer userId){
        try{
            User user = userRepo.findById(userId.longValue()).orElse(null);
            if(user == null){
                return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
            }
            List<Tasks> tasks = taskService.findByCompleted(user.getId().intValue(),true);
            if(tasks.isEmpty()){
                return new ResponseEntity<>("No completed tasks found",HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(tasks,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{user_id}/uncompleted")
    public ResponseEntity<?> getUncompletedTask(@PathVariable("user_id") Integer userId){
        try{
            User user = userRepo.findById(userId.longValue()).orElse(null);
            if(user == null){
                return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
            }
            List<Tasks> tasks = taskService.findByCompleted(user.getId().intValue(),false);
            if(tasks.isEmpty()){
                return new ResponseEntity<>("No uncompleted tasks found",HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(tasks,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{user_id}/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable("user_id") Integer userId,@PathVariable("id") Integer id){
        try{
            ;
            if(!taskService.deleteById(userId,id)){
                return new ResponseEntity<>("Task with id: "+id+" not found",HttpStatus.NOT_FOUND);
            }
            taskRepository.deleteById(id);
            return new ResponseEntity<>("Task deleted",HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
