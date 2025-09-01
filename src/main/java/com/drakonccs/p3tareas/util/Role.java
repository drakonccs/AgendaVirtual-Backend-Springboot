package com.drakonccs.p3tareas.util;

import java.util.*;

public enum Role {
    USER(Arrays.asList(
        Permission.TASK_CREATE,
        Permission.TASK_READ,
        Permission.TASK_UPDATE,
        Permission.TASK_DELETE
    )),
    ADMIN(Arrays.asList(
        Permission.USER_READ,
        Permission.USER_DELETE,
        Permission.TASK_CREATE,
        Permission.TASK_READ,
        Permission.TASK_UPDATE,
        Permission.TASK_DELETE
    ));

    private List<Permission> permissions;

    Role(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }
     public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}