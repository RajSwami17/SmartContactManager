package com.scm.service;


import javax.mail.*;
import java.util.Properties;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService 
{
	public boolean sendMail(String subject,String message,String to)
	{
		boolean f = false;
		String from = "swamrajesh1708@gmail.com";
		
		//variable for Mail
		String host = "smtp.gmail.com";
		
		// Get the system properties
		Properties properties = new Properties();
		System.out.println("Properties: " + properties);
		//Setting important information to properties object
		
		//host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		
		//Step 1 : To get the Session Object
		Session session1 = Session.getInstance(properties, new Authenticator()
		{
			@Override
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication("swamrajesh1708@gmail.com","mfcgjazfxkbsgupm");
			}
		});
		
		session1.setDebug(true);
		//Step 2 : Compose the message [text,multimedia]
		MimeMessage m = new MimeMessage(session1);
		try
		{
			//from email
			m.setFrom(from);
			//adding recipient to message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			//adding subject to message
			m.setSubject(subject);
			//adding text to message
			//m.setText(message);
			m.setContent(message,"text/html");
			//send
			//Step 3: send the message using transport class
			Transport.send(m);
			System.out.println("Send Success.....!!");
			f = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return f; 
	}
}
