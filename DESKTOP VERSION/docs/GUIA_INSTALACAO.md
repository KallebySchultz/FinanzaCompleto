# Guia de Configuração e Uso - Finanza Desktop

## Pré-requisitos

1. **Java 8 ou superior**
   ```bash
   java -version
   javac -version
   ```

2. **MySQL Server**
   - MySQL 5.7 ou superior
   - Servidor rodando na porta padrão 3306

3. **Driver JDBC MySQL** (opcional - para funcionalidades avançadas)
   - Download: https://dev.mysql.com/downloads/connector/j/

## Configuração Inicial

### 1. Configurar Banco de Dados

1. Instale e inicie o MySQL Server
2. Crie o banco de dados executando o script:
   ```bash
   mysql -u root -p < banco/script_inicial.sql
   ```

   Ou execute manualmente no MySQL:
   ```sql
   CREATE DATABASE finanza_db;
   USE finanza_db;
   # Execute o restante do script banco/script_inicial.sql
   ```

### 2. Configurar Conexão (se necessário)

Se suas configurações do MySQL forem diferentes das padrão, edite o arquivo:
`ServidorFinanza/util/DatabaseUtil.java`

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/finanza_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "";
```

## Como Executar

### Método 1: Scripts Automatizados

1. **Iniciar Servidor:**
   ```bash
   ./run_server.sh
   ```

2. **Iniciar Cliente (em outro terminal):**
   ```bash
   ./run_client.sh
   ```

### Método 2: Compilação Manual

1. **Compilar e executar Servidor:**
   ```bash
   cd ServidorFinanza
   javac -cp . model/*.java util/*.java dao/*.java server/*.java MainServidor.java
   java -cp . MainServidor
   ```

2. **Compilar e executar Cliente:**
   ```bash
   cd ClienteFinanza
   javac -cp . model/*.java util/*.java controller/*.java view/*.java MainCliente.java
   java -cp . MainCliente
   ```

## Uso do Sistema

### 1. Primeiro Acesso

1. Execute o servidor primeiro
2. Execute o cliente
3. Na tela de login, clique em "Não tem conta? Cadastre-se"
4. Preencha os dados:
   - Nome completo
   - Email válido
   - Senha (mínimo 6 caracteres)
5. Clique em "Cadastrar"

### 2. Login

1. Digite seu email e senha
2. Clique em "Entrar"
3. Você será redirecionado para o dashboard

### 3. Dashboard

O dashboard exibe:
- Saldo total
- Receitas do mês
- Despesas do mês
- Número de transações

### 4. Funcionalidades Disponíveis

**Implementado:**
- ✅ Cadastro de usuário
- ✅ Login/Logout
- ✅ Dashboard básico
- ✅ Comunicação cliente-servidor

**Em desenvolvimento:**
- 🔄 Gerenciamento de contas
- 🔄 Gerenciamento de categorias
- 🔄 Movimentações financeiras
- 🔄 Relatórios
- 🔄 Exportação de dados

## Solução de Problemas

### Erro: "Não foi possível conectar ao banco de dados"

1. Verifique se o MySQL está rodando:
   ```bash
   sudo systemctl status mysql
   # ou
   brew services list | grep mysql
   ```

2. Verifique se o banco `finanza_db` existe:
   ```sql
   SHOW DATABASES;
   ```

3. Execute o script de criação se necessário:
   ```bash
   mysql -u root -p < banco/script_inicial.sql
   ```

### Erro: "Erro ao conectar ao servidor"

1. Verifique se o servidor está rodando na porta 8080
2. Verifique se não há firewall bloqueando a porta
3. Tente reiniciar o servidor

### Problemas de Compilação

1. Verifique a versão do Java:
   ```bash
   java -version
   javac -version
   ```

2. Certifique-se de estar no diretório correto
3. Verifique se todos os arquivos .java estão presentes

## Estrutura de Arquivos

```
Finanza/
├── ClienteFinanza/              # Aplicação cliente
│   ├── controller/              # Lógica de controle
│   ├── model/                   # Modelos de dados
│   ├── view/                    # Interface gráfica
│   ├── util/                    # Utilitários
│   └── MainCliente.java         # Classe principal
├── ServidorFinanza/             # Aplicação servidor
│   ├── dao/                     # Acesso a dados
│   ├── model/                   # Modelos de dados
│   ├── server/                  # Lógica do servidor
│   ├── util/                    # Utilitários
│   └── MainServidor.java        # Classe principal
├── banco/                       # Scripts do banco
│   └── script_inicial.sql       # Criação das tabelas
├── docs/                        # Documentação
├── .gitignore                   # Arquivos ignorados
├── README.md                    # Documentação principal
├── run_client.sh                # Script do cliente
└── run_server.sh                # Script do servidor
```

## Próximos Desenvolvimentos

1. **Contas**: Criar, editar e excluir contas bancárias
2. **Categorias**: Gerenciar categorias de receitas e despesas
3. **Movimentações**: CRUD completo de transações financeiras
4. **Relatórios**: Gráficos e relatórios por período
5. **Exportação**: Exportar dados em CSV, Excel e PDF
6. **Perfil**: Editar dados do usuário e alterar senha

## Contribuição

Para contribuir com o projeto:

1. Fork o repositório
2. Crie uma branch para sua feature
3. Faça commit das mudanças
4. Abra um Pull Request

## Suporte

Para dúvidas ou problemas:
- Abra uma issue no GitHub
- Consulte a documentação completa
- Verifique os logs do servidor para debugging