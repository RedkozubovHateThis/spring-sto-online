package io.swagger.service.impl;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import io.swagger.service.MailSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailSendServiceImpl implements MailSendService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.from}")
    private String mailFrom;

    @Value("${domain.origin}")
    private String originalDomain;

    @Override
    public void sendPasswordRestoreMail(String email, String subject, String uuid) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setFrom(mailFrom);
            helper.setSubject(subject);
            helper.setText( buildPasswordRestoreContent(uuid), true );

            javaMailSender.send(message);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    private String buildPasswordRestoreContent(String uuid) throws IOException {
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/templates");
        loader.setSuffix(".hbs");
        Handlebars handlebars = new Handlebars(loader);

        Template mainTemplate = handlebars.compile("MainTemplate");
        Template passwordRestoreTemplate = handlebars.compile("PasswordRestoreTemplate");

        Map<String, Object> passwordRestoreTemplateParams = new HashMap<String, Object>() {{
            put("originalDomain", originalDomain);
            put("restoreLink", originalDomain + "/restore");
            put("hash", uuid);
        }};
        Map<String, Object> mainTemplateParams = new HashMap<String, Object>() {{
            put("originalDomain", originalDomain);
            put("content", passwordRestoreTemplate.apply( passwordRestoreTemplateParams ) );
        }};

        return mainTemplate.apply(mainTemplateParams);
    }

}
