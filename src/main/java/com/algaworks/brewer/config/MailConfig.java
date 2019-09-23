package com.algaworks.brewer.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource({ "classpath:env/mail-${ambiente:local}.properties" })
public class MailConfig {

	
	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("in-v3.mailjet.com");
		mailSender.setUsername(System.getenv("EMAIL_USERNAME"));
		mailSender.setPassword(System.getenv("EMAIL_PASSWORD"));
		
		Properties props = mailSender.getJavaMailProperties();
		props.put ("mail.smtp.host", "in.mailjet.com");
		props.put ("mail.smtp.socketFactory.port", "465");
		props.put ("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put ("mail.smtp.auth", "true");
		props.put ("mail.smtp.port", "465");
		
		mailSender.setJavaMailProperties(props);
		
		return mailSender;
	}
}
//@PropertySource(value = { "file://${HOME}/.brewer-mail.properties" }, ignoreResourceNotFound = true)
//@PropertySource({ "classpath:env/mail-${ambiente:local}.properties" })

