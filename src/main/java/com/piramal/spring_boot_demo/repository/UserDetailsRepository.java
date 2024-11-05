package com.piramal.spring_boot_demo.repository;
import com.piramal.spring_boot_demo.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDetailsRepository extends CrudRepository<User, Integer> {

    @Query(value = "SELECT DISTINCT a.username, a.userpassword, a.usertypeid, a.profileid, a.userid, " +
            "d.profile AS profilename, b.profilephoto, COALESCE(c.approvalstatus, 0) AS approvalstatus, " +
            "c.usertypenameen AS usertype, " +
            "f.facilityid, f.facilitynameen, f.facilitynameen AS facilitynamehi, " +
            "f.facilitytypeid, f.facilitynin, f.faciltyaddress, f.facilitypincode, " +
            "f.faciltylattitude, f.facilitylongitude, f.facilityphoto, " +
            "f.faciltyownershiptypeid, f.faciltytimings, f.facilitystateid, " +
            "f.facilitydistrictid, f.facilityblockid, f.facilitynameen, " +
            "f.facilitynamereg, '' AS facilityownershipnameen, '' AS facilityownershipnamereg, '' AS facilitybaselocation, " +
            "s.state_name AS faciltystate, dist.district_name AS facilitydistrict, '' AS facilityblock, '' AS reglid " +
            "FROM tbluser a " +
            "INNER JOIN tblprofile b ON a.profileid = b.profileid " +
            "INNER JOIN tbl_regl_profile d ON b.profileid = d.profileid " +
            "INNER JOIN tblusertype c ON a.usertypeid = c.usertypeid " +
            "LEFT JOIN tblfacilityusermapping fm ON a.userid = fm.userid " +
            "INNER JOIN tblfacility f ON f.facilityid = fm.facilityid " +
            "INNER JOIN tblstates s ON s.state_code = f.facilitystateid " +
            "INNER JOIN tbldistricts dist ON dist.district_code = f.facilitydistrictid " +
            "WHERE a.username = :userName AND a.userpassword = :userPassword", nativeQuery = true)
    List<Object[]> findUserProfileWithFacilities(@Param("userName") String userName, @Param("userPassword") String userPassword);
}