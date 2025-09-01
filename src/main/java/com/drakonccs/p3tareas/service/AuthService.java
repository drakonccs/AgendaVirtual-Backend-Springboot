package com.drakonccs.p3tareas.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.drakonccs.p3tareas.dto.AuthRequest;
import com.drakonccs.p3tareas.dto.AuthResponse;
import com.drakonccs.p3tareas.dto.UserDTO;
import com.drakonccs.p3tareas.entity.User;
import com.drakonccs.p3tareas.repository.UserRepository;
import com.drakonccs.p3tareas.security.JwtService;
import com.drakonccs.p3tareas.util.Role;

@Service
public class AuthService {


    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public UserDTO register(UserDTO dto) {
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new RuntimeException("Usuario ya registrado");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setRole(Role.USER);
        
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }

    public AuthResponse login(AuthRequest authRequest){
        UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(
            authRequest.getUsername(), authRequest.getPassword());
        authenticationManager.authenticate(authToken);
        User user=userRepository.findByUsername(authRequest.getUsername()).get();

        String jwt=jwtService.generateToken(user, generateExtraClaims(user));

        return new AuthResponse(jwt);

    }
    private Map<String , Object> generateExtraClaims(User user){

        Map<String , Object> extraClaims=new HashMap<>();
        extraClaims.put("name", user.getName());
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("permissions", user.getAuthorities());
        return extraClaims;
    }
}
