package com.unla.TPGrupal002Hito2Grupo07.services.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // Cuenta de Gmail configurada en spring.mail.username (variable de entorno EMAIL_USER)
    @Value("${spring.mail.username}")
    private String remitente;

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCredenciales(String destinatario, String usuario, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(remitente);
        message.setTo(destinatario);
        message.setSubject("Alta de Empleado - Epicentro Gourmet");
        message.setText("¡Bienvenido al staff de Epicentro Gourmet!\n\n"
                + "Tu cuenta ha sido dada de alta exitosamente. Tus credenciales de acceso son:\n\n"
                + "Usuario: " + usuario + "\n"
                + "Contrasena temporal: " + password + "\n\n"
                + "Por favor, no compartas esta información.");

        mailSender.send(message);
    }
}
