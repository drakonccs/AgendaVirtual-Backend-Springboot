package com.drakonccs.p3tareas.dto;

import com.drakonccs.p3tareas.entity.User;

public class UserDTO {
    
    private long id;
    private String username,name,password,email;
    
    


    public UserDTO() {
}
    public UserDTO(User dtoUser){
        this.id= dtoUser.getId();
        this.username=dtoUser.getUsername();
        this.name=dtoUser.getName();
        this.password = dtoUser.getPassword();
        this.email= dtoUser.getEmail();
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
}
