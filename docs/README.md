# 📊 Documentação Técnica Completa - Sistema Finanza

## 📋 Sobre Esta Documentação

Esta documentação contém **fluxogramas, diagramas e especificações técnicas completas** do Sistema Finanza. Todos os diagramas foram criados para permitir que **qualquer pessoa entenda o funcionamento completo do software sem necessidade de acessar o código-fonte**.

## 📁 Estrutura da Documentação

### 📄 Documento PDF Principal
- **`Finanza_Sistema_Completo.pdf`** (2.6 MB)
  - Documento PDF completo e profissional
  - Contém TODOS os diagramas em alta resolução
  - Inclui descrições detalhadas de cada componente
  - Ideal para apresentações e documentação formal
  - **Pronto para imprimir ou compartilhar**

### 🖼️ Diagramas Individuais (Pasta `images/`)

#### 1. **Arquitetura do Sistema** (`01_architecture.png` - 395 KB)
Diagrama completo da arquitetura do sistema mostrando:
- ✅ Aplicativo Mobile Android (todas as Activities)
- ✅ Desktop Admin (Java Swing)
- ✅ Servidor Java (FinanzaServer + ClientHandler)
- ✅ Camada DAO (acesso a dados)
- ✅ Banco de dados MySQL
- ✅ Comunicação via TCP/IP Socket (porta 8080)
- ✅ Fluxo de dados entre todos os componentes

#### 2. **Fluxo do Aplicativo Mobile** (`02_mobile_flow.png` - 291 KB)
Fluxograma detalhado do aplicativo Android incluindo:
- ✅ Processo completo de autenticação (Login/Registro)
- ✅ Validação de credenciais
- ✅ Dashboard com resumo financeiro
- ✅ Gestão de contas (listar, adicionar, editar, excluir)
- ✅ Gestão de movimentações (receitas e despesas)
- ✅ Filtros por período e conta
- ✅ Gestão de categorias personalizadas
- ✅ Perfil do usuário e alteração de senha
- ✅ Configurações e logout
- ✅ Serviço de sincronização em background
- ✅ Navegação entre telas

#### 3. **Protocolo do Servidor** (`03_server_protocol.png` - 262 KB)
Diagrama completo do protocolo de comunicação:
- ✅ Mais de 40 comandos suportados
- ✅ Comandos de autenticação: `LOGIN`, `REGISTER`, `LOGOUT`, `CHANGE_PASSWORD`, `RESET_PASSWORD`
- ✅ Comandos de dashboard: `GET_DASHBOARD`, `GET_PERFIL`, `UPDATE_PERFIL`
- ✅ Comandos de contas: `LIST_CONTAS`, `ADD_CONTA`, `UPDATE_CONTA`, `DELETE_CONTA`
- ✅ Comandos de categorias: `LIST_CATEGORIAS`, `LIST_CATEGORIAS_TIPO`, `ADD_CATEGORIA`, `UPDATE_CATEGORIA`, `DELETE_CATEGORIA`
- ✅ Comandos de movimentações: `LIST_MOVIMENTACOES`, `LIST_MOVIMENTACOES_PERIODO`, `LIST_MOVIMENTACOES_CONTA`, `ADD_MOVIMENTACAO`, `UPDATE_MOVIMENTACAO`, `DELETE_MOVIMENTACAO`
- ✅ Comandos administrativos: `LIST_USERS`, `UPDATE_USER`, `UPDATE_USER_PASSWORD`, `DELETE_USER`, `ADMIN_*`
- ✅ Fluxo de processamento de comandos
- ✅ Integração com camada DAO e banco de dados

#### 4. **Aplicativo Desktop Admin** (`04_desktop_admin_flow.png` - 407 KB)
Fluxograma do aplicativo desktop administrativo:
- ✅ Login exclusivo para administradores
- ✅ Verificação de tipo de usuário
- ✅ Dashboard administrativo
- ✅ Listagem de todos os usuários do sistema
- ✅ Visualização de informações dos usuários
- ✅ Edição de dados (nome, email)
- ✅ Alteração de senhas de usuários
- ✅ Edição do próprio perfil do admin
- ✅ Comandos do protocolo utilizados

#### 5. **Esquema do Banco de Dados** (`05_database_schema.png` - 286 KB)
Diagrama ER (Entidade-Relacionamento) completo:
- ✅ **Tabela `usuarios`**: id, nome, email, senha, tipo_usuario, data_criacao
- ✅ **Tabela `contas`**: id, nome, saldo_inicial, usuario_id (FK)
- ✅ **Tabela `categorias`**: id, nome, cor_hex, tipo (receita/despesa)
- ✅ **Tabela `lancamentos`**: id, valor, data, descricao, conta_id (FK), categoria_id (FK), usuario_id (FK), tipo
- ✅ **View `v_saldo_contas`**: calcula saldo atual de cada conta
- ✅ **View `v_resumo_categorias`**: totaliza valores por categoria
- ✅ **View `v_lancamentos_detalhados`**: lançamentos com nomes de conta, categoria e usuário
- ✅ Relacionamentos e chaves estrangeiras
- ✅ Regras de integridade (CASCADE, SET NULL)
- ✅ Triggers de validação

#### 6. **Sequência: Processo de Login** (`06_sequence_login.png` - 253 KB)
Diagrama de sequência detalhado do login:
1. Usuário insere credenciais na interface
2. Cliente faz hash da senha (SHA-256)
3. Envia comando `LOGIN` para o servidor
4. Servidor recebe e processa comando
5. ClientHandler chama UsuarioDAO.findByEmail()
6. DAO consulta banco de dados MySQL
7. Banco retorna dados do usuário
8. Servidor verifica hash da senha
9. Cria sessão do usuário autenticado
10. Envia resposta `OK` com dados do usuário
11. Cliente armazena sessão localmente
12. Navega para dashboard/menu principal

#### 7. **Sequência: Adicionar Transação** (`07_sequence_transaction.png` - 255 KB)
Diagrama de sequência para criar uma transação:
1. Usuário preenche formulário de transação (valor, tipo, categoria, conta)
2. App valida entrada localmente
3. Envia comando `ADD_MOVIMENTACAO` para servidor
4. Servidor parseia comando e extrai parâmetros
5. Valida sessão do usuário logado
6. ClientHandler chama MovimentacaoDAO.insert()
7. DAO executa `INSERT INTO lancamentos`
8. Trigger do banco valida dados (valor não zero, tipo correto)
9. Banco retorna ID da nova transação
10. Servidor envia resposta `OK` com ID
11. Cliente atualiza banco de dados local (SQLite)
12. Atualiza lista de transações na tela
13. Exibe mensagem de sucesso para o usuário

#### 8. **Fluxo de Sincronização** (`08_sync_flow.png` - 350 KB)
Diagrama do processo de sincronização de dados:
- ✅ **Gatilhos de sincronização**:
  - Ação do usuário (adicionar/editar/excluir dados)
  - Timer periódico (sincronização em background)
  - Botão de sincronização manual
- ✅ **Verificação de conexão de rede**
- ✅ **Modo Online**:
  - Obter mudanças pendentes do banco local
  - Enviar alterações para o servidor
  - Receber atualizações do servidor
  - Atualizar banco de dados local
  - Marcar mudanças como sincronizadas
  - Notificar usuário do sucesso
- ✅ **Modo Offline**:
  - Enfileirar mudanças para sincronização posterior
  - Continuar operando offline
  - Armazenar no banco local
  - Sincronizar quando conexão for restaurada

## 🎯 Como Usar Esta Documentação

### Para Apresentações
1. Abra o arquivo **`Finanza_Sistema_Completo.pdf`**
2. Use para apresentar o sistema completo
3. Cada diagrama tem uma página de descrição seguida da imagem em alta resolução

### Para Estudo Individual
1. Comece pelo diagrama de **Arquitetura** (01) para entender a visão geral
2. Estude o fluxo **Mobile** (02) para entender a experiência do usuário
3. Analise o **Protocolo do Servidor** (03) para entender a comunicação
4. Veja o **Desktop Admin** (04) para entender a administração
5. Estude o **Banco de Dados** (05) para entender a estrutura de dados
6. Analise as **Sequências** (06, 07) para entender fluxos específicos
7. Entenda a **Sincronização** (08) para ver como os dados são mantidos consistentes

### Para Desenvolvimento
- 📚 **[MAPEAMENTO-SOFTWARE.md](../MAPEAMENTO-SOFTWARE.md)** - Consulte este guia completo de todos os 48 arquivos do projeto
  - Documentação detalhada de cada arquivo (Desktop Cliente, Servidor e Mobile)
  - "O que é?", "O que faz?", "Pontos importantes" para cada componente
  - Ideal para entender o código e arquitetura interna
- Use os diagramas como **referência durante o desenvolvimento**
- Consulte o **Protocolo do Servidor** ao implementar novos comandos
- Use o **Esquema do Banco** ao criar queries ou modificar estrutura
- Consulte os **Diagramas de Sequência** ao implementar funcionalidades

## 📊 Estatísticas da Documentação

- **Total de diagramas**: 8 diagramas profissionais
- **Tamanho total das imagens**: ~2.5 MB
- **Tamanho do PDF**: 2.6 MB
- **Resolução**: 300 DPI (alta qualidade para impressão)
- **Comandos documentados**: 40+ comandos do protocolo
- **Tabelas do banco**: 4 tabelas principais + 3 views
- **Activities mobile**: 8 activities principais
- **Componentes**: Mobile, Server, Desktop Admin, Database

## 🛠️ Tecnologias Utilizadas no Sistema

### Backend (Servidor)
- **Java JDK 17+**
- **MySQL** - Banco de dados principal
- **Sockets TCP/IP** - Comunicação cliente-servidor
- **JDBC** - Conexão com banco de dados
- **Padrão DAO** - Acesso a dados
- **Multi-threading** - Thread por cliente conectado

### Mobile (Android)
- **Android SDK** (Java + XML)
- **SQLite** - Banco de dados local
- **Room Database** (opcional)
- **Padrão MVVM**
- **Sockets TCP/IP** - Comunicação com servidor
- **Background Services** - Sincronização

### Desktop Admin
- **Java Swing** - Interface gráfica
- **Padrão MVC**
- **Sockets TCP/IP** - Comunicação com servidor

## 📖 Funcionalidades Documentadas

### Autenticação e Segurança
- ✅ Login com validação de credenciais
- ✅ Registro de novos usuários
- ✅ Criptografia de senhas (SHA-256)
- ✅ Alteração de senha
- ✅ Reset de senha
- ✅ Sessões de usuário
- ✅ Controle de acesso (usuário comum vs admin)

### Gestão Financeira
- ✅ Dashboard com resumo financeiro
- ✅ Gestão de contas (corrente, poupança, etc.)
- ✅ Gestão de categorias (receitas e despesas)
- ✅ Lançamento de transações
- ✅ Filtros por período e conta
- ✅ Cálculo automático de saldos
- ✅ Relatórios financeiros

### Sincronização
- ✅ Sincronização em tempo real
- ✅ Modo offline (mobile)
- ✅ Sincronização periódica em background
- ✅ Sincronização manual
- ✅ Resolução de conflitos

### Administração
- ✅ Listagem de todos os usuários
- ✅ Edição de dados de usuários
- ✅ Alteração de senhas de usuários
- ✅ Visualização de informações
- ✅ Gestão centralizada

## 📞 Informações do Projeto

- **Projeto**: Finanza - Sistema de Controle Financeiro
- **Tipo**: Trabalho Interdisciplinar
- **Instituição**: IFSUL - Campus Venâncio Aires
- **Curso**: 4º ano Técnico em Informática Integrado ao Ensino Médio
- **Desenvolvedor**: Kalleby Schultz
- **Ano**: 2024

## 📝 Disciplinas Envolvidas

1. **Engenharia e Qualidade de Software** - Planejamento, requisitos, documentação
2. **Gestão e Empreendedorismo** - Modelo de negócios
3. **Linguagem de Programação III** - Versão desktop (Java)
4. **Programação para Dispositivos Móveis** - Versão mobile (Android)
5. **Segurança da Informação** - Autenticação e criptografia

## 🎓 Propósito Acadêmico

Esta documentação foi criada especificamente para:
- ✅ Apresentações do projeto a professores e avaliadores
- ✅ Permitir compreensão completa sem acesso ao código
- ✅ Demonstrar arquitetura e design do sistema
- ✅ Servir como material de estudo e referência
- ✅ Documentar todas as funcionalidades e fluxos
- ✅ Facilitar manutenção e evolução futura do sistema

## 📄 Licença

Este software foi desenvolvido para **fins acadêmicos** no **IFSUL - Campus Venâncio Aires**.
Seu uso é livre para fins educacionais e de aprendizado.

---

**Documentação gerada automaticamente**  
**© 2024 - Finanza System - Todos os direitos reservados**
