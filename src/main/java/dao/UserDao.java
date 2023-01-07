package dao;


import domain.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class UserDao {

    JdbcTemplate jdbcTemplate;

    DataSource dataSource;

    private RowMapper<User> userMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        return user;
    };


    UserDao() {
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(User user) throws DuplicateKeyException {
            this.jdbcTemplate.update("insert into users(id,name,password) values(?,?,?)"
                    , user.getId()
                    , user.getName()
                    , user.getPassword());
    }

    public User get(String id) throws SQLException {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?",
                new Object[]{id},this.userMapper);
    }

    public int deleteAll() throws SQLException {
        return this.jdbcTemplate.update("delete from users");
    }

    public int getCount() throws SQLException {
        return this.jdbcTemplate.queryForObject("select count(*) from users",Integer.class);
    }

    public List<User> getAll(){
        return this.jdbcTemplate.query("select * from users order by id",this.userMapper);
    }


}
