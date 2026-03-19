# Gerenciamento de Estacionamento

API em Spring Boot para registo de entrada/saída de carros, com frontend web estático e persistência em ficheiro texto.

## Estado atual do projeto

- Backend: Spring Boot (`estacionamento-api`).
- Frontend: `index.html` servido pela própria aplicação (em `src/main/resources/static`).
- Persistência: ficheiros texto (sem base de dados relacional).
- Deploy alvo: Render.

## Funcionalidades

- Listar carros estacionados.
- Registar entrada de carro.
- Registar saída de carro.
- Persistir automaticamente a lista de carros em `baseDados.txt` após entrada/saída via API.
- Carregar os dados guardados no arranque da aplicação.

## Persistência de dados

A aplicação usa estes ficheiros:

- `./data/baseDados.txt` (default local)
- `./data/archiveCars.txt` (default local)

Detalhes importantes:

- No arranque, a aplicação cria o diretório configurado em `DATA_DIR` e `baseDados.txt` se ainda não existirem.
- Em produção (Render), use `DATA_DIR=/var/data` e monte um disco persistente no mesmo path.
- Sem disco persistente, os dados podem ser perdidos em restart/redeploy.

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

## Deploy no Render (evitar perda de dados)

1. No serviço web, criar um **Persistent Disk** com mount path `/var/data`.
2. Garantir variável de ambiente `DATA_DIR=/var/data`.
3. Fazer deploy e validar em `/api/debug/base-dados` que o ficheiro está a ser lido/escrito.
4. Se o disco estiver montado noutro path, usar esse mesmo valor em `DATA_DIR`.

## Configuração

`estacionamento-api/src/main/resources/application.properties`:

- `server.port=${PORT:8080}`
- `app.cors.allowed-origin-patterns=...`
- `app.data.dir=${DATA_DIR:./data}`

Também é possível configurar CORS por variável de ambiente:

- `CORS_ALLOWED_ORIGIN_PATTERNS`

Também é possível configurar persistência por variável de ambiente:

- `DATA_DIR`

## Estrutura principal

- `estacionamento-api/src/main/java/com/pecoelhoo/estacionamento_api/controller/EstacionamentoController.java`
- `estacionamento-api/src/main/java/com/pecoelhoo/estacionamento_api/service/Estacionamento.java`
- `estacionamento-api/src/main/java/com/pecoelhoo/estacionamento_api/model/Carro.java`
- `estacionamento-api/src/main/resources/static/index.html`

## Notas

- Existe também uma classe CLI (`MainEstacionamento`) para uso manual local.
- O endpoint `/api/debug/base-dados` é útil para diagnóstico; remover ou proteger em ambiente público quando não for necessário.
