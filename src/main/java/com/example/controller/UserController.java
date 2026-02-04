package com.example.controller;

import com.example.domain.User;
import com.example.dto.Request;
import com.example.dto.Response;
import com.example.service.UserService;

import java.util.Optional;

import static com.example.converter.JsonConverter.toJson;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public Response createUser(Request request){
        try {
            String name = request.getParam("name");
            String email = request.getParam("email");

            if (name == null || name.trim().isEmpty()) {
                return new Response(400, "이름은 필수입니다.");
            }

            if(email == null || email.trim().isEmpty()) {
                return new Response(400, "이메일은 필수입니다.");
            }

            User user = userService.createUser(name, email);
            return new Response(201, toJson(user));
        }catch (IllegalArgumentException e){
            return new Response(400, e.getMessage());
        }catch (Exception e){
            return new Response(500, e.getMessage());
        }
    }

    public Response getAllUsers(){
        return new Response(200, toJson(userService.getAllUsers()));
    }

    public Response getUser(Request request){
        try{
            String idStr = request.getParam("id");
            long id = Long.parseLong(idStr);
            Optional<User> userById = userService.getUserById(id);
            if(userById.isPresent()){
                return new Response(200, toJson(userById.get()));
            } else {
                return new Response(404, "존재하지 않는 사용자입니다");
            }
        }catch (IllegalArgumentException e){
            return new Response(400, e.getMessage());
        }catch (Exception e){
            return new Response(500, e.getMessage());
        }
    }
}
