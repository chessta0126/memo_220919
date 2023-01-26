package com.memo.post;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.memo.post.bo.PostBO;

import jakarta.servlet.http.HttpSession;

@RequestMapping("/post")
@RestController
public class PostRestController {

	@Autowired
	private PostBO postBO;
	
	/**
	 * 글쓰기 API
	 * @param subject
	 * @param content
	 * @param file
	 * @param session
	 * @return
	 */
	@PostMapping("/create")
	public Map<String,Object> create(
			@RequestParam("subject") String subject
			,@RequestParam(value="content", required=false) String content
			,@RequestParam(value="file", required=false) MultipartFile file
			,HttpSession session
			){
		// 원래 들어왔던 자료형으로 Casting
		// 로그인 안 된 사람은 여기로 절대 못 들어옴 (int로 하면 로그인 안 할 시 에러)
		int userId = (int)session.getAttribute("userId");
		String userLoginId = (String)session.getAttribute("userLoginId");
		
		// DB insert
		int rowCount = postBO.addPost(userId, userLoginId, subject, content, file);
		
		Map<String,Object> result = new HashMap<>();
		if(rowCount > 0) {
			result.put("code", 1);
			result.put("result", "성공");
		} else {
			result.put("code", 500);
			result.put("result", "메모 저장에 실패했습니다. 관리자에게 문의해주세요");
		}
		
		return result;
	}
	
	/**
	 * 글 수정 API
	 * @param postId
	 * @param subject
	 * @param content
	 * @param file
	 * @param session
	 * @return
	 */
	@PutMapping("/update")
	public Map<String,Object> update(
			@RequestParam("postId") int postId
			,@RequestParam("subject") String subject
			,@RequestParam(value="content", required=false) String content
			,@RequestParam(value="file", required=false) MultipartFile file
			,HttpSession session
			){
		int userId = (int)session.getAttribute("userId");
		String userLoginId = (String)session.getAttribute("userLoginId");
		
		// Update DB
		postBO.updatePost(userId, userLoginId, postId, subject, content, file);
		
		Map<String,Object> result = new HashMap<>();
		result.put("code",1);
		result.put("result","성공");

		return result;
	}
	
	@DeleteMapping("/delete")
	public Map<String,Object> delete(
			@RequestParam("postId") int postId
			,HttpSession session
			){
		
		// int -> 로그인 된 사람만 들어올 수 있음(아니면 에러 발생)
		int userId = (int)session.getAttribute("userId");
		
		// int rowCount = postBO
		int rowCount = 1;
		Map<String,Object> result = new HashMap<>();
		if(rowCount > 0) {
			result.put("code", 1);
			result.put("result", "성공");
		} else {
			result.put("code", 500);
			result.put("errorMessage", "메모 삭제에 실패했습니다. 관리자에게 문의해주세요");
		}
		
		return result;
	}
}
