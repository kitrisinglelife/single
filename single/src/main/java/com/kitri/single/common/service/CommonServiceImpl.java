package com.kitri.single.common.service;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kitri.single.common.dao.CommonDao;

@Service
public class CommonServiceImpl implements CommonService {

	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public int getNextSeq() {
		return sqlSession.getMapper(CommonDao.class).getNextSeq();
	}

//	@Override
//	public PageNavigation getPageNamigation(Map<String, String> parameter) {
//		
//		// new 하기 싫으면 conpornunt? 컨포넌트 하기 controller, service,dao설정 아니면 전부 컨포넌트 
//		PageNavigation navigation = new PageNavigation(); 
//		
//		// bcode없으면 에러남 우리가 생각해보기.
//		int newArticleCount = sqlSession.getMapper(CommonDao.class).getNewArticleCount(Integer.parseInt(parameter.get("bcode")));
//		navigation.setNewArticleCount(newArticleCount);
//		
//		int totalArticleCount = sqlSession.getMapper(CommonDao.class).getTotalArticleCount(parameter);
//		navigation.setTotalArticleCount(totalArticleCount);
//		
//		int totalPageCount = (totalArticleCount - 1) / CafeConstance.ARTICLE_SIZE + 1;
//		navigation.setTotalPageCount(totalPageCount);
//		
//		int pg = NumberCheck.NotNumberToOne(parameter.get("pg"));
//		navigation.setNowFirst(pg <= CafeConstance.PAGE_SIZE);
//		navigation.setNowEnd((totalPageCount - 1) / CafeConstance.PAGE_SIZE * CafeConstance.PAGE_SIZE < pg);
//		navigation.setPageNo(pg);
//		
//		return navigation;
//		
//		
//	}

	
	

}















