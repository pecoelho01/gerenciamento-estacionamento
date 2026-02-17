package com.pecoelhoo.estacionamento_api.controller;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pecoelhoo.estacionamento_api.model.Carro;
import com.pecoelhoo.estacionamento_api.service.Estacionamento;

@RestController
@RequestMapping("/api")
public class EstacionamentoController {
    private final Estacionamento estacionamento;

    public EstacionamentoController(Estacionamento estacionamento) {
        this.estacionamento = estacionamento;
    }

    @GetMapping("/carros")
    public List<Carro> listarCarros() {
        return estacionamento.getCarsParking();
    }

    @GetMapping(value = "/debug/base-dados", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> verBaseDados() {
        try {
            if (!estacionamento.getBD().exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ficheiro baseDados.txt não encontrado.");
            }
            return ResponseEntity.ok(Files.readString(estacionamento.getBD().toPath()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao ler baseDados.txt: " + e.getMessage());
        }
    }

    @PostMapping("/entrada")
    public ResponseEntity<Map<String, String>> entrada(@RequestBody EntradaRequest request) {
        try {
            Carro carro = new Carro(
                request.ownCar(),
                request.matricula(),
                request.category(),
                LocalDateTime.now(),
                null
            );

            int result = estacionamento.putCar(carro);
            if (result == 0) {
                estacionamento.writeDB();
                return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Carro entrou com sucesso."));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Carro já está no estacionamento."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/saida/{matricula}")
    public ResponseEntity<Map<String, String>> saida(@PathVariable String matricula) {
        for (Carro carro : estacionamento.getCarsParking()) {
            if (carro.getMatricula().equalsIgnoreCase(matricula)) {
                estacionamento.pushCar(carro);
                estacionamento.writeDB();
                return ResponseEntity.ok(Map.of("message", "Carro saiu com sucesso."));
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("message", "Carro não encontrado."));
    }

    public record EntradaRequest(String ownCar, String matricula, char category) {}
}
