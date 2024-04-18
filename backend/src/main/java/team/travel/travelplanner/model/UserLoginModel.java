package team.travel.travelplanner.model;

import jakarta.validation.constraints.NotEmpty;

public record UserLoginModel(

        @NotEmpty
        String username,

        @NotEmpty
        String password
){
}
