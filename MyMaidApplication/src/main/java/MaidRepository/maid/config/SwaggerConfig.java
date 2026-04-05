package MaidRepository.maid.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI maidRepositoryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Maid Repository API")
                        .description("""
                        Maid Repository - Complete Domestic Service Platform API.
                        
                        Features:
                        - User & Maid Registration with OTP
                        - Secure Authentication with Token
                        - Booking Management
                        - Location Services with OpenRouteService
                        - Payment Integration with RazorPay
                        - Rating & Reviews
                        - Document Verification
                        
                        How to Authenticate:
                        1. Register user/maid via /api/auth/user/register
                        2. Login via /api/auth/login
                        3. Copy the token from response
                        4. Click Authorize button and paste token
                        
                        Token Format: token-{mobile}-{timestamp}-{random}
                        Example: token-9876543210-1743157123456-abc123
                        
                        Test Credentials:
                        User: 9876543210 / password123
                        Maid: 9988776651 / password123
                        """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("MaidRepository Support")
                                .email("support@maidrepository.com")
                                .url("https://maidrepository.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.maidrepository.com")
                                .description("Production Server")))
                .tags(Arrays.asList(
                        new Tag().name("Authentication").description("User and Maid registration, login, OTP"),
                        new Tag().name("Bookings").description("Create, manage, and track bookings"),
                        new Tag().name("Location").description("Location services and distance calculation"),
                        new Tag().name("Users").description("User profile management"),
                        new Tag().name("Maids").description("Maid profile management"),
                        new Tag().name("Ratings").description("Rate maids and view reviews"),
                        new Tag().name("Address").description("Manage user addresses"),
                        new Tag().name("Dashboard").description("User and maid statistics"),
                        new Tag().name("Subscription").description("Subscription plans"),
                        new Tag().name("File Upload").description("Upload photos and documents"),
                        new Tag().name("Admin").description("Admin verification endpoints"),
                        new Tag().name("Public").description("Public endpoints (no auth)")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("Token")
                                        .description("""
                                        Enter your authentication token.
                                        
                                        Format: token-{mobile}-{timestamp}-{random}
                                        Example: token-9876543210-1743157123456-abc123
                                        
                                        To get token:
                                        1. Register: POST /api/auth/user/register
                                        2. Login: POST /api/auth/login
                                        3. Copy token from response
                                        """)));
    }
}