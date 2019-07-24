package com.kitri.single.group.contorller;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.kitri.single.group.model.CalendarDto;
import com.kitri.single.group.model.GroupDto;
import com.kitri.single.group.model.GroupMemberDto;
import com.kitri.single.group.model.HomeworkDto;
import com.kitri.single.group.service.GroupService;
import com.kitri.single.group.service.HomeworkService;
import com.kitri.single.user.model.UserDto;
import com.kitri.single.util.Pagination;
import com.kitri.single.util.SiteConstance;
import com.kitri.single.util.Utill;

@Controller
@RequestMapping("/group")
@SessionAttributes(names = {"userInfo"})
public class GroupController {
	
	//서비스 부분
	@Autowired
	private GroupService groupService;
	
	@Autowired
	private HomeworkService homeworkService;
	
	@Autowired
	private ServletContext servletContext;
	
	//로그
	private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
	
	//첫페이지용
	@RequestMapping(method = RequestMethod.GET)
	public String group(Model model, HttpSession session) {
		UserDto userInfo = (UserDto) session.getAttribute("userInfo");
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("page", "1");
		if(userInfo != null) {
			parameter.put("userId", userInfo.getUserId());
		}else {
			parameter.put("userId", "");
		}
		parameter.put("key", "");
		parameter.put("word", "");
		parameter.put("isMyGroup", "");
		parameter.put("groupCategoryNum", "0");
		
		//System.out.println(parameter);
		int size = groupService.getGroupConunt(parameter);
		//System.out.println("size : " + size);
		
		
		model.addAttribute("parameter", parameter);
		model.addAttribute("size", size);
		
		return "group/grouplist";
	}
	
	@RequestMapping(value = "/grouplist", method = RequestMethod.GET)
	public String getGroupList(@RequestParam Map<String, String> parameter, HttpSession session, Model model) {
		UserDto user = (UserDto)session.getAttribute("userInfo");
		if(user != null) {
			parameter.put("userId", user.getUserId());
		}else {
			parameter.put("userId", "");
		}
		
		//System.out.println(parameter);
		logger.info(parameter.toString());
		
		Pagination<GroupDto> pagination = groupService.getGroupList(parameter);
		model.addAttribute("parameter", parameter);
		//model.addAttribute("groupList", pagination);
		model.addAttribute("pagination", pagination);
		
		//System.out.println(json);
		
		return "group/grouplistresult";
	}
	
	@RequestMapping(value = "/groupdetail", method = RequestMethod.GET)
	public @ResponseBody String getGroup(@RequestParam(name = "groupNum") int groupNum){
		//String json = groupService.getGrou(parameter);
		//System.out.println(groupNum);
		GroupDto groupDto = groupService.getGroup(groupNum);
		
		JSONObject jsonObject = new JSONObject();
		
		JSONObject groupJson = new JSONObject(groupDto);
		jsonObject.put("group", groupJson);
		jsonObject.put("taglist", groupDto.getHashtagList());
		
		return jsonObject.toString();
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public void groupCreate(@ModelAttribute("userInfo") UserDto userInfo) {
		
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public @ResponseBody String groupCreate(
			GroupDto groupDto
			, HttpSession session
			, @RequestParam("imgdata") MultipartFile multipartFile
			, @RequestParam("groupHashtag") String groupHashtag) {
		
		//System.out.println(groupDto);
		//System.out.println(multipartFile);
		//System.out.println(multipartFile.isEmpty());
		UserDto userInfo = (UserDto)session.getAttribute("userInfo");
		if(userInfo == null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("resultCode", 99);
			jsonObject.put("resultData", "로그인이 필요한 기능입니다.");
			return jsonObject.toString();
		}
		
		if(multipartFile != null && !multipartFile.isEmpty()) {
			
			String realPath = servletContext.getRealPath("");
			
			String src = "";
			try {
				src = Utill.profileUpload(multipartFile, "group", realPath, servletContext.getContextPath());
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			groupDto.setGroupImgSrc(src);
		}else {
			int groupCategoryNum = groupDto.getGroupCategoryNum();
			groupDto.setGroupImgSrc(servletContext.getContextPath() + SiteConstance.GROUP_DEFAULT_IMG_PATH +  groupCategoryNum + "_default.jpg");
		}
		groupDto.setGroupMemberCount(1);
		//서비스 시작...
		String json = groupService.createGroup(groupDto, userInfo, groupHashtag);
		
		//System.out.println(json);
	
		return json;

	}
	
	@RequestMapping("/{groupNum}")
	public String groupMain(@ModelAttribute("userInfo") UserDto userInfo
			, @PathVariable(name = "groupNum") int groupNum
			, Model model) {
		
		//그룹 멤버인지 확인
		String path = "group/grouplist";
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("userId", userInfo.getUserId());
		parameter.put("groupNum", groupNum);
		GroupMemberDto groupMemberDto = groupService.getGroupMember(parameter);
		
		//멤버의 상태에 따른 결정
		// L : 그룹장 Leader   
		// M : 그룹원 Member
		// W : 승인대기 Waiting
		if(groupMemberDto == null) {
			model.addAttribute("enteredGroupFail", "존재하지 않는 모임이거나 가입요청을 하지 않은 모임입니다.");
			path = "group/grouplist";
		}else if(groupMemberDto.getGroupMemberStatecode().equals("W")) {
			model.addAttribute("enteredGroupFail", "아직 모임장이 가입을 승인하지 않았습니다.");
			path = "group/grouplist";
		}else {
			GroupDto groupDto = groupService.getGroup(groupNum);
			if(groupDto == null) {
				model.addAttribute("enteredGroupFail", "모임이 존재하지 않습니다.");
				path = "group/grouplist";
			}else {
				model.addAttribute("group", groupDto);
				model.addAttribute("groupMember", groupMemberDto);
				model.addAttribute("serverIP", SiteConstance.CHAT_SERVER_IP);
				path = "group/groupentered/groupmain";
				
				//logger.info(groupMemberDto.toString());
				//logger.info(groupDto.toString());
			}	
		}
		
		//System.out.println(groupMemberDto);
		
		
		//그룹 멤버라면 그룹멤버정보및 그룹 메인화면 정보를 가져옴
		
		return path;
	}
	
	@ResponseBody
	@RequestMapping(value = "/groupmodify" , method = RequestMethod.POST)
	public String groupmodify(HttpSession session
			, GroupDto groupDto
			, @RequestParam("imgdata") MultipartFile multipartFile
			, @RequestParam("groupHashtag") String groupHashtag) {
		
		UserDto userInfo = (UserDto)session.getAttribute("userInfo");
		if(userInfo == null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("resultCode", 99);
			jsonObject.put("resultData", "로그인이 필요한 기능입니다.");
			return jsonObject.toString();
		}
		
		String json = makeJSON(0, "권한이 없습니다");
		
		if("L".equals(getGroupMemberStatecode(userInfo.getUserId(), groupDto.getGroupNum()))) {
			//파일정보 세팅
			if(multipartFile != null && !multipartFile.isEmpty()) {
				String realPath = servletContext.getRealPath("");
				
				String src = "";
				try {
					src = Utill.profileUpload(multipartFile, "group", realPath, servletContext.getContextPath());
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				groupDto.setGroupImgSrc(src);
			}
		
			//System.out.println(groupDto);
			json = groupService.groupModify(groupDto, groupHashtag);
		}		
		return json;
	}
	
	@ResponseBody
	@RequestMapping("/groupstamp")
	public String groupStamp(HttpSession session
				, @RequestParam("groupNum") int groupNum) {
		//logger.info("groupNum : " + groupNum);
		UserDto userInfo = (UserDto)session.getAttribute("userInfo");
		if(userInfo == null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("resultCode", 99);
			jsonObject.put("resultData", "로그인이 필요한 기능입니다.");
			return jsonObject.toString();
		}
		
		String json = makeJSON(0, "찜하기 실패하였습니다. 관리자에게 문의하세요");
		if(groupNum != 0) {
			json = groupService.groupStamp(userInfo.getUserId(), groupNum);
		}
		//logger.info(json);
		return json;
	}
	
	@ResponseBody
	@RequestMapping("/memberstate")
	public String getMemberStatecode(HttpSession session
			, @RequestParam("groupNum") int groupNum) {
		String json = makeJSON(0, "시스템 에러");
		
		UserDto userInfo = (UserDto)session.getAttribute("userInfo");
		if(userInfo == null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("resultCode", 99);
			jsonObject.put("resultData", "로그인이 필요한 기능입니다.");
			return jsonObject.toString();
		}
		
		String memberStatecode = getGroupMemberStatecode(userInfo.getUserId(), groupNum);
		
		if(memberStatecode != null) {
			if(memberStatecode.equals("L")) {
				json = makeJSON(1, memberStatecode);
			}else {
				json = makeJSON(1, memberStatecode);
			}	
		}
		
		return json;
	}
	
	@ResponseBody
	@RequestMapping("/groupmember")
	public String groupMember(HttpSession session, @RequestParam Map<String, String> parameter) {
			
		UserDto userDto = (UserDto)session.getAttribute("userInfo");
		String json = makeJSON(0, "시스템 에러입니다");
		if(userDto == null) {
			json = makeJSON(99, "로그인이 필요한 기능입니다");
		}else {
			parameter.put("userId", userDto.getUserId());
			String type = parameter.get("type");
			
			if(type != null) {
				GroupMemberDto groupMemberDto = new GroupMemberDto();
				groupMemberDto.setGroupNum(Integer.parseInt(parameter.get("groupNum")));
				if(type.equals("apply")) {
					//가입요청
					groupMemberDto.setGroupMemberStatecode("W");
					groupMemberDto.setUserId(parameter.get("userId"));
					groupService.insertGroupMember(groupMemberDto);

					json = makeJSON(1, "모임에 가입신청 되었습니다. 모임장이 승인해야 가입이 완료됩니다.");
				}else if(type.equals("leader")) {
					
					groupService.changeGroupLeader(parameter);
					
					json = makeJSON(2, parameter.get("userNickname") + " 님으로 모임장이 변경되었습니다");
					
				}else if(type.equals("fire")) {
					//퇴출
					groupMemberDto.setGroupMemberStatecode("D");
					groupMemberDto.setUserId(parameter.get("targetId"));				
					
					groupService.fireGroupMember(groupMemberDto);
					
					json = makeJSON(3, parameter.get("userNickname") + " 님을 모임에서 퇴출하였습니다");
					
				}else if(type.equals("applyok")) {
					//그룹 멤버 숫자 확인하기
					int result = groupService.applyokGroupMember(parameter);
					if(result == 0) {
						json = makeJSON(0, "모임의 인원수가 가득 찼습니다. 인원제한을 변경하세요");
					}else {
						json = makeJSON(4, parameter.get("userNickname") + " 님을 가입승인 하였습니다");
					}
				}else if(type.equals("applyno")) {
					//가입 승인 거절
					groupMemberDto.setUserId(parameter.get("targetId"));	
					groupService.applynoGroupMember(groupMemberDto);
					
					json = makeJSON(5, parameter.get("userNickname") + " 님의 가입승인을 거절하였습니다");
				}
			}
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping("/groupdelete")
	public String groupDelete(HttpSession session
			, @RequestParam("groupNum") int groupNum) {
		String json = makeJSON(0, "시스템 에러입니다");
		UserDto userInfo = (UserDto)session.getAttribute("userInfo");
		if(userInfo == null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("resultCode", 99);
			jsonObject.put("resultData", "로그인이 필요한 기능입니다.");
			return jsonObject.toString();
		}
		
		String memberStatecode = getGroupMemberStatecode(userInfo.getUserId(), groupNum);

		if(memberStatecode != null && memberStatecode.equals("L")) {
			json = groupService.groupDelete(groupNum);
		}else {
			json = makeJSON(0, "권한이 없습니다");
		}
		
		return json;
	}
	
	//추천용 테스트 jsp
	/*
	 * @RequestMapping(value = "/grouprecommend", method = RequestMethod.GET) public
	 * String groupRecommend(HttpSession session) {
	 * 
	 * String path = "group/grouprecommend";
	 * 
	 * UserDto userInfo = (UserDto)session.getAttribute("userInfo"); if(userInfo ==
	 * null) { path = "group"; }
	 * 
	 * return path; }
	 */
	
	//추천 알고리즘
	@RequestMapping(value = "/grouprecommend", method = RequestMethod.GET)
	public String grouprecommend(Model model, HttpSession session) {
		UserDto userDto = (UserDto) session.getAttribute("userInfo");
//		UserDto userDto= new UserDto();
//		userDto.setUserId("hth0893@naver.com");
		

	
		
		Map<String, Object> parameter = new HashMap<String, Object>();
		System.out.println();
//		parameter.put("page", "1");
		if(userDto != null) {
			parameter.put("userDto", userDto);
		}else {
			parameter.put("userDto", "");
		}
		model.addAttribute("parameter", parameter);
//		parameter.put("key", "");
//		parameter.put("word", "");
//		parameter.put("isMyGroup", "");
//		parameter.put("groupCategoryNum", "0");
		
		//System.out.println(parameter);
		Map<String, Object> recommendParamMap = new HashMap<String, Object>();
		recommendParamMap.put("page", "1");
		recommendParamMap .put("userDto",userDto);
		List<GroupDto> groupList = groupService.getRecommendGroupList(recommendParamMap);
		//System.out.println("size : " + size);
		//관련해시태그를 찾지 못하였습니다. -> 추천수 가 많은 순서대로 return ;

		

//		model.addAttribute("size", size);
		model.addAttribute("parameter", parameter);
		model.addAttribute("groupList", groupList);
		//return "group2/grouplistresult2";
		return "group/recommendresult";
	}
	
	
	
	//그룹 내 nav바 이동 관련
	////////////////////////////////////////////////////////////////////////////////////////////////////
	@RequestMapping(value = "/grouppage", method = RequestMethod.GET)
	public ModelAndView groupModify(HttpSession session
			, @RequestParam Map<String, String> parameter
			, ModelAndView model) {
		
		UserDto userInfo = (UserDto)session.getAttribute("userInfo");
		if(userInfo == null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("resultCode", 99);
			jsonObject.put("resultData", "로그인이 필요한 기능입니다.");
			model.setViewName("group/main");
			return model;
		}
		logger.info(parameter.toString());
		if(parameter.get("groupNum") != null) {
			int groupNum = Integer.parseInt(parameter.get("groupNum"));
			String type = parameter.get("type");
			if("main".equals(type)) {
				model = mainPage(userInfo, groupNum, model);
			}else if("modify".equals(type)) {
				model = modifyPage(userInfo, groupNum, model);
			}else if("member".equals(type)) {
				model = memberPage(userInfo, groupNum, model);
			}else if("homework".equals(type)) {
				model = homeworkPage(userInfo, groupNum, model);
			}else if("homeworkcreate".equals(type)){
				model = homeworkCreatePage(userInfo, groupNum, model);
			}else {
				model.setViewName("group/groupentered/main");
			}
		}
		model.addObject("root", servletContext.getContextPath());
		return model;
	}
	
	private ModelAndView homeworkCreatePage(UserDto userInfo, int groupNum, ModelAndView model) {
		String path = "group/groupentered/homework/homeworkcreate";
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("userId", userInfo.getUserId());
		parameter.put("groupNum", groupNum);
		GroupMemberDto groupMemberDto = groupService.getGroupMember(parameter);
		
		if(groupMemberDto.getGroupMemberStatecode().equals("L")) {
			model.addObject("groupNum", groupNum);
			model.setViewName(path);
		}
		
		return model;
	}

	private ModelAndView homeworkPage(UserDto userInfo, int groupNum, ModelAndView model) {
		String path = "group/groupentered/homework/grouphomework";
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("userId", userInfo.getUserId());
		parameter.put("groupNum", groupNum);
		GroupMemberDto groupMemberDto = groupService.getGroupMember(parameter);
		
		model.addObject("groupNum", groupNum);
		model.addObject("groupMember", groupMemberDto);
		
		Map<String, String> param = new HashMap<String, String>();
		param.put("groupNum", groupNum + "");
		param.put("userId", userInfo.getUserId());
		param.put("page", "1");
		
		//List<HomeworkDto> list = homeworkService.getHomeworkList(param);
		//model.addObject("homeworkList", list);
		//model.addObject("parameter", param);
		
		model.setViewName(path);
		
		return model;
	}

	private ModelAndView memberPage(UserDto userInfo, int groupNum, ModelAndView model) {
		String path = "group/groupentered/groupmember";
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("userId", userInfo.getUserId());
		parameter.put("groupNum", groupNum);
		GroupMemberDto groupMemberDto = groupService.getGroupMember(parameter);
		
		if(groupMemberDto.getGroupMemberStatecode().equals("L")) {
			
			List<GroupMemberDto> list = groupService.getGroupMemberList(groupNum);
			
			System.out.println(list);
			
			model.addObject("groupNum", groupNum);
			model.addObject("memberlist", list);
			model.setViewName(path);
		}
		
		return model;
	}

	private ModelAndView modifyPage(UserDto userInfo, int groupNum, ModelAndView model) {
		String path = "group/groupentered/groupmodify";
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("userId", userInfo.getUserId());
		parameter.put("groupNum", groupNum);
		GroupMemberDto groupMemberDto = groupService.getGroupMember(parameter);
		
		if(groupMemberDto.getGroupMemberStatecode().equals("L")) {
			GroupDto groupDto = groupService.getGroup(groupNum);
			if(groupDto != null) {
				model.addObject("group", groupDto);
				model.setViewName(path);
			}
			//logger.info(groupDto.toString());
		}
		
		return model;
	}

	public ModelAndView mainPage(UserDto userInfo, int groupNum, ModelAndView model) {
		//그룹 멤버인지 확인
		String path = "group/groupentered/main";
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("userId", userInfo.getUserId());
		parameter.put("groupNum", groupNum);
		GroupMemberDto groupMemberDto = groupService.getGroupMember(parameter);
		
		//멤버의 상태에 따른 결정
		// L : 그룹장 Leader   
		// M : 그룹원 Member
		// W : 승인대기 Waiting

		GroupDto groupDto = groupService.getGroup(groupNum);
		if(groupDto != null) {
			model.addObject("group", groupDto);
			model.addObject("groupMember", groupMemberDto);
		}
		
		model.setViewName(path);
		return model;
	}
	
	public String getGroupMemberStatecode(String userId, int groupNum) {
		
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("userId", userId);
		parameter.put("groupNum", groupNum);
		GroupMemberDto groupMemberDto = groupService.getGroupMember(parameter);
		
		//멤버의 상태에 따른 결정
		// L : 그룹장 Leader   
		// M : 그룹원 Member
		// W : 승인대기 Waiting

		return groupMemberDto.getGroupMemberStatecode();
	}
	
	//json데이터 생성
	public String makeJSON(int resultCode, Object resultData) {
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("resultCode", resultCode);
		if(resultData instanceof Collection) {
			jsonObject.put("resultData", new JSONArray(resultData));
		}else if(resultData instanceof String){
			jsonObject.put("resultData", resultData.toString());
		}else {
			jsonObject.put("resultData", new JSONObject(resultData));
		}
		
		return jsonObject.toString();
		
	}
	
}
