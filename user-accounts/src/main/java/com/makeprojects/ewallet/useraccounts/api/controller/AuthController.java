package com.makeprojects.ewallet.useraccounts.api.controller;

import com.makeprojects.ewallet.useraccounts.dto.AuthDto;
import com.makeprojects.ewallet.useraccounts.dto.AuthResponse;
import com.makeprojects.ewallet.shared.database.model.User;
import com.makeprojects.ewallet.useraccounts.core.service.implementation.UserService;
import com.makeprojects.ewallet.useraccounts.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(JwtUtil jwtUtil, UserService userService, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDto authDto) {
        Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getUsername(), authDto.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = this.jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(userDetails, jwtToken));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        User addedUser = this.userService.addUser(user);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(addedUser.getUserName(), addedUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(addedUser.getRole())));
        String jwtToken = this.jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(userDetails, jwtToken));
    }
}
