package dao;


import domain.Level;
import domain.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

public class UserDaoImpl implements UserDao {

    JdbcTemplate jdbcTemplate;

    DataSource dataSource;

    private RowMapper<User> userMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        user.setLevel(Level.getValueOf(rs.getInt("level")));
        user.setLogin(rs.getInt("login"));
        user.setRecommend(rs.getInt("recommend"));
        return user;
    };


    UserDaoImpl() {
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(User user) throws DuplicateKeyException {
            this.jdbcTemplate.update("insert into users(id,name,password,level,login,recommend,email) values(?,?,?,?,?,?,?)"
                    , user.getId()
                    , user.getName()
                    , user.getPassword()
                    , user.getLevel().intValue()
                    , user.getLogin()
                    , user.getRecommend()
                    , user.getEmail());
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?",
                new Object[]{id},this.userMapper);
    }

    public int deleteAll() {
        return this.jdbcTemplate.update("delete from users");
    }

    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from users",Integer.class);
    }

    @Override
    public int update(User user) {
        return this.jdbcTemplate.update(
                "update users set name=?,level=?,login=?,recommend=?,email=? where id=?",
                user.getName(),user.getLevel().intValue(),user.getLogin(),user.getRecommend(),user.getEmail(),user.getId()
        );
    }

    public List<User> getAll(){
        return this.jdbcTemplate.query("select * from users order by id",this.userMapper);
    }


}
