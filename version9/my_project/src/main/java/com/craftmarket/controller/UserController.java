package com.craftmarket.controller;

import com.craftmarket.entity.User;
import com.craftmarket.service.UserService;
import com.craftmarket.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {
        return userService.login(user);
    }

    @GetMapping("/{id}")
    public Result<?> getUserById(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public Result<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return userService.updateUser(user);
    }

    @GetMapping
    public Result<?> getAllUsers(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return Result.success(userService.getAllUsers(page, size));
    }
}