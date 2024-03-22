package team.travel.travelplanner.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record UserModel(
        @NotEmpty
        String firstName,

        @NotEmpty
        String lastName,

        @NotEmpty
        String username,

        @NotEmpty(message = "The email field cannot be empty!")
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Please provide a valid email address")
        String email,

        @NotEmpty(message = "The password field cannot be empty!")
        String password
) {
}
