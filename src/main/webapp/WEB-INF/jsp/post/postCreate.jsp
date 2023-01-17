<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div class="d-flex justify-content-center">
	<div class="w-50">
		<h1>글쓰기</h1>
		<input type="text" id="subject" class="form-control" placeholder="제목을 입력하세요">
		<textarea rows="15" id="content" class="form-control" placeholder="내용을 입력하세요"></textarea>
		<div class="d-flex justify-content-end my-4">
			<input type="file" id="file" accept=".jpg,.jpeg,.png,.gif">
		</div>
		<div class="d-flex justify-content-between">
			<button type="button" id="postListBtn" class="btn btn-dark">목록</button>
			<div>
				<button type="button" id="clearBtn" class="btn btn-secondary">모두 지우기</button>
				<button type="button" id="postCreateBtn" class="btn btn-info">저장</button>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function(){
		// 목록 버튼 클릭 => 글 목록
		$('#postListBtn').on('click',function(){
			location.href = "/post/post_list_view";
		});
		
		// 모두 지우기 눌렀을 때
		$('#clearBtn').on('click',function(){
			$('#subject').val("");
			$('#content').val("");
		});
		
		// 글 저장
		$('#postCreateBtn').on('click',function(){
			let subject = $('#subject').val().trim();
			let content = $('#content').val();
			
			if(subject == ''){
				alert("제목을 입력하세요");
				return;
			}
			
			console.log(content);
			
			let file = $('#file').val();
			// alert(file);
			// file이 업로드 된 경우에만(null 가능) 확장자 체크
			if(file != ''){
				let ext = file.split(".").pop().toLowerCase(); // 맨 마지막 문자(확장자) 소문자로 변경
				if($.inArray(ext,['jpg','jpeg','png','gif']) == -1){
					alert("이미지 파일만 업로드 할 수 있습니다.");
					$('#file').val(""); // 파일을 비운다.
					return;
				}
			}
			
			// 서버 - AJAX
			
			// 이미지(file)를 업로드할 때는 form 태그가 있어야 한다.
			// 근데 다른 방법으로 할 거임
			// append로 넣는 값은 form의 name과 같다.(request parameter)
			let formData = new FormData();
			formData.append("subject",subject);
			formData.append("content",content);
			formData.append("file",$('#file')[0].files[0]);
			
			// AJAX 통신으로 formData에 있는 데이터 전송
			$.ajax({
				// request
				type:"POST"
				,url:"/post/create"
				,data:formData // form 객체를 통째로 보냄
				,enctype:"multipart/form-data" // 파일 업로드를 위한 필수 설정
				// 이건 String(기본)으로 보낼 게 아니라 form이라고 명시(false)하는 것
				, processData:false // 파일 업로드를 위한 필수 설정
				, contentType:false // 파일 업로드를 위한 필수 설정
				
				// response
				,success:function(data){
					if(data.code == 1){
						// 성공
						alert("메모가 저장되었습니다.");
						location.href = "/post/post_list_view";
					} else{
						// 실패
						alert(data.errorMessage);
					}
				}
				,error:function(e){
					alert("메모 저장에 실패했습니다.");
				}
			});
		});
	});
</script>