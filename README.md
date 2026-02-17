# Gerenciamento de Estacionamento

API em Spring Boot para registo de entrada/saída de carros, com frontend web estático e persistência em ficheiro texto.

## Estado atual do projeto

- Backend: Spring Boot (`estacionamento-api`).
- Frontend: `index.html` servido pela própria aplicação (em `src/main/resources/static`).
- Persistência: ficheiros texto (sem base de dados relacional).
- Deploy alvo: Railway.

## Funcionalidades

- Listar carros estacionados.
- Registar entrada de carro.
- Registar saída de carro.
- Persistir automaticamente a lista de carros em `baseDados.txt` após entrada/saída via API.
- Carregar os dados guardados no arranque da aplicação.

## Persistência de dados

A aplicação usa estes ficheiros:

- `/app/data/baseDados.txt`
- `/app/data/archiveCars.txt`

Detalhes importantes:

- No arranque, a aplicação cria `/app/data` e `baseDados.txt` se ainda não existirem.
- Em produção (Railway), é necessário montar um Volume persistente em `/app/data`.
- Sem Volume, os dados podem ser perdidos em redeploy/restart.

Formato das linhas em `baseDados.txt`:

`motorista-matricula-categoria-dataEntrada-dataSaida`

Formato de data/hora:

`dd/MM/yyyy:HH'h'mm`

## Endpoints da API

Base: `/api`

- `GET /api/carros`: lista os carros no estacionamento.
- `POST /api/entrada`: regista entrada.
- `POST /api/saida/{matricula}`: regista saída por matrícula.
- `GET /api/debug/base-dados`: devolve o conteúdo do `baseDados.txt` em texto (endpoint de debug).

Exemplo de body para `POST /api/entrada`:

```json
{
  "ownCar": "Tiago Ferreira",
  "matricula": "AA11BB",
  "category": "L"
}
```

## Como executar localmente

```bash
cd estacionamento-api
./mvnw spring-boot:run
```

Depois abrir:

- App web: `http://localhost:8080`
- API carros: `http://localhost:8080/api/carros`

## Configuração

`estacionamento-api/src/main/resources/application.properties`:

- `server.port=${PORT:8080}`
- `app.cors.allowed-origin-patterns=...`

Também é possível configurar CORS por variável de ambiente:

- `CORS_ALLOWED_ORIGIN_PATTERNS`

## Estrutura principal

- `estacionamento-api/src/main/java/com/pecoelhoo/estacionamento_api/controller/EstacionamentoController.java`
- `estacionamento-api/src/main/java/com/pecoelhoo/estacionamento_api/service/Estacionamento.java`
- `estacionamento-api/src/main/java/com/pecoelhoo/estacionamento_api/model/Carro.java`
- `estacionamento-api/src/main/resources/static/index.html`

## Notas

- Existe também uma classe CLI (`MainEstacionamento`) para uso manual local.
- O endpoint `/api/debug/base-dados` é útil para diagnóstico; remover ou proteger em ambiente público quando não for necessário.
