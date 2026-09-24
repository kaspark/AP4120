package ee.taltech.ite4120.tea.api;

import ee.taltech.ite4120.tea.dto.TeaQueryParams;
import ee.taltech.ite4120.tea.model.Category;
import ee.taltech.ite4120.tea.model.Tea;
import ee.taltech.ite4120.tea.model.TeaUser;
import ee.taltech.ite4120.tea.service.TeaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.helex.commons.model.QueryResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Tea", description = "Tea register — users, classifications, and stocked teas")
public class TeaController {

    private final TeaService service;

    public TeaController(TeaService service) {
        this.service = service;
    }

    @Operation(summary = "Search teas")
    @GetMapping("/teas")
    public QueryResult<Tea> search(@ModelAttribute TeaQueryParams params) {
        return service.search(params);
    }

    @Operation(summary = "One tea by id")
    @GetMapping("/teas/{id}")
    public Tea byId(@PathVariable Long id) {
        return service.require(id);
    }

    @Operation(summary = "Register a tea")
    @PostMapping("/teas")
    public ResponseEntity<Tea> create(@Valid @RequestBody TeaRequest body) {
        Tea saved = service.create(body.toModel());
        return ResponseEntity.created(URI.create("/api/teas/" + saved.getId())).body(saved);
    }

    @Operation(summary = "Update a tea")
    @PutMapping("/teas/{id}")
    public Tea update(@PathVariable Long id, @Valid @RequestBody TeaRequest body) {
        return service.update(id, body.toModel());
    }

    @Operation(summary = "Retire a tea (soft delete)")
    @DeleteMapping("/teas/{id}")
    public ResponseEntity<Void> retire(@PathVariable Long id) {
        service.retire(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Tea classifications")
    @GetMapping("/categories")
    public List<Category> categories() {
        return service.categories();
    }

    @Operation(summary = "Add a tea classification")
    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryRequest body) {
        Category saved = service.createCategory(body.toModel());
        return ResponseEntity.created(URI.create("/api/categories/" + saved.getId())).body(saved);
    }

    @Operation(summary = "Users")
    @GetMapping("/users")
    public List<TeaUser> users() {
        return service.users();
    }

    @Operation(summary = "One user by id")
    @GetMapping("/users/{id}")
    public TeaUser user(@PathVariable Long id) {
        return service.requireUser(id);
    }

    @Operation(summary = "Add a user")
    @PostMapping("/users")
    public ResponseEntity<TeaUser> createUser(@Valid @RequestBody UserRequest body) {
        TeaUser saved = service.createUser(body.toModel());
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId())).body(saved);
    }
}
