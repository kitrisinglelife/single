<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/commons/template/modern_business_top.jsp"%>
<link rel="stylesheet" href="${root}/resources/css/group/group.css">
<link rel="stylesheet" href="${root}/resources/css/user/user.css">

<style>
.modal-body{
	 font-family : '돋움'
}
label {
    display: inline-block;
    margin-bottom: .5rem;
    float: left;
    margin-right: 115px;
    font-size: 14px;
}

.h6, h6 {
    font-size: 0.8rem;
}


.btn_submit {
    color: white !important;
}


</style>
	
<%
HttpSession httpSession = request.getSession();
UserDto userInfo = new UserDto();
userInfo.setUserId("calubang1@naver.com");
userInfo.setUserNickname("카루뱅");
userInfo.setUserName("안병욱");
httpSession.setAttribute("userInfo", userInfo);
%>

<script>



/* 변경사항 저장 버튼 클릭 시  */
 $(function() {
	 $(".userinfoBtn").click(function() {
		//나중에 입력하라고 경고문구띄우기
		$.ajax({
			url: "${root}/mypage/modify",
			type: "POST",
			processData: false,  //사진업로드때문에 함
			contentType: false,  //사진업로드때문에 함
			data: new FormData($("#userinfoform")[0]), //사진업로드때문에 함
			dataType: "json", //응답
			success: function(result) {
				console.log(result);
			}
		});	
	});
})


/* 비밀번호 변경 클릭 시  */
$(function(){
	$(".btn-primary").click(function(){
		var result = confirm('정말 탈퇴하시겠습니까?');

		if(result){
			   alert("탈퇴가 완료되었습니다.");      
			   $(this).attr("href", "${root}/member/exit");
		   }
	});
});



</script>


<div id="my-page-wrap">
	<div id="my-page-header">
		<div id="header-content" class="my-page-content">
			<div class="user-photo"
				style="display: inline-block; background-image: url(&quot;https://cdn.studysearch.co.kr/images/users/199883/profile/1561443247&quot;); background-size: cover; background-position: 50% 50%;"></div>
			<div id="tabs">
				<div id="user-name">고세라</div>
				<div id="tab-box">
					<a class="tab selected" href="${root }/mypage/mypage">내 프로필</a> 
					<a class="tab" href="${root }/mypage/groupall">나의 모임관리</a> 
					<a class="tab" href="${root}/mypage/stampgroup">찜한 모임</a> 
					<a class="tab" href="${root}/user_mypage/myPageWrite.jsp">게시물관리</a>
				</div>
			</div>
		</div>
	</div>

	<br><br>
	<!-- 여기서부터 비밀번호 모달  -->
	<div class="modal" id="myModal">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">

				<!-- Modal Header -->
				<div class="modal-header">
					<h4 class="modal-title">비밀번호 재설정</h4>
					<button type="button" class="close" data-dismiss="modal">&times;</button>
				</div>

				<!-- Modal body -->
				<form action="/ko/member/change_pw" method="post" id="pw_form">
					<div class="modal-body">
						<input type="password" placeholder="현재 비밀번호"
							class="pw_input old_pw" name="old_pw"> <input
							type="password" placeholder="신규 비밀번호"
							class="pw_input new_pw" name="userPassword"> <input
							type="password" placeholder="신규 비밀번호 확인"
							class="pw_input pw_vali" name="pw_vali">
					</div>

					<!-- Modal footer -->
					<div class="modal-footer">
						<button type="button" class="btn btn-danger" id="btn-danger"
							data-dismiss="modal">변경</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	<!-- 여기서부터 비밀번호 모달 끝 -->
	
	
	<!-- 여기서부터 탈퇴 모달  -->
	<div class="modal" id="deleteModal">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">

				<!-- Modal Header -->
				<div class="modal-header">
					<h4 class="modal-title">회원탈퇴</h4>
					<button type="button" class="close" data-dismiss="modal">&times;</button>
				</div>

				<!-- Modal body -->
				<form action="/ko/member/change_pw" method="post" id="delete_form">
					<div class="modal-body">
						<div>
						<h6>사용하고 계신 계정을 탈퇴할 경우 복구가 불가능합니다. 
						탈퇴후 회원 정보 및 서비스 이용기록은 모두 삭제되오니 신중하게 선택하여 주시기 바랍니다.
						<br><br>
						회원탈퇴를 위해 비밀번호를 입력해 주세요.
						</h6>

						</div>
						<input type="password" placeholder="현재 비밀번호"class="pw_input old_pw" name="old_pw"> 
					</div>

					<!-- Modal footer -->
					<div class="modal-footer">
						<button type="button" class="btn btn-primary" data-num="">탈퇴</button>
	          			<button type="button" class="btn btn-danger" data-dismiss="modal">닫기</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	<!-- 여기서부터 비밀번호 모달 끝 -->
	
	
	<div class="wrap">
	<form id="userinfoform" method="post" action="" enctype="multipart/form-data" >
	<!-- 사진 방식을 보낼때 지킬것  -->
		<div class="config_area">
			<div class="config_title">
				<!--계정설정-->
				계정설정
			</div>
			<table class="config_table" width="100%">
				<colgroup>
					<col width="250">
					<col width="*">
				</colgroup>
				<tbody>
					<!-- 테이블 한 줄  -->
					<tr>
						<th>
							<!--이메일 주소-->아이디 :
						</th>
						<td>
							<input type="text" name="userId" class="t_input" value="${userInfo.userId}">
							<%-- <input type="text" name="userId" class="t_input" value="${dto.userId}" disabled=""> --%>
						</td>
					</tr>
					<tr>
						<td class="space">&nbsp;</td>
					</tr>
					<!-- 테이블 한 줄  -->
					
					<!-- 테이블 한 줄  -->
					<tr>
						<th>
							<!--이메일 주소-->이   름 :
						</th>
						<td>
							<input type="text" name="userId" class="t_input" value="${userInfo.userName}">
							<%-- <input type="text" name="userId" class="t_input" value="${dto.userId}" disabled=""> --%>
						</td>
					</tr>
					<tr>
						<td class="space">&nbsp;</td>
					</tr>
					<!-- 테이블 한 줄  -->
					
					<!-- 테이블 한 줄  -->
					<tr>
						<th>
							<!--이름-->닉네임 :
						</th>
						<td><input type="text" name="userNickname" class="t_input" value="${userInfo.userNickname}"></td>
					</tr>
					<tr>
						<td class="space">&nbsp;</td>
					</tr>
					<!-- 테이블 한 줄  -->
					
					<!-- 테이블 한 줄  -->
					<tr>
						<th>
							<!--비밀번호-->비밀번호 :
						</th>
						<td>
							<!-- Button to Open the Modal -->
							<button type="button" class="btn btn-primary" data-toggle="modal"
								data-target="#myModal">비밀번호 재설정</button> <!-- The Modal -->
							
						</td>
					</tr>
					<tr>
						<td class="space">&nbsp;</td>
					</tr>
					<!-- 테이블 한 줄  -->
					
					<!-- 테이블 한 줄  -->
					<tr>
						<th>
							<!--이름-->핸드폰 :
						</th>
						<td><input type="text" name="userPhone" class="t_input"
							value="${userInfo.userPhone}"></td>
					</tr>
					<tr>
						<td class="space">&nbsp;</td>
					</tr>
					<!-- 테이블 한 줄  -->
					
					
					<%@ include file="/WEB-INF/views/commons/fileupload.jsp"%>


				</tbody>
			</table>

			<div class="config_foot">
				<a href="#" class="leavelink" data-toggle="modal" data-target="#deleteModal">회원탈퇴</a> 
				<!-- <button type="button" class="btn btn-primary">변경사항 저장</button> -->
				<a href="#" class="btn_submit userinfoBtn" > 변경사항 저장</a>
				<div class="clear"></div>
			</div>
		</div>
		</form>
	</div>
</div>
<%@ include
	file="/WEB-INF/views/commons/template/modern_business_bottom.jsp"%>