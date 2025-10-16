# 📱 Finanza Mobile - Aplicação Android

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Room](https://img.shields.io/badge/Room-4285F4?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white)](https://material.io/)

**Finanza Mobile** é uma aplicação Android moderna para controle financeiro pessoal, desenvolvida com foco em **offline-first** e sincronização inteligente com servidor desktop.

## 🌟 Características Principais

### ✨ **Design Moderno**
- **Material Design 3**: Interface seguindo as diretrizes mais recentes do Google
- **Navegação Intuitiva**: Fluxo de usuário otimizado para produtividade
- **Responsividade**: Adapta-se a diferentes tamanhos de tela
- **Tema Consistente**: Cores e tipografia harmoniosas

### 🔄 **Offline-First**
- **Funcionamento Sem Internet**: Todas as funcionalidades disponíveis offline
- **Banco Local Room**: Armazenamento SQLite otimizado
- **Sincronização Automática**: Dados sincronizados quando conectado
- **Resolução de Conflitos**: Sistema automático baseado em timestamps

### 🔐 **Segurança e Privacidade**
- **Autenticação Local**: Login funciona mesmo offline
- **Criptografia de Senhas**: Hash SHA-256 para armazenamento seguro
- **Validação de Dados**: Verificações no lado cliente
- **Backup Automático**: Dados preservados durante sincronização

## 📸 Interface do Aplicativo

### Tela de Login
![Login Mobile](screenshots/mobile/01-login.png)
*Autenticação com design Material e validação em tempo real*

### Dashboard Principal
![Dashboard Mobile](screenshots/mobile/03-dashboard.png)
*Visão geral completa das finanças com gráficos e resumos*

### Gerenciamento de Transações
![Transações Mobile](screenshots/mobile/04-add-transaction.png)
*Interface intuitiva para adicionar receitas e despesas*

### Gerenciamento de Contas
![Contas Mobile](screenshots/mobile/05-accounts.png)
*Administração completa de contas bancárias e cartões*

## 🚀 Funcionalidades

### ✅ **Implementadas**

#### 💰 **Gestão Financeira**
- [x] **Transações**: Adicionar receitas e despesas
- [x] **Contas**: Gerenciar contas bancárias, cartões e dinheiro
- [x] **Categorias**: Organizar transações por categorias personalizáveis
- [x] **Saldos**: Cálculo automático de saldos por conta
- [x] **Histórico**: Visualização completa de movimentações

#### 📊 **Dashboard**
- [x] **Resumo Financeiro**: Receitas, despesas e saldo atual
- [x] **Transações Recentes**: Últimas movimentações
- [x] **Visualização**: Dados financeiros organizados
- [x] **Filtros**: Busca por período, categoria ou conta

#### 👤 **Gestão de Usuário**
- [x] **Autenticação**: Login e registro de usuários
- [x] **Perfil**: Edição de dados pessoais
- [x] **Configurações**: Personalização do aplicativo
- [x] **Logout**: Encerramento seguro de sessão

#### 🔄 **Sincronização**
- [x] **Conexão TCP**: Comunicação direta com servidor desktop
- [x] **Sincronização Bidirecional**: Dados atualizados em ambos os lados
- [x] **Modo Offline**: Funcionamento independente de conexão
- [x] **Resolução de Conflitos**: Sistema automático por timestamp

## 📋 Requisitos Técnicos

### 📱 **Sistema**
- **Android**: 7.0 (API 24) ou superior
- **RAM**: Mínimo 2GB recomendado
- **Armazenamento**: 100MB livres
- **Conectividade**: Wi-Fi ou dados móveis (opcional para sincronização)

### 🛠️ **Desenvolvimento**
- **Android Studio**: Arctic Fox ou superior
- **Java**: JDK 11
- **Gradle**: 8.0+
- **SDK Target**: Android 14 (API 34)

## 🔧 Tecnologias Utilizadas

### 📚 **Principais Bibliotecas**
```gradle
// Interface do Usuário
implementation 'com.google.android.material:material:1.12.0'
implementation 'androidx.appcompat:appcompat:1.7.1'
implementation 'androidx.constraintlayout:constraintlayout:2.2.1'

// Banco de Dados
implementation 'androidx.room:room-runtime:2.6.1'
annotationProcessor 'androidx.room:room-compiler:2.6.1'

// Arquitetura
implementation 'androidx.lifecycle:lifecycle-viewmodel:2.7.0'
implementation 'androidx.lifecycle:lifecycle-livedata:2.7.0'
```

### 🏗️ **Arquitetura**
- **MVVM**: Model-View-ViewModel pattern
- **Room Database**: ORM para SQLite
- **Repository Pattern**: Abstração de dados
- **LiveData**: Observação reativa de dados

## 📂 Estrutura do Projeto

```
app/src/main/java/com/example/finanza/
├── MainActivity.java              # Atividade principal
├── db/                           # Banco de dados Room
│   ├── AppDatabase.java          # Configuração do banco
│   ├── UsuarioDao.java           # DAO de usuários
│   ├── ContaDao.java             # DAO de contas
│   ├── CategoriaDao.java         # DAO de categorias
│   └── LancamentoDao.java        # DAO de transações
├── model/                        # Entidades do banco
│   ├── Usuario.java              # Modelo de usuário
│   ├── Conta.java                # Modelo de conta
│   ├── Categoria.java            # Modelo de categoria
│   └── Lancamento.java           # Modelo de transação
├── network/                      # Camada de rede
│   ├── AuthManager.java          # Gerenciamento de autenticação
│   ├── SyncService.java          # Serviço de sincronização
│   ├── ServerClient.java         # Cliente TCP
│   └── Protocol.java             # Protocolo de comunicação
├── ui/                           # Interfaces de usuário
│   ├── LoginActivity.java        # Tela de login
│   ├── RegisterActivity.java     # Tela de registro
│   ├── AccountsActivity.java     # Gerenciar contas
│   ├── MovementsActivity.java    # Gerenciar movimentações
│   └── ProfileActivity.java      # Perfil do usuário
└── util/                         # Utilitários
    ├── DateUtils.java            # Utilitários de data
    ├── CurrencyUtils.java        # Formatação de moeda
    └── ValidationUtils.java      # Validação de dados
```

## 🚀 Como Instalar e Usar

### 📲 **Instalação via APK**
1. Baixe o APK mais recente das [Releases](https://github.com/KallebySchultz/Finanza-Mobile/releases)
2. Ative "Fontes desconhecidas" nas configurações do Android
3. Instale o APK baixado
4. Abra o aplicativo e crie sua conta

### 🔧 **Instalação para Desenvolvimento**

#### 1. **Clone o Repositório**
```bash
git clone https://github.com/KallebySchultz/Finanza-Mobile.git
cd Finanza-Mobile
```

#### 2. **Abra no Android Studio**
- Inicie o Android Studio
- Selecione "Open an existing project"
- Navegue até a pasta clonada
- Aguarde a sincronização do Gradle

#### 3. **Configure um Dispositivo**
- **Emulador**: Configure um AVD com Android 7.0+
- **Dispositivo físico**: Ative depuração USB

#### 4. **Execute o Projeto**
- Clique em "Run" (▶️) ou pressione `Shift + F10`
- Selecione o dispositivo de destino
- Aguarde a instalação e execução

### 📱 **Primeiro Uso**

#### 1. **Registro de Usuário**
- Abra o aplicativo
- Toque em "Criar nova conta"
- Preencha nome, email e senha
- Confirme o registro

#### 2. **Configuração Inicial**
- **Primeira conta**: Adicione uma conta bancária ou cartão
- **Categorias**: Crie categorias para receitas e despesas
- **Sincronização**: Configure servidor (opcional)

#### 3. **Uso Diário**
- **Adicionar transação**: Toque no botão "+" na tela principal
- **Visualizar saldo**: Dashboard mostra resumo atualizado
- **Gerenciar dados**: Acesse contas e categorias pelo menu

## ⚙️ Configurações Avançadas

### 🔄 **Sincronização com Desktop**

#### 1. **Configurar Servidor**
- Abra as Configurações do app
- Toque em "Servidor"
- Insira o IP do computador com o servidor desktop
- Defina a porta (padrão: 8080)

#### 2. **Testar Conexão**
- Toque em "Testar Conexão"
- Aguarde confirmação de conectividade
- Ative sincronização automática se desejado

#### 3. **Sincronização Manual**
- Puxe para baixo na tela principal para atualizar
- Ou acesse Configurações → Sincronizar agora

### 🎨 **Personalização**

#### **Tema**
- Configurações → Aparência
- Escolha tema claro ou escuro
- Personalize cores de destaque

#### **Moeda**
- Configurações → Regional
- Selecione moeda padrão
- Configure formato de números

## 🐛 Solução de Problemas

### ❌ **Problemas Comuns**

#### **App não inicia**
```
✓ Verifique se o dispositivo atende aos requisitos mínimos
✓ Reinicie o dispositivo
✓ Limpe cache do aplicativo
✓ Reinstale o aplicativo
```

#### **Sincronização não funciona**
```
✓ Verifique conexão Wi-Fi/dados móveis
✓ Confirme IP e porta do servidor
✓ Teste conectividade (ping)
✓ Verifique se servidor desktop está ativo
```

#### **Dados perdidos**
```
✓ Dados ficam salvos localmente mesmo offline
✓ Faça sincronização manual se necessário
✓ Verifique se backup automático está ativo
✓ Contate suporte se problema persistir
```

### 📊 **Logs e Depuração**

#### **Ativar logs detalhados**
1. Configurações → Desenvolvedor
2. Ative "Logs detalhados"
3. Reproduza o problema
4. Envie logs via "Reportar problema"

#### **Informações do sistema**
- Configurações → Sobre
- Versão do app, Android, modelo do dispositivo
- Use estas informações ao reportar bugs

## 🔒 Segurança e Privacidade

### 🛡️ **Proteção de Dados**
- **Criptografia local**: Senhas são criptografadas com SHA-256
- **Validação**: Todos os dados são validados antes do armazenamento
- **Backup seguro**: Sincronização preserva integridade dos dados
- **Sem telemetria**: Nenhum dado pessoal é enviado para terceiros

### 🔐 **Boas Práticas**
- Use senhas fortes e únicas
- Mantenha o aplicativo atualizado
- Configure sincronização apenas em redes confiáveis
- Faça backup regular dos dados

## 🔄 Atualizações e Versões

### 📋 **Histórico de Versões**

#### **v1.0.0** (Atual)
- ✅ Funcionalidades principais implementadas
- ✅ Interface Material Design
- ✅ Sincronização com desktop
- ✅ Modo offline completo

#### **Próximas Versões**
- 🔜 **v1.1.0**: Melhorias de interface e gráficos avançados
- 🔜 **v1.2.0**: Notificações e lembretes
- 🔜 **v1.3.0**: Suporte a múltiplos idiomas

### 🔄 **Como Atualizar**
- **Play Store**: Atualizações automáticas (quando disponível)
- **APK direto**: Baixe nova versão e instale sobre a atual
- **Desenvolvimento**: `git pull` e rebuild no Android Studio

## 🤝 Contribuição e Suporte

### 🐛 **Reportar Bugs**
1. Vá para [Issues](https://github.com/KallebySchultz/Finanza-Mobile/issues)
2. Clique em "New Issue"
3. Escolha template "Bug Report"
4. Preencha informações detalhadas

### 💡 **Sugerir Funcionalidades**
1. Acesse [Issues](https://github.com/KallebySchultz/Finanza-Mobile/issues)
2. Clique em "New Issue"
3. Escolha template "Feature Request"
4. Descreva a funcionalidade desejada

### 🔧 **Contribuir com Código**
1. Fork o projeto
2. Crie branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para branch (`git push origin feature/MinhaFeature`)
5. Abra Pull Request

## 📄 Licença

Este projeto está licenciado sob a [MIT License](LICENSE) - veja o arquivo de licença para detalhes.

## 📞 Contato e Suporte

- 🐛 **Issues**: [GitHub Issues](https://github.com/KallebySchultz/Finanza-Mobile/issues)
- 💬 **Discussões**: [GitHub Discussions](https://github.com/KallebySchultz/Finanza-Mobile/discussions)
- 📧 **Email**: [finanza.support@exemplo.com](mailto:finanza.support@exemplo.com)

---

<div align="center">

**📱 Desenvolvido com ❤️ para Android**

[⬆ Voltar ao topo](#-finanza-mobile---aplicação-android)

</div>