package com.example.personio.interview.modules.hrmanager.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.personio.interview.exceptions.LoopHierarchyException;
import com.example.personio.interview.exceptions.MultipleRootsException;
import com.example.personio.interview.exceptions.PersonioBadRequestException;
import com.example.personio.interview.exceptions.UnableToFetchDataFromDbException;
import com.example.personio.interview.exceptions.UnableToSaveInDbException;
import com.example.personio.interview.modules.hrmanager.models.Employee;
import com.example.personio.interview.modules.hrmanager.models.EmployeeManagerDbModel;
import com.example.personio.interview.modules.hrmanager.models.EmployeeSkipManager;
import com.example.personio.interview.modules.hrmanager.models.processors.EmployeeProcessor;
import com.example.personio.interview.modules.hrmanager.repository.EmployeeDbOperation;
import com.example.personio.interview.utils.httputils.Response;
import com.example.personio.interview.utils.httputils.ResponseGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class HRManagerServiceTest {
	
	@MockBean
	private EmployeeDbOperation employeeDbOperation;

	@MockBean
	private EmployeeProcessor processor;
	
	@MockBean
	private ResponseGenerator responseGenerator;

	private HRManagerService hRManagerService;

	@BeforeEach
	public void setup() {
		this.hRManagerService = new HRManagerService(employeeDbOperation, processor, responseGenerator);
	}

	@Test
	public void testGetEmployeesHierarchy_ValidInput_ShouldPass() {
		Employee topHierarchyEmployee = new Employee("Jonas");
		List<Employee> allEmployees = getAllEmployees();
		List<String> allEmpNames = getAllEmployeesNames(allEmployees);
		List<EmployeeManagerDbModel> managerSubordinateList = getAllManagerSubordinates();
		Map<String, List<String>> empManagerMap = getManagerSubordinateMap();
		String employeeJsonHierarchy = getEmployeeJsonHierarchy();
		Mockito.when(employeeDbOperation.getTopHierarchyEmployee()).thenReturn(topHierarchyEmployee);
		Mockito.when(employeeDbOperation.getAllEmployees()).thenReturn(allEmployees);
		Mockito.when(processor.getAllEmployeesNames(any())).thenReturn(allEmpNames);
		Mockito.when(employeeDbOperation.getAllManagerSubordinates()).thenReturn(managerSubordinateList);
		Mockito.when(processor.getEmployeeManagerMap(any())).thenReturn(empManagerMap);
		Mockito.when(processor.createEmployeeJsonHierarchy(any(), any(), any())).thenReturn(employeeJsonHierarchy);
		String expected = employeeJsonHierarchy;
		String actual = hRManagerService.getEmployeesHierarchy();
		assertEquals(expected, actual);
	}

	@Test
	public void testGetEmployeesHierarchy_NullTopHierarchyManager_ShouldReturnEmptyJson() {
		Employee topHierarchyEmployee = null;
		Mockito.when(employeeDbOperation.getTopHierarchyEmployee()).thenReturn(topHierarchyEmployee);
		String expected = "";
		String actual = hRManagerService.getEmployeesHierarchy();
		assertEquals(expected, actual);
	}

	@Test
	public void testGetEmployeesHierarchy_DatabaseConnectionFail_ShouldThrowException() {
		Mockito.when(employeeDbOperation.getTopHierarchyEmployee()).thenThrow(DataAccessResourceFailureException.class);
		assertThrows(UnableToFetchDataFromDbException.class, () -> {
			hRManagerService.getEmployeesHierarchy();
		});
	}


	@Test
	public void testGetEmployeeManagerAndSkipManager_EmptyEmloyeeName_ShouldThrowException() {
		String empName = "";
		assertThrows(PersonioBadRequestException.class, () -> {
			hRManagerService.getEmployeeManagerAndSkipManager(empName);
		});
	}

	@Test
	public void testGetEmployeeManagerAndSkipManager_NullEmloyeeName_ShouldThrowException() {
		String empName = null;
		assertThrows(PersonioBadRequestException.class, () -> {
			hRManagerService.getEmployeeManagerAndSkipManager(empName);
		});
	}

	@Test
	public void testGetEmployeeManagerAndSkipManager_ValidEmloyeeName_ShouldPass() {
		String empName = "Nick";
		EmployeeSkipManager mockEmpSkipManager = getEmployeeManagerAndSkipManager();
		Mockito.when(employeeDbOperation.getEmployeeManagerAndSkipManager(any())).thenReturn(mockEmpSkipManager);
		EmployeeSkipManager expected = mockEmpSkipManager;
		EmployeeSkipManager actual = hRManagerService.getEmployeeManagerAndSkipManager(empName);
		assertEquals(expected.getSupervisor(), actual.getSupervisor());
		assertEquals(expected.getSkipSupervisor(), actual.getSkipSupervisor());
	}

	@Test
	public void testGetEmployeeManagerAndSkipManager_EmployeeNotFound_ShouldReturnNull() {
		String empName = "Kunal";
		EmployeeSkipManager mockEmpSkipManager = null;
		Mockito.when(employeeDbOperation.getEmployeeManagerAndSkipManager(any())).thenReturn(mockEmpSkipManager);
		EmployeeSkipManager actual = hRManagerService.getEmployeeManagerAndSkipManager(empName);
		assertNull(actual);

	}

	@Test
	public void testSaveEmployees_MultipleTopManagersEmployee_ShouldThrowException() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Nick\",\n\t\"Sophie\": \"Jonas\"\n}";
		doThrow(MultipleRootsException.class).when(processor).checkForMultipleRoots(any());
		assertThrows(MultipleRootsException.class, () -> {
			hRManagerService.saveEmployees(inputEmployeeJson);
		});
	}

	@Test
	public void testSaveEmployee_TwoManagersFor1Employee_returnFailedResponse() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Nick\",\n\t\"Barbara\": \"Nick\",\n\t\"Pete\": \"Sophie\"\n}";
		doThrow(MultipleRootsException.class).when(processor).checkForMultipleRoots(any());
		assertThrows(MultipleRootsException.class, () -> {
			hRManagerService.saveEmployees(inputEmployeeJson);
		});
	}

	@Test
	public void testSaveEmployee_ImmediateLoopHierarchyEmployee_returnFailedResponse() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Nick\",\n\t\"Barbara\": \"Nick\",\n\t\"Nick\": \"Pete\"\n}";
		Map<String,String> empManagerMap = getEmployeeManagerMap();
		doNothing().when(processor).checkForMultipleRoots(any());
		Mockito.when(processor.createEmpManagerMap(any())).thenReturn(empManagerMap);
		doThrow(LoopHierarchyException.class).when(processor).checkForLoops(any());
		assertThrows(LoopHierarchyException.class, () -> {
			hRManagerService.saveEmployees(inputEmployeeJson);
		});
	}

	@Test
	public void testSaveEmployee_SkipLoopHierarchyEmployee_returnFailedResponse() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Nick\",\n\t\"Barbara\": \"Nick\",\n\t\"Nick\" : \"Sophie\",\n\t\"Sohpie\" : \"Pete\"\n}";
		Map<String,String> empManagerMap = getEmployeeManagerMap();
		doNothing().when(processor).checkForMultipleRoots(any());
		Mockito.when(processor.createEmpManagerMap(any())).thenReturn(empManagerMap);
		doThrow(LoopHierarchyException.class).when(processor).checkForLoops(any());
		assertThrows(LoopHierarchyException.class, () -> {
			hRManagerService.saveEmployees(inputEmployeeJson);
		});
	}

	@Test
	public void testSaveEmployee_SelfLoopHierarchyEmployee_returnFailedResponse() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Pete\",\n\t\"Barbara\": \"Nick\"\n}";
		Map<String,String> empManagerMap = getEmployeeManagerMap();
		doNothing().when(processor).checkForMultipleRoots(any());
		Mockito.when(processor.createEmpManagerMap(any())).thenReturn(empManagerMap);
		doThrow(LoopHierarchyException.class).when(processor).checkForLoops(any());
		assertThrows(LoopHierarchyException.class, () -> {
			hRManagerService.saveEmployees(inputEmployeeJson);
		});
	}

	@Test
	public void testSaveEmployee_ValidEmployee_DatabaseIssue_shouldThrowException() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Nick\",\n\t\"Barbara\": \"Nick\",\n\t\"Nick\": \"Sophie\",\n\t\"Sophie\": \"Jonas\"\n}";
		Map<String,String> empManagerMap = getEmployeeManagerMap();
		doNothing().when(processor).checkForMultipleRoots(any());
		Mockito.when(processor.createEmpManagerMap(any())).thenReturn(empManagerMap);
		doNothing().when(processor).checkForLoops(any());
		Mockito.when(employeeDbOperation.getEmployeeByName(any())).thenThrow(DataAccessResourceFailureException.class);
		assertThrows(UnableToSaveInDbException.class, () -> {
			hRManagerService.saveEmployees(inputEmployeeJson);
		});
	}

	@Test
	public void testSaveEmployee_ValidEmployee_AllNewRecords_shouldPass() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Nick\",\n\t\"Barbara\": \"Nick\",\n\t\"Nick\": \"Sophie\",\n\t\"Sophie\": \"Jonas\"\n}";
		Map<String,String> empManagerMap = getEmployeeManagerMap();
		Response mockResponse = new Response(true,HttpStatus.CREATED,null,null,null,null);
		doNothing().when(processor).checkForMultipleRoots(any());
		Mockito.when(processor.createEmpManagerMap(any())).thenReturn(empManagerMap);
		doNothing().when(processor).checkForLoops(any());
		Mockito.when(employeeDbOperation.getEmployeeByName(any())).thenReturn(null);
		Mockito.when(employeeDbOperation.saveEmployee(any())).thenReturn(1);
		Mockito.when(employeeDbOperation.saveEmployee(any(), anyInt())).thenReturn(1);
		Mockito.when(responseGenerator.successResponse(any())).thenReturn(mockResponse);
		Response expected =  mockResponse;
		Response actual = hRManagerService.saveEmployees(inputEmployeeJson);
		verify(employeeDbOperation,times(4)).saveEmployee(any());
		verify(employeeDbOperation,times(4)).saveEmployee(any(), anyInt());
		assertTrue(actual.isSucesss());
		assertEquals(expected.getHttpStatus(), actual.getHttpStatus());
	}

	@Test
	public void testSaveEmployee_ValidEmployee_ManagerAlreadyExists_shouldPass() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Nick\",\n\t\"Barbara\": \"Nick\",\n\t\"Nick\": \"Sophie\",\n\t\"Sophie\": \"Jonas\"\n}";
		Map<String,String> empManagerMap = getEmployeeManagerMap();
		Response mockResponse = new Response(true,HttpStatus.CREATED,null,null,null,null);
		doNothing().when(processor).checkForMultipleRoots(any());
		Mockito.when(processor.createEmpManagerMap(any())).thenReturn(empManagerMap);
		doNothing().when(processor).checkForLoops(any());
		Employee mockEmp = getMockEmployee();
		Mockito.when(employeeDbOperation.getEmployeeByName(any())).thenReturn(mockEmp);
		Mockito.when(employeeDbOperation.updateEmployeeManager(any(), anyInt())).thenReturn(1);
		Mockito.when(responseGenerator.successResponse(any())).thenReturn(mockResponse);
		Response expected =  mockResponse;
		Response actual = hRManagerService.saveEmployees(inputEmployeeJson);
		hRManagerService.saveEmployees(inputEmployeeJson);
		verify(employeeDbOperation,times(8)).updateEmployeeManager(any(), anyInt());
		assertTrue(actual.isSucesss());
		assertEquals(expected.getHttpStatus(), actual.getHttpStatus());
	}


	private Employee getMockEmployee() {
		Employee emp  = new Employee("Jonas");
		emp.setEmpId(1);
		return emp;
	}

	private List<Employee> getAllEmployees() {
		ArrayList<Employee> allEmployees = new ArrayList<Employee>(){
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

	private List<EmployeeManagerDbModel> getAllManagerSubordinates() {
		List<EmployeeManagerDbModel> managerSubordinateList = new ArrayList<EmployeeManagerDbModel>() {
			{
				add(new EmployeeManagerDbModel("Jonas","Sophie"));
				add(new EmployeeManagerDbModel("Sophie","Nick"));
				add(new EmployeeManagerDbModel("Nick","Pete"));
				add(new EmployeeManagerDbModel("Nick","Barbara"));
			}
		};
		return managerSubordinateList;
	}

	private List<String> getAllEmployeesNames(List<Employee> allEmployees) {
		return allEmployees.stream().map(Employee::getEmpName).collect(Collectors.toList());
	}

	private Map<String, List<String>> getManagerSubordinateMap() {
		Map<String, List<String>> empManagerMap = new HashMap<>();
		empManagerMap.put("Jonas", new ArrayList<>(Arrays.asList("Sophie")));
		empManagerMap.put("Sophie", new ArrayList<>(Arrays.asList("Nick")));
		empManagerMap.put("Nick", new ArrayList<>(Arrays.asList("Pete", "Barbera")));
		return empManagerMap;

	}

	private String getEmployeeJsonHierarchy() {
		return "{\"Jonas\":{\"Sophie\":{\"Nick\":{\"Pete\":{},\"Barbara\":{}}}}}";
	}

	private EmployeeSkipManager getEmployeeManagerAndSkipManager() {
		EmployeeSkipManager employeeSkipManager = new EmployeeSkipManager("Sophie","Jonas");
		return employeeSkipManager;
	}

	private Map<String, String> getEmployeeManagerMap() {
		Map<String,String> empManagerMap = new HashMap<>();
		empManagerMap.put("Sophie", "Jonas");
		empManagerMap.put("Pete", "Nick");
		empManagerMap.put("Barbara", "Nick");
		empManagerMap.put("Nick", "Sophie");
		return empManagerMap;
	}
}
