package br.com.ifmg.event_manager.services;

import br.com.ifmg.event_manager.dtos.EmailDTO;
import br.com.ifmg.event_manager.services.exceptions.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Value("${spring.mail.username}")
    private String emailFrom;

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(EmailDTO dto) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(dto.getTo());
            message.setSubject(dto.getSubject());
            message.setText(dto.getBody());
            mailSender.send(message);
        } catch (MailSendException e) {
            throw new EmailException(e.getMessage());
        }


    }

}
