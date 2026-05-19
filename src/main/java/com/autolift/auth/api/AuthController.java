package com.autolift.auth.api;

import com.autolift.auth.JwtTokenProvider;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider tokenProvider;
  private final UserDetailsService userDetailsService;

  public AuthController(
      AuthenticationManager authenticationManager,
      JwtTokenProvider tokenProvider,
      UserDetailsService userDetailsService) {
    this.authenticationManager = authenticationManager;
    this.tokenProvider = tokenProvider;
    this.userDetailsService = userDetailsService;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
    List<String> roles =
        userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(r -> r.replace("ROLE_", ""))
            .toList();

    String token = tokenProvider.generateToken(userDetails.getUsername(), roles);

    LoginResponse response = new LoginResponse(token, "Bearer", userDetails.getUsername());
    return ResponseEntity.ok(response);
  }
}
