package com.neerjaphysio.controller;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neerjaphysio.dto.ApiResponse;
import com.neerjaphysio.dto.PatientDTO;
import com.neerjaphysio.model.Patient;
import com.neerjaphysio.service.PatientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PatientDTO>> createPatient(@Valid @RequestBody Patient patient) {
        Patient created = patientService.createPatient(patient);
        return ResponseEntity.ok(ApiResponse.success("Patient created successfully", PatientDTO.fromEntity(created)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatient(@PathVariable Long id) {
        PatientDTO patient = patientService.getPatientById(id);
        return ResponseEntity.ok(ApiResponse.success("Patient retrieved successfully", patient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientDTO>> updatePatient(@PathVariable Long id, @Valid @RequestBody Patient patientDetails) {
        Patient updated = patientService.updatePatient(id, patientDetails);
        return ResponseEntity.ok(ApiResponse.success("Patient updated successfully", PatientDTO.fromEntity(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok(ApiResponse.success("Patient deleted successfully", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PatientDTO>>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PatientDTO> patients = patientService.getAllPatients(pageable);
        return ResponseEntity.ok(ApiResponse.success("Patients retrieved successfully", patients));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PatientDTO>>> searchPatients(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PatientDTO> patients = patientService.searchPatients(searchTerm, pageable);
        return ResponseEntity.ok(ApiResponse.success("Patients found successfully", patients));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<Page<PatientDTO>>> getPatientsByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Patient.PatientType patientType = Patient.PatientType.valueOf(type.toUpperCase());
            Pageable pageable = PageRequest.of(page, size);
            Page<PatientDTO> patients = patientService.getPatientsByType(patientType, pageable);
            return ResponseEntity.ok(ApiResponse.success("Patients retrieved successfully", patients));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid patient type: " + type + ". Valid values: CLINIC, HOME, ONLINE");
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getPatientsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<PatientDTO> patients = patientService.getPatientsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Patients retrieved successfully", patients));
    }

    @GetMapping("/stats/new-patients")
    public ResponseEntity<ApiResponse<Long>> getNewPatientsCount(@RequestParam LocalDate fromDate) {
        long count = patientService.getNewPatientCountFrom(fromDate);
        return ResponseEntity.ok(ApiResponse.success("New patients count retrieved", count));
    }
}