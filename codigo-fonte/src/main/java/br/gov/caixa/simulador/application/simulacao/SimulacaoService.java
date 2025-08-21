package br.gov.caixa.simulador.application.simulacao;

import br.gov.caixa.simulador.api.registro.RegistroSimulacaoDTO;
import br.gov.caixa.simulador.api.simulacao.ParcelaDTO;
import br.gov.caixa.simulador.api.simulacao.SimulacaoRequestDTO;
import br.gov.caixa.simulador.api.simulacao.SimulacaoResponseDTO;
import br.gov.caixa.simulador.api.simulacao.ParcelasPorTipoDTO;
import br.gov.caixa.simulador.application.registro.RegistroService;
import br.gov.caixa.simulador.domain.defaultdb.Produto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import br.gov.caixa.simulador.infrastructure.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SimulacaoService {

    private static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);

    @Inject
    ProdutoRepository produtoRepository;

    @Inject
    EventHubService eventHubService;

    @Inject
    RegistroService registroService;

    public List<ParcelaDTO> calculaParcelasSAC(SimulacaoRequestDTO request, RegistroSimulacaoDTO registroSimulacaoDTO){

        Produto produto = produtoRepository.filtrarProdutosPossiveis(request.valorDesejado, request.prazo).getFirst();
        BigDecimal saldoDevedor = request.valorDesejado;
        List<ParcelaDTO> lista = new ArrayList<>();

        registroSimulacaoDTO.valorDesejado = request.valorDesejado;
        registroSimulacaoDTO.prazo = request.prazo;

        BigDecimal amortizacao = request.valorDesejado
                .divide(BigDecimal.valueOf(request.prazo), 2, RoundingMode.HALF_UP);

        for(int i = 1; i <= request.prazo; i++){
            ParcelaDTO parcelaDTO = new ParcelaDTO();
            parcelaDTO.numero = i;
            parcelaDTO.valorAmortizacao = amortizacao;
            parcelaDTO.valorJuros = saldoDevedor
                    .multiply(produto.taxaJuros)
                    .setScale(2, RoundingMode.HALF_UP);

            parcelaDTO.valorPrestacao = parcelaDTO.valorAmortizacao
                    .add(parcelaDTO.valorJuros)
                    .setScale(2, RoundingMode.HALF_UP);

            registroSimulacaoDTO.valorTotalParcelasSAC = registroSimulacaoDTO.valorTotalParcelasSAC
                    .add(parcelaDTO.valorPrestacao, MC)
                    .setScale(2, RoundingMode.HALF_UP);

            saldoDevedor = saldoDevedor.subtract(parcelaDTO.valorAmortizacao, MC);
            lista.add(parcelaDTO);
        }
        return lista;
    }

    public List<ParcelaDTO> calculaParcelasPRICE(SimulacaoRequestDTO request, RegistroSimulacaoDTO registroSimulacaoDTO){
        Produto produto = produtoRepository.filtrarProdutosPossiveis(request.valorDesejado, request.prazo).getFirst();
        BigDecimal saldoDevedor = request.valorDesejado;
        List<ParcelaDTO> lista = new ArrayList<>();

        BigDecimal taxa = produto.taxaJuros;
        int prazo = request.prazo;

        BigDecimal potencia = BigDecimal.ONE.add(taxa, MC).pow(prazo, MC);

        BigDecimal pmt = saldoDevedor.multiply(taxa, MC)
                .multiply(potencia, MC)
                .divide(potencia.subtract(BigDecimal.ONE, MC), MC)
                .setScale(2, RoundingMode.HALF_UP);

        for(int i = 1; i <= prazo; i++){
            ParcelaDTO parcelaDTO = new ParcelaDTO();
            parcelaDTO.numero = i;
            parcelaDTO.valorJuros = saldoDevedor
                    .multiply(taxa, MC)
                    .setScale(2, RoundingMode.HALF_UP);

            parcelaDTO.valorPrestacao = pmt;

            parcelaDTO.valorAmortizacao = pmt.subtract(parcelaDTO.valorJuros, MC)
                    .setScale(2, RoundingMode.HALF_UP);

            saldoDevedor = saldoDevedor.subtract(parcelaDTO.valorAmortizacao, MC);
            lista.add(parcelaDTO);

            registroSimulacaoDTO.valorTotalParcelasPRICE = registroSimulacaoDTO.valorTotalParcelasPRICE
                    .add(parcelaDTO.valorPrestacao, MC)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return lista;
    }

    private ParcelasPorTipoDTO agrupaParcelasPorTipo(String tipo, List<ParcelaDTO> parcelaDTOS){
        ParcelasPorTipoDTO parcelasPorTipoDTO = new ParcelasPorTipoDTO();
        parcelasPorTipoDTO.tipo = tipo;
        parcelasPorTipoDTO.parcelas = parcelaDTOS;
        return parcelasPorTipoDTO;
    }

    private SimulacaoResponseDTO criaSimulacaoResponseDTO(Produto produto, List<ParcelaDTO> parcelasSAC, List<ParcelaDTO> parcelasPRICE){
        ParcelasPorTipoDTO sac = agrupaParcelasPorTipo("SAC", parcelasSAC);
        ParcelasPorTipoDTO price = agrupaParcelasPorTipo("PRICE", parcelasPRICE);

        SimulacaoResponseDTO response = new SimulacaoResponseDTO();
        response.idSimulacao = UUID.randomUUID();
        response.codigoProduto = produto.id;
        response.descricaoProduto = produto.nomeProduto;
        response.taxaJuros = produto.taxaJuros.setScale(4, RoundingMode.HALF_UP);
        response.resultadoSimulacao = List.of(sac, price);

        return response;
    }

    public SimulacaoResponseDTO processaSimulacao(SimulacaoRequestDTO request){
        RegistroSimulacaoDTO registroSimulacaoDTO = new RegistroSimulacaoDTO();

        Produto produto = produtoRepository.filtrarProdutosPossiveis(request.valorDesejado, request.prazo).getFirst();
        List<ParcelaDTO> parcelasSAC = calculaParcelasSAC(request, registroSimulacaoDTO);
        List<ParcelaDTO> parcelasPRICE = calculaParcelasPRICE(request, registroSimulacaoDTO);
        SimulacaoResponseDTO response = criaSimulacaoResponseDTO(produto, parcelasSAC, parcelasPRICE);

        registroSimulacaoDTO.idSimulacao = response.idSimulacao;

        registroService.persistRegistro(registroSimulacaoDTO, produto.id);
        eventHubService.sendJson(response);
        return response;
    }

}
