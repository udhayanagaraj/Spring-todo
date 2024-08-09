package com.udhaya.spring_todo.controller;


import com.udhaya.spring_todo.entity.User;
import com.udhaya.spring_todo.models.LoginCredentials;
import com.udhaya.spring_todo.repository.UserRepo;
import com.udhaya.spring_todo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserRepo userRepo;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Map<String,Object>> registerHandler(@RequestBody User user){
        try {
            if(userRepo.findByEmail(user.getEmail()).isPresent()){
                Map<String, Object> response = new HashMap<>();
                response.put("Message", "Email already exists");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            String encodedPass = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPass);
            user = userRepo.save(user);
            String token = jwtUtil.generateToken(user.getEmail());
            Map<String,Object> response =  Collections.singletonMap("jwt-token",token);
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("Message", "Registration failed");
            errorResponse.put("Error", e.getMessage());
            return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> loginHandler(@RequestBody LoginCredentials body){
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(body.getEmail(),body.getPassword());
            authenticationManager.authenticate(authenticationToken);
            String token = jwtUtil.generateToken(body.getEmail());
            Map<String, Object> response = Collections.singletonMap("jwt-token", token);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (BadCredentialsException e) {
            Map<String,Object> response = new HashMap<>();
            response.put("message","Invalid email or password");
            return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
        }catch (AuthenticationException e){
            Map<String,Object> response = new HashMap<>();
            response.put("message","Authentication failed");
            return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
        }catch (Exception e){
            Map<String,Object> response = new HashMap<>();
            response.put("message","Login failed");
            response.put("error",e.getMessage());
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
