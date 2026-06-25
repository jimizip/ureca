# API 명세

도서(Book) CRUD API. `/books` 만 JSP 뷰를 반환하고, 나머지는 `@ResponseBody` 로 JSON 응답한다. SQL 실행은 MyBatis 매퍼(`book-mapper.xml`)가 담당한다.

## 엔드포인트 목록

| Method | URL | 설명 | 응답 형태 |
|--------|-----|------|-----------|
| GET | `/books` | 도서 관리 화면(books.jsp) | HTML(JSP) |
| GET | `/books/list` | 도서 목록 조회 | JSON 배열 |
| GET | `/books/detail/{bookId}` | 도서 상세 조회 | JSON 객체 |
| POST | `/books/insert` | 도서 등록 | JSON `{result}` |
| POST | `/books/update` | 도서 수정 | JSON `{result}` |
| GET | `/books/delete/{bookId}` | 도서 삭제 | JSON `{result}` |

## 상세 명세

### GET /books
도서 관리 화면(`books.jsp`)으로 이동한다. 데이터 응답이 아닌 뷰 반환.

---

### GET /books/list
전체 도서 목록을 JSON 배열로 반환한다. (매퍼 `listBook`)

**요청 파라미터**: 없음

**응답 예시**
```json
[
  { "bookId": 1, "bookName": "축구의 역사", "publisher": "굿스포츠", "price": 7000 },
  { "bookId": 2, "bookName": "축구 아는 여자", "publisher": "나무수", "price": 13000 }
]
```

---

### GET /books/detail/{bookId}
`bookId` 에 해당하는 도서 한 건을 반환한다. (매퍼 `detailBook`)

**요청 파라미터**

| 위치 | 이름 | 타입 | 설명 |
|------|------|------|------|
| Path | `bookId` | Integer | 도서 ID |

**응답 예시**
```json
{ "bookId": 1, "bookName": "축구의 역사", "publisher": "굿스포츠", "price": 7000 }
```

> 해당 ID 가 없으면 본문은 빈 값(null)이 된다.

---

### POST /books/insert
도서를 등록한다. (매퍼 `insertBook`)

**요청 파라미터** (form 파라미터로 `BookDto` 바인딩)

| 이름 | 타입 | 설명 |
|------|------|------|
| `bookId` | int | 도서 ID |
| `bookName` | String | 도서명 |
| `publisher` | String | 출판사 |
| `price` | int | 가격 |

**응답 예시**
```json
{ "result": "success" }
```
처리 행 수가 1이면 `success`, 아니면 `fail`.

---

### POST /books/update
도서를 수정한다. `bookId` 기준으로 `bookName`, `publisher`, `price` 갱신. (매퍼 `updateBook`)

**요청 파라미터**: insert 와 동일 (`bookId`, `bookName`, `publisher`, `price`)

**응답 예시**
```json
{ "result": "success" }
```

---

### GET /books/delete/{bookId}
`bookId` 에 해당하는 도서를 삭제한다. (매퍼 `deleteBook`)

**요청 파라미터**

| 위치 | 이름 | 타입 | 설명 |
|------|------|------|------|
| Path | `bookId` | Integer | 삭제할 도서 ID |

**응답 예시**
```json
{ "result": "success" }
```
처리 행 수가 1이면 `success`, 아니면 `fail`.
