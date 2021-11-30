package com.example.personio.interview.modules.hrmanager.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;

import com.example.personio.interview.modules.hrmanager.models.Employee;
import com.example.personio.interview.modules.hrmanager.models.EmployeeManagerDbModel;
import com.example.personio.interview.modules.hrmanager.models.EmployeeSkipManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class EmployeeDbOperationTest {

	@MockBean
	private EmployeeRepository employeeRepository;

	private EmployeeDbOperation employeeDbOperation;

	@BeforeEach
	public void setup() {
		this.employeeDbOperation = new EmployeeDbOperation(employeeRepository);
	}

	@Test
	public void testGetAllEmployees_ShouldPass() {
		List<Employee> mockAllEmp = getAllEmployees();
		Mockito.when(employeeRepository.findAll()).thenReturn(mockAllEmp);

		List<Employee> expected = mockAllEmp;
		List<Employee> actual = employeeDbOperation.getAllEmployees();
		assertEquals(expected.size(), actual.size());
		assertEquals(expected.get(0).getEmpName(), actual.get(0).getEmpName());
		assertEquals(expected.get(1).getEmpName(), actual.get(1).getEmpName());
		assertEquals(expected.get(1).getManagerId(), actual.get(1).getManagerId());
	}

	@Test
	public void testGetEmployeeByName_RecordNotFound_ShouldReturnNull() {
		String empName = "kunal";
		Mockito.when(employeeRepository.findByEmpName(any())).thenReturn(new ArrayList<Employee>());
		Employee actual = employeeDbOperation.getEmployeeByName(empName);
		assertNull(actual);
	}

	@Test
	public void testGetEmployeeByName_NullEmpNmae_ShouldReturnNull() {
		String empName = null;
		Mockito.when(employeeRepository.findByEmpName(any())).thenReturn(new ArrayList<Employee>());
		Employee actual = employeeDbOperation.getEmployeeByName(empName);
		assertNull(actual);
	}

	@Test
	public void testGetEmployeeByName_AllGood_ShouldPass() {
		String empName = "Jonas";
		Employee mockEmp = getMockEmp();
		Mockito.when(employeeRepository.findByEmpName(any())).thenReturn(new ArrayList<Employee>() {
			{
				add(mockEmp);
			}
		});
		Employee expected = mockEmp;
		Employee actual = employeeDbOperation.getEmployeeByName(empName);
		assertEquals(expected.getEmpName(), actual.getEmpName());
		assertNull(actual.getManagerId());
	}

	@Test
	public void testSaveEmployee_shouldPass() {
		String empName = "Jonas";
		Employee mockEmp = getMockEmp();
		Mockito.when(employeeRepository.save(any())).thenReturn(mockEmp);
		int actual = employeeDbOperation.saveEmployee(empName);
		assertEquals(1, actual);
	}

	@Test
	public void testSaveEmployeeWithManagerId_ShouldPass() {
		String empName = "Sophie";
		Employee mockEmp = getMockEmp();
		Mockito.when(employeeRepository.save(any())).thenReturn(mockEmp);
		int actual = employeeDbOperation.saveEmployee(empName, 2);
		assertEquals(1, actual);

		// TODO: assert scenario
	}

	@Test
	public void testUpdateEmployeeManager_ShouldPass() {
		Employee emp = getMockEmp();
		int managerId = 2;
		Mockito.when(employeeRepository.save(any())).thenReturn(emp);
		int actual = employeeDbOperation.updateEmployeeManager(emp, managerId);
		assertEquals(1, actual);
	}

	@Test
	public void testGetAllManagerSubordinates_ShouldPass() {
		List<EmployeeManagerDbModel> mockEmpSubordinateList = getAllManagerSubordinates();
		Mockito.when(employeeRepository.getAllManagerSubordinates()).thenReturn(mockEmpSubordinateList);
		List<EmployeeManagerDbModel> expected = mockEmpSubordinateList;
		List<EmployeeManagerDbModel> actual = employeeDbOperation.getAllManagerSubordinates();
		assertEquals(expected.size(), actual.size());
		assertEquals(expected.get(0).getManager(), actual.get(0).getManager());
		assertNull(actual.get(0).getSubordinate());
		assertEquals(expected.get(1).getManager(), actual.get(1).getManager());
		assertEquals(expected.get(1).getSubordinate(), actual.get(1).getSubordinate());
	}

	@Test
	public void testGetTopHierarchyEmployee_EmptyList_ShouldReturnNull() {
		Mockito.when(employeeRepository.getEmployeeWithManagerIdNull()).thenReturn(new ArrayList<Employee>());
		Employee actualValue = employeeDbOperation.getTopHierarchyEmployee();
		assertNull(actualValue);
	}

	@Test
	public void testGetTopHierarchyEmployee_NullList_ShouldReturnNull() {
		Mockito.when(employeeRepository.getEmployeeWithManagerIdNull()).thenReturn(null);
		Employee actualValue = employeeDbOperation.getTopHierarchyEmployee();
		assertNull(actualValue);
	}

	@Test
	public void testGetTopHierarchyEmployee_ShouldPass() {
		Employee mockEmp = getMockEmp();
		List<Employee> empListWithManagerIdNull = new ArrayList<Employee>() {
			{
				add(mockEmp);
			}
		};
		Mockito.when(employeeRepository.getEmployeeWithManagerIdNull()).thenReturn(empListWithManagerIdNull);
		Employee expected = mockEmp;
		Employee actualValue = employeeDbOperation.getTopHierarchyEmployee();
		assertEquals(expected.getEmpName(), actualValue.getEmpName());
		assertNull(actualValue.getManagerId());
	}

	@Test
	public void testGetEmployeeManagerAndSkipManager_NullList_ShouldReturnNull() {
		String empName = "Kunal";
		Mockito.when(employeeRepository.getEmployeeManagerAndSkipManager(any()))
		.thenReturn(new ArrayList<EmployeeSkipManager>());
		EmployeeSkipManager actualValue = employeeDbOperation.getEmployeeManagerAndSkipManager(empName);
		assertNull(actualValue);
		
	}

	@Test
	public void testGetEmployeeManagerAndSkipManager_EmptyList_ShouldReturnNull() {
		String empName = "Kunal";
		Mockito.when(employeeRepository.getEmployeeManagerAndSkipManager(any()))
		.thenReturn(null);
		EmployeeSkipManager actualValue = employeeDbOperation.getEmployeeManagerAndSkipManager(empName);
		assertNull(actualValue);
	}
	
	@Test
	public void testGetEmployeeManagerAndSkipManager_ReturnTopManager_ShouldPass() {
		String empName = "Jonas";
		EmployeeSkipManager mockEmpSkipManager = new EmployeeSkipManager();
		Mockito.when(employeeRepository.getEmployeeManagerAndSkipManager(any()))
		.thenReturn(new ArrayList<EmployeeSkipManager> (){
			{
				add(mockEmpSkipManager);
			}
		});
		EmployeeSkipManager actualValue = employeeDbOperation.getEmployeeManagerAndSkipManager(empName);
		assertNull(actualValue.getSupervisor());
		assertNull(actualValue.getSkipSupervisor());

	}

	@Test
	public void testGetEmployeeManagerAndSkipManager_ShouldPass() {
		String empName = "Jonas";
		EmployeeSkipManager mockEmpSkipManager = getEmployeeManagerAndSkipManager();
		Mockito.when(employeeRepository.getEmployeeManagerAndSkipManager(any()))
		.thenReturn(new ArrayList<EmployeeSkipManager> (){
			{
				add(mockEmpSkipManager);
			}
		});
		EmployeeSkipManager expected = mockEmpSkipManager;
		EmployeeSkipManager actualValue = employeeDbOperation.getEmployeeManagerAndSkipManager(empName);
		assertEquals(expected.getSupervisor(), actualValue.getSupervisor());
		assertEquals(expected.getSkipSupervisor(), actualValue.getSkipSupervisor());
	}

	private List<Employee> getAllEmployees() {
		List<Employee> allEmployees = new ArrayList<Employee>(){
			{
				add(new Employee("Jonas"));
				add(new Employee("Sophie" , 1));
				add(new Employee("Nick" , 2));
				add(new Employee("Pete" , 3));
				add(new Employee("Barbara", 4));
			}
		};
		return allEmployees;
	}

	private Employee getMockEmp() {
		Employee emp = new Employee("Sophie");
		emp.setEmpId(1);
		return emp;
	}

	private List<EmployeeManagerDbModel> getAllManagerSubordinates() {
		List<EmployeeManagerDbModel> managerSubordinateList = new ArrayList<EmployeeManagerDbModel>() {
			{
				add(new EmployeeManagerDbModel("Jonas", null));
				add(new EmployeeManagerDbModel("Sophie", "Jonas"));
				add(new EmployeeManagerDbModel("Nick", "Sophie"));
				add(new EmployeeManagerDbModel("Pete", "Nick"));
				add(new EmployeeManagerDbModel("Barbera", "Nick"));
			}
		};
		return managerSubordinateList;
	}
	private EmployeeSkipManager getEmployeeManagerAndSkipManager() {
		EmployeeSkipManager employeeSkipManager = new EmployeeSkipManager("Sophie","Jonas");
		return employeeSkipManager;
	}
}
