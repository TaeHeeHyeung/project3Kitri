<%@page import="com.kitri.single.user.model.UserDto"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/commons/template/modern_business_top.jsp"%>
<% 
HttpSession httpSession = request.getSession();
UserDto userInfo = new UserDto();
userInfo.setUserId("calubang2@naver.com");
httpSession.setAttribute("userInfo", userInfo);
System.out.println((UserDto)httpSession.getAttribute("userInfo"));
%>
<script>
$(location).attr("href", "/single/group");
</script>