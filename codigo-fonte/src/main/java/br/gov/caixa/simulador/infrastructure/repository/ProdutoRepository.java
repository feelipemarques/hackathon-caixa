package br.gov.caixa.simulador.infrastructure.repository;

import br.gov.caixa.simulador.domain.exception.ProdutoNaoEncontrado;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import br.gov.caixa.simulador.domain.defaultdb.Produto;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ProdutoRepository implements PanacheRepository<Produto> {

    @CacheResult(cacheName = "produtos")
    public List<Produto> filtrarProdutosPossiveis(BigDecimal valor, int prazo){
        System.out.println("Buscando produtos no DB..."); //"log" pra verificar o uso do cache funcional
        List<Produto> produtoPossivel = list("valorMinimo <= ?1 and (valorMaximo is null or valorMaximo >= ?1) and prazoMinimo <= ?2 and (prazoMaximo is null or prazoMaximo >= ?2)", valor, prazo);
        if(produtoPossivel.isEmpty()){
            throw new ProdutoNaoEncontrado();
        }else {
            return produtoPossivel;
        }
    }

}
