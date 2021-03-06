<!-- 문의 내용 출력 화면 -->

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<c:set var="path" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<link rel="preconnect" href="https://fonts.gstatic.com">
<link
	href="https://fonts.googleapis.com/css2?family=Open+Sans&display=swap"
	rel="stylesheet">
<link rel="stylesheet" href="css/boardContent.css" type="text/css">
<link rel="stylesheet" href="css/menubar.css" type="text/css">

<title>inquiry content</title>
</head>
<body>
	<jsp:include page="/WEB-INF/views/homeView/menubar.jsp"></jsp:include>
	<nav>
		<div class="mbody">
			<div class="mcontWidth">

				<jsp:include page="/WEB-INF/views/homeView/userInfoBox.jsp"></jsp:include> 
				<div class="rightBox">
					<section>
						<div class="section">
							<br>
							<h2>문의 내용</h2>
							<hr>
						</div>
					</section>
					<section>
						<form action="inquiryDelete.do" name="DataDelete" method="POST"
							id="form">
							<div class="section2">
								<input type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" />
								<table id="contentTable">
									<tr id="title">
										<td><input type="text" name="InquiryTitle"
											id="inquiryTitle" class="inputBox" placeholder="제목" disabled
											readonly value=${InquiryTitle}></td>
									</tr>
									<tr id="receive">
										<td colspan="2"><input type="text" name="InquiryWriter"
											id="inquiryWriter" class="inputBox" placeholder="작성자"
											disabled readonly value=${InquiryWriter}> <input
											type="text" name="Date" id="date" class="inputBox"
											placeholder="날짜" disabled readonly value=${BulletinDate}>
										</td>
									</tr>
									<tr id="content">
										<td><textarea name="InquiryContent" id="inquiryContent"
												class="inputBox" placeholder="내용" disabled readonly></textarea>
											<c:out value="${InquiryContent}" escapeXml="false" /></td>
									</tr>
								</table>
								<hr>
							</div>

							<div id="btn">
								<a href="inquiryList"><input type="button" value="목록"
									id="listButton"></a>
								<!-- 접속한 UserID와 해당 글을 작성한 UserID가 같을 때 수정/삭제 버튼 보이게 하기 -->
								<c:set var="UserID" value="${UserID}" />
								<c:set var="UserIDFromWriter" value="${UserIDFromWriter}" />
								<c:if test="${UserID == UserIDFromWriter}">
									<button type="submit" id="deleteButton">삭제</button>
								</c:if>
							</div>
						</form>
						<br>
						<hr>
						
						<form action="Delete.do" name="Delete" method="POST"
							id="form">
							<!-- 답변이 null이 아닐 때만 출력 가능하게 해야함 answerList? -->
							<c:if test="${answerList != null}">
							<table>
							<tr>
								<td><label for="inquiryAnswerContent"><b>답변</b>&nbsp;&nbsp;&nbsp;</label></td>
								<td><textarea name="InquiryAnswerContent" id="inquiryAnswerContent" class="inputBox" disabled readonly></textarea>&nbsp;&nbsp;</td>
								<!-- 관리자만 답변 삭제 가능 -->
								<sec:authorize access="hasRole('ROLE_ADMIN')">
								<td><input type="submit" value="삭제" id="deleteButton"></td>
								</sec:authorize>
							</tr>
							</table>
							</c:if>
						</form>
						
						<br><br>
						
						<!-- 관리자만 답변 작성 가능 -->
						<form action="Answer.do" name="Answer" method="POST"
							id="form">
							<sec:authorize access="hasRole('ROLE_ADMIN')">
							<table>
							<tr>
								<td><label for="inquiryAnswer"><b>답변 작성</b>&nbsp;</label></td>
								<td><textarea name="InquiryAnswer" id="inquiryAnswer" class="inputBox" placeholder="내용"></textarea>&nbsp;&nbsp;</td>
								<td><input type="submit" value="저장" id="saveButton"></td>
								<!-- input hidden으로 관리자 아이디 값 넘겨야 함! -->
							</tr>
							</table>
							</sec:authorize>
						</form>
						<!-- section2 -->
					</section>
				</div>
				<!-- right_box -->
			</div>
			<!-- mcont_width -->
		</div>
		<!-- mbody -->
	</nav>
</body>
</html>