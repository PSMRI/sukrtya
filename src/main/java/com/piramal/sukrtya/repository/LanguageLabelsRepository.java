package com.piramal.sukrtya.repository;

import com.piramal.sukrtya.entity.TblRegLLabelMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface LanguageLabelsRepository extends JpaRepository<TblRegLLabelMaster, Long> {

    @Query(value = "SELECT labelid, label FROM Tbl_RegL_LabelMaster WHERE FormId = :formId AND RegLId = :regLId ORDER BY LabelId", nativeQuery = true)
    List<Object[]> findLabelIdAndLabelByFormIdAndRegLId(@Param("formId") int formId, @Param("regLId") int regLId);
}


