package ee.taltech.ite4120.parking.api;

import ee.taltech.ite4120.parking.model.PermitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PermitTypeRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    public PermitType toModel() {
        PermitType type = new PermitType();
        type.setName(name);
        return type;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
