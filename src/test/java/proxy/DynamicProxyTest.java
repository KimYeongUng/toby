package proxy;

import dao.TobyConfigure;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reflection.Hello;
import reflection.HelloTarget;
import reflection.UpperCaseHandler;

import java.lang.reflect.Proxy;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        assertEquals(Objects.requireNonNull(hello).sayHello("hero"),"HELLO HERO");
        assertEquals(hello.sayHi("hero"),"HI HERO");
        assertEquals(hello.thankYou("hero"),"THANK YOU HERO");

    }

    @Test
    public void classNamePointcutAdvisor(){
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut(){
            public ClassFilter getClassFilter(){
                return aClass -> aClass.getSimpleName().startsWith("HelloT");
            }
        };

        pointcut.setMappedName("sayH*");

        checkAdviced(new HelloTarget(),pointcut,true);
        class HelloWorld extends HelloTarget{}
        checkAdviced(new HelloWorld(),pointcut,false);

        class HelloTest extends HelloTarget{}
        checkAdviced(new HelloTest(),pointcut,true);

    }

    private void checkAdviced(Object target, NameMatchMethodPointcut pointcut, boolean adviced) {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut,new UpperCaseAdvice()));
        Hello hello  = (Hello) pfBean.getObject();

        if(adviced){
            assertEquals(Objects.requireNonNull(hello).sayHello("hero"),"HELLO HERO");
            assertEquals(hello.sayHi("hero"),"HI HERO");
            assertEquals(hello.thankYou("hero"),"thank you hero");
        }else {
            assertEquals(Objects.requireNonNull(hello).sayHello("hero"),"hello hero");
            assertEquals(hello.sayHi("hero"),"hi hero");
            assertEquals(hello.thankYou("hero"),"thank you hero");
        }

    }


    @Test
    public void pointCutAdvisor(){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("say*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut,new UpperCaseAdvice()));

        Hello hello = (Hello) pfBean.getObject();

        assertEquals(Objects.requireNonNull(hello).sayHello("hero"),"HELLO HERO");
        assertEquals(hello.sayHi("hero"),"HI HERO");
        assertEquals(hello.thankYou("hero"),"thank you hero");
    }

    @Test
    public void proxyFactoryBean(){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UpperCaseAdvice());
        Hello proxied = (Hello) pfBean.getObject();

        assertEquals(Objects.requireNonNull(proxied).sayHi("hero"),"HI HERO");
        assertEquals(Objects.requireNonNull(proxied).sayHello("hero"),"HELLO HERO");
        assertEquals(proxied.thankYou("hero"),"THANK YOU HERO");
    }

    static class UpperCaseAdvice implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            String ret = (String)methodInvocation.proceed();

            return Objects.requireNonNull(ret).toUpperCase();
        }
    }
}
