package com.srizvi.spring.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srizvi.spring.domain.Role;
import com.srizvi.spring.domain.User;
import com.srizvi.spring.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;


    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(){
         log.info("Getting all the users from DB");
        return ResponseEntity.ok().body(userService.getUsers());

    }

    @PostMapping("/user/save")
    public ResponseEntity<User> saveUsers(@RequestBody User user){
        log.info("Getting the user by ");
         URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));

    }

    @PostMapping("/role/save")
    public ResponseEntity<Role> saveRoles(@RequestBody Role role){
        log.info("Getting the user by ");
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));

    }

    @PostMapping("/role/addtouser")
    public ResponseEntity<Role> saveRoles(@RequestBody RoleToUserForm form){
        log.info("Adding the user by role");
        userService.addRoleToUser(form.getUsername(), form.getRoleName());
        return ResponseEntity.ok().build();

    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request , HttpServletResponse response) throws IOException {
        log.info("getting the refresh Token");
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            log.info("getting the Token");
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);


                String refresh_token = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles",user.getRoles().stream().map(Role::getName).collect(Collectors.toUnmodifiableList()))
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("refresh_token", refresh_token);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                log.info("refresh Token  {} :", refresh_token);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception e) {
                log.error("Error in Logging  : {} " , e.getMessage());
                response.setHeader("error" , e.getMessage());
                response.setStatus(FORBIDDEN.value());
                //  response.sendError(FORBIDDEN.value());

                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
                log.info("Error Message  {} : ",error);
            }
        } else{
            throw new RuntimeException("Refresh Token is Missing");
        }

    }
}


@Data
class RoleToUserForm{
    private String username;
    private String roleName;
}
