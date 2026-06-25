# API 명세

사원(Emp) 관리 API. 컨트롤러가 두 개로 분리된다.
- `PageController` : JSP 뷰 이동 (HTML 응답)
- `EmpController` : `/emps` prefix, 클래스 레벨 `@ResponseBody` 로 전체 JSON 응답

## 엔드포인트 목록

### 뷰 이동 (PageController)

| Method | URL | 설명 | 응답 형태 |
|--------|-----|------|-----------|
| GET | `/emps` | 사원 관리 화면(emps.jsp) | HTML(JSP) |
| GET | `/salaries` | 급여 화면(salaries.jsp) | HTML(JSP) |
| GET | `/stores` | 매장 화면(stores.jsp) | HTML(JSP) |

### 데이터 API (EmpController, 전체 JSON)

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/emps/list` | 사원 목록 조회 |
| GET | `/emps/detail/{employeeId}` | 사원 상세 조회 |
| POST | `/emps/insert` | 사원 등록 |
| POST | `/emps/update` | 사원 수정 |
| GET | `/emps/delete/{employeeId}` | 사원 삭제 |
| GET | `/emps/listEmpLike` | email 접두사 LIKE 검색 |
| GET | `/emps/listEmpMap` | 일부 필드만(resultMap) 목록 |
| GET | `/emps/listEmpWhereIf` | 동적 SQL 조건 검색 |

## 상세 명세

### GET /emps/list
전체 사원 목록을 JSON 배열로 반환. (매퍼 `listEmp`)

**응답 예시**
```json
[
  { "employeeId": 100, "firstName": "Steven", "lastName": "King", "email": "SKING", "hireDate": "2003-06-17" }
]
```

---

### GET /emps/detail/{employeeId}
`employeeId` 사원 한 건 반환. (매퍼 `detailEmp`)

| 위치 | 이름 | 타입 | 설명 |
|------|------|------|------|
| Path | `employeeId` | Integer | 사원 ID |

---

### POST /emps/insert
사원 등록. (매퍼 `insertEmp`)

**요청 파라미터** (form 파라미터로 `EmpDto` 바인딩)

| 이름 | 타입 | 설명 |
|------|------|------|
| `employeeId` | int | 사원 ID |
| `firstName` | String | 이름 |
| `lastName` | String | 성 |
| `email` | String | 이메일 |
| `hireDate` | String | 입사일 |

**응답 예시**
```json
{ "result": "success" }
```
처리 행 수 1이면 `success`, 아니면 `fail`.

> 주의: 매퍼의 insert 문에서 `hire_date` 가 `${hireDate}` 로 작성되어 있다. `#{hireDate}` 로 바꿔야 안전하고 따옴표 문제도 없다 (README 코드 점검 참고).

---

### POST /emps/update
사원 수정. `employeeId` 기준으로 나머지 필드 갱신. (매퍼 `updateEmp`)

**요청 파라미터**: insert 와 동일

**응답 예시**
```json
{ "result": "success" }
```

---

### GET /emps/delete/{employeeId}
`employeeId` 사원 삭제. (매퍼 `deleteEmp`)

| 위치 | 이름 | 타입 | 설명 |
|------|------|------|------|
| Path | `employeeId` | Integer | 삭제할 사원 ID |

---

### GET /emps/listEmpLike
email 이 검색어로 **시작** 하는 사원 목록. (매퍼 `listEmpLike`, `like concat(?, '%')`)

**요청 파라미터**

| 위치 | 이름 | 타입 | 설명 |
|------|------|------|------|
| Query | `serchWord` | String | email 접두사 검색어 |

> 파라미터명이 코드상 `serchWord` 오타다. 요청 시 `?serchWord=...` 로 보내야 한다 (README 코드 점검 참고).

**예시 요청**: `GET /emps/listEmpLike?serchWord=SK`

---

### GET /emps/listEmpMap
`employeeId`, `firstName`, `lastName` 일부 필드만 매핑한 목록. (매퍼 `listEmpMap`, `<resultMap>`)

**응답 예시**
```json
[
  { "employeeId": 100, "firstName": "Steven", "lastName": "King", "email": null, "hireDate": null }
]
```

---

### GET /emps/listEmpWhereIf
동적 SQL 조건 검색. 전달한 쿼리 파라미터(`firstName`, `lastName`, `email`)만 `WHERE` 조건으로 조립된다. (매퍼 `listEmpWhereIf`, `<where>`/`<if>`)

**요청 파라미터** (모두 선택, `@RequestParam Map` 으로 수신)

| 위치 | 이름 | 타입 | 설명 |
|------|------|------|------|
| Query | `firstName` | String | 이름 일치 조건 (선택) |
| Query | `lastName` | String | 성 일치 조건 (선택) |
| Query | `email` | String | 이메일 일치 조건 (선택) |

**예시 요청**: `GET /emps/listEmpWhereIf?firstName=Steven&email=SKING`

> 파라미터를 하나도 주지 않으면 `WHERE` 절 없이 전체 조회된다.
