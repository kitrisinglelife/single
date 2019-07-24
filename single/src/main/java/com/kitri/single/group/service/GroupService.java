package com.kitri.single.group.service;

import java.util.List;
import java.util.Map;

import com.kitri.single.group.model.CalendarDto;
import com.kitri.single.group.model.GroupDto;
import com.kitri.single.group.model.GroupMemberDto;
import com.kitri.single.user.model.UserDto;
import com.kitri.single.util.Pagination;

public interface GroupService {
	//그룹 전체 개수
	public int getGroupConunt(Map<String, String> parameter);
	
	//그룹관련
	public GroupDto getGroup(int groupNum);
	public Pagination<GroupDto> getGroupList(Map<String, String> parameter);
	public String groupModify(GroupDto groupDto, String groupHashtag);
	public String createGroup(GroupDto groupDto, UserDto userInfo, String groupHashtag);
	public String groupDelete(int groupNum);
	
	//추천관련
	public List<GroupDto> getRecommendGroupList(Map<String, Object> parameter);
	
	//그룹멤버 관련
	public GroupMemberDto getGroupMember(Map<String, Object> parameter);
	public List<GroupMemberDto> getGroupMemberList(int groupNum);
	public void insertGroupMember(GroupMemberDto groupMemberDto);
	public void changeGroupLeader(Map<String, String> parameter);
	public void fireGroupMember(GroupMemberDto groupMemberDto);
	public int applyokGroupMember(Map<String, String> parameter);
	public void applynoGroupMember(GroupMemberDto groupMemberDto);
	
	//그룹 일정
	public String createCalendar(CalendarDto calendarDto);
	public String getCalendar(Map<String, String> parameter);
	public String getCalendarList(Map<String, String> parameter);
	public String modifyCalendar(CalendarDto calendarDto);
	public String deleteCalendar(CalendarDto calendarDtol);
	
	//그룹 찜하기
	public String groupStamp(String userId, int groupNum);


	
}
