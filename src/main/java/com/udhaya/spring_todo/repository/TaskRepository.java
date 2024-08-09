package com.udhaya.spring_todo.repository;

import com.udhaya.spring_todo.entity.Tasks;
import com.udhaya.spring_todo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Tasks,Integer> {
   List<Tasks> findByUserIdAndCompleted(Long userId,boolean completed);
   List<Tasks> findAllByOrderByCreatedAtDesc();
   List<Tasks> findByUser(User user);
}
