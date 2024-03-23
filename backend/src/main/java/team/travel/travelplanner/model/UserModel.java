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

        @NotEmpty(message = "The password field cannot be empty!")
        String password
) {
}
