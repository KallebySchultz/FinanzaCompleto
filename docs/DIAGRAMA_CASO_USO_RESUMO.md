# 📋 RESUMO EXECUTIVO - DIAGRAMA DE CASOS DE USO FINANZA

## 🎯 Visão Geral Rápida

**Sistema:** Finanza - Controle Financeiro Integrado  
**Plataformas:** Android Mobile + Java Desktop  
**Arquitetura:** Cliente-Servidor com Sincronização  

---

## 👥 ATORES (4)

```
👤 Usuário Mobile     🖥️  Usuário Desktop     ⚙️  Sistema Sync     🗄️  Sistema BD
├─ App Android        ├─ Cliente Java        ├─ Automático       ├─ MySQL/SQLite
├─ Modo Offline       ├─ Administração       ├─ Background       ├─ Backup/Restore
└─ Sincronização      └─ Relatórios Avançados └─ Conflitos       └─ Integridade
```

---

## 🔗 CASOS DE USO POR MÓDULO (31 total)

### 🔐 AUTENTICAÇÃO (5 casos)
```
UC01: Login          UC02: Criar Conta    UC03: Recuperar Senha
UC04: Alterar Senha  UC05: Logout
```

### 💰 GESTÃO FINANCEIRA (6 casos)
```
UC06: Gerenciar Transações    UC07: Gerenciar Contas
UC08: Gerenciar Categorias    UC09: Visualizar Dashboard  
UC10: Filtrar Movimentações   UC11: Calcular Saldos
```

### 📱 MOBILE ESPECÍFICO (5 casos)
```
UC12: Sincronizar Manual      UC13: Trabalhar Offline
UC14: Receber Notificações    UC15: Configurar App
UC16: Testar Conexão
```

### 🖥️ DESKTOP ESPECÍFICO (7 casos)
```
UC17: Administrar Usuários    UC18: Gerar Relatórios
UC19: Exportar/Importar       UC20: Monitorar Sistema
UC21: Configurar Servidor     UC22: Gerenciar Conexões
UC23: Visualizar Logs
```

### 🔄 SINCRONIZAÇÃO (4 casos)
```
UC24: Sincronizar Auto        UC25: Resolver Conflitos
UC26: Validar Integridade     UC27: Processar Fila
```

### 🗄️ BANCO DE DADOS (4 casos)
```
UC28: Backup Automático       UC29: Restaurar Dados
UC30: Integridade Referencial UC31: Gerenciar Transações BD
```

---

## 📊 MATRIZ DE ACESSO

| Ator | Autenticação | Gestão Financeira | Mobile | Desktop | Sync | BD |
|------|--------------|-------------------|--------|---------|------|----|
| 👤 Mobile | ✅ 5/5 | ✅ 6/6 | ✅ 5/5 | ❌ 0/7 | 🔄 Auto | 📱 SQLite |
| 🖥️ Desktop | ✅ 5/5 | ✅ 6/6 | ❌ 0/5 | ✅ 7/7 | 🔄 Auto | 🗄️ MySQL |
| ⚙️ Sync | ❌ 0/5 | ❌ 0/6 | ❌ 0/5 | ❌ 0/7 | ✅ 4/4 | 🔄 Auto |
| 🗄️ BD | ❌ 0/5 | 🔄 Auto | ❌ 0/5 | ❌ 0/7 | ❌ 0/4 | ✅ 4/4 |

---

## 🔗 RELACIONAMENTOS CRÍTICOS

### Includes (Obrigatórios):
- **UC06** (Transações) ➜ **UC07** (Contas) + **UC08** (Categorias) + **UC11** (Saldos)
- **UC09** (Dashboard) ➜ **UC11** (Saldos)
- **UC18** (Relatórios) ➜ **UC06** (Transações)
- **UC25** (Conflitos) ➜ **UC26** (Integridade)

### Extends (Opcionais):
- **UC12** (Sync Manual) ⟵ **UC24** (Sync Auto)
- **UC19** (Export) ⟵ **UC18** (Relatórios)
- **UC04** (Alterar) ⟵ **UC03** (Recuperar)

---

## 🎯 PRIORIDADES DE DESENVOLVIMENTO

### 🔴 CRÍTICO (MVP - 11 casos)
```
UC01, UC02, UC05 (Auth Base)
UC06, UC07, UC08, UC09, UC11 (Gestão Core)  
UC13 (Offline), UC24, UC26 (Sync Base)
```

### 🟡 IMPORTANTE (14 casos)
```
UC03, UC04 (Auth Avançada)
UC10 (Filtros), UC12, UC14, UC15, UC16 (Mobile)
UC17, UC18, UC19, UC20 (Desktop)
UC25, UC27 (Sync Avançada)
UC28, UC30 (BD Core)
```

### 🟢 DESEJÁVEL (6 casos)
```
UC21, UC22, UC23 (Admin Avançada)
UC29, UC31 (BD Avançado)
```

---

## 📱🖥️ COMPARATIVO DE FUNCIONALIDADES

### COMUM A AMBAS PLATAFORMAS:
- ✅ Autenticação completa (Login, Cadastro, Recuperação, Logout)
- ✅ Gestão financeira completa (Transações, Contas, Categorias, Dashboard)
- ✅ Sincronização automática e manual
- ✅ Cálculo de saldos em tempo real

### MOBILE EXCLUSIVO:
- 📱 Modo offline completo
- 📱 Notificações push
- 📱 Configurações de app
- 📱 Teste de conexão manual

### DESKTOP EXCLUSIVO:
- 🖥️ Administração de usuários
- 🖥️ Relatórios avançados com gráficos
- 🖥️ Exportação/Importação de dados
- 🖥️ Monitoramento de sistema
- 🖥️ Configuração de servidor
- 🖥️ Gerenciamento de conexões
- 🖥️ Logs do sistema

---

## 🏗️ ARQUITETURA TÉCNICA COBERTA

```
┌─────────────────┐    ┌─────────────────┐
│   📱 MOBILE     │    │   🖥️ DESKTOP    │
│   Android Java  │◄──►│   Java Swing    │
│   SQLite Room   │    │   MySQL/Socket  │
│   31 Casos      │    │   31 Casos      │
└─────────────────┘    └─────────────────┘
         │                       │
         └───────┐       ┌───────┘
                 ▼       ▼
          ┌─────────────────┐
          │  ⚙️ SYNC ENGINE │
          │  Resolução      │
          │  Conflitos      │
          │  4 Casos        │
          └─────────────────┘
                 │
                 ▼
          ┌─────────────────┐
          │  🗄️ DATABASE    │
          │  MySQL + SQLite │
          │  Backup/Restore │
          │  4 Casos        │
          └─────────────────┘
```

---

**✅ DIAGRAMA COMPLETO E PRONTO PARA DESENVOLVIMENTO**  
*Versão 2.0 | 31 Casos de Uso | 100% Cobertura Mobile + Desktop*