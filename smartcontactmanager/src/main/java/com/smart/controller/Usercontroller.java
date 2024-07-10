package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.rep.ContactRepo;
import com.smart.rep.UserRepo;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class Usercontroller {
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ContactRepo contactRepo; 
	// methid for adding comon data
	@ModelAttribute
	public void addCommondata(Model m ,Principal principal) {
		String userName = principal.getName();
		User user = this.userRepo.getUserByUserName(userName);
		m.addAttribute("user",user);
	}
	
    @RequestMapping("/index")
	public String dashboard(Model m,Principal principal) {
    	m.addAttribute("title","User_Dashboard");
		return "normal/user_dashboard";
	}
    
    @GetMapping("/add_contact")
    public String openAddcontact(Model m) {
    	 m.addAttribute("title","AddContact");
    	 m.addAttribute("contact",new Contact());;
    	return "normal/add_contact";
    }
    
//    @PostMapping("/process-contact")
//    public  String processContact(@ModelAttribute Contact contact) {
//    	
//    	System.out.println(contact);
//    	return "normal/add_contact";
//    }
    
    
    @PostMapping("/process-contact")
        public String addContactHandler(@Valid @ModelAttribute Contact contact,
        		@RequestParam("profileimage") MultipartFile file ,BindingResult bindingResult,
        		Principal principal ,HttpSession session){
        
    	try {     
    	String name = principal.getName();
    	User user=userRepo.getUserByUserName(name);
    	
    	// proceing an duploading file
    	
    	if(file.isEmpty()) {
    		System.out.println("image not found");
    		contact.setImage("contact.jpg");
    	}else {
    		contact.setImage(file.getOriginalFilename());
    		
    		File fl =new ClassPathResource("static/image").getFile();
    		
    		Path path=Paths.get(fl.getAbsolutePath()+File.separator + file.getOriginalFilename());
    		
    		Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
    	}
    	contact.setUser(user);
    	
    	user.getContacts().add(contact);
    	
    	userRepo.save(user);
           System.out.println(user);
           
           session.setAttribute("message",new Message("Your conatct is Added !! Add more..","success")); 
           
        }catch(Exception e) {
        	System.out.println(e.getMessage());
        	e.printStackTrace();
        	session.setAttribute("message",new Message("Error occured while adding contact","danger"));
        }
        return "normal/add_contact";
        }
    
    
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, 
    		Model m, Principal p) {
    	m.addAttribute("title","View_contact");
    	String username = p.getName();
    	
    	Pageable pg =PageRequest.of(page, 5);
    	User us = userRepo.getUserByUserName(username);
    	int id =us.getId();
    	
    	Page<Contact> contacts = this.contactRepo.findContactbyId(id,pg);
    	
    	m.addAttribute("contacts",contacts);
    	m.addAttribute("currentPage",page);
    	m.addAttribute("totalPages",contacts.getTotalPages());
    	return "normal/show_contacts";
    }
    
    @RequestMapping("/{cId}/contact")
    public String showContactDetails(@PathVariable("cId") Integer cId, Model m, Principal principal) {
        System.out.println(cId);
        
        Optional<Contact> contactOpt = this.contactRepo.findById(cId);
        
        if (contactOpt.isPresent()) {
            Contact contact = contactOpt.get();
            String userName = principal.getName();
            User user = this.userRepo.getUserByUserName(userName);
            
            if (user.getId() == contact.getUser().getId()) {
                m.addAttribute("contact", contact);
            } else {
                // Handle unauthorized access attempt
                m.addAttribute("errorMessage", "You are not authorized to view this contact.");
            }
        } else {
            // Handle contact not found
            m.addAttribute("errorMessage", "Contact not found.");
        }
        
        return "normal/contact_detail";
    }
    
     @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cId,Model m,HttpSession session) {
    	Contact c=this.contactRepo.findById(cId).get(); 
    	this.contactRepo.delete(c);
    	session.setAttribute("message", new Message("Contat deleted successfully","success"));
    	return"redirect:/user/show-contacts/0";
    }
    @PostMapping("/update-contact/{cId}")
     public String updateForm(@PathVariable("cId") Integer cid, Model m)
     {
    	m.addAttribute("title","Update_Contact");
    	Contact c = this.contactRepo.findById(cid).get();
    	m.addAttribute("contact", c);
    	 return "normal/update_form";
     }
    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileimage") MultipartFile file, Model m, HttpSession session, Principal principal) {
        try {
            // Retrieve the existing contact from the database
            Contact existingContact = this.contactRepo.findById(contact.getcId()).orElseThrow(() -> new Exception("Contact not found"));

            // Update the existing contact's details
            existingContact.setName(contact.getName());
            existingContact.setSecondName(contact.getSecondName());
            existingContact.setPhone(contact.getPhone());
            existingContact.setEmail(contact.getEmail());
            existingContact.setWork(contact.getWork());
            existingContact.setDescription(contact.getDescription());

            // Process and upload the new profile image if it is provided
            if (!file.isEmpty()) {
                // Delete the old image if it exists
                File deleteFile = new ClassPathResource("static/image").getFile();
                File oldFile = new File(deleteFile, existingContact.getImage());
                if (oldFile.exists()) {
                    oldFile.delete();
                }

                // Upload the new image
                String newFileName = file.getOriginalFilename();
                Path path = Paths.get(deleteFile.getAbsolutePath() + File.separator + newFileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                existingContact.setImage(newFileName);
            }

            // Update the user information
            User user = this.userRepo.getUserByUserName(principal.getName());
            existingContact.setUser(user);

            // Save the updated contact
            this.contactRepo.save(existingContact);

            // Add success message to the session
            session.setAttribute("message", new Message("Contact updated successfully!", "success"));
        } catch (Exception e) {
            e.printStackTrace();
            // Add error message to the session
            session.setAttribute("message", new Message("Error updating contact", "danger"));
        }
        return "redirect:/user/show-contacts/0";
    }

    @GetMapping("/profile")
    public String showProfile(Model m, Principal principal) {
        String username = principal.getName();
        User user = userRepo.getUserByUserName(username);
        m.addAttribute("title", "User Profile");
        m.addAttribute("user", user);
        return "normal/profile";
    }

}
