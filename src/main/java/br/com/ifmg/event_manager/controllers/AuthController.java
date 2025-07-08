package br.com.ifmg.event_manager.controllers;

import br.com.ifmg.event_manager.dtos.NewPasswordDTO;
import br.com.ifmg.event_manager.dtos.RequestTokenDTO;
import br.com.ifmg.event_manager.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody RequestTokenDTO dto){

        authService.createRecoverToken(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "new-password")
    public ResponseEntity<Void> saveNewPassword(@Valid @RequestBody NewPasswordDTO dto){

        authService.saveNewPassword(dto);
        return ResponseEntity.noContent().build();
    }
}
