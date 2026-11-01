package ee.taltech.ite4120.parking.dto;

import org.helex.commons.model.QueryParams;

/** List filters for permits. {@code limit}, {@code offset} and {@code sort} come from {@link QueryParams}. */
public class PermitQueryParams extends QueryParams {

    private String textContains;
    private Long permitTypeId;
    private Long holderId;

    public String getTextContains() { return textContains; }
    public void setTextContains(String textContains) { this.textContains = textContains; }
    public Long getPermitTypeId() { return permitTypeId; }
    public void setPermitTypeId(Long permitTypeId) { this.permitTypeId = permitTypeId; }
    public Long getHolderId() { return holderId; }
    public void setHolderId(Long holderId) { this.holderId = holderId; }
}
