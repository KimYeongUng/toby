package dao;

import ex.MessageFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import proxy.NameMatchClassMethodPointcut;
import proxy.TransactionAdvice;
import service.MockMailSender;
import service.TxProxyFactoryBean;
import service.UserService;
import service.impl.UserServiceImpl;

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
    public TxProxyFactoryBean TxUserService(){
        TxProxyFactoryBean userService = new TxProxyFactoryBean();
        userService.setServiceInterface(UserService.class);
        userService.setTransactionManager(transactionManager());
        userService.setPattern("upgradeLevels");
        return userService;
    }

    @Bean(name = "userService")
    public UserServiceImpl userServiceImpl(){
        UserServiceImpl service = new UserServiceImpl();
        service.setUserDao(userDao());
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

    @Bean
    public TransactionAdvice transactionAdvice(){
        TransactionAdvice advice = new TransactionAdvice();
        advice.setTransactionManager(transactionManager());
        return advice;
    }

    @Bean
    public NameMatchMethodPointcut transactionPointcut(){
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("upgrade*");
        return pointcut;
    }

    @Bean(name = "transactionAdvisor")
    public DefaultPointcutAdvisor transactionAdvisor(){
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setAdvice(transactionAdvice());
        advisor.setPointcut(transactionPointcut());
        return advisor;
    }

    @Bean
    public DefaultPointcutAdvisor defaultPointcutAdvisor(){
        return new DefaultPointcutAdvisor();
    }

    @Bean
    public NameMatchClassMethodPointcut nameMatchMethodPointcut(){
        NameMatchClassMethodPointcut pointcut = new NameMatchClassMethodPointcut();
        pointcut.setMappedClassName("*ServiceImpl");
        pointcut.setMappedName("upgrade*");
        return pointcut;
    }

}
