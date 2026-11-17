# Cota de Rua

Projeto desenvolvido para a Global Solution da disciplina Advanced Programming and Mobile Development.

**Aluno:** Leonardo Fernandes Veleiro  
**RM:** 557773  
**Curso:** Engenharia da Computação — FIAP

## Proposta

O Cota de Rua é um protótipo para acompanhar pontos de alagamento e organizar informações que chegam de fontes diferentes.

Escolhi trabalhar com situações próximas da rotina de um bairro: água acumulando na entrada de lojas, na passagem de pedestres e perto de uma escola. A ideia é permitir que o usuário veja primeiro os pontos que precisam de maior atenção, identificando de onde veio a informação e quando ela foi atualizada.

Os locais da Vila Aurora usados na demonstração são fictícios. As medições dos sensores são simuladas. A previsão meteorológica de São Paulo é consultada na fonte pública Open-Meteo.

## Tecnologias

- Java 21 e Spring Boot.
- Spring Web, Spring Data JPA e Bean Validation.
- Banco H2 salvo em arquivo.
- React Native com Expo e TypeScript.
- JUnit 5.
- Git para acompanhar a evolução do projeto.

## Organização

- `backend`: API, regras de classificação, integração externa e banco.
- `mobile`: aplicativo React Native, também executável no navegador.

O backend está organizado em camadas:

- `controller`: recebe as requisições HTTP.
- `service`: executa as regras e coordena as operações.
- `repository`: acessa o banco.
- `model`: representa os dados persistidos.
- `dto`: define os dados de entrada e saída.
- `config`: configura a aplicação e os pontos iniciais.

## Funcionalidades

- Lista de pontos monitorados.
- Registro de medições simuladas.
- Histórico das últimas 20 medições de cada ponto.
- Classificação dos pontos por prioridade.
- Exibição de origem, horário e confiança da informação.
- Cadastro de relatos comunitários pelo aplicativo.
- Consulta dos últimos 20 relatos de cada ponto pela API.
- Previsão pública de chuva para São Paulo.
- Persistência das medições, relatos e consultas meteorológicas.
- Atualização automática do painel.
- Aviso visual para novas medições críticas.
- Detecção de desconexão e tentativa automática de recuperação.

## Classificação dos alertas

A análise utiliza faixas de nível de água e ordena os pontos pela pontuação:

| Nível registrado | Classificação | Pontuação |
|---|---|---|
| Menor que 10 cm | Baixo | 10 |
| De 10 até menos de 20 cm | Atenção | 40 |
| De 20 até menos de 40 cm | Alto | 70 |
| A partir de 40 cm | Crítico | 100 |
| Sem medição | Sem dados | 0 |

Em caso de empate, os pontos são ordenados pelo nome.

Esses limites foram definidos para a demonstração acadêmica. Não são parâmetros oficiais de segurança.

Uma medição com mais de cinco minutos é marcada como desatualizada. Sua classificação é mantida como referência do último registro, sem indicar que a situação atual está confirmada.

Esta implementação utiliza análise por regras, atendendo à alternativa de análise prevista no enunciado. Não utiliza um modelo de aprendizado de máquina.

## Fontes e confiança

| Fonte | Tratamento |
|---|---|
| Sensor simulado | Pontuação demonstrativa de confiança de 80/100 |
| Relato comunitário | Pontuação inicial de 40/100, sem verificação |
| Open-Meteo | Confiança não quantificada; previsão de modelo meteorológico |

As pontuações do protótipo não representam probabilidades estatisticamente calibradas.

A probabilidade de chuva fornecida pela Open-Meteo não representa a probabilidade de alagamento nem a confiança do dado.

Os relatos ficam registrados separadamente e ainda não alteram automaticamente a classificação dos sensores. A previsão também é exibida como contexto regional, sem confirmar ocorrências nos pontos fictícios.

Fonte: https://open-meteo.com/  
Documentação: https://open-meteo.com/en/docs

## Requisitos para execução

- JDK 21.
- Node.js 22 e npm.
- Acesso à internet para instalar dependências e consultar a previsão.
- Dois terminais: um para o backend e outro para o Expo.

Não é necessário instalar o Maven globalmente. O backend inclui Maven Wrapper.

## Executar o backend no Windows

Abra um terminal na pasta `backend`:

```powershell
cd backend
```

Configure `JAVA_HOME` para o caminho do JDK 21 instalado na sua máquina. No ambiente usado no desenvolvimento:

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
```

Inicie:

```powershell
.\mvnw.cmd spring-boot:run
```

A API fica disponível em:

```text
http://localhost:8080/api/status
```

O banco é criado em `backend/dados`. Essa pasta não é versionada.

Na primeira execução, são cadastrados três pontos fictícios, desde que a tabela de pontos esteja vazia. As medições são inseridas separadamente pelos comandos de simulação.

## Executar o aplicativo na web

Em outro terminal, a partir da raiz do repositório:

```powershell
cd mobile
npm.cmd ci
```

Crie um arquivo `.env` dentro de `mobile`:

```properties
EXPO_PUBLIC_API_URL=http://localhost:8080
```

Execute:

```powershell
npx.cmd expo start --web --port 8083
```

Abra:

```text
http://localhost:8083
```

O backend permite acesso web pelas origens locais nas portas 8081, 8082 e 8083.

Após alterar o `.env`, reinicie o Expo. O endereço da API é público na configuração do aplicativo e não deve conter senhas ou segredos.

## Execução em dispositivo móvel

O código do aplicativo é React Native com Expo. Para abrir com Expo Go:

```powershell
npx.cmd expo start
```

Em um dispositivo físico, `localhost` aponta para o próprio celular. O `.env` precisa usar um endereço do backend acessível pelo dispositivo.

O túnel do Expo permite carregar o aplicativo, mas não cria automaticamente um túnel para a API Java.

A interface foi aberta no Expo Go em um iPhone. A integração completa com a API foi validada na versão web; a conexão entre o iPhone e o backend permaneceu pendente por dificuldades de acesso pela rede local.

## Rotas principais

| Método | Rota | Função |
|---|---|---|
| GET | `/api/status` | Verificar disponibilidade |
| GET | `/api/pontos` | Listar pontos |
| GET | `/api/alertas` | Consultar classificação e prioridade |
| POST | `/api/simulacao/medicoes` | Registrar medição simulada |
| GET | `/api/pontos/{id}/medicoes` | Consultar histórico de medições |
| POST | `/api/relatos` | Registrar relato |
| GET | `/api/pontos/{id}/relatos` | Consultar relatos |
| GET | `/api/previsao` | Consultar previsão pública |

## Simular uma medição

Com o backend ligado, execute no PowerShell:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/simulacao/medicoes" -ContentType "application/json" -Body '{"pontoId":3,"nivelAguaCm":50}'
```

No banco inicial, os pontos são:

- ID 1: Travessa do Mercado.
- ID 2: Passagem do Córrego.
- ID 3: Rua da Escola.

É possível conferir os IDs em `/api/pontos`.

A API aceita níveis entre 0 e 500 cm. Esse intervalo é uma validação do protótipo.

## Testes automatizados

Na pasta `backend`, com Java 21 configurado:

```powershell
.\mvnw.cmd "-Dtest=ClassificadorAlertaTest" test
```

Os seis testes verificam:

- Limites entre as faixas de classificação.
- Manutenção do alerta crítico quando a medição fica antiga.
- Medição com exatamente cinco minutos.
- Medição com mais de cinco minutos.
- Rejeição de níveis inválidos.
- Rejeição de horário futuro.

Resultado obtido: seis testes executados, sem falhas ou erros.

Esse comando executa especificamente os testes do classificador; não representa uma validação de toda a aplicação.

Também foram feitos testes manuais de cadastro, consulta, rejeição de nível negativo, persistência, envio de relato, atualização do painel e recuperação de conexão.

## Demonstração da desconexão

1. Iniciar o backend e o Expo.
2. Abrir o painel e aguardar os pontos aparecerem.
3. Encerrar somente o backend com `Ctrl + C`.
4. Manter a página aberta, sem recarregar.
5. Aguardar o aviso de desconexão.
6. Confirmar que os últimos dados continuam na tela.
7. Iniciar o backend novamente.
8. Observar a recuperação automática e o avanço do horário da consulta.

Os últimos alertas ficam na memória da tela. Recarregar a página durante a desconexão limpa esse estado.

## Demonstração do aviso crítico

1. Abrir o painel e aguardar a primeira consulta.
2. Enviar uma nova medição de pelo menos 40 cm.
3. Aguardar a atualização automática.
4. Observar o aviso de nova medição crítica.
5. Clicar em “Entendi”.
6. Confirmar que a mesma medição não gera outro aviso nas consultas seguintes.

As medições presentes na primeira consulta não geram esse aviso. A deduplicação vale durante a sessão da tela.

Os avisos são internos, com o aplicativo aberto. Não foram implementadas notificações push em segundo plano.

## Disponibilidade, escala e dados pessoais

As consultas do painel são sequenciais e têm limite de espera. Quando falham, os últimos alertas são preservados e uma nova tentativa é feita automaticamente.

A previsão é reaproveitada por até 15 minutos no mesmo dia. Quando a fonte externa falha, o backend pode devolver a última consulta salva, identificada como desatualizada. Sem registro anterior, retorna HTTP 503.

O formulário não exige nome, CPF ou telefone e orienta o usuário a não inserir dados pessoais na descrição. Como o campo é livre, essa orientação não equivale a uma remoção automática de dados pessoais.

A arquitetura em camadas permite evoluir as funcionalidades. Para uso com maior volume, seria necessário adotar banco de produção, otimizar consultas, adicionar paginação, autenticação, controle de acesso e limites de requisição.

A versão atual é um protótipo local, sem autenticação, e não deve ser tratada como um serviço pronto para operação pública.

## Limitações

- Pontos fictícios e sensores simulados.
- Regras de classificação demonstrativas.
- Confiança definida por regras do protótipo.
- Relatos e previsão ainda não são cruzados para alterar a prioridade.
- Avisos somente com a tela aberta.
- Integração completa no iPhone ainda não validada.
- Dependência de internet para atualizar a previsão.
- Ausência de autenticação e moderação dos relatos.

O sistema não substitui os canais oficiais de emergência e monitoramento.