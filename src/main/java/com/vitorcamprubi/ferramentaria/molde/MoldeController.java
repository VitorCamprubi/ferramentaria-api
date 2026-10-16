package com.vitorcamprubi.ferramentaria.molde;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vitorcamprubi.ferramentaria.molde.dto.AtualizacaoMoldeRequest;
import com.vitorcamprubi.ferramentaria.molde.dto.MoldeRequest;
import com.vitorcamprubi.ferramentaria.molde.dto.MoldeResponse;
import com.vitorcamprubi.ferramentaria.molde.dto.RegistroCiclosRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/moldes")
@Tag(name = "Moldes", description = "Cadastro de moldes e registro de ciclos de produção")
public class MoldeController {

    private final MoldeService moldeService;

    public MoldeController(MoldeService moldeService) {
        this.moldeService = moldeService;
    }

    @PostMapping
    @Operation(summary = "Cadastra um molde")
    public ResponseEntity<MoldeResponse> cadastrar(@RequestBody @Valid MoldeRequest request) {
        MoldeResponse molde = moldeService.cadastrar(request);
        return ResponseEntity.created(URI.create("/moldes/" + molde.id())).body(molde);
    }

    @GetMapping
    @Operation(summary = "Lista os moldes com o percentual de uso até a próxima preventiva")
    public List<MoldeResponse> listar() {
        return moldeService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um molde pelo id")
    public MoldeResponse buscar(@PathVariable Long id) {
        return moldeService.buscar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza descrição, cavidades e limite de ciclos do molde")
    public MoldeResponse atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoMoldeRequest request) {
        return moldeService.atualizar(id, request);
    }

    @PatchMapping("/{id}/ciclos")
    @Operation(summary = "Registra ciclos injetados pela produção")
    public MoldeResponse registrarCiclos(@PathVariable Long id, @RequestBody @Valid RegistroCiclosRequest request) {
        return moldeService.registrarCiclos(id, request);
    }
}
