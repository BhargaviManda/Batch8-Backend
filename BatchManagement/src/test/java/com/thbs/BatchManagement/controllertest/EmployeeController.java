package com.thbs.BatchManagement.controllertest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.thbs.BatchManagement.controller.BatchController;
import com.thbs.BatchManagement.service.BatchService;

@Controller
public class EmployeeController {

	
	@Autowired  
	private BatchController batchController;
	
	@Autowired
    private BatchService batchService;
	
	
	// remaining employees
    @GetMapping("/{batchName}/remaining-employees")
    public List<Long> findRemainingEmployees(@PathVariable String batchName) {
        List<Long> allEmployeeIds = batchController.getMergedEmployeeIds(); // Fetch all employee IDs, you can implement this logic
        return batchService.findRemainingEmployees(batchName, allEmployeeIds);
    }

    
}
