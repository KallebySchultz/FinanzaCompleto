# Finanza - Build com Ant

Este documento descreve como compilar e executar o projeto Finanza usando Apache Ant.

## Pré-requisitos

- Java 8 ou superior
- Apache Ant instalado
- Conexão com a internet (para baixar dependências)

## Estrutura do Projeto

```
finanza/
├── finanzadesktop/     # Aplicação Desktop Java/Swing
│   ├── build.xml       # Build Ant para desktop
│   ├── src/            # Código fonte
│   ├── lib/            # Dependências (criado automaticamente)
│   ├── dist/           # JAR final (criado automaticamente)
│   └── build/          # Classes compiladas (criado automaticamente)
├── server-java/        # Servidor Java
│   ├── build.xml       # Build Ant para servidor
│   ├── src/            # Código fonte do servidor
│   └── dist/           # JAR final (criado automaticamente)
└── database/
    └── finanza.sql     # Script SQL para criar banco
```

## Compilação e Execução

### 1. Aplicação Desktop

```bash
cd finanzadesktop

# Ver informações do build
ant info

# Baixar dependências e compilar
ant compile

# Criar JAR executável
ant jar

# Executar aplicação
ant run

# Ou executar diretamente
java -jar dist/finanza-desktop.jar

# Limpar arquivos gerados
ant clean

# Limpar tudo incluindo dependências
ant clean-all
```

### 2. Servidor

```bash
cd server-java

# Ver informações do build
ant info

# Compilar
ant compile

# Criar JAR
ant jar

# Executar servidor
ant run

# Executar servidor em background
ant start

# Parar servidor
ant stop

# Limpar arquivos gerados
ant clean
```

## Banco de Dados

O banco de dados SQLite é criado automaticamente pela aplicação desktop. Para criar manualmente ou em outro sistema:

```bash
# Criar banco usando o script SQL
sqlite3 finanza.db < database/finanza.sql

# Ou usar qualquer cliente SQLite compatível
```

### Usuário Padrão

O script SQL cria um usuário padrão para testes:
- **Email:** admin@finanza.com
- **Senha:** admin

## Dependências

### Desktop Application
- SQLite JDBC 3.42.0.0
- Gson 2.10.1
- Logback Classic 1.4.7
- Logback Core 1.4.7
- SLF4J API 2.0.7
- Apache Commons Lang3 3.12.0

### Server
- Nenhuma dependência externa (usa apenas JDK padrão)

## Funcionalidades

### Desktop Application
- Sistema de login/registro
- Gerenciamento de contas financeiras
- Lançamentos (receitas e despesas)
- Categorização de transações
- Dashboard com resumos
- Interface gráfica intuitiva

### Server
- Servidor TCP na porta 8080
- Suporte a múltiplas conexões simultâneas
- Processamento de comandos JSON
- Threading para performance

### Controllers (Padrão Android)
- `UsuarioController` - Operações de usuário
- `ContaController` - Gerenciamento de contas
- `LancamentoController` - Transações financeiras
- `CategoriaController` - Categorias de lançamentos

## Troubleshooting

### Problemas Comuns

1. **Erro de dependências**
   ```bash
   ant clean-all
   ant dependencies
   ```

2. **Problemas de compilação**
   ```bash
   ant clean
   ant compile
   ```

3. **Servidor não inicia**
   - Verificar se porta 8080 está livre
   - Verificar se Java está instalado corretamente

4. **Aplicação desktop não abre**
   - Verificar se há ambiente gráfico disponível
   - Verificar logs de erro

### Logs

Os logs da aplicação são salvos automaticamente e podem ser encontrados:
- Console durante execução
- Arquivo de log (se configurado)

## Desenvolvimento

Para desenvolver e modificar o código:

1. Editar arquivos em `src/main/java`
2. Recompilar: `ant compile`
3. Testar: `ant run`
4. Criar distribuição: `ant jar`

## Estrutura MVC/Android Pattern

O projeto segue padrões similares ao desenvolvimento Android:

- **Model**: Classes de dados em `model/`
- **View**: Interfaces gráficas em `ui/`
- **Controller**: Lógica de negócio em `controller/`
- **DAO**: Acesso a dados em `database/`

## Licença

Este projeto é desenvolvido para fins educacionais e demonstração de conceitos de desenvolvimento Java com Ant.