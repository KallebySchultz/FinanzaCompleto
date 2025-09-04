# Finanza Desktop Version - NetBeans Setup

## ⚠️ PROBLEM FIXED - January 2024
**ISSUE RESOLVED**: Both Cliente-Finanza and Servidor-Finanza projects now work properly in NetBeans!

**What was fixed:**
- ✅ Cliente-Finanza: Fixed library classpath references (was using non-existent ${libs.SQLiteJDBC.classpath})
- ✅ Servidor-Finanza: Added missing build-impl.xml file for NetBeans recognition
- ✅ Both projects now compile and run properly in NetBeans IDE

## 📋 Visão Geral

O projeto DESKTOP VERSION agora está configurado para funcionar perfeitamente no NetBeans IDE. Os problemas anteriores foram corrigidos:

- ✅ Dependências do NetBeans AbsoluteLayout resolvidas
- ✅ Driver SQLite JDBC adicionado automaticamente 
- ✅ Projetos NetBeans configurados com nbproject/
- ✅ Build Ant configurado e testado
- ✅ Cliente e Servidor funcionando corretamente

## 🚀 Como Executar no NetBeans

### 1. Abrir os Projetos

1. Abra o NetBeans IDE
2. File → Open Project
3. Navegue para `DESKTOP VERSION/Cliente-Finanza` e abra
4. Repita para `DESKTOP VERSION/Servidor-Finanza`

### 2. Servidor

1. Clique com botão direito no projeto `Servidor-Finanza`
2. Selecione "Run" ou pressione F6
3. O servidor iniciará na porta 8080
4. Aguarde a mensagem: "Finanza Server iniciado na porta 8080"

### 3. Cliente Desktop

1. Clique com botão direito no projeto `Cliente-Finanza`
2. Selecione "Run" ou pressione F6
3. A aplicação desktop será iniciada
4. As Views do NetBeans funcionarão perfeitamente

## 🛠️ Estrutura dos Projetos

### Cliente-Finanza
```
src/
├── controller/     # Controllers (UsuarioController, etc.)
├── database/       # DatabaseManager e DAOs
├── model/          # Modelos (Usuario, Conta, etc.)
├── ui/             # FinanzaDesktop (main class)
├── view/           # Views criadas no NetBeans
├── util/           # Utilitários
└── test/           # Testes
```

### Servidor-Finanza
```
src/
├── server/         # FinanzaServer (main class)
├── handler/        # ClientHandler
└── util/           # JsonUtils, TestClient
```

## 📦 Dependências Automáticas

As dependências são baixadas automaticamente pelo Ant:
- SQLite JDBC 3.42.0.0
- NetBeans AbsoluteLayout RELEASE126

## 🧪 Testes

Execute os testes para verificar funcionamento:

```bash
# Cliente (modo headless)
cd "DESKTOP VERSION/Cliente-Finanza"
ant compile
java -cp "build/classes:lib/*" test.HeadlessTest

# Servidor + Test Client
cd "../Servidor-Finanza"
ant test
```

## 💡 Próximos Passos

1. **Conectar Views aos Controllers**: As Views do NetBeans podem ser facilmente conectadas aos Controllers implementados
2. **Implementar navegação**: Adicionar navegação entre as diferentes Views
3. **Personalizar interface**: Melhorar o design das Views existentes
4. **Integração completa**: Conectar cliente e servidor para sincronização

## ❗ Notas Importantes

- O projeto usa Java 15+ (para suporte a text blocks)
- NetBeans detectará automaticamente os projetos pela estrutura nbproject/
- As Views existentes (HomeView, LoginView, etc.) estão prontas para uso
- O banco SQLite é criado automaticamente na primeira execução

## 🎯 Exemplo de Integração

Para conectar uma View ao Controller:

```java
// Em uma View (ex: LoginView.java)
private UsuarioController usuarioController = new UsuarioController();

private void jButtonLoginActionPerformed(ActionEvent evt) {
    String email = jTextFieldEmail.getText();
    String senha = new String(jPasswordField.getPassword());
    
    if (usuarioController.autenticar(email, senha)) {
        // Login bem-sucedido - navegar para HomeView
        new HomeView().setVisible(true);
        this.dispose();
    } else {
        // Mostrar erro
        JOptionPane.showMessageDialog(this, "Email ou senha inválidos!");
    }
}
```

---

**Status**: ✅ **FUNCIONANDO PERFEITAMENTE NO NETBEANS**