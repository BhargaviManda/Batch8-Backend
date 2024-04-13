package com.thbs.BatchManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.thbs.BatchManagement.entity.Batch;
import com.thbs.BatchManagement.entity.EmployeeDTO;
import com.thbs.BatchManagement.exceptionhandler.BatchNotFoundException;
import com.thbs.BatchManagement.exceptionhandler.DuplicateEmployeeException;
import com.thbs.BatchManagement.repository.BatchRepository;
import com.thbs.BatchManagement.service.BatchService;


import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


@RestController
@CrossOrigin("*")
@RequestMapping("/batch")
public class BatchController {

	
    @Autowired
    private BatchService batchService;

    @Autowired
    private BatchRepository batchRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    
    @GetMapping("/merged-employee-ids")
    public List<Long> getMergedEmployeeIds() {
        List<Long> mergedEmployeeIds = new ArrayList<>();
        // Fetch data from the first path
        String firstPathUrl = "http://192.168.100.16:7001/upload/trainee-employeeids";
        List<Long> firstPathEmployeeIds = restTemplate.getForObject(firstPathUrl, List.class);
        mergedEmployeeIds.addAll(firstPathEmployeeIds);
        // System.out.println(mergedEmployeeIds);

        // Fetch data from the second path
        String secondPathUrl = "http://localhost:7002/another-path"; 
        List<Long> secondPathEmployeeIds = restTemplate.getForObject(secondPathUrl, List.class);
        mergedEmployeeIds.addAll(secondPathEmployeeIds);

        return mergedEmployeeIds;
    }
    
    
    // adding trainees with batch creation
    @PostMapping
    public ResponseEntity<String> createBatch(@RequestBody Batch batch) {
        return batchService.createBatch(batch);
    }

    
    // bulk upload with batch creation
    @PostMapping("/new-batch/bulk")
    public String bulkUpload(@RequestParam("file") MultipartFile file, @RequestParam("data") String data) throws IOException, ParseException {
        List<EmployeeDTO> Employees = batchService.parseExcel(file);
        batchService.addEmployeesToBatchFromExcel(Employees, data);
        return "Batch created successfully";
    }

    
    // adding employees to existing batch by batchid
    @PostMapping("/employee/batch-id/{batchId}")
    public String addEmployeesToBatch(@PathVariable Long batchId, @RequestBody List<EmployeeDTO> employees) {
        batchService.addEmployeesToExistingBatches(batchId, employees);
        return "Employees added to batch successfully";
    }

    
    // adding employees to existing batch by batchname
    @PostMapping("/employee/batch-name/{batchName}")
    public String addEmployeesToBatch(@PathVariable String batchName, @RequestBody List<EmployeeDTO> employees) {
            batchService.addEmployeesToExistingBatch(batchName, employees);
            return "Employees added to batch successfully";  
    }
    
    
    // bulk upload to existing batch by batchid
    @PostMapping("/existing-batch/bulk/batch-id/{batchId}")
    public String addEmployeesToExistingBatchBulkUpload(@PathVariable("batchId") Long batchId, @RequestParam("file") MultipartFile file) throws BatchNotFoundException, DuplicateEmployeeException, IOException {
        List<EmployeeDTO> employees = batchService.parseExcel(file);
        batchService.addEmployeesToExistingBatchesFromExcel(batchId, employees);
        return "Employees added to batch successfully";  
    }

    
    // bulk upload to existing batch by batchname
    @PostMapping("/existing-batch/bulk/batch-name/{batchName}")
    public String addEmployeesToExistingBatchBulkUpload(@PathVariable("batchName") String batchName, @RequestParam("file") MultipartFile file) throws BatchNotFoundException, DuplicateEmployeeException, IOException {
            List<EmployeeDTO> employees = batchService.parseExcel(file);
            batchService.addEmployeesToExistingBatchFromExcel(batchName, employees);
            return "Employees added to batch successfully";   
    }


    // list of batches by batchid
    @GetMapping("/id/{batchId}")
    public ResponseEntity<Object> getBatchById(@PathVariable Long batchId) {    
    	return batchService.getBatchById(batchId);
    }

    
    // list of batches by batchnames
    @GetMapping("/name/{batchName}")
    public ResponseEntity<Object> getBatchByName(@PathVariable String batchName) {
    	return batchService.getBatchByName(batchName);
    }
    

    // list of all batch details
    @GetMapping
    public List<Batch> getAllBatches() {
        return batchService.getAllBatches();
    }
    
    
    // list of batchnames
    @GetMapping("/name")
    public List<String> getAllBatchNames() {
        return batchService.getAllBatchNames();
    }

    
    // list of employees using batchid
    @GetMapping("/batch-id/employees/{batchId}")
    public List<Long> getEmployeesInBatch(@PathVariable Long batchId) {
        return batchService.getEmployeesInBatch(batchId);
    }
    
    
    // list of employees using batchname
    @GetMapping("/batch-name/employees/{batchName}")
    public List<Long> getEmployeesInBatchByName(@PathVariable String batchName) {
        return batchService.getEmployeesInBatchByName(batchName);
    }

    
    // list of all batch names along with ids
    @GetMapping("/name/id")
    public List<Map<String, Object>> getAllBatchNamesWithIds() {
        return batchService.getAllBatchNamesWithIds();
    }
    
    
    // deleting batch with batchid
    @DeleteMapping("/batch-id/{batchId}")
    public String deleteBatch(@PathVariable Long batchId) {
        batchService.deleteBatchById(batchId);
        return "Batch deleted successfully";
    }

    
    // deleting batch with batchname
    @DeleteMapping("/batch-name/{batchName}")
    public String deleteBatch(@PathVariable String batchName) {
        batchService.deleteBatchByName(batchName);
        return "Batch deleted successfully";
    }

    
    // deleting employees with batchname
    @DeleteMapping("/batch-name/{batchName}/{employeeId}")
    public String deleteEmployeeFromBatch(@PathVariable String batchName, @PathVariable int employeeId) {
        batchService.deleteEmployeeFromBatch(batchName, employeeId);
        return "Employee deleted from batch successfully";
    }

    
    // deleting employees with batchid
    @DeleteMapping("/batch-id/{batchId}/{employeeId}")
    public String deleteEmployeeFromBatch(@PathVariable Long batchId, @PathVariable int employeeId) {
        batchService.deleteEmployeeFromBatch(batchId, employeeId);
        return "Employee deleted from batch successfully";
    }
    
    
    //updating enddate with id
    @PatchMapping("/end-date/{batchId}")
    public String updateEndDate(@PathVariable Long batchId, @RequestBody Batch batch) {
        batchService.updateEndDate(batchId, batch.getEndDate());
        return "EndDate updated successfully";
    }
   
    
    //renaming batchname with id
    @PatchMapping("/batch-name/{batchId}")
    public String updateBatchName(@PathVariable Long batchId, @RequestBody Batch batch) {
        batchService.updateBatchName(batchId, batch.getBatchName());
        return "BatchName updated successfully";
    }
    
    
}
