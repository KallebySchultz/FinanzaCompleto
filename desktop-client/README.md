# 🏦 Finanza Desktop Client - Sistema de Gestão Financeira

## 📋 Visão Geral

O **Finanza Desktop Client** é uma aplicação de gestão financeira completa desenvolvida em Java com interface gráfica Swing. Implementa uma arquitetura MVC profissional com banco de dados SQLite integrado.

## 🏗️ Arquitetura MVC Completa

### 📊 **Model (Modelo)**
- `Usuario.java` - Entidade de usuário do sistema
- `Conta.java` - Contas bancárias do usuário
- `Lancamento.java` - Transações financeiras (receitas e despesas)
- `Categoria.java` - Categorias para classificação dos lançamentos

### 🎨 **View (Visão)**
- `FinanzaDesktop.java` - Interface principal com Swing
- `ModernUIHelper.java` - Componentes UI modernos e estilizados
- Sistema de login/registro com abas
- Dashboard dinâmico com dados reais
- Navegação entre telas

### 🎛️ **Controller (Controlador)**
- `AuthController.java` - Gerencia autenticação e usuários
- `FinanceController.java` - Gerencia operações financeiras

### 💾 **DAO (Data Access Object)**
- `DatabaseManager.java` - Gerencia conexões SQLite
- `UsuarioDAO.java` - CRUD de usuários
- `ContaDAO.java` - CRUD de contas
- `CategoriaDAO.java` - CRUD de categorias
- `LancamentoDAO.java` - CRUD de lançamentos

## 🔧 Funcionalidades Implementadas

### 🔐 **Autenticação Segura**
- ✅ Registro de usuários com validação
- ✅ Login com email e senha
- ✅ Criptografia BCrypt para senhas
- ✅ Validação de email e senha forte
- ✅ Sistema de logout

### 💳 **Gestão de Contas**
- ✅ Criação de contas bancárias
- ✅ Cálculo automático de saldos
- ✅ Listagem e gerenciamento

### 📊 **Transações Financeiras**
- ✅ Lançamento de receitas e despesas
- ✅ Categorização automática
- ✅ Cálculos em tempo real
- ✅ Histórico completo

### 📈 **Relatórios e Dashboard**
- ✅ Dashboard com resumo financeiro
- ✅ Saldo total calculado automaticamente
- ✅ Últimas transações do mês
- ✅ Indicadores visuais

### 🎨 **Interface Moderna**
- ✅ Design baseado no app móvel
- ✅ Ícones Unicode modernos
- ✅ Cores tema profissionais
- ✅ Componentes estilizados

## 🚀 Como Executar

### 📋 Pré-requisitos
- Java 8 ou superior
- Maven 3.6+

### 💻 Executar a Aplicação
```bash
cd desktop-client
mvn compile exec:java -Dexec.mainClass="com.finanza.desktop.FinanzaDesktop"
```

### 🧪 Testar o Sistema
```bash
cd desktop-client
mvn compile
java -cp "target/classes:$(find ~/.m2/repository -name "*.jar" | tr '\n' ':')" com.finanza.desktop.test.DatabaseTest
```

## 📦 Dependências

```xml
<!-- SQLite Database -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.41.2.2</version>
</dependency>

<!-- JSON Processing -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>

<!-- Password Encryption -->
<dependency>
    <groupId>org.mindrot</groupId>
    <artifactId>jbcrypt</artifactId>
    <version>0.4</version>
</dependency>
```

## 💾 Banco de Dados

### 🗃️ **SQLite Automático**
- Criação automática do esquema
- Arquivo `finanza.db` gerado automaticamente
- Tabelas com foreign keys e constraints
- Categorias padrão inseridas automaticamente

### 📋 **Categorias Padrão**

**Receitas:**
- 💰 Salário
- 💼 Freelance
- 📈 Investimentos
- 💎 Outros Ganhos

**Despesas:**
- 🍔 Alimentação
- 🚗 Transporte
- 🏠 Moradia
- 🏥 Saúde
- 📚 Educação
- 🎮 Lazer
- 🛒 Compras
- 💸 Outras Despesas

## 🎯 Características Técnicas

### 🔒 **Segurança**
- Senhas criptografadas com BCrypt
- Validação de entrada de dados
- SQL injection prevention com PreparedStatements

### 🏗️ **Padrões de Design**
- MVC (Model-View-Controller)
- DAO (Data Access Object)
- Singleton para managers
- Factory para componentes UI

### 🎨 **Interface**
- Swing com look moderno
- CardLayout para navegação
- Componentes personalizados
- Ícones Unicode
- Tema de cores consistente

## 📱 Integração com Mobile

O sistema está preparado para sincronização com o app móvel através de:
- Estrutura de dados compatível
- JSON para transferência
- NetworkManager para comunicação
- Mesma modelagem de dados

## 🔮 Próximos Passos

- [ ] Implementar gráficos com JFreeChart
- [ ] Adicionar sincronização completa com mobile
- [ ] Implementar backup/restore
- [ ] Adicionar filtros avançados
- [ ] Implementar importação/exportação
- [ ] Adicionar notificações

## 🏆 Status Atual

**✅ SISTEMA TOTALMENTE FUNCIONAL**

O desktop client está completo com:
- ✅ Arquitetura MVC profissional
- ✅ Banco de dados SQLite integrado
- ✅ Autenticação segura com BCrypt
- ✅ CRUD completo para todas entidades
- ✅ Interface moderna e responsiva
- ✅ Cálculos financeiros em tempo real
- ✅ Sistema testado e validado

**🎯 Pronto para uso em produção!**