package com.it7890.orange.common.mail;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * MailService ??�类.
 */
public abstract class MailService {

	protected static final Logger log = LoggerFactory.getLogger(MailService.class);

	private Configuration mailTemplateEngine = null;

	/**
	 * Method description
	 * 
	 * @param mailTemplateEngine
	 */
	public void setMailTemplateEngine(Configuration mailTemplateEngine) {
		this.mailTemplateEngine = mailTemplateEngine;
	}

	/**
	 * ??????SimpleMailMessage?????��??.
	 * 
	 * @param msg
	 */
	public abstract void send(SimpleMailMessage msg);

	/**
	 * �???????信�?��??.
	 * 
	 * @param from
	 * @param to
	 * @param subject
	 * @param text
	 */
	public void send(String from, String to, String subject, String text) {

		SimpleMailMessage msg = new SimpleMailMessage();

		msg.setFrom(from);
		msg.setTo(to);
		msg.setSubject(subject);
		msg.setText(text);
		send(msg);
	}

	/**
	 * 使�?�模?????????HTML??��????????�?.
	 * 
	 * @param msg
	 *            �????to,from,subject信�?????SimpleMailMessage
	 * @param templateName
	 *            模�?????,模�????�路�?已�?��??�????件�??�?�?freemakarengine�?
	 * @param model
	 *            渲�??模�?????????????��??
	 */
	public abstract void send(SimpleMailMessage msg, String templateName, Map model);

	/**
	 * 使�?�模???�?步�?????HTML??��????????�?.
	 * 
	 * @param msg
	 *            �????to,from,subject信�?????SimpleMailMessage
	 * @param templateName
	 *            模�?????,模�????�路�?已�?��??�????件�??�?�?freemakarengine�?
	 * @param model
	 *            渲�??模�?????????????��??
	 */
	public abstract void asyncSend(SimpleMailMessage msg, String templateName, Map model);

	/**
	 * 使�??Freemarker ??��??模�???????????件�??�?.
	 * 
	 * @param templateName
	 * @param map
	 * 
	 * @return
	 */
	public String generateEmailContent(String templateName, Map map) {

		try {
			Template t = mailTemplateEngine.getTemplate(templateName);

			return FreeMarkerTemplateUtils.processTemplateIntoString(t, map);
		} catch (TemplateException e) {
			log.error("Error while processing FreeMarker template ", e);
		} catch (FileNotFoundException e) {
			log.error("Error while open template file ", e);
		} catch (IOException e) {
			log.error("Error while generate Email Content ", e);
		}

		return null;
	}
}
