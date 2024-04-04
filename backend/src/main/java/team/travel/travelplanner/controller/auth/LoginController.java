package team.travel.travelplanner.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import team.travel.travelplanner.entity.User;
import team.travel.travelplanner.model.UserLoginModel;
import team.travel.travelplanner.model.UserModel;
import team.travel.travelplanner.repository.UserRepository;

@RestController
@RequestMapping("api/auth")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    private final UserRepository userRepository;

    public LoginController(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Validated UserLoginModel loginModel, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(loginModel.username(), loginModel.password());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextRepository.saveContext(context, request, response);
        return ResponseEntity.ok("Login successful");
    }

    @GetMapping("/check-login")
    public UserModel checkLogin(Authentication authentication) {
           User user = userRepository.findByUsername(authentication.getName());
           return new UserModel(user.getUsername(), user.getFirstName(), user.getLastName());
    }
}