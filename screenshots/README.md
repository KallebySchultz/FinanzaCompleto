# 📸 Screenshots - Finanza

Este diretório contém capturas de tela das interfaces do sistema Finanza, incluindo as versões mobile (Android) e desktop (Java).

## 📱 Mobile Screenshots

### Telas Principais
- `mobile/01-login.png` - Tela de login
- `mobile/02-register.png` - Tela de cadastro
- `mobile/03-dashboard.png` - Dashboard principal
- `mobile/04-add-transaction.png` - Adicionar transação
- `mobile/05-accounts.png` - Gerenciar contas
- `mobile/06-categories.png` - Gerenciar categorias
- `mobile/07-movements.png` - Lista de movimentações
- `mobile/08-profile.png` - Perfil do usuário
- `mobile/09-settings.png` - Configurações de sincronização

## 🖥️ Desktop Screenshots

### Interface Principal
- `desktop/01-server-start.png` - Inicialização do servidor
- `desktop/02-client-login.png` - Login do cliente desktop
- `desktop/03-dashboard.png` - Dashboard desktop
- `desktop/04-sync-monitor.png` - Monitor de sincronização
- `desktop/05-reports.png` - Relatórios avançados
- `desktop/06-export.png` - Funcionalidade de exportação

## 🔄 Sincronização

### Fluxo de Dados
- `sync/01-connection-established.png` - Conexão estabelecida
- `sync/02-data-sync.png` - Sincronização de dados
- `sync/03-conflict-resolution.png` - Resolução de conflitos

## 📋 Como Capturar Screenshots

### Mobile (Android)
1. Execute o aplicativo no emulador ou dispositivo
2. Navegue pelas telas principais
3. Capture usando `adb shell screencap` ou ferramenta do Android Studio
4. Salve no formato PNG com nomenclatura padronizada

### Desktop (Java)
1. Inicie o servidor Finanza
2. Execute o cliente desktop
3. Capture as telas usando ferramenta de screenshot do sistema
4. Salve no diretório apropriado

### Configuração Recomendada
- **Resolução**: 1080x1920 (mobile), 1920x1080 (desktop)
- **Formato**: PNG
- **Qualidade**: Alta definição
- **Tamanho**: Otimizado para web (< 1MB por imagem)

## 🎨 Padrões de Screenshot

### Mobile
- Incluir status bar completo
- Mostrar dados realistas (não dados de teste vazios)
- Capturar estados de carregamento quando relevante
- Incluir estados de erro para documentação

### Desktop
- Mostrar janela completa da aplicação
- Incluir múltiplas janelas abertas quando relevante
- Capturar logs do servidor em terminal separado
- Mostrar integração com banco de dados

---

**Nota**: Os screenshots devem ser atualizados a cada release significativa do sistema para manter a documentação sempre atualizada.