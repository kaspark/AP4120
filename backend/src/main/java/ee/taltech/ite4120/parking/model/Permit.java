package ee.taltech.ite4120.parking.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/** One parking permit. Maps onto {@code tea.permits}. */
public class Permit {

    private Long id;
    private Long permitTypeId;
    private Long holderId;
    private Long grantedById;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    /** Joined for the list and the record page. Not columns of {@code tea.permits}. */
    private String permitTypeName;
    private String holderName;
    private String grantedByName;

    private String sysStatus;
    private Long sysVersion;
    private OffsetDateTime sysCreatedAt;
    private String sysCreatedBy;
    private OffsetDateTime sysModifiedAt;
    private String sysModifiedBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public String getPermitTypeName() { return permitTypeName; }
    public void setPermitTypeName(String permitTypeName) { this.permitTypeName = permitTypeName; }
    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }
    public String getGrantedByName() { return grantedByName; }
    public void setGrantedByName(String grantedByName) { this.grantedByName = grantedByName; }
    public String getSysStatus() { return sysStatus; }
    public void setSysStatus(String sysStatus) { this.sysStatus = sysStatus; }
    public Long getSysVersion() { return sysVersion; }
    public void setSysVersion(Long sysVersion) { this.sysVersion = sysVersion; }
    public OffsetDateTime getSysCreatedAt() { return sysCreatedAt; }
    public void setSysCreatedAt(OffsetDateTime sysCreatedAt) { this.sysCreatedAt = sysCreatedAt; }
    public OffsetDateTime getSysModifiedAt() { return sysModifiedAt; }
    public void setSysModifiedAt(OffsetDateTime sysModifiedAt) { this.sysModifiedAt = sysModifiedAt; }
    public String getSysCreatedBy() { return sysCreatedBy; }
    public void setSysCreatedBy(String sysCreatedBy) { this.sysCreatedBy = sysCreatedBy; }
    public String getSysModifiedBy() { return sysModifiedBy; }
    public void setSysModifiedBy(String sysModifiedBy) { this.sysModifiedBy = sysModifiedBy; }
}
