package com.ppsahu.careaccess.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final String username;
    private final String password;

    public AuthController(@Value("${demo.auth.username:demo@careway.test}") String username,
                          @Value("${demo.auth.password:careway-demo}") String password) {
        this.username=username;
        this.password=password;
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        boolean valid=MessageDigest.isEqual(username.getBytes(StandardCharsets.UTF_8),request.username().trim().getBytes(StandardCharsets.UTF_8))
                & MessageDigest.isEqual(password.getBytes(StandardCharsets.UTF_8),request.password().getBytes(StandardCharsets.UTF_8));
        if(!valid) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Email or password is incorrect");
        HttpSession session=servletRequest.getSession(true);
        servletRequest.changeSessionId();
        session.setAttribute("patientId","patient-demo-001");
        session.setMaxInactiveInterval(4*60*60);
        return new UserResponse("patient-demo-001","Demo Patient");
    }

    @GetMapping("/me")
    public UserResponse me(HttpServletRequest request) {
        HttpSession session=request.getSession(false);
        Object patientId=session==null?null:session.getAttribute("patientId");
        if(!(patientId instanceof String id)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Please sign in");
        return new UserResponse(id,"Demo Patient");
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        HttpSession session=request.getSession(false);
        if(session!=null) session.invalidate();
    }

    public record LoginRequest(@NotBlank String username,@NotBlank String password) {}
    public record UserResponse(String patientId,String displayName) {}
}
