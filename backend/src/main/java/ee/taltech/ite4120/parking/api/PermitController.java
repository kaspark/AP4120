package ee.taltech.ite4120.parking.api;

import ee.taltech.ite4120.parking.dto.PermitQueryParams;
import ee.taltech.ite4120.parking.model.Permit;
import ee.taltech.ite4120.parking.model.PermitType;
import ee.taltech.ite4120.parking.model.Person;
import ee.taltech.ite4120.parking.service.PermitService;
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
@Tag(name = "Parking permits", description = "Parking permit portal — people, permit types, and permits")
public class PermitController {

    private final PermitService service;

    public PermitController(PermitService service) {
        this.service = service;
    }

    @Operation(summary = "Search permits")
    @GetMapping("/permits")
    public QueryResult<Permit> search(@ModelAttribute PermitQueryParams params) {
        return service.search(params);
    }

    @Operation(summary = "One permit by id")
    @GetMapping("/permits/{id}")
    public Permit byId(@PathVariable Long id) {
        return service.require(id);
    }

    @Operation(summary = "Grant a permit")
    @PostMapping("/permits")
    public ResponseEntity<Permit> create(@Valid @RequestBody PermitRequest body) {
        Permit saved = service.create(body.toModel());
        return ResponseEntity.created(URI.create("/api/permits/" + saved.getId())).body(saved);
    }

    @Operation(summary = "Update a permit")
    @PutMapping("/permits/{id}")
    public Permit update(@PathVariable Long id, @Valid @RequestBody PermitRequest body) {
        return service.update(id, body.toModel());
    }

    @Operation(summary = "Retire a permit (soft delete)")
    @DeleteMapping("/permits/{id}")
    public ResponseEntity<Void> retire(@PathVariable Long id) {
        service.retire(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Parking permit types")
    @GetMapping("/permit-types")
    public List<PermitType> permitTypes() {
        return service.permitTypes();
    }

    @Operation(summary = "Add a permit type")
    @PostMapping("/permit-types")
    public ResponseEntity<PermitType> createPermitType(@Valid @RequestBody PermitTypeRequest body) {
        PermitType saved = service.createPermitType(body.toModel());
        return ResponseEntity.created(URI.create("/api/permit-types/" + saved.getId())).body(saved);
    }

    @Operation(summary = "People who hold or grant permits")
    @GetMapping("/persons")
    public List<Person> persons() {
        return service.persons();
    }

    @Operation(summary = "One person by id")
    @GetMapping("/persons/{id}")
    public Person person(@PathVariable Long id) {
        return service.requirePerson(id);
    }

    @Operation(summary = "Add a person")
    @PostMapping("/persons")
    public ResponseEntity<Person> createPerson(@Valid @RequestBody PersonRequest body) {
        Person saved = service.createPerson(body.toModel());
        return ResponseEntity.created(URI.create("/api/persons/" + saved.getId())).body(saved);
    }
}
