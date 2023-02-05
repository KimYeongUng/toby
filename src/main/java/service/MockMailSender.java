package service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MockMailSender implements MailSender {
    List<String> requests = new ArrayList<>();

    public List<String> getRequests(){
        return this.requests;
    }
    @Override
    public void send(SimpleMailMessage simpleMailMessage) throws MailException {
        requests.add(Objects.requireNonNull(simpleMailMessage.getTo())[0]);
    }

    @Override
    public void send(SimpleMailMessage... simpleMailMessages) throws MailException {

    }
}
