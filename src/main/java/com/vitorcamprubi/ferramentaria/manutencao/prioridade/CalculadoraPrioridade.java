package com.vitorcamprubi.ferramentaria.manutencao.prioridade;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.vitorcamprubi.ferramentaria.manutencao.Prioridade;
import com.vitorcamprubi.ferramentaria.manutencao.TipoManutencao;
import com.vitorcamprubi.ferramentaria.molde.Molde;

/**
 * Escolhe a estratégia certa para o tipo de manutenção.
 * <p>
 * O Spring injeta todas as implementações de {@link CalculoPrioridade} na lista,
 * e aqui elas viram um mapa indexado pelo tipo. Se faltar estratégia para algum
 * tipo (ou houver duas para o mesmo), a aplicação nem sobe: prefiro descobrir o
 * erro na inicialização do que no meio de uma requisição.
 * <p>
 * Como todo {@code @Component}, esta classe é um <b>Singleton</b> gerenciado pelo
 * Spring: o mapa é montado uma única vez e compartilhado por todas as requisições.
 */
@Component
public class CalculadoraPrioridade {

    private final Map<TipoManutencao, CalculoPrioridade> estrategias = new EnumMap<>(TipoManutencao.class);

    public CalculadoraPrioridade(List<CalculoPrioridade> calculos) {
        for (CalculoPrioridade calculo : calculos) {
            CalculoPrioridade anterior = estrategias.put(calculo.tipo(), calculo);
            if (anterior != null) {
                throw new IllegalStateException("Mais de uma estratégia de prioridade para " + calculo.tipo());
            }
        }
        for (TipoManutencao tipo : TipoManutencao.values()) {
            if (!estrategias.containsKey(tipo)) {
                throw new IllegalStateException("Nenhuma estratégia de prioridade para " + tipo);
            }
        }
    }

    public Prioridade calcular(TipoManutencao tipo, Molde molde, boolean paradaDeProducao) {
        return estrategias.get(tipo).calcular(molde, paradaDeProducao);
    }
}
