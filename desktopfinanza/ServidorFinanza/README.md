# Finanza Desktop Server

Servidor Java para sincronização com aplicativo Android Finanza.

## Pré-requisitos

1. **Java JDK 8 ou superior**
2. **Apache Ant**
3. **MySQL Server**

## Configuração do Banco de Dados

1. Instale e inicie o MySQL Server
2. Execute o script de configuração:

```bash
mysql -u root -p < finanza_mysql.sql
```

Ou usando uma ferramenta gráfica como phpMyAdmin ou MySQL Workbench.

## Compilação e Execução

### Usando Ant (Recomendado)

```bash
# Compilar e executar
ant run

# Apenas compilar
ant compile

# Criar JAR
ant jar

# Limpar build
ant clean
```

### Execução Manual

```bash
# Baixar dependências manualmente
wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar -P lib/
wget https://repo1.maven.org/maven2/org/json/json/20230618/json-20230618.jar -P lib/

# Compilar
javac -cp "lib/*" -d build/classes src/main/java/com/finanza/servidor/*.java

# Executar
java -cp "build/classes:lib/*" com.finanza.servidor.ServidorFinanza
```

## Configuração

O servidor usa as seguintes configurações padrão:

- **Porta**: 8080
- **Banco de dados**: jdbc:mysql://localhost:3306/finanza_db
- **Usuário MySQL**: root
- **Senha MySQL**: (vazio)

Para alterar essas configurações, edite as constantes no arquivo `ServidorFinanza.java`.

## API do Servidor

O servidor aceita requisições JSON via socket TCP com as seguintes ações:

### Sincronizar Usuário
```json
{
    "action": "sync_user",
    "userId": 1
}
```

### Sincronizar Contas
```json
{
    "action": "sync_accounts", 
    "userId": 1
}
```

### Sincronizar Lançamentos
```json
{
    "action": "sync_transactions",
    "userId": 1
}
```

## Resolução de Problemas

### Erro: Driver MySQL não encontrado

Certifique-se de que o mysql-connector-j está no diretório `lib/`:

```bash
ls -la lib/
# Deve mostrar: mysql-connector-j-8.0.33.jar
```

### Erro: Conexão com MySQL

1. Verifique se o MySQL está rodando:
   ```bash
   # Linux/Mac
   sudo systemctl status mysql
   
   # Windows
   net start mysql
   ```

2. Verifique se o banco `finanza_db` existe:
   ```sql
   SHOW DATABASES;
   ```

3. Teste a conexão:
   ```bash
   mysql -u root -p -e "USE finanza_db; SHOW TABLES;"
   ```

### Erro: Porta 8080 em uso

Altere a porta no código ou pare o processo que está usando a porta 8080:

```bash
# Verificar que processo está usando a porta
netstat -tulpn | grep 8080

# Linux: matar processo
sudo kill -9 <PID>
```

## Configuração no App Android

No aplicativo Android, vá em Configurações e defina:

- **IP do Servidor**: IP da máquina onde o servidor está rodando
- **Porta**: 8080 (ou a porta configurada)

Para desenvolvimento local:
- Use `localhost` ou `127.0.0.1` se o emulador estiver na mesma máquina
- Use o IP da rede local (ex: `192.168.1.100`) para dispositivos físicos

## Logs

O servidor imprime logs no console indicando:
- Status da inicialização
- Conexões de clientes
- Requisições processadas
- Erros de conexão ou processamento