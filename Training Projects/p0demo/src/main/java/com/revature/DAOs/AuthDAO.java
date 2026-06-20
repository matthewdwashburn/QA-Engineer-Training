package com.revature.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.revature.models.Employee;
import com.revature.models.Role;
import com.revature.utils.ConnectionUtil;

public class AuthDAO {
    
    // Using first name last name instead of username password for now
    
    public Employee login(String first_name, String last_name) {
        try( Connection conn = ConnectionUtil.getConnection()) {
            String sql = "select * from employees where first_name = ? and last_name = ?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, first_name);
            ps.setString(2, last_name);

            ResultSet rs = ps.executeQuery();
            
            //Since we're only expecting one record, we can just use an if whith rs.next() instead of while
            if(rs.next()) {
                int roleFK = rs.getInt("role_id_fk");
                RoleDAO rDAO = new RoleDAO();
                Role role = rDAO.getRoleById(roleFK);

                Employee e = new Employee(
                    rs.getInt("employee_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    role
                );
                return e; // Returning the employee with matching first_name/last_name
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
