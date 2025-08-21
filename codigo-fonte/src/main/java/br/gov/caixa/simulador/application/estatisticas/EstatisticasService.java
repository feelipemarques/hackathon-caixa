package br.gov.caixa.simulador.application.estatisticas;

import br.gov.caixa.simulador.api.estatisticas.EstatisticasDTO;
import br.gov.caixa.simulador.api.estatisticas.EstatisticasPorDiaDTO;
import br.gov.caixa.simulador.domain.defaultdb.Produto;
import br.gov.caixa.simulador.domain.registrodb.Registro;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import br.gov.caixa.simulador.infrastructure.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class EstatisticasService {

    @Inject
    ProdutoRepository produtoRepository;

    public List<EstatisticasPorDiaDTO> listaEstatisticasPorProdutoEDia(LocalDate dataReferencia) {
        List<Registro> registros = Registro.find("dataReferencia = ?1", dataReferencia).list();

        EstatisticasPorDiaDTO dia = new EstatisticasPorDiaDTO();
        dia.dataReferencia = dataReferencia;
        dia.simulacoes = new ArrayList<>();

        if(registros.isEmpty()){
            return List.of(dia);
        }

        Map<Integer, List<Registro>> registrosPorProduto = registros.stream()
                .collect(Collectors.groupingBy(r -> r.codigoProduto));

        for (Map.Entry<Integer, List<Registro>> entry : registrosPorProduto.entrySet()) {
            Integer codigoProduto = entry.getKey();
            List<Registro> registrosProduto = entry.getValue();

            Produto produto = Produto.findById(codigoProduto);
            if (produto == null) continue; // segurança

            EstatisticasDTO estat = new EstatisticasDTO();
            estat.codigoProduto = codigoProduto;
            estat.descricaoProduto = produto.nomeProduto;

            estat.taxaMediaJuro = registrosProduto.stream()
                    .map(r -> produto.taxaJuros)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(registrosProduto.size()), 4, RoundingMode.HALF_UP);

            estat.valorMedioPrestacao = registrosProduto.stream()
                    .map(r -> r.valorTotalParcelasPRICE
                            .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(registrosProduto.size()), 2, RoundingMode.HALF_UP);

            estat.valorTotalDesejado = registrosProduto.stream()
                    .map(r -> r.valorDesejado)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            estat.valorTotalCredito = registrosProduto.stream()
                    .map(r -> r.valorTotalParcelasPRICE)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            dia.simulacoes.add(estat);
        }

        return List.of(dia);
    }

}
