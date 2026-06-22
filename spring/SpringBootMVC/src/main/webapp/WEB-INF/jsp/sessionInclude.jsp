<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // session 에 없으면 null 리턴 
    String username = (String) session.getAttribute("username");
%>
<div>
<%if( username == null ){ %>
    <div>로그인 하세요.</div>
<%}else{ %>   
    <div>[<%=username %>] 님 반갑습니다.</div>
    
<%} %>        
</div>
<!-- jsp session=true 가 default -->