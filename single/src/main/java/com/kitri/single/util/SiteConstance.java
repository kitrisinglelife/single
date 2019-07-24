package com.kitri.single.util;

//사이트내 상수값 정의하는 곳
public class SiteConstance {
	
	//게시판에서 한번에 보여주는 글의 갯수
	public static final int ARTICLE_SIZE = 20;
	//페이지처리시 한번에 보여주는 페이지의 갯수
	public static final int PAGE_SIZE = 10;
	
	//모임리스트에서 한번에 불러오는 모임의 개수
	public static final int GROUP_SIZE = 15;
	
	public static final String GROUP_DEFAULT_IMG_PATH = "/resources/img/group/";

	public static final String USER_DEFAULT_IMG_PATH = "/resources/img/user/";

	public static final String CHAT_SERVER_IP = "ws://localhost:80";
	
	//모임과제에서 한번에 불러오는 과제의 개수
	public static final int GROUP_HOMEWORK_SIZE = 4;
	//그룹 과제 페이지에서의 페이징그룹의 사이즈
	public static final int GROUP_HOMEWORK_PAGE_SIZE = 5;
}
