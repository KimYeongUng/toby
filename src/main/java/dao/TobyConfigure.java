package dao;

import ex.Message;
import ex.MessageFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import service.MockMailSender;
import service.TxProxyFactoryBean;
import service.UserService;
import service.impl.UserServiceImpl;
import service.impl.UserServiceTx;

import javax.sql.DataSource;

@Configuration
public class TobyConfigure {

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
    public TxProxyFactoryBean userService(){
        TxProxyFactoryBean userService = new TxProxyFactoryBean();
        userService.setServiceInterface(UserService.class);
        userService.setTransactionManager(transactionManager());
        userService.setPattern("upgradeLevels");
        return userService;
    }

    @Bean
    public UserServiceImpl userServiceImpl(){
        UserServiceImpl service = new UserServiceImpl();
        service.setUserDao(userDao());
        service.setDataSource(dataSource());
        service.setTransactionManager(transactionManager());
        service.setMailSender(mailSender());
        return service;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(){
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public MailSender mailSender(){
        return new MockMailSender();
    }

    @Bean
    public MessageFactoryBean message() throws Exception {
        MessageFactoryBean factoryBean = new MessageFactoryBean("message");
        return factoryBean;
    }
}
