# Guia de Teste - Painel de Administração V2

## 🎯 Objetivo

Este guia irá ajudá-lo a testar todas as novas funcionalidades implementadas no painel de administração desktop.

---

## 📋 Pré-requisitos

1. MySQL rodando com banco `finanza_db` configurado
2. Java JDK 8+ instalado
3. Servidor e Cliente compilados

---

## 🚀 Iniciando o Sistema

### 1. Iniciar o Servidor
```bash
cd "DESKTOP VERSION/ServidorFinanza"
java -cp bin:lib/* MainServidor
```

**Esperado**: Mensagem "Servidor iniciado na porta 12345" (ou similar)

### 2. Iniciar o Cliente
```bash
cd "DESKTOP VERSION/ClienteFinanza"
java -cp bin MainCliente
```

**Esperado**: Tela de login aparece

---

## ✅ Teste 1: Registro de Novo Administrador

### Passos:
1. Na tela de login, clique em **"Registrar Admin"**
2. Observe que:
   - O título muda para "Finanza Desktop - Registrar Admin"
   - Um novo campo "Nome" aparece
   - O botão muda para "Criar Conta"
   - O botão inferior muda para "Voltar ao Login"

3. Preencha os campos:
   - **Nome**: Seu Nome Completo
   - **Email**: seuemail@teste.com
   - **Senha**: minhasenha123 (mínimo 6 caracteres)

4. Clique em **"Criar Conta"**

### Resultado Esperado:
- ✅ Mensagem de sucesso: "Administrador registrado com sucesso! Faça login para continuar."
- ✅ Tela volta ao modo de login automaticamente
- ✅ Email já está preenchido no campo

### Testes de Validação:
- ❌ Tentar registrar com senha menor que 6 caracteres → deve mostrar erro
- ❌ Tentar registrar com campos vazios → deve mostrar erro
- ❌ Tentar registrar com email já existente → deve mostrar erro

---

## ✅ Teste 2: Login no Sistema

### Passos:
1. Use o email que acabou de registrar
2. Digite a senha
3. Clique em **"Entrar"**

### Resultado Esperado:
- ✅ Dashboard de administração abre
- ✅ Header mostra seu nome e email
- ✅ Contador "Total de usuários" aparece

---

## ✅ Teste 3: Visualizar Lista de Usuários

### Passos:
1. Observe o dashboard

### Resultado Esperado:
- ✅ Tabela com colunas: ID, Nome, Email, Data de Criação
- ✅ Todos os usuários do banco aparecem listados
- ✅ Header mostra "Total de usuários: X" (onde X é o número total)
- ✅ Botões "Atualizar Lista", "Editar" e "Excluir" visíveis
- ✅ Botões "Editar" e "Excluir" estão desabilitados (cinza)

---

## ✅ Teste 4: Buscar Usuários

### Passos:
1. Digite no campo de busca (por exemplo: parte de um nome)
2. Clique em **"Filtrar"** (ou pressione Enter)

### Resultado Esperado:
- ✅ Apenas usuários que correspondem à busca aparecem
- ✅ Contador muda para "Total de usuários: X (Filtrados: Y)"
- ✅ Busca funciona para: Nome, Email e ID

### Teste Limpar Filtro:
3. Clique em **"Limpar"**

### Resultado Esperado:
- ✅ Todos os usuários voltam a aparecer
- ✅ Contador volta ao formato original
- ✅ Campo de busca é limpo

---

## ✅ Teste 5: Editar Usuário

### Passos:
1. Clique em uma linha da tabela (selecionar usuário)
2. Observe que os botões "Editar" e "Excluir" ficam habilitados (coloridos)
3. Clique no botão **"Editar"**

### Resultado Esperado:
- ✅ Diálogo "Editar Usuário" abre
- ✅ Campos estão preenchidos com dados atuais:
  - ID (não editável, em cinza)
  - Nome
  - Email
  - Nova Senha (vazio, opcional)

### Editando:
4. Altere o nome
5. Altere o email
6. Opcionalmente, digite uma nova senha
7. Clique em **"Salvar"**

### Resultado Esperado:
- ✅ Mensagem "Usuário atualizado com sucesso!"
- ✅ Diálogo fecha
- ✅ Lista de usuários é atualizada automaticamente
- ✅ Alterações aparecem na tabela

### Teste Duplo Clique:
8. Dê duplo clique em um usuário

### Resultado Esperado:
- ✅ Diálogo de edição abre diretamente (atalho)

---

## ✅ Teste 6: Editar Próprio Perfil

### Passos:
1. Clique no botão **"Editar Meu Perfil"** (canto inferior direito)

### Resultado Esperado:
- ✅ Diálogo de edição abre com seus dados
- ✅ Você pode alterar seu nome, email e senha
- ✅ Após salvar, as informações no header são atualizadas

---

## ✅ Teste 7: Excluir Usuário (Outro)

### Passos:
1. Selecione um usuário QUE NÃO SEJA VOCÊ
2. Clique no botão **"Excluir"** (vermelho)

### Resultado Esperado:
- ✅ Diálogo de confirmação aparece mostrando:
  - Nome do usuário
  - Email do usuário
  - Aviso: "Esta ação não pode ser desfeita!"
  
3. Clique em **"Sim"** para confirmar

### Resultado Esperado:
- ✅ Mensagem "Usuário excluído com sucesso!"
- ✅ Usuário desaparece da lista
- ✅ Contador de usuários diminui em 1

---

## ✅ Teste 8: Proteção de Auto-Exclusão

### Passos:
1. Na lista, localize e selecione O SEU PRÓPRIO USUÁRIO
   - Dica: Procure pelo seu email no dashboard header
2. Clique no botão **"Excluir"**

### Resultado Esperado:
- ✅ Mensagem de aviso aparece:
  - "Não é possível excluir o próprio usuário logado."
- ✅ Seu usuário NÃO é excluído
- ✅ Você continua logado no sistema

---

## ✅ Teste 9: Atualizar Lista

### Passos:
1. Clique no botão **"Atualizar Lista"**

### Resultado Esperado:
- ✅ Lista de usuários é recarregada do servidor
- ✅ Todos os usuários atuais aparecem
- ✅ Contador é atualizado

**Quando usar**: Após outro admin fazer alterações, para ver mudanças em tempo real

---

## ✅ Teste 10: Logout

### Passos:
1. Clique no botão **"Sair"** (canto inferior direito)

### Resultado Esperado:
- ✅ Diálogo de confirmação: "Deseja realmente fazer logout?"
2. Clique em **"Sim"**

### Resultado Esperado:
- ✅ Dashboard fecha
- ✅ Tela de login reaparece
- ✅ Campos de login estão vazios

---

## 🐛 Testes de Erro e Validações

### Teste A: Senha Curta no Registro
1. Tente registrar com senha de 5 caracteres ou menos
2. **Esperado**: Mensagem de erro "A senha deve ter no mínimo 6 caracteres"

### Teste B: Campos Vazios no Registro
1. Tente registrar deixando algum campo vazio
2. **Esperado**: Mensagem de erro "Preencha todos os campos"

### Teste C: Campos Vazios na Edição
1. Tente editar um usuário e apagar o nome
2. **Esperado**: Mensagem de erro "O nome não pode estar vazio"

### Teste D: Email Inválido na Edição
1. Tente editar um usuário com email sem "@"
2. **Esperado**: Mensagem de erro "Digite um email válido"

### Teste E: Servidor Desconectado
1. Pare o servidor
2. Tente fazer login
3. **Esperado**: Mensagem "Erro ao conectar ao servidor"

---

## 📊 Checklist Final

Marque cada item testado:

- [ ] Registro de novo admin
- [ ] Login com nova conta
- [ ] Visualização da lista de usuários
- [ ] Contador de usuários funciona
- [ ] Busca/filtro por nome
- [ ] Busca/filtro por email
- [ ] Busca/filtro por ID
- [ ] Limpar filtro
- [ ] Editar usuário via botão
- [ ] Editar usuário via duplo clique
- [ ] Editar próprio perfil
- [ ] Excluir usuário (outro)
- [ ] Proteção: não excluir próprio usuário
- [ ] Confirmação de exclusão
- [ ] Atualizar lista
- [ ] Logout
- [ ] Validações de senha no registro
- [ ] Validações de campos vazios
- [ ] Validações de email

---

## 🎉 Conclusão

Se todos os testes acima passaram, o sistema está funcionando corretamente e pronto para uso!

### Recursos Confirmados:
✅ Registro de admins  
✅ Login seguro  
✅ Listagem de usuários  
✅ Busca e filtro  
✅ Edição de usuários  
✅ Exclusão de usuários  
✅ Proteções de segurança  
✅ Estatísticas básicas  

---

## 🆘 Problemas Comuns

### Problema: "Erro ao conectar ao servidor"
**Solução**: Verifique se o servidor está rodando e se a porta está correta

### Problema: Tabela vazia no dashboard
**Solução**: 
1. Verifique conexão com MySQL
2. Clique em "Atualizar Lista"
3. Verifique se há usuários no banco de dados

### Problema: "Email já cadastrado"
**Solução**: Use um email diferente ou faça login com o existente

### Problema: Botões desabilitados
**Solução**: Selecione um usuário na tabela clicando na linha

---

**Versão do Guia**: 1.0  
**Data**: 14 de Outubro de 2024  
**Compatível com**: Desktop Admin Panel V2.1.0
