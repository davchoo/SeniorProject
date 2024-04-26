package team.travel.travelplanner.model;

import jakarta.validation.constraints.NotEmpty;

public record UserSignUpModel(
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
