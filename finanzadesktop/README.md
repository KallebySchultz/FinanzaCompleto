# Finanza Desktop

Sistema de Gestão Financeira Pessoal desenvolvido em Java com interface Swing.

## 📋 Funcionalidades

### ✅ Implementadas
- **Autenticação Completa**
  - Login e cadastro de usuários
  - Validação de email e senha
  - Gestão de perfil do usuário

- **Dashboard Financeiro**
  - Resumo de saldo, receitas e despesas
  - Visualização de transações recentes
  - Lista de contas com saldos atuais
  - Opção de ocultar valores sensíveis

- **Gestão de Contas**
  - Criar, editar e excluir contas
  - Visualização de saldo atual calculado
  - Proteção contra exclusão de contas com transações

- **Controle de Transações**
  - Adicionar receitas e despesas
  - Editar e excluir transações
  - Filtros por período (mês/ano)
  - Resumo financeiro por período
  - Categorização automática

- **Gestão de Categorias**
  - Categorias padrão pré-configuradas
  - Criar novas categorias personalizadas
  - Separação entre receitas e despesas

- **Configurações e Ferramentas**
  - Atualização de perfil do usuário
  - Exportação de dados em relatório
  - Interface sobre o sistema

### 🎨 Interface
- Design moderno e profissional
- Cores consistentes com identidade visual
- Layout responsivo e intuitivo
- Feedback visual para ações do usuário
- Validações em tempo real

## 🛠️ Tecnologias

- **Java 11+** - Linguagem de programação
- **Swing** - Interface gráfica
- **SQLite** - Banco de dados local
- **Maven** - Gerenciamento de dependências
- **JDBC** - Conexão com banco de dados

## 📦 Dependências

- `sqlite-jdbc` - Driver SQLite
- `gson` - Processamento JSON
- `logback` - Sistema de logging
- `commons-lang3` - Utilitários Java

## 🚀 Como executar

### Pré-requisitos
- Java 11 ou superior instalado
- Maven 3.6+ (para compilação)

### Compilação
```bash
cd finanzadesktop
mvn clean package
```

### Execução
```bash
# Usando o script fornecido (Linux/Mac)
./run.sh

# Ou diretamente
java -jar target/finanza-desktop-1.0.0.jar
```

## 📊 Banco de Dados

O sistema utiliza SQLite com as seguintes tabelas:

- **usuarios** - Dados dos usuários
- **contas** - Contas bancárias/financeiras
- **categorias** - Categorias de transações
- **lancamentos** - Transações financeiras

O banco é criado automaticamente na primeira execução com categorias padrão pré-configuradas.

## 📁 Estrutura do Projeto

```
finanzadesktop/
├── src/main/java/com/finanza/desktop/
│   ├── model/          # Modelos de dados
│   ├── database/       # DAOs e conexão
│   ├── ui/            # Interfaces gráficas
│   ├── util/          # Classes utilitárias
│   └── FinanzaDesktopApp.java
├── pom.xml            # Configuração Maven
├── run.sh             # Script de execução
└── README.md          # Este arquivo
```

## 🔒 Segurança

- Senhas não são criptografadas (versão de desenvolvimento)
- Banco de dados local sem conexão externa
- Validações de entrada em todos os formulários

## 📈 Funcionalidades Avançadas

### Relatórios
- Exportação completa de dados
- Resumo financeiro detalhado
- Lista de todas as transações
- Saldo atual de todas as contas

### Validações
- Email válido obrigatório
- Senha mínima de 6 caracteres
- Valores numéricos validados
- Prevenção de dados duplicados

### Performance
- Conexão de banco otimizada
- Queries eficientes com índices
- Interface responsiva
- Carregamento rápido de dados

## 🐛 Tratamento de Erros

- Mensagens de erro amigáveis
- Logs detalhados para debugging
- Validação preventiva de dados
- Recuperação automática de falhas

## 📱 Usabilidade

- Interface intuitiva e moderna
- Navegação clara entre telas
- Feedback visual para todas as ações
- Confirmações para ações destrutivas
- Atalhos de teclado (Enter para confirmar)

## 🔄 Atualizações Futuras

O sistema está preparado para receber:
- Sincronização com servidor
- Gráficos e relatórios visuais
- Importação de dados bancários
- Backup automático
- Temas personalizáveis

## 📞 Suporte

Para dúvidas ou problemas:
1. Verifique os logs na console
2. Confirme os pré-requisitos
3. Teste com dados limpos
4. Reporte bugs com detalhes

---

**Finanza Desktop v1.0.0** - Sistema profissional de gestão financeira pessoal.