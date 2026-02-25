package combookmanageweb.bookmanageweb.mapper;

import combookmanageweb.bookmanageweb.dto.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM USERS")
    List<User> findAll();

    @Select("SELECT * FROM USERS WHERE id = #{id}")
    User findById(String id);

    @Insert("INSERT INTO USERS (id, name, grade) " +
            "VALUES (#{id}, #{name}, #{grade})")
    void insertUser(User User);

    @Select("SELECT COUNT(*) FROM USERS")
    int countUsers();
}
