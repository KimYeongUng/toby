package service;

import handler.TransactionHandler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Proxy;

public class TxProxyFactoryBean implements FactoryBean<Object> {

    Object target;
    PlatformTransactionManager transactionManager;
    String pattern;
    Class<?> serviceInterface;

    // target setting
    public void setTarget(Object target){
        this.target = target;
    }

    // transactionManager DI
    public void setTransactionManager(PlatformTransactionManager transactionManager){
        this.transactionManager = transactionManager;
    }

    // pointCut - startWith 'get*'
    public void setPattern(String pattern){
        this.pattern = pattern;
    }

    public void setServiceInterface(Class<?> serviceInterface){
        this.serviceInterface = serviceInterface;
    }

    @Override
    public Object getObject() {
        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(target);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPattern(pattern);

        return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{serviceInterface},txHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
