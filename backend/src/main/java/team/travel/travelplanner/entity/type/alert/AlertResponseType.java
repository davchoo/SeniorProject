package team.travel.travelplanner.entity.type.alert;

import com.fasterxml.jackson.annotation.JsonAlias;

public enum AlertResponseType {
    SHELTER,
    EVACUATE,
    PREPARE,
    EXECUTE,
    AVOID,
    MONITOR,
    ASSES,
    @JsonAlias("AllClear")
    ALL_CLEAR,
    NONE
}
