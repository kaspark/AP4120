package ee.taltech.ite4120.tea.repository;

import ee.taltech.ite4120.tea.dto.TeaQueryParams;
import ee.taltech.ite4120.tea.model.Category;
import ee.taltech.ite4120.tea.model.Tea;
import ee.taltech.ite4120.tea.model.TeaUser;
import java.util.List;
import java.util.Map;
import org.helex.commons.db.bean.PgBeanProcessor;
import org.helex.commons.db.repo.BaseRepository;
import org.helex.commons.db.sql.SqlBuilder;
import org.helex.commons.exception.ApiClientException;
import org.helex.commons.model.QueryResult;
import org.springframework.stereotype.Repository;

@Repository
public class TeaRepository extends BaseRepository {

    private static final String SELECT = """
            select t.*, c.name as category_name, u.name as owner_name
              from tea.tea t
              join tea.category c on c.id = t.category_id
              join tea.tea_user u on u.id = t.owner_id
            """;

    private static final Map<String, String> SORT = Map.of(
            "name", "t.name",
            "brand", "t.brand",
            "purchaseDate", "t.purchase_date",
            "expiryDate", "t.expiry_date");

    private final PgBeanProcessor teaBp = new PgBeanProcessor(Tea.class);
    private final PgBeanProcessor userBp = new PgBeanProcessor(TeaUser.class);
    private final PgBeanProcessor categoryBp = new PgBeanProcessor(Category.class);

    public Tea load(Long id) {
        return getBean(SELECT + "where t.id = ? and t.sys_status = 'A'", teaBp, id);
    }

    public QueryResult<Tea> search(TeaQueryParams params) {
        return query(params, this::count, this::list);
    }

    private Integer count(TeaQueryParams p) {
        SqlBuilder sb = new SqlBuilder("""
                select count(1)
                  from tea.tea t
                  join tea.category c on c.id = t.category_id
                  join tea.tea_user u on u.id = t.owner_id
                 where t.sys_status = 'A'""");
        sb.append(filter(p));
        return queryForObject(sb.getSql(), Integer.class, sb.getParams());
    }

    private List<Tea> list(TeaQueryParams p) {
        SqlBuilder sb = new SqlBuilder(SELECT + "where t.sys_status = 'A'");
        sb.append(filter(p));
        sb.append(order(p));
        sb.append(limit(p));
        return getBeans(sb.getSql(), teaBp, sb.getParams());
    }

    private SqlBuilder order(TeaQueryParams p) {
        try {
            return order(p, SORT);
        } catch (IllegalArgumentException e) {
            throw new ApiClientException("unknown sort key; sortable: " + String.join(", ", SORT.keySet().stream().sorted().toList()));
        }
    }

    private SqlBuilder filter(TeaQueryParams p) {
        SqlBuilder sb = new SqlBuilder();
        if (p.getTextContains() != null && !p.getTextContains().isBlank()) {
            String like = "%" + p.getTextContains().toLowerCase() + "%";
            sb.and("(lower(t.name) like ? or lower(t.brand) like ?)", like, like);
        }
        sb.appendIfNotNull("and t.category_id = ?", p.getCategoryId());
        sb.appendIfNotNull("and t.owner_id = ?", p.getOwnerId());
        return sb;
    }

    public Long insert(Tea tea) {
        String sql = """
                insert into tea.tea (name, brand, category_id, owner_id, purchase_date, expiry_date, quantity, unit)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                returning id""";
        return queryForObject(sql, Long.class,
                tea.getName(), tea.getBrand(), tea.getCategoryId(), tea.getOwnerId(),
                tea.getPurchaseDate(), tea.getExpiryDate(), tea.getQuantity(), tea.getUnit());
    }

    public boolean update(Tea tea) {
        String sql = """
                update tea.tea
                   set name = ?, brand = ?, category_id = ?, owner_id = ?,
                       purchase_date = ?, expiry_date = ?, quantity = ?, unit = ?
                 where id = ? and sys_status = 'A'""";
        return jdbcTemplate.update(sql,
                tea.getName(), tea.getBrand(), tea.getCategoryId(), tea.getOwnerId(),
                tea.getPurchaseDate(), tea.getExpiryDate(), tea.getQuantity(), tea.getUnit(),
                tea.getId()) == 1;
    }

    public boolean retire(Long id) {
        return jdbcTemplate.update(
                "update tea.tea set sys_status = 'C' where id = ? and sys_status = 'A'", id) == 1;
    }

    public boolean categoryExists(Long id) {
        Integer n = queryForObject(
                "select count(1) from tea.category where id = ? and sys_status = 'A'", Integer.class, id);
        return n != null && n > 0;
    }

    public boolean userExists(Long id) {
        Integer n = queryForObject(
                "select count(1) from tea.tea_user where id = ? and sys_status = 'A'", Integer.class, id);
        return n != null && n > 0;
    }

    public List<Category> categories() {
        return getBeans(
                "select c.* from tea.category c where c.sys_status = 'A' order by c.name", categoryBp);
    }

    public Category loadCategoryByName(String name) {
        return getBean(
                "select c.* from tea.category c where lower(c.name) = lower(?) and c.sys_status = 'A'",
                categoryBp, name);
    }

    public Long insertCategory(Category category) {
        return queryForObject(
                "insert into tea.category (name) values (?) returning id", Long.class, category.getName());
    }

    public List<TeaUser> users() {
        return getBeans(
                "select u.* from tea.tea_user u where u.sys_status = 'A' order by u.name", userBp);
    }

    public TeaUser loadUser(Long id) {
        return getBean("select u.* from tea.tea_user u where u.id = ? and u.sys_status = 'A'", userBp, id);
    }

    public TeaUser loadUserByEmail(String email) {
        return getBean(
                "select u.* from tea.tea_user u where lower(u.email) = lower(?) and u.sys_status = 'A'",
                userBp, email);
    }

    public Long insertUser(TeaUser user) {
        return queryForObject(
                "insert into tea.tea_user (name, email) values (?, ?) returning id",
                Long.class, user.getName(), user.getEmail());
    }
}
