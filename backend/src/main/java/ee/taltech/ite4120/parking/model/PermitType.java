package ee.taltech.ite4120.parking.model;

/** One row of {@code tea.permit_type} — Resident, Visitor, Staff, or Service. */
public class PermitType {

    private Long id;
    private String name;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
