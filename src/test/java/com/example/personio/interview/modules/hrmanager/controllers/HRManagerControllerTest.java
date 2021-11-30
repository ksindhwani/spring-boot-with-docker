package com.example.personio.interview.modules.hrmanager.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import com.example.personio.interview.exceptions.PersonioBadRequestException;
import com.example.personio.interview.modules.hrmanager.models.EmployeeSkipManager;
import com.example.personio.interview.modules.hrmanager.services.HRManagerService;
import com.example.personio.interview.utils.httputils.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class) 
class HRManagerControllerTest {
	public static final String MOCK_HIERARCHY_RETURN_FROM_HR_SERVICE = "{\"Jonas\":{\"Sophie\":{\"Nick\":{\"Pete\":{},\"Barbara\":{}}}}}";
	public static final String SELF_LOOP_HIERARCHY_ERR_MESSAGE = "The given json hierarchy has self loop for employee Pete";
	private static final String MULTIPLE_ROOTS_ERR_MESSAGE = "The given json has multiple roots has multiple roots";

	private HRManagerController hrManagerController;

	@MockBean
	private HRManagerService hrManagerService;

	@BeforeEach
	public void setup() {
		hrManagerController = new HRManagerController(hrManagerService);
	}


	@Test
	public void testWelcome_Should_Pass() {
		String expectedValue = "welcome";
		String actualValue = hrManagerController.welcome();
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void testGetEmployeesHierarchy_ShouldPass() {
		String expected = "{\"Jonas\":{\"Sophie\":{\"Nick\":{\"Pete\":{},\"Barbara\":{}}}}}";
		Mockito.when(hrManagerController.getEmployeesHierarchy()).thenReturn(MOCK_HIERARCHY_RETURN_FROM_HR_SERVICE);
		String actual = hrManagerController.getEmployeesHierarchy();
		assertEquals(expected, actual);
	}

	@Test
	public void testGetEmployeeManagerAndSkipManager_EmptyEmployeeName_ShouldThrowWxception() throws PersonioBadRequestException {
		Mockito.when(hrManagerService.getEmployeeManagerAndSkipManager(any()))
		.thenThrow(new PersonioBadRequestException(new Exception(HRManagerService.EMPTY_EMPLOYEE_NAME), null, HRManagerService.EMPTY_EMPLOYEE_NAME));
		assertThrows(PersonioBadRequestException.class, () -> {
			hrManagerController.getEmployeeManagerAndSkipManager("");
		});
	}

	@Test
	public void testGetEmployeeManagerAndSkipManager_NullEmployeeName_ShouldThrowWxception() throws PersonioBadRequestException {
		Mockito.when(hrManagerService.getEmployeeManagerAndSkipManager(any()))
		.thenThrow( new PersonioBadRequestException(new Exception(HRManagerService.EMPTY_EMPLOYEE_NAME), null, HRManagerService.EMPTY_EMPLOYEE_NAME));
		assertThrows(PersonioBadRequestException.class, () -> {
			hrManagerController.getEmployeeManagerAndSkipManager(null);
		});
	}

	@Test
	public void testGetEmployeeManagerAndSkipManager_ValidEmployeeName_ShouldPass() {
		String inputEmpName = "Pete";
		EmployeeSkipManager expected = new EmployeeSkipManager("Nick" , "Sophie");
		Mockito.when(hrManagerService.getEmployeeManagerAndSkipManager(any())).thenReturn(expected);
		EmployeeSkipManager actual = hrManagerController.getEmployeeManagerAndSkipManager(inputEmpName);
		assertEquals(expected.getSupervisor(), actual.getSupervisor());
		assertEquals(expected.getSkipSupervisor(), actual.getSkipSupervisor());
	}

	@Test
	public void testCreateEmployee_ValidEmployee_returnSuccessResponse() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Nick\",\n\t\"Barbara\": \"Nick\",\n\t\"Nick\": \"Sophie\",\n\t\"Sophie\": \"Jonas\"\n}";
		Response mockSuccessResponse = new Response(true,HttpStatus.CREATED,null,null,null,null);
		Mockito.when(hrManagerService.saveEmployees(any())).thenReturn(mockSuccessResponse);
		ResponseEntity<?> expected = new ResponseEntity<>(mockSuccessResponse, HttpStatus.CREATED);
		ResponseEntity<?> actual = hrManagerController.createEmployees(inputEmployeeJson);
		assertEquals(expected.getStatusCode(), actual.getStatusCode());
		assertEquals(expected.getBody(), actual.getBody());
	}

	@Test
	public void testCreateEmployee_MultipleTopManagersEmployee_returnFailedResponse() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Nick\",\n\t\"Sophie\": \"Jonas\"\n}";
		Response mockFailedResponse = new Response(
			false,
			HttpStatus.BAD_REQUEST,
			MULTIPLE_ROOTS_ERR_MESSAGE,
			MULTIPLE_ROOTS_ERR_MESSAGE,
			MULTIPLE_ROOTS_ERR_MESSAGE,
			inputEmployeeJson
		);
		Mockito.when(hrManagerService.saveEmployees(any())).thenReturn(mockFailedResponse);
		ResponseEntity<?> expected = new ResponseEntity<>(mockFailedResponse, HttpStatus.BAD_REQUEST);
		ResponseEntity<?> actual = hrManagerController.createEmployees(inputEmployeeJson);
		assertEquals(expected.getStatusCode(), actual.getStatusCode());
		Response expectedResponse = (Response)(expected.getBody());
		Response actualResponse = (Response)(actual.getBody());
		assertEquals(expectedResponse.getErrMessage(), actualResponse.getErrMessage());
		assertEquals(expectedResponse.getCustomErrMessage(), actualResponse.getCustomErrMessage());
	}

	@Test
	public void testCreateEmployee_TwoManagersFor1Employee_returnFailedResponse() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Nick\",\n\t\"Barbara\": \"Nick\",\n\t\"Pete\": \"Sophie\"\n}";
		Response mockFailedResponse = new Response(
			false,
			HttpStatus.BAD_REQUEST,
			MULTIPLE_ROOTS_ERR_MESSAGE,
			MULTIPLE_ROOTS_ERR_MESSAGE,
			MULTIPLE_ROOTS_ERR_MESSAGE,
			inputEmployeeJson
		);
		Mockito.when(hrManagerService.saveEmployees(any())).thenReturn(mockFailedResponse);
		ResponseEntity<?> expected = new ResponseEntity<>(mockFailedResponse, HttpStatus.BAD_REQUEST);
		ResponseEntity<?> actual = hrManagerController.createEmployees(inputEmployeeJson);
		assertEquals(expected.getStatusCode(), actual.getStatusCode());
		Response expectedResponse = (Response)(expected.getBody());
		Response actualResponse = (Response)(actual.getBody());
		assertEquals(expectedResponse.getErrMessage(), actualResponse.getErrMessage());
		assertEquals(expectedResponse.getCustomErrMessage(), actualResponse.getCustomErrMessage());
	}

	@Test
	public void testCreateEmployee_ImmediateLoopHierarchyEmployee_returnFailedResponse() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Nick\",\n\t\"Barbara\": \"Nick\",\n\t\"Nick\": \"Pete\"\n}";
		Response mockFailedResponse = new Response(
			false,
			HttpStatus.BAD_REQUEST,
			MULTIPLE_ROOTS_ERR_MESSAGE,
			MULTIPLE_ROOTS_ERR_MESSAGE,
			MULTIPLE_ROOTS_ERR_MESSAGE,
			inputEmployeeJson
		);
		Mockito.when(hrManagerService.saveEmployees(any())).thenReturn(mockFailedResponse);
		ResponseEntity<?> expected = new ResponseEntity<>(mockFailedResponse, HttpStatus.BAD_REQUEST);
		ResponseEntity<?> actual = hrManagerController.createEmployees(inputEmployeeJson);
		assertEquals(expected.getStatusCode(), actual.getStatusCode());
		Response expectedResponse = (Response)(expected.getBody());
		Response actualResponse = (Response)(actual.getBody());
		assertEquals(expectedResponse.getErrMessage(), actualResponse.getErrMessage());
		assertEquals(expectedResponse.getCustomErrMessage(), actualResponse.getCustomErrMessage());
	}

	@Test
	public void testCreateEmployee_SkipLoopHierarchyEmployee_returnFailedResponse() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Nick\",\n\t\"Barbara\": \"Nick\",\n\t\"Nick\" : \"Sophie\",\n\t\"Sohpie\" : \"Pete\"\n}";
		Response mockFailedResponse = new Response(
			false,
			HttpStatus.BAD_REQUEST,
			MULTIPLE_ROOTS_ERR_MESSAGE,
			MULTIPLE_ROOTS_ERR_MESSAGE,
			MULTIPLE_ROOTS_ERR_MESSAGE,
			inputEmployeeJson
		);
		Mockito.when(hrManagerService.saveEmployees(any())).thenReturn(mockFailedResponse);
		ResponseEntity<?> expected = new ResponseEntity<>(mockFailedResponse, HttpStatus.BAD_REQUEST);
		ResponseEntity<?> actual = hrManagerController.createEmployees(inputEmployeeJson);
		assertEquals(expected.getStatusCode(), actual.getStatusCode());
		Response expectedResponse = (Response)(expected.getBody());
		Response actualResponse = (Response)(actual.getBody());
		assertEquals(expectedResponse.getErrMessage(), actualResponse.getErrMessage());
		assertEquals(expectedResponse.getCustomErrMessage(), actualResponse.getCustomErrMessage());
	}

	@Test
	public void testCreateEmployee_SelfLoopHierarchyEmployee_returnFailedResponse() {
		String inputEmployeeJson = "{\n\t\"Pete\": \"Pete\",\n\t\"Barbara\": \"Nick\"\n}";
		Response mockFailedResponse = new Response(
			false,
			HttpStatus.BAD_REQUEST,
			MULTIPLE_ROOTS_ERR_MESSAGE,
			MULTIPLE_ROOTS_ERR_MESSAGE,
			MULTIPLE_ROOTS_ERR_MESSAGE,
			inputEmployeeJson
		);
		Mockito.when(hrManagerService.saveEmployees(any())).thenReturn(mockFailedResponse);
		ResponseEntity<?> expected = new ResponseEntity<>(mockFailedResponse, HttpStatus.BAD_REQUEST);
		ResponseEntity<?> actual = hrManagerController.createEmployees(inputEmployeeJson);
		assertEquals(expected.getStatusCode(), actual.getStatusCode());
		Response expectedResponse = (Response)(expected.getBody());
		Response actualResponse = (Response)(actual.getBody());
		assertEquals(expectedResponse.getErrMessage(), actualResponse.getErrMessage());
		assertEquals(expectedResponse.getCustomErrMessage(), actualResponse.getCustomErrMessage());
	}
}
