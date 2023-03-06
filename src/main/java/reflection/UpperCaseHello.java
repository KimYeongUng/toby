package reflection;

public class UpperCaseHello implements Hello{
    Hello hello;

    UpperCaseHello(Hello hello){
        this.hello = hello;
    }

    @Override
    public String sayHi(String name) {
        return hello.sayHi(name).toUpperCase();
    }

    @Override
    public String sayHello(String name) {
        return hello.sayHello(name).toUpperCase();
    }

    @Override
    public String thankYou(String name) {
        return hello.thankYou(name).toUpperCase();
    }
}
