package ec.com.erre.fastfood.bootstrap;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API FastFood
 *
 * @author eduardo.romero
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {

	@Value("${server.port:8080}")
	private String serverPort;

	@Value("${server.servlet.context-path:/}")
	private String contextPath;

	@Bean
	public OpenAPI customOpenAPI() {
		final String securitySchemeName = "bearerAuth";
		final String apiTitle = "FastFood POS API";
		final String apiDescription = """
				API REST para sistema de gestión de restaurante/fast food.

				**Funcionalidades principales:**
				- Gestión de pedidos (POS)
				- Control de inventario y compras
				- Manejo de recetas y platos
				- Pagos a clientes y proveedores
				- Promociones programadas
				- Kardex de movimientos

				**Autenticación:** Bearer Token JWT requerido para endpoints protegidos.
				""";

		// Servidor local
		Server localServer = new Server();
		localServer.setUrl("http://localhost:" + serverPort + contextPath);
		localServer.setDescription("Servidor Local de Desarrollo");

		// Información de contacto
		Contact contact = new Contact();
		contact.setName("ERRE - Equipo de Desarrollo");
		contact.setEmail("info@erre.com.ec");
		contact.setUrl("https://erre.com.ec");

		// Licencia
		License license = new License();
		license.setName("Propietario");
		license.setUrl("https://erre.com.ec/licencia");

		// Información de la API
		Info info = new Info();
		info.setTitle(apiTitle);
		info.setDescription(apiDescription);
		info.setVersion("1.0.0");
		info.setContact(contact);
		info.setLicense(license);

		// Esquema de seguridad JWT
		SecurityScheme securityScheme = new SecurityScheme();
		securityScheme.setName(securitySchemeName);
		securityScheme.setType(SecurityScheme.Type.HTTP);
		securityScheme.setScheme("bearer");
		securityScheme.setBearerFormat("JWT");
		securityScheme.setDescription("Ingrese el token JWT en el formato: Bearer {token}");

		// Componentes
		Components components = new Components();
		components.addSecuritySchemes(securitySchemeName, securityScheme);

		// Requerimiento de seguridad
		SecurityRequirement securityRequirement = new SecurityRequirement();
		securityRequirement.addList(securitySchemeName);

		return new OpenAPI().info(info).servers(List.of(localServer)).addSecurityItem(securityRequirement)
				.components(components);
	}
}
