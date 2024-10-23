package com.example.pocauth.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class PocController {
    private final UtenteService utenteService;
    private final JwtUtils jwtUtils;

    @GetMapping("/authenticate")
    public ResponseEntity<JwtDto> getAuth(){
        var user = utenteService.loadUserByUsername("hertzino");
        if (user != null) {
            return ResponseEntity.ok(new JwtDto(jwtUtils.generateToken("hertzino")));
        }
        return ResponseEntity.ok(null);
    }

    @GetMapping("/foo")
    public ResponseEntity<Boolean> getFoo(){
        return ResponseEntity.ok(true);
    }
}
