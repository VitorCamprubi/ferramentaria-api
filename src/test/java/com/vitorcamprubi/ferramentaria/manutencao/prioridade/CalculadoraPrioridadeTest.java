package com.vitorcamprubi.ferramentaria.manutencao.prioridade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.vitorcamprubi.ferramentaria.manutencao.Prioridade;
import com.vitorcamprubi.ferramentaria.manutencao.TipoManutencao;
import com.vitorcamprubi.ferramentaria.molde.Molde;

class CalculadoraPrioridadeTest {

    private final CalculadoraPrioridade calculadora = new CalculadoraPrioridade(List.of(
            new PrioridadePreventiva(), new PrioridadeCorretiva(), new PrioridadeMelhoria()));

    @ParameterizedTest(name = "{0} de 100.000 ciclos -> {1}")
    @CsvSource({
            "0,      BAIXA",
            "79999,  BAIXA",
            "80000,  MEDIA",
            "99999,  MEDIA",
            "100000, ALTA",
            "130000, ALTA"
    })
    void preventivaUsaOPercentualDeCiclosAteOLimite(long ciclos, Prioridade esperada) {
        Molde molde = moldeComCiclos(ciclos, 100_000);

        Prioridade prioridade = calculadora.calcular(TipoManutencao.PREVENTIVA, molde, false);

        assertThat(prioridade).isEqualTo(esperada);
    }

    @Test
    void corretivaQueParouAProducaoEhCritica() {
        Molde molde = moldeComCiclos(10_000, 100_000);

        assertThat(calculadora.calcular(TipoManutencao.CORRETIVA, molde, true)).isEqualTo(Prioridade.CRITICA);
        assertThat(calculadora.calcular(TipoManutencao.CORRETIVA, molde, false)).isEqualTo(Prioridade.ALTA);
    }

    @Test
    void melhoriaEntraSempreComPrioridadeBaixa() {
        Molde moldeNoLimite = moldeComCiclos(200_000, 100_000);

        assertThat(calculadora.calcular(TipoManutencao.MELHORIA, moldeNoLimite, true)).isEqualTo(Prioridade.BAIXA);
    }

    @Test
    void naoSobeSeFaltarEstrategiaParaAlgumTipo() {
        List<CalculoPrioridade> semMelhoria = List.of(new PrioridadePreventiva(), new PrioridadeCorretiva());

        assertThatThrownBy(() -> new CalculadoraPrioridade(semMelhoria))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MELHORIA");
    }

    @Test
    void naoSobeSeHouverDuasEstrategiasParaOMesmoTipo() {
        List<CalculoPrioridade> duplicada = List.of(new PrioridadePreventiva(), new PrioridadeCorretiva(),
                new PrioridadeMelhoria(), new PrioridadeCorretiva());

        assertThatThrownBy(() -> new CalculadoraPrioridade(duplicada))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CORRETIVA");
    }

    private Molde moldeComCiclos(long ciclos, long limite) {
        Molde molde = new Molde("MD-TESTE", "Molde de teste", 4, limite);
        molde.registrarCiclos(ciclos);
        return molde;
    }
}
