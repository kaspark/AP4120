package ee.taltech.ite4120.tea.service;

import ee.taltech.ite4120.tea.dto.TeaQueryParams;
import ee.taltech.ite4120.tea.model.Category;
import ee.taltech.ite4120.tea.model.Tea;
import ee.taltech.ite4120.tea.model.TeaUser;
import ee.taltech.ite4120.tea.repository.TeaRepository;
import java.util.List;
import org.helex.commons.exception.ApiClientException;
import org.helex.commons.exception.ConflictException;
import org.helex.commons.exception.NotFoundException;
import org.helex.commons.model.QueryResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeaService {

    private final TeaRepository repository;

    public TeaService(TeaRepository repository) {
        this.repository = repository;
    }

    public QueryResult<Tea> search(TeaQueryParams params) {
        return repository.search(params);
    }

    public Tea require(Long id) {
        Tea tea = repository.load(id);
        if (tea == null) {
            throw new NotFoundException("tea " + id + " not found");
        }
        return tea;
    }

    @Transactional
    public Tea create(Tea tea) {
        validate(tea);
        return require(repository.insert(tea));
    }

    @Transactional
    public Tea update(Long id, Tea tea) {
        require(id);
        validate(tea);
        tea.setId(id);
        if (!repository.update(tea)) {
            throw new NotFoundException("tea " + id + " not found");
        }
        return require(id);
    }

    @Transactional
    public void retire(Long id) {
        if (!repository.retire(id)) {
            throw new NotFoundException("tea " + id + " not found");
        }
    }

    public List<Category> categories() {
        return repository.categories();
    }

    public Category requireCategory(String name) {
        Category category = repository.loadCategoryByName(name);
        if (category == null) {
            throw new NotFoundException("category " + name + " not found");
        }
        return category;
    }

    @Transactional
    public Category createCategory(Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new ApiClientException("name is required");
        }
        if (repository.loadCategoryByName(category.getName()) != null) {
            throw new ConflictException("category",
                    "an active classification named " + category.getName() + " already exists");
        }
        Long id = repository.insertCategory(category);
        return repository.categories().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("category " + id + " not found"));
    }

    public List<TeaUser> users() {
        return repository.users();
    }

    public TeaUser requireUser(Long id) {
        TeaUser user = repository.loadUser(id);
        if (user == null) {
            throw new NotFoundException("user " + id + " not found");
        }
        return user;
    }

    @Transactional
    public TeaUser createUser(TeaUser user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ApiClientException("name is required");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ApiClientException("email is required");
        }
        if (repository.loadUserByEmail(user.getEmail()) != null) {
            throw new ConflictException("user",
                    "an active user with email " + user.getEmail() + " already exists");
        }
        Long id = repository.insertUser(user);
        return requireUser(id);
    }

    private void validate(Tea tea) {
        if (tea.getQuantity() == null) {
            throw new ApiClientException("quantity is required");
        }
        if (tea.getQuantity() < 0) {
            throw new ApiClientException("quantity must not be negative");
        }
        if (tea.getPurchaseDate() != null && tea.getExpiryDate() != null
                && tea.getExpiryDate().isBefore(tea.getPurchaseDate())) {
            throw new ApiClientException("expiryDate must not be before purchaseDate");
        }
        if (tea.getCategoryId() == null || !repository.categoryExists(tea.getCategoryId())) {
            throw new ApiClientException("unknown categoryId: " + tea.getCategoryId());
        }
        if (tea.getOwnerId() == null || !repository.userExists(tea.getOwnerId())) {
            throw new ApiClientException("unknown ownerId: " + tea.getOwnerId());
        }
    }
}
