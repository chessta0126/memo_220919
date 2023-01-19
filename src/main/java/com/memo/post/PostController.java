package com.memo.post;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.memo.post.bo.PostBO;
import com.memo.post.model.Post;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/post")
@Controller
public class PostController {

	@Autowired
	private PostBO postBO;
	
	// 글 목록 화면
	// http://localhost:8080/post/post_list_view
	@GetMapping("/post_list_view")
	public String postListView(Model model, HttpSession session) {
		
		// int로 하면 로그인 안 되어 있을 시 에러, Integer은 로그인 안 해도 볼 수 있다.
		Integer userId = (Integer)session.getAttribute("userId");
		if(userId == null) {
			return "redirect:/user/sign_in_view";
		}
		
		List<Post> postList = postBO.getPostListByUserId(userId);
		model.addAttribute("postList",postList);
		model.addAttribute("viewName","post/postList");
		
		return "template/layout";
	}
	
	/**
	 * 글쓰기 화면
	 * @param model
	 * @return
	 */
	// http://localhost:8080/post/post_create_view
	@GetMapping("/post_create_view")
	public String postCreateView(Model model) {
		model.addAttribute("viewName", "post/postCreate");

		return "template/layout";
	}

	// http://localhost:8080/post/post_detail_view
	@GetMapping("/post_detail_view")
	public String postDetailView(
			@RequestParam("postId") int postId
			,HttpSession session
			,Model model
			) {

		Integer userId = (Integer)session.getAttribute("userId");
		if(userId == null) {
			return "redirect:/user/sign_in_view";
		}
		
		// DB select by - userId, postId
		Post post = postBO.getPostByPostIdUserId(postId, userId);
		model.addAttribute("post",post);
		model.addAttribute("viewName","post/postDetail");
		
		return "template/layout";
	}
}
