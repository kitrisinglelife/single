package com.kitri.single.member.controller;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.mail.Session;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitri.single.member.service.MemberService;
import com.kitri.single.sns.model.SnsDto;
import com.kitri.single.user.model.UserDto;
import com.kitri.single.user.service.UserService;
import com.kitri.single.user.service.UserServiceImpl;

//Controller는 요청과 응답을 처리한다.
@Controller
@RequestMapping("/member")
@SessionAttributes("userInfo")
public class MemberController {
	private Logger logger = LoggerFactory.getLogger(MemberController.class);
	
	@Autowired
	MemberService memberService;	
	
	// 회원가입페이지이동
//	@RequestMapping(value = "/register", method = RequestMethod.GET)
//	public String register() {
//		return "member/register/register";
//	}


			
	// 회원가입
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(UserDto userDto, Model model 
			, String snsId, String snsType, String snsToken, String snsConnectDate
			, HttpServletRequest request) {
		logger.info(">>>>>register UserDto >>>" +userDto.toString());

		UserDto oldUser = memberService.getUser(userDto); //이메일 인증당시 이메일을 생성한상태
		
		if(snsId != null && !"".equals(snsId)) {  //sns계정으로 가입
			logger.info(">>>>>sns계정으로 가입");
			logger.info(">>>>>register>>>>: snsid:" +snsId+"snsType: " +snsType+"snsToken: "+snsToken+"snsConnectDate: "+snsConnectDate);
			logger.info(userDto.toString());
			logger.info(">>>>>>>>>>>>>>>>>");
			
			userDto.setAuthKey("confirmed");
			userDto.setAuthState("1");
			
			SnsDto snsDto = new SnsDto();
			snsDto.setSnsId(snsId);
			snsDto.setSnsType(snsType);
			snsDto.setSnsToken(snsToken);
			snsDto.setSnsConnectDate(snsConnectDate); // 월 일 년
			snsDto.setUserId(userDto.getUserId());
			
			userDto.setSnsDto(snsDto);
			userDto.setUserStatecode("1");			
			logger.info(userDto.getSnsDto().toString());
			memberService.registSns(userDto);
//			model.addAttribute("userInfo", userDto);
			WebUtils.setSessionAttribute(request, "userInfo", userDto ); //리다이렉트시 세션은 이렇게 담아준다.
			
		}
		else if(oldUser.getAuthState().equals("1")){ //이메일 인증이되어 있을경우 =>일반회원가입 ()
			logger.info(">>>>>일반회원가입");
			logger.info(userDto.toString());
			logger.info(">>>>>>>>>>>>>>>>>");
			userDto.setUserStatecode("1");
			
//			SnsDto snsDto = new SnsDto();
//			userDto.setSnsDto(snsDto);
			memberService.registCommon(userDto);
			logger.info(">>>>>>>>>>>>>>>>> 일반회원가입 세션에 넣어주는 userDto: "+userDto.toString());
			
			WebUtils.setSessionAttribute(request, "userInfo", userDto ); //리다이렉트시 세션은 이렇게 담아준다.
//			model.addAttribute("userInfo", userDto);
		}
		return "redirect:/index.jsp";
	}
	
	// 소셜정보와함께 회원가입페이지로이동
	@RequestMapping(value = "/registersns", method = RequestMethod.POST)
	public String registersns(UserDto userDto, Model model 
			, String snsId, String snsType, String snsToken, String snsConnectDate
			, HttpServletRequest request){
		logger.debug(">>>>>>registersns"+ userDto.toString());
		
		SnsDto snsDto = new SnsDto();
		
		snsDto.setSnsId(snsId);
		snsDto.setSnsType(snsType);
		snsDto.setSnsToken(snsToken);
		snsDto.setSnsConnectDate(snsConnectDate);
		userDto.setSnsDto(snsDto);
		
//		request.setAttribute("userInfo", userDto);
		request.setAttribute("paramUserDto", userDto);
		
		return "member/register/register";
	}
	
	// 로그인 페이지
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "member/login/loginpage";
	}
	
	// 로그인
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public String login(UserDto userDto, Model model) {
		logger.debug("login>>>" +userDto.toString());
		String inputId= userDto.getUserId().trim();
		String inputPass= userDto.getUserPassword().trim();
//		System.out.println("login>>>" +userDto.toString());
		userDto = memberService.login(userDto);

		if(userDto != null 
				&& userDto.getUserId().equals(inputId)
				&& userDto.getUserPassword().equals(inputPass)
				&& userDto.getUserStatecode().equals("1")) {
			//로그인 성공
			model.addAttribute("userInfo", userDto);
			return "{\"msg\":\"1\"}";
		}else if(userDto == null ) {
			//아이디가 존재하지 않는 회원입니다.
			model.addAttribute("userInfo", null);
			return "{\"msg\":\"11\"}";
		}
		else if(userDto != null 
				&& userDto.getUserId().equals(inputId)
				&& !userDto.getUserPassword().equals(inputPass)
				&& userDto.getUserStatecode().equals("1")) {
			//비밀번호가 틀렸습니다.
			model.addAttribute("userInfo", null);
			return "{\"msg\":\"12\"}";
		}else if(userDto != null
				&&userDto.getUserStatecode().equals("0")) {
			//탈퇴한 회원입니다.
			model.addAttribute("userInfo", userDto);
			return "{\"msg\":\"13\"}";
		}
		
		//알수 없는 에러입니다.
		return "{\"msg\":\"99\"}";
	}
	
	// 로그아웃
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseBody
	public String logout(SessionStatus status, HttpServletRequest request, HttpSession session) {
//		String oldUrl = request.getHeader("REFERER");
//		logger.info(oldUrl);
		
		status.setComplete();
		// 이렇게 나와야하는데 "redirect:/index_logintest.jsp";
		// 이렇게 나와야하는데 "redirect:/http://localhost/single/index_logintest.jsp 이렇게되어서 문제
		
		//ajax 호출부분에서 location.reload();가 필요하다
		String json ="{'msg':'1'}";
		return json;
	}

	// 이메일 인증페이지 이동
	@RequestMapping(value = "/emailauth", method = RequestMethod.GET)
	public String emailauth() throws Exception {
		return "member/emailauth/emailauth";
	}
	
	@RequestMapping(value = "/emailpassfind", method = RequestMethod.GET)
	public String emailpassfind(UserDto userDto, Model model) {
		return "member/emailauth/emailpassfind";
	}
	
	// 메일인증 
	@ResponseBody
	@RequestMapping(value = "/sendemail", method = RequestMethod.POST)
	public String sendemail(@RequestBody UserDto userDto,Model model) throws Exception {
		String email = userDto.getUserId();
		logger.info("sendemail>>>>: "+email);

		Map<String,Object > map = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		
		userDto = memberService.getUser(userDto);
		if(userDto  != null && userDto.getUserStatecode().equals("1")) {
			//이미 회원가입한 상태
			map.put("msgcode", "1");
			map.put("userDto", userDto);
			logger.info("userDto>>>>: "+userDto.toString());
		}
//		else if(userDto != null &&userDto.getAuthState().equals("1")) {
//			//이미 이메일 인증된 상태
//			map.put("msgcode", "2");
//			map.put("userDto", userDto);
//			logger.info("userDto>>>>: "+userDto.toString());
//		}
		else {
			//이메일 인증을 보냈다.
			userDto= new UserDto();
			userDto.setUserId(email);
			userDto = memberService.sendAuthMail(userDto);
			map.put("msgcode", "3");
			map.put("userDto", userDto);
		}
		String json = mapper.writeValueAsString(map);
		return json;
	}

	// 인증키 확인
	@RequestMapping(value = "/authconfirm", method = RequestMethod.GET)
	public String authconfirm(String email, String authKey,Model model) throws Exception {
//		String email = userDto.getUserId();
//		String authKey = userDto.getAuthKey();
		logger.info("email:" + email);
		logger.info("authkey:" + authKey);
		UserDto userDto =new UserDto();
		userDto.setUserId(email);
		userDto.setAuthKey(authKey);
		userDto.setAuthState("1");

		// 이메일 유효상태
		memberService.updateAuthstatus(userDto);

//		model.addAttribute("userInfo", userDto);
		model.addAttribute("paramUserDto", userDto);
		return "member/register/register";
	}

//	@ResponseBody
//	@RequestMapping(value = "/authconfirm", method = RequestMethod.GET)
//	public String emailConfirm(@RequestBody UserDto userDto,Model model) throws Exception {
//		String email = userDto.getUserId();
//		String authKey = userDto.getAuthKey();
//		logger.info("email:" + email);
//		logger.info("authkey:" + authKey);
//
//		userDto.setUserId(email);
//		userDto.setAuthKey(authKey);
//		userDto.setAuthState("1");
//
//		// 이메일 유효상태
//		memberService.updateAuthstatus(userDto);
//
//		model.addAttribute("userInfo", userDto);
//		return "member/emailauthok";
//	}
	

}
