package com.mycom.myapp.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mycom.myapp.dao.EmpDao;
import com.mycom.myapp.dto.EmpDto;

@Service
public class EmpServiceImpl implements EmpService{
	
	// 생성자 주입
	private final EmpDao empDao;
	
	public EmpServiceImpl(EmpDao empDao) {
		this.empDao = empDao;
	}
	
	@Override
	public List<EmpDto> listEmp() {
		return empDao.listEmp();
	}

	@Override
	public EmpDto detailEmp(int employeeId) {
		return empDao.detailEmp(employeeId);
	}

	@Override
	public int insertEmp(EmpDto empDto) {
		return empDao.insertEmp(empDto);
	}

	@Override
	public int updateEmp(EmpDto empDto) {
		return empDao.updateEmp(empDto);
	}

	@Override
	public int deleteEmp(int employeeId) {
		return empDao.deleteEmp(employeeId);
	}

	@Override
	public List<EmpDto> listEmpLike(String searchWord) {
		return empDao.listEmpLike(searchWord);
	}

	@Override
	public List<EmpDto> listEmpMap() {
		return empDao.listEmpMap();
	}

	@Override
	public List<EmpDto> listEmpWhereIf(Map<String, String> map) {
		return empDao.listEmpWhereIf(map);
	}

}
