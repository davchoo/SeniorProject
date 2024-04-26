package team.travel.travelplanner.model.error;

import java.util.List;
import java.util.Objects;

public class ValidationErrorModel extends ApiErrorModel {
    private final List<String> globalErrors;

    private final List<ValueErrorModel> valueErrors;

    public ValidationErrorModel(List<String> globalErrors, List<ValueErrorModel> valueErrors) {
        super("validation_error", "Invalid data was provided");
        this.globalErrors = globalErrors;
        this.valueErrors = valueErrors;
    }

    public List<String> getGlobalErrors() {
        return globalErrors;
    }

    public List<ValueErrorModel> getValueErrors() {
        return valueErrors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ValidationErrorModel that = (ValidationErrorModel) o;
        return Objects.equals(globalErrors, that.globalErrors) && Objects.equals(valueErrors, that.valueErrors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), globalErrors, valueErrors);
    }

    @Override
    public String toString() {
        return "ValidationErrorModel{" +
                "globalErrors=" + globalErrors +
                ", valueErrors=" + valueErrors +
                '}';
    }
}
