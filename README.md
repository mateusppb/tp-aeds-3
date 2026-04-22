# Sistema de Gestão de Consultório Médico - AEDS III

Projeto prático para a disciplina de Algoritmos e Estruturas de Dados III.

## Funcionalidades
- CRUD completo de Pacientes, Exames e Consultas.
- Indexação via **Hash Extensível** (Chaves Primárias e Estrangeiras).
- **Ordenação Externa** (Intercalação Balanceada) de Consultas por Data.
- Persistência em arquivos binários (`.dat` e `.idx`).

## Instruções de Execução

### Pré-requisitos
- JDK 17 ou superior instalado.
- Git instalado.

### Passo a passo
1. Clone o repositório:
   ```bash
   git clone [LINK_DO_SEU_REPOSITORIO]
   ```
Navegue até a pasta do projeto:

```Bash
cd [NOME_DA_PASTA]
```
Compile o projeto (via terminal):

```Bash
javac -d out src/main/java/com/aeds/**/*.java
```
Execute o programa:

```Bash
java -cp out com.aeds.Main
```
Estrutura de Pastas
src/main/java/com/aeds/dao: Lógica de persistência e crud.

src/main/java/com/aeds/model: Classes de Entidades.

src/main/java/com/aeds/utils: Implementação do Hash e Ordenação.

src/main/java/com/aeds/view: Interface com o usuário.


---
