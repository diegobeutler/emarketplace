package br.edu.utfpr.emarketplace.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class EnvioEmailService {

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    public void resetTokenEmail(
            String contextPath, String token, String toEmail) {
        String url = contextPath + "/changePassword?token=" + token;
        String message = "Olá,\nPara alterar a sua senha, basta acessar o link e inserir a nova senha: \n";
        mailSender.send(constructEmail("Alteração de senha E-marketplace", message + url, toEmail));
    }

    public void validateInstituicaoEmail(String url, Set<String> toEmail) {
        String message = "Olá,\nFavor validar o cadastro da instituição, acessando o link: \n";
        toEmail.forEach(e -> mailSender.send(constructEmail("Validação de cadastro E-marketplace", message + url, e)));
    }

    public void convidarInstituicaoEmail(String toEmail) {
        String message = "Olá,\nGostaria de informar que um usuário do E-marketplace, não lhe encontrou na lista de instituições disponíveis para doação de produtos/valores." +
                "\nPara que possa receber doações por meio do sistema, deve efetuar o seu cadastro, acessando o endereço: http://localhost:4200/usuario/form";
        mailSender.send(constructEmail("Venha fazer parte do E-marketplace!", message, toEmail));
    }

    public void notificarInstituicaoEmail(String toEmail) {
        String message = "Olá,\nSeu cadastro foi validado por um de nossos administradores." +
                "\nAcesse: http://localhost:4200/login" +
                "\nNessa tela basta entrar com seu login e senha. Caso tenha esquecido, pode acessar a opção de redefinir senha para a alteração.";
        mailSender.send(constructEmail("Bem vinda ao E-marketplace!", message, toEmail));
    }

    private SimpleMailMessage constructEmail(String subject, String body,
                                            String toEmail) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setReplyTo(from);
        email.setText(body);
        email.setTo(toEmail);
        email.setFrom("E-marketplace");
        return email;
    }
}
