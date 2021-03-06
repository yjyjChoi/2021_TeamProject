package com.mju.groupware.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mju.groupware.dto.Student;
import com.mju.groupware.dto.User;
import com.mju.groupware.dto.UserEmail;
import com.mju.groupware.service.EmailService;
import com.mju.groupware.service.StudentService;
import com.mju.groupware.service.UserEmailService;
import com.mju.groupware.service.UserService;

@Controller
public class UserFunctionController {

	@Autowired
	private UserService userService;
	@Autowired
	private StudentService studentService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserEmailService userEmailService;

	private String StudentColleges;
	private String StudentGradeForShow;
	private String UserMajorForShow;
	private String UserLoginID;
	private String UserLoginPwd;
	private String NewUserLoginPwd;
	private String UserName;
	private String StudentGender;
	private String UserPhoneNum;
	private String StudentNum;
	private String StudentGradeForSignUp;
	private String StudentMajor;
	private String StudentDoubleMajor;
	private String UserEmail;
	private String AuthNum;
	private boolean IDChecker = false;
	private boolean EmailChecker = false;
	private boolean NameChecker = false;
	private boolean EmailCheck = true;
	private String Address;
	private Date Now;
	private Calendar Calendear;
	private DateFormat DateFormat;

	@RequestMapping(value = "/findPassword", method = RequestMethod.GET)
	public String findPassword() {
		return "/signin/findPassword";
	}

	/* ????????? ?????? ??? ???????????? ???????????? */
	@RequestMapping(value = "/showPassword", method = RequestMethod.GET)
	public String showPassword(User user, RedirectAttributes redirectAttributes, Model model,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		return "/signin/showPassword";
	}

	/* ?????? ?????? ?????? ?????? ??? ???????????? ?????? */
	@RequestMapping(value = "/checkPassword", method = RequestMethod.GET)
	public String checkPassword() {
		return "/mypage/checkPassword";
	}

	/* ???????????? ?????? ?????? */
	@RequestMapping(value = "/modifyPassword", method = RequestMethod.GET)
	public String modifyPassword() {
		return "/mypage/modifyPassword";
	}

	// ?????? ?????????
	@RequestMapping(value = "/withdrawal", method = RequestMethod.GET)
	public String withdrawal() {
		return "/mypage/withdrawal";
	}

	@RequestMapping(value = "/withdrawal", method = RequestMethod.POST)
	public String DoWithdrawl(HttpServletRequest request, Principal Principal, User user, Student student) {

		String UserLoginID = Principal.getName();// ??????
		user.setUserLoginID(UserLoginID);
		if ((String) request.getParameter("AgreeWithdrawal") != null) {
			// user?????? select?????? user??? set
			User UserInfo = userService.SelectUserInfo(UserLoginID);
			user.setUserName(UserInfo.getUserName());
			user.setUserPhoneNum(UserInfo.getUserPhoneNum());
			user.setUserEmail(UserInfo.getUserEmail());
			user.setUserLoginID(UserInfo.getUserLoginID());
			// ????????? ????????????
			if (UserInfo.getUserRole().equals("STUDENT")) {
				user.setUserRole("STUDENT");
				// withdrawalUser, withdrawalStudent??? insert
				userService.InsertWithdrawalUser(user);

				Student StudentInfo = studentService.SelectStudentInfo(Integer.toString(UserInfo.getUserID()));
				student.setWUserID(user.getWUserID());
				student.setStudentColleges(StudentInfo.getStudentColleges());
				student.setStudentDoubleMajor(StudentInfo.getStudentDoubleMajor());
				student.setStudentGender(StudentInfo.getStudentGender());
				student.setStudentGrade(StudentInfo.getStudentGrade());
				student.setStudentMajor(StudentInfo.getStudentMajor());

				studentService.InsertWithdrawalStudent(student);

				// user,student?????? delete
				userService.DeleteWithdrawalUser(user);
				studentService.DeleteWithdrawalStudent(student);
			}

		} else {
		}
		return "/mypage/withdrawal";
	}

	// ????????? ?????? ?????? ??????????????? ???????????? ??????. ???????????? ??????????????? ??????, ?????? ??? ?????????.
	@RequestMapping(value = "/checkPassword2", method = RequestMethod.GET)
	public String checkPassword2() {
		return "/mypage/checkPassword2";
	}

	@RequestMapping(value = "/checkPassword2.do", method = RequestMethod.POST)
	public String checkPassword2(HttpServletResponse response, HttpServletRequest request, Principal Principal) {
		String UserID = Principal.getName();
		UserLoginPwd = (String) request.getParameter("UserLoginPwd");// ?????? ????????????
		boolean Checker = userService.SelectForPwdCheckBeforeModify(UserID, UserLoginPwd);
		if (Checker == true) {
			return "redirect:withdrawal";
		} else {
			return "/mypage/checkPassword2";
		}
	}

	@RequestMapping(value = "/emailAuthentication", method = RequestMethod.GET)
	public String emailAuthentication() {
		return "/signup/emailAuthentication";
	}

	// ????????? ????????????, ????????? ??????
	@RequestMapping(value = "/email.do", method = RequestMethod.POST)
	public String DoEmail(User user, UserEmail userEmail, RedirectAttributes redirectAttributes, Model model,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		UserEmail = (String) request.getParameter("Email");
		AuthNum = (String) request.getParameter("Number");

		if ((String) request.getParameter("Email") != null) {
			model.addAttribute("Email", UserEmail);
			Address = "@mju.ac.kr";
			UserEmail = UserEmail + Address;
			user.setUserEmail(UserEmail);
		}
		if ((String) request.getParameter("Number") != null) {
			model.addAttribute("Number", AuthNum);
		}

		if (request.getParameter("EmailCheck") != null && !UserEmail.equals("")) {
			user.setUserEmail(UserEmail);
			// ????????? ????????????
			EmailCheck = emailService.SelectForEmailDuplicateCheck(user);

			if (!EmailCheck) {
				int Num = emailService.sendEmail(user);

				// ?????? ?????? ??????
				Calendear = Calendar.getInstance();
				DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Now = new Date();
				Calendear.setTime(Now);
				///////////////////////////////////////////////////////////

				response.setContentType("text/html; charset=UTF-8");
				PrintWriter Out = response.getWriter();
				Out.println("<script>alert('??????????????? ????????? ????????? ?????????????????????.' );</script>");
				Out.flush();

				// ?????? ???????????? ????????????, ????????????????????? ????????? ???????????? ?????? ????????? ???
				userEmail.setUserEmail(UserEmail);
				userEmail.setUserCertificationNum(Num);
				// ?????? ????????? ??????
				userEmail.setUserCertificationTime(DateFormat.format(Calendear.getTime()));
				this.userEmailService.InsertCertification(userEmail);
				////////////////////////////////////////////////////
			} else {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter Out = response.getWriter();
				Out.println("<script>alert('?????? ????????? ????????? ?????????.' );</script>");
				Out.flush();
			}
			return "/signup/emailAuthentication";
		} else if (UserEmail.equals("")) {
			// ???????????? ??????????????????
		} else if (request.getParameter("EmailValid") != null && AuthNum != "") {

			boolean Checker = userEmailService.SelectForCheckCertification(AuthNum);
			if (Checker) {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter Out = response.getWriter();
				Out.println("<script>alert('????????? ?????????????????????.' );</script>");
				Out.flush();
				EmailChecker = true;
			} else {
				if (AuthNum != "") {
					AuthNum = "";
				}
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter Out = response.getWriter();
				Out.println("<script>alert('???????????? ?????? ?????????????????????. ???????????? ??????????????????' );</script>");
				Out.flush();
				EmailChecker = false;
				return "/signup/emailAuthentication";
			}
		}

		if (request.getParameter("BtnAgree") != null && EmailChecker) {
			return "/signup/signupSelect";
		} else {
			return "/signup/emailAuthentication";
		}
	}

	// ?????? ????????????
	@RequestMapping(value = "/signupStudent.do", method = RequestMethod.POST)
	public String dosignup(User user, Student student, RedirectAttributes redirectAttributes, Model model,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		UserLoginID = (String) request.getParameter("UserLoginID");
		UserLoginPwd = (String) request.getParameter("UserLoginPwd");
		UserName = (String) request.getParameter("UserName");
		StudentGender = (String) request.getParameter("StudentGender");
		UserPhoneNum = (String) request.getParameter("UserPhoneNum");
		StudentGradeForSignUp = (String) request.getParameter("StudentGrade");
		StudentColleges = (String) request.getParameter("StudentColleges");
		StudentMajor = (String) request.getParameter("StudentMajor");
		StudentDoubleMajor = (String) request.getParameter("StudentDoubleMajor");

		if ((String) request.getParameter("UserLoginID") != null) {
			model.addAttribute("UserLoginID", UserLoginID);
		}
		if ((String) request.getParameter("UserLoginPwd") != null) {
			model.addAttribute("UserLoginPwd", UserLoginPwd);
		}
		if ((String) request.getParameter("UserName") != null) {
			model.addAttribute("UserName", UserName);
		}
		if ((String) request.getParameter("StudentGender") != null) {
			model.addAttribute("StudentGender", StudentGender);
		}
		if ((String) request.getParameter("UserPhoneNum") != null) {
			model.addAttribute("UserPhoneNum", UserPhoneNum);
		}
		if ((String) request.getParameter("StudentNum") != null) {
			model.addAttribute("StudentNum", StudentNum);
		}
		if ((String) request.getParameter("StudentGrade") != null) {
			model.addAttribute("StudentGrade", StudentGradeForSignUp);
		}
		if ((String) request.getParameter("UserColleges") != null) {
			model.addAttribute("UserColleges", StudentColleges);
		}
		if ((String) request.getParameter("UserMajor") != null) {
			model.addAttribute("UserMajor", StudentMajor);
		}
		if ((String) request.getParameter("StudentDoubleMajor") != null) {
			model.addAttribute("StudentDoubleMajor", StudentDoubleMajor);
		}

		if (request.getParameter("IdCheck") != null) {
			// name??? ????????? jsp?????? ?????? ????????????.
			String UserLoginID = (String) request.getParameter("UserLoginID");

			if (UserLoginID.equals("")) {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter Out = response.getWriter();
				Out.println("<script>alert('????????? ???????????? ??????????????????. ??????????????????' );</script>");
				Out.flush();
				return "/signup/signupStudent";
			} else {
				user.setUserLoginID(UserLoginID);
				boolean Checker = this.userService.SelctForIDConfirm(user);
				if (Checker) {
					UserLoginID = "";
					model.addAttribute("check", UserLoginID);
					Checker = false;
					response.setContentType("text/html; charset=UTF-8");
					PrintWriter Out = response.getWriter();
					Out.println("<script>alert('?????? ????????? ?????? ?????????.' );</script>");
					Out.flush();
					IDChecker = false;
					return "/signup/signupStudent";
				} else {
					response.setContentType("text/html; charset=UTF-8");
					PrintWriter Out = response.getWriter();
					Checker = true;
					Out.println("<script>alert('?????? ????????? ?????? ?????????.');</script>");
					Out.flush();
					IDChecker = true;
					return "/signup/signupStudent";
				}
			}
		} else if (request.getParameter("submitName") != null && IDChecker) {
			String HashedPw = BCrypt.hashpw(user.getUserLoginPwd(), BCrypt.gensalt());
			user.setUserLoginPwd(HashedPw);
			user.setUserRole("STUDENT"); // user role = ??????
			user.setUserEmail(UserEmail);

			this.userService.InsertForSignUp(user); // insert into user table
			user.setUserID(this.userService.SelectUserID(student)); // db??? userID(foreign key)??? user????????? userID??? set
			student.setStudentColleges(StudentColleges);
			student.setStudentMajor(StudentMajor);
			student.setUserID(user.getUserID());

			if (!((String) request.getParameter("member")).equals("Y")) {
				student.setStudentDoubleMajor("??????");
			} else {
				student.setStudentDoubleMajor(student.getStudentDoubleMajor());
			}
			this.studentService.InsertInformation(student); // insert into student table

			redirectAttributes.addFlashAttribute("msg", "signupERED");
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter Out = response.getWriter();
			Out.println("<script>alert('??????????????? ?????? ???????????????.');</script>");
			Out.flush();
			return "/signin/login";

		} else {
			return "/signup/signupStudent";
		}
	}

	// ???????????? ??????
	@RequestMapping(value = "/findPassword.do", method = RequestMethod.POST)
	public String findPassword(User user, RedirectAttributes redirectAttributes, Model model,
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserLoginID = (String) request.getParameter("UserLoginID");
		UserName = (String) request.getParameter("UserName");
		UserEmail = (String) request.getParameter("UserEmail");
		AuthNum = (String) request.getParameter("Number");
		if (request.getParameter("IdCheck") != null) {
			user.setUserLoginID(UserLoginID);
			user.setUserName(UserName);
			if (UserLoginID.equals("")) {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter Out = response.getWriter();
				Out.println("<script>alert('????????? ???????????? ??????????????????.');</script>");
				Out.flush();
			} else if (UserName.equals("")) {
				model.addAttribute("UserLoginID", UserLoginID);
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter Out = response.getWriter();
				Out.println("<script>alert('????????? ???????????? ??????????????????.');</script>");
				Out.flush();
			}
			boolean IDChecker = this.userService.SelectPwdForConfirmToFindPwd(user);
			if (IDChecker) {
				model.addAttribute("UserLoginID", UserLoginID);
				model.addAttribute("UserName", UserName);
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter Out = response.getWriter();
				Out.println("<script>alert('????????? ?????????????????????.');</script>");
				Out.flush();
				this.IDChecker = true;
				return "/signin/findPassword";
			} else {
				model.addAttribute("UserLoginID", UserLoginID);
				model.addAttribute("UserName", UserName);
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter Out = response.getWriter();
				Out.println("<script>alert('????????? ???????????? ????????????.');</script>");
				Out.flush();
				this.IDChecker = false;
				return "/signin/findPassword";
			}
		} else if (request.getParameter("EmailCheck") != null) {
			if (UserEmail.equals("")) {
				model.addAttribute("UserLoginID", UserLoginID);
				model.addAttribute("UserName", UserName);
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter Out = response.getWriter();
				Out.println("<script>alert('???????????? ???????????? ??????????????????.');</script>");
				Out.flush();
			} else {
				model.addAttribute("UserLoginID", UserLoginID);
				model.addAttribute("UserName", UserName);
				model.addAttribute("UserEmail", UserEmail);
				UserEmail = UserEmail + "@mju.ac.kr";
				user.setUserEmail(UserEmail);
				// ????????? ????????????
				EmailCheck = emailService.SelectForEmailDuplicateCheck(user);
				if (EmailCheck) {
					emailService.sendEmail(user);
					response.setContentType("text/html; charset=UTF-8");
					PrintWriter Out = response.getWriter();
					Out.println("<script>alert('??????????????? ????????? ????????? ?????????????????????.');</script>");
					Out.flush();
				} else {
					response.setContentType("text/html; charset=UTF-8");
					PrintWriter Out = response.getWriter();
					Out.println("<script>alert('???????????? ?????? ??????????????????.');</script>");
					Out.flush();
				}
				return "/signin/findPassword";
			}

		} else if (request.getParameter("EmailValid") != null) {
			model.addAttribute("UserLoginID", UserLoginID);
			model.addAttribute("UserName", UserName);
			model.addAttribute("UserEmail", UserEmail);
			NameChecker = emailService.AuthNum(AuthNum);
			if (NameChecker) {
				model.addAttribute("Number", AuthNum);
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("<script>alert('??????????????? ???????????????.');</script>");
				out.flush();
			} else {
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("<script>alert('??????????????? ???????????? ????????????.');</script>");
				out.flush();

			}
			return "/signin/findPassword";
		} else if (request.getParameter("SubmitName") != null && NameChecker && IDChecker) {
			user.setUserLoginID(UserLoginID);
			user.setUserName(UserName);
			String NewPwd = userService.SelectForShowPassword(user);
			String HashedPw = BCrypt.hashpw(NewPwd, BCrypt.gensalt());// ?????? ???????????? ?????????
			user.setUserLoginPwd(HashedPw);
			model.addAttribute("UserLoginPwd", NewPwd);
			userService.UpdateTemporaryPwd(user);

			return "/signin/showPassword";
		}
		return "/signin/findPassword";
	}

	/* ???????????? ??? ???????????? ?????? */
	@RequestMapping(value = "/checkPassword.do", method = RequestMethod.POST)
	public String checkPassword(HttpServletResponse response, HttpServletRequest request, Principal Principal) {

		String UserLoginID = Principal.getName();
		UserLoginPwd = (String) request.getParameter("UserLoginPwd");// ?????? ????????????
		boolean checker = userService.SelectForPwdCheckBeforeModify(UserLoginID, UserLoginPwd);
		if (checker == true) {
			return "redirect:modifyStudent";
		} else {
			return "/mypage/checkPassword";
		}
	}

	// ???????????? ??????
	@RequestMapping(value = "/modifyPassword.do", method = RequestMethod.POST)
	public String modifyPassword(HttpServletResponse response, HttpServletRequest request, User user,
			Principal Principal) throws IOException {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String UserLoginID = Principal.getName();
		UserLoginPwd = (String) request.getParameter("UserLoginPwd");// ?????? ????????????
		NewUserLoginPwd = (String) request.getParameter("UserNewPwd"); // ????????? ?????? ????????????
		String HashedPw = BCrypt.hashpw(NewUserLoginPwd, BCrypt.gensalt());// ?????? ???????????? ?????????
		user.setUserModifiedPW(HashedPw);

		// (???????????? ????????? ???????????? , ???????????? ????????????)
		if (encoder.matches(UserLoginPwd, userService.SelectCurrentPwd(UserLoginID))) {// ?????? ??????
			UserLoginPwd = userService.SelectCurrentPwd(UserLoginID);
			user.setUserLoginPwd(UserLoginPwd);
			userService.UpdatePwd(user);

			return "/mypage/modifyPassword";
		} else {
			PrintWriter out = response.getWriter();
			out.println("<script>alert('?????? ??????????????? ???????????? ????????????');</script>");

			return "/mypage/modifyPassword";
		}
	}

	// ????????? ?????? ?????? + ?????? ????????????
	@RequestMapping(value = "/homeLogin", method = RequestMethod.GET)
	public String homeLogin(User user, Principal Principal, Model model, HttpServletRequest request) {
		String UserLoginID = Principal.getName();// ????????? ??? ?????????
		ArrayList<String> Info = new ArrayList<String>();
		Info = userService.SelectUserProfileInfo(UserLoginID);

		user.setUserLoginID(UserLoginID);
		ArrayList<String> StudentInfo = new ArrayList<String>();
		StudentInfo = studentService.SelectStudentProfileInfo(Info.get(1));

		// ?????? ??????
		UserName = Info.get(0);
		model.addAttribute("UserName", UserName);
		// ?????? ??????
		StudentColleges = StudentInfo.get(0);
		model.addAttribute("SC", StudentColleges);

		UserMajorForShow = StudentInfo.get(1);
		model.addAttribute("UserMajor", UserMajorForShow);

		StudentGradeForShow = StudentInfo.get(2);
		model.addAttribute("Grade", StudentGradeForShow);

		Date Now = new Date();
		SimpleDateFormat Date = new SimpleDateFormat("yyyy-MM-dd");
		user.setDate(Date.format(Now));
		userService.UpdateLoginDate(user);

		return "/homeView/homeLogin";
	}

	// ????????? ????????? ??????
	@RequestMapping(value = "/emailLogin", method = RequestMethod.GET)
	public String emailLogin() {
		return "/email/emailLogin";
	}

	@RequestMapping(value = "/emailLogin.do", method = RequestMethod.POST)
	public String DoEmailLogin(HttpServletRequest request, Model model) {
		String id = request.getParameter("EmailLoginID");
		String pw = request.getParameter("EmailLoginPwd");
		System.out.println(id + " " + pw);
		List<String> emailList = emailService.printEmailList(id, pw);// ?????? + ????????? + ??????
		
		if(emailList.isEmpty()) {
			return "emailLogin";
		} else {
			model.addAttribute("EmailList", emailList);
			return "/email/emailList";
		}
	}

	// ????????? ????????? ??????
	@RequestMapping(value = "/emailList", method = RequestMethod.GET)
	public String emailList() {
		return "/email/emailList";
	}

	// ????????? ??????????????? ?????? ?????? ??? ?????? ????????? ?????? ??????
	// ????????? ????????? ??????
	@RequestMapping(value = "/emailContent", method = RequestMethod.GET)
	public String emailContent(HttpServletRequest request, Model model) {
		String num = request.getParameter("no");
		List<String> Content = emailService.getContent();
		List<String> From = emailService.getFrom();
		List<String> Subject = emailService.getsubject();
		List<String> Date = emailService.getDate();

		model.addAttribute("EmailSender", From.get(Integer.parseInt(num) - 1));
		model.addAttribute("EmailTitle", Subject.get(Integer.parseInt(num) - 1));
		model.addAttribute("EmailDate", Date.get(Integer.parseInt(num) - 1));
		model.addAttribute("EmailContent", Content.get(Integer.parseInt(num) - 1));

		return "/email/emailContent";
	}
}