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
	public String postListView(
			@RequestParam(value="prevId", required=false) Integer prevIdParam
			,@RequestParam(value="nextId", required=false) Integer nextIdParam
			,Model model, HttpSession session) {
		
		// int로 하면 로그인 안 되어 있을 시 에러, Integer은 로그인 안 해도 볼 수 있다.
		Integer userId = (Integer)session.getAttribute("userId");
		if(userId == null) {
			return "redirect:/user/sign_in_view";
		}
		
		int prevId = 0;
		int nextId = 0;
		List<Post> postList = postBO.getPostListByUserId(userId, prevIdParam, nextIdParam);
		if(postList.isEmpty() == false) { // postList가 비어있을 때 에러 방지
			prevId = postList.get(0).getId(); // 가져온 리스트 중 가장 앞쪽(큰 id)
			nextId = postList.get(postList.size()-1).getId(); // 가져온 리스트 중 가장 뒤쪽(작은 id)
			
			// 이전 방향의 끝인가? postList.get(0) == 가장 큰 postId
			if(postBO.isPrevLastPage(prevId, userId)) { // 첫 페이지일 때
				prevId = 0;
			}
			
			// 다음 방향의 끝인가? nextId == 가장 작은 postId
			if(postBO.isNextLastPage(nextId, userId)) { // 마지막 페이지일 때
				nextId = 0;
			}
		}
		model.addAttribute("prevId",prevId);
		model.addAttribute("nextId",nextId);
		
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
