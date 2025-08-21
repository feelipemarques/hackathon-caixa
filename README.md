# Simulador de Empréstimo

## Descrição
Esta API gerencia simulações de empréstimo, gera estatísticas de simulações (por dia e produto) e fornece dados de telemetria da aplicação.

**Tecnologias:** Java, Quarkus, Docker, SQL Server/H2, SmallRye OpenAPI (Swagger UI).

---

## 1️⃣ Rodando via Docker

### Pré-requisitos
- Docker e Docker Compose instalados.

### Estrutura na pasta `docker-executavel`
- `docker-compose.yml` → Contém 3 services:
  - **sqlserver**: Container SQL Server vazio.
  - **init-db**: Container MSSQL Tools que cria tabelas e insere dados do `init-db.sql`.
  - **app**: Container com a API apontando para o SQL Server.
- `init-db.sql` → Script de criação das tabelas e inserção dos produtos mínimos.

### Como rodar
```bash
cd docker-executavel
docker compose up --build
```

A API estará disponível em: `http://localhost:8080`  
SQL Server: `localhost:1433`

---

## 2️⃣ Rodando via Quarkus Dev Mode

### Pré-requisitos
- Java 17+
- Maven (ou use o wrapper `./mvnw`)

### Como rodar
```bash
cd codigo-fonte
./mvnw quarkus:dev
```

A API estará disponível em: `http://localhost:8080`

---

## 3️⃣ Endpoints principais

### Telemetria
```http
GET /telemetria
```
**Exemplo de retorno:**
```json
{
  "dataReferencia": "2025-08-20",
  "listaEndpoints": [
    {
      "nomeApi": "Simulacao",
      "qtdRequisicoes": 0,
      "tempoMedio": 0,
      "tempoMinimo": 0,
      "tempoMaximo": 0,
      "percentualSucesso": 0.0
    }
  ]
}
```

### Simulação
```http
POST /simulacao
Content-Type: application/json
```
**Exemplo de payload:**
```json
{
  "valorDesejado": 200,
  "prazo": 2
}
```
**Exemplo de retorno:**
```json
{
  "idSimulacao": "uuid",
  "codigoProduto": 1,
  "descricaoProduto": "Produto 1",
  "taxaJuros": 0.0179,
  "resultadoSimulacao": [
    {
      "tipo": "SAC",
      "parcelas": [
        { "numero": 1, "valorAmortizacao": 100.00, "valorJuros": 3.58, "valorPrestacao": 103.58 }
      ]
    }
  ]
}
```

### Estatísticas
```http
GET /estatisticas?data=2025-08-20
```
**Exemplo de retorno:**
```json
[
  {
    "dataReferencia": "2025-08-20",
    "simulacoes": [
      {
        "codigoProduto": 1,
        "descricaoProduto": "Produto 1",
        "taxaMediaJuro": 0.0179,
        "valorMedioPrestacao": 4107.56,
        "valorTotalDesejado": 2000.00,
        "valorTotalCredito": 4107.56
      }
    ]
  }
]
```

---

## 4️⃣ Swagger/OpenAPI
Acesse a documentação interativa em:
```
http://localhost:8080/q/swagger-ui
```

---

## 5️⃣ Observações importantes
- **Banco SQL Server:** O script `init-db.sql` garante os produtos mínimos. É possível alterar ou adicionar dados se necessário.
- **Portas:** API → 8080, SQL Server → 1433 (conforme Docker Compose).
- **Resetar containers:**
```bash
docker compose down -v
docker compose up --build
```
- **Código fonte:** Não inclui a pasta `target/`. Quem pegar o ZIP pode buildar localmente via Maven.
