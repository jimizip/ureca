package com.mycom.myapp.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.mycom.myapp.dto.EmpDto;

@Mapper
public interface EmpDao {

	// emp crud
	// emp-mapper.xml
	List<EmpDto> listEmp();
	EmpDto detailEmp(int employeeId);
	int insertEmp(EmpDto empDto);
	int updateEmp(EmpDto empDto);
	int deleteEmp(int employeeId);
	
	// emp 검색, ResultMap, 동적 SQL
	// emp-mapper-2.xml
	List<EmpDto> listEmpLike(String searchWord); // email
	List<EmpDto> listEmpMap();
	List<EmpDto> listEmpWhereIf(Map<String, String> map);
}
