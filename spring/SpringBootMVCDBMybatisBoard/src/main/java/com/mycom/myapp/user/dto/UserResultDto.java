package com.mycom.myapp.user.dto;

// 사용자 도메인 관련 표준 응답 구조
public class UserResultDto {
	private String result; // "success", "fail" <= 코드화도 가능

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
