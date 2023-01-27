package com.memo.post.bo;

import java.util.Collections;
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
import com.mysql.cj.x.protobuf.MysqlxCrud.Collection;

@Service
public class PostBO {

	// private Logger logger = LoggerFactory.getLogger(postBO.class);
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final int POST_MAX_SIZE = 3;
	
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
	
	// 글 삭제
	public int deletePostByPostIdUserId(int postId,int userId){
		// 기존 글 가져오기
		Post post = getPostByPostIdUserId(postId, userId);
		if(post == null) {
			logger.warn("[글 삭제] post is null. postId:{}, userId:{}", postId, userId);
			return 0;
		}

		// 업로드 되었던 이미지가 있으면 파일 삭제
		if(post.getImagePath() != null) {
			fileManagerService.deleteFile(post.getImagePath());
		}
		
		// DB delete
		return postDAO.deletePostByPostIdUserId(postId, userId);
	}
	
	public List<Post> getPostListByUserId(int userId, Integer prevId, Integer nextId){
		// 게시글 번호 : 10 9 8 | 7 6 5 | 4 3 2 | 1
		// 만약 4 3 2 페이지에 있을 때
		// 1) <<이전 클릭: 4보다 큰 3개 id 기준 ASC (5 6 7) 가져옴 -> List reverse(7 6 5)
		// 2) 다음>> 클릭: 2보다 작은 3개 id 기준 DESC
		// 3) 첫 페이지(이전, 다음 없음) : DESC 3개
		String direction = null; // 방향
		Integer standardId = null; // 기준 postId
		if(prevId != null) { // 이전
			direction = "prev";
			standardId =  prevId;

			List<Post> postList = postDAO.selectPostListByUserId(userId, direction, standardId, POST_MAX_SIZE);
			Collections.reverse(postList);
			return postList;
		} else if(nextId != null) { // 다음
			direction = "next";
			standardId =  nextId;
		}
		
		// 첫 페이지일 때, 페이징 안함(standardId, direction = null)
		// 다음일 때 standardId, direction이 채워져서 넘어감
		return postDAO.selectPostListByUserId(userId, direction, standardId, POST_MAX_SIZE);
	}
	
	// prevId는 BO가 쓸라고 받아온 거임(DAO한테 보낼 필요 x)
	public boolean isPrevLastPage(int prevId, int userId) {
		int maxPostId = postDAO.selectPostIdByUserIdSort(userId, "DESC");
		return maxPostId == prevId? true : false;
	}

	public boolean isNextLastPage(int nextId, int userId) {
		int minPostId = postDAO.selectPostIdByUserIdSort(userId, "ASC");
		return minPostId == nextId? true : false;
	}
	
	public Post getPostByPostIdUserId(
			@Param("postId") int postId
			,@Param("userId") int userId){
		return postDAO.selectPostByPostIdUserId(postId,userId);
	}
}
