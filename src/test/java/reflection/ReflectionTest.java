package reflection;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.Assert.assertEquals;

public class ReflectionTest {

    @Test
    public void invokeMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String name = "Spring";

        assertEquals(name.length(),6);

        Method lengthMethod = String.class.getMethod("length");
        assertEquals(lengthMethod.invoke(name),6);

        assertEquals(name.charAt(0),'S');

        Method charAtMethod = String.class.getMethod("charAt", int.class);
        assertEquals(charAtMethod.invoke(name,0),'S');
    }

    @Test
    public void simpleProxy(){
        Hello hello = new HelloTarget();
        assertEquals(hello.sayHello("hero"),"hello hero");
        assertEquals(hello.sayHi("hero"),"hi hero");
        assertEquals(hello.thankYou("hero"),"thank you hero");
    }

    @Test
    public void proxiedHello(){
        Hello hello = new UpperCaseHello(new HelloTarget());

        assertEquals(hello.sayHello("hero"),"HELLO HERO");
        assertEquals(hello.sayHi("hero"),"HI HERO");
        assertEquals(hello.thankYou("hero"),"THANK YOU HERO");
    }

    @Test
    public void invocation(){
        Hello proxied = (Hello) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{Hello.class},
                new UpperCaseHandler(new HelloTarget()));

        assertEquals(proxied.sayHello("hero"),"HELLO HERO");
        assertEquals(proxied.thankYou("hero"),"thank you hero");
    }
}
