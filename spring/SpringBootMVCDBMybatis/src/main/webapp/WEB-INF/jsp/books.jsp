<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>도서 관리</h1>
	<table>
		<thead>
			<tr><th>bookId</th><th>bookName</th><th>publisher</th><th>price</th></tr>
		</thead>
		<tbody id="bookTbody">
			
		</tbody>
	</table>
	<hr>
	<!-- servlet, jsp 버전에서는 form 을 이용한 전송, action, post, submit 버튼 등 사용 
	     javascript 를 이용한 전송, action, post, submit 버튼 없다. 각각의 입력항목에 id 부여 -->
	<form>
		<input type="text" name="bookId" id="bookId"></input><br>
		<input type="text" name="bookName" id="bookName"></input><br>
		<input type="text" name="publisher" id="publisher"></input><br>
		<input type="text" name="price" id="price"></input><br>	
	</form>	
	<hr>
	<button id="btnInsert">등록</button> <button id="btnUpdate">수정</button> <button id="btnDelete">삭제</button> <button id="btnClear">초기화</button>
	
	<!-- 아래 script 는 브라우저에 다운로드 되어서 (jsp 의 결과로 포함되어서 ) 브라우저에 의해 실행 -->
	<script>
		
		window.onload = function(){
			// 도서 목록 요청 -> json 데이터 수신 -> 목록 UI 구성  <= Ajax
			listBook();
			
			document.querySelector("#btnClear").onclick = clearForm; // 초기화
			document.querySelector("#btnInsert").onclick = insertBook; // 등록
			document.querySelector("#btnUpdate").onclick = updateBook; // 수정
			document.querySelector("#btnDelete").onclick = deleteBook; // 삭제
		}
		
		async function listBook(){
			// 데이터 요청
			let url = '/books/list'; // 브라우저에서 요청, 확인한 url 을 그대로 사용
			let response = await fetch(url); // 비동기 요청
			let data = await response.json();
			
			console.log(data);
			
			// 화면 도서 목록 ui 구성
			makeListHtml(data);
		}
		
		function makeListHtml(list){
			let listHtml = ``;
			// 아래 comment 에도 \ 필요
			// JSP 의 EL 문법과 Javascript 의 \${} 의 문법이 동일.
			// JSP 파서가 \${} 을 EL 로 처리하려고 시도 => 오류
			// \ escape 문자 추가
			list.forEach( book => {
				listHtml += 
					`<tr style="cursor:pointer" data-bookId=\${book.bookId}>
						<td>\${book.bookId}</td>
						<td>\${book.bookName}</td>
						<td>\${book.publisher}</td>
						<td>\${book.price}</td>
					</tr>`;
			} );
			
			document.querySelector("#bookTbody").innerHTML = listHtml;
			
			document.querySelectorAll("#bookTbody tr").forEach( tr => {
				tr.onclick = function(){
					let bookId = this.getAttribute("data-bookId");
					detailBook(bookId);
				}
			} );
		}
		
		async function detailBook(bookId){
			
			// 데이터 요청
			let url = '/books/detail/' + bookId;
			let response = await fetch(url); // 비동기 요청
			let data = await response.json();
			
			console.log(data);
			
			// 화면 폼에 data 개별 항목을 표시
			document.querySelector("#bookId").value = data.bookId;
			document.querySelector("#bookName").value = data.bookName;
			document.querySelector("#publisher").value = data.publisher;
			document.querySelector("#price").value = data.price;
		}
		
		function clearForm(){
			document.querySelector("#bookId").value = "";
			document.querySelector("#bookName").value = "";
			document.querySelector("#publisher").value = "";
			document.querySelector("#price").value = "";			
		}
		
		async function insertBook(){
			// 사용자 입력을 통해 javascript book 객체 생성
			// post 전송 ( x-www.form-urlencoded 방식 => URLSearchParams 객체 이용 ) 

			let book = {
				bookId: document.querySelector("#bookId").value,
				bookName: document.querySelector("#bookName").value,
				publisher: document.querySelector("#publisher").value,
				price: document.querySelector("#price").value
			};
			
			let urlParams = new URLSearchParams(book);
			
			let fetchOptions = {
				method: "post",
				body: urlParams
			}
			
			let url = '/books/insert';
			let response = await fetch(url, fetchOptions); // 비동기 요청, fetch Option
			let data = await response.json();
			
			console.log(data);
			
			if( data.result == "success"){
				alert("도서 등록 성공!");
				listBook(); // 도서 목록 갱신
				clearForm(); // 입력 폼 초기화
			}else{
				alert("도서 등록 실패!");
			}
		}
		
		async function updateBook(){
			// 별도의 객체 생성을 거치지 않고 바로 URLSearchParams 객체 생성
			let urlParams = new URLSearchParams({
				bookId: document.querySelector("#bookId").value,
				bookName: document.querySelector("#bookName").value,
				publisher: document.querySelector("#publisher").value,
				price: document.querySelector("#price").value
			});
			
			let fetchOptions = {
				method: "post",
				body: urlParams
			}
			
			let url = '/books/update';
			let response = await fetch(url, fetchOptions); // 비동기 요청, fetch Option
			let data = await response.json();
			
			console.log(data);

			if( data.result == "success"){
				alert("도서 수정 성공!");
				listBook(); // 도서 목록 갱신
				clearForm(); // 입력 폼 초기화
			}else{
				alert("도서 수정 실패!");
			}			
		}
		
		async function deleteBook(){
			let bookId = document.querySelector("#bookId").value;
			let url = '/books/delete/' + bookId;
			let response = await fetch(url); // 비동기 요청
			let data = await response.json();
			
			console.log(data);

			if( data.result == "success"){
				alert("도서 삭제 성공!");
				listBook(); // 도서 목록 갱신
				clearForm(); // 입력 폼 초기화
			}else{
				alert("도서 삭제 실패!");
			}				
		}

	</script>
</body>
</html>