package com.piramal.spring_boot_demo.services;

import com.piramal.spring_boot_demo.models.State;
import com.piramal.spring_boot_demo.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StateService {

    @Autowired
    private StateRepository stateRepository;

    public List<State> getAllStates() {
        return stateRepository.findAll();
    }

    public Optional<State> getStateById(String id) {
        return stateRepository.findById(id);
    }

    public State addState(State state) {
        return stateRepository.save(state);
    }

    public void deleteState(String id) {
        stateRepository.deleteById(id);
    }
}