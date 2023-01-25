package com.memo.post.bo;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.common.FileManagerService;
import com.memo.post.dao.PostDAO;
import com.memo.post.model.Post;

@Service
public class PostBO {

	// private Logger logger = LoggerFactory.getLogger(postBO.class);
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PostDAO postDAO;

	@Autowired
	private FileManagerService fileManagerService;
	
	// 글 추가
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
	
	// 글 수정
	public void updatePost(int userId, String userLoginId
			, int postId, String subject, String content, MultipartFile file) {
		// 기존 글을 가져온다. (이미지가 교체될 때 기존 이미지 제거를 위해)
		Post post = getPostByPostIdUserId(postId, userId);
		if(post == null) { // 글이 없을 때
			logger.warn("[update post] 수정할 메모가 존재하지 않습니다. postId:{},userId:{}", postId, userId);
			return;
		}
		
		// MultipartFile이 있다면 업로드 후 imagePath -> 업로드가 성공하면 기존 이미지 제거
		String imagePath = null;
		if(file != null) {
			// 업로드
			imagePath = fileManagerService.saveFile(userLoginId, file);
			
			// 업로드 성공 -> 기존 이미지 제거 (업로드가 실패할 수 있으므로, 성공한 후에 제거하는 것)
			if(imagePath != null && post.getImagePath() != null) {
				// 이미지 제거
				fileManagerService.deleteFile(post.getImagePath()); // 낚여서 imagePath 넣으면 안 됨(방금 만든 그거 지워지는 거임)
			}
		}
		
		// DB Update
		postDAO.updatePostByPostIdUserId(postId, userId, subject, content, imagePath);
	}
	
	public List<Post> getPostListByUserId(int userId){
		return postDAO.selectPostListByUserId(userId);
	}
	
	public Post getPostByPostIdUserId(
			@Param("postId") int postId
			,@Param("userId") int userId){
		return postDAO.selectPostByPostIdUserId(postId,userId);
	}
}
