package com.piramal.spring_boot_demo.controller;

import com.piramal.spring_boot_demo.models.State;
import com.piramal.spring_boot_demo.services.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/states")
public class StateController {

    @Autowired
    private StateService stateService;

    // Get all states
    @GetMapping
    public List<State> getAllStates() {
        return stateService.getAllStates();
    }

    // Get a state by ID
    @GetMapping("/{id}")
    public Optional<State> getStateById(@PathVariable String id) {
        return stateService.getStateById(id);
    }

    // Add a new state
    @PostMapping
    public State addState(@RequestBody State state) {
        return stateService.addState(state);
    }

    // Delete a state
    @DeleteMapping("/{id}")
    public void deleteState(@PathVariable String id) {
        stateService.deleteState(id);
    }
}