package com.memo.post.bo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.common.FileManagerService;
import com.memo.post.dao.PostDAO;
import com.memo.post.model.Post;

@Service
public class PostBO {

	@Autowired
	private PostDAO postDAO;

	@Autowired
	private FileManagerService fileManagerService;
	
	public int addPost(int userId, String userLoginId
			, String subject, String content, MultipartFile file
			) {
		// 파일 업로드 => 경로
		String imagePath = null;
		if(file != null) {
			imagePath = fileManagerService.saveFile(userLoginId, file);
		}
		
		// DAO insert
		return postDAO.insertPost(userId, subject, content, imagePath);
	}
	
	public List<Post> getPostListByUserId(int userId){
		return postDAO.selectPostListByUserId(userId);
	}
}