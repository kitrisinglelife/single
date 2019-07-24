<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file = "/WEB-INF/views/commons/template/modern_business_top.jsp" %>

<!-- <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script> -->


<script>

$(function() {
	
	var boardNum = $(".boardNum").val();
	
	// 답변 보기를 들어와서 보여주는 이유는 답변 작성하고 나서 다시 서버에 들렸다 내것을 보여주기 위해서임.
	selectanswer(boardNum);
	
	// 답변 글쓰기 textarea불러오기.
	$(document).on("click",".answerBtn",function (){
		
		$.ajax({
			url : '${root}/board/answerwritepage',
			type : 'GET',
			success : function(response) {
				if(response.trim() == "99"){
					alert("로그인을 해주세요");
				} else {
					$(".answer").html(response.trim());
					$(".answer").show();
				}
			}
		});
	});
	
	
	// 답변 글쓰기.
	$(document).on("click","#completeBtn",function (){
		
		var replyContent = $('#replyContent').val();
		
		$.ajax({
			url : '${root}/board/answerwrite',
			contentType : "application/json",
			dataType : "json",
			type : 'GET',
			data : {
				'boardNum': boardNum,
				'replyContent': replyContent
			},
			success : function(response) {
				if(response.resultCode == 1){
					$(".answer").hide();
					$("#replyContent").val("");
					selectanswer(boardNum);
				} else {
					alert("답변쓰기가 실패하였습니다. 다시 시도해주세요.");
					$(".answer").hide();
					$("#replyContent").val("");
				}
			}
		});
	});
	
	
	// 답변 삭제하기.
	$(document).on("click",".answerDelete",function (){
		
		//var replyNum = $(".replyNum").val();
		var replyNum = $(this).children(".replyNum").val();
		
		$.ajax({
			url : '${root}/board/delete',
			contentType : "application/json",
			//type : 'DELETE',
			data : {
				'replyNum': replyNum,
			},
			
			dataType : "json",
			success : function(response) {
				if(response.resultCode == 1){
					alert("삭제 성공");
					selectanswer(boardNum);
				} else if(response.resultCode == 0){
					alert("삭제가 실패하였습니다. 다시 시도해주세요.");
				} else {
					alert("다시 로그인을 해주세요.");
					location.reload();
				}
			}
		});
	});
	
	
	// 답변 쓰기 취소.
	$(document).on("click","#cancleBtn", function () {
		$(".answer").hide();
		$("#replyContent").val("");
	});
	
	
	// 좋아요 기능.
	$(document).on("click","#like", function () {
		$.ajax({
			url : '${root}/board/like',
			contentType : "application/json",
			dataType : "json",
			type : 'POST',
			data : JSON.stringify({
				'boardNum': boardNum
			}),
			dataType : "json",
			success : function(response) {
				if(response.resultCode == 1){
					//성공
					location.reload();
				} else if(response.resultCode == 2){
					//사실은 서버 오류도 포함된 내용
					//좀더 세분화해야 함
					alert("이미 좋아요 누른 글입니다");
				} else {
					alert("로그인을 해주세요"); 
				}
			}
		});
	});
	
	
	// 글 삭제
	$(document).on("click","#boardDelete",function (){
		//alert("버튼클릭");
		$.ajax({
			url : '${root}/board/boardDelete?boardNum=' + boardNum,
			dataType : "json",
			success : function(response) {
				if(response.resultCode == 1){
					alert("삭제 성공");
					//selectanswer(boardNum);
					location.href="${root}/board/singlemain";  
				} else if(response.resultCode == 2){
					alert("삭제가 실패하였습니다. 다시 시도해주세요.");
				} else {
					alert("다시 로그인을 해주세요.");
					//location.reload();
					location.href="${root}/member/login"; 
				}
			}
		});
	});
	
	
});







function selectanswer(boardNum) {
	
	// 답변들 불러오기.
	$.ajax({
		url : '${root}/board/answerview',
		type : 'GET',
		data : {
		'boardNum':boardNum
		},
		success : function(response) {
			$(".answerview").html(response.trim());
		}
	});
		
}


</script>


<!-- 글번호 -->
<input class="boardNum" type="hidden" value="${article.boardNum }">




<div class="container">

    <!-- Page Heading/Breadcrumbs -->
    <h2 class="mt-4 mb-3">혼자놀아요
		<small>- 
		<c:if test="${article.boardListNum == 1 }">
			자취생활팁
		</c:if>
		<c:if test="${article.boardListNum == 2 }">
			요리레시피
		</c:if>
		<c:if test="${article.boardListNum == 3 }">
			명예의전당
		</c:if>
		</small>
    </h2>

    <!-- <ol class="breadcrumb" style="background-color: #004085!important;"></ol> -->




    <div class="row">
    <!-- 메인 중앙 -->
    <div class="col-lg-10 mb-4">
      
      
		<div class="row">
			<div class="col-lg-3">
				<h5>
					<c:if test="${article.boardListNum == 1 }">
						자취생활 팁 공유
					</c:if>
					<c:if test="${article.boardListNum == 2 }">
						요리레시피 팁 공유
					</c:if>
					<c:if test="${article.boardListNum == 3 }">
						명예의전당
					</c:if>
				</h5>
			</div>
			<div class="col-lg-9"></div>
		</div>
		
		
		    
		<!-- <div class="col-lg-10 mb-4" style="border: 2px solid; background-color: #fff"> -->
	    <div class="container" style=" border: 2px solid #444444;"><br><br>  
      
      
      
      	
      	<!-- 본글 -->
	   	<div class="row">
			<div class="col-lg-2"></div>
			<!-- 제목 -->
			<div class="col-lg-5">
				<h5 style="margin-top: 2rem">${article.boardSubject }</h5>
			</div> 
			<div class="col-lg-4">
				<div>
					<img src="${root}/resources/img/bimg/like.jpg" alt="좋아요" width="100" height="40" style="margin-bottom: 0.5rem">
					${article.boardLike }
				</div>
				<small>작성자 : ${article.userId }</small>
			</div>
			<div class="col-lg-1"></div>
		</div>
		
		
		
		<div class="row">
			<div class="col-lg-1"></div>
			<div class="col-lg-10"><hr style="background-color: #6c757d"></div>
			<div class="col-lg-1"></div>
		</div>
		<div class="row">
			<div class="col-lg-2"></div>
			<div class="col-lg-3"><small>작성일 : ${article.boardCreatedate }</small></div>
			<div class="col-lg-2" style="padding-right: 0;"></div>
			<div class="col-lg-2" style="padding-right: 0.5; text-align: right;">
				<c:if test="${session.userId == article.userId}">
					<button type="button" id="boardDelete" class="btn btn-outline-danger">글삭제</button>
	  			</c:if>
			</div>
			<div class="col-lg-2" style="padding-left: 0;">
				<button type="button" id="like" class="btn btn-outline-secondary" style="color: #007bff; border-color: #007bff;">좋아요</button>
			</div>
		</div>
		
		
		
		<!-- 답글 -->
	   	<div class="row">
	   		<div class="col-lg-2"></div>
			<div class="col-lg-8" style="border: 1px solid #444444;">
			<br><br><br><br>
			<!-- 글 내용 -->
			
			${article.boardContent }
			
			<br><br>
			
			<c:forEach var="hash" items="${article.hashtagList}">
				<c:if test="${hash != null }">
					<tag>
					#${hash}
					</tag>&nbsp;
				</c:if>
			</c:forEach>	
			<br><br>
			</div>
			<div class="col-lg-2"></div>
	   	</div><br>
	   	<div class="row">
	   		<div class="col-lg-7"></div>
	   		<div class="col-lg-2">
	   		</div>
	   		<div class="col-lg-2">
	   			<a class="answerBtn">
		   			<button class="btn btn-outline-secondary" style="color: #007bff; border-color: #007bff;">답변</button>
	   			</a>
	   		</div>
	   		<div class="col-lg-1"></div>
	   	</div><br>
	   	
	   	
		<!-- <div class="row">
			<div class="col-lg-1"></div>
			<div class="col-lg-10"><hr style="background-color: #adb5bd"></div>
			<div class="col-lg-1"></div>
		</div> -->  




		<div class="row"  style="background-color: #eee;">   
		   	<!-- 답변글 쓰기 -->
		   	<div class="col-lg-12">
			<div class="answer"></div>    
		   	</div>
			 
			<div class="col-lg-12">
			<!-- 답별글 보기 -->
	      	<div class="answerview"></div>
			</div>			

		</div>
      	
      	
      	
      	</div>
	</div>
	  
	  
	  
	  
	<!-- Sidebar Column -->
	<%@ include file = "/WEB-INF/views/commons/singlecategory.jsp" %>
		
		
		
		
	</div>
	<!-- row -->

</div>
<br><br><br><br><br><br><br><br>






  
  
  
  
  
  
  
<%@ include file = "/WEB-INF/views/commons/template/modern_business_bottom.jsp" %>
