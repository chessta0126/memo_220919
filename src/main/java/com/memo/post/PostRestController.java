package com.memo.post;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
}
