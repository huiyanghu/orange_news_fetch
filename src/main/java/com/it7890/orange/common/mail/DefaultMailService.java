package com.it7890.orange.common.mail;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;

/**
 * MailService??��?��??�????.
 * 
 */
public class DefaultMailService extends MailService implements InitializingBean, DisposableBean {

	private static final int POOL_SIZE = 4;

	private final ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);

	private String from;

	protected JavaMailSender mailSender;

	/**
	 * Method description
	 * 
	 * @param mailSender
	 */
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	/**
	 * ??????SimpleMailMessage.
	 * 
	 * @param msg
	 */
	@Override
	public void send(SimpleMailMessage msg) {

		try {
			mailSender.send(msg);
		} catch (MailException e) {
			log.error(e.getMessage(), e);
		}
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
	@Override
	@SuppressWarnings("unchecked")
	public void send(SimpleMailMessage msg, String templateName, Map model) {

		MimeMessage mimeMsg = generateMimeMsg(msg, templateName, model);

		mailSender.send(mimeMsg);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void asyncSend(SimpleMailMessage msg, String templateName, Map model) {

		final MimeMessage mimeMsg = generateMimeMsg(msg, templateName, model);

		pool.execute(new Runnable() {

			// 两�?��?????�???��????��??
			private long sleeping = 0;

			// ???�?次�??
			private Integer retries = 3;

			private Integer failures = 0;

			private boolean done = false;

			public void run() {

				while (!done && (failures < retries)) {
					try {
						mailSender.send(mimeMsg);

						done = true;

					} catch (Exception e) {
						failures++;

						log.error("asyncSend error. exception {},mimeMsg {}", e, mimeMsg);

						log.error("asyncSend error.fail {} times. ", failures);

						if (sleeping > 0) {
							log.error("asyncSend error. wait {} ms try again. ", sleeping);

							try {
								Thread.sleep(sleeping);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
					}
				}

			}
		});

	}

	@SuppressWarnings("unchecked")
	private MimeMessage generateMimeMsg(SimpleMailMessage msg, String templateName, Map model) {

		// ??????html???件�??�?
		String content = generateEmailContent(templateName, model);
		MimeMessage mimeMsg = null;

		try {
			mimeMsg = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, true, "utf-8");

			helper.setTo(msg.getTo());
			helper.setSubject(msg.getSubject());
			helper.setFrom(from);
			helper.setText(content, true);
		} catch (MessagingException ex) {
			log.error(ex.getMessage(), ex);
		}

		return mimeMsg;
	}

	/**
	 * Method description
	 * 
	 * @throws Exception
	 */
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(mailSender, "???注�??MailSender");
	}

	/**
	 * Method description
	 * 
	 * @param from
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	public void destroy() throws Exception {

		pool.shutdown(); // Disable new tasks from being submitted

		try {

			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks

				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
					log.error("Pool did not terminate");
				}
			}
		} catch (InterruptedException ie) {

			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();

			// Preserve interrupt status
			// Thread.currentThread().interrupt();
		}

	}

}
