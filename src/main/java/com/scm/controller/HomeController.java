package com.scm.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.User;
import com.scm.helper.Message;
import com.scm.repo.UserRepo;

@Controller
public class HomeController 
{
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@RequestMapping("/")
	public String home(Model model)
	{
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model)
	{
		model.addAttribute("title","About - Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signUp")
	public String signUp(Model model)
	{ 
		model.addAttribute("title","Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signUp";
	}
	
	//handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model)
	{ 
		model.addAttribute("title","Login - Smart Contact Manager");
		return "login";
	}
	//Handler for Registering User
	@RequestMapping(value="/do_register",method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1,@RequestParam(value="agreement",defaultValue="false")
								boolean agreement,Model model, HttpSession session)
	{
		try {
			if(!agreement)
			{
				System.out.println("You are not agreed terms and conditions..!");
				throw new Exception("You are not agreed terms and conditions..!");
			}
			if(result1.hasErrors())
			{
				System.out.println("Error:" + result1.toString());
				model.addAttribute("user", user);
				return "signUp";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));//mahi@123
			
			System.out.println("Agreement:" + agreement);
			System.out.println("User:" + user);
			
			User result = this.userRepo.save(user);//user saved
			
			model.addAttribute("user",new User());
			session.setAttribute("message", new Message("User Registered Successfully...!!","alert-success"));
			return "signUp";
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something Went Wrong !!" + e.getMessage(),"alert-danger"));
			return "signUp";
		}
		
	}
	
}
