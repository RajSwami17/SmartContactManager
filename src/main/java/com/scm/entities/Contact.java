package com.scm.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="User_Contact")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contact 
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="Contact_Id")
	private int cid;
	
	@Column(name="Contact_First_Name")
	private String firstName;
	
	@Column(name="Contact_Last_Name")
	private String lastName;
	
	@Column(name="Work_Details")
	private String work;
	
	@Column(name="Contact_Email")
	private String email;
	
	@Column(name="Contact_Phone")
	private String phone;
	
	@Column(name="Contact_Image")
	private String image;
	
	@Column(name="Contact_Description",length=5000)
	private String description;
	
	@ManyToOne
	@JsonIgnore //this field not serialized
	private User user;
	
	@Override
	public boolean equals(Object obj)
	{
		return this.cid==((Contact)obj).getCid();
	}
}
