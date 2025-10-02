package Restaurante.example.Restaurante.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    // Mapeia a URL base (/) para a página inicial
    @GetMapping("/")
    public String index() {
        return "index"; // Retorna o template index.html
    }
}