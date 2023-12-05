package com.scm.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.User;
import com.scm.repo.UserRepo;
import com.scm.service.EmailService;

@Controller

public class ForgotController 
{
	Random random = new Random(1000);
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@RequestMapping(value="/forgot")
	public String openEmailForm()
	{
		return "forgot_email_form";
	}
	
	@PostMapping(value="/send-otp")
	public String sendOtp(@RequestParam("email")String email,HttpSession session)
	{
		System.out.println("Email Id: " + email);
		//Generating OTP of 6 Digit	
		int otp = random.nextInt(999999);
		System.out.println("Random OTP:" + otp);
		//write code for send OTP to email
		String subject = "OTP From Smart Contact Mananger";
		//String message = "Dear customer, use this One Time Password " + otp + " For Verify your Email Account. This OTP will be valid for the next 5 mins.";
		String message = " " + "<div style='border:2px solid black;padding:40px;'>" 
		+ "Dear Customer, use this One Time Password " + "<h3>"  + otp + "</h3>" +" For Verify your Email Account. This OTP will be valid for the next 5 mins."+ "</div>";
		String to = email;
		boolean flag = this.emailService.sendMail(subject,message,to);
		if(flag)
		{
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
			
		}
		else
		{
			session.setAttribute("message","Check your Email Id..!!");
			return "forgot_email_form";
		}
	}
	//verify otp controller
	@PostMapping(value="/verify-otp")
	public String verifyOtp(@RequestParam("otp")int otp,HttpSession session)
	{
		int myOtp = (int) session.getAttribute("myotp");
		String email = (String)session.getAttribute("email");
		if(myOtp == otp)
		{ 
			//sbkuch sahi hai
			//password change form
			//fetch user by email
			User user = this.userRepo.getUserByUserName(email);
			if(user==null)
			{
				//if user is null then send error message
				session.setAttribute("message", "User Doesn't Exist With This Email Address..!!");
				return "forgot_email_form";
			}
			else
			{
				// if user in not null then send to change password form
				
			}
			return "password_change_form";
		}
		else
		{
			//kuch bhi sahi nahi hai
			session.setAttribute("message", "You Have Entered Wrong OTP..!");
			return "verify_otp";
		}
	}
	
	//Change Password Handler
	@PostMapping(value="/change-password")
	public String changePassword(@RequestParam("newPassword")String newPassword,HttpSession session)
	{
		String email = (String)session.getAttribute("email");
		User user = this.userRepo.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		this.userRepo.save(user);
		return "redirect:/signin?change=Password Changed Successfully..!!";
	}
}
