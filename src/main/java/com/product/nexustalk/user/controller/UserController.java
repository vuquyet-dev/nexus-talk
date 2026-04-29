package com.product.nexustalk.user.controller;

import com.product.nexustalk.auth.security.AuthUserPrincipal;
import com.product.nexustalk.user.dto.UpdateProfileRequest;
import com.product.nexustalk.user.dto.UserResponse;
import com.product.nexustalk.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal AuthUserPrincipal me) {
        return userService.getMe(me.userId());
    }

    @PatchMapping("/me")
    public UserResponse updateMe(
            @AuthenticationPrincipal AuthUserPrincipal me,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return userService.updateMe(me.userId(), request);
    }

    @PostMapping("/{id}/follow")
    public void follow(@AuthenticationPrincipal AuthUserPrincipal me, @PathVariable Long id) {
        userService.follow(me.userId(), id);
    }

    @DeleteMapping("/{id}/follow")
    public void unfollow(@AuthenticationPrincipal AuthUserPrincipal me, @PathVariable Long id) {
        userService.unfollow(me.userId(), id);
    }

    @PostMapping("/{id}/block")
    public void block(@AuthenticationPrincipal AuthUserPrincipal me, @PathVariable Long id) {
        userService.block(me.userId(), id);
    }

    @DeleteMapping("/{id}/block")
    public void unblock(@AuthenticationPrincipal AuthUserPrincipal me, @PathVariable Long id) {
        userService.unblock(me.userId(), id);
    }
}

