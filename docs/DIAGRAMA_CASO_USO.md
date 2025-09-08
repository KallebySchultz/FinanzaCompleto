# 6. DIAGRAMA DE CASO DE USO - FINANZA

## 📋 Visão Geral

O diagrama de casos de uso do sistema Finanza apresenta as principais funcionalidades disponíveis para cada tipo de ator, demonstrando as interações entre usuários, administradores e o sistema integrado mobile-desktop.

---

## 🎭 Atores do Sistema

### **👤 Usuário Mobile**
- Pessoa física que utiliza o aplicativo Android
- Acesso a funcionalidades pessoais de controle financeiro
- Sincronização automática com servidor

### **🖥️ Usuário Desktop** 
- Administrador ou usuário avançado
- Acesso via cliente Java desktop
- Funcionalidades administrativas e relatórios avançados

### **⚙️ Sistema de Sincronização**
- Ator automático responsável pela sincronização
- Gerencia conflitos e consistência de dados
- Monitora integridade entre plataformas

---

## 🎯 Diagrama de Casos de Uso

```mermaid
graph TB
    %% Atores
    UM[👤 Usuário Mobile]
    UD[🖥️ Usuário Desktop]
    SS[⚙️ Sistema Sincronização]
    
    %% Sistema principal
    subgraph "Sistema Finanza"
        %% Autenticação
        UC1[UC01: Fazer Login]
        UC2[UC02: Criar Conta]
        UC3[UC03: Recuperar Senha]
        
        %% Gestão Financeira
        UC4[UC04: Gerenciar Transações]
        UC5[UC05: Gerenciar Contas]
        UC6[UC06: Gerenciar Categorias]
        UC7[UC07: Visualizar Dashboard]
        
        %% Mobile Específico
        UC8[UC08: Sincronizar Dados]
        UC9[UC09: Trabalhar Offline]
        UC10[UC10: Receber Notificações]
        
        %% Desktop Específico
        UC11[UC11: Administrar Usuários]
        UC12[UC12: Gerar Relatórios]
        UC13[UC13: Exportar Dados]
        UC14[UC14: Monitorar Sistema]
        UC15[UC15: Configurar Servidor]
        
        %% Sincronização
        UC16[UC16: Sincronizar Automaticamente]
        UC17[UC17: Resolver Conflitos]
        UC18[UC18: Validar Integridade]
    end
    
    %% Relacionamentos Usuário Mobile
    UM --> UC1
    UM --> UC2
    UM --> UC3
    UM --> UC4
    UM --> UC5
    UM --> UC6
    UM --> UC7
    UM --> UC8
    UM --> UC9
    UM --> UC10
    
    %% Relacionamentos Usuário Desktop
    UD --> UC1
    UD --> UC2
    UD --> UC3
    UD --> UC4
    UD --> UC5
    UD --> UC6
    UD --> UC7
    UD --> UC11
    UD --> UC12
    UD --> UC13
    UD --> UC14
    UD --> UC15
    
    %% Relacionamentos Sistema Sincronização
    SS --> UC16
    SS --> UC17
    SS --> UC18
    
    %% Extensões e Inclusões
    UC4 -.-> UC5 : includes
    UC4 -.-> UC6 : includes
    UC8 -.-> UC16 : extends
    UC17 -.-> UC18 : includes
    UC12 -.-> UC4 : includes
    UC13 -.-> UC12 : extends
    
    %% Styling
    classDef actor fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef usecase fill:#f3e5f5,stroke:#4a148c,stroke-width:1px
    classDef system fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    
    class UM,UD,SS actor
    class UC1,UC2,UC3,UC4,UC5,UC6,UC7,UC8,UC9,UC10,UC11,UC12,UC13,UC14,UC15,UC16,UC17,UC18 usecase
```

---

## 📋 Especificação dos Casos de Uso

### **🔐 Módulo de Autenticação**

#### **UC01: Fazer Login**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** Autenticação no sistema com email/senha
- **Fluxo Principal:**
  1. Usuário informa email e senha
  2. Sistema valida credenciais
  3. Sistema autentica usuário
  4. Sistema redireciona para dashboard
- **Pré-condições:** Usuário deve ter conta criada
- **Pós-condições:** Usuário autenticado no sistema

#### **UC02: Criar Conta**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** Registro de novo usuário no sistema
- **Fluxo Principal:**
  1. Usuário informa dados pessoais
  2. Sistema valida informações
  3. Sistema cria conta
  4. Sistema envia confirmação
- **Pré-condições:** Email não deve estar em uso
- **Pós-condições:** Nova conta criada e ativa

#### **UC03: Recuperar Senha**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** Recuperação de senha via email
- **Fluxo Principal:**
  1. Usuário solicita recuperação
  2. Sistema envia link por email
  3. Usuário define nova senha
  4. Sistema atualiza credenciais
- **Pré-condições:** Email deve existir no sistema
- **Pós-condições:** Senha alterada com sucesso

---

### **💰 Módulo de Gestão Financeira**

#### **UC04: Gerenciar Transações**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** CRUD completo de transações financeiras
- **Fluxo Principal:**
  1. Usuário acessa área de transações
  2. Usuário escolhe ação (criar/editar/excluir)
  3. Sistema processa operação
  4. Sistema atualiza dados
- **Inclui:** UC05 (Gerenciar Contas), UC06 (Gerenciar Categorias)
- **Pré-condições:** Usuário autenticado
- **Pós-condições:** Transação processada

#### **UC05: Gerenciar Contas**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** Gestão de contas bancárias e carteiras
- **Fluxo Principal:**
  1. Usuário acessa gestão de contas
  2. Usuário gerencia contas (CRUD)
  3. Sistema valida operações
  4. Sistema atualiza saldos
- **Pré-condições:** Usuário autenticado
- **Pós-condições:** Contas atualizadas

#### **UC06: Gerenciar Categorias**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** Organização de categorias de receitas/despesas
- **Fluxo Principal:**
  1. Usuário acessa categorias
  2. Usuário gerencia categorias (CRUD)
  3. Sistema organiza transações
  4. Sistema atualiza relatórios
- **Pré-condições:** Usuário autenticado
- **Pós-condições:** Categorias organizadas

#### **UC07: Visualizar Dashboard**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** Visualização de resumo financeiro
- **Fluxo Principal:**
  1. Sistema carrega dados do usuário
  2. Sistema calcula métricas
  3. Sistema exibe gráficos
  4. Sistema apresenta dashboard
- **Pré-condições:** Usuário autenticado
- **Pós-condições:** Dashboard exibido

---

### **📱 Módulo Mobile Específico**

#### **UC08: Sincronizar Dados**
- **Ator:** Usuário Mobile
- **Descrição:** Sincronização manual com servidor
- **Fluxo Principal:**
  1. Usuário solicita sincronização
  2. Sistema verifica conectividade
  3. Sistema envia dados locais
  4. Sistema recebe dados do servidor
- **Estende:** UC16 (Sincronizar Automaticamente)
- **Pré-condições:** Conectividade disponível
- **Pós-condições:** Dados sincronizados

#### **UC09: Trabalhar Offline**
- **Ator:** Usuário Mobile
- **Descrição:** Funcionamento sem conexão
- **Fluxo Principal:**
  1. Sistema detecta falta de conexão
  2. Sistema ativa modo offline
  3. Usuário opera normalmente
  4. Sistema agenda sincronização
- **Pré-condições:** Dados locais disponíveis
- **Pós-condições:** Operações salvas localmente

#### **UC10: Receber Notificações**
- **Ator:** Usuário Mobile
- **Descrição:** Notificações de sincronização e alertas
- **Fluxo Principal:**
  1. Sistema gera evento
  2. Sistema verifica configurações
  3. Sistema envia notificação
  4. Usuário visualiza alerta
- **Pré-condições:** Notificações habilitadas
- **Pós-condições:** Usuário informado

---

### **🖥️ Módulo Desktop Específico**

#### **UC11: Administrar Usuários**
- **Ator:** Usuário Desktop
- **Descrição:** Gestão administrativa de usuários
- **Fluxo Principal:**
  1. Admin acessa painel de usuários
  2. Admin visualiza lista completa
  3. Admin executa ações administrativas
  4. Sistema atualiza dados
- **Pré-condições:** Permissões administrativas
- **Pós-condições:** Usuários gerenciados

#### **UC12: Gerar Relatórios**
- **Ator:** Usuário Desktop
- **Descrição:** Criação de relatórios financeiros
- **Fluxo Principal:**
  1. Usuário define parâmetros
  2. Sistema consulta dados
  3. Sistema processa informações
  4. Sistema gera relatório
- **Inclui:** UC04 (Gerenciar Transações)
- **Pré-condições:** Dados disponíveis
- **Pós-condições:** Relatório gerado

#### **UC13: Exportar Dados**
- **Ator:** Usuário Desktop
- **Descrição:** Exportação de dados em vários formatos
- **Fluxo Principal:**
  1. Usuário escolhe formato de exportação
  2. Sistema seleciona dados
  3. Sistema converte formato
  4. Sistema disponibiliza arquivo
- **Estende:** UC12 (Gerar Relatórios)
- **Pré-condições:** Relatório disponível
- **Pós-condições:** Dados exportados

#### **UC14: Monitorar Sistema**
- **Ator:** Usuário Desktop
- **Descrição:** Monitoramento de performance e status
- **Fluxo Principal:**
  1. Sistema coleta métricas
  2. Sistema analisa performance
  3. Sistema exibe status
  4. Admin monitora indicadores
- **Pré-condições:** Sistema em execução
- **Pós-condições:** Status monitorado

#### **UC15: Configurar Servidor**
- **Ator:** Usuário Desktop
- **Descrição:** Configurações administrativas do servidor
- **Fluxo Principal:**
  1. Admin acessa configurações
  2. Admin modifica parâmetros
  3. Sistema valida alterações
  4. Sistema aplica configurações
- **Pré-condições:** Permissões administrativas
- **Pós-condições:** Sistema configurado

---

### **🔄 Módulo de Sincronização**

#### **UC16: Sincronizar Automaticamente**
- **Ator:** Sistema de Sincronização
- **Descrição:** Sincronização automática periódica
- **Fluxo Principal:**
  1. Sistema verifica agendamento
  2. Sistema identifica mudanças
  3. Sistema executa sincronização
  4. Sistema atualiza timestamps
- **Pré-condições:** Agendamento ativo
- **Pós-condições:** Dados sincronizados

#### **UC17: Resolver Conflitos**
- **Ator:** Sistema de Sincronização
- **Descrição:** Resolução automática de conflitos
- **Fluxo Principal:**
  1. Sistema detecta conflito
  2. Sistema analisa timestamps
  3. Sistema aplica regras de resolução
  4. Sistema notifica resultado
- **Inclui:** UC18 (Validar Integridade)
- **Pré-condições:** Conflito detectado
- **Pós-condições:** Conflito resolvido

#### **UC18: Validar Integridade**
- **Ator:** Sistema de Sincronização
- **Descrição:** Verificação de integridade dos dados
- **Fluxo Principal:**
  1. Sistema verifica checksums
  2. Sistema valida relacionamentos
  3. Sistema detecta inconsistências
  4. Sistema corrige automaticamente
- **Pré-condições:** Dados disponíveis
- **Pós-condições:** Integridade verificada

---

## 🔗 Relacionamentos entre Casos de Uso

### **Includes (Inclusão):**
- UC04 → UC05: Gerenciar transações requer gestão de contas
- UC04 → UC06: Gerenciar transações requer gestão de categorias
- UC12 → UC04: Gerar relatórios inclui consulta de transações
- UC17 → UC18: Resolver conflitos inclui validação de integridade

### **Extends (Extensão):**
- UC08 → UC16: Sincronização manual estende automática
- UC13 → UC12: Exportação estende geração de relatórios

### **Generalização:**
- Usuários mobile e desktop compartilham casos básicos
- Funcionalidades específicas para cada plataforma

---

## 📊 Métricas dos Casos de Uso

### **Por Ator:**
- **Usuário Mobile:** 10 casos de uso
- **Usuário Desktop:** 13 casos de uso
- **Sistema Sincronização:** 3 casos de uso

### **Por Módulo:**
- **Autenticação:** 3 casos de uso
- **Gestão Financeira:** 4 casos de uso
- **Mobile Específico:** 3 casos de uso
- **Desktop Específico:** 5 casos de uso
- **Sincronização:** 3 casos de uso

### **Complexidade:**
- **Simples:** 8 casos de uso
- **Médio:** 7 casos de uso
- **Complexo:** 3 casos de uso

---

**Legenda do Diagrama:**
- **👤 Atores:** Representam os usuários e sistemas externos
- **Elipses:** Casos de uso funcionais do sistema
- **Setas sólidas:** Associações diretas entre ator e caso de uso
- **Setas tracejadas:** Relacionamentos include/extend entre casos de uso
- **Retângulo:** Fronteira do sistema Finanza

**Observações:**
- O diagrama segue padrão UML 2.0
- Casos de uso cobrem 100% das funcionalidades principais
- Relacionamentos garantem reusabilidade e manutenibilidade
- Separação clara entre funcionalidades mobile e desktop

---

*Diagrama criado seguindo padrões UML 2.0*  
*Versão: 1.0 | Data: Dezembro 2024*  
*Ferramenta: Mermaid + Análise de Requisitos*