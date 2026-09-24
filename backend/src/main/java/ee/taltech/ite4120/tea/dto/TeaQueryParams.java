package ee.taltech.ite4120.tea.dto;

import org.helex.commons.model.QueryParams;

/** List filters for teas. {@code limit}, {@code offset} and {@code sort} come from {@link QueryParams}. */
public class TeaQueryParams extends QueryParams {

    private String textContains;
    private Long categoryId;
    private Long ownerId;

    public String getTextContains() { return textContains; }
    public void setTextContains(String textContains) { this.textContains = textContains; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
