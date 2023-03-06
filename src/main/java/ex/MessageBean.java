package ex;

import org.springframework.beans.factory.FactoryBean;


public class MessageBean implements FactoryBean<Message> {
    String text;

    public MessageBean(String text) {
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
        return Message.getInstance(this.text);
    }

    @Override
    public Class<? extends Message> getObjectType() {
        return Message.class;
    }

    public static MessageBean getInstance(String text){
        return new MessageBean(text);
    }

    public boolean isSingleton(){
        return false;
    }
}
