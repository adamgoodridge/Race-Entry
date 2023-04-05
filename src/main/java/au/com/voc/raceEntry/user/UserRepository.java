//demo only
package au.com.voc.raceEntry.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "FROM User u WHERE u.username = :email")
    User findByEmail(@Param("email") String email);
    //default didn't throw correct exception
}
