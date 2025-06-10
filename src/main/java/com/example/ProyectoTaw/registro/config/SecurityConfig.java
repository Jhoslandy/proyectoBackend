package com.example.ProyectoTaw.registro.config;

import com.example.ProyectoTaw.registro.security.JwtAuthenticationEntryPoint;
import com.example.ProyectoTaw.registro.security.JwtAuthenticationFilter;
import com.example.ProyectoTaw.registro.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita la seguridad a nivel de método con @PreAuthorize
public class SecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService; // Nuestro servicio para cargar los detalles del usuario

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler; // Manejador para errores de autenticación (401 Unauthorized)

    // Filtro JWT que se encargará de validar el token en cada petición
    @Bean
    public JwtAuthenticationFilter authenticationJwtTokenFilter() {
        return new JwtAuthenticationFilter();
    }

    // Proveedor de autenticación que usa nuestro UserDetailsService y el PasswordEncoder
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Gestiona el proceso de autenticación (usado en AuthController para el login)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Codificador de contraseñas (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- Configuración CORS ---
    // Define qué orígenes, métodos y encabezados son permitidos
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ¡IMPORTANTE! Ajusta estos orígenes a los que realmente uses:
        // 1. El origen de tu frontend (ej. Live Server de VS Code)
        //    Según la imagen, tu frontend está en 'http://127.0.0.1:5500'.
        //    Asegúrate de que este origen esté en la lista.
        // 2. Si también usas 'http://localhost:5500' para el frontend, inclúyelo.
        configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500", "http://localhost:5500")); 
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Métodos HTTP permitidos
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept")); // Encabezados permitidos
        configuration.setAllowCredentials(true); // Permite el envío de cookies, encabezados de autorización, etc.
        
        // Opcional: expone encabezados para que el frontend pueda leerlos (ej. Authorization)
        configuration.setExposedHeaders(List.of("Authorization", "X-Auth-Token", "Access-Control-Allow-Origin")); 
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuración CORS a todas las rutas (/**)
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }

    // Cadena de filtros de seguridad HTTP
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Deshabilita CSRF para APIs REST sin estado
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Manejo de excepciones de autenticación
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sesiones sin estado (para JWT)
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas que no requieren autenticación
                .requestMatchers("/api/auth/**").permitAll() // Para login, registro, etc.
                .requestMatchers("/api/test/**").permitAll() // Para endpoints de prueba, si los tienes
                
                // Rutas protegidas que requieren roles específicos
                .requestMatchers("/api/estudiantes/**").hasRole("ESTUDIANTE") // Solo para usuarios con rol ESTUDIANTE
                .requestMatchers("/api/docentes/**").hasRole("DOCENTE")   // Solo para usuarios con rol DOCENTE
                .requestMatchers("/api/admin/**").hasRole("ADMIN")       // Solo para usuarios con rol ADMIN
                
                .anyRequest().authenticated() // Cualquier otra solicitud requiere que el usuario esté autenticado
            );

        // Configura el proveedor de autenticación
        http.authenticationProvider(authenticationProvider());

        // Añade el filtro JWT antes del filtro de autenticación de usuario y contraseña
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // Habilita la configuración CORS definida
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }
}