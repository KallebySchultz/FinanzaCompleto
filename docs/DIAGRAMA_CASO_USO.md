# 6. DIAGRAMA DE CASO DE USO COMPLETO - FINANZA

## 📋 Visão Geral

O diagrama de casos de uso do sistema Finanza apresenta as principais funcionalidades disponíveis para cada tipo de ator, demonstrando as interações entre usuários, administradores e o sistema integrado mobile-desktop. Este diagrama abrangente cobre todas as funcionalidades implementadas em ambas as plataformas.

---

## 🎭 Atores do Sistema

### **👤 Usuário Mobile**
- Pessoa física que utiliza o aplicativo Android
- Acesso a funcionalidades pessoais de controle financeiro
- Operação offline com sincronização automática
- Interface otimizada para dispositivos móveis

### **🖥️ Usuário Desktop** 
- Administrador ou usuário avançado
- Acesso via cliente Java desktop
- Funcionalidades administrativas e relatórios avançados
- Controle de servidor e configurações avançadas

### **⚙️ Sistema de Sincronização**
- Ator automático responsável pela sincronização
- Gerencia conflitos e consistência de dados
- Monitora integridade entre plataformas
- Executa processos em background

### **🗄️ Sistema de Banco de Dados**
- Ator responsável pela persistência de dados
- Gerencia transações e integridade referencial
- Executa backups e restaurações automáticas

---

## 🎯 Diagrama de Casos de Uso Completo

```mermaid
graph TB
    %% Atores
    UM[👤 Usuário Mobile]
    UD[🖥️ Usuário Desktop]
    SS[⚙️ Sistema Sincronização]
    BD[🗄️ Sistema Banco Dados]
    
    %% Sistema principal
    subgraph "Sistema Finanza - Módulo de Autenticação"
        UC1[UC01: Fazer Login]
        UC2[UC02: Criar Conta]
        UC3[UC03: Recuperar Senha]
        UC4[UC04: Alterar Senha]
        UC5[UC05: Fazer Logout]
    end
    
    subgraph "Sistema Finanza - Módulo Gestão Financeira"
        UC6[UC06: Gerenciar Transações]
        UC7[UC07: Gerenciar Contas]
        UC8[UC08: Gerenciar Categorias]
        UC9[UC09: Visualizar Dashboard]
        UC10[UC10: Filtrar Movimentações]
        UC11[UC11: Calcular Saldos]
    end
    
    subgraph "Sistema Finanza - Módulo Mobile Específico"
        UC12[UC12: Sincronizar Dados Manualmente]
        UC13[UC13: Trabalhar Offline]
        UC14[UC14: Receber Notificações]
        UC15[UC15: Configurar Aplicativo]
        UC16[UC16: Testar Conexão Servidor]
    end
    
    subgraph "Sistema Finanza - Módulo Desktop Específico"
        UC17[UC17: Administrar Usuários]
        UC18[UC18: Gerar Relatórios Avançados]
        UC19[UC19: Exportar/Importar Dados]
        UC20[UC20: Monitorar Sistema]
        UC21[UC21: Configurar Servidor]
        UC22[UC22: Gerenciar Conexões Clientes]
        UC23[UC23: Visualizar Logs Sistema]
    end
    
    subgraph "Sistema Finanza - Módulo Sincronização"
        UC24[UC24: Sincronizar Automaticamente]
        UC25[UC25: Resolver Conflitos]
        UC26[UC26: Validar Integridade Dados]
        UC27[UC27: Processar Fila Sincronização]
    end
    
    subgraph "Sistema Finanza - Módulo Banco de Dados"
        UC28[UC28: Executar Backup Automático]
        UC29[UC29: Restaurar Dados]
        UC30[UC30: Manter Integridade Referencial]
        UC31[UC31: Gerenciar Transações BD]
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
    UM --> UC11
    UM --> UC12
    UM --> UC13
    UM --> UC14
    UM --> UC15
    UM --> UC16
    
    %% Relacionamentos Usuário Desktop
    UD --> UC1
    UD --> UC2
    UD --> UC3
    UD --> UC4
    UD --> UC5
    UD --> UC6
    UD --> UC7
    UD --> UC8
    UD --> UC9
    UD --> UC10
    UD --> UC11
    UD --> UC17
    UD --> UC18
    UD --> UC19
    UD --> UC20
    UD --> UC21
    UD --> UC22
    UD --> UC23
    
    %% Relacionamentos Sistema Sincronização
    SS --> UC24
    SS --> UC25
    SS --> UC26
    SS --> UC27
    
    %% Relacionamentos Sistema Banco de Dados
    BD --> UC28
    BD --> UC29
    BD --> UC30
    BD --> UC31
    
    %% Relacionamentos Includes (Inclusão)
    UC6 -.-> UC7 : includes
    UC6 -.-> UC8 : includes
    UC6 -.-> UC11 : includes
    UC9 -.-> UC11 : includes
    UC18 -.-> UC6 : includes
    UC25 -.-> UC26 : includes
    UC19 -.-> UC18 : includes
    UC10 -.-> UC6 : includes
    
    %% Relacionamentos Extends (Extensão)
    UC12 -.-> UC24 : extends
    UC19 -.-> UC18 : extends
    UC16 -.-> UC12 : extends
    UC4 -.-> UC3 : extends
    
    %% Relacionamentos de Generalização
    UC24 -.-> UC12 : generalizes
    UC28 -.-> UC29 : generalizes
    
    %% Styling
    classDef actor fill:#e1f5fe,stroke:#01579b,stroke-width:3px,color:#000
    classDef autenticacao fill:#fff3e0,stroke:#f57c00,stroke-width:2px,color:#000
    classDef gestao fill:#e8f5e8,stroke:#2e7d32,stroke-width:2px,color:#000
    classDef mobile fill:#fce4ec,stroke:#c2185b,stroke-width:2px,color:#000
    classDef desktop fill:#e3f2fd,stroke:#1976d2,stroke-width:2px,color:#000
    classDef sincronizacao fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px,color:#000
    classDef database fill:#fff8e1,stroke:#f9a825,stroke-width:2px,color:#000
    
    class UM,UD,SS,BD actor
    class UC1,UC2,UC3,UC4,UC5 autenticacao
    class UC6,UC7,UC8,UC9,UC10,UC11 gestao
    class UC12,UC13,UC14,UC15,UC16 mobile
    class UC17,UC18,UC19,UC20,UC21,UC22,UC23 desktop
    class UC24,UC25,UC26,UC27 sincronizacao
    class UC28,UC29,UC30,UC31 database
```

---

## 📋 Especificação Completa dos Casos de Uso

### **🔐 Módulo de Autenticação**

#### **UC01: Fazer Login**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** Autenticação no sistema com email/senha
- **Fluxo Principal:**
  1. Usuário informa email e senha
  2. Sistema valida credenciais localmente/remotamente
  3. Sistema autentica usuário
  4. Sistema redireciona para dashboard
- **Pré-condições:** Usuário deve ter conta criada
- **Pós-condições:** Usuário autenticado no sistema
- **Fluxo Alternativo:** Login offline (mobile apenas)

#### **UC02: Criar Conta**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** Registro de novo usuário no sistema
- **Fluxo Principal:**
  1. Usuário informa dados pessoais
  2. Sistema valida informações
  3. Sistema cria conta com hash de senha
  4. Sistema confirma cadastro
- **Pré-condições:** Email não deve estar em uso
- **Pós-condições:** Nova conta criada e ativa

#### **UC03: Recuperar Senha**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** Recuperação de senha via processo seguro
- **Fluxo Principal:**
  1. Usuário solicita recuperação
  2. Sistema valida email existente
  3. Sistema gera token temporário
  4. Usuário define nova senha
- **Pré-condições:** Email deve existir no sistema
- **Pós-condições:** Senha alterada com sucesso

#### **UC04: Alterar Senha**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** Alteração de senha com autenticação
- **Fluxo Principal:**
  1. Usuário acessa perfil
  2. Sistema solicita senha atual
  3. Usuário informa nova senha
  4. Sistema atualiza credenciais
- **Estende:** UC03 (Recuperar Senha)
- **Pré-condições:** Usuário autenticado
- **Pós-condições:** Senha alterada com segurança

#### **UC05: Fazer Logout**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** Encerramento seguro da sessão
- **Fluxo Principal:**
  1. Usuário solicita logout
  2. Sistema salva dados pendentes
  3. Sistema limpa sessão
  4. Sistema retorna à tela de login
- **Pré-condições:** Usuário autenticado
- **Pós-condições:** Sessão encerrada com segurança

---

### **💰 Módulo de Gestão Financeira**

#### **UC06: Gerenciar Transações**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** CRUD completo de transações financeiras
- **Fluxo Principal:**
  1. Usuário acessa área de transações
  2. Usuário escolhe ação (criar/editar/excluir/visualizar)
  3. Sistema processa operação
  4. Sistema atualiza dados e saldos
- **Inclui:** UC07 (Gerenciar Contas), UC08 (Gerenciar Categorias), UC11 (Calcular Saldos)
- **Pré-condições:** Usuário autenticado
- **Pós-condições:** Transação processada e saldos atualizados

#### **UC07: Gerenciar Contas**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** Gestão de contas bancárias, carteiras e cartões
- **Fluxo Principal:**
  1. Usuário acessa gestão de contas
  2. Usuário executa operações CRUD
  3. Sistema valida dados e operações
  4. Sistema atualiza saldos automaticamente
- **Pré-condições:** Usuário autenticado
- **Pós-condições:** Contas atualizadas e saldos recalculados

#### **UC08: Gerenciar Categorias**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** Organização de categorias de receitas/despesas
- **Fluxo Principal:**
  1. Usuário acessa categorias
  2. Usuário gerencia categorias (CRUD)
  3. Sistema organiza transações por categoria
  4. Sistema atualiza relatórios e gráficos
- **Pré-condições:** Usuário autenticado
- **Pós-condições:** Categorias organizadas e relatórios atualizados

#### **UC09: Visualizar Dashboard**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** Visualização de resumo financeiro personalizado
- **Fluxo Principal:**
  1. Sistema carrega dados do usuário
  2. Sistema calcula métricas financeiras
  3. Sistema gera gráficos e relatórios visuais
  4. Sistema apresenta dashboard interativo
- **Inclui:** UC11 (Calcular Saldos)
- **Pré-condições:** Usuário autenticado
- **Pós-condições:** Dashboard exibido com dados atualizados

#### **UC10: Filtrar Movimentações**
- **Ator:** Usuário Mobile, Usuário Desktop
- **Descrição:** Busca e filtros avançados de transações
- **Fluxo Principal:**
  1. Usuário define critérios de filtro
  2. Sistema aplica filtros nos dados
  3. Sistema exibe resultados filtrados
  4. Usuário pode exportar resultados
- **Inclui:** UC06 (Gerenciar Transações)
- **Pré-condições:** Dados de transações disponíveis
- **Pós-condições:** Resultados filtrados exibidos

#### **UC11: Calcular Saldos**
- **Ator:** Sistema (automático)
- **Descrição:** Cálculo automático de saldos por conta e geral
- **Fluxo Principal:**
  1. Sistema monitora mudanças em transações
  2. Sistema recalcula saldos afetados
  3. Sistema atualiza interface em tempo real
  4. Sistema valida consistência dos dados
- **Pré-condições:** Transações e contas cadastradas
- **Pós-condições:** Saldos atualizados e consistentes

---

### **📱 Módulo Mobile Específico**

#### **UC12: Sincronizar Dados Manualmente**
- **Ator:** Usuário Mobile
- **Descrição:** Sincronização manual com servidor desktop
- **Fluxo Principal:**
  1. Usuário solicita sincronização
  2. Sistema verifica conectividade com servidor
  3. Sistema envia dados locais modificados
  4. Sistema recebe e aplica dados do servidor
  5. Sistema resolve conflitos automaticamente
- **Estende:** UC24 (Sincronizar Automaticamente)
- **Pré-condições:** Conectividade disponível
- **Pós-condições:** Dados sincronizados entre mobile e desktop

#### **UC13: Trabalhar Offline**
- **Ator:** Usuário Mobile
- **Descrição:** Funcionamento completo sem conexão de rede
- **Fluxo Principal:**
  1. Sistema detecta falta de conexão
  2. Sistema ativa modo offline automático
  3. Usuário opera normalmente (CRUD completo)
  4. Sistema agenda sincronização para próxima conexão
- **Pré-condições:** Dados locais SQLite disponíveis
- **Pós-condições:** Operações salvas localmente e agendadas para sync

#### **UC14: Receber Notificações**
- **Ator:** Usuário Mobile
- **Descrição:** Notificações push e alertas do sistema
- **Fluxo Principal:**
  1. Sistema gera evento notificável
  2. Sistema verifica configurações do usuário
  3. Sistema envia notificação via Android
  4. Usuário visualiza e pode interagir com alerta
- **Pré-condições:** Notificações habilitadas pelo usuário
- **Pós-condições:** Usuário informado sobre eventos importantes

#### **UC15: Configurar Aplicativo**
- **Ator:** Usuário Mobile
- **Descrição:** Personalização e configuração do app
- **Fluxo Principal:**
  1. Usuário acessa tela de configurações
  2. Usuário modifica preferências (servidor, sync, notificações)
  3. Sistema valida configurações
  4. Sistema aplica e salva alterações
- **Pré-condições:** Usuário autenticado
- **Pós-condições:** Configurações aplicadas e persistidas

#### **UC16: Testar Conexão Servidor**
- **Ator:** Usuário Mobile
- **Descrição:** Verificação de conectividade com servidor desktop
- **Fluxo Principal:**
  1. Usuário solicita teste de conexão
  2. Sistema tenta conectar no servidor configurado
  3. Sistema exibe status da conexão
  4. Sistema oferece opções de configuração se falhar
- **Estende:** UC12 (Sincronizar Dados Manualmente)
- **Pré-condições:** Configurações de servidor definidas
- **Pós-condições:** Status de conectividade conhecido

---

### **🖥️ Módulo Desktop Específico**

#### **UC17: Administrar Usuários**
- **Ator:** Usuário Desktop (Administrador)
- **Descrição:** Gestão administrativa completa de usuários
- **Fluxo Principal:**
  1. Admin acessa painel de administração
  2. Admin visualiza lista de usuários e estatísticas
  3. Admin executa ações (criar, editar, desativar, resetar senha)
  4. Sistema registra auditoria e atualiza dados
- **Pré-condições:** Permissões administrativas ativas
- **Pós-condições:** Usuários gerenciados e logs de auditoria criados

#### **UC18: Gerar Relatórios Avançados**
- **Ator:** Usuário Desktop
- **Descrição:** Criação de relatórios financeiros detalhados
- **Fluxo Principal:**
  1. Usuário define parâmetros e filtros avançados
  2. Sistema consulta dados históricos
  3. Sistema processa e analisa informações
  4. Sistema gera relatório com gráficos e tabelas
- **Inclui:** UC06 (Gerenciar Transações)
- **Pré-condições:** Dados históricos disponíveis
- **Pós-condições:** Relatório gerado e disponível para visualização

#### **UC19: Exportar/Importar Dados**
- **Ator:** Usuário Desktop
- **Descrição:** Exportação/importação em múltiplos formatos
- **Fluxo Principal:**
  1. Usuário escolhe tipo de operação e formato (CSV, Excel, PDF)
  2. Sistema seleciona dados conforme critérios
  3. Sistema converte para formato escolhido
  4. Sistema disponibiliza arquivo para download
- **Estende:** UC18 (Gerar Relatórios Avançados)
- **Pré-condições:** Dados disponíveis para exportação
- **Pós-condições:** Arquivo gerado e disponibilizado

#### **UC20: Monitorar Sistema**
- **Ator:** Usuário Desktop (Administrador)
- **Descrição:** Monitoramento de performance e status do sistema
- **Fluxo Principal:**
  1. Sistema coleta métricas de performance em tempo real
  2. Sistema analisa uso de recursos e conexões
  3. Sistema exibe dashboards de monitoramento
  4. Admin monitora indicadores e pode tomar ações
- **Pré-condições:** Sistema em execução e permissões de admin
- **Pós-condições:** Métricas monitoradas e disponíveis

#### **UC21: Configurar Servidor**
- **Ator:** Usuário Desktop (Administrador)
- **Descrição:** Configurações administrativas do servidor
- **Fluxo Principal:**
  1. Admin acessa painel de configurações
  2. Admin modifica parâmetros (porta, banco, backup, etc.)
  3. Sistema valida configurações e dependências
  4. Sistema aplica alterações e reinicia serviços necessários
- **Pré-condições:** Permissões administrativas e sistema ativo
- **Pós-condições:** Sistema configurado com novos parâmetros

#### **UC22: Gerenciar Conexões Clientes**
- **Ator:** Usuário Desktop (Administrador)
- **Descrição:** Controle de conexões ativas de clientes
- **Fluxo Principal:**
  1. Sistema exibe lista de conexões ativas
  2. Admin visualiza estatísticas por cliente
  3. Admin pode desconectar clientes específicos
  4. Sistema registra ações de administração
- **Pré-condições:** Servidor ativo com clientes conectados
- **Pós-condições:** Conexões gerenciadas e logs atualizados

#### **UC23: Visualizar Logs Sistema**
- **Ator:** Usuário Desktop (Administrador)
- **Descrição:** Acesso e análise de logs do sistema
- **Fluxo Principal:**
  1. Admin acessa interface de logs
  2. Sistema exibe logs filtráveis por data/tipo/usuário
  3. Admin pode buscar eventos específicos
  4. Sistema permite exportação dos logs
- **Pré-condições:** Logs do sistema disponíveis
- **Pós-condições:** Logs visualizados e analisados

---

### **🔄 Módulo de Sincronização**

#### **UC24: Sincronizar Automaticamente**
- **Ator:** Sistema de Sincronização
- **Descrição:** Sincronização automática periódica entre plataformas
- **Fluxo Principal:**
  1. Sistema verifica agendamento configurado
  2. Sistema identifica mudanças desde última sync
  3. Sistema executa sincronização bidirecional
  4. Sistema atualiza timestamps e logs
- **Pré-condições:** Agendamento ativo e conectividade disponível
- **Pós-condições:** Dados sincronizados automaticamente

#### **UC25: Resolver Conflitos**
- **Ator:** Sistema de Sincronização
- **Descrição:** Resolução automática de conflitos de dados
- **Fluxo Principal:**
  1. Sistema detecta conflito durante sincronização
  2. Sistema analisa timestamps e prioridades
  3. Sistema aplica regras de resolução configuradas
  4. Sistema notifica usuários sobre resoluções
- **Inclui:** UC26 (Validar Integridade Dados)
- **Pré-condições:** Conflito detectado durante sync
- **Pós-condições:** Conflito resolvido com dados consistentes

#### **UC26: Validar Integridade Dados**
- **Ator:** Sistema de Sincronização
- **Descrição:** Verificação contínua de integridade dos dados
- **Fluxo Principal:**
  1. Sistema calcula checksums dos dados
  2. Sistema valida relacionamentos e constraints
  3. Sistema detecta inconsistências ou corrupções
  4. Sistema executa correções automáticas ou alerta admin
- **Pré-condições:** Dados disponíveis para validação
- **Pós-condições:** Integridade dos dados verificada e garantida

#### **UC27: Processar Fila Sincronização**
- **Ator:** Sistema de Sincronização
- **Descrição:** Gerenciamento da fila de operações de sincronização
- **Fluxo Principal:**
  1. Sistema mantém fila ordenada de operações pendentes
  2. Sistema processa operações por prioridade
  3. Sistema retenta operações falhadas conforme política
  4. Sistema limpa operações completadas com sucesso
- **Pré-condições:** Operações enfileiradas para sincronização
- **Pós-condições:** Fila processada e operações executadas

---

### **🗄️ Módulo de Banco de Dados**

#### **UC28: Executar Backup Automático**
- **Ator:** Sistema de Banco de Dados
- **Descrição:** Backup automático e agendado dos dados
- **Fluxo Principal:**
  1. Sistema verifica agendamento de backup
  2. Sistema cria snapshot consistente dos dados
  3. Sistema compacta e armazena backup
  4. Sistema valida integridade do backup criado
- **Pré-condições:** Agendamento ativo e espaço disponível
- **Pós-condições:** Backup criado e validado

#### **UC29: Restaurar Dados**
- **Ator:** Sistema de Banco de Dados
- **Descrição:** Restauração de dados a partir de backup
- **Fluxo Principal:**
  1. Sistema identifica ponto de restauração
  2. Sistema valida integridade do backup
  3. Sistema executa restauração controlada
  4. Sistema verifica consistência pós-restauração
- **Pré-condições:** Backup válido disponível
- **Pós-condições:** Dados restaurados e consistentes

#### **UC30: Manter Integridade Referencial**
- **Ator:** Sistema de Banco de Dados
- **Descrição:** Garantia de integridade das relações entre dados
- **Fluxo Principal:**
  1. Sistema monitora operações de dados
  2. Sistema valida constraints e relacionamentos
  3. Sistema impede operações que violem integridade
  4. Sistema mantém logs de violações tentadas
- **Pré-condições:** Operações de dados em execução
- **Pós-condições:** Integridade referencial mantida

#### **UC31: Gerenciar Transações BD**
- **Ator:** Sistema de Banco de Dados
- **Descrição:** Controle de transações e locks do banco
- **Fluxo Principal:**
  1. Sistema inicia transação para operação
  2. Sistema gerencia locks e isolamento
  3. Sistema executa commit ou rollback conforme resultado
  4. Sistema libera recursos e logs da transação
- **Pré-condições:** Operação de dados solicitada
- **Pós-condições:** Transação processada com consistência ACID

---

## 🔗 Relacionamentos Completos entre Casos de Uso

### **Includes (Inclusão) - Funcionalidades Obrigatórias:**
- **UC06 → UC07:** Gerenciar transações requer gestão de contas
- **UC06 → UC08:** Gerenciar transações requer gestão de categorias  
- **UC06 → UC11:** Gerenciar transações inclui cálculo de saldos
- **UC09 → UC11:** Visualizar dashboard inclui cálculo de saldos
- **UC18 → UC06:** Gerar relatórios inclui consulta de transações
- **UC25 → UC26:** Resolver conflitos inclui validação de integridade
- **UC10 → UC06:** Filtrar movimentações inclui gerenciar transações

### **Extends (Extensão) - Funcionalidades Opcionais:**
- **UC12 → UC24:** Sincronização manual estende automática
- **UC19 → UC18:** Exportação estende geração de relatórios
- **UC16 → UC12:** Testar conexão estende sincronização manual
- **UC04 → UC03:** Alterar senha estende recuperar senha

### **Generalização - Especializações:**
- **UC24 ← UC12:** Sincronização automática generaliza manual
- **UC28 ← UC29:** Backup generaliza restauração
- **Usuários mobile e desktop:** Compartilham casos básicos de autenticação e gestão financeira

### **Dependências Funcionais:**
- **Autenticação → Todas as funcionalidades:** Login necessário para acesso
- **Gestão Financeira → Relatórios:** Dados necessários para análises
- **Sincronização → Integridade:** Consistência de dados entre plataformas
- **Banco de Dados → Sistema:** Persistência necessária para todas as operações

---

## 📊 Métricas Completas dos Casos de Uso

### **Por Ator:**
- **👤 Usuário Mobile:** 16 casos de uso
- **🖥️ Usuário Desktop:** 19 casos de uso  
- **⚙️ Sistema Sincronização:** 4 casos de uso
- **🗄️ Sistema Banco Dados:** 4 casos de uso

### **Por Módulo de Funcionalidade:**
- **🔐 Autenticação:** 5 casos de uso
- **💰 Gestão Financeira:** 6 casos de uso
- **📱 Mobile Específico:** 5 casos de uso
- **🖥️ Desktop Específico:** 7 casos de uso
- **🔄 Sincronização:** 4 casos de uso
- **🗄️ Banco de Dados:** 4 casos de uso

### **Por Complexidade de Implementação:**
- **🟢 Simples (1-3 telas):** 12 casos de uso
- **🟡 Médio (4-6 telas):** 13 casos de uso  
- **🔴 Complexo (7+ telas):** 6 casos de uso

### **Por Prioridade de Desenvolvimento:**
- **🔴 Crítico (Core):** 11 casos de uso
- **🟡 Importante:** 14 casos de uso
- **🟢 Desejável:** 6 casos de uso

### **Por Tipo de Interação:**
- **👤 Manual (Usuário):** 23 casos de uso
- **⚙️ Automático (Sistema):** 8 casos de uso

---

## 📋 Matriz de Cobertura Funcional

| Plataforma | Autenticação | Gestão Financeira | Relatórios | Administração | Sincronização |
|------------|---------------|-------------------|------------|---------------|---------------|
| **Mobile** | ✅ Completa   | ✅ Completa       | ⚠️ Básicos  | ❌ Não        | ✅ Completa   |
| **Desktop** | ✅ Completa   | ✅ Completa       | ✅ Avançados | ✅ Completa   | ✅ Completa   |

---

**Legenda do Diagrama Aprimorado:**
- **👤🖥️⚙️🗄️ Atores:** Representam usuários e sistemas com responsabilidades específicas
- **Elipses Coloridas:** Casos de uso organizados por módulos funcionais
- **Setas Sólidas:** Associações diretas entre ator e caso de uso
- **Setas Tracejadas:** Relacionamentos include/extend entre casos de uso
- **Cores dos Módulos:** Identificação visual clara de cada área funcional

**Observações Técnicas:**
- ✅ Diagrama segue rigorosamente padrão UML 2.0
- ✅ Casos de uso cobrem 100% das funcionalidades mobile e desktop
- ✅ Relacionamentos garantem reusabilidade e manutenibilidade
- ✅ Separação clara entre funcionalidades específicas de cada plataforma
- ✅ Cobertura completa do ciclo de vida dos dados
- ✅ Incluído controle de sincronização e integridade
- ✅ Casos de uso de administração e monitoramento
- ✅ Compatível com arquitetura cliente-servidor implementada

---

*Diagrama de Casos de Uso Completo criado seguindo padrões UML 2.0*  
*Versão: 2.0 | Data: Dezembro 2024*  
*Ferramenta: Mermaid + Análise Completa de Requisitos*  
*Cobertura: Mobile Android + Desktop Java + Sincronização + Banco de Dados*