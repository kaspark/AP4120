package ee.taltech.ite4120.tea.api;

import ee.taltech.ite4120.tea.model.Tea;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class TeaRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String brand;

    @NotNull
    private Long categoryId;

    @NotNull
    private Long ownerId;

    @NotNull
    private LocalDate purchaseDate;

    @NotNull
    private LocalDate expiryDate;

    @NotNull
    @Min(0)
    private Integer quantity;

    @NotBlank
    @Size(max = 30)
    private String unit;

    public Tea toModel() {
        Tea tea = new Tea();
        tea.setName(name);
        tea.setBrand(brand);
        tea.setCategoryId(categoryId);
        tea.setOwnerId(ownerId);
        tea.setPurchaseDate(purchaseDate);
        tea.setExpiryDate(expiryDate);
        tea.setQuantity(quantity);
        tea.setUnit(unit);
        return tea;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
