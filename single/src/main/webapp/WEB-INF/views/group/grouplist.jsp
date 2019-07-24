<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/commons/template/modern_business_top.jsp"%>
<link rel="stylesheet" href="${root}/resources/css/group/group.css" >
<%@ include file="/WEB-INF/views/commons/movetop.jsp"%>
<%@ include file="/WEB-INF/views/commons/alert_danger.jsp"%>
<%@ include file="/WEB-INF/views/commons/alert_success.jsp"%>
<script src="${root}/resources/js/group/group.js"></script>
<c:set var="parameter" value="${requestScope.parameter}"></c:set>
<script>
//페이징 처리를 위한 부분
var page = "";
var key = "";
var word = "";
var totalPageCount = "";
var totalListCount = "";
$(function() {
	//$(".groupcard").on("click", groupcardClick);
	
	page = "${parameter.page}";
	key = "${parameter.key}";
	word = "${parameter.word}";
	isMyGroup = "${parameter.isMyGroup}";
	groupCategoryNum = "${parameter.groupCategoryNum}";
	getGroupList(page, key, word, isMyGroup, groupCategoryNum);
	
	//페이지 이동류 버튼
	$(".pagebtn").on("click", function () {
		location.href = "${root}/group/" + $(this).attr("data-page");
	});
	
	//내 모임보기 버튼
	$("button.mygrouplist").on("click", function() {
		$("button.mygrouplist").toggle();
		$("#categoryFilter").val("0").siblings("button").text("카테고리");
		if($(this).hasClass("my")){
			getGroupList("1", "", "", "yes", "");
		}else{
			getGroupList("1", "", "", "", "");
		}
		
	});
	
	//모두보기 버튼
	/* $("section.search .dropdown").on("hidden.bs.dropdown", function(){
		$("#categoryFilter").val("0").siblings("button").text("카테고리");
		
		//page, key, word, isMyGroup, groupCategoryNum
		var key = $(this).find("input[type=hidden]").val();
		var word = $("#word").val();
		if(key == 'all'){
			getGroupList("1", "", "", "", "");
		}else{
			getGroupList("1", key, word, "", "");
		}
	}); */
	
	//카테고리 이벤트
	$(".container>.row>.dropdown").off("hidden.bs.dropdown").on("hidden.bs.dropdown", function() {
		var categoryFilter = $("#categoryFilter").val();
		if(categoryFilter == '0'){
			$(".groupcard").show();
		}else{
			$(".groupcard").each(function(index, item) {
				var categoryNum = $(item).attr("data-cate");
				if(categoryNum == categoryFilter){
					$(item).show();
				}else{
					$(item).hide();
				}
			})
		}
	});
	
	//스크롤 페이징..
	$(window).off("scroll").on("scroll", scrollPage);
	
	//검색어 기능
	$("#word").on("keyup", function(e) {
		if(e.keyCode == 13){
			$("#searchBtn").trigger("click");
		}
		return false
	});
	$("#searchBtn").on("click", function (){
		word = $("#word").val();
		if($("#key").val() == 'all'){
			key = "";
		}else{
			key = $("#key").val();
		}
		getGroupList("1", key , word , "" , "");
	});
	
	//2019-07-12 형태희 추천받기 버튼
	$(".recomandBtn").on("click", function () {
		$("#categoryFilter").val("0").siblings("button").text("카테고리");
		$.ajax({
			 url : "${root}/group/grouprecommend",
			 method : "get",
			 success : function(result) {
				console.log(result);
				//console.log(result);
				$(".group-list").html(result);
				$(".groupcard").click(groupcardClick);
			}
		});	
	});
});


function scrollPage(){
	//top 버튼
	if($("body").scrollTop() > 20 || $(document).scrollTop() > 20){
		$("#topBtn").css("display", "block");
	}else{
		$("#topBtn").css("display", "none");
	}
	
	if($(this).scrollTop() + $(this).height() > $("body").height()){
		if(Number(page) < Number(totalPageCount)){
			getGroupList(Number(page)+1, key, word, "", "");
			$(window).off("scroll");
		}
	}
	return false
}

function getGroupList(page, key, word, isMyGroup, groupCategoryNum){
	if(page == null || (typeof page) == "undefined" || page == ""){
		page = "1";
	}
	if(key == null || (typeof key) == "undefined" || key.length == 0){
		key = "";
	}
	if(isMyGroup == null || (typeof isMyGroup) == "undefined"){
		isMyGroup = "";
	}
	if(groupCategoryNum == null || (typeof groupCategoryNum) == "undefined"){
		groupCategoryNum = "";
	}
	
	var url = "${root}/group/grouplist";
	var data = {
		"page" : page
		, "key" : key
		, "word" : word
		, "isMyGroup" : isMyGroup
		, "groupCategoryNum" : groupCategoryNum
	};
	var success = function(result) {
		if(isMyGroup == 'yes'){
			//console.log(result);
			$(".group-list").html(result);
			$(".groupcard").click(groupenter);
		}else{
			//console.log(result);
			if(page ==  '1'){
				$(".group-list").html(result);
			}else{
				$(".group-list").append(result);
			}
			$(".groupcard").click(groupcardClick);
		}
	
		var categoryFilter = $("#categoryFilter").val();
		if(categoryFilter == '0'){
			$(".groupcard").show();
		}else{
			$(".groupcard").each(function(index, item) {
				var categoryNum = $(item).attr("data-cate");
				if(categoryNum == categoryFilter){
					$(item).show();
				}else{
					$(item).hide();
				}
			})
		}
		$(window).off("scroll").on("scroll", scrollPage);
		$("#listsize").text("모두 " + totalListCount + " 개의 모임");
		
	}
	$.ajax({
		url : url
		, data : data
		, method : "get"
		, success : success
	});
}
function groupenter(e){
	var groupNum = $(this).attr("data-num");
	location.href = "${root}/group/" + groupNum;
}

function groupcardClick(){
	var groupNum = $(this).attr("data-num");
	var url = "${root}/group/groupdetail";
	var data = {
		"groupNum" : groupNum	
	};
	var success = function (result) {
		groupDetailModalSetting(result);
		$("#groupDetailModal").modal("show");
	}
	
	ajaxFunc(data, url, "get", success);
	
} 
</script>

<%@ include file="groupdetailmodal.jsp"%>
<%@ include file="groupheader.jsp"%>
<%@ include file="groupsearch.jsp" %>

<!-- 여기서부터 실제 데이터 영역 -->
<section class="contents">
	<div class="container">
		<div class="row">
			<h1 class="col-lg-4 col-md-4 col-sm-3 my-4" id="listsize"> 개의 모임</h1>
			<!-- <div class="btn-group col-lg-8 col-md-8 col-sm-9 right" style="padding: 0px;"> -->
			<c:if test="${empty sessionScope.userInfo}">
			<div class="col-lg-2 col-md-2 col-sm-3 my-4 category">
				<button type="button" class="btn btn-success mygrouplist all" style="visibility: hidden;">모두보기</button>
			</div>
			<div class="col-lg-2 col-md-2 col-sm-3 my-4 category">
				<button type="button" class="btn btn-primary pagebtn" data-page="create" style="visibility: hidden;">모임 만들기</button>
			</div>
			<div class="col-lg-2 col-md-2 col-sm-3 my-4 category" >
				<button type="button" class="btn btn-info recomandBtn" style="visibility: hidden;">추천 받기</button>
			</div>
			</c:if>
			<c:if test="${!empty sessionScope.userInfo}">
			<div class="col-lg-2 col-md-2 col-sm-3 my-4 category">
				<button type="button" class="btn btn-success mygrouplist my">내 모임보기</button>
				<button type="button" class="btn btn-success mygrouplist all" style="display: none;">모두보기</button>
			</div>
			<div class="col-lg-2 col-md-2 col-sm-3 my-4 category" >
				<button type="button" class="btn btn-info recomandBtn">추천 받기</button>
			</div>
			<div class="col-lg-2 col-md-2 col-sm-3 my-4 category">
				<button type="button" class="btn btn-primary pagebtn" data-page="create">모임 만들기</button>
			</div>
			</c:if>
			
			<div class="dropdown col-lg-2 col-md-2 col-sm-3 my-4 category" id="cateDropdown">
				<input type="hidden" value="0" id="categoryFilter">
				<button type="button" class="btn btn-primary dropdown-toggle"
					data-toggle="dropdown">카테고리</button>
				<div class="dropdown-menu">
					<a class="dropdown-item" role="0">전체</a> 
					<a class="dropdown-item" role="1">스터디</a> 
					<a class="dropdown-item" role="2">취미</a> 
					<a class="dropdown-item" role="3">친목도모</a>
					<a class="dropdown-item" role="4">맛집탐방</a>
				</div>
			</div>
			<!-- </div> -->
		</div>
		<!-- Marketing Icons Section -->
		<div class="row group-list">
			
			<!-- 모임 출력 시작 -->
			
			<!-- 모임 출력 종료 -->
			
		</div>
	</div>
</section>

<!-- /.container -->

<%@ include file="/WEB-INF/views/commons/template/modern_business_bottom.jsp"%>
