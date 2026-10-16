package com.vitorcamprubi.ferramentaria.molde;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vitorcamprubi.ferramentaria.common.exception.RecursoNaoEncontradoException;
import com.vitorcamprubi.ferramentaria.common.exception.RegraNegocioException;
import com.vitorcamprubi.ferramentaria.molde.dto.AtualizacaoMoldeRequest;
import com.vitorcamprubi.ferramentaria.molde.dto.MoldeRequest;
import com.vitorcamprubi.ferramentaria.molde.dto.MoldeResponse;
import com.vitorcamprubi.ferramentaria.molde.dto.RegistroCiclosRequest;

@Service
public class MoldeService {

    private final MoldeRepository moldeRepository;

    public MoldeService(MoldeRepository moldeRepository) {
        this.moldeRepository = moldeRepository;
    }

    @Transactional
    public MoldeResponse cadastrar(MoldeRequest request) {
        if (moldeRepository.existsByCodigo(request.codigo())) {
            throw new RegraNegocioException("Já existe um molde com o código " + request.codigo());
        }
        Molde molde = new Molde(request.codigo(), request.descricao(), request.cavidades(),
                request.limiteCiclosPreventiva());
        return MoldeResponse.de(moldeRepository.save(molde));
    }

    @Transactional(readOnly = true)
    public List<MoldeResponse> listar() {
        return moldeRepository.findAll().stream()
                .map(MoldeResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public MoldeResponse buscar(Long id) {
        return MoldeResponse.de(buscarEntidade(id));
    }

    @Transactional
    public MoldeResponse atualizar(Long id, AtualizacaoMoldeRequest request) {
        Molde molde = buscarEntidade(id);
        molde.atualizarDados(request.descricao(), request.cavidades(), request.limiteCiclosPreventiva());
        return MoldeResponse.de(molde);
    }

    @Transactional
    public MoldeResponse registrarCiclos(Long id, RegistroCiclosRequest request) {
        Molde molde = buscarEntidade(id);
        molde.registrarCiclos(request.quantidade());
        return MoldeResponse.de(molde);
    }

    private Molde buscarEntidade(Long id) {
        return moldeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Molde %d não encontrado".formatted(id)));
    }
}
