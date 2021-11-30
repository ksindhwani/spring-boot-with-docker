package com.example.personio.interview.modules.hrmanager.models.processors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.personio.interview.exceptions.LoopHierarchyException;
import com.example.personio.interview.exceptions.MultipleRootsException;
import com.example.personio.interview.modules.hrmanager.models.Employee;
import com.example.personio.interview.modules.hrmanager.models.EmployeeManagerDbModel;
import com.google.gson.Gson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class EmployeeProcessorTest {
	
	private Gson gson = new Gson();
	private EmployeeProcessor employeeProcessor;

	@BeforeEach	
	public void setup() {
		this.employeeProcessor = new EmployeeProcessor();
		doReturn(gson).when(Mockito.spy(this.employeeProcessor)).getGson();
	}

	@Test
	public void testCheckForMultipleRoots_ValidJson_ShouldPass() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Nick\",\n\t\"Barbara\": \"Nick\",\n\t\"Nick\": \"Sophie\",\n\t\"Sophie\": \"Jonas\"\n}";
		employeeProcessor.checkForMultipleRoots(inputEmployeeJson);
		assertTrue(true);
	}

	@Test
	public void testCheckForMultipleRoots_MultipleTopManagers_ShouldThrowException() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Nick\",\n\t\"Sophie\": \"Jonas\"\n}";
		assertThrows(MultipleRootsException.class, () -> {
			employeeProcessor.checkForMultipleRoots(inputEmployeeJson);
		});
	}

	@Test
	public void testCreateEmpManagerMap_ShouldPass() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Nick\",\n\t\"Barbara\": \"Nick\",\n\t\"Nick\": \"Sophie\",\n\t\"Sophie\": \"Jonas\"\n}";
		Map<String,String> expected = getMockEmployeeManagerMap();
		Map<String,String> actualValue = employeeProcessor.createEmpManagerMap(inputEmployeeJson);
		assertEquals(expected.size(), actualValue.size());
		for (String employee : actualValue.keySet()) {
			assertEquals(expected.get(employee), actualValue.get(employee));
		}
	}

	@Test
	public void testCheckForLoops_ImmediateLoop_ShouldThrowException() {
		Map<String,String> empManagerMap = new HashMap<>();
		empManagerMap.put("Pete", "Nick");
		empManagerMap.put("Barbara", "Nick");
		empManagerMap.put("Nick", "Pete");
		assertThrows(LoopHierarchyException.class, () -> {
			employeeProcessor.checkForLoops(empManagerMap);
		});
	}

	@Test
	public void testCheckForLoops_SkipLoop_ShouldThrowException() {
		Map<String,String> empManagerMap = new HashMap<>();
		empManagerMap.put("Pete", "Nick");
		empManagerMap.put("Barbara", "Nick");
		empManagerMap.put("Nick", "Sophie");
		empManagerMap.put("Sophie", "Pete");
		assertThrows(LoopHierarchyException.class, () -> {
			employeeProcessor.checkForLoops(empManagerMap);
		});
	}

	@Test
	public void testCheckForLoops_SelfLoop_ShouldThrowException() {
		Map<String,String> empManagerMap = new HashMap<>();
		empManagerMap.put("Pete", "Pete");
		empManagerMap.put("Barbara", "Nick");
		empManagerMap.put("Nick", "Sophie");
		assertThrows(LoopHierarchyException.class, () -> {
			employeeProcessor.checkForLoops(empManagerMap);
		});
	}

	@Test
	public void testCheckForLoops_ValidInput_ShouldPass() {
		Map<String,String> empManagerMap = getMockEmployeeManagerMap();
		employeeProcessor.checkForLoops(empManagerMap);
		assertTrue(true);
	}
		

	@Test
	public void testCreateEmployeeJsonHierarchy_ShouldPass() {
		Employee topManager = getMockEmp();
		List<String> empNames = getAllEmployeesNames();
		Map<String,List<String>> empManagerMap =  getMockEmpManagerMap();
		String expected = getExpectedJsonHierarchy();
		String actual = employeeProcessor.createEmployeeJsonHierarchy(
			topManager, empNames, empManagerMap
		);
		assertEquals(expected, actual);

	}

	@Test
	public void testGetAllEmployeesNames_EmptyList_ShouldPass() {
		List<Employee> empList = new ArrayList<>();
		List<String> expected = new ArrayList<>();
		List<String> actual = employeeProcessor.getAllEmployeesNames(empList);
		assertEquals(expected.size(), actual.size());
		for (int i=0; i<actual.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
			
		}
	}

	@Test
	public void shouldGetAllEmployeesNames() {
		List<Employee> empList = getAllEmployees();
		List<String> expected = getAllEmployeesNames();
		List<String> actual = employeeProcessor.getAllEmployeesNames(empList);
		assertEquals(expected.size(), actual.size());
		for (int i=0; i<actual.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
			
		}
	}

	@Test
	public void shouldGetEmployeeManagerMap() {
		List<EmployeeManagerDbModel> managerSubordinateList = getAllManagerSubordinates();
		Map<String,List<String>> expected = getMockEmpManagerMap();
		Map<String,List<String>> actual = employeeProcessor.getEmployeeManagerMap(managerSubordinateList);
		assertEquals(expected.size(), actual.size());
		for (String employee : expected.keySet()) {
			List<String> managerSubordinate = expected.get(employee);
			if(managerSubordinate == null) {
				assertNull(actual.get(employee));
			} else {
				for(int i=0;i<managerSubordinate.size();i++) {
					assertEquals(managerSubordinate.get(i), actual.get(employee).get(i));
				}
			}
		}
	}

	private Map<String, List<String>> getMockEmpManagerMap() {
		Map<String, List<String>> empManagerMap =  new HashMap<>();
		empManagerMap.put("Nick", new ArrayList<String>() {
			{
				add("Pete");
				add("Barbara");
			}
		});
		empManagerMap.put("Sophie", new ArrayList<String>() {
			{
				add("Nick");
			}
		});
		empManagerMap.put("Jonas", new ArrayList<String>() {
			{
				add("Sophie");
			}
		});
		return empManagerMap;
	}

	private List<String> getAllEmployeesNames() {
		return new ArrayList<String> () {
			{
				add("Jonas");
				add("Sophie");
				add("Nick");
				add("Pete");
				add("Barbara");
			}
		};
	}

	private Employee getMockEmp() {
		Employee emp =  new Employee("Jonas");
		emp.setEmpId(1);
		return emp;
	}

	private String getExpectedJsonHierarchy() {
		return "{\"Jonas\":{\"Sophie\":{\"Nick\":{\"Pete\":{},\"Barbara\":{}}}}}";
	}

	private Map<String, String> getMockEmployeeManagerMap() {
		Map<String, String> empManagerMap = new HashMap<>();
		empManagerMap.put("Sophie", "Jonas");
		empManagerMap.put("Pete", "Nick");
		empManagerMap.put("Barbara", "Nick");
		empManagerMap.put("Nick", "Sophie");
		return empManagerMap;
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
}
