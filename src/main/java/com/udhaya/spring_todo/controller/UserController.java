package com.udhaya.spring_todo.controller;


import com.udhaya.spring_todo.entity.User;
import com.udhaya.spring_todo.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired private UserRepo userRepo;

    @GetMapping("/info")
    public ResponseEntity<?> getUserDetails(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof String)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            String email = (String) authentication.getPrincipal();

            return userRepo.findByEmail(email)
                    .map(user -> new ResponseEntity<>(user,HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }catch (Exception e){
            return new ResponseEntity<>("An error occurred: "+e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
