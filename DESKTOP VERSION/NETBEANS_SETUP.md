# Como abrir o projeto no NetBeans

Este projeto agora está totalmente configurado para funcionar perfeitamente no NetBeans IDE.

## Estrutura do Projeto

O projeto está dividido em dois módulos independentes:

- **ClienteFinanza**: Aplicação cliente (interface gráfica)
- **ServidorFinanza**: Aplicação servidor (backend)

## Abrindo no NetBeans

### Opção 1: Abrir ambos os projetos como uma suite
1. Abra o NetBeans IDE
2. Vá em `File` → `Open Project...`
3. Navegue até a pasta `deskfinanza`
4. Selecione as pastas `ClienteFinanza` e `ServidorFinanza` (mantenha Ctrl pressionado para selecionar múltiplos)
5. Clique em `Open Project`

### Opção 2: Abrir cada projeto individualmente
1. Abra o NetBeans IDE
2. Vá em `File` → `Open Project...`
3. Navegue até `deskfinanza/ClienteFinanza`
4. Clique em `Open Project`
5. Repita o processo para `deskfinanza/ServidorFinanza`

## Configuração Automática

Cada projeto está configurado com:
- ✅ Estrutura padrão do NetBeans (src/, build/, dist/)
- ✅ Arquivos de projeto NetBeans (nbproject/)
- ✅ Build scripts Ant (build.xml, build-impl.xml)
- ✅ Propriedades do projeto (project.properties)
- ✅ Dependências configuradas (MySQL Connector para o servidor)

## Compilação e Execução

### No NetBeans IDE:
- **Compilar**: Clique com botão direito no projeto → `Build`
- **Executar**: Clique com botão direito no projeto → `Run`
- **Limpar**: Clique com botão direito no projeto → `Clean`

### Via linha de comando:
```bash
# Executar cliente
./run_client.sh

# Executar servidor  
./run_server.sh
```

## Dependências

- **Java 8+** (configurado para target 1.8)
- **MySQL Connector/J** (incluído em `lib/mysql-connector-j-8.0.33.jar`)
- **MySQL Server** (para o servidor funcionar completamente)

## Classes Principais

- **ClienteFinanza**: `MainCliente.java`
- **ServidorFinanza**: `MainServidor.java`

## Estrutura de Arquivos

```
deskfinanza/
├── ClienteFinanza/           # Projeto cliente NetBeans
│   ├── nbproject/           # Configurações NetBeans
│   ├── src/                 # Código fonte
│   ├── build/               # Arquivos compilados
│   ├── dist/                # JARs de distribuição
│   ├── build.xml            # Script Ant
│   └── manifest.mf          # Manifest do JAR
├── ServidorFinanza/         # Projeto servidor NetBeans
│   ├── nbproject/           # Configurações NetBeans
│   ├── src/                 # Código fonte
│   ├── build/               # Arquivos compilados
│   ├── dist/                # JARs de distribuição
│   ├── build.xml            # Script Ant
│   └── manifest.mf          # Manifest do JAR
├── lib/                     # Bibliotecas externas
│   └── mysql-connector-j-8.0.33.jar
├── banco/                   # Scripts do banco
├── docs/                    # Documentação
├── run_client.sh            # Script executar cliente
└── run_server.sh            # Script executar servidor
```

## Resolução de Problemas

Se encontrar problemas:

1. **Projeto não abre**: Certifique-se de ter Java 8+ instalado
2. **Erro de compilação**: Verifique se as dependências estão na pasta `lib/`
3. **MySQL não conecta**: Configure o banco conforme documentação em `banco/`

## Funcionalidades Testadas

- ✅ Abertura dos projetos no NetBeans
- ✅ Compilação via NetBeans e Ant
- ✅ Execução das aplicações
- ✅ Dependências do MySQL configuradas
- ✅ Build scripts funcionais