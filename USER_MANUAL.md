# 📖 Manual do Usuário - Finanza

## 📋 Índice

1. [Visão Geral](#-visão-geral)
2. [Primeiros Passos](#-primeiros-passos)
3. [Interface do Mobile](#-interface-do-mobile)
4. [Interface do Desktop](#-interface-do-desktop)
5. [Funcionalidades Principais](#-funcionalidades-principais)
6. [Sincronização](#-sincronização)
7. [Relatórios e Análises](#-relatórios-e-análises)
8. [Dicas e Truques](#-dicas-e-truques)
9. [Perguntas Frequentes](#-perguntas-frequentes)

## 🌟 Visão Geral

### O que é o Finanza?

O **Finanza** é um sistema completo de controle financeiro pessoal que oferece:

- 📱 **App Mobile (Android)**: Acesso portátil às suas finanças
- 🖥️ **Sistema Desktop**: Gestão avançada com relatórios completos
- 🔄 **Sincronização Automática**: Dados sempre atualizados entre dispositivos
- 💾 **Modo Offline**: Funciona mesmo sem internet
- 📊 **Relatórios Detalhados**: Análises completas dos seus gastos

### Por que usar o Finanza?

- ✅ **Controle Total**: Gerencie receitas, despesas, contas e categorias
- ✅ **Multiplataforma**: Use no celular e no computador
- ✅ **Sempre Disponível**: Funciona offline com sincronização automática
- ✅ **Seguro**: Seus dados ficam no seu controle
- ✅ **Gratuito**: Sistema open-source sem custos

## 🚀 Primeiros Passos

### 📱 Configuração Inicial do Mobile

#### 1. **Instalação e Primeiro Acesso**

1. Instale o app Finanza no seu Android
2. Abra o aplicativo
3. Na tela inicial, você verá opções de **Login** e **Criar Conta**

#### 2. **Criando sua Primeira Conta**

![Tela de Registro](screenshots/mobile_register.png)

1. Toque em **"Criar Nova Conta"**
2. Preencha os dados:
   ```
   Nome Completo: João Silva
   Email: joao@exemplo.com
   Senha: MinhaSenh@123
   Confirmar Senha: MinhaSenh@123
   ```
3. Toque em **"Registrar"**
4. Aguarde confirmação e você será direcionado ao dashboard

#### 3. **Configuração de Dados Iniciais**

Após criar sua conta, configure:

**a) Sua Primeira Conta Bancária:**
1. Vá em **"Contas"** (ícone na navegação inferior)
2. Toque no **"+"** para adicionar nova conta
3. Preencha:
   ```
   Nome: Conta Corrente Banco X
   Tipo: Corrente
   Saldo Inicial: R$ 1.500,00
   ```

**b) Categorias de Receita:**
1. Vá em **Menu → Categorias**
2. Adicione categorias como:
   - Salário (cor verde)
   - Freelance (cor azul)
   - Investimentos (cor dourada)

**c) Categorias de Despesa:**
1. Continue em **Categorias**
2. Adicione categorias como:
   - Alimentação (cor laranja)
   - Transporte (cor vermelha)
   - Saúde (cor rosa)
   - Lazer (cor roxa)

### 🖥️ Configuração do Desktop (Opcional)

Se você deseja usar a sincronização com desktop:

#### 1. **Instalação do Servidor**
1. Siga o [Guia de Instalação](SETUP_GUIDE.md)
2. Configure o MySQL
3. Inicie o servidor desktop

#### 2. **Conexão Mobile ↔ Desktop**
1. No mobile, vá em **Menu → Configurações → Servidor**
2. Configure:
   ```
   IP do Servidor: 192.168.1.100
   Porta: 8080
   ```
3. Toque em **"Testar Conexão"**
4. Se aparecer 🟢 **"Conectado"**, está funcionando!

## 📱 Interface do Mobile

### 🏠 Dashboard Principal

![Dashboard Mobile](screenshots/mobile_dashboard.png)

#### **Visão Geral da Tela**

1. **Cabeçalho**
   - 👁️ Botão para ocultar/mostrar valores
   - 💰 Saldo total atual
   - 📈 Total de receitas do mês
   - 📉 Total de despesas do mês

2. **Resumo de Contas**
   - Lista suas contas com saldos atuais
   - Cores indicam saldo positivo (verde) ou negativo (vermelho)
   - Toque para ir ao gerenciamento de contas

3. **Principais Categorias**
   - Mostra as 5 categorias com maiores gastos
   - Valores em vermelho (despesas)
   - Ordenadas por valor

4. **Transações Recentes**
   - Últimas 5 movimentações
   - Ícones indicam tipo (↗️ receita, ↘️ despesa)
   - Toque para ver todas as movimentações

#### **Navegação Inferior**

- 🏠 **Home**: Dashboard principal
- 💳 **Contas**: Gerenciar contas bancárias
- 📊 **Movimentações**: Ver e gerenciar transações
- ➕ **Adicionar**: Botão flutuante para nova transação
- 📋 **Menu**: Configurações e outras opções

### ➕ Adicionando Transações

#### **Processo Rápido**

![Adicionar Transação](screenshots/mobile_add_transaction.png)

1. **Na tela principal, toque no botão ➕**
2. **Escolha o tipo:**
   - 📈 **Receita**: Dinheiro que entra
   - 📉 **Despesa**: Dinheiro que sai

3. **Preencha os dados:**
   ```
   Descrição: Almoço no restaurante
   Conta: Conta Corrente (toque para selecionar)
   Data: 15/03/2024 (toque para abrir calendário)
   Categoria: Alimentação (toque para selecionar)
   Valor: 45,90
   ```

4. **Toque em "Salvar"**

#### **Validações Automáticas**

O app verifica automaticamente:
- ✅ Todos os campos obrigatórios preenchidos
- ✅ Valor maior que zero
- ✅ Conta e categoria selecionadas
- ✅ Formato de valor correto (aceita vírgula ou ponto)

### 💳 Gerenciamento de Contas

![Gerenciar Contas](screenshots/mobile_accounts.png)

#### **Visualizar Contas**
- Lista todas suas contas
- Mostra nome, tipo e saldo atual
- Cores indicam status financeiro

#### **Adicionar Nova Conta**
1. Toque no **"+"**
2. Preencha:
   ```
   Nome: Conta Poupança
   Tipo: Poupança
   Saldo Inicial: R$ 5.000,00
   ```

#### **Editar Conta**
1. Toque na conta desejada
2. Modifique os dados necessários
3. Salve as alterações

### 📊 Movimentações

![Lista de Movimentações](screenshots/mobile_movements.png)

#### **Visualização**
- Lista cronológica das transações
- Filtros por período, conta ou categoria
- Busca por descrição
- Cores diferentes para receitas e despesas

#### **Ações Disponíveis**
- ✏️ **Editar**: Modificar transação existente
- 🗑️ **Excluir**: Remover transação
- 👁️ **Detalhes**: Ver informações completas

### 📋 Menu e Configurações

#### **Opções Disponíveis**
- 🏷️ **Categorias**: Gerenciar categorias
- 📈 **Relatórios**: Ver análises (se implementado)
- 👤 **Perfil**: Dados do usuário
- ⚙️ **Configurações**: Preferências do app
- 🔄 **Sincronização**: Status e configurações de sync
- 🚪 **Sair**: Logout da conta

## 🖥️ Interface do Desktop

### 🏠 Dashboard Desktop

![Dashboard Desktop](screenshots/desktop_dashboard.png)

#### **Funcionalidades Avançadas**

1. **Gráficos e Relatórios**
   - Gráficos de pizza para categorias
   - Gráficos de linha para evolução temporal
   - Comparativos mensais e anuais

2. **Exportação de Dados**
   - Relatórios em PDF
   - Planilhas Excel/CSV
   - Dados formatados para impressão

3. **Gerenciamento Avançado**
   - Backup e restore de dados
   - Importação de dados bancários
   - Configurações avançadas de servidor

### 🔄 Monitor de Sincronização

![Monitor Sync](screenshots/desktop_sync_monitor.png)

- **Status das Conexões**: Clientes mobile conectados
- **Log de Atividades**: Histórico de sincronizações
- **Resolução de Conflitos**: Interface para resolver conflitos manuais
- **Estatísticas**: Dados de uso e performance

## 💡 Funcionalidades Principais

### 1. **Controle de Receitas e Despesas**

#### **Receitas - Exemplos Práticos**

**Salário Mensal:**
```
Descrição: Salário Março 2024
Categoria: Salário
Conta: Conta Corrente
Valor: R$ 4.500,00
Data: 01/03/2024
```

**Freelance:**
```
Descrição: Projeto Website Cliente X
Categoria: Freelance
Conta: Conta Corrente
Valor: R$ 1.200,00
Data: 15/03/2024
```

#### **Despesas - Exemplos Práticos**

**Compras do Mercado:**
```
Descrição: Compras da semana
Categoria: Alimentação
Conta: Cartão de Débito
Valor: R$ 180,50
Data: 12/03/2024
```

**Combustível:**
```
Descrição: Abastecimento posto BR
Categoria: Transporte
Conta: Conta Corrente
Valor: R$ 75,30
Data: 10/03/2024
```

### 2. **Sistema de Categorização**

#### **Categorias Recomendadas**

**Receitas:**
- 💼 **Salário**: Renda principal do trabalho
- 💻 **Freelance**: Trabalhos extras
- 📈 **Investimentos**: Rendimentos de aplicações
- 🎁 **Extras**: Presentes, bonificações
- 🏠 **Aluguéis**: Se você tem imóveis para alugar

**Despesas Essenciais:**
- 🏠 **Moradia**: Aluguel, financiamento, condomínio
- ⚡ **Utilidades**: Luz, água, gás, internet
- 🍽️ **Alimentação**: Mercado, restaurantes
- 🚗 **Transporte**: Combustível, transporte público
- 🏥 **Saúde**: Plano de saúde, medicamentos

**Despesas Não-Essenciais:**
- 🎬 **Lazer**: Cinema, teatro, viagens
- 👕 **Vestuário**: Roupas, calçados
- 💄 **Cuidados Pessoais**: Salão, cosméticos
- 📚 **Educação**: Cursos, livros
- 🎮 **Hobbies**: Equipamentos, materiais

### 3. **Gestão de Múltiplas Contas**

#### **Tipos de Conta Sugeridos**

1. **Conta Corrente**
   - Para movimentação diária
   - Recebimento de salário
   - Pagamento de contas

2. **Conta Poupança**
   - Para reserva de emergência
   - Objetivos de médio prazo

3. **Cartão de Crédito**
   - Para compras parceladas
   - Controle de limite

4. **Investimentos**
   - Para aplicações financeiras
   - Acompanhamento de rendimentos

#### **Exemplo de Organização**

```
💳 Conta Corrente Banco X    → R$ 2.500,00
🏦 Poupança Banco X          → R$ 10.000,00
💰 Investimentos CDB         → R$ 25.000,00
💳 Cartão Crédito           → -R$ 800,00 (limite usado)
```

### 4. **Acompanhamento de Saldos**

#### **Saldo Real vs Saldo Projetado**

O Finanza calcula automaticamente:
- **Saldo Inicial**: Valor informado ao criar a conta
- **Movimentações**: Soma de receitas e despesas
- **Saldo Atual**: Saldo inicial + receitas - despesas

#### **Indicadores Visuais**

- 🟢 **Verde**: Saldo positivo, situação boa
- 🟡 **Amarelo**: Saldo baixo, atenção necessária
- 🔴 **Vermelho**: Saldo negativo, ação necessária

## 🔄 Sincronização

### 📶 Status de Conexão

No mobile, observe os indicadores:
- 🟢 **Verde**: Conectado ao servidor, sincronização ativa
- 🟡 **Amarelo**: Conectando ou sincronizando
- 🔴 **Vermelho**: Desconectado, modo offline

### 🔄 Como Funciona a Sincronização

#### **Automática**
- Ocorre a cada nova transação
- A cada 30 segundos (quando conectado)
- Ao abrir o aplicativo
- Ao retornar do segundo plano

#### **Manual**
1. Vá em **Menu → Configurações → Sincronização**
2. Toque em **"Sincronizar Agora"**
3. Aguarde confirmação

### ⚠️ Resolução de Conflitos

Quando o mesmo dado é modificado no mobile e desktop simultaneamente:

1. **Automática**: O sistema usa o timestamp mais recente
2. **Manual**: Interface permite escolher qual versão manter
3. **Backup**: Versão "perdedora" fica salva para recuperação

### 📱 Modo Offline

O mobile funciona completamente offline:
- ✅ Adicionar transações
- ✅ Editar transações existentes
- ✅ Gerenciar contas e categorias
- ✅ Ver relatórios básicos
- 🔄 Sincroniza automaticamente quando reconectar

## 📊 Relatórios e Análises

### 📈 Relatórios Básicos (Mobile)

#### **Dashboard Inteligente**
- **Saldo Total**: Soma de todas as contas
- **Este Mês**: Receitas vs Despesas do mês atual
- **Principais Gastos**: Top 5 categorias de despesa
- **Últimas Transações**: Histórico recente

### 📊 Relatórios Avançados (Desktop)

#### **Relatório Mensal**
- Comparação receitas vs despesas
- Evolução do patrimônio
- Gastos por categoria (gráfico pizza)
- Tendências de crescimento

#### **Relatório Anual**
- Visão consolidada do ano
- Meses com maior e menor gasto
- Categorias que mais cresceram
- Análise de padrões

#### **Relatórios Personalizados**
- Filtros por período específico
- Seleção de contas ou categorias
- Exportação em múltiplos formatos
- Agendamento de relatórios

### 📤 Exportação de Dados

#### **Formatos Disponíveis**
- **PDF**: Relatórios formatados para impressão
- **Excel**: Planilhas com dados brutos
- **CSV**: Dados para importação em outros sistemas
- **HTML**: Relatórios para web

#### **Como Exportar (Desktop)**
1. Vá em **Relatórios → Exportar**
2. Selecione o período desejado
3. Escolha o formato
4. Clique em **"Gerar Relatório"**
5. Salve o arquivo gerado

## 💡 Dicas e Truques

### 🎯 Organização Financeira

#### **1. Categorização Consistente**
- Use sempre as mesmas categorias
- Seja específico mas não exagere no detalhamento
- Revise categorias mensalmente

#### **2. Registro Imediato**
- Adicione transações assim que acontecem
- Use o mobile para registros rápidos
- Configure lembretes se necessário

#### **3. Revisão Regular**
- Confira os dados semanalmente
- Compare com extratos bancários
- Ajuste categorias se necessário

### 💰 Controle de Gastos

#### **1. Estabeleça Limites**
- Defina orçamento por categoria
- Use o relatório mensal como base
- Monitore gastos principais

#### **2. Identifique Padrões**
- Observe categorias que mais crescem
- Identifique gastos desnecessários
- Aproveite os relatórios do desktop

#### **3. Metas e Objetivos**
- Use categorias específicas para metas
- Monitore progresso regularmente
- Celebre conquistas

### 🔧 Uso Eficiente do App

#### **1. Atalhos do Mobile**
- Toque longo na navegação para acesso rápido
- Use a busca para encontrar transações
- Configure notificações importantes

#### **2. Sincronização Otimizada**
- Mantenha mobile e desktop conectados na mesma rede
- Use Wi-Fi sempre que possível
- Verifique logs de sincronização no desktop

#### **3. Backup Regular**
- Configure backup automático no desktop
- Exporte dados periodicamente
- Mantenha cópia de segurança dos dados

## ❓ Perguntas Frequentes

### 📱 **Sobre o Mobile**

**P: O app funciona sem internet?**
R: Sim! O mobile funciona completamente offline. Os dados são sincronizados automaticamente quando você se conecta novamente.

**P: Posso usar em múltiplos celulares?**
R: Atualmente, cada instalação cria uma conta local. Para sincronizar entre dispositivos, use o servidor desktop como centralizador.

**P: Como fazer backup dos dados do mobile?**
R: Os dados são sincronizados com o servidor desktop. Mantenha backups regulares do servidor para garantir segurança.

### 🖥️ **Sobre o Desktop**

**P: É necessário usar o desktop?**
R: Não! O mobile funciona independentemente. O desktop oferece recursos avançados como relatórios e sincronização entre dispositivos.

**P: Posso acessar de qualquer computador?**
R: O servidor deve estar rodando em um computador da rede. O cliente desktop pode ser usado de qualquer PC que consiga conectar ao servidor.

**P: Quantos usuários posso ter?**
R: O sistema suporta múltiplos usuários simultâneos. Cada usuário tem seus dados isolados e seguros.

### 🔄 **Sobre Sincronização**

**P: E se eu modificar os mesmos dados no mobile e desktop?**
R: O sistema resolve conflitos automaticamente usando o timestamp mais recente. Versões antigas ficam salvas para recuperação.

**P: A sincronização consome muitos dados?**
R: Não! Apenas dados modificados são transferidos. Em uso normal, consome poucos KB por sincronização.

**P: Posso usar fora de casa?**
R: A sincronização funciona apenas na mesma rede do servidor. Fora de casa, use o modo offline normalmente.

### 🔐 **Sobre Segurança**

**P: Meus dados estão seguros?**
R: Sim! Os dados ficam no seu controle, seja localmente no mobile ou no seu servidor. Não há envio para serviços externos.

**P: Posso recuperar dados excluídos?**
R: Dados excluídos por engano podem ser recuperados do backup do servidor ou dos logs de sincronização.

**P: Alguém pode acessar meus dados?**
R: Apenas quem tem acesso físico aos seus dispositivos ou conhece suas senhas. O sistema não compartilha dados externamente.

### 🛠️ **Problemas Técnicos**

**P: O app está lento, o que fazer?**
R: Verifique espaço de armazenamento, feche outros apps, reinicie o dispositivo. Se persistir, limpe os dados do app e refaça a configuração.

**P: Dados não sincronizam, como resolver?**
R: Verifique conexão de rede, status do servidor, configurações de IP/porta. Consulte a seção de solução de problemas no manual.

**P: Posso importar dados de outros apps?**
R: Atualmente não há importação automática. Você pode adicionar transações manualmente ou desenvolver scripts de importação.

---

## 🎉 Conclusão

O Finanza é uma ferramenta poderosa para controle financeiro pessoal. Com este manual, você está pronto para:

- ✅ Gerenciar suas finanças de forma organizada
- ✅ Acompanhar receitas e despesas em tempo real
- ✅ Usar sincronização entre mobile e desktop
- ✅ Gerar relatórios para análise financeira
- ✅ Manter controle mesmo offline

### 📞 Suporte

Para dúvidas adicionais:
- 📚 Consulte a [documentação completa](README.md)
- 🐛 Reporte problemas no [GitHub Issues](https://github.com/KallebySchultz/Finanza-Mobile/issues)
- 💬 Participe das [discussões](https://github.com/KallebySchultz/Finanza-Mobile/discussions)

**Tenha um excelente controle financeiro com o Finanza! 💰**