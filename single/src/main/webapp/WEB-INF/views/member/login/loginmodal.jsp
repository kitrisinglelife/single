<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/commons/alert_danger.jsp"%>

<!-- 
----설명---- 
네이버 로그인 모달창 입니다.
현재 페이지를 import한 후 
id: loginmodal의 display를 block으로 변경해주세요.
-->
 
<!-- 네아로 설정값-->
<c:set var = "clientId" value = "3FGMY2V_UXaBQxS0sx0g"/>
<!-- 로컬용 -->
<c:set var = "serviceUrl" value = "http://localhost/single"/>
<c:set var = "callbackUrl" value = "http://localhost/single/member/callback.jsp"/>

<!-- ip설정용 -->
<%-- <c:set var = "serviceUrl" value = "https://192.168.14.40:8443/single"/> --%>
<%-- <c:set var = "callbackUrl" value = "https://192.168.14.40:8443/single/member/callback.jsp"/>  --%>

<%--<c:set var = "serviceUrl" value = "https://192.168.14.14:8443/single"/>  --%>
<%-- <c:set var = "callbackUrl" value = "https://192.168.14.14:8443/single/member/callback.jsp"/>  --%>

<%-- <c:set var = "serviceUrl" value = "https://192.168.14.22:8443/single"/> --%>
<%-- <c:set var = "callbackUrl" value = "https://192.168.14.22:8443/single/member/callback.jsp"/> --%>





<!-- 네아로 자바스크립트-->
<script type="text/javascript" src="https://static.nid.naver.com/js/naverLogin_implicit-1.0.3.js" charset="utf-8"></script>
<!-- 모달 css (이미지 참고 사이트 :  https://www.w3schools.com/howto/tryit.asp?filename=tryhow_css_login_form_modal -->
<link rel="stylesheet" href="${root}/resources/css/member/loginmodal.css" type="text/css">


<script>
$(document).ready(function(){
	
	var loginBtn =$('.loginModalBtn'); //로그인 버튼을 등록해주세요.  
	
	//로그인 모달 띄우기  loginmodal id를 통해서 사용됨
	loginBtn.click(function() {
		$('#loginmodal').css("display","block").attr("width","auto");
		return false;
	});
	
	//회원가입 버튼
	$('.registerBtn').click(function(){
		$(location).attr("href","${root}/member/emailauth");
	})
	  
	//비밀번호 찾기
	$('#passfindBtn').click(function(){
		$(location).attr("href","${root}/member/emailpassfind");
    });
	
	 //로그인
	$('#btnLogin').click(function() {
		login();	
	})
	$('#userpass').keydown(function(key) {
		if (key.keyCode == 13) {
			login();
		}
	});
	function login(){
//		$('#loginForm').attr("action", "${root}/member/login").attr("method", "post");

// 	 	var formdata = new FormData($('#loginForm')[0]); // ->	contentType: false,
// 		contentType: false, //서버에 전달하는 형식
// 		processData: false,
		var formdata = $('#loginForm').serialize(); //-> contentType:"application/x-www-form-urlencoded; charset=UTF-8", //default
		console.log(formdata);
		$.ajax({
			url:"${root}/member/login",
			type: "POST",
			data: formdata,
			dataType : "json", //서버에서 반환되는 형식
			success :function(data){
				console.log('datareceive :'+ data.msg);
				if(data.msg == '1'){
					console.log('로그인에 성공하였습니다.');
					console.log('datareceive :'+ data);
					console.log('datareceive :'+ data.msg);
					location.reload();
//		 				history.back();
//		 				history.go();
//		 				history.forward();
				}
				else if(data.msg == '11'){
					showAlertModal('로그인 실패', '존재하지 않는 아이디입니다.')
				}
				else if(data.msg == '12'){
					showAlertModal('로그인 실패', '비밀번호가 틀렸습니다.')
				}
				else if(data.msg == '13'){
					showAlertModal('로그인 실패', '탈퇴한 회원입니다.')
				}
			
			},error: function (){
				console.log('error')
			}
		});
	}
	
	
		 
	

// 	//로그인 
// 	$('#loginForm').attr("action", "${root}/member/login").attr("method", "post")
});


</script>



<!-- <button class = "naverlogin-button" onclick="document.getElementById('loginmodal').style.display='block'" style="width:auto;">Login</button> -->
  <div id="loginmodal" class="modal">  
  <form id="loginForm" class="modal-content animate" >
    <div class="imgcontainer">
      <span onclick="document.getElementById('loginmodal').style.display='none'" class="close" title="Close Modal">&times;</span>
    </div>

    <div class="container">
    	<h3>우리 혼자 살아요</h3>
      
      <label class="loginmodal-label" for="userId"><b>아이디</b></label>
      <input type="email" placeholder="아이디를 입력해주세요" name="userId" required style="font-family:돋움">

      <label class="loginmodal-label" for="userPassword" ><b>비밀번호</b></label>
      <input type="password" placeholder="비밀번호를 입력해주세요" name="userPassword" id="userpass" required style="font-family:돋움">
      
      <!-- 로그인 -->  
      <button id= "btnLogin" type="button">Login</button>
      <!-- 네이버로그인 -->
       <div id="naver_id_login" style="display:inline-block"></div>
      <span class="other"><a href="#" class = "registerBtn">회원가입</a> <%--<a href="#" id= "passfindBtn">비밀번호 찾기</a> --%></span>
      <%--<span class="other"> <a href="#" class = "registerBtn">회원가입</a> <a href="#" id= "passfindBtn">비밀번호 찾기</a> </span>--%>
<!--       <input type="checkbox"  name="remember"/> <label for="remember">아이디 기억하기</label> -->
    </div>

    <div class="container" style="background-color:#f1f1f1">
      <button type="button" onclick="document.getElementById('loginmodal').style.display='none'" class="cancelbtn">Cancel</button>
    </div>
    
     
  </form>
  
</div>

<script>
var modal = document.getElementById('loginmodal');
// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}
</script>

<!-- 네이버 로그인 실행  http://localhost/single/naverlogintest.jsp-->
<script type="text/javascript">
  	var naver_id_login = new naver_id_login("3FGMY2V_UXaBQxS0sx0g","${callbackUrl}");
//   	var naver_id_login = new naver_id_login("3FGMY2V_UXaBQxS0sx0g", "http://localhost/single/naverlogin/mvcallback");
  	var state = naver_id_login.getUniqState();
  	naver_id_login.setButton("white", 2,40);
  	naver_id_login.setDomain("${serviceUrl}");

  	naver_id_login.setState(state);
//   	naver_id_login.setPopup();
  	naver_id_login.init_naver_id_login();
</script>
<!-- //네이버 로그인 실행-->

