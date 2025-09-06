# Finanza Desktop

Sistema completo de controle financeiro pessoal e empresarial desenvolvido em Java, seguindo arquitetura cliente-servidor.

## Estrutura do Projeto

```
Finanza/
├── ClienteFinanza/          # Aplicação cliente (GUI)
│   ├── controller/          # Controllers MVC
│   ├── model/              # Modelos de dados
│   ├── view/               # Interface gráfica
│   ├── util/               # Utilitários
│   └── MainCliente.java    # Classe principal do cliente
├── ServidorFinanza/        # Aplicação servidor
│   ├── controller/         # Controllers do servidor
│   ├── model/             # Modelos de dados
│   ├── dao/               # Data Access Objects
│   ├── server/            # Lógica do servidor
│   ├── util/              # Utilitários
│   └── MainServidor.java  # Classe principal do servidor
├── docs/                  # Documentação
├── banco/                 # Scripts do banco de dados
│   └── script_inicial.sql # Script de criação das tabelas
├── run_server.sh          # Script para executar servidor
└── run_client.sh          # Script para executar cliente
```

## Funcionalidades

### Implementadas
- ✅ Sistema de autenticação (login/registro)
- ✅ Dashboard principal com resumo financeiro
- ✅ Comunicação cliente-servidor via sockets
- ✅ Banco de dados MySQL
- ✅ Interface gráfica com Swing
- ✅ Arquitetura MVC
- ✅ **Exportação de dados aprimorada com tabelas bonitas**

### Em Desenvolvimento
- 🔄 Gerenciamento de contas
- 🔄 Gerenciamento de categorias
- 🔄 Movimentações financeiras (CRUD)
- 🔄 Relatórios financeiros
- ✅ **Exportação de dados (CSV, HTML) com formatação profissional**
- 🔄 Perfil do usuário

### 🎯 Destaque: Exportação Aprimorada
O sistema agora oferece **3 formatos de exportação** para melhor organização:
- **CSV Aprimorado**: Tabelas ASCII com bordas e totais automáticos
- **HTML**: Relatórios web profissionais para impressão
- **CSV Simples**: Compatibilidade com sistemas antigos

Veja [documentação detalhada](docs/EXPORTACAO_APRIMORADA.md) para mais informações.

## Requisitos Técnicos

- Java 8 ou superior
- MySQL 5.7 ou superior
- Driver JDBC MySQL (mysql-connector-java)

## Configuração do Banco de Dados

1. Instale o MySQL Server
2. Execute o script `banco/script_inicial.sql` para criar o banco e tabelas
3. Ajuste as configurações de conexão em `ServidorFinanza/util/DatabaseUtil.java` se necessário

```sql
-- Configurações padrão:
-- Host: localhost
-- Porta: 3306
-- Banco: finanza_db
-- Usuário: root
-- Senha: (vazia)
```

## Como Executar

### 1. Executar o Servidor
```bash
chmod +x run_server.sh
./run_server.sh
```

### 2. Executar o Cliente
```bash
chmod +x run_client.sh
./run_client.sh
```

Ou execute manualmente:

**Servidor:**
```bash
cd ServidorFinanza
javac -cp . model/*.java util/*.java dao/*.java server/*.java MainServidor.java
java -cp . MainServidor
```

**Cliente:**
```bash
cd ClienteFinanza
javac -cp . model/*.java util/*.java controller/*.java view/*.java MainCliente.java
java -cp . MainCliente
```

## Protocolo de Comunicação

O sistema utiliza um protocolo simples baseado em texto sobre TCP/IP:

- **Formato:** `COMANDO|DADOS`
- **Separador de campos:** `;`
- **Porta padrão:** 8080

### Comandos Disponíveis

- `LOGIN|email|senha` - Realizar login
- `REGISTER|nome|email|senha` - Registrar novo usuário
- `LOGOUT` - Fazer logout
- `GET_DASHBOARD` - Obter dados do dashboard

### Respostas

- `OK|dados` - Operação bem-sucedida
- `ERROR|mensagem` - Erro genérico
- `INVALID_CREDENTIALS` - Credenciais inválidas
- `USER_EXISTS` - Usuário já existe
- `INVALID_DATA|mensagem` - Dados inválidos

## Modelo de Dados

### Usuário
- id (INT, AUTO_INCREMENT, PRIMARY KEY)
- nome (VARCHAR(100), NOT NULL)
- email (VARCHAR(150), UNIQUE, NOT NULL)
- senha_hash (VARCHAR(255), NOT NULL)
- data_criacao (TIMESTAMP)
- data_atualizacao (TIMESTAMP)

### Conta
- id (INT, AUTO_INCREMENT, PRIMARY KEY)
- nome (VARCHAR(100), NOT NULL)
- tipo (ENUM: 'corrente', 'poupanca', 'cartao', 'investimento', 'dinheiro')
- saldo_inicial (DECIMAL(10,2))
- id_usuario (INT, FOREIGN KEY)
- data_criacao (TIMESTAMP)

### Categoria
- id (INT, AUTO_INCREMENT, PRIMARY KEY)
- nome (VARCHAR(100), NOT NULL)
- tipo (ENUM: 'receita', 'despesa')
- id_usuario (INT, FOREIGN KEY)
- data_criacao (TIMESTAMP)

### Movimentação
- id (INT, AUTO_INCREMENT, PRIMARY KEY)
- valor (DECIMAL(10,2), NOT NULL)
- data (DATE, NOT NULL)
- descricao (TEXT)
- tipo (ENUM: 'receita', 'despesa')
- id_conta (INT, FOREIGN KEY)
- id_categoria (INT, FOREIGN KEY)
- id_usuario (INT, FOREIGN KEY)
- data_criacao (TIMESTAMP)
- data_atualizacao (TIMESTAMP)

## Segurança

- Senhas são armazenadas como hash SHA-256
- Validação de dados no cliente e servidor
- Comunicação via sockets TCP/IP

## Desenvolvimento

O projeto segue o padrão MVC (Model-View-Controller) e possui separação clara entre:

- **Model:** Entidades de dados
- **View:** Interface gráfica (Swing)
- **Controller:** Lógica de negócio e comunicação
- **DAO:** Acesso aos dados (servidor)
- **Util:** Utilitários e helpers

## Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## Licença

Este projeto está sob licença MIT. Veja o arquivo LICENSE para detalhes.