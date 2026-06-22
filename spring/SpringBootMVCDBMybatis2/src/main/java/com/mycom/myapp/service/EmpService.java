package com.mycom.myapp.service;

import java.util.List;
import java.util.Map;

import com.mycom.myapp.dto.EmpDto;

public interface EmpService {
	// emp crud
	List<EmpDto> listEmp();
	EmpDto detailEmp(int employeeId);
	int insertEmp(EmpDto empDto);
	int updateEmp(EmpDto empDto);
	int deleteEmp(int employeeId);
	
	// emp 검색, ResultMap, 동적 SQL
	List<EmpDto> listEmpLike(String searchWord); // email
	List<EmpDto> listEmpMap();
	List<EmpDto> listEmpWhereIf(Map<String, String> map);
}
