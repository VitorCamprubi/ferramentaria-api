package com.vitorcamprubi.ferramentaria.manutencao.liberacao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Monta a corrente de liberação na mesma ordem em que o checklist é feito na prática:
 * primeiro os testes de bancada (água e extração), depois o registro da causa raiz
 * e, por último, o try-out na injetora.
 * <p>
 * A corrente é criada uma vez, na subida da aplicação, e reaproveitada em todas as
 * requisições (bean <b>Singleton</b>). Isso é seguro porque, depois de montada,
 * nenhuma etapa guarda estado: elas só leem a ordem e o checklist recebidos.
 */
@Configuration
public class LiberacaoConfig {

    @Bean
    public EtapaLiberacao cadeiaLiberacao() {
        EtapaLiberacao inicio = new VerificacaoRefrigeracao();
        inicio.entaoVerificar(new VerificacaoExtracao())
                .entaoVerificar(new VerificacaoCausaRaiz())
                .entaoVerificar(new VerificacaoTryOut());
        return inicio;
    }
}
