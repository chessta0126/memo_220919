package com.memo.post;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/post")
@Controller
public class PostController {

	// 글 목록 화면
	// http://localhost:8080/post/post_list_view
	@GetMapping("/post_list_view")
	public String postListView(Model model) {
		model.addAttribute("viewName","post/postList");
		
		return "template/layout";
	}
	
	// http://localhost:8080/post/post_create_view
	@GetMapping("/post_create_view")
	public String postCreateView(Model model) {
		model.addAttribute("viewName", "post/postCreate");

		return "template/layout";
	}
}
