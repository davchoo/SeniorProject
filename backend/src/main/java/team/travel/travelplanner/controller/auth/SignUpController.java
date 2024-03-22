package team.travel.travelplanner.controller.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.travel.travelplanner.entity.User;
import team.travel.travelplanner.model.UserModel;
import team.travel.travelplanner.repository.UserRepository;

@RestController
@RequestMapping("api/auth")
public class SignUpController {
    private final UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public SignUpController(UserRepository userRepository,
                            PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody @Valid UserModel userModel){
        if(userRepository.existsByUsername(userModel.username())){
            return new ResponseEntity<>("Username, " + userModel.username() + ", already exists",
                    HttpStatus.BAD_REQUEST);
        }
        if(userRepository.existsByEmail(userModel.username())){
            return new ResponseEntity<>("Email, " + userModel.email() + ", is already taken.",
                    HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setFirstName(userModel.firstName());
        user.setLastName(userModel.lastName());
        user.setEmail(userModel.email());
        user.setUsername(userModel.username());
        user.setPassword(passwordEncoder.encode(userModel.password()));
        userRepository.save(user);
        return new ResponseEntity<>("User is registered successfully on TripEase!", HttpStatus.OK);
    }
}
