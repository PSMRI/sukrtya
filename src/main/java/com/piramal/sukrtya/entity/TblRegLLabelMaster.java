package com.piramal.sukrtya.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Tbl_RegL_LabelMaster")
public class TblRegLLabelMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LabelId")
    private Long labelId;

    @Column(name = "FormId")
    private int formId;

    @Column(name = "RegLId")
    private int regLId;

    @Column(name = "Label")
    private String label;

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public int getRegLId() {
        return regLId;
    }

    public void setRegLId(int regLId) {
        this.regLId = regLId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


}
