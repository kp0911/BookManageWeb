package combookmanageweb.bookmanageweb.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("도서 관리 시스템 API")
                        .description("도서 대출/반납 및 회원 관리 기능을 제공하는 API입니다.")
                        .version("1.0.0"));
    }
}