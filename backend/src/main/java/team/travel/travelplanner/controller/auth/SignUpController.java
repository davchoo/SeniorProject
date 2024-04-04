package team.travel.travelplanner.controller.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.travel.travelplanner.entity.User;
import team.travel.travelplanner.model.UserSignUpModel;
import team.travel.travelplanner.repository.UserRepository;

@RestController
@RequestMapping("api/auth")
public class SignUpController {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public SignUpController(UserRepository userRepository,
                            PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody @Valid UserSignUpModel userSignUpModel){
        if(userRepository.existsByUsername(userSignUpModel.username())){
            return new ResponseEntity<>("Username, " + userSignUpModel.username() + ", already exists",
                    HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setFirstName(userSignUpModel.firstName());
        user.setLastName(userSignUpModel.lastName());
        user.setUsername(userSignUpModel.username());
        user.setPassword(passwordEncoder.encode(userSignUpModel.password()));
        userRepository.save(user);
        return new ResponseEntity<>("User is registered successfully on TripEase!", HttpStatus.OK);
    }
}
