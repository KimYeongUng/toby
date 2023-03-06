package message;

import dao.TobyConfigure;
import ex.Message;
import ex.MessageFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TobyConfigure.class)
public class MessageTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void getBean(){
        Object message = context.getBean("message");
        assertEquals(message.getClass(),Message.class);
    }

    @Test
    public void getFactoryBean(){
        Object bean = context.getBean("&message");
        assertEquals(bean.getClass(), MessageFactoryBean.class);
    }
}
