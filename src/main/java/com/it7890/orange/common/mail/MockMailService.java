package com.it7890.orange.common.mail;

import java.util.Map;

import org.springframework.mail.SimpleMailMessage;

public class MockMailService extends MailService {

	@Override
	public void asyncSend(SimpleMailMessage msg, String templateName, Map model) {
	}

	@Override
	public void send(SimpleMailMessage msg) {

	}

	@Override
	public void send(SimpleMailMessage msg, String templateName, Map model) {

	}

}
