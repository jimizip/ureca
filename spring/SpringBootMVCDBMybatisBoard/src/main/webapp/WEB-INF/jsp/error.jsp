<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4" crossorigin="anonymous"></script>

<title>예외 발생</title>
</head>
<body>
	<h1>서버에 문제가 발생했습니다.</h1>
	<hr>
	<h4>요청하신 URL : <%= request.getRequestURI()%></h4>
	<h4>발생한 예외 : <%= exception.getMessage()%></h4>
	<hr>
	<h4>요청하신 URL : <%= request.getAttribute("requestURI")%></h4>
	<h4>발생한 예외 : <%= request.getAttribute("exception")%></h4>	
</body>
</html>