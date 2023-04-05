package au.com.voc.raceEntry.user.register;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value = "FROM Role r WHERE r.name = :roleName")
    Role findByRoleName(@Param("roleName") String roleName);
}
