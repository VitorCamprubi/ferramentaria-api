package com.vitorcamprubi.ferramentaria.manutencao;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vitorcamprubi.ferramentaria.manutencao.dto.AberturaOrdemRequest;
import com.vitorcamprubi.ferramentaria.manutencao.dto.OrdemResponse;
import com.vitorcamprubi.ferramentaria.manutencao.liberacao.ChecklistLiberacao;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * O controller conversa só com a {@link ManutencaoFacade}. Ele não sabe que existem
 * estratégias de prioridade nem uma corrente de verificações por trás.
 */
@RestController
@RequestMapping("/ordens")
@Tag(name = "Ordens de manutenção", description = "Abertura, fila e liberação de moldes")
public class OrdemManutencaoController {

    private final ManutencaoFacade manutencaoFacade;

    public OrdemManutencaoController(ManutencaoFacade manutencaoFacade) {
        this.manutencaoFacade = manutencaoFacade;
    }

    @PostMapping
    @Operation(summary = "Abre uma ordem de manutenção e tira o molde da produção",
            description = "A prioridade é calculada automaticamente conforme o tipo da manutenção")
    public ResponseEntity<OrdemResponse> abrir(@RequestBody @Valid AberturaOrdemRequest request) {
        OrdemResponse ordem = manutencaoFacade.abrirOrdem(request);
        return ResponseEntity.created(URI.create("/ordens/" + ordem.id())).body(ordem);
    }

    @GetMapping
    @Operation(summary = "Lista as ordens na ordem da fila (mais urgente primeiro)",
            description = "Use status=ABERTA para ver só o que está pendente na ferramentaria")
    public List<OrdemResponse> listar(@RequestParam(required = false) StatusOrdem status) {
        return manutencaoFacade.listar(status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma ordem pelo id")
    public OrdemResponse buscar(@PathVariable Long id) {
        return manutencaoFacade.buscar(id);
    }

    @PostMapping("/{id}/conclusao")
    @Operation(summary = "Conclui a ordem e libera o molde",
            description = "Passa pelo checklist de liberação; se alguma etapa reprovar, retorna 422 com o motivo")
    public OrdemResponse concluir(@PathVariable Long id, @RequestBody @Valid ChecklistLiberacao checklist) {
        return manutencaoFacade.concluirOrdem(id, checklist);
    }
}
