package br.edu.utfpr.emarketplace.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EnvioEmailServiceImpl {

    @Value("${spring.mail.password}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    public void resetTokenEmail(
            String contextPath, String token, String toEmail) {
        String url = contextPath + "/changePassword?token=" + token;
        String message = "Olá,\nPara alterar a sua senha, basta acessar o link e inserir a nova senha: \n";
        mailSender.send(constructEmail("Alteração de senha E-marketplace", message + url, toEmail));
    }

//    sage constructValidateInstituicaoEmail(String toEmail) {
//        String url = contextPath + "/changePassword?token=" + token;
//        String message = "Olá,\nPara alterar a sua senha, basta acessar o link e inserir a nova senha: \n";
//        return constructEmail("Alteração de senha Relojoaria Hora Certa", message + url, toEmail);
//    }

//    public SimpleMailMessage constructConviteInstituicaoEmail(String toEmail) {
//        String url = contextPath + "/changePassword?token=" + token;
//        String message = "Olá,\nPara alterar a sua senha, basta acessar o link e inserir a nova senha: \n";
//        return constructEmail("Alteração de senha Relojoaria Hora Certa", message + url, toEmail);
//    }

    public SimpleMailMessage constructEmail(String subject, String body,
                                            String toEmail) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setReplyTo("teste");
        email.setText(body);
        email.setTo(toEmail);
        email.setFrom( from);
        return email;
    }
}
