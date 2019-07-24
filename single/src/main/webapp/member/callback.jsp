<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> --%>

<!-- 네이버로부터 정보를 받는 페이지입니다. -->
<%@ include file = "/WEB-INF/views/commons/template/modern_business_top.jsp" %>
<%@ include file="/WEB-INF/views/commons/alert_danger.jsp"%>

<c:set var="root" value="${pageContext.request.contextPath}" />
<!-- 네아로 설정값-->
<c:set var = "clientId" value = "3FGMY2V_UXaBQxS0sx0g"/>
<!-- 로컬용 -->
<c:set var = "serviceUrl" value = "http://localhost/single"/>
<c:set var = "callbackUrl" value = "http://localhost/single/member/callback.jsp"/>

<!-- 네아로 자바스크립트-->
<script type="text/javascript" src="https://static.nid.naver.com/js/naverLogin_implicit-1.0.3.js" charset="utf-8"></script>

<%-- <script type="text/javascript" src="https://code.jquery.com/jquery-1.11.3.min.js"></script> --%>

<!-- ip설정용 -->
<%-- <c:set var = "serviceUrl" value = "https://192.168.14.40:8443/single"/> --%>
<%-- <c:set var = "callbackUrl" value = "https://192.168.14.40:8443/single/member/callback.jsp"/>  --%>

<%--<c:set var = "serviceUrl" value = "https://192.168.14.14:8443/single"/>  --%>
<%-- <c:set var = "callbackUrl" value = "https://192.168.14.14:8443/single/member/callback.jsp"/>  --%>

<%-- <c:set var = "serviceUrl" value = "https://192.168.14.22:8443/single"/> --%>
<%-- <c:set var = "callbackUrl" value = "https://192.168.14.22:8443/single/member/callback.jsp"/> --%>

<form id="naverProfileForm">
	<input type ="hidden" name ="email" id="email" >
	<input type ="hidden" name ="accessToken" id="accessToken" >

	<input type="hidden" name="userId" id="userId">
	<input type="hidden" name="userName" id="userName">
	<input type="hidden" name="userNickname" id="userNickname">
	<input type="hidden" name="userGender" id="userGender">
	<input type="hidden" name="userBirthday" id="userBirthday">
	<input type="hidden" name="userProfile_image" id="userProfile_image">

	<input type="hidden" name="snsId" id="snsId">
	<input type="hidden" name="snsType"  id="snsType">
<%--    <input type="hidden" name="snsemail" value="${userInfo.snsDto.snsEmail }"> 한페이지에서 두개의 값을 전달하면 배열로 전달된다. --%>
	<input type="hidden" name="snsToken" id="snsToken">
	<input type="hidden" name="snsConnectDate" id="snsConnectDate">
</form>


<%-- javascript에서 c태그 씀녀 에러난다 주석이라도 .. <c: if test exsist memberout/> --%>
	

<script type="text/javascript">
  var naver_id_login = new naver_id_login("${clientId}", "${callbackUrl}");
  // 접근 토큰 값 출력
  var accessToken =naver_id_login.oauthParams.access_token;
  console.log('accessToken'+ accessToken);
  // 네이버 사용자 프로필 조회
  naver_id_login.get_naver_userprofile("naverSignInCallback()");
  
  // 네이버 사용자 프로필 조회 이후 프로필 정보를 처리할 callback function
  function naverSignInCallback() {
    var email = naver_id_login.getProfileData('email');
   
    $('#email').val(email);
	$('#accessToken').val(accessToken);
	
	console.log('email'+ email);
	console.log('accessToken'+ accessToken);
// 	2019-07-10backup
// 	$("#naverProfileForm").attr("method", "POST").attr("action", "${root}/navermember/callback").submit();
// 기존 코드 
	
// 	$(document).ready(function(){
		var formdata = $('#naverProfileForm').serialize(); //-> contentType:"application/x-www-form-urlencoded; charset=UTF-8", //default
		console.log(formdata);
		$.ajax({
			method:"POST",
			url: "${root}/navermember/callback",
			data: formdata,
			dataType : "json", //서버에서 반환되는 형식
			success :function(data){
				console.log(data);
				
				
				if(data.msg =='outmember'){
// 					console.log('탈퇴한 회원');
// 					window.location = document.referrer + '?msg=outmember';
// 					history.back();
// msg : memberout 
// altert 1! 맴버없습니다..
<%--  <c: if test exsist memberout/> --%>
					$('.btn-danger').html('뒤로가기');
					$(document).on('click', '.btn-danger', function(){
						history.back();
					});
					showAlertModal('로그인 실패', '탈퇴한 회원입니다.');
					$(document).on('click', '.backBtn', function(){
	 					history.back();					
					});
					
				}
				else if(data.msg =='register'){
// 					console.log('회원가입 페이지이동');
// 					console.log(data);
// 					console.log('=======');
// 					console.log(data.userInfo.userId);
					$('#userId').val(data.userInfo.userId);
					$('#userName').val(data.userInfo.userName);
					$('#userGender').val(data.userInfo.userGender);
					$('#userBirthday').val(data.userInfo.userBirthday);
					$('#userProfile_image').val(data.userInfo.userProfile_image);
					
					$('#snsId').val(data.userInfo.userId);
					$('#snsType').val(data.userInfo.snsDto.snsType);
					$('#snsToken').val(data.userInfo.snsDto.snsToken);
					$('#snsConnectDate').val(data.userInfo.snsDto.snsConnectDate);
					
					//registerpage로 전달 되야함
					$('#naverProfileForm').attr("method","post");
					$('#naverProfileForm').attr("action","${root}/member/registersns").submit();
					//회원가입폼으로 값 전달.
				}else if (data.msg =='refresh'){
// 					console.log('자동 로그인');
					var userInfo = data.userInfo;
					console.log(data.userInfo);
					console.log(document.referrer);
					sessionStorage.setItem("userInfo",data.userInfo);
					history.back();
				}		
			},beforeSend: function () {
				
			},error: function(erorrdata){
				console.log(erorrdata);
			}
	  	});
// 	});
  }
</script>
네아로 콜백 사이트
<button type ="button" class = "backBtn">뒤로가기</button>
<!-- <!-- 2019-07-10backup --> 
<!-- <form id="naverProfileForm"  > -->
<!-- 	<input type ="hidden" name ="email" id="email" > -->
<!-- 	<input type ="hidden" name ="accessToken" id="accessToken" > -->
<!-- 	<input type ="hidden" name ="id" id="id" > -->
<!-- 	<input type ="hidden" name ="nickname" id="nickname" > -->
<!-- 	<input type ="hidden" name ="age" id="age" > -->
<!-- 	<input type ="hidden" name ="gender" id="gender" > -->
<!-- 	<input type ="hidden" name ="birthday" id="birthday" > -->
<!-- 	<input type ="hidden" name ="profile_image" id="profile_image" > -->
<!-- </form> -->

<!--     var id = naver_id_login.getProfileData('id'); -->
<!--     var nickname = naver_id_login.getProfileData('nickname'); -->
<!--     var age= naver_id_login.getProfileData('age'); -->
<!--     var gender= naver_id_login.getProfileData('gender'); -->
<!--     var birthday= naver_id_login.getProfileData('birthday'); -->
<!--     var profile_image= naver_id_login.getProfileData('profile_image'); -->
<!--     	$('#id').val(accessToken); -->
<!-- 	$('#nickname').val(nickname); -->
<!-- 	$('#age').val(age); -->
<!-- 	$('#gender').val(gender); -->
<!-- 	$('#birthday').val(birthday); -->
<!-- 	$('#profile_image').val(profile_image); -->
<%@ include file = "/WEB-INF/views/commons/template/modern_business_bottom.jsp" %>