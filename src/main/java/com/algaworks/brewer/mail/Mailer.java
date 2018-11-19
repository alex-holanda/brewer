package com.algaworks.brewer.mail;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.storage.FotoStorage;

@Component
public class Mailer {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine thymeleaf;
	
	@Autowired
	private FotoStorage fotoStorage;
	
	private static Logger logger = LoggerFactory.getLogger(Mailer.class);
	
	@Async
	public void enviar(Venda venda) {
		
		Context context = new Context(new Locale("pt", "BR"));
		context.setVariable("venda", venda);
		context.setVariable("logo", "logo");
		
		Map<String, String> fotos = new HashMap<>();
		
		for (ItemVenda item : venda.getItens()) {
			Cerveja cerveja = item.getCerveja();
			
			String cid = "foto-" + cerveja.getCodigo();
			
			context.setVariable(cid, cid);
			
			fotos.put(cid, cerveja.getFotoOuMock());
			
		}
		
		System.out.println(">>> " + fotos);
		
		try {
			String email = thymeleaf.process("mail/ResumoVenda", context);
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
			
			helper.setFrom("alex.holanda@outlook.com");
			helper.setTo("alex@perfilnet.com.br"); //venda.getCliente().getEmail()
			helper.setSubject(String.format("Brewer - Venda nº %d", venda.getCodigo()));
			helper.setText(email, true);
			
			helper.addInline("logo", new ClassPathResource("static/images/logo-gray.png"));
			
			for (String cid : fotos.keySet()) {
				byte[] thumbnail = fotoStorage.recuperarThumbnail(fotos.get(cid));
				helper.addInline(cid, new ByteArrayResource(thumbnail), "image/png");
			}
			
			mailSender.send(mimeMessage);
			
		} catch (MessagingException e) {
			logger.error("Erro enviando e-mail", e);
		}
		
	}
	
}

//SimpleMailMessage mensagem = new SimpleMailMessage();
//
//mensagem.setFrom("alex.holanda@outlook.com");
//mensagem.setTo("alex@perfilnet.com.br");
//mensagem.setSubject("Venda evetuada");
//mensagem.setText("Obrigado, sua venda foi processada");
//
//mailSender.send(mensagem);