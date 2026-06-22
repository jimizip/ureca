<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mycom.myapp.dto.CarDto"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>viewTest3.jsp</h1>
<%
	String seq = (String) request.getAttribute("seq");
	CarDto carDto = (CarDto) request.getAttribute("carDto");
%>	
	<hr>
	<p>seq : <%= seq %></p>
	<p>car name  : <%= carDto.getName() %></p>
	<p>car price : <%= carDto.getPrice() %></p>
	<p>car owner : <%= carDto.getOwner() %></p>
</body>
</html>