package com.example.committeeportal.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.committeeportal.Entity.Approver;
import com.example.committeeportal.Repository.ApproverRepository;

@RestController

@RequestMapping("/api/approvers")
public class ApproverController {

    private final ApproverRepository approverRepository;

    public ApproverController(ApproverRepository approverRepository) {
        this.approverRepository = approverRepository;
    }


    // Endpoint to create a new approver
    @PostMapping
    public Approver createApprover(@RequestBody Approver approver) {
        return approverRepository.save(approver);
    }

    // Endpoint to get all approvers

    @GetMapping
    public List<Approver> getAllApprovers() {
        return approverRepository.findAll();
    }

}
=======

    // ✅ GET approver by ID
    @GetMapping("/{id}")
    public ResponseEntity<Approver> getApproverById(@PathVariable Long id) {
        Optional<Approver> approver = approverRepository.findById(id);
        return approver.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ POST create new approver
    @PostMapping
    public ResponseEntity<Approver> createApprover(@RequestBody Approver approver) {
        Approver saved = approverRepository.save(approver);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

  


    // ✅ PUT update (replace entire object)
    @PutMapping("/{id}")
    public ResponseEntity<Approver> updateApprover(
            @PathVariable Long id,
            @RequestBody Approver approverDetails) {

        return approverRepository.findById(id)
                .map(existing -> {
                    existing.setName(approverDetails.getName());
                    existing.setEmail(approverDetails.getEmail());
                    // 👉 add other fields here
                    Approver updated = approverRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ PATCH partial update
    @PatchMapping("/{id}")
    public ResponseEntity<Approver> patchApprover(
            @PathVariable Long id,
            @RequestBody Approver partial) {

        return approverRepository.findById(id)
                .map(existing -> {
                    if (partial.getName() != null) {
                        existing.setName(partial.getName());
                    }
                    if (partial.getEmail() != null) {
                        existing.setEmail(partial.getEmail());
                    }
                    // 👉 add checks for other fields
                    Approver updated = approverRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ DELETE approver
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApprover(@PathVariable Long id) {
        return approverRepository.findById(id)
                .map(existing -> {
                    approverRepository.delete(existing);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

