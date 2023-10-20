package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.dto.UserDto;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.service.UserService;

import jakarta.validation.Valid;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Controller
public class AuthController {

    private UserService userService;
    
    
    

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("index")
    public String home(){
        return "uploader";
    }

    @GetMapping({"/","/login"})
    public String loginForm() {    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();    	
    	System.out.println("==========Auth in Login link "+auth);
    	if(auth instanceof AnonymousAuthenticationToken) {
    		return "login";
    	}
    	
    	return "redirect:/users";
        
    	
    }

    // handler method to handle user registration request
    @GetMapping("register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }
    
    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model){
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/register?success";
    }

    @GetMapping("/users")
    public String listRegisteredUsers(Model model){
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	
    	System.out.println("==========Auth in Users "+auth);
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }
 // handler method to handle user registration request
    @GetMapping("/upload")
    public String Upload(Model model){
        //UserDto user = new UserDto();
        //model.addAttribute("upload", user);
        return "upload";
    }
    
    
    @GetMapping("/downloadMergedFile")
    public ResponseEntity<byte[]> downloadMergedFile() throws IOException {
    	System.out.println("=============== ");
        String uploadDir = "C:\\Users\\DINESH KUMAR\\Desktop\\vishh";
        String mergedFileName = "mergedFile.csv"; // Name of the merged CSV file

        File file = new File(uploadDir, mergedFileName);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        byte[] fileContent = Files.readAllBytes(file.toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData(mergedFileName, mergedFileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileContent.length)
                .body(fileContent);
    }
    

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("files") MultipartFile[] files) {
        String uploadDir = "C:\\Users\\DINESH KUMAR\\Desktop\\vishh";
        
        // Create a merged file to append the content of each uploaded file
        File mergedFile = new File(uploadDir, "mergedFile.csv");
        
        for (MultipartFile multipartFile : files) {
            try {
                File file = new File(uploadDir, multipartFile.getOriginalFilename());
                multipartFile.transferTo(file);
                
                // Read the content of the uploaded file
                String fileContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                
                // Append the content to the merged file
                Files.write(mergedFile.toPath(), fileContent.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading or reading the file.");
            }
        }
        
        return ResponseEntity.ok("Files uploaded and merged successfully.");
    
  
    	
		/*
		 * if (file.isEmpty()) { return
		 * ResponseEntity.badRequest().body("Please select a file to upload."); }
		 */
		

		/*
		 * try { String fileName = file.getOriginalFilename(); // Define the directory
		 * where you want to save the uploaded files String uploadDir =
		 * "C:\\Users\\DINESH KUMAR\\Desktop\\vishh"; File uploadPath = new
		 * File(uploadDir);
		 * 
		 * if (!uploadPath.exists()) { uploadPath.mkdirs(); // Create the directory if
		 * it doesn't exist }
		 * 
		 * File destFile = new File(uploadPath, fileName); file.transferTo(destFile);
		 *return ResponseEntity.ok("File uploaded successfully."); } catch (Exception
		 * e) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
		 * body("Error uploading the file."); }
		 */
    }

}                    