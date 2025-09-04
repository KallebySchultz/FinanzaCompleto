# Finanza - Financial Management System

**ALWAYS follow these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.**

## Working Effectively

### Bootstrap and Build the System
```bash
# 1. Server Component (NEVER CANCEL - builds in ~2 seconds)
cd "DESKTOP VERSION/Servidor-Finanza"
ant compile    # Takes ~2 seconds
ant jar        # Takes <1 second
ant info       # Show project information

# 2. Desktop Client Component (NEVER CANCEL - builds in ~3 seconds) 
cd "../Cliente-Finanza"
ant download-dependencies  # Takes ~2 seconds, downloads SQLite JDBC and NetBeans libs
ant compile-with-deps      # Takes ~3 seconds total
```

### Run and Test the System
```bash
# 1. Start Server (NEVER CANCEL - starts immediately)
cd "DESKTOP VERSION/Servidor-Finanza"
ant run        # Server runs on port 8080, press Ctrl+C to stop

# 2. Test Server Communication (in another terminal)
cd "DESKTOP VERSION/Servidor-Finanza"  
ant test-server  # Takes ~1 second, tests all server endpoints

# 3. Test Desktop Client Integration (headless mode)
cd "../Cliente-Finanza"
java -cp "build/classes:lib/*" util.IntegrationTest  # Takes ~2 seconds

# 4. Android App Build (LIMITATION - see notes below)
cd "../../"
./gradlew assembleDebug  # FAILS - requires Android SDK setup
```

### Key Timing Expectations
- **Server compile**: ~2 seconds - NEVER CANCEL
- **Server startup**: Instant - Server listens on port 8080
- **Desktop client dependencies**: ~2 seconds - NEVER CANCEL  
- **Desktop client compile**: ~3 seconds - NEVER CANCEL
- **Integration tests**: ~2 seconds - All tests must pass
- **Android build**: FAILS - Android SDK not available in this environment

## System Architecture

### Components Overview
```
📦 Finanza Complete System
├── 🖧 DESKTOP VERSION/Servidor-Finanza/    # TCP Server (Java + Ant)
├── 🖥️ DESKTOP VERSION/Cliente-Finanza/     # Desktop Client (Java Swing + Ant)
├── 📱 app/                                 # Android App (Java + Gradle) 
├── 🗄️ database/finanza.sql                 # SQLite Database Schema
└── 📖 Documentation files (README.md, etc.)
```

### Technology Stack
- **Java 17** - Primary language (verified working)
- **Apache Ant 1.10.14** - Build system for server/desktop
- **Gradle 8.13** - Build system for Android (Android SDK required)
- **SQLite + JDBC** - Database (auto-created)
- **TCP Sockets** - Server communication on port 8080
- **Java Swing** - Desktop UI (requires graphical environment)

## Critical Commands That Work

### Server Operations
```bash
cd "DESKTOP VERSION/Servidor-Finanza"

# Build and run (ALWAYS works)
ant compile && ant jar && ant run

# Test communication (ALWAYS test after changes)
ant test-server  # Must show ✅ for all tests

# Get project info
ant info
```

### Desktop Client Operations  
```bash
cd "DESKTOP VERSION/Cliente-Finanza"

# Full build sequence (ALWAYS works)
ant download-dependencies && ant compile-with-deps

# Run integration tests (CRITICAL - must pass)
java -cp "build/classes:lib/*" util.IntegrationTest

# Available but fails in headless environment
java -cp "build/classes:lib/*" ui.FinanzaDesktop  # Requires X11 display
```

## Validation Scenarios

### ALWAYS Test After Making Changes
1. **Server Communication Test**: 
   ```bash
   cd "DESKTOP VERSION/Servidor-Finanza"
   ant compile && ant run &  # Start server in background
   sleep 2                   # Let server start
   ant test-server           # Must show all ✅ responses
   pkill -f FinanzaServer    # Stop server
   ```

2. **Desktop Integration Test**:
   ```bash
   cd "DESKTOP VERSION/Cliente-Finanza"
   ant compile-with-deps
   java -cp "build/classes:lib/*" util.IntegrationTest
   # Must show: "✅ TODOS OS TESTES PASSARAM!"
   ```

3. **Database Functionality Test**:
   - Login test: `admin@finanza.com` / `admin` (default user)
   - Account creation and transaction processing
   - SQLite database auto-creation and schema validation

### Expected Test Output (Must Match)
**Server Test Output:**
```
=================================================
Finanza Test Client
=================================================
--- Teste de Conexão ---
✅ Conexão estabelecida com o servidor
--- Teste Ping ---
Resposta: {"action":"pong","message":"Servidor ativo","success":true}
--- Teste Login ---
Resposta: {"action":"login","message":"Login realizado com sucesso para: admin@finanza.com","success":true}
--- Teste Sincronização Usuário/Contas/Lançamentos ---
✅ All sync commands working
=================================================
```

**Desktop Integration Test Output:**
```
=================================================
FINANZA INTEGRATION TEST
=================================================
✅ Conexão com banco de dados estabelecida
✅ UsuarioController - Login funcional  
✅ ContaController - X contas encontradas
✅ LancamentoController - Resumo obtido
✅ CategoriaController - X categorias encontradas
✅ Workflow - Login realizado
✅ Workflow - Nova conta criada
✅ Workflow - Nova transação criada
✅ Workflow - Logout realizado
✅ TODOS OS TESTES PASSARAM!
```

## Build System Details

### Ant Build Files Locations
- **Server**: `DESKTOP VERSION/Servidor-Finanza/build.xml`
- **Desktop**: `DESKTOP VERSION/Cliente-Finanza/build.xml`  
- **NetBeans**: Uses `nbproject/build-impl.xml` (auto-generated)

### Dependencies (Auto-Downloaded)
- **SQLite JDBC 3.42.0.0** - Database connectivity
- **NetBeans AbsoluteLayout RELEASE126** - UI layouts
- **No external dependencies for server** - Pure JDK

### Project Structure (Key Directories)
```bash
# Server source code
DESKTOP VERSION/Servidor-Finanza/src/
├── server/           # FinanzaServer main class  
├── handler/          # ClientHandler for connections
└── util/             # JsonUtils, TestClient

# Desktop client source code  
DESKTOP VERSION/Cliente-Finanza/src/
├── controller/       # Business logic (UsuarioController, etc.)
├── database/         # DatabaseManager, DAOs
├── model/            # Data models (Usuario, Conta, etc.)
├── ui/               # FinanzaDesktop main class
├── view/             # NetBeans GUI Views
└── util/             # IntegrationTest, utilities
```

## Limitations and Known Issues

### Android App Build
- **DOES NOT WORK** in this environment
- Requires Android SDK installation
- Gradle fails with: "Plugin 'com.android.application' was not found"
- **DO NOT** attempt Android builds - document as limitation

### Desktop UI 
- **Compiles successfully** but requires graphical environment
- In headless mode: Expected `HeadlessException: No X11 DISPLAY`
- **This is normal** - UI code is functional, just can't display

### Network Dependencies
- **WORKS**: Maven Central repository access for dependencies
- **WORKS**: SQLite JDBC and NetBeans library downloads
- **FAILS**: Android plugin repository access

## Troubleshooting

### Common Issues and Solutions
1. **"Target does not exist"**: Use correct Ant targets listed above
2. **Java warnings about source 15**: Normal, system uses Java 17
3. **Server port in use**: Kill existing Java processes with `pkill -f FinanzaServer`
4. **Dependencies not found**: Run `ant download-dependencies` first
5. **Android build fails**: Expected - requires Android SDK setup

### Debug Commands
```bash
# Check Java version (must be 17+)
java -version

# Check Ant version  
ant -version

# Kill any running servers
pkill -f FinanzaServer

# Clean build artifacts
cd "DESKTOP VERSION/Servidor-Finanza" && ant clean
cd "../Cliente-Finanza" && ant clean
```

## Database Information

### Default User Account (Always Available)
- **Email**: `admin@finanza.com`
- **Password**: `admin`
- **Use this for all login tests**

### Database Auto-Creation
- SQLite database created automatically on first run
- Schema loaded from `database/finanza.sql`
- No manual database setup required

## Development Workflow

### Making Changes to Server Code
1. Edit files in `DESKTOP VERSION/Servidor-Finanza/src/`
2. **ALWAYS** run: `ant compile && ant jar`  
3. **ALWAYS** test: Start server with `ant run`, then `ant test-server`
4. **CRITICAL**: All server tests must pass before committing

### Making Changes to Desktop Code
1. Edit files in `DESKTOP VERSION/Cliente-Finanza/src/`
2. **ALWAYS** run: `ant compile-with-deps`
3. **ALWAYS** test: `java -cp "build/classes:lib/*" util.IntegrationTest`
4. **CRITICAL**: Integration test must show "✅ TODOS OS TESTES PASSARAM!"

### Before Committing Any Changes
```bash
# Full validation sequence (NEVER SKIP)
cd "DESKTOP VERSION/Servidor-Finanza"
ant clean && ant compile && ant jar
ant run &
sleep 3
ant test-server
pkill -f FinanzaServer

cd "../Cliente-Finanza" 
ant clean && ant compile-with-deps
java -cp "build/classes:lib/*" util.IntegrationTest
```

## Quick Reference Commands

### Most Common Developer Workflows
```bash
# Full system validation (run this after any changes)
cd "DESKTOP VERSION/Servidor-Finanza"
ant clean && ant compile && ant jar && ant run &
sleep 3 && ant test-server && pkill -f FinanzaServer

cd "../Cliente-Finanza"  
ant clean && ant compile-with-deps
java -cp "build/classes:lib/*" util.IntegrationTest

# Quick server rebuild and test
cd "DESKTOP VERSION/Servidor-Finanza"
ant compile && ant jar && ant test-server

# Quick client rebuild and test  
cd "DESKTOP VERSION/Cliente-Finanza"
ant compile-with-deps
java -cp "build/classes:lib/*" util.IntegrationTest
```

### Frequently Used Information
- **Default login**: `admin@finanza.com` / `admin`
- **Server port**: 8080
- **Build times**: Server ~2s, Client ~3s (NEVER CANCEL)
- **Test runtime**: ~2 seconds each
- **Java version**: 17 (required)
- **Ant version**: 1.10.14 (working)

All tests must pass before committing changes to ensure system stability.