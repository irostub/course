package com.example;


import com.example.controller.UserController;
import com.example.database.DatabaseInitializer;
import com.example.dto.Request;
import com.example.dto.Response;
import com.example.repository.UserRepository;
import com.example.repository.UserRepositoryImpl;
import com.example.service.UserService;

import java.sql.SQLException;

public class App
{
    public static void main( String[] args ) throws SQLException {
//        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
        DatabaseInitializer.init();

        UserRepository userRepository = new UserRepositoryImpl();
        UserService userService = new UserService(userRepository);
        UserController userController = new UserController(userService);

        Request request = new Request();
        request.setParam("name", "Alice");
        request.setParam("email", "alice@gmail.com");
        userController.createUser(request);

        Request request1 = new Request();
        request1.setParam("id", "1");
        Response user = userController.getUser(request1);
        System.out.println(user.getBody());
    }
}
