package com.revature.DAOs;

import java.util.ArrayList;

import com.revature.models.Employee;
import com.revature.models.Role;
import com.revature.utils.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class EmployeeDAO implements EmployeeDAOInterface {

    @Override
    public ArrayList<Employee> getEmployees() {
        // Instaniate a connection object so that we can talk to the DB.
        try(Connection conn = ConnectionUtil.getConnection()){

            String sql = "select * from employees";

            Statement s = conn.createStatement();

            ResultSet rs = s.executeQuery(sql);

            ArrayList<Employee> employeeList = new ArrayList<>();

            while(rs.next()) {
                int roleFK = rs.getInt("role_id_fk");
                RoleDAO rDAO = new RoleDAO();
                Role role = rDAO.getRoleById(roleFK);

                Employee e = new Employee(
                    rs.getInt("employee_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    role
                );
                employeeList.add(e);
            }
            return employeeList;

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Employee insertEmployee(Employee employee) {
        try(Connection conn = ConnectionUtil.getConnection()){
            
            String sql = "insert into employees (first_name, last_name, role_id_fk) values (?,?,?);";

            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, employee.getFirst_name());
            ps.setString(2, employee.getLast_name());
            ps.setInt(3, employee.getRole_id_fk());

            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                employee.setEmployee_id(generatedKeys.getInt(1));
            }

            return employee;

            } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
