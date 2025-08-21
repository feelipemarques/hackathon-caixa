package br.gov.caixa.simulador.domain.defaultdb;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(schema = "dbo", name = "PRODUTO")
public class Produto extends PanacheEntityBase {

    @Id
    @Column(name = "CO_PRODUTO")
    public int id;

    @Column(nullable = false, name = "NO_PRODUTO", length = 200)
    public String nomeProduto;

    @Column(nullable = false, name = "PC_TAXA_JUROS", precision = 10, scale = 9)
    public BigDecimal taxaJuros;

    @Column(nullable = false, name = "NU_MINIMO_MESES")
    public Short prazoMinimo;

    @Column(name = "NU_MAXIMO_MESES")
    public Short prazoMaximo;

    @Column(nullable = false, name = "VR_MINIMO", precision = 18, scale = 2)
    public BigDecimal valorMinimo;

    @Column(name = "VR_MAXIMO", precision = 18, scale = 2)
    public BigDecimal valorMaximo;


}
