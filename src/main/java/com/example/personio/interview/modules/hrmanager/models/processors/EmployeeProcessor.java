package com.example.personio.interview.modules.hrmanager.models.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.personio.interview.exceptions.LoopHierarchyException;
import com.example.personio.interview.exceptions.MultipleRootsException;
import com.example.personio.interview.modules.hrmanager.models.Employee;
import com.example.personio.interview.modules.hrmanager.models.EmployeeManagerDbModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class EmployeeProcessor {

    public static final String LOOP_HIERARCHY_ERR_MESSAGE = "The given json has hierarchy loop for employees %s and %s";
    public static final String MULTIPLE_ROOTS_ERR_MESSAGE = "The given json has multiple roots has multiple roots";
    public static final String SELF_LOOP_HIERARCHY_ERR_MESSAGE = "The given json hierarchy has self loop for employee %s";
    
    private Gson gson;

    public void checkForMultipleRoots(String employeesJson)  {
        boolean haveMultipleRoots = checkForMultipleTopManagers(employeesJson);
        if (haveMultipleRoots == true) {
            throw new MultipleRootsException(new Exception(MULTIPLE_ROOTS_ERR_MESSAGE),employeesJson,MULTIPLE_ROOTS_ERR_MESSAGE);
        }
    }

    public Gson getGson() {
        if(this.gson == null) {
            this.gson = new Gson();
        }
        return this.gson;
    }

    public Map<String, String> createEmpManagerMap(String employeeJson) {
        Map<String, String> empManagerMap = getGson().fromJson(
            employeeJson, new TypeToken<HashMap<String, String>>() {}.getType()
        );
        return empManagerMap;
    }

    public void checkForLoops(Map<String, String> empManagerMap) {
        Set<String> employeeSet = new HashSet<>();
        for (Map.Entry<String,String> empManager : empManagerMap.entrySet()) {
            String employee = empManager.getKey();
            String manager = empManager.getValue();
            if(employee.toLowerCase().equals(manager.toLowerCase())) {
                throw new LoopHierarchyException(new Exception(String.format(SELF_LOOP_HIERARCHY_ERR_MESSAGE, employee)), empManagerMap,SELF_LOOP_HIERARCHY_ERR_MESSAGE);
            }
            if(employeeSet.contains(employee) && employeeSet.contains(manager)) {
                String currentManager = manager;
                while(empManagerMap.containsKey(currentManager)) {
                    if(empManagerMap.get(currentManager).toLowerCase().equals(employee.toLowerCase())) {
                        throw new LoopHierarchyException(new Exception(String.format(LOOP_HIERARCHY_ERR_MESSAGE, employee,currentManager)), empManagerMap,LOOP_HIERARCHY_ERR_MESSAGE);
                    }
                    currentManager = empManagerMap.get(currentManager);
                }
            } else {
                employeeSet.add(employee);
                employeeSet.add(manager);
            }
        }
    }

    public String createEmployeeJsonHierarchy(Employee topManager, List<String> empNames,
    Map<String, List<String>> empManagerMap) 
    {
        JSONObject finalResult = new JSONObject();
        Map<String,JSONObject> empJsonObjMap = new HashMap<>();
        for (String empName : empNames) {
            empJsonObjMap.put(empName, new JSONObject());
        }

        for (String manager : empManagerMap.keySet()) {
            JSONObject managerJsonObj = empJsonObjMap.get(manager);
            List<String> subOrdinates = empManagerMap.get(manager);
            for (String subOrdinate : subOrdinates) {
                JSONObject subOrdinateJsonObj = empJsonObjMap.get(subOrdinate);
                managerJsonObj.put(subOrdinate, subOrdinateJsonObj);
            }
        }
        finalResult.put(topManager.getEmpName(), empJsonObjMap.get(topManager.getEmpName()));
        return finalResult.toString();
    }

    public List<String> getAllEmployeesNames(List<Employee> empList) {
        return empList.stream().map(Employee::getEmpName).collect(Collectors.toList());
    }

    public Map<String, List<String>> getEmployeeManagerMap(List<EmployeeManagerDbModel> managerSubordinateList) {
        Map<String,List<String>> empManagerMap = new HashMap<>();
        List<String> subOrdinateList = null;
        for (EmployeeManagerDbModel empMan : managerSubordinateList) {
            if(empManagerMap.containsKey(empMan.getManager())) {
                subOrdinateList = empManagerMap.get(empMan.getManager());
            } else {
                subOrdinateList = new ArrayList<String>();
                
            }
            subOrdinateList.add(empMan.getSubordinate());
            empManagerMap.put(empMan.getManager(), subOrdinateList);
        }
        return empManagerMap;
    }

    private boolean checkForMultipleTopManagers(String employeesJson) {
        Map<String,String> empManagerMap = createEmpManagerMap(employeesJson);
        Set<String> topLevelManagers = new HashSet<>();

        for (String employee : empManagerMap.keySet()) {
            String manager = empManagerMap.get(employee);
            if(!empManagerMap.containsKey(manager)) {
                topLevelManagers.add(manager);
            }
        }
        return topLevelManagers.size() > 1;
    }
} 
