package com.example;


import com.example.database.DatabaseInitializer;
import com.example.domain.User;
import com.example.repository.UserRepository;
import com.example.repository.UserRepositoryImpl;
import com.example.service.UserService;

import java.util.List;

public class App
{
    public static void main( String[] args )
    {
        DatabaseInitializer.init();

        UserRepository userRepository = new UserRepositoryImpl();
        UserService userService = new UserService(userRepository);

        userService.createUser("Alice","alice@gmail.com");
        userService.createUser("Bob","bob@gmail.com");

        List<User> allUsers = userService.getAllUsers();
        allUsers.forEach(System.out::println);
    }
}
