package ee.taltech.ite4120.tea.api;

import ee.taltech.ite4120.tea.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    public Category toModel() {
        Category category = new Category();
        category.setName(name);
        return category;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
