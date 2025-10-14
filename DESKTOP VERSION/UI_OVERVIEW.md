# Admin Dashboard UI Overview

## Window Layout

```
┌────────────────────────────────────────────────────────────────────────────┐
│  Finanza Admin - Painel de Administração Completo                     [_][□][X] │
├────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │  Painel de Administração Completo          Admin: João (joao@...) │  │
│  │  Total de usuários: 25                                              │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
├────────────────────────────────────────────────────────────────────────────┤
│  ┌─[Usuários]─┬─[Contas]─┬─[Categorias]─┬─[Movimentações]─────────────┐  │
│  │                                                                      │  │
│  │  [CONTENT AREA - VARIES BY TAB]                                    │  │
│  │                                                                      │  │
│  │                                                                      │  │
│  │                                                                      │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
├────────────────────────────────────────────────────────────────────────────┤
│                                      [Editar Meu Perfil] [Sair]           │
└────────────────────────────────────────────────────────────────────────────┘
```

## Tab 1: Usuários (Users)

```
┌──────────────────────────────────────────────────────────────────────────┐
│ Buscar: [_____________________________] [Filtrar] [Limpar]              │
├──────────────────────────────────────────────────────────────────────────┤
│ ┌────┬──────────────────┬───────────────────────┬──────────────────────┐ │
│ │ ID │ Nome             │ Email                 │ Data de Criação      │ │
│ ├────┼──────────────────┼───────────────────────┼──────────────────────┤ │
│ │ 1  │ João Silva       │ joao@gmail.com       │ N/A                  │ │
│ │ 2  │ Maria Santos     │ maria@gmail.com      │ N/A                  │ │
│ │ 3  │ Pedro Costa      │ pedro@gmail.com      │ N/A                  │ │
│ │... │ ...              │ ...                  │ ...                  │ │
│ └────┴──────────────────┴───────────────────────┴──────────────────────┘ │
├──────────────────────────────────────────────────────────────────────────┤
│ [Atualizar Lista] [Editar] [Excluir]                                    │
└──────────────────────────────────────────────────────────────────────────┘
```

**Features:**
- Search by name, email, or ID
- Select user to enable Edit/Delete buttons
- Double-click to edit
- Can't delete self

## Tab 2: Contas (Accounts)

```
┌──────────────────────────────────────────────────────────────────────────┐
│ Filtrar por usuário: [Todos ▼] [Filtrar]                               │
├──────────────────────────────────────────────────────────────────────────┤
│ ┌────┬─────────────┬──────────────┬───────────────┬──────────────────┐  │
│ │ ID │ Nome        │ Tipo         │ Saldo Inicial │ ID Usuário       │  │
│ ├────┼─────────────┼──────────────┼───────────────┼──────────────────┤  │
│ │ 1  │ Conta Principal │ corrente │ 1000.00      │ 1                │  │
│ │ 2  │ Poupança    │ poupanca     │ 5000.00       │ 1                │  │
│ │ 3  │ Nubank      │ cartao       │ 0.00          │ 2                │  │
│ │... │ ...         │ ...          │ ...           │ ...              │  │
│ └────┴─────────────┴──────────────┴───────────────┴──────────────────┘  │
├──────────────────────────────────────────────────────────────────────────┤
│ [Atualizar Lista] [Editar] [Excluir]                                    │
└──────────────────────────────────────────────────────────────────────────┘
```

**Features:**
- Filter by specific user or view all
- Edit: Name, Type (dropdown), Initial Balance
- Delete with cascade warning
- Shows which user owns each account

## Tab 3: Categorias (Categories)

```
┌──────────────────────────────────────────────────────────────────────────┐
│ Filtrar por usuário: [Todos ▼] [Filtrar]                               │
├──────────────────────────────────────────────────────────────────────────┤
│ ┌────┬─────────────────┬──────────┬──────────────────────────────────┐  │
│ │ ID │ Nome            │ Tipo     │ ID Usuário                       │  │
│ ├────┼─────────────────┼──────────┼──────────────────────────────────┤  │
│ │ 1  │ Alimentação     │ despesa  │ 1                                │  │
│ │ 2  │ Transporte      │ despesa  │ 1                                │  │
│ │ 3  │ Salário         │ receita  │ 1                                │  │
│ │ 4  │ Lazer           │ despesa  │ 2                                │  │
│ │... │ ...             │ ...      │ ...                              │  │
│ └────┴─────────────────┴──────────┴──────────────────────────────────┘  │
├──────────────────────────────────────────────────────────────────────────┤
│ [Atualizar Lista] [Editar] [Excluir]                                    │
└──────────────────────────────────────────────────────────────────────────┘
```

**Features:**
- Filter by specific user or view all
- Edit: Name, Type (receita/despesa dropdown)
- Delete with confirmation
- See all categories across all users

## Tab 4: Movimentações (Transactions)

```
┌──────────────────────────────────────────────────────────────────────────┐
│ Filtrar por usuário: [Todos ▼] [Filtrar]                               │
├──────────────────────────────────────────────────────────────────────────┤
│ ┌────┬────────┬────────────┬────────────┬────────┬─────────┬──────────┐ │
│ │ ID │ Valor  │ Data       │ Descrição  │ Tipo   │ ID Conta│ID Categ. │ │
│ ├────┼────────┼────────────┼────────────┼────────┼─────────┼──────────┤ │
│ │ 1  │ 500.00 │ 2024-01-15 │ Mercado    │ despesa│ 1       │ 1        │ │
│ │ 2  │ 3000.00│ 2024-01-20 │ Salário    │ receita│ 1       │ 3        │ │
│ │ 3  │ 120.00 │ 2024-01-22 │ Uber       │ despesa│ 2       │ 2        │ │
│ │... │ ...    │ ...        │ ...        │ ...    │ ...     │ ...      │ │
│ └────┴────────┴────────────┴────────────┴────────┴─────────┴──────────┘ │
├──────────────────────────────────────────────────────────────────────────┤
│ [Atualizar Lista] [Editar] [Excluir]                                    │
└──────────────────────────────────────────────────────────────────────────┘
```

**Features:**
- Filter by specific user or view all
- Edit: All fields (value, date, description, type, account ID, category ID)
- Delete with confirmation
- See all financial transactions

## Edit Dialogs

### Edit User Dialog
```
┌─────────────────────────────────┐
│ Editar Usuário          [X]     │
├─────────────────────────────────┤
│ Nome:     [___________________] │
│ Email:    [___________________] │
│ Senha:    [___________________] │
│          (deixe vazio para não  │
│           alterar)              │
│                                 │
│        [Salvar]  [Cancelar]     │
└─────────────────────────────────┘
```

### Edit Account Dialog
```
┌─────────────────────────────────┐
│ Editar Conta            [X]     │
├─────────────────────────────────┤
│ Nome:          [_____________]  │
│ Tipo:          [corrente ▼]     │
│ Saldo Inicial: [_____________]  │
│                                 │
│        [OK]  [Cancelar]         │
└─────────────────────────────────┘
```

### Edit Category Dialog
```
┌─────────────────────────────────┐
│ Editar Categoria        [X]     │
├─────────────────────────────────┤
│ Nome: [___________________]     │
│ Tipo: [despesa ▼]               │
│                                 │
│        [OK]  [Cancelar]         │
└─────────────────────────────────┘
```

### Edit Transaction Dialog
```
┌─────────────────────────────────┐
│ Editar Movimentação     [X]     │
├─────────────────────────────────┤
│ Valor:       [_______________]  │
│ Data:        [YYYY-MM-DD____]   │
│ Descrição:   [_______________]  │
│ Tipo:        [despesa ▼]        │
│ ID Conta:    [_______________]  │
│ ID Categoria:[_______________]  │
│                                 │
│        [OK]  [Cancelar]         │
└─────────────────────────────────┘
```

## Delete Confirmation
```
┌─────────────────────────────────────────────┐
│ Confirmar Exclusão                    [X]   │
├─────────────────────────────────────────────┤
│ Tem certeza que deseja excluir:            │
│                                             │
│ [Item details displayed here]               │
│                                             │
│ Esta ação não pode ser desfeita!           │
│                                             │
│              [Sim]  [Não]                   │
└─────────────────────────────────────────────┘
```

## Key UI Principles

1. **Consistency**: All tabs follow the same layout pattern
2. **Clarity**: Clear labels and intuitive controls
3. **Safety**: Confirmation dialogs for destructive actions
4. **Feedback**: Success/error messages after operations
5. **Simplicity**: Minimal clicks to perform actions
6. **Efficiency**: Filter options to narrow down data
7. **Accessibility**: Keyboard shortcuts (Enter to filter/confirm)

## Color Coding

- **Red buttons**: Destructive actions (Delete)
- **Blue/Default buttons**: Safe actions (Edit, Refresh)
- **Disabled buttons**: Actions not available (no selection)
- **Warning dialogs**: Yellow/Orange background for delete confirmations

## Responsive Behavior

- Tables resize with window
- Horizontal scrolling for wide tables (transactions)
- Minimum window size: 1200x750
- All dialogs are modal and centered
