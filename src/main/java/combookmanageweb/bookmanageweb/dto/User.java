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
    private String name;
    private String grade;

    public int getRentalDays() {
        if ("vip".equalsIgnoreCase(grade)) {
            return 14;
        }
        return 7;
    }
}
