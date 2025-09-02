package com.example.committeeportal.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.committeeportal.Entity.Approver;
import com.example.committeeportal.Repository.ApproverRepository;

@RestController
@RequestMapping("/approvers")
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