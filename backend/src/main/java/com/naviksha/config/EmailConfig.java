package com.naviksha.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Properties;

@Configuration
public class EmailConfig {
    
    @Value("${spring.mail.host}")
    private String mailHost;
    
    @Value("${spring.mail.port}")
    private int mailPort;
    
    @Value("${spring.mail.username}")
    private String mailUsername;
    
    @Value("${spring.mail.password}")
    private String mailPassword;
    
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        
        // Gmail-specific configuration based on port
        if (mailPort == 465) {
            // Port 465 uses SSL (Gmail supports this)
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.required", "true");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
        } else {
            // Port 587 uses STARTTLS (Gmail recommended)
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        }
        
        // Connection timeouts (optimized for Gmail and cloud hosting)
        // Connection: 15 seconds - Gmail responds quickly, fail fast if server is unreachable
        props.put("mail.smtp.connectiontimeout", "15000"); // 15 seconds
        // Read/IO: 25 seconds - Allows time for large attachments (PDFs) while avoiding long hangs
        props.put("mail.smtp.timeout", "25000"); // 25 seconds
        // Write: 25 seconds - Email with PDF attachment can take time to upload
        props.put("mail.smtp.writetimeout", "25000"); // 25 seconds
        
        // Gmail SSL trust settings
        props.put("mail.smtp.ssl.trust", "*"); // Trust all SSL certificates (Gmail uses valid certs)
        props.put("mail.smtp.ssl.checkserveridentity", "true");
        
        // Gmail-specific optimizations
        props.put("mail.smtp.quitwait", "false");
        
        // Debug (set to false in production, true for troubleshooting)
        props.put("mail.debug", "false");
        
        return mailSender;
    }
    
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        return templateEngine;
    }
    
    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false); // Set to true in production
        return templateResolver;
    }
}
