package dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import service.UserService;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {

    @Bean
    public UserDaoImpl userDao(){
        UserDaoImpl dao = new UserDaoImpl();
        dao.setDataSource(dataSource());
        return dao;
    }

    @Bean
    public ConnectionMaker connectionMaker(){
        return new DConnectionMaker();
    }

    @Bean
    public DataSource dataSource(){
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(org.h2.Driver.class);
        dataSource.setUrl("jdbc:h2:tcp://localhost/~/test;AUTO_SERVER=true");
        dataSource.setUsername("sa");
        dataSource.setPassword("1234");

        return dataSource;
    }

    @Bean
    public UserService userService(){
        UserService service = new UserService();
        service.setUserDao(userDao());
        service.setDataSource(dataSource());
        service.setTransactionManager(transactionManager());
        return service;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(){
        return new DataSourceTransactionManager(dataSource());
    }
}
