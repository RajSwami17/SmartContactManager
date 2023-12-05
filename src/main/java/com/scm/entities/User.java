package com.scm.entities;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="User_Details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User 
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="User_Id")
	private int id;
	
	@Column(name="User_Name")
	@NotBlank(message="Name Field is required")
	@Size(min=2,max=20,message="Min 2 and Max 20 characters are allowed")
	private String name;
	
	@Column(name="User_Email",unique = true)
	private String email;
	
	@Column(name="User_Password")
	private String password;
	
	@Column(name="User_Role")
	private String role;
	
	@Column(name="User_Status")
	private boolean enabled;
	
	@Column(name="User_ImageLink")
	private String imageUrl;
	
	@Column(name="User_About",length=500)
	private String about;
	
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy="user",orphanRemoval=true)
	private List<Contact> contacts = new ArrayList<>();
	
	
	
}
