# 📚 Índice de Documentação - Sistema Finanza

Este documento serve como índice central para toda a documentação do projeto Finanza.

---

## 🎯 Comece Por Aqui

Se você é **novo no projeto**, recomendamos ler nesta ordem:

1. **[README.md](README.md)** - Visão geral do projeto Finanza
2. **[MAPEAMENTO_COMPLETO.md](MAPEAMENTO_COMPLETO.md)** - Arquitetura detalhada do sistema
3. **[GUIA_COMENTARIOS_CODIGO.md](GUIA_COMENTARIOS_CODIGO.md)** - Padrões de código e comentários
4. **[RESUMO_ALTERACOES.md](RESUMO_ALTERACOES.md)** - Histórico de limpeza e melhorias

---

## 📖 Documentação Principal

### Visão Geral do Projeto
- **[README.md](README.md)** - Apresentação completa do Sistema Finanza
  - Sobre o projeto e objetivos
  - Funcionalidades mobile e desktop
  - Arquitetura e tecnologias
  - Instalação e uso
  - Contexto acadêmico

### Documentação Móvel
- **[README_MOBILE.md](README_MOBILE.md)** - Guia específico do app Android
  - Funcionalidades do app mobile
  - Instalação e configuração
  - Uso do aplicativo

### Projeto Acadêmico
- **[TRABALHO_INTERDISCIPLINAR_2025.md](TRABALHO_INTERDISCIPLINAR_2025.md)** - Descrição do trabalho interdisciplinar
  - Disciplinas envolvidas
  - Requisitos e entregas
  - Critérios de avaliação

---

## 🗺️ Documentação Técnica

### Arquitetura e Mapeamento
- **[MAPEAMENTO_COMPLETO.md](MAPEAMENTO_COMPLETO.md)** - ⭐ **ESSENCIAL**
  - **Versão Mobile (Android)**
    - Estrutura completa de diretórios
    - Descrição de cada Activity
    - Fluxo de funcionalidades
    - Camada de dados (Models, DAOs, Database)
    - Camada de rede (ServerClient, Protocol, Sync)
    - Utilitários e helpers
  - **Versão Desktop**
    - Cliente Desktop (Java Swing)
    - Servidor Desktop (Java + MySQL)
    - Controllers, Views e Models
    - DAOs e Protocol
  - **Fluxos de Dados Completos**
    - Login de usuário
    - Criar lançamento
    - Administrador edita usuário
    - Sincronização automática
  - **Informações Técnicas**
    - Camada de interface
    - Banco de dados (Room e MySQL)
    - Protocolo de comunicação TCP/IP
    - Segurança e autenticação

### Padrões de Código
- **[GUIA_COMENTARIOS_CODIGO.md](GUIA_COMENTARIOS_CODIGO.md)** - ⭐ **PARA DESENVOLVEDORES**
  - Padrões de comentários em português
  - Templates para cada tipo de arquivo
  - Status de documentação de todos os arquivos
  - Diretrizes por tipo (Activity, DAO, Network)
  - Boas práticas e checklist
  - Como continuar o trabalho

### Histórico de Mudanças
- **[RESUMO_ALTERACOES.md](RESUMO_ALTERACOES.md)** - Registro de limpeza e melhorias
  - Arquivos removidos (54 arquivos)
  - Documentação criada
  - Comentários adicionados
  - Estatísticas completas
  - Próximos passos

### Autenticação e Administração
- **[GUIA_RAPIDO_ADMIN_USER.md](GUIA_RAPIDO_ADMIN_USER.md)** - Guia rápido de admin/usuário
  - Sistema de tipos de usuário
  - Credenciais padrão
  - Operações comuns
  - Troubleshooting
- **[ALTERACOES_USER_ADMIN.md](ALTERACOES_USER_ADMIN.md)** - Implementação de tipos de usuário
  - Diferenciação admin/usuário
  - Fluxos de autenticação
  - Migração de banco de dados
- **[DESKTOP VERSION/ALTERACOES_REGISTRO_ADMIN.md](DESKTOP VERSION/ALTERACOES_REGISTRO_ADMIN.md)** - Registro via desktop
  - Usuários criados via desktop são administradores
  - Protocolo atualizado de registro
  - Testes e validação

---

## 💻 Código Fonte

### Versão Mobile (Android)

#### 📱 Pacote Principal
```
app/src/main/java/com/example/finanza/
```

#### Estrutura de Pacotes:
- **model/** - Entidades do banco de dados
  - ✅ Usuario.java - Usuário do sistema (comentado)
  - ✅ Conta.java - Conta bancária (comentado)
  - ✅ Lancamento.java - Transação financeira (comentado)
  - ✅ Categoria.java - Categoria de transação (comentado)

- **ui/** - Interfaces do usuário (Activities)
  - ✅ MainActivity.java - Dashboard principal (comentado)
  - ✅ LoginActivity.java - Tela de login (comentado)
  - ⏳ RegisterActivity.java - Cadastro
  - ⏳ MenuActivity.java - Menu principal
  - ⏳ MovementsActivity.java - Gerenciar transações
  - ⏳ AccountsActivity.java - Gerenciar contas
  - ⏳ CategoriaActivity.java - Gerenciar categorias
  - ⏳ ProfileActivity.java - Perfil do usuário
  - ⏳ SettingsActivity.java - Configurações

- **network/** - Comunicação e sincronização
  - ⏳ ServerClient.java - Cliente de socket TCP/IP
  - ⏳ Protocol.java - Protocolo de comunicação
  - ⏳ AuthManager.java - Gerenciador de autenticação
  - ⏳ SyncService.java - Serviço de sincronização
  - ⏳ EnhancedSyncService.java - Sync avançada
  - ⏳ ConflictResolutionManager.java - Resolução de conflitos

- **db/** - Persistência local (Room)
  - ⏳ AppDatabase.java - Configuração do banco
  - ⏳ UsuarioDao.java - DAO de usuários
  - ⏳ ContaDao.java - DAO de contas
  - ⏳ CategoriaDao.java - DAO de categorias
  - ⏳ LancamentoDao.java - DAO de lançamentos

- **util/** - Utilitários
  - ⏳ DataIntegrityValidator.java - Validador de integridade

### Versão Desktop

#### 🖥️ Cliente Desktop
```
DESKTOP VERSION/ClienteFinanza/src/
```
- ⏳ MainCliente.java - Aplicação principal
- ⏳ view/ - Interfaces Swing
- ⏳ controller/ - Controladores MVC
- ⏳ model/ - Modelos de dados
- ⏳ util/ - Utilitários

#### 🔧 Servidor Desktop
```
DESKTOP VERSION/ServidorFinanza/src/
```
- ⏳ MainServidor.java - Servidor principal
- ⏳ server/ - Lógica do servidor TCP/IP
- ⏳ dao/ - Acesso a dados MySQL
- ⏳ model/ - Modelos de dados
- ⏳ util/ - Utilitários (Database, Security)

---

## 🗄️ Banco de Dados

### Script SQL
- **[database/finanza.sql](database/finanza.sql)** - Script de criação do banco MySQL
  - Estrutura de tabelas
  - Relacionamentos
  - Dados iniciais

---

## 🖼️ Imagens e Recursos

### Screenshots
- **[IMAGENS/](IMAGENS/)** - Screenshots do sistema
  - mobile/ - Capturas de tela do app Android
  - desktop/ - Capturas de tela do cliente desktop
  - README.md - Descrição das imagens

---

## 📊 Status da Documentação

### Legenda
- ✅ Completamente documentado em português
- ⏳ Aguardando documentação (template disponível)
- ⚠️ Precisa de revisão

### Cobertura Atual

**Mobile:**
- Models: ✅ 100% (4/4)
- UI Activities: ✅ 33% (2/6)
- Network: ⏳ 0% (0/6)
- DB/DAOs: ⏳ 0% (0/5)
- Utils: ⏳ 0% (0/1)

**Desktop:**
- Cliente: ⏳ 0%
- Servidor: ⏳ 0%

**Documentação Geral:**
- README e guias: ✅ 100%
- Mapeamento: ✅ 100%
- Padrões: ✅ 100%

---

## 🚀 Começando a Desenvolver

### 1. Setup do Ambiente

**Mobile (Android):**
```bash
# Importar projeto no Android Studio
# Configurar SDK Android
# Configurar IP do servidor em ServerClient.java
# Executar no emulador ou dispositivo
```

**Desktop:**
```bash
# Importar no NetBeans ou IntelliJ
# Configurar banco MySQL
# Executar MainServidor.java primeiro
# Depois executar MainCliente.java
```

### 2. Entender a Arquitetura

Leia **[MAPEAMENTO_COMPLETO.md](MAPEAMENTO_COMPLETO.md)** para entender:
- Como as camadas se comunicam
- Fluxo de dados entre mobile e servidor
- Estrutura de cada componente

### 3. Adicionar Novas Funcionalidades

Antes de codificar:
1. Consulte o **MAPEAMENTO_COMPLETO.md** para entender onde encaixa
2. Siga o **GUIA_COMENTARIOS_CODIGO.md** para manter padrões
3. Veja exemplos em Usuario.java, LoginActivity.java, etc.
4. Documente seu código em português

### 4. Manter a Qualidade

- Use o checklist do **GUIA_COMENTARIOS_CODIGO.md**
- Mantenha comentários atualizados
- Siga os padrões estabelecidos
- Teste antes de commitar

---

## 🔧 Ferramentas e Tecnologias

### Mobile
- **Linguagem:** Java (Android)
- **IDE:** Android Studio
- **Banco Local:** Room (SQLite)
- **UI:** XML Layouts
- **Padrão:** MVVM
- **Comunicação:** Sockets TCP/IP

### Desktop
- **Linguagem:** Java (SE)
- **IDE:** NetBeans / IntelliJ IDEA
- **Banco:** MySQL
- **UI:** Swing
- **Padrão:** MVC (Cliente), Servidor TCP/IP
- **Comunicação:** Sockets TCP/IP

---

## 📞 Suporte e Recursos

### Documentos de Referência
- **Arquitetura:** MAPEAMENTO_COMPLETO.md
- **Código:** GUIA_COMENTARIOS_CODIGO.md
- **Histórico:** RESUMO_ALTERACOES.md

### Exemplos de Código Bem Documentado
- app/src/main/java/com/example/finanza/model/Usuario.java
- app/src/main/java/com/example/finanza/model/Conta.java
- app/src/main/java/com/example/finanza/model/Lancamento.java
- app/src/main/java/com/example/finanza/model/Categoria.java
- app/src/main/java/com/example/finanza/MainActivity.java
- app/src/main/java/com/example/finanza/ui/LoginActivity.java

### Para Novos Desenvolvedores
1. Leia README.md
2. Estude MAPEAMENTO_COMPLETO.md
3. Veja exemplos de código comentado
4. Siga GUIA_COMENTARIOS_CODIGO.md ao codificar

---

## 📈 Roadmap de Documentação

### Próximas Prioridades
1. ⏳ Comentar Activities restantes (7 arquivos)
2. ⏳ Comentar pacote Network (6 arquivos)
3. ⏳ Comentar pacote DB (5 arquivos)
4. ⏳ Comentar Desktop Cliente (7 arquivos)
5. ⏳ Comentar Desktop Servidor (14 arquivos)

**Meta:** 100% do código comentado em português

---

## ✨ Contribuindo

Ao adicionar código ou documentação:
- ✅ Mantenha comentários em **português**
- ✅ Siga os **templates** do guia
- ✅ Veja **exemplos** nos arquivos já comentados
- ✅ Use o **checklist** antes de commitar
- ✅ Atualize o **status** neste índice se necessário

---

## 📝 Licença

Software acadêmico desenvolvido para fins educacionais no IFSUL - Campus Venâncio Aires.

---

## 👥 Equipe

**Finanza Team**  
Curso Técnico em Informática Integrado ao Ensino Médio  
IFSUL - Campus Venâncio Aires  
2024

---

**Última atualização:** 2024  
**Versão do Índice:** 1.0

---

**Dica:** Adicione este arquivo aos favoritos do seu navegador ou IDE para acesso rápido a toda documentação!
