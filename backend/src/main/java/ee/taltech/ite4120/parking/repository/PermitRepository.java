package ee.taltech.ite4120.parking.repository;

import ee.taltech.ite4120.parking.dto.PermitQueryParams;
import ee.taltech.ite4120.parking.model.Permit;
import ee.taltech.ite4120.parking.model.PermitType;
import ee.taltech.ite4120.parking.model.Person;
import java.util.List;
import java.util.Map;
import org.helex.commons.db.bean.PgBeanProcessor;
import org.helex.commons.db.repo.BaseRepository;
import org.helex.commons.db.sql.SqlBuilder;
import org.helex.commons.exception.ApiClientException;
import org.helex.commons.model.QueryResult;
import org.springframework.stereotype.Repository;

@Repository
public class PermitRepository extends BaseRepository {

    private static final String SELECT = """
            select p.*, t.name as permit_type_name, h.name as holder_name, g.name as granted_by_name
              from tea.permits p
              join tea.permit_type t on t.id = p.permit_type_id
              join tea.person h on h.id = p.holder_id
              join tea.person g on g.id = p.granted_by_id
            """;

    private static final Map<String, String> SORT = Map.of(
            "startDate", "p.start_date",
            "endDate", "p.end_date",
            "holderName", "h.name");

    private final PgBeanProcessor permitBp = new PgBeanProcessor(Permit.class);
    private final PgBeanProcessor personBp = new PgBeanProcessor(Person.class);
    private final PgBeanProcessor typeBp = new PgBeanProcessor(PermitType.class);

    public Permit load(Long id) {
        return getBean(SELECT + "where p.id = ? and p.sys_status = 'A'", permitBp, id);
    }

    public QueryResult<Permit> search(PermitQueryParams params) {
        return query(params, this::count, this::list);
    }

    private Integer count(PermitQueryParams p) {
        SqlBuilder sb = new SqlBuilder("""
                select count(1)
                  from tea.permits p
                  join tea.permit_type t on t.id = p.permit_type_id
                  join tea.person h on h.id = p.holder_id
                  join tea.person g on g.id = p.granted_by_id
                 where p.sys_status = 'A'""");
        sb.append(filter(p));
        return queryForObject(sb.getSql(), Integer.class, sb.getParams());
    }

    private List<Permit> list(PermitQueryParams p) {
        SqlBuilder sb = new SqlBuilder(SELECT + "where p.sys_status = 'A'");
        sb.append(filter(p));
        sb.append(order(p));
        sb.append(limit(p));
        return getBeans(sb.getSql(), permitBp, sb.getParams());
    }

    private SqlBuilder order(PermitQueryParams p) {
        try {
            return order(p, SORT);
        } catch (IllegalArgumentException e) {
            throw new ApiClientException("unknown sort key; sortable: " + String.join(", ", SORT.keySet().stream().sorted().toList()));
        }
    }

    private SqlBuilder filter(PermitQueryParams p) {
        SqlBuilder sb = new SqlBuilder();
        if (p.getTextContains() != null && !p.getTextContains().isBlank()) {
            String like = "%" + p.getTextContains().toLowerCase() + "%";
            sb.and("(lower(p.description) like ? or lower(h.name) like ? or lower(g.name) like ?)", like, like, like);
        }
        sb.appendIfNotNull("and p.permit_type_id = ?", p.getPermitTypeId());
        sb.appendIfNotNull("and p.holder_id = ?", p.getHolderId());
        return sb;
    }

    public Long insert(Permit permit) {
        String sql = """
                insert into tea.permits (permit_type_id, holder_id, granted_by_id, start_date, end_date, description)
                values (?, ?, ?, ?, ?, ?)
                returning id""";
        return queryForObject(sql, Long.class,
                permit.getPermitTypeId(), permit.getHolderId(), permit.getGrantedById(),
                permit.getStartDate(), permit.getEndDate(), permit.getDescription());
    }

    public boolean update(Permit permit) {
        String sql = """
                update tea.permits
                   set permit_type_id = ?, holder_id = ?, granted_by_id = ?,
                       start_date = ?, end_date = ?, description = ?
                 where id = ? and sys_status = 'A'""";
        return jdbcTemplate.update(sql,
                permit.getPermitTypeId(), permit.getHolderId(), permit.getGrantedById(),
                permit.getStartDate(), permit.getEndDate(), permit.getDescription(),
                permit.getId()) == 1;
    }

    public boolean retire(Long id) {
        return jdbcTemplate.update(
                "update tea.permits set sys_status = 'C' where id = ? and sys_status = 'A'", id) == 1;
    }

    public boolean permitTypeExists(Long id) {
        Integer n = queryForObject(
                "select count(1) from tea.permit_type where id = ? and sys_status = 'A'", Integer.class, id);
        return n != null && n > 0;
    }

    public boolean personExists(Long id) {
        Integer n = queryForObject(
                "select count(1) from tea.person where id = ? and sys_status = 'A'", Integer.class, id);
        return n != null && n > 0;
    }

    public List<PermitType> permitTypes() {
        return getBeans(
                "select t.* from tea.permit_type t where t.sys_status = 'A' order by t.name", typeBp);
    }

    public PermitType loadPermitTypeByName(String name) {
        return getBean(
                "select t.* from tea.permit_type t where lower(t.name) = lower(?) and t.sys_status = 'A'",
                typeBp, name);
    }

    public Long insertPermitType(PermitType type) {
        return queryForObject(
                "insert into tea.permit_type (name) values (?) returning id", Long.class, type.getName());
    }

    public List<Person> persons() {
        return getBeans(
                "select p.* from tea.person p where p.sys_status = 'A' order by p.name", personBp);
    }

    public Person loadPerson(Long id) {
        return getBean("select p.* from tea.person p where p.id = ? and p.sys_status = 'A'", personBp, id);
    }

    public Person loadPersonByEmail(String email) {
        return getBean(
                "select p.* from tea.person p where lower(p.email) = lower(?) and p.sys_status = 'A'",
                personBp, email);
    }

    public Long insertPerson(Person person) {
        return queryForObject(
                "insert into tea.person (name, email) values (?, ?) returning id",
                Long.class, person.getName(), person.getEmail());
    }
}
