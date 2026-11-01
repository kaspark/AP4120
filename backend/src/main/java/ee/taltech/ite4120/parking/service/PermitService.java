package ee.taltech.ite4120.parking.service;

import ee.taltech.ite4120.parking.dto.PermitQueryParams;
import ee.taltech.ite4120.parking.model.Permit;
import ee.taltech.ite4120.parking.model.PermitType;
import ee.taltech.ite4120.parking.model.Person;
import ee.taltech.ite4120.parking.repository.PermitRepository;
import java.util.List;
import org.helex.commons.exception.ApiClientException;
import org.helex.commons.exception.ConflictException;
import org.helex.commons.exception.NotFoundException;
import org.helex.commons.model.QueryResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermitService {

    static final int DESCRIPTION_MAX = 200;

    private final PermitRepository repository;

    public PermitService(PermitRepository repository) {
        this.repository = repository;
    }

    public QueryResult<Permit> search(PermitQueryParams params) {
        return repository.search(params);
    }

    public Permit require(Long id) {
        Permit permit = repository.load(id);
        if (permit == null) {
            throw new NotFoundException("permit " + id + " not found");
        }
        return permit;
    }

    @Transactional
    public Permit create(Permit permit) {
        validate(permit);
        return require(repository.insert(permit));
    }

    @Transactional
    public Permit update(Long id, Permit permit) {
        require(id);
        validate(permit);
        permit.setId(id);
        if (!repository.update(permit)) {
            throw new NotFoundException("permit " + id + " not found");
        }
        return require(id);
    }

    @Transactional
    public void retire(Long id) {
        if (!repository.retire(id)) {
            throw new NotFoundException("permit " + id + " not found");
        }
    }

    public List<PermitType> permitTypes() {
        return repository.permitTypes();
    }

    public PermitType requirePermitType(String name) {
        PermitType type = repository.loadPermitTypeByName(name);
        if (type == null) {
            throw new NotFoundException("permit type " + name + " not found");
        }
        return type;
    }

    @Transactional
    public PermitType createPermitType(PermitType type) {
        if (type.getName() == null || type.getName().isBlank()) {
            throw new ApiClientException("name is required");
        }
        if (repository.loadPermitTypeByName(type.getName()) != null) {
            throw new ConflictException("permit-type",
                    "an active permit type named " + type.getName() + " already exists");
        }
        Long id = repository.insertPermitType(type);
        return repository.permitTypes().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("permit type " + id + " not found"));
    }

    public List<Person> persons() {
        return repository.persons();
    }

    public Person requirePerson(Long id) {
        Person person = repository.loadPerson(id);
        if (person == null) {
            throw new NotFoundException("person " + id + " not found");
        }
        return person;
    }

    @Transactional
    public Person createPerson(Person person) {
        if (person.getName() == null || person.getName().isBlank()) {
            throw new ApiClientException("name is required");
        }
        if (person.getEmail() == null || person.getEmail().isBlank()) {
            throw new ApiClientException("email is required");
        }
        if (repository.loadPersonByEmail(person.getEmail()) != null) {
            throw new ConflictException("person",
                    "an active person with email " + person.getEmail() + " already exists");
        }
        Long id = repository.insertPerson(person);
        return requirePerson(id);
    }

    private void validate(Permit permit) {
        if (permit.getDescription() == null || permit.getDescription().isBlank()) {
            throw new ApiClientException("description is required");
        }
        if (permit.getDescription().length() > DESCRIPTION_MAX) {
            throw new ApiClientException("description must be at most 200 characters");
        }
        if (permit.getStartDate() == null) {
            throw new ApiClientException("startDate is required");
        }
        if (permit.getEndDate() == null) {
            throw new ApiClientException("endDate is required");
        }
        if (permit.getEndDate().isBefore(permit.getStartDate())) {
            throw new ApiClientException("endDate must not be before startDate");
        }
        if (permit.getPermitTypeId() == null || !repository.permitTypeExists(permit.getPermitTypeId())) {
            throw new ApiClientException("unknown permitTypeId: " + permit.getPermitTypeId());
        }
        if (permit.getHolderId() == null || !repository.personExists(permit.getHolderId())) {
            throw new ApiClientException("unknown holderId: " + permit.getHolderId());
        }
        if (permit.getGrantedById() == null || !repository.personExists(permit.getGrantedById())) {
            throw new ApiClientException("unknown grantedById: " + permit.getGrantedById());
        }
    }
}
