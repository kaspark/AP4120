package ee.taltech.ite4120.parking.api;

import ee.taltech.ite4120.parking.model.Permit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class PermitRequest {

    @NotNull
    private Long permitTypeId;

    @NotNull
    private Long holderId;

    @NotNull
    private Long grantedById;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotBlank
    @Size(max = 200)
    private String description;

    public Permit toModel() {
        Permit permit = new Permit();
        permit.setPermitTypeId(permitTypeId);
        permit.setHolderId(holderId);
        permit.setGrantedById(grantedById);
        permit.setStartDate(startDate);
        permit.setEndDate(endDate);
        permit.setDescription(description);
        return permit;
    }

    public Long getPermitTypeId() { return permitTypeId; }
    public void setPermitTypeId(Long permitTypeId) { this.permitTypeId = permitTypeId; }
    public Long getHolderId() { return holderId; }
    public void setHolderId(Long holderId) { this.holderId = holderId; }
    public Long getGrantedById() { return grantedById; }
    public void setGrantedById(Long grantedById) { this.grantedById = grantedById; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
