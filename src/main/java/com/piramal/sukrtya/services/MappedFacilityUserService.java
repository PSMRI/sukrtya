package com.piramal.sukrtya.services;

import com.piramal.sukrtya.DTO.MappedFacilityUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class MappedFacilityUserService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MappedFacilityUserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MappedFacilityUserDTO> getMappedFacilityUsers(Long facilityId, Long regLId) {
        // SQL query to fetch data from multiple tables
        String sql = """
            SELECT f.profile,  
                   p.profilemobile,  
                   b.profileid,  
                   c.isgovt,  
                   d.uesrtype  
            FROM public.tblfacilityusermapping a  
            INNER JOIN public.tbluser b ON a.userid = b.userid  
            INNER JOIN public.tblusertype c ON b.usertypeid = c.usertypeid  
            INNER JOIN public.tbl_regl_usertype d ON c.usertypeid = d.usertypeid  
            INNER JOIN public.tblprofile p ON p.profileid = b.profileid  
            INNER JOIN public.tbl_regl_profile f ON p.profileid = f.profileid  
                AND f.regl_profileid = ?  
                AND d.RegL_UsertTypeId = ?  
            WHERE a.facilityid = ?
        """;

        // Execute the query and map results to DTO
        return jdbcTemplate.query(sql, this::mapRowToDTO, regLId, regLId, facilityId);
    }

    private MappedFacilityUserDTO mapRowToDTO(ResultSet rs, int rowNum) throws SQLException {
        return new MappedFacilityUserDTO(
                rs.getString("profile"),
                rs.getString("profilemobile"),
                rs.getLong("profileid"),
                rs.getBoolean("isgovt"),
                rs.getString("uesrtype")
        );
    }
}
