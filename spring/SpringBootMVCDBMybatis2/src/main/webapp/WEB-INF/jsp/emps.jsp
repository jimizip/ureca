<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>사원 관리</title>
</head>
<body>
	<h1>사원 관리</h1>
	<table>
		<thead>
			<tr><th>employeeId</th><th>firstName</th><th>lastName</th><th>email</th><th>hireDate</th></tr>
		</thead>
		<tbody id="empTbody">
			
		</tbody>
	</table>
	<hr>
	<!-- servlet, jsp 버전에서는 form 을 이용한 전송, action, post, submit 버튼 등 사용 
	     javascript 를 이용한 전송, action, post, submit 버튼 없다. 각각의 입력항목에 id 부여 -->
	<form>
		<input type="text" name="employeeId" id="employeeId"></input><br>
		<input type="text" name="firstName" id="firstName"></input><br>
		<input type="text" name="lastName" id="lastName"></input><br>
		<input type="text" name="email" id="email"></input><br>	
		<input type="text" name="hireDate" id="hireDate"></input><br>	
	</form>	
	<hr>
	<button id="btnInsert">등록</button> <button id="btnUpdate">수정</button> <button id="btnDelete">삭제</button> <button id="btnClear">초기화</button>
	
	<script>
		
		window.onload = function(){
			// 사원 목록 요청 -> json 데이터 수신 -> 목록 UI 구성  <= Ajax
			listEmp();
			
			document.querySelector("#btnClear").onclick = clearForm; // 초기화
			document.querySelector("#btnInsert").onclick = insertEmp; // 등록
			document.querySelector("#btnUpdate").onclick = updateEmp; // 수정
			document.querySelector("#btnDelete").onclick = deleteEmp; // 삭제
		}
		
		async function listEmp(){
			// 데이터 요청
			let url = '/emps/list'; // 브라우저에서 요청, 확인한 url 을 그대로 사용
			let response = await fetch(url); // 비동기 요청
			let data = await response.json();
			
			console.log(data);
			
			// 화면 사원 목록 ui 구성
			makeListHtml(data);
		}
		
		function makeListHtml(list){
			let listHtml = ``;
			// 아래 comment 에도 \ 필요
			// JSP 의 EL 문법과 Javascript 의 \${} 의 문법이 동일.
			// JSP 파서가 \${} 을 EL 로 처리하려고 시도 => 오류
			// \ escape 문자 추가
			list.forEach( emp => {
				listHtml += 
					`<tr style="cursor:pointer" data-employeeId=\${emp.employeeId}>
						<td>\${emp.employeeId}</td>
						<td>\${emp.firstName}</td>
						<td>\${emp.lastName}</td>
						<td>\${emp.email}</td>
						<td>\${emp.hireDate}</td>
					</tr>`;
			} );
			
			document.querySelector("#empTbody").innerHTML = listHtml;
			
			document.querySelectorAll("#empTbody tr").forEach( tr => {
				tr.onclick = function(){
					let employeeId = this.getAttribute("data-employeeId");
					detailEmp(employeeId);
				}
			} );
		}
		
		async function detailEmp(employeeId){
			
			// 데이터 요청
			let url = '/emps/detail/' + employeeId; // path variable 대응 코드
			let response = await fetch(url); // 비동기 요청
			let data = await response.json();
			
			console.log(data);
			
			// 화면 폼에 data 개별 항목을 표시
			document.querySelector("#employeeId").value = data.employeeId;
			document.querySelector("#firstName").value = data.firstName;
			document.querySelector("#lastName").value = data.lastName;
			document.querySelector("#email").value = data.email;
			document.querySelector("#hireDate").value = data.hireDate;
		}
		
		function clearForm(){
			document.querySelector("#employeeId").value = "";
			document.querySelector("#firstName").value = "";
			document.querySelector("#lastName").value = "";
			document.querySelector("#email").value = "";			
			document.querySelector("#hireDate").value = "";			
		}
		
		async function insertEmp(){
			// 사용자 입력을 통해 javascript book 객체 생성
			// post 전송 ( x-www.form-urlencoded 방식 => URLSearchParams 객체 이용 ) 

			let emp = {
				employeeId: document.querySelector("#employeeId").value,
				firstName: document.querySelector("#firstName").value,
				lastName: document.querySelector("#lastName").value,
				email: document.querySelector("#email").value,
				hireDate: document.querySelector("#hireDate").value
			};
			
			let urlParams = new URLSearchParams(emp);
			
			let fetchOptions = {
				method: "post",
				body: urlParams
			}
			
			let url = '/emps/insert';
			let response = await fetch(url, fetchOptions); // 비동기 요청, fetch Option
			let data = await response.json();
			
			console.log(data);
			
			if( data.result == "success"){
				alert("사원 등록 성공!");
				listEmp(); // 사원 목록 갱신
				clearForm(); // 입력 폼 초기화
			}else{
				alert("사원 등록 실패!");
			}
		}
		
		async function updateEmp(){
			// 별도의 객체 생성을 거치지 않고 바로 URLSearchParams 객체 생성
			let urlParams = new URLSearchParams({
				employeeId: document.querySelector("#employeeId").value,
				firstName: document.querySelector("#firstName").value,
				lastName: document.querySelector("#lastName").value,
				email: document.querySelector("#email").value,
				hireDate: document.querySelector("#hireDate").value
			});
			
			let fetchOptions = {
				method: "post",
				body: urlParams
			}
			
			let url = '/emps/update';
			let response = await fetch(url, fetchOptions); // 비동기 요청, fetch Option
			let data = await response.json();
			
			console.log(data);

			if( data.result == "success"){
				alert("사원 수정 성공!");
				listEmp(); // 사원 목록 갱신
				clearForm(); // 입력 폼 초기화
			}else{
				alert("사원 수정 실패!");
			}			
		}
		
		async function deleteEmp(){
			let employeeId = document.querySelector("#employeeId").value;
			let url = '/emps/delete/' + employeeId; // path variable 대응 코드
			let response = await fetch(url); // 비동기 요청
			let data = await response.json();
			
			console.log(data);

			if( data.result == "success"){
				alert("사원 삭제 성공!");
				listEmp(); // 사원 목록 갱신
				clearForm(); // 입력 폼 초기화
			}else{
				alert("사원 삭제 실패!");
			}				
		}

	</script>
</body>
</html>