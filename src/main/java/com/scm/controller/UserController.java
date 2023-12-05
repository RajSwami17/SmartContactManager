package com.scm.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helper.Message;
import com.scm.repo.ContactRepo;
import com.scm.repo.UserRepo;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ContactRepo contactRepo;

	// logic for common data which is useful for all the methods
	@ModelAttribute
	public void addCommonData(Model m, Principal p) {
		String userName = p.getName();
		System.out.println("UserName: " + userName);
		// get the use using getUserByUserName()
		User user = userRepo.getUserByUserName(userName);
		System.out.println("User: " + user);
		m.addAttribute("user", user);
	}

	@RequestMapping(value = "/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard-Smart Contact Manager");
		return "normal/user_dashboard";
	}

	// open add from handler
	@GetMapping("/add-contact")
	public String oepnAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact-Smart Contact Manager");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/save-contact")
	public String saveContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepo.getUserByUserName(name);
			// processing and uploading file
			if (file.isEmpty()) {
				// if file is empty then try our message
				System.out.println("File is Empty");
				contact.setImage("contact.png");
			} else {
				// file the file to folder and update the name to contact
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("File is Uploaded");
			}
			contact.setUser(user);

			user.getContacts().add(contact);
			this.userRepo.save(user);
			System.out.println("Data:" + contact);
			System.out.println("Contact Data added to Database..!");
			// Success Message
			session.setAttribute("message", new Message("Contact Saved Successfully..!!", "success"));
		} catch (Exception e) {
			System.out.println("Error:" + e.getMessage());
			e.printStackTrace();
			// Error message
			session.setAttribute("message", new Message("Something Went Wrong? Try Again..!!", "danger"));

		}
		return "normal/add_contact_form";
	}

	// View All Contacts Of User Handler
	// Per page 5[n]
	// current page = 0 page
	@GetMapping("/view-contacts/{page}")
	public String viewContact(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "View Contact-Smart Contact Manager");
		String name = principal.getName();
		User user = userRepo.getUserByUserName(name);
		Pageable pageable = PageRequest.of(page, 4);
		Page<Contact> userContacts = this.contactRepo.findContactsByUser(user.getId(), pageable);
		m.addAttribute("contacts", userContacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", userContacts.getTotalPages());
		return "normal/show_contacts";
	}

	// show particular contact details
	@GetMapping("/contact/{cid}")
	public String showContactDetails(@PathVariable("cid") Integer cid, Model model, Principal principal) {
		System.out.println("Contact Id:" + cid);
		Optional<Contact> contactId = this.contactRepo.findById(cid);
		Contact contact = contactId.get();
		// contact specific to specific user
		String name = principal.getName();
		User user = userRepo.getUserByUserName(name);
		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}
		return "normal/contact_details";
	}

	// Delete particular contact details
	@GetMapping("/delete-contact/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model model, Principal principal,
			HttpSession session) {
		Optional<Contact> contactById = this.contactRepo.findById(cid);
		Contact contactId = contactById.get();
		System.out.println("Contact:" + contactId.getCid());
		// contactId.setUser(null);
		// this.contactRepo.delete(contactId);
		User user = this.userRepo.getUserByUserName(principal.getName());
		user.getContacts().remove(contactId);
		this.userRepo.save(user);
		session.setAttribute("message", new Message("Contact Deleted Successfully..!", "success"));
		return "redirect:/user/view-contacts/0";
	}

	// Open Data For Update particular contact details
	@PostMapping("/update-contact/{cid}")
	public String updateContact(@PathVariable("cid") Integer cid, Model model) {
		model.addAttribute("title", "Update Contact-Smart Contact Manager");
		Contact contact = this.contactRepo.findById(cid).get();
		model.addAttribute("contact", contact);
		return "normal/update_contact";
	}

	// Save Updated Contact Details
	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updateContactData(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession session, Principal principal) {
		try {
			// old contact details
			Contact oldContactDetail = this.contactRepo.findById(contact.getCid()).get();
			if (!file.isEmpty()) {
				// file work
				// old file overwrite by new file
				// delete old photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContactDetail.getImage());
				file1.delete();
				// update new photo
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());

			} else {
				contact.setImage(oldContactDetail.getImage());
			}
			User user = this.userRepo.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepo.save(contact);
			session.setAttribute("message", new Message("Contact Details Updated Successfully", "success"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Contact FirstName :" + contact.getFirstName());
		System.out.println("Contact Id :" + contact.getCid());
		return "redirect:/user/contact/" + contact.getCid();
	}

	// profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "User Profile-Smart Contact Manager");
		return "normal/profile";
	}
	//Open Settings Handler
	@GetMapping("/settings")
	public String openSettings(Model model)
	{
		model.addAttribute("title","Forgot Password - Smart Contact Manager");
		return "/normal/settings";
	}
	// forgot/change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword")String oldPassword,
			@RequestParam("newPassword")String newPassword,Principal principal,HttpSession session)
	{
		System.out.println("Old Password: " + oldPassword);
		System.out.println("New Password: " + newPassword);
		String userName = principal.getName();
		User currentUser = this.userRepo.getUserByUserName(userName);
		System.out.println(currentUser.getPassword());
		if(this.bcryptPasswordEncoder.matches(oldPassword, currentUser.getPassword()))
		{
			//if old password and user password matches then change password
			currentUser.setPassword(this.bcryptPasswordEncoder.encode(newPassword));
			this.userRepo.save(currentUser);
			session.setAttribute("message", new Message("Your Password Changed Successfully..!!", "success"));
		}
		else
		{
			//if old password and user password not matches then throw error
			session.setAttribute("message", new Message("Password Does not Match", "danger"));
			return "redirect:/user/settings";
		}
		return "redirect:/user/index";
	}

}
