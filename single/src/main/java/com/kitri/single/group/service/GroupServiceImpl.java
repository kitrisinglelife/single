package com.kitri.single.group.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitri.single.group.dao.GroupDao;
import com.kitri.single.group.dao.RecomendDao;
import com.kitri.single.group.model.CalendarDto;
import com.kitri.single.group.model.GroupDto;
import com.kitri.single.group.model.GroupMemberDto;
import com.kitri.single.hashtag.dao.HashtagDao;
import com.kitri.single.hashtag.model.HashtagDto;
import com.kitri.single.user.model.UserDto;
import com.kitri.single.util.NumberCheck;
import com.kitri.single.util.Pagination;
import com.kitri.single.util.SiteConstance;

@Service
public class GroupServiceImpl implements GroupService {
	
	@Autowired
	private SqlSession sqlSession;
	
	//로그
	private static final Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);
	
	@Override
	public Pagination<GroupDto> getGroupList(Map<String, String> parameter) {
		GroupDao groupDao = sqlSession.getMapper(GroupDao.class);
		
		int page = Integer.parseInt(parameter.get("page"));
		int groupSize = SiteConstance.GROUP_SIZE;
				
		int endRow = page * groupSize;
		int startRow = endRow - groupSize;
		
		parameter.put("endRow", endRow + "");
		parameter.put("startRow", startRow + "");
		
		List<GroupDto> list = null;
		int totalPageCount = 0;
		int totalListCount = 0;
		if("yes".equals(parameter.get("isMyGroup"))) {
			list = groupDao.getMyGroupList(parameter);
			totalPageCount = 1;
			totalListCount = list.size();
		}else {
			list = groupDao.getGroupList(parameter);
			totalListCount = groupDao.selectGroupCount(parameter);
			totalPageCount = (totalListCount - 1) / groupSize + 1;
		}
		
		Pagination<GroupDto> pagination = new Pagination<GroupDto>();
		pagination.setCurrentPage(page);
		pagination.setTotalPageCount(totalPageCount);
		pagination.setTotalListCount(totalListCount);
		pagination.setList(list);
		
		
		//System.out.println(list);
		//JSONObject jsonObject = new JSONObject();
		//JSONArray jsonArray = new JSONArray(list);
		//jsonObject.put("groupList", jsonArray);
		//jsonObject.put("pagination", new JSONObject(pagination));
		return pagination;
	}

	@Override
	@Transactional
	public GroupDto getGroup(int groupNum) {
		//그룹 정보 가져오기
		GroupDto groupDto = sqlSession.getMapper(GroupDao.class).getGroup(groupNum);
		
		
		if(groupDto != null) {
			//해시태그 정보
			Map<String, Integer> parameter = new HashMap<String, Integer>();
			parameter.put("tagType", 2);
			parameter.put("groupNum", groupNum);
			List<String> tagList = sqlSession.getMapper(HashtagDao.class).getHashtagList(parameter);
			groupDto.setHashtagList(tagList);
			//groupDto.setGroupDescription(groupDto.getGroupDescription().replace("\n", "<br>"));
		}
		
		return groupDto;
	}

	@Override
	@Transactional
	public String createGroup(GroupDto groupDto, UserDto userInfo, String groupHashtag) {
		GroupDao groupDao = (GroupDao)sqlSession.getMapper(GroupDao.class);
		int groupNum = groupDao.selectGroupNumSeq();
		groupDto.setGroupNum(groupNum);
		//groupDto.setGroupDescription(groupDto.getGroupDescription().replace("\n", "<br>"));
		groupNum = groupDao.insertGroup(groupDto);
		
		String[] hashtags = null;
		List<String> hashtagList = new ArrayList<String>();
		if(groupHashtag != null) {
			hashtags = groupHashtag.split("#");
			for(int i=0; i<hashtags.length ; i++) {
				if(hashtags[i] != null && hashtags[i].length() != 0) {
					hashtagList.add(hashtags[i].trim());
				}
			}
			for(int i=0 ; i<hashtagList.size(); i++) {
				HashtagDto hashtagDto = new HashtagDto();
				hashtagDto.setHashtagContent(hashtagList.get(i));
				hashtagDto.setHashtagTypeNum(2);
				hashtagDto.setGroupNum(groupDto.getGroupNum());
				groupDao.insertHashtag(hashtagDto);
			}
		}
		
		GroupMemberDto groupMemberDto = new GroupMemberDto();
		groupMemberDto.setGroupNum(groupDto.getGroupNum());
		groupMemberDto.setUserId(userInfo.getUserId());
		groupMemberDto.setGroupMemberStatecode("L");
		System.out.println(groupMemberDto);
		groupDao.insertGroupMember(groupMemberDto);
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("groupNum", (groupNum != 0 ? groupDto.getGroupNum() : 0));
		
		return jsonObject.toString();
	}

	@Override
	public GroupMemberDto getGroupMember(Map<String, Object> parameter) {
		return sqlSession.getMapper(GroupDao.class).selectGroupMember(parameter);
	}


	@Override
	public int getGroupConunt(Map<String, String> parameter) {
		return sqlSession.getMapper(GroupDao.class).getGroupConunt(parameter);
	}

	@Override
	public String createCalendar(CalendarDto calendarDto) {
		GroupDao groupDao = (GroupDao)sqlSession.getMapper(GroupDao.class);
		int calendarNum = groupDao.selectCalendarSeq();
		calendarDto.setCalendarNum(calendarNum);
		
		groupDao.insertCalendar(calendarDto);
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("resultCode", 1);
		JSONObject calendarJSON = new JSONObject(calendarDto);
		jsonObject.put("resultData", calendarJSON);
		
		return jsonObject.toString();
	}

	@Override
	public String getCalendar(Map<String, String> parameter) {
		
		CalendarDto calendarDto = sqlSession.getMapper(GroupDao.class).selectCalendar(parameter);
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("resultCode", 1);
		jsonObject.put("resultData", new JSONObject(calendarDto));
		
		return jsonObject.toString();
	}
	
	@Override
	public String getCalendarList(Map<String, String> parameter) {
		List<CalendarDto> list = sqlSession.getMapper(GroupDao.class).selectCalendarList(parameter);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("resultCode", 1);
		jsonObject.put("resultData", new JSONArray(list));
		
		return jsonObject.toString();
	}
	@Override
	public String modifyCalendar(CalendarDto calendarDto) {
		sqlSession.getMapper(GroupDao.class).updateCalendar(calendarDto);
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("resultCode", 2);
		jsonObject.put("resultData", new JSONObject(calendarDto));
		return jsonObject.toString();
	}
	
	@Override
	public String deleteCalendar(CalendarDto calendarDto) {
		sqlSession.getMapper(GroupDao.class).deleteCalendar(calendarDto.getCalendarNum());
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("resultCode", 3);
		jsonObject.put("resultData", new JSONObject(calendarDto));
		return jsonObject.toString();
	}
	
	@Override
	@Transactional
	public String groupModify(GroupDto groupDto, String groupHashtag) {
		
		GroupDao groupDao = sqlSession.getMapper(GroupDao.class);
		
		//해시태그 삭제
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("hashtagTypeNum", 2);
		parameter.put("groupNum", groupDto.getGroupNum());
		groupDao.deleteHashtag(parameter);
		
		//해시태그 인설트
		String[] hashtags = null;
		List<String> hashtagList = new ArrayList<String>();
		if(groupHashtag != null) {
			hashtags = groupHashtag.split("#");
			for(int i=0; i<hashtags.length ; i++) {
				if(hashtags[i] != null && hashtags[i].length() != 0) {
					hashtagList.add(hashtags[i].trim());
				}
			}
			for(int i=0 ; i<hashtagList.size(); i++) {
				HashtagDto hashtagDto = new HashtagDto();
				hashtagDto.setHashtagContent(hashtagList.get(i));
				hashtagDto.setHashtagTypeNum(2);
				hashtagDto.setGroupNum(groupDto.getGroupNum());
				groupDao.insertHashtag(hashtagDto);
			}
		}
		//그룹 업데이트
		groupDao.updateGroup(groupDto);

		return makeJSON(1, groupDto);
	}
	
	@Override
	@Transactional
	public String groupStamp(String userId, int groupNum) {
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("userId", userId);
		parameter.put("groupNum", groupNum);
		GroupDao groupDao = sqlSession.getMapper(GroupDao.class);
		if(groupDao.countGroupStamp(parameter) == 0) {
			groupDao.insertGroupStamp(parameter);
		}else {
			return makeJSON(2, "이미 찜한 모임입니다");
		}
		
		return makeJSON(1, "모임을 찜했습니다. 나의 찜목록 페이지에서 확인하세요");
	}

	@Override
	public void insertGroupMember(GroupMemberDto groupMemberDto) {
		sqlSession.getMapper(GroupDao.class).insertGroupMember(groupMemberDto);
	}
	
	@Override
	@Transactional
	public void changeGroupLeader(Map<String, String> parameter) {
		// TODO Auto-generated method stub
		//리더 변경
		//대상자 리더 만들기
		GroupMemberDto groupMemberDto = new GroupMemberDto();
		groupMemberDto.setGroupNum(Integer.parseInt(parameter.get("groupNum")));
		groupMemberDto.setGroupMemberStatecode("L");
		groupMemberDto.setUserId(parameter.get("targetId"));	
		sqlSession.getMapper(GroupDao.class).updateGroupMember(groupMemberDto);
		
		//로그인 사용자 일반모임원 만들기
		groupMemberDto.setGroupMemberStatecode("M");
		groupMemberDto.setUserId(parameter.get("userId"));
		sqlSession.getMapper(GroupDao.class).updateGroupMember(groupMemberDto);
	}
	
	@Override
	@Transactional
	public void fireGroupMember(GroupMemberDto groupMemberDto) {
		GroupDao groupDao = sqlSession.getMapper(GroupDao.class);
		
		groupDao.updateGroupMember(groupMemberDto);
		//그룹 멤버 숫자 내리기
		groupDao.groupMemberCountDown(groupMemberDto.getGroupNum());
	}
	
	@Override
	@Transactional
	public int applyokGroupMember(Map<String, String> parameter) {
		GroupDao groupDao = sqlSession.getMapper(GroupDao.class);
		int result = groupDao.groupMemberCountUp(Integer.parseInt(parameter.get("groupNum")));
		if(result == 0) {
			return result;
		}else {
			//가입승인
			GroupMemberDto groupMemberDto = new GroupMemberDto();
			groupMemberDto.setGroupNum(Integer.parseInt(parameter.get("groupNum")));
			groupMemberDto.setGroupMemberStatecode("M");
			groupMemberDto.setUserId(parameter.get("targetId"));		
			groupDao.updateGroupMember(groupMemberDto);
			
			//승인시 찜목록에서 삭제
			groupDao.deleteGroupStamp(groupMemberDto);
		}
		return result;
	}
	
	@Override
	public void applynoGroupMember(GroupMemberDto groupMemberDto) {
		sqlSession.getMapper(GroupDao.class).deleteGroupMember(groupMemberDto);
	}
	
	@Transactional
	private void groupMemberCountDown(int groupNum) {
		sqlSession.getMapper(GroupDao.class).groupMemberCountDown(groupNum);
	}
	
	@Transactional
	private int groupMemberCountUp(int groupNum) {
		return sqlSession.getMapper(GroupDao.class).groupMemberCountUp(groupNum);
	}


	@Override
	public List<GroupMemberDto> getGroupMemberList(int groupNum) {
		return sqlSession.getMapper(GroupDao.class).getGroupMemberList(groupNum);
	}
	
	@Override
	public String groupDelete(int groupNum) {
		//그룹 삭제
		GroupDto groupDto = new GroupDto();
		groupDto.setGroupNum(groupNum);
		groupDto.setGroupStatecode(99);
		groupDto.setGroupMemberCount(0);
		GroupDao groupDao = sqlSession.getMapper(GroupDao.class);
		
		//그룹원 전체 D 상태로 변경
		groupDao.updateGroup(groupDto);
		GroupMemberDto groupMemberDto = new GroupMemberDto();
		groupMemberDto.setGroupNum(groupNum);
		groupMemberDto.setGroupMemberStatecode("D");
		groupDao.updateGroupMember(groupMemberDto);
		return makeJSON(1, "정상적으로 모임이 삭제되었습니다");
	}
	
	//추천
	@Override
	public List<GroupDto> getRecommendGroupList(Map<String, Object> parameter) {
		UserDto userDto = (UserDto)(parameter.get("userDto"));
		
		
		int page = NumberCheck.NotNumberToOne((String)parameter.get("page"));
		int groupSize = SiteConstance.GROUP_SIZE;
				
		int endRow = page * groupSize;
		int startRow = endRow - groupSize;
		
		parameter.put("endRow", endRow + "");
		parameter.put("startRow", startRow + "");
		
		//logger.debug(">>>>>"+userDto.toString());
		List<String> list = (List<String>)sqlSession.getMapper(RecomendDao.class).getRecommendTagList(userDto);
		
		List<String> parseStrtag = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<list.size(); i++) {
			String str = list.get(i);
			if(str==null) {
				continue;
			}
			int len= str.length();
//			logger.debug(">>>>>"+str+" len:"+len);
			if(len <= 2) {
//				logger.debug(">>>>>add1");		
//				parseStrtag.add(str);
				sb.append("|"+str);
			}else {
//				logger.debug(">>>>>add2");
				for(int j =len; j>=2;j--) {
//					parseStrtag.add(str.substring(0, j));
					sb.append("|"+str.substring(0, j));
				}	
			}
		}
		if(sb.length() != 0) {
			sb.deleteCharAt(0);
		}
		String parseTagStr= sb.toString();
		logger.debug(">>>>>태그 파싱 후 "+parseTagStr);
	
//		logger.debug(">>>>>파싱 후 ");
//		for(String str : parseStrtag) {
//			logger.debug(">>>>>"+str);
//		}
		List<String>resultList = (List<String>)sqlSession.getMapper(RecomendDao.class).getResultList(parseTagStr);
		logger.debug(">>>>>결과값 개수:"+ (resultList.equals(null)?"null":resultList.size()) );	
		for(String groupNum : resultList) {
			if(groupNum!= null) {
				logger.debug(">>>>>결과 그룹 번호: "+groupNum);	
			}
		}
		
//		Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
//		paramMap.put("list", resultList);
		List<GroupDto> groupRecommendList = new ArrayList<GroupDto>();
		if(resultList.size()!=0) {
			groupRecommendList = (List<GroupDto>)sqlSession.getMapper(RecomendDao.class).getGroupDtoList(resultList);	
		}else if( groupRecommendList.size()==0 ) {
			groupRecommendList = (List<GroupDto>)sqlSession.getMapper(RecomendDao.class).getSingleRecommendList();
//			groupRecommendList =(List<GroupDto>)sqlSession.getMapper(RecomendDao.class).getGroupDtoList(resultList);
		}
		
		if(groupRecommendList.size()>=20) {
			groupRecommendList= groupRecommendList.subList(0, 19);
		}
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray(list);
		jsonObject.put("groupList", jsonArray);
		return groupRecommendList;
	}
	
	
	
	public String makeJSON(int resultCode, Object resultData) {
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("resultCode", resultCode);
		if(resultData instanceof Collection) {
			jsonObject.put("resultData", new JSONArray(resultData));
		}else if(resultData instanceof String) {
			jsonObject.put("resultData", resultData.toString());
		}else {
			jsonObject.put("resultData", new JSONObject(resultData));
		}
		
		return jsonObject.toString();
		
	}
}
