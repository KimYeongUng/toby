package proxy;

import dao.TobyConfigure;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reflection.Hello;
import reflection.HelloTarget;
import reflection.UpperCaseHandler;

import java.lang.reflect.Proxy;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TobyConfigure.class)
public class DynamicProxyTest {

    @Test
    public void simpleProxy(){
        Hello proxied = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{Hello.class},
                new UpperCaseHandler(new HelloTarget()));

        assertEquals(proxied.sayHello("hero"),"HELLO HERO");
        assertEquals(proxied.thankYou("hero"),"thank you hero");
    }

    @Test
    public void proxyFactoryBeanTest(){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UpperCaseAdvice());
        Hello hello = (Hello)pfBean.getObject();

        assertEquals(hello.sayHello("hero"),"HELLO HERO");
        assertEquals(hello.sayHi("hero"),"HI HERO");
        assertEquals(hello.thankYou("hero"),"THANK YOU HERO");

    }

    static class UpperCaseAdvice implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            String ret = (String)methodInvocation.proceed();

            return Objects.requireNonNull(ret).toUpperCase();
        }
    }
}
