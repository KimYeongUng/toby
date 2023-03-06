package reflection;

public class HelloTarget implements Hello{
    @Override
    public String sayHi(String name) {
        return "hi "+name;
    }

    @Override
    public String sayHello(String name) {
        return "hello "+name;
    }

    @Override
    public String thankYou(String name) {
        return "thank you "+name;
    }
}
