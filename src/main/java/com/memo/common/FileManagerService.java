package com.memo.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component // 일반적인 Spring Bean. 로직은 들어가는데, Controller, BO, DAO에 속하지 않을 때
public class FileManagerService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// 실제 이미지가 저장될 경로(서버)
	public static final String FILE_UPLOAD_PATH = "D:\\chessta\\6_Spring Project\\memo\\workspace\\images/";
	
	// input : MultipartFile, userLoginId
	// output : imagePath
	public String saveFile(String userLoginId, MultipartFile file) {
		// 파일 디렉토리 (ex) 지정한 id_업로드 시간을 유일한 값으로 담는다/이미지파일명.확장자)
		// ex) aaaa_16205468768/sun.png
		String directoryName = userLoginId + "_" + System.currentTimeMillis() + "/";
		String filePath = FILE_UPLOAD_PATH + directoryName;

		File directory = new File(filePath);
		directory.mkdir(); // 폴더 만들기 성공여부
		if(directory.mkdir() == false) { // 폴더 만들기 실패
			return null;
		}

		// 파일 업로드 : byte 단위로 업로드 된다.
		try {
			byte[] bytes = file.getBytes();
			// 사용자가 올린 이름 그대로 올라가는데, 한글 안 올라간다. -> 영어로 번역하는 로직 추가해야 함
			Path path = Paths.get(filePath + file.getOriginalFilename());
			Files.write(path, bytes);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		// 파일 업로드 성공했으면, 이미지 url path를 return 한다.
		// http://localhost:8080/images/aaaa_16205468768/sun.png
		
		return "/images/" + directoryName + file.getOriginalFilename();
	}
	
	
	// imagePath : /images/aaaa_16205468768/sun.png
	public void deleteFile(String imagePath) {
		// 겹치는 경로인 "/images" 글자 제거(replace)
		Path path = Paths.get(FILE_UPLOAD_PATH + imagePath.replace("/images", ""));
		if(Files.exists(path)) {
			// 이미지 삭제
			try {
				Files.delete(path);
			} catch (IOException e) {
				logger.error("[이미지 삭제] 이미지 삭제 실패. imagePath:{}", imagePath);
			}
		}
			
		// 디렉토리(폴더) 삭제
		path = path.getParent();
		if(Files.exists(path)) {
			// 이미지 삭제
			try {
				Files.delete(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("[이미지 삭제] 디렉토리 삭제 실패. imagePath:{}", imagePath);
			}
		}
	}
}
