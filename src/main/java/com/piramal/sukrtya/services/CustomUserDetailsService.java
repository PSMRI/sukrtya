//package com.piramal.sukrtya.services;
//
//
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // Replace this with your actual user retrieval logic
//        if ("9166958261".equals(username)) {
//            return new User(
//                    "9166958261",
//                    "{noop}password", // {noop} indicates no encoding, replace with encoded password in production
//                    Collections.emptyList() // Replace with actual roles or authorities
//            );
//        } else {
//            throw new UsernameNotFoundException("User not found");
//        }
//    }
//}
//

package com.piramal.sukrtya.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    public CustomUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String query = "SELECT username, userpassword FROM tbluser WHERE username = ?";

        try {
            // Query the user details
            Map<String, Object> user = jdbcTemplate.queryForMap(query, username);
            String dbUsername = (String) user.get("username");
            String dbPassword = (String) user.get("userpassword");

            // Assign roles (for simplicity, assigning a default role; fetch roles if required)
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

            // Return the UserDetails object
            return new User(dbUsername, dbPassword, authorities);

        } catch (Exception e) {
            // Handle exceptions such as user not found
            throw new UsernameNotFoundException("User not found: " + username, e);
        }
    }
}

