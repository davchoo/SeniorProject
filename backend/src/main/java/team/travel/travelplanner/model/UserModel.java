package team.travel.travelplanner.model;

import jakarta.validation.constraints.NotEmpty;

public record UserModel(
        @NotEmpty
        String firstName,

        @NotEmpty
        String lastName,

        @NotEmpty
        String username,

        @NotEmpty(message = "The email field cannot be empty!")
        String email,

        @NotEmpty(message = "The password field cannot be empty!")
        String password
) {
}
