package com.mju.groupware.controller;

import java.security.Principal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mju.groupware.dto.User;
import com.mju.groupware.function.UserInfoMethod;
import com.mju.groupware.service.ProfessorService;
import com.mju.groupware.service.StudentService;
import com.mju.groupware.service.UserService;

@Controller
public class SearchController {
	@Autowired
	private UserService userService;
	@Autowired
	private StudentService studentService;
	@Autowired
	private ProfessorService professorService;
	@Autowired
	private UserInfoMethod userInfoMethod;

	// review 사용자 검색
	@RequestMapping(value = "/search/searchUser", method = RequestMethod.GET)
	public String searchUser(Principal principal,  Model model, User user) {
		// 유저 정보
		GetUserInformation(principal, user, model);
		return "/search/searchUser";
	}
	// review list 검색
	@RequestMapping(value = "/search/reviewList", method = RequestMethod.GET)
	public String reviewList(Principal principal,  Model model, User user) {
		// 유저 정보
		GetUserInformation(principal, user, model);
		return "/search/reviewList";
	}

	// 후기 content 화면
	@RequestMapping(value = "/search/reviewContent", method = RequestMethod.GET)
	public String reviewContent(Principal principal,  Model model, User user) {
		// 유저 정보
		GetUserInformation(principal, user, model);
		return "/search/reviewContent";
	}

	// Information가져오는부분
	private void GetUserInformation(Principal principal, User user, Model model) {
		String LoginID = principal.getName();// 로그인 한 아이디
		ArrayList<String> SelectUserProfileInfo = new ArrayList<String>();
		SelectUserProfileInfo = userService.SelectUserProfileInfo(LoginID);
		user.setUserLoginID(LoginID);
		if (SelectUserProfileInfo.get(2).equals("STUDENT")) {
			ArrayList<String> StudentInfo = new ArrayList<String>();
			StudentInfo = studentService.SelectStudentProfileInfo(SelectUserProfileInfo.get(1));
			userInfoMethod.StudentInfo(model, SelectUserProfileInfo, StudentInfo);
		} else if (SelectUserProfileInfo.get(2).equals("PROFESSOR")) {
			ArrayList<String> ProfessorInfo = new ArrayList<String>();
			ProfessorInfo = professorService.SelectProfessorProfileInfo(SelectUserProfileInfo.get(1));
			userInfoMethod.ProfessorInfo(model, SelectUserProfileInfo, ProfessorInfo);
		} else if (SelectUserProfileInfo.get(2).equals("ADMINISTRATOR")) {
			userInfoMethod.AdministratorInfo(model, SelectUserProfileInfo);
		}
	}

}
