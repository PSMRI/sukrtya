package com.piramal.sukrtya.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tblfaanswers")
public class TblFaAnswers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fatransactionsid")
    private String faTransactionsID;

    @Column(name = "faqid")
    private Integer faQID;

    @Column(name = "faanswers")
    private String faAnswers;

    @Column(name = "formid")
    private Integer formId;

    public String getFaTransactionsID() {
        return faTransactionsID;
    }

    public void setFaTransactionsID(String faTransactionsID) {
        this.faTransactionsID = faTransactionsID;
    }

    public Integer getFaQID() {
        return faQID;
    }

    public void setFaQID(Integer faQID) {
        this.faQID = faQID;
    }

    public String getFaAnswers() {
        return faAnswers;
    }

    public void setFaAnswers(String faAnswers) {
        this.faAnswers = faAnswers;
    }

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    // Getters and Setters
}

