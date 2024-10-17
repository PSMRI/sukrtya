package com.piramal.spring_boot_demo.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "state_master")
public class State {
     
    @Id
    @Column(name = "stateid", length = 3) 
    private String StateID;

    @Column(name = "state_name_en",  length = 200)
    private String StateNameEn;

    @Column(name = "state_name_hn",length = 200)
    private String StateNameHn;

     

    @Column(name = "is_active")
    private boolean IsActive;

    

    // Getters and Setters

    

    public String getStateID() {
        return StateID;
    }

    public void setStateID(String StateID) {
        this.StateID = StateID;
    }

    public String getStateNameEn() {
        return StateNameEn;
    }

    public void setStateNameEn(String StateNameEn) {
        this.StateNameEn = StateNameEn;
    }

    public String getStateNameHn() {
        return StateNameHn;
    }

    public void setStateNameHn(String StateNameHn) {
        this.StateNameHn = StateNameHn;
    }

    

    public Boolean isIsActive() {
        return IsActive;
    }

    public void setIsActive(boolean IsActive) {
        this.IsActive = IsActive;
    }
 
}