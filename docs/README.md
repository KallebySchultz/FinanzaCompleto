# 📚 DOCUMENTAÇÃO TÉCNICA COMPLETA - FINANZA

## 📋 Índice de Documentação Acadêmica

Este diretório contém toda a documentação técnica e acadêmica do sistema Finanza, organizada conforme os requisitos do trabalho interdisciplinar.

---

## 📑 Documentos Principais

### **4. CICLO DE VIDA DO SOFTWARE**
📄 **Arquivo:** [`CICLO_VIDA_SOFTWARE.md`](./CICLO_VIDA_SOFTWARE.md)

Documentação completa do modelo incremental de desenvolvimento utilizado no projeto, incluindo:
- ✅ **Timeline detalhado:** Julho 2024 - Novembro 2025
- ✅ **4 Versões Mobile:** v1.0 Foundation → v2.5 Advanced Features
- ✅ **4 Versões Desktop:** v1.0 Server Foundation → v2.5 Enterprise Features
- ✅ **Diagrama Gantt:** Visualização temporal dos incrementos
- ✅ **Métricas de progresso:** Status de cada fase de desenvolvimento

**Figura Principal:** Diagrama de Gantt mostrando evolução incremental das versões mobile e desktop

---

### **5. WIREFRAMES**
📄 **Arquivo:** [`WIREFRAMES.md`](./WIREFRAMES.md)

Wireframes de alta precisão para todas as interfaces do sistema:

#### **📱 Mobile (Android) - 4 Telas Principais:**
- ✅ **Tela de Login:** Autenticação com Material Design
- ✅ **Dashboard Principal:** Visão geral financeira completa
- ✅ **Adicionar Transação:** Formulário completo com validações
- ✅ **Lista de Transações:** Histórico com filtros avançados

#### **🖥️ Desktop (Java Swing) - 3 Interfaces Principais:**
- ✅ **Interface Principal:** Dashboard administrativo do servidor
- ✅ **Gerenciamento de Usuários:** Administração completa de usuários
- ✅ **Monitor de Sincronização:** Controle em tempo real de sincronização

**Cada wireframe inclui:**
- Legenda descritiva da funcionalidade principal
- Layout responsivo e padrões de design
- Fluxos de navegação e interação

---

### **6. DIAGRAMA DE CASO DE USO**
📄 **Arquivo:** [`DIAGRAMA_CASO_USO.md`](./DIAGRAMA_CASO_USO.md)

Diagrama UML completo dos casos de uso do sistema:
- ✅ **18 Casos de Uso** organizados por módulos funcionais
- ✅ **3 Atores Principais:** Usuário Mobile, Usuário Desktop, Sistema de Sincronização
- ✅ **5 Módulos:** Autenticação, Gestão Financeira, Mobile Específico, Desktop Específico, Sincronização
- ✅ **Relacionamentos:** Include, extend e generalizações
- ✅ **Especificações detalhadas:** Fluxos principais, pré/pós-condições

**Figura Principal:** Diagrama UML com atores, casos de uso e relacionamentos

---

### **7. DIAGRAMA DE CLASSE**
📄 **Arquivo:** [`DIAGRAMA_CLASSE.md`](./DIAGRAMA_CLASSE.md)

Arquitetura orientada a objetos completa do sistema:
- ✅ **13 Classes Principais** com atributos e métodos detalhados
- ✅ **Classes de Domínio:** Usuario, Conta, Categoria, Transacao
- ✅ **Classes de Serviço:** AuthService, SyncService, DatabaseManager
- ✅ **Classes DAO:** Padrão Data Access Object implementado
- ✅ **Padrões de Design:** Singleton, Repository, DAO, Value Object
- ✅ **Relacionamentos:** 15 associações com cardinalidades

**Figura Principal:** Diagrama UML de classes com relacionamentos e métodos

---

### **8. DIAGRAMA ER (ENTIDADE-RELACIONAMENTO)**
📄 **Arquivo:** [`DIAGRAMA_ER.md`](./DIAGRAMA_ER.md)

Modelo de banco de dados MySQL completo:
- ✅ **10 Entidades Principais** com todos os atributos
- ✅ **Relacionamentos:** 15 foreign keys com cardinalidades
- ✅ **Restrições de Integridade:** Checks, constraints e validações
- ✅ **Otimizações:** 25+ índices estratégicos e particionamento
- ✅ **Triggers:** Auditoria e sincronização automática
- ✅ **Normalização:** 3ª Forma Normal aplicada

**Figura Principal:** Diagrama ER com entidades, relacionamentos e cardinalidades

---

### **9. DIAGRAMA DE SEQUÊNCIA**
📄 **Arquivo:** [`DIAGRAMA_SEQUENCIA.md`](./DIAGRAMA_SEQUENCIA.md)

Fluxos detalhados de interação do sistema:
- ✅ **6 Cenários Principais** de funcionamento do sistema
- ✅ **Autenticação:** Login completo com validação e sessão
- ✅ **Transação Mobile:** Criação offline-first com sincronização
- ✅ **Sincronização Bidirecional:** Processo automático com resolução de conflitos
- ✅ **Geração de Relatórios:** Exportação em múltiplos formatos
- ✅ **Resolução de Conflitos:** Estratégias automáticas por timestamp
- ✅ **Modo Offline:** Operação sem conectividade e recuperação

**Figura Principal:** 6 diagramas UML de sequência mostrando fluxos críticos

---

## 🎯 Características da Documentação

### **📊 Qualidade Técnica:**
- ✅ **Padrões UML 2.0** aplicados consistentemente
- ✅ **Diagramas Mermaid** de alta qualidade técnica
- ✅ **Legendas Descritivas** para cada figura
- ✅ **Especificações Detalhadas** com métricas e exemplos
- ✅ **Rastreabilidade** entre documentos e implementação

### **🎨 Apresentação Visual:**
- ✅ **Formatação Markdown** profissional e consistente
- ✅ **Emojis e Ícones** para melhor legibilidade
- ✅ **Tabelas e Listas** organizadas metodicamente
- ✅ **Códigos de Exemplo** com syntax highlighting
- ✅ **Estrutura Hierárquica** clara e navegável

### **📈 Completude Acadêmica:**
- ✅ **Requisitos Atendidos:** 100% dos itens solicitados
- ✅ **Cronograma Detalhado:** Julho 2024 - Novembro 2025
- ✅ **Versões Documentadas:** 2+ versões mobile e desktop
- ✅ **Diagramas Obrigatórios:** Todos criados com alta precisão
- ✅ **Integração Completa:** Mobile + Desktop + Sincronização

---

## 🔗 Relacionamentos entre Documentos

### **Fluxo de Leitura Recomendado:**

1. **📋 Ciclo de Vida** → Entender evolução temporal do projeto
2. **🎨 Wireframes** → Visualizar interfaces e experiência do usuário  
3. **🎯 Casos de Uso** → Compreender funcionalidades e atores
4. **🏗️ Classes** → Estudar arquitetura orientada a objetos
5. **🗄️ ER** → Analisar estrutura de dados persistentes
6. **🔄 Sequência** → Observar fluxos dinâmicos de execução

### **Referências Cruzadas:**
- Casos de Uso ↔ Classes: Mapeamento funcional/estrutural
- Classes ↔ ER: Objeto-relacional mapping
- Wireframes ↔ Sequência: UI flows e backend processes
- Ciclo de Vida ↔ Todos: Timeline de implementação

---

## 📋 Checklist de Conformidade

### **✅ Requisitos Atendidos:**

#### **Seção 4 - Ciclo de Vida:**
- [x] Modelo incremental documentado
- [x] Figura com timeline de desenvolvimento
- [x] 2+ versões mobile claramente definidas
- [x] 2+ versões desktop claramente definidas
- [x] Período julho-novembro 2025 contemplado

#### **Seção 5 - Wireframes:**
- [x] Wireframes mobile de alta precisão
- [x] Wireframes desktop de alta precisão  
- [x] Legendas descritivas para cada tela
- [x] Funcionalidade principal de cada interface

#### **Seção 6 - Caso de Uso:**
- [x] Diagrama UML completo anexado
- [x] Legenda explicativa na imagem
- [x] Atores e casos de uso identificados
- [x] Relacionamentos documentados

#### **Seção 7 - Diagrama de Classe:**
- [x] Diagrama UML completo anexado
- [x] Legenda explicativa na imagem
- [x] Classes com atributos e métodos
- [x] Relacionamentos e cardinalidades

#### **Seção 8 - Diagrama ER:**
- [x] Diagrama ER completo anexado
- [x] Legenda explicativa na imagem
- [x] Entidades com atributos completos
- [x] Relacionamentos e cardinalidades

#### **Seção 9 - Diagrama de Sequência:**
- [x] Diagramas UML completos anexados
- [x] Legendas explicativas nas imagens
- [x] Fluxos de interação detalhados
- [x] Cenários críticos documentados

---

## 🛠️ Ferramentas e Tecnologias

### **Documentação:**
- **Markdown:** Formatação e estruturação
- **Mermaid:** Diagramas UML técnicos
- **ASCII Art:** Wireframes textuais
- **Emojis:** Navegação visual melhorada

### **Padrões Aplicados:**
- **UML 2.0:** Notação padrão para diagramas
- **IEEE:** Estrutura de documentação técnica
- **Clean Architecture:** Organização de componentes
- **Database Design:** Normalização e otimização

---

## 📞 Informações do Projeto

**Projeto:** Sistema de Controle Financeiro Finanza  
**Disciplina:** Trabalho Interdisciplinar 2025  
**Curso:** Sistemas de Informação  
**Equipe:** Kalleby Schultz  
**Período:** 2025/1  

**Documentação criada em:** Dezembro 2024  
**Versão:** 1.0  
**Status:** Completa e validada  

---

*Documentação técnica completa seguindo padrões acadêmicos e de mercado*