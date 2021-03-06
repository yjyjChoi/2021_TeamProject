<!-- 게시판 리스트 출력 화면 -->

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="path" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<link rel="preconnect" href="https://fonts.gstatic.com">
<link
	href="https://fonts.googleapis.com/css2?family=Open+Sans&display=swap"
	rel="stylesheet">
<link rel="stylesheet" href="css/boardList.css" type="text/css">
<link rel="stylesheet" href="css/menubar.css" type="text/css">
<title>community list</title>
</head>
<body>
	<jsp:include page="/WEB-INF/views/homeView/menubar.jsp"></jsp:include>
	<div class="mbody">
		<div class="mcontWidth">
			<jsp:include page="/WEB-INF/views/homeView/userInfoBox.jsp"></jsp:include> 
			<div class="rightBox">
				<section>
					<div class="section">
						<br>
						<h2>커뮤니티</h2>
						<hr>
					</div>
				</section>

				<section>
					<div class="section2">
						<div id="search">
							<select name="SelectOption" id="selectOption">
								<option value="all">전체</option>
							</select> <input type="text" placeholder="검색어 입력하세요."> <input
								type="submit" value="검색">
						</div>

						<form action="" name="CommunityList" method="POST" id="form">
							<input type="hidden" name="${_csrf.parameterName}"
								value="${_csrf.token}" />
							<table id="communityList">
								<thead>
									<tr>
										<th class="col1">번호</th>
										<th class="col2">제목</th>
										<th class="col3">작성자</th>
										<th class="col4">작성일</th>
										<th class="col6">조회수</th>
									</tr>
									<hr>
								</thead>
								<tbody>
									<c:forEach items="${communityList}" var="communityList"
										varStatus="status">
										<tr>
											<td><c:out value="${communityList.getBoardID()}" /></td>
											<td><a href="${path}/communityContent?no=${communityList.getBoardID()}">
												<c:out value="${communityList.getBoardSubject()}" /></a></td>
											<td><c:out value="${communityList.getBoardWriter()}" /></td>
											<td><c:out value="${communityList.getBoardDate()}" /></td>
											<td><c:out value="${communityList.getBoardHit()}" /></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							<hr>
							<div id="page" class="btn">
								<input type="button" value="←" id="leftList"> <input
									type="button" value="1" id="pageList"> <input
									type="button" value="→" id="rightList"> <a
									href="${path}/communityWrite"><input type="button"
									value="글쓰기" id="write"></a>
							</div>
						</form>
					</div>
					<!-- section2 -->
				</section>
			</div>
			<!-- right_box -->


		</div>
		<!-- mcont_width -->
	</div>
	<!-- mbody -->
</body>
</html>