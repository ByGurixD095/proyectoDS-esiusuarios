package edu.esi.ds.esiusuarios.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.esi.ds.esiusuarios.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/external")
public class ExternalController {

    @Autowired
    UsuarioService userService;

    @GetMapping("/token/{tokenID}")
    public String getMethodName(@PathVariable("tokenID") String tokenID) {
        return this.userService.validateToken(tokenID);
    }

    @GetMapping("/saludoToken")
    public String getToken() {
        return userService.token();
    }

}