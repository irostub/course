package com.example;


import com.example.database.DatabaseInitializer;
import com.example.domain.User;
import com.example.repository.UserRepository;
import com.example.repository.UserRepositoryImpl;
import com.example.service.UserService;
import org.h2.tools.Server;

import java.sql.SQLException;
import java.util.List;

public class App
{
    public static void main( String[] args ) throws SQLException {
//        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();

        DatabaseInitializer.init();

        UserRepository userRepository = new UserRepositoryImpl();
        UserService userService = new UserService(userRepository);

        userService.createUser("Alice","alice@gmail.com");
        userService.createUser("Bob","bob@gmail.com");

        List<User> allUsers = userService.getAllUsers();
        allUsers.forEach(System.out::println);
/*
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
 */
    }
}
