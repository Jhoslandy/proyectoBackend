package com.example.ProyectoTaw.registro.controller;

import com.example.ProyectoTaw.registro.dto.AuthDTO.JwtResponse;
import com.example.ProyectoTaw.registro.dto.AuthDTO.LoginRequest;
import com.example.ProyectoTaw.registro.dto.AuthDTO.MessageResponse;
import com.example.ProyectoTaw.registro.dto.AuthDTO.SignupRequest;
import com.example.ProyectoTaw.registro.model.Rol;
import com.example.ProyectoTaw.registro.model.Usuario;
import com.example.ProyectoTaw.registro.repository.RolRepository;
import com.example.ProyectoTaw.registro.repository.UsuarioRepository;
import com.example.ProyectoTaw.registro.security.JwtUtils;
import com.example.ProyectoTaw.registro.service.UserDetailsImpl; // IMPORTAR UserDetailsImpl
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // Mantener este import si es necesario, pero usaremos UserDetailsImpl
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        // CASTEAMOS A UserDetailsImpl para acceder a sus propiedades personalizadas (id, email, roles)
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (usuarioRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: ¡El nombre de usuario ya está en uso!"));
        }

        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: ¡El correo electrónico ya está en uso!"));
        }

        // Usa el nuevo constructor que añadimos
        Usuario user = new Usuario(signUpRequest.getNombre(),
                                signUpRequest.getApellido(),
                                signUpRequest.getEmail(),
                                signUpRequest.getUsername(),
                                encoder.encode(signUpRequest.getPassword()),
                                true); // Por defecto activo

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Rol> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) { // Si no se especifican roles, asigna ESTUDIANTE por defecto
            Rol defaultRol = rolRepository.findByNombre(Rol.NombreRol.ROL_ESTUDIANTE)
                    .orElseThrow(() -> new RuntimeException("Error: Rol ESTUDIANTE no encontrado."));
            roles.add(defaultRol);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Rol adminRol = rolRepository.findByNombre(Rol.NombreRol.ROL_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Rol ADMIN no encontrado."));
                        roles.add(adminRol);
                        break;
                    case "docente":
                        Rol docenteRol = rolRepository.findByNombre(Rol.NombreRol.ROL_DOCENTE)
                                .orElseThrow(() -> new RuntimeException("Error: Rol DOCENTE no encontrado."));
                        roles.add(docenteRol);
                        break;
                    case "estudiante":
                        Rol estudianteRol = rolRepository.findByNombre(Rol.NombreRol.ROL_ESTUDIANTE)
                                .orElseThrow(() -> new RuntimeException("Error: Rol ESTUDIANTE no encontrado."));
                        roles.add(estudianteRol);
                        break;
                    default:
                        // Si un rol no es válido, puedes ignorarlo, lanzar una excepción o asignar un rol por defecto
                        System.err.println("Advertencia: Rol no reconocido durante el registro: " + role);
                        Rol defaultRol = rolRepository.findByNombre(Rol.NombreRol.ROL_ESTUDIANTE)
                            .orElseThrow(() -> new RuntimeException("Error: Rol ESTUDIANTE no encontrado para default."));
                        roles.add(defaultRol);
                }
            });
        }
        user.setRoles(roles);
        usuarioRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente!"));
    }

    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toSet());
            
            return ResponseEntity.ok(new JwtResponse(
                null, // No se envía un nuevo token aquí
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
            ));
        }
        
        return ResponseEntity.ok(new MessageResponse("No hay sesión activa o usuario no autenticado."));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Sesión cerrada exitosamente!"));
    }
}