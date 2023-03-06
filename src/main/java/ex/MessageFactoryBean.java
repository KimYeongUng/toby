package ex;

import org.springframework.beans.factory.FactoryBean;


public class MessageFactoryBean implements FactoryBean<Message> {
    String text;

    public MessageFactoryBean(String text){
        this.text = text;
    }


    public void setText(String text){
        this.text = text;
    }

    public String getText(){
        return this.text;
    }

    @Override
    public Message getObject() throws Exception {
        return Message.newMessage(this.text);
    }

    @Override
    public Class<? extends Message> getObjectType() {
        return Message.class;
    }

    public boolean isSingleton(){
        return false;
    }
}
