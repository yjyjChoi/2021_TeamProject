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
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mju.groupware.constant.ConstantDoEmail;
import com.mju.groupware.constant.ConstantDoFindPassword;
import com.mju.groupware.constant.ConstantDoSignUp;
import com.mju.groupware.constant.ConstantEmail;
import com.mju.groupware.constant.ConstantFindPassword;
import com.mju.groupware.constant.ConstantHome;
import com.mju.groupware.constant.ConstantMyInquiryList;
import com.mju.groupware.constant.ConstantMyPostList;
import com.mju.groupware.constant.ConstantUserFunctionURL;
import com.mju.groupware.constant.ConstantWithdrawal;
import com.mju.groupware.dto.Board;
import com.mju.groupware.dto.Inquiry;
import com.mju.groupware.dto.Professor;
import com.mju.groupware.dto.Student;
import com.mju.groupware.dto.User;
import com.mju.groupware.dto.UserEmail;
import com.mju.groupware.function.UserInfoMethod;
import com.mju.groupware.service.BoardService;
import com.mju.groupware.service.EmailService;
import com.mju.groupware.service.InquiryService;
import com.mju.groupware.service.ProfessorService;
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
   private ProfessorService professorService;
   @Autowired
   private EmailService emailService;
   @Autowired
   private UserEmailService userEmailService;
   @Autowired
   private UserInfoMethod userInfoMethod;
   @Autowired
   private BoardService boardService;
   @Autowired
   private InquiryService inquiryService;
   private GenericXmlApplicationContext ctx;

   private String StudentColleges;
   private String UserLoginID;

   private String StudentGender;
   private String StudentGradeForSignUp;
   private String StudentMajor;
   private String StudentDoubleMajor;
   private String ProfessorColleges;
   private String ProfessorMajor;
   private String ProfessorRoom;
   private String ProfessorRoomNum;
   private String UserEmail;
   private boolean IDChecker = false;
   private boolean EmailChecker = false;
   private boolean NameChecker = false;
   private boolean EmailCheck = true;
   private String Address;
   private Date Now;
   private Calendar Calendear;
   private DateFormat DateFormat;

   private ConstantWithdrawal ConstantWithdrawal;
   private ConstantFindPassword ConstantFindPassword;
   private ConstantHome ConstantHome;
   private ConstantMyPostList ConstantMyPostList;
   private ConstantMyInquiryList ConstantMyInquiryList;
   private ConstantUserFunctionURL ConstantUserFunctionURL;

   public UserFunctionController() {
      ctx = new GenericXmlApplicationContext();
      ctx.load("classpath:/xmlForProperties/UserFunctionController.xml");
      ctx.refresh();
   }

   @RequestMapping(value = "/findPassword", method = RequestMethod.GET)
   public String findPassword() {
      ConstantFindPassword ConstantFindPassword = (ConstantFindPassword) ctx.getBean("FindPassword");
      return ConstantFindPassword.getFPUrl();
   }

   /* ????????? ?????? ??? ???????????? ???????????? */
   @RequestMapping(value = "/showPassword", method = RequestMethod.GET)
   public String showPassword(User user, RedirectAttributes redirectAttributes, Model model,
         HttpServletRequest request, HttpServletResponse response) throws IOException {
      this.ConstantFindPassword = (ConstantFindPassword) ctx.getBean("FindPassword");

      return this.ConstantFindPassword.getSPUrl();
   }

   // home ????????? ?????? ?????? + ?????? ????????????
   @RequestMapping(value = "/home", method = RequestMethod.GET)
   public String home(User user, Principal principal, Model model, HttpServletRequest request, Student student,
         Professor professor) {
      this.ConstantHome = (ConstantHome) ctx.getBean("Home");

      if (principal != null) { // ????????? ??????(????????? ????????? principal ???????????????)?????????
         // ?????? ??????
         String LoginID = principal.getName();// ????????? ??? ?????????
         ArrayList<String> SelectUserProfileInfo = new ArrayList<String>();
         SelectUserProfileInfo = userService.SelectUserProfileInfo(LoginID);
         int UserID = Integer.parseInt(userService.SelectUserIDForDate(LoginID));
         user.setUserLoginID(LoginID);

         // ???????????? ?????? ?????? ??? update
         boolean DormantCheck = userService.SelectDormant(LoginID);
         if (DormantCheck) {
            userService.UpdateRecoveryDormant(LoginID);
         }

         if (SelectUserProfileInfo.get(2).equals(this.ConstantHome.getSRole())) {
            ArrayList<String> StudentInfo = new ArrayList<String>();
            StudentInfo = studentService.SelectStudentProfileInfo(SelectUserProfileInfo.get(1));

            userInfoMethod.StudentInfo(model, SelectUserProfileInfo, StudentInfo);
         } else if (SelectUserProfileInfo.get(2).equals(this.ConstantHome.getPRole())) {

            ArrayList<String> ProfessorInfo = new ArrayList<String>();
            ProfessorInfo = professorService.SelectProfessorProfileInfo(SelectUserProfileInfo.get(1));

            userInfoMethod.ProfessorInfo(model, SelectUserProfileInfo, ProfessorInfo);
         } else if (SelectUserProfileInfo.get(2).equals(this.ConstantHome.getARole())) {
            userInfoMethod.AdministratorInfo(model, SelectUserProfileInfo);
         }
         Date Now = new Date();
         SimpleDateFormat Date = new SimpleDateFormat(this.ConstantHome.getDFormat());
         user.setDate(Date.format(Now));
         student.setDate(Date.format(Now));
         student.setUserID(UserID);
         professor.setDate(Date.format(Now));
         professor.setUserID(UserID);
         userService.UpdateLoginDate(user);
         studentService.UpdateStudentLoginDate(student);
         professorService.UpdateProfessorLoginDate(professor);

      }

      // ???????????? ????????? ?????????
      List<Board> NoticeList = boardService.SelectNoticeBoardList();
      model.addAttribute(this.ConstantHome.getNL(), NoticeList);

      // ???????????? ????????? ?????????
      List<Board> CommunityList = boardService.SelectCommunityBoardList();
      model.addAttribute(this.ConstantHome.getCL(), CommunityList);

      return this.ConstantHome.getHUrl();
   }

   @RequestMapping(value = "/", method = RequestMethod.GET)
   public String BlankHome(User user, Principal principal, Model model, HttpServletRequest request, Student student,
         Professor professor) {
      this.ConstantHome = (ConstantHome) ctx.getBean("Home");

      if (principal != null) {
         // ?????? ??????
         String LoginID = principal.getName();// ????????? ??? ?????????
         ArrayList<String> SelectUserProfileInfo = new ArrayList<String>();
         SelectUserProfileInfo = userService.SelectUserProfileInfo(LoginID);
         int UserID = Integer.parseInt(userService.SelectUserIDForDate(LoginID));
         user.setUserLoginID(LoginID);

         // ???????????? ?????? ?????? ??? update
         boolean DormantCheck = userService.SelectDormant(LoginID);
         if (DormantCheck) {
            userService.UpdateRecoveryDormant(LoginID);
         }

         if (SelectUserProfileInfo.get(2).equals(this.ConstantHome.getSRole())) {
            ArrayList<String> StudentInfo = new ArrayList<String>();
            StudentInfo = studentService.SelectStudentProfileInfo(SelectUserProfileInfo.get(1));

            userInfoMethod.StudentInfo(model, SelectUserProfileInfo, StudentInfo);
         } else if (SelectUserProfileInfo.get(2).equals(this.ConstantHome.getPRole())) {

            ArrayList<String> ProfessorInfo = new ArrayList<String>();
            ProfessorInfo = professorService.SelectProfessorProfileInfo(SelectUserProfileInfo.get(1));

            userInfoMethod.ProfessorInfo(model, SelectUserProfileInfo, ProfessorInfo);
         } else if (SelectUserProfileInfo.get(2).equals(this.ConstantHome.getARole())) {
            userInfoMethod.AdministratorInfo(model, SelectUserProfileInfo);
         }

         Date Now = new Date();
         SimpleDateFormat Date = new SimpleDateFormat(this.ConstantHome.getDFormat());
         user.setDate(Date.format(Now));
         student.setDate(Date.format(Now));
         student.setUserID(UserID);
         professor.setDate(Date.format(Now));
         professor.setUserID(UserID);
         userService.UpdateLoginDate(user);
         studentService.UpdateStudentLoginDate(student);
         professorService.UpdateProfessorLoginDate(professor);
      }

      // ???????????? ????????? ?????????
      List<Board> NoticeList = boardService.SelectNoticeBoardList();
      model.addAttribute("noticeList", NoticeList);

      // ???????????? ????????? ?????????
      List<Board> CommunityList = boardService.SelectCommunityBoardList();
      model.addAttribute(this.ConstantHome.getCL(), CommunityList);

      return this.ConstantHome.getHUrl();
   }

   // home?????? ??????????????? ?????? ??? ?????? role??? ?????? ????????? ??????
   @RequestMapping(value = "/myPage", method = RequestMethod.GET)
   public String myPageByRole(HttpServletRequest request, Model model) throws IOException {
      String MysqlRole = request.getParameter("R");
      this.ConstantHome = (ConstantHome) ctx.getBean("Home");

      if (MysqlRole.equals(this.ConstantHome.getSRole())) {
         return this.ConstantHome.getMPSUrl();
      } else if (MysqlRole.equals(this.ConstantHome.getPRole())) {
         return this.ConstantHome.getMPPUrl();
      } else if (MysqlRole.equals(this.ConstantHome.getARole())) {
         return this.ConstantHome.getRUrl();
      }
      return this.ConstantHome.getRUrl();
   }

   // ??????????????? - ??? ????????? ??????
   @RequestMapping(value = "/myPostList", method = RequestMethod.GET)
   public String myPostList(Model model, Principal principal, User user) {
      // ?????? ??????
      this.ConstantMyPostList = (ConstantMyPostList) ctx.getBean("MyPostList");

      String LoginID = principal.getName();// ????????? ??? ?????????
      GetUserInformation(principal, user, model);
      //
      String UserID = userService.SelectUserIDForMyBoard(LoginID);
      List<Board> MyBoardList = boardService.SelectMyBoardList(UserID);

      model.addAttribute(this.ConstantMyPostList.getMBList(), MyBoardList);

      return this.ConstantMyPostList.getMBUrl();
   }

   // ??????????????? - ??? ?????? ??????
   @RequestMapping(value = "/myInquiryList", method = RequestMethod.GET)
   public String myInquiryList(Model model, Principal principal, User user) {
      // ?????? ??????
      this.ConstantMyInquiryList = (ConstantMyInquiryList) ctx.getBean("MyInquiryList");

      String LoginID = principal.getName();// ????????? ??? ?????????
      GetUserInformation(principal, user, model);
      //
      String UserID = userService.SelectUserIDForMyBoard(LoginID);
      List<Inquiry> MyInquiryList = inquiryService.SelectMyInquiryList(UserID);
      if (!MyInquiryList.isEmpty()) {
         model.addAttribute(this.ConstantMyInquiryList.getMIList(), MyInquiryList);
      }
      System.out.println(this.ConstantMyInquiryList.getMIUrl());
      return this.ConstantMyInquiryList.getMIUrl();
   }

   /* ??????????????? - ??? ??? ?????? */
   @RequestMapping(value = "/checkMyTeam", method = RequestMethod.GET)
   public String checkMyTeam(Model model, Principal principal, User user) {
      // ?????? ??????
      GetUserInformation(principal, user, model);
      //
      this.ConstantUserFunctionURL = (ConstantUserFunctionURL) ctx.getBean("UserFunctionURL");

      return this.ConstantUserFunctionURL.getCMTUrl();
   }

   /* ?????? ?????? ?????? ?????? ??? ???????????? ?????? */
   @RequestMapping(value = "/checkPassword", method = RequestMethod.GET)
   public String checkPassword() {
      this.ConstantUserFunctionURL = (ConstantUserFunctionURL) ctx.getBean("UserFunctionURL");

      return this.ConstantUserFunctionURL.getCPUrl();
   }

   /* ???????????? ?????? ?????? */
   @RequestMapping(value = "/modifyPassword", method = RequestMethod.GET)
   public String modifyPassword() {
      this.ConstantUserFunctionURL = (ConstantUserFunctionURL) ctx.getBean("UserFunctionURL");

      return this.ConstantUserFunctionURL.getMPUrl();
   }

   // ?????? ?????????
   @RequestMapping(value = "/withdrawal", method = RequestMethod.GET)
   public String withdrawal() {
      this.ConstantWithdrawal = (ConstantWithdrawal) ctx.getBean("Withdrawal");
      return ConstantWithdrawal.getUrl();
   }

   @RequestMapping(value = "/withdrawal", method = RequestMethod.POST)
   public String DoWithdrawl(HttpServletRequest request, Principal Principal, User user, Student student,
         Professor professor) {
      this.ConstantWithdrawal = (ConstantWithdrawal) ctx.getBean("Withdrawal");
      String UserLoginID = Principal.getName();// ??????
      user.setUserLoginID(UserLoginID);

      if ((String) request.getParameter(ConstantWithdrawal.getParameter1()) != null) {
         // user?????? select?????? user??? set
         User UserInfo = userService.SelectUserInfo(UserLoginID);
         user.setUserLoginID(UserInfo.getUserLoginID());
         // ????????? ?????? set
         Date Now = new Date();
         SimpleDateFormat Date = new SimpleDateFormat(ConstantWithdrawal.getParameter2());
         user.setDate(Date.format(Now));

         userService.UpdateWithdrawal(user);
      }
      return ConstantWithdrawal.getUrl();
   }

   // ????????? ?????? ?????? ??????????????? ???????????? ??????. ???????????? ??????????????? ??????, ?????? ??? ?????????.
   @RequestMapping(value = "/checkPassword2", method = RequestMethod.GET)
   public String checkPassword2() {
      this.ConstantUserFunctionURL = (ConstantUserFunctionURL) ctx.getBean("UserFunctionURL");

      return this.ConstantUserFunctionURL.getCPWUrl();
   }

   @RequestMapping(value = "/checkPassword2.do", method = RequestMethod.POST)
   public String checkPassword2(HttpServletResponse response, HttpServletRequest request, Principal Principal)
         throws IOException {
      this.ConstantUserFunctionURL = (ConstantUserFunctionURL) ctx.getBean("UserFunctionURL");
      String UserID = Principal.getName();

      boolean Checker = userService.SelectForPwdCheckBeforeModify(UserID,
            (String) request.getParameter(this.ConstantUserFunctionURL.getULPWD()));
      if (Checker == true) {
         return this.ConstantUserFunctionURL.getRWUrl();
      } else {
         response.setContentType("text/html; charset=UTF-8");
         PrintWriter Out = response.getWriter();
         Out.println("<script>alert('??????????????? ?????? ??????????????????.' );</script>");
         Out.flush();
         return this.ConstantUserFunctionURL.getCPWUrl();
      }
   }

   @RequestMapping(value = "/emailAuthentication", method = RequestMethod.GET)
   public String emailAuthentication() {
      this.ConstantUserFunctionURL = (ConstantUserFunctionURL) ctx.getBean("UserFunctionURL");

      return this.ConstantUserFunctionURL.getEAUrl();
   }

   // ????????? ????????????, ????????? ??????
   @RequestMapping(value = "/email.do", method = RequestMethod.POST)
   public String DoEmail(User user, UserEmail userEmail, RedirectAttributes redirectAttributes, Model model,
         HttpServletRequest request, HttpServletResponse response) throws IOException {

      ConstantDoEmail constantDoEmail = (ConstantDoEmail) ctx.getBean("DoEmail");
      UserEmail = (String) request.getParameter(constantDoEmail.getEM());
      if ((String) request.getParameter(constantDoEmail.getEM()) != null) {
         model.addAttribute(constantDoEmail.getEM(), UserEmail);
         Address = constantDoEmail.getEmailAdress();
         UserEmail = UserEmail + Address;
         user.setUserEmail(UserEmail);
      }
      if ((String) request.getParameter(constantDoEmail.getAuthNum()) != null) {
         model.addAttribute(constantDoEmail.getAuthNum(),
               (String) request.getParameter(constantDoEmail.getAuthNum()));
      }
      if (request.getParameter(constantDoEmail.getEC()) != null && !UserEmail.equals("")) {
         user.setUserEmail(UserEmail);
         // ????????? ????????????
         EmailCheck = emailService.SelectForEmailDuplicateCheck(user);

         if (!EmailCheck) {
            int Num = emailService.sendEmail(user);
            // ?????? ?????? ??????
            Calendear = Calendar.getInstance();
            DateFormat = new SimpleDateFormat(constantDoEmail.getDateFormat());
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
         return constantDoEmail.getAuthUrl();
      } else if (UserEmail.equals("")) {
         // ???????????? ??????????????????
      } else if (request.getParameter(constantDoEmail.getEV()) != null
            && request.getParameter(constantDoEmail.getAuthNum()) != "") {
         boolean Checker = userEmailService
               .SelectForCheckCertification(request.getParameter(constantDoEmail.getAuthNum()));
         if (Checker) {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter Out = response.getWriter();
            Out.println("<script>alert('????????? ?????????????????????.' );</script>");
            Out.flush();
            EmailChecker = true;
         } else {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter Out = response.getWriter();
            Out.println("<script>alert('???????????? ?????? ?????????????????????. ???????????? ??????????????????' );</script>");
            Out.flush();
            EmailChecker = false;
            return constantDoEmail.getAuthUrl();
         }
      }

      if (request.getParameter(constantDoEmail.getBA()) != null && EmailChecker) {
         return constantDoEmail.getAgreeUrl();
      } else {
         return constantDoEmail.getAuthUrl();
      }
   }

   // ?????? ????????????
   @RequestMapping(value = "/signupStudent.do", method = RequestMethod.POST)
   public String DoSignUp(User user, Student student, RedirectAttributes redirectAttributes, Model model,
         HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      ConstantDoSignUp constantDoSignUp = (ConstantDoSignUp) ctx.getBean("DoSignUp");

      UserLoginID = (String) request.getParameter("UserLoginID");
      StudentGender = (String) request.getParameter("StudentGender");
      StudentGradeForSignUp = (String) request.getParameter("StudentGrade");
      StudentColleges = (String) request.getParameter("StudentColleges");
      StudentMajor = (String) request.getParameter("StudentMajor");
      StudentDoubleMajor = (String) request.getParameter("StudentDoubleMajor");

      if ((String) request.getParameter("UserLoginID") != null) {
         model.addAttribute("UserLoginID", UserLoginID);
      }
      if ((String) request.getParameter(constantDoSignUp.getPwd()) != null) {
         model.addAttribute(constantDoSignUp.getPwd(), (String) request.getParameter(constantDoSignUp.getPwd()));
      }
      if ((String) request.getParameter(constantDoSignUp.getSName()) != null) {
         model.addAttribute(constantDoSignUp.getSName(), (String) request.getParameter(constantDoSignUp.getSName()));
      }
      if ((String) request.getParameter("StudentGender") != null) {
         model.addAttribute("StudentGender", StudentGender);
      }
      if ((String) request.getParameter("UserPhoneNum") != null) {
         model.addAttribute(constantDoSignUp.getPhoneNum(),
               (String) request.getParameter(constantDoSignUp.getPhoneNum()));
      }
      if ((String) request.getParameter(constantDoSignUp.getSNum()) != null) {
         model.addAttribute(constantDoSignUp.getSNum(), request.getParameter(constantDoSignUp.getSNum()));
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
            return constantDoSignUp.getSSUrl();
         } else if (UserLoginID.length() != 8) {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter Out = response.getWriter();
            Out.println("<script>alert('??????(8??????)??? ??????????????????. ' );</script>");
            Out.flush();
            return constantDoSignUp.getSSUrl();
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
               return constantDoSignUp.getSSUrl();
            } else {
               response.setContentType("text/html; charset=UTF-8");
               PrintWriter Out = response.getWriter();
               Checker = true;
               Out.println("<script>alert('?????? ????????? ?????? ?????????.');</script>");
               Out.flush();
               IDChecker = true;
               return constantDoSignUp.getSSUrl();
            }
         }
      } else if (request.getParameter("submitName") != null && IDChecker) {
         if (StudentColleges.equals("")) {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter Out = response.getWriter();
            Out.println("<script>alert('??????????????? ??????????????????.');</script>");
            Out.flush();
            return constantDoSignUp.getSSUrl();
         } else if (StudentMajor.equals("-??????-")) {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter Out = response.getWriter();
            Out.println("<script>alert('????????? ??????????????????.');</script>");
            Out.flush();
            return constantDoSignUp.getSSUrl();

         } else {
            String HashedPw = BCrypt.hashpw(user.getUserLoginPwd(), BCrypt.gensalt());
            user.setUserLoginPwd(HashedPw);
            user.setUserRole(constantDoSignUp.getSRole()); // user role = ??????
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
            return constantDoSignUp.getSLUrl();
         }
      } else {
         return constantDoSignUp.getSSUrl();
      }
   }

   // ?????? ????????????
   @RequestMapping(value = "/signupProfessor.do", method = RequestMethod.POST)
   public String dosignup(User user, Professor professor, RedirectAttributes redirectAttributes, Model model,
         HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      ConstantDoSignUp constantDoSignUp = (ConstantDoSignUp) ctx.getBean("DoSignUp");

      UserLoginID = (String) request.getParameter("UserLoginID");

      ProfessorColleges = (String) request.getParameter("ProfessorColleges");
      ProfessorMajor = (String) request.getParameter("ProfessorMajor");
      ProfessorRoom = (String) request.getParameter("ProfessorRoom");
      ProfessorRoomNum = (String) request.getParameter("ProfessorRoomNum");

      if ((String) request.getParameter("UserLoginID") != null) {
         model.addAttribute("UserLoginID", UserLoginID);
      }
      if ((String) request.getParameter(constantDoSignUp.getPwd()) != null) {
         model.addAttribute(constantDoSignUp.getPwd(), (String) request.getParameter(constantDoSignUp.getPwd()));
      }
      if ((String) request.getParameter(constantDoSignUp.getPName()) != null) {
         model.addAttribute(constantDoSignUp.getPName(), (String) request.getParameter(constantDoSignUp.getPName()));
      }
      if ((String) request.getParameter("UserPhoneNum") != null) {
         model.addAttribute(constantDoSignUp.getPhoneNum(),
               (String) request.getParameter(constantDoSignUp.getPhoneNum()));
      }
      if ((String) request.getParameter(constantDoSignUp.getPNum()) != null) {
         model.addAttribute(constantDoSignUp.getPNum(), request.getParameter(constantDoSignUp.getPNum()));
      }
      if ((String) request.getParameter("UserColleges") != null) {
         model.addAttribute("UserColleges", ProfessorColleges);
      }
      if ((String) request.getParameter("UserMajor") != null) {
         model.addAttribute("UserMajor", ProfessorMajor);
      }
      if ((String) request.getParameter("ProfessorRoom") != null) {
         model.addAttribute("ProfessorRoom", ProfessorRoom);
      }
      if ((String) request.getParameter("ProfessorRoomNum") != null) {
         model.addAttribute("ProfessorRoomNum", ProfessorRoomNum);
      }

      if (request.getParameter("IdCheck") != null) {
         // name??? ????????? jsp?????? ?????? ????????????.
         String UserLoginID = (String) request.getParameter("UserLoginID");

         if (UserLoginID.equals("")) {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter Out = response.getWriter();
            Out.println("<script>alert('????????? ???????????? ??????????????????. ??????????????????' );</script>");
            Out.flush();
            return "/signup/signupProfessor";
         } else if (UserLoginID.length() != 8) {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter Out = response.getWriter();
            Out.println("<script>alert('??????(8??????)??? ??????????????????. ' );</script>");
            Out.flush();
            return "/signup/signupProfessor";
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
               return "/signup/signupProfessor";
            } else {
               response.setContentType("text/html; charset=UTF-8");
               PrintWriter Out = response.getWriter();
               Checker = true;
               Out.println("<script>alert('?????? ????????? ?????? ?????????.');</script>");
               Out.flush();
               IDChecker = true;
               return "/signup/signupProfessor";
            }
         }
      } else if (request.getParameter("submitName") != null && IDChecker) {

         if (ProfessorColleges.equals("")) {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter Out = response.getWriter();
            Out.println("<script>alert('??????????????? ??????????????????.');</script>");
            Out.flush();
            return "/signup/signupProfessor";
         } else if (ProfessorMajor.equals("-??????-")) {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter Out = response.getWriter();
            Out.println("<script>alert('????????? ??????????????????.');</script>");
            Out.flush();
            return "/signup/signupProfessor";
         } else {
            String HashedPw = BCrypt.hashpw(user.getUserLoginPwd(), BCrypt.gensalt());
            user.setUserLoginPwd(HashedPw);

            user.setUserRole(constantDoSignUp.getPRole()); // user role = ??????
            user.setUserEmail(UserEmail);

            this.userService.InsertForSignUp(user); // insert into user table
            user.setUserID(this.userService.SelectUserID(professor)); // db??? userID(foreign key)??? user????????? userID???
                                                         // set
            professor.setProfessorColleges(ProfessorColleges);
            professor.setProfessorMajor(ProfessorMajor);
            professor.setProfessorRoom(ProfessorRoom);
            professor.setProfessorRoomNum(ProfessorRoomNum);
            professor.setUserID(user.getUserID());

            this.professorService.InsertInformation(professor); // insert into student table

            redirectAttributes.addFlashAttribute("msg", "signupERED");
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter Out = response.getWriter();
            Out.println("<script>alert('??????????????? ?????? ???????????????.');</script>");
            Out.flush();
            return "/signin/login";
         }
      } else {
         return "/signup/signupProfessor";
      }
   }

   // ???????????? ?????? findPassword.do?????????????????????
   @RequestMapping(value = "/findPassword.do", method = RequestMethod.POST)
   public String findPassword(User user, RedirectAttributes redirectAttributes, Model model,
         HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      // xml???????????????
      ConstantDoFindPassword constantDoFindPassword = (ConstantDoFindPassword) ctx.getBean("DoFindPassword");

      UserLoginID = (String) request.getParameter("UserLoginID");
      UserEmail = (String) request.getParameter("UserEmail");
      if (request.getParameter("IdCheck") != null) {
         user.setUserLoginID(UserLoginID);
         user.setUserName((String) request.getParameter(constantDoFindPassword.getUName()));
         if (UserLoginID.equals("")) {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter Out = response.getWriter();
            Out.println("<script>alert('????????? ???????????? ??????????????????.');</script>");
            Out.flush();
         } else if (((String) request.getParameter(constantDoFindPassword.getUName())).equals("")) {
            model.addAttribute("UserLoginID", UserLoginID);
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter Out = response.getWriter();
            Out.println("<script>alert('????????? ???????????? ??????????????????.');</script>");
            Out.flush();
         }
         boolean IDChecker = this.userService.SelectPwdForConfirmToFindPwd(user);
         if (IDChecker) {
            model.addAttribute("UserLoginID", UserLoginID);
            model.addAttribute(constantDoFindPassword.getUName(),
                  (String) request.getParameter(constantDoFindPassword.getUName()));
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter Out = response.getWriter();
            Out.println("<script>alert('????????? ?????????????????????.');</script>");
            Out.flush();
            this.IDChecker = true;
            return constantDoFindPassword.getFPUrl();
         } else {
            model.addAttribute("UserLoginID", UserLoginID);
            model.addAttribute(constantDoFindPassword.getUName(),
                  (String) request.getParameter(constantDoFindPassword.getUName()));
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter Out = response.getWriter();
            Out.println("<script>alert('????????? ???????????? ????????????.');</script>");
            Out.flush();
            this.IDChecker = false;
            return constantDoFindPassword.getFPUrl();
         }
      } else if (request.getParameter("EmailCheck") != null) {
         if (UserEmail.equals("")) {
            model.addAttribute("UserLoginID", UserLoginID);
            model.addAttribute(constantDoFindPassword.getUName(),
                  (String) request.getParameter(constantDoFindPassword.getUName()));
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter Out = response.getWriter();
            Out.println("<script>alert('???????????? ???????????? ??????????????????.');</script>");
            Out.flush();
         } else {
            model.addAttribute("UserLoginID", UserLoginID);
            model.addAttribute(constantDoFindPassword.getUName(),
                  (String) request.getParameter(constantDoFindPassword.getUName()));
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
            return constantDoFindPassword.getFPUrl();
         }

      } else if (request.getParameter("EmailValid") != null) {
         model.addAttribute("UserLoginID", UserLoginID);
         model.addAttribute(constantDoFindPassword.getUName(),
               (String) request.getParameter(constantDoFindPassword.getUName()));
         model.addAttribute("UserEmail", UserEmail);
         NameChecker = emailService.AuthNum((String) request.getParameter(constantDoFindPassword.getAuthNum()));
         if (NameChecker) {
            model.addAttribute(constantDoFindPassword.getAuthNum(),
                  (String) request.getParameter(constantDoFindPassword.getAuthNum()));
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
         return constantDoFindPassword.getFPUrl();
      } else if (request.getParameter("SubmitName") != null && NameChecker && IDChecker) {
         user.setUserLoginID(UserLoginID);
         user.setUserName((String) request.getParameter(constantDoFindPassword.getUName()));
         String NewPwd = userService.SelectForShowPassword(user);
         String HashedPw = BCrypt.hashpw(NewPwd, BCrypt.gensalt());// ?????? ???????????? ?????????
         user.setUserLoginPwd(HashedPw);
         model.addAttribute("UserLoginPwd", NewPwd);
         userService.UpdateTemporaryPwd(user);

         return constantDoFindPassword.getSSPUrl();
      }
      return constantDoFindPassword.getFPUrl();
   }

   /* ???????????? ??? ???????????? ?????? */
   @RequestMapping(value = "/checkPassword.do", method = RequestMethod.POST)
   public String checkPassword(HttpServletResponse response, HttpServletRequest request, Principal Principal)
         throws IOException {
      ConstantDoFindPassword constantDoFindPassword = (ConstantDoFindPassword) ctx.getBean("DoFindPassword");

      String UserLoginID = Principal.getName();
      boolean Checker = userService.SelectForPwdCheckBeforeModify(UserLoginID,
            (String) request.getParameter(constantDoFindPassword.getPwd()));
      String MysqlRole = userService.SelectUserRole(UserLoginID);
      if (Checker == true) {
         if (MysqlRole.equals(constantDoFindPassword.getSRole())) {
            return constantDoFindPassword.getRMS();
         } else if (MysqlRole.equals(constantDoFindPassword.getPRole())) {
            return constantDoFindPassword.getRMP();
         }
      } else {
         response.setContentType("text/html; charset=UTF-8");
         PrintWriter Out = response.getWriter();
         Out.println("<script>alert('??????????????? ?????? ??????????????????.' );</script>");
         Out.flush();
         return constantDoFindPassword.getCPUrl();
      }
      return "/";
   }

   // ???????????? ????????? ??? ?????? ?????? ??????
   @RequestMapping(value = "/checkPassword3", method = RequestMethod.GET)
   public String checkPassword3() {
      ConstantDoFindPassword constantDoFindPassword = (ConstantDoFindPassword) ctx.getBean("DoFindPassword");

      return constantDoFindPassword.getCPUrl3();
   }

   @RequestMapping(value = "/checkPassword3.do", method = RequestMethod.POST)
   public String checkPassword3(HttpServletResponse response, HttpServletRequest request, Principal Principal)
         throws IOException {
      ConstantDoFindPassword constantDoFindPassword = (ConstantDoFindPassword) ctx.getBean("DoFindPassword");

      String UserID = Principal.getName();
      boolean Checker = userService.SelectForPwdCheckBeforeModify(UserID,
            (String) request.getParameter(constantDoFindPassword.getULPWD()));
      if (Checker == true) {
         return constantDoFindPassword.getRMPWD();
      } else {
         response.setContentType("text/html; charset=UTF-8");
         PrintWriter Out = response.getWriter();
         Out.println("<script>alert('??????????????? ?????? ??????????????????.' );</script>");
         Out.flush();
         return constantDoFindPassword.getCPUrl3();
      }
   }

   // ???????????? ??????
   @RequestMapping(value = "/modifyPassword.do", method = RequestMethod.POST)
   public String modifyPassword(HttpServletResponse response, HttpServletRequest request, User user,
         Principal Principal) throws IOException {
      ConstantDoFindPassword constantDoFindPassword = (ConstantDoFindPassword) ctx.getBean("DoFindPassword");
      String UserLoginID = Principal.getName();
      String HashedPwd = BCrypt.hashpw((String) request.getParameter(constantDoFindPassword.getUNPWD()),
            BCrypt.gensalt());
      user.setUserModifiedPW(HashedPwd);
      // ??????????????? ??????????????? ?????? ??????????????? ????????????
      if ((request.getParameter(constantDoFindPassword.getUNPWD())
            .equals((String) request.getParameter(constantDoFindPassword.getUNPWDC())))) {
         String UserLoginPwd = userService.SelectCurrentPwd(UserLoginID);
         user.setUserLoginPwd(UserLoginPwd);
         userService.UpdatePwd(user);

         return constantDoFindPassword.getMPUrl();
      }
      return constantDoFindPassword.getMPUrl();
   }

   // ????????? ????????? ??????
   @RequestMapping(value = "/email/emailLogin", method = RequestMethod.GET)
   public String emailLogin() {
      ConstantEmail constantEmail = (ConstantEmail) ctx.getBean("Email");
      return constantEmail.getEMURL();
   }

   @RequestMapping(value = "/email/emailList", method = RequestMethod.POST)
   public String DoEmailLogin(HttpServletRequest request, Model model, Principal principal, User user,
         RedirectAttributes rttr) {
      // ?????? ??????
      GetUserInformation(principal, user, model);
      // ????????? ????????? ???????????? ???????????? ????????? ??????
      // ?????? ?????????
      ConstantDoEmail constantDoEmail = (ConstantDoEmail) ctx.getBean("DoEmail");

      String ID = request.getParameter("EmailLoginID") + constantDoEmail.getEmailAdress();

      boolean CheckID = emailService.CheckEmailLogin(ID, request.getParameter(constantDoEmail.getEPwd()));
      if (CheckID) {
         return constantDoEmail.getREURL();
      } else {
         rttr.addFlashAttribute("Checker", "LoginFail");
         return constantDoEmail.getRELURL();
      }
   }

   // ????????? ????????? ??????
   @RequestMapping(value = "/email/emailList", method = RequestMethod.GET)
   public String emailList(HttpServletRequest request, Model model, Principal principal, User user) {
      // ?????? ??????
      ConstantDoEmail constantDoEmail = (ConstantDoEmail) ctx.getBean("DoEmail");

      GetUserInformation(principal, user, model);
      List<UserEmail> EmailList = emailService.PrintEmailList();// ?????? + ????????? + ??????
      if (EmailList.isEmpty()) {
         return constantDoEmail.getEURL();
      } else {
         model.addAttribute("EmailList", EmailList);
         return constantDoEmail.getEURL();
      }
   }

   // ????????? ??????????????? ?????? ?????? ??? ?????? ????????? ?????? ??????
   // ????????? ?????? ??????
   @RequestMapping(value = "/email/emailContent", method = RequestMethod.GET)
   public String emailContent(HttpServletRequest request, Model model, Principal principal, User user) {
      ConstantDoEmail constantDoEmail = (ConstantDoEmail) ctx.getBean("DoEmail");

      GetUserInformation(principal, user, model);

      // ??????????????? ?????????
      String Num = request.getParameter("no");
      int IntNum = Integer.parseInt(Num) - 1;
      List<UserEmail> EmailList = emailService.GetEmailList();
      if (EmailList.isEmpty()) {

      } else {
         model.addAttribute("EmailSender", EmailList.get(IntNum).getFrom());
         model.addAttribute("EmailTitle", EmailList.get(IntNum).getTitle());
         model.addAttribute("EmailDate", EmailList.get(IntNum).getDate());
         model.addAttribute("EmailContent", EmailList.get(IntNum).getContent());
      }
      return constantDoEmail.getECURL();
   }

   private void GetUserInformation(Principal principal, User user, Model model) {
      this.ConstantHome = (ConstantHome) ctx.getBean("Home");

      String LoginID = principal.getName();// ????????? ??? ?????????
      ArrayList<String> SelectUserProfileInfo = new ArrayList<String>();
      SelectUserProfileInfo = userService.SelectUserProfileInfo(LoginID);
      user.setUserLoginID(LoginID);

      if (SelectUserProfileInfo.get(2).equals(this.ConstantHome.getSRole())) {
         ArrayList<String> StudentInfo = new ArrayList<String>();
         StudentInfo = studentService.SelectStudentProfileInfo(SelectUserProfileInfo.get(1));
         userInfoMethod.StudentInfo(model, SelectUserProfileInfo, StudentInfo);
      } else if (SelectUserProfileInfo.get(2).equals(this.ConstantHome.getPRole())) {
         ArrayList<String> ProfessorInfo = new ArrayList<String>();
         ProfessorInfo = professorService.SelectProfessorProfileInfo(SelectUserProfileInfo.get(1));
         userInfoMethod.ProfessorInfo(model, SelectUserProfileInfo, ProfessorInfo);
      } else if (SelectUserProfileInfo.get(2).equals(this.ConstantHome.getARole())) {
         userInfoMethod.AdministratorInfo(model, SelectUserProfileInfo);
      }
   }

}