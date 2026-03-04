package combookmanageweb.bookmanageweb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String password;
    private String name;
    private String role;

    public int getRentalDays() {
        if ("vip".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(role)) {
            return 14;
        }
        return 7;
    }
}
