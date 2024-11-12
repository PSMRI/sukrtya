package com.piramal.sukrtya.services;
import com.piramal.sukrtya.DTO.UserDTO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class LoginService {
    private final JdbcTemplate jdbcTemplate;

    public LoginService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
//    @Autowired
//    private JdbcTemplate jdbcTemplate;

    public UserDTO getUserData(String username, String password) {
        String sql = "SELECT * FROM get_user_data(?, ?)";
        try {
        return jdbcTemplate.queryForObject(sql, new Object[]{username, password}, new RowMapper<UserDTO>() {
            @Override
            public UserDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                UserDTO user = new UserDTO();
                user.setUserName(rs.getString("username"));
                user.setPassword(rs.getString("userpassword"));
                user.setUserTypeID(rs.getInt("usertypeid"));
                user.setProfileID(rs.getInt("profileid"));
                user.setUserID(rs.getLong("userid"));
                user.setProfileName(rs.getString("profilename"));
                user.setProfilePhoto(rs.getString("profilephoto"));
                user.setApprovalStatus(rs.getInt("approvalstatus"));
                user.setUserType(rs.getString("usertype"));
                return user;
            }
        });
        } catch (EmptyResultDataAccessException e) {
            // Return null or throw a custom exception if no result is found
            return null;
        }
    }
}
