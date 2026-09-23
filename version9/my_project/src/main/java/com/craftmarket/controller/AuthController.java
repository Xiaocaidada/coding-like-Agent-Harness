package com.craftmarket.controller;

import com.craftmarket.entity.User;
import com.craftmarket.service.AuthService;
import com.craftmarket.utils.Result;
import com.craftmarket.vo.JwtResponse;
import com.craftmarket.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、注册、token管理接口")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "根据用户名和密码获取JWT令牌")
    public Result<JwtResponse> login(@NotBlank @RequestParam String username,
                                   @NotBlank @RequestParam String password) {
        try {
            String token = authService.login(username, password);
            User user = authService.getUserFromToken(token);
            UserVO userVO = new UserVO(user.getId(), user.getUsername(), user.getRole().name());
            return Result.success(new JwtResponse(token, userVO));
        } catch (Exception e) {
            return Result.badRequest(e.getMessage());
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册（摊主注册）")
    public Result<String> register(@NotBlank @RequestParam String username,
                                 @NotBlank @RequestParam String password,
                                 @NotBlank @RequestParam String phone,
                                 @NotBlank @RequestParam String name,
                                 @NotBlank @RequestParam String businessType) {
        try {
            boolean success = authService.register(username, password, phone, name, businessType);
            return success ? Result.success("注册成功") : Result.badRequest("注册失败");
        } catch (Exception e) {
            return Result.badRequest(e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新令牌", description = "使用原有令牌刷新获取新的JWT令牌")
    public Result<JwtResponse> refreshToken(@NotBlank @RequestParam String token) {
        try {
            String newToken = authService.refreshToken(token);
            User user = authService.getUserFromToken(newToken);
            UserVO userVO = new UserVO(user.getId(), user.getUsername(), user.getRole().name());
            return Result.success(new JwtResponse(newToken, userVO));
        } catch (Exception e) {
            return Result.badRequest(e.getMessage());
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "验证令牌", description = "验证JWT令牌是否有效")
    public Result<String> validateToken(@NotBlank @RequestParam String token) {
        try {
            boolean valid = authService.validateToken(token);
            return valid ? Result.success("令牌有效") : Result.badRequest("令牌无效");
        } catch (Exception e) {
            return Result.badRequest(e.getMessage());
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出（JWT无状态，主要供客户端清除令牌）")
    @PreAuthorize("isAuthenticated()")
    public Result<String> logout(@RequestParam String token) {
        try {
            boolean success = authService.logout(token);
            return success ? Result.success("登出成功") : Result.badRequest("登出失败");
        } catch (Exception e) {
            return Result.badRequest(e.getMessage());
        }
    }

    @GetMapping("/current-user")
    @Operation(summary = "获取当前用户信息", description = "根据JWT令牌获取当前登录用户信息")
    @PreAuthorize("isAuthenticated()")
    public Result<UserVO> getCurrentUser() {
        try {
            String token = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            User user = authService.getUserFromToken(token);
            UserVO userVO = new UserVO(user.getId(), user.getUsername(), user.getRole().name());
            return Result.success(userVO);
        } catch (Exception e) {
            return Result.badRequest(e.getMessage());
        }
    }
}