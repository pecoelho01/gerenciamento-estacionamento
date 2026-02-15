# gerenciamento-estacionamento

Projeto Java de consola para gerir entradas e saídas de carros num estacionamento.

## Funcionalidades atuais

- Carregar os carros de `baseDados.txt` ao iniciar o programa (se o ficheiro existir).
- Adicionar carro com:
  - nome do motorista
  - matrícula (6 caracteres)
  - categoria
  - data/hora de entrada (automática)
- Retirar carro pela matrícula.
- Listar carros estacionados.
- Guardar os dados no ficheiro `baseDados.txt`.
- Guardar automaticamente ao sair pela opção `0`.

## Formato dos dados

- Cada linha da base de dados segue o formato:
  `motorista-matricula-categoria-dataEntrada-dataSaida`
- Formato de data/hora:
  `dd/MM/yyyy:HH'h'mm`
- Quando o carro ainda não saiu, `dataSaida` fica como `null`.

## Estrutura do projeto

- `MainEstacionamento.java`: menu e interação com o utilizador.
- `Estacionamento.java`: gestão da lista de carros e leitura/escrita da base de dados.
- `Carro.java`: modelo do carro e validações.
- `baseDados.txt`: base de dados em texto.

## Como executar

```bash
javac MainEstacionamento.java Estacionamento.java Carro.java
java MainEstacionamento
```

## Git

- Os ficheiros compilados (`*.class`) são ignorados pelo Git através do `.gitignore`.
