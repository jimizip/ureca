package com.mycom.myapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mycom.myapp.dto.EmpDto;
import com.mycom.myapp.service.EmpService;

// 사원 관리 controller
@Controller
@ResponseBody // 이 컨트롤러의 전체 메소드의 응답이 모두 json 처리
@RequestMapping("/emps")
public class EmpController {

	// 생성자 주입
	private final EmpService empService;
	
	public EmpController(EmpService empService) {
		this.empService = empService;
	}
	
	// 목록
	@GetMapping("/list")	
	public List<EmpDto> listEmp(){
		return empService.listEmp();
	}
	
	// 상세
	@GetMapping("/detail/{employeeId}")	
	public EmpDto detailEmp(@PathVariable Integer employeeId){
		return empService.detailEmp(employeeId);
	}
	
	// 등록
	@PostMapping("/insert")
	public Map<String, String> insertEmp(EmpDto empDto){
		int ret = empService.insertEmp(empDto);
		Map<String, String> map = new HashMap<>();
		if( ret == 1 ) {
			map.put("result", "success");
		}else {
			map.put("result", "fail");
		}
		return map;
	}
	
	// 수정
	@PostMapping("/update")
	public Map<String, String> updateEmp(EmpDto empDto){
		int ret = empService.updateEmp(empDto);
		Map<String, String> map = new HashMap<>();
		if( ret == 1 ) {
			map.put("result", "success");
		}else {
			map.put("result", "fail");
		}
		return map;
	}
	
	// 삭제
	@GetMapping("/delete/{employeeId}")
	public Map<String, String> deleteEmp(@PathVariable Integer employeeId){
		int ret = empService.deleteEmp(employeeId);
		Map<String, String> map = new HashMap<>();
		if( ret == 1 ) {
			map.put("result", "success");
		}else {
			map.put("result", "fail");
		}
		return map;
	}
	
	
	@GetMapping("/listEmpLike")
	public List<EmpDto> listEmpLike(@RequestParam String serchWord){
		return empService.listEmpLike(serchWord);
	}
	
	@GetMapping("/listEmpMap")
	public List<EmpDto> listEmpMap(){
		return empService.listEmpMap();
	}
	
	@GetMapping("/listEmpWhereIf")
	public List<EmpDto> listEmpWhereIf(@RequestParam Map<String, String> map){
		return empService.listEmpWhereIf(map);
	}
}














