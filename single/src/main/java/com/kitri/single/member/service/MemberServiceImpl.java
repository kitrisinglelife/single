package com.kitri.single.member.service;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kitri.single.member.dao.MemberDao;
import com.kitri.single.user.dao.UserDao;
import com.kitri.single.user.model.UserDto;


//DB에서 받은 내용을 가공 전달한다. (처리작업)
@Service
public class MemberServiceImpl implements MemberService {
	Logger logger= LoggerFactory.getLogger(MemberServiceImpl.class);
	
	@Autowired
	private JavaMailSender mailSender;	
	
	@Autowired
	private SqlSession sqlSession;
	
	@Override
	public void registCommon(UserDto userDto) {
		sqlSession.getMapper(MemberDao.class).registCommon(userDto);
	}
	@Override
	public void registSns(UserDto userDto) {
		sqlSession.getMapper(MemberDao.class).registSns(userDto);
	}

	@Override
	@Transactional
	public UserDto sendAuthMail(UserDto userDto) throws MessagingException, UnsupportedEncodingException {
		MemberDao memberDao  = sqlSession.getMapper(MemberDao.class);
		// 임의의 authkey 생성
		String authkey = new TempKey().getKey(50, false);
		String email =userDto.getUserId();
		logger.info("email: "+ email);
//		System.out.println(">>>>>>> parameter userDto  주소:" +userDto); 
		userDto = sqlSession.getMapper(MemberDao.class).getUser(userDto);
//		System.out.println(">>>>>>> sqlsession userDto  주소:" +userDto); sqlSession으로 값을 얻어오면 주소가 바뀐다.
		if(userDto == null) {
			logger.info(" >>>> 유저의 인증키를 생성합니다.");
			userDto = new UserDto();
			userDto.setUserStatecode("0");
			userDto.setUserId(email);
			userDto.setAuthKey(authkey);
			userDto.setAuthState("0");
			memberDao.createAuthkey(userDto);
		}else if(userDto != null &&userDto.getAuthKey() !=null &&"0".equals(userDto.getAuthState())){
//			logger.info("userEamil :"+ userDto.getUserId());
//			logger.info("usertoString :"+ userDto.toString());
			logger.info(">>>> 유저의 인증키를 변경합니다.");
			userDto.setAuthKey(authkey);
			memberDao.updateAuthkey(userDto);
		}
		
		// mail 작성 생성
		MailUtils sendMail = new MailUtils(mailSender);
		sendMail.setSubject("[우리 혼자 살아요] 회원가입 이메일 인증");
		sendMail.setText(new StringBuffer().append("<h1>[이메일 인증]</h1>")
				.append("<p>아래 링크를 클릭하시면 이메일 인증이 완료됩니다.</p>")
				.append("<a href='http://localhost/single/member/authconfirm?email=")
				.append(userDto.getUserId())
				.append("&authKey=")
				.append(authkey)
				.append("' target='_blenk'>이메일 인증 확인</a>")
//				.append("인증키:" + userDto.getAuthKey())
				.toString());
		sendMail.setFrom("singlekitri@gmail.com", "우리혼자산다");
		sendMail.setTo(userDto.getUserId());
		sendMail.send();
		return userDto;
	}

	@Override
	public void updateAuthstatus(UserDto userDto) {
		sqlSession.getMapper(MemberDao.class).updateUserAuthState(userDto);
	}

	@Override
	public UserDto login(UserDto userDto) {
		MemberDao memberDao  = sqlSession.getMapper(MemberDao.class);
		userDto = memberDao.login(userDto);
		return userDto;
	}

	@Override
	public UserDto getUser(UserDto userDto) {
		MemberDao memberDao  = sqlSession.getMapper(MemberDao.class);
		userDto = memberDao.getUser(userDto);
		return userDto;
	}



}
