package com.example.authentication.controller;

import com.example.authentication.dto.LoginRequest;
import com.example.authentication.dto.LoginResponse;
import com.example.authentication.model.LoginUserDetails;
import com.example.authentication.model.User;
import com.example.authentication.service.UserService;
import com.example.authentication.util.JwtUtil;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/register")
    public String register() {
       return "register";
    }

    @PostMapping("/register")
    public ResponseEntity<String> createUser( @Valid @RequestBody User user) {
        String responseData;
        if(!userService.isEmailAvailable(user.getEmail())){
            responseData = userService.createNewUser(user);
            return new ResponseEntity<>(responseData, HttpStatus.ACCEPTED);
        }
        else responseData = "Email Already Exists.. Please Login.";

        return new ResponseEntity<>(responseData, HttpStatus.CONFLICT);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            LoginUserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());
            String token = jwtUtil.generateToken(userDetails);

            return new ResponseEntity<>(new LoginResponse("Login Successful!",
                    userDetails.getFullname(), userDetails.getEmail(),token), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

    }

    @PostMapping("/login/token")
    public void loginByToken(){

    }

}
