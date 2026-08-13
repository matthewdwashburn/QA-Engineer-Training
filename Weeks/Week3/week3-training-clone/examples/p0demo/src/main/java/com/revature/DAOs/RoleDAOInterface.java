package com.revature.DAOs;

import com.revature.models.Role;

public interface RoleDAOInterface {

    //a method that returns a Role given an id
    Role getRoleById(int id);

    //a method that updates a Role's salary
    boolean updateRoleSalary(String title, int salary);

}
