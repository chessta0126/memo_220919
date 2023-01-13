package com.memo.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/user")
@Controller
public class UserController {

	/**
	 * 회원가입 화면
	 * @return
	 */
	
	// http://localhost:8080/user/sign_up_view
	@GetMapping("/sign_up_view")
	public String signUpView(Model model) {
		model.addAttribute("viewName","user/signUp");
		return "template/layout";
	}
	
	@PostMapping("/sign_up")
	public String signUp(Model model) {
		model.addAttribute("viewName","user/signUp");
		return "template/layout";
	}
	
	// http://localhost:8080/user/sign_up_view
	@GetMapping("/sign_in_view")
	public String signInView(Model model) {
		model.addAttribute("viewName","user/signIn");
		return "template/layout";
	}
		
	@PostMapping("/sign_in")
	public String signIn(Model model) {
		model.addAttribute("viewName","user/signIn");
		return "template/layout";
	}
}
