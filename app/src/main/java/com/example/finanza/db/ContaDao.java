package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Transaction;
import com.example.finanza.model.Conta;
import java.util.List;

/**
 * ContaDao - Data Access Object para Contas Bancárias
 *
 * Interface que define todas as operações de banco de dados relacionadas
 * à entidade Conta. Utiliza Room Database com suporte completo para:
 *
 * - Operações CRUD básicas (Create, Read, Update, Delete)
 * - Consultas por usuário
 * - Sincronização bidirecional com servidor desktop
 * - Resolução de conflitos por timestamp
 * - Detecção de duplicatas por nome e usuário
 * - Métodos transacionais para operações complexas
 *
 * Tipos de conta suportados:
 * - Conta corrente
 * - Poupança
 * - Cartão de crédito
 * - Investimentos
 * - Dinheiro
 *
 * Sincronização:
 * As contas podem estar em diferentes estados de sincronização:
 * - SYNCED (1): Sincronizado com servidor
 * - NEEDS_SYNC (2): Modificado localmente, aguardando sincronização
 * - CONFLICT (3): Conflito detectado durante sincronização
 *
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
@Dao
public interface ContaDao {
    
    // ================== OPERAÇÕES CRUD BÁSICAS ==================
    
    /**
     * Insere uma nova conta no banco de dados
     *
     * @param conta Conta a ser inserida
     * @return ID da conta inserida
     */
    @Insert
    long inserir(Conta conta);

    /**
     * Atualiza uma conta existente
     *
     * @param conta Conta com dados atualizados
     */
    @Update
    void atualizar(Conta conta);

    /**
     * Remove uma conta do banco de dados
     *
     * @param conta Conta a ser removida
     */
    @Delete
    void deletar(Conta conta);

    /**
     * Remove todas as contas de um usuário específico
     *
     * @param usuarioId ID do usuário
     */
    @Query("DELETE FROM Conta WHERE usuarioId = :usuarioId")
    void excluirPorUsuario(int usuarioId);

    /**
     * Remove todas as contas de um usuário (alias para excluirPorUsuario)
     *
     * @param usuarioId ID do usuário
     */
    @Query("DELETE FROM Conta WHERE usuarioId = :usuarioId")
    void deletarTodosDoUsuario(int usuarioId);

    /**
     * Lista todas as contas de um usuário específico
     *
     * @param usuarioId ID do usuário
     * @return Lista de contas do usuário
     */
    @Query("SELECT * FROM Conta WHERE usuarioId = :usuarioId")
    List<Conta> listarPorUsuario(int usuarioId);

    /**
     * Busca uma conta pelo ID
     *
     * @param id ID da conta
     * @return Conta encontrada ou null
     */
    @Query("SELECT * FROM Conta WHERE id = :id")
    Conta buscarPorId(int id);

    /**
     * Lista todas as contas cadastradas no sistema
     *
     * @return Lista completa de contas
     */
    @Query("SELECT * FROM Conta")
    List<Conta> listarTodos();

    // ================== MÉTODOS DE SINCRONIZAÇÃO ==================

    /**
     * Busca uma conta pelo UUID universal
     *
     * @param uuid UUID da conta
     * @return Conta encontrada ou null
     */
    @Query("SELECT * FROM Conta WHERE uuid = :uuid LIMIT 1")
    Conta buscarPorUuid(String uuid);

    /**
     * Obtém todas as contas que precisam ser sincronizadas
     *
     * @return Lista de contas com status NEEDS_SYNC ou CONFLICT
     */
    @Query("SELECT * FROM Conta WHERE syncStatus = 2 OR syncStatus = 3")
    List<Conta> obterPendentesSync();

    /**
     * Obtém contas pendentes de sincronização de um usuário específico
     *
     * @param usuarioId ID do usuário
     * @return Lista de contas pendentes do usuário
     */
    @Query("SELECT * FROM Conta WHERE usuarioId = :usuarioId AND (syncStatus = 2 OR syncStatus = 3)")
    List<Conta> obterPendentesSyncPorUsuario(int usuarioId);

    /**
     * Obtém contas modificadas após um timestamp
     *
     * @param timestamp Timestamp de referência
     * @return Lista de contas modificadas
     */
    @Query("SELECT * FROM Conta WHERE lastModified > :timestamp")
    List<Conta> obterModificadosApos(long timestamp);

    /**
     * Obtém contas de um usuário modificadas após um timestamp
     *
     * @param usuarioId ID do usuário
     * @param timestamp Timestamp de referência
     * @return Lista de contas modificadas do usuário
     */
    @Query("SELECT * FROM Conta WHERE usuarioId = :usuarioId AND lastModified > :timestamp")
    List<Conta> obterModificadosAposPorUsuario(int usuarioId, long timestamp);

    /**
     * Marca uma conta como sincronizada
     *
     * @param id ID da conta
     * @param syncTime Timestamp da sincronização
     */
    @Query("UPDATE Conta SET syncStatus = 1, lastSyncTime = :syncTime WHERE id = :id")
    void marcarComoSincronizado(int id, long syncTime);

    /**
     * Marca uma conta para sincronização
     *
     * @param id ID da conta
     * @param timestamp Timestamp da modificação
     */
    @Query("UPDATE Conta SET syncStatus = 2, lastModified = :timestamp WHERE id = :id")
    void marcarParaSync(int id, long timestamp);

    /**
     * Atualiza metadados de sincronização
     *
     * @param uuid UUID da conta
     * @param status Novo status de sincronização
     * @param syncTime Timestamp da sincronização
     * @param hash Hash do servidor para detecção de mudanças
     */
    @Query("UPDATE Conta SET syncStatus = :status, lastSyncTime = :syncTime, serverHash = :hash WHERE uuid = :uuid")
    void atualizarMetadataSync(String uuid, int status, long syncTime, String hash);

    /**
     * Atualiza status de sincronização de múltiplas contas
     *
     * @param uuids Lista de UUIDs das contas
     * @param status Novo status de sincronização
     */
    @Query("UPDATE Conta SET syncStatus = :status WHERE uuid IN (:uuids)")
    void atualizarStatusSync(List<String> uuids, int status);

    /**
     * Obtém o timestamp da última sincronização bem-sucedida
     *
     * @return Timestamp da última sincronização ou null
     */
    @Query("SELECT MAX(lastSyncTime) FROM Conta WHERE syncStatus = 1")
    Long obterUltimoTempoSync();

    // ================== DETECÇÃO DE DUPLICATAS ==================

    /**
     * Busca conta duplicada por nome e usuário, excluindo um UUID
     *
     * @param nome Nome da conta
     * @param usuarioId ID do usuário
     * @param excludeUuid UUID a ser excluído da busca
     * @return Conta duplicada ou null
     */
    @Query("SELECT * FROM Conta WHERE nome = :nome AND usuarioId = :usuarioId AND uuid != :excludeUuid LIMIT 1")
    Conta buscarDuplicataPorNomeEUsuario(String nome, int usuarioId, String excludeUuid);

    /**
     * Busca conta por nome e usuário exatos
     *
     * @param nome Nome da conta
     * @param usuarioId ID do usuário
     * @return Conta encontrada ou null
     */
    @Query("SELECT * FROM Conta WHERE nome = :nome AND usuarioId = :usuarioId LIMIT 1")
    Conta buscarPorNomeEUsuario(String nome, int usuarioId);

    // ================== OPERAÇÕES TRANSACIONAIS ==================

    /**
     * Insere ou atualiza uma conta baseado no UUID e timestamp
     *
     * Lógica de resolução de conflitos:
     * - Se UUID existe e timestamp é mais recente: atualiza
     * - Se UUID existe e timestamp é mais antigo: marca conflito
     * - Se UUID não existe mas há duplicata: atualiza a duplicata
     * - Caso contrário: insere nova conta
     *
     * @param conta Conta a ser inserida ou atualizada
     * @return ID da conta resultante
     */
    @Transaction
    default long inserirOuAtualizar(Conta conta) {
        Conta existente = buscarPorUuid(conta.uuid);
        if (existente != null) {
            if (conta.lastModified > existente.lastModified) {
                conta.id = existente.id;
                atualizar(conta);
                return existente.id;
            } else {
                existente.syncStatus = 3; // conflito
                atualizar(existente);
                return existente.id;
            }
        } else {
            Conta duplicata = buscarPorNomeEUsuario(conta.nome, conta.usuarioId);
            if (duplicata != null) {
                conta.id = duplicata.id;
                conta.uuid = duplicata.uuid;
                atualizar(conta);
                return duplicata.id;
            } else {
                return inserir(conta);
            }
        }
    }

    /**
     * Insere uma conta de forma segura, evitando duplicatas
     *
     * Verifica duplicatas por UUID e por nome+usuário antes de inserir.
     * Retorna ID da conta existente se já cadastrada.
     *
     * @param conta Conta a ser inserida
     * @return ID da conta inserida ou existente
     */
    @Transaction
    default long inserirSeguro(Conta conta) {
        Conta existenteUuid = buscarPorUuid(conta.uuid);
        if (existenteUuid != null) {
            return existenteUuid.id;
        }
        Conta duplicata = buscarPorNomeEUsuario(conta.nome, conta.usuarioId);
        if (duplicata != null) {
            return duplicata.id;
        }
        conta.markAsModified();
        return inserir(conta);
    }

    /**
     * Sincroniza conta do servidor para o banco local
     *
     * Método transacional que garante integridade ao sincronizar dados
     * do servidor. Trata casos de:
     * - Conta já existente no servidor
     * - Duplicata local que precisa ser alinhada
     * - Nova conta vinda do servidor
     *
     * @param serverId ID da conta no servidor
     * @param nome Nome da conta
     * @param tipo Tipo da conta
     * @param saldo Saldo inicial
     * @param usuarioId ID do usuário dono da conta
     * @return ID da conta sincronizada
     */
    @Transaction
    default long sincronizarDoServidor(int serverId, String nome, String tipo, double saldo, int usuarioId) {
        Conta existenteServerId = buscarPorId(serverId);
        if (existenteServerId != null) {
            return serverId;
        }
        Conta localConta = buscarPorNomeEUsuario(nome, usuarioId);
        if (localConta != null && localConta.id != serverId) {
            Conta serverConta = new Conta();
            serverConta.id = serverId;
            serverConta.nome = nome;
            serverConta.tipo = tipo;
            serverConta.saldoInicial = saldo;
            serverConta.saldoAtual = saldo;
            serverConta.usuarioId = usuarioId;
            serverConta.syncStatus = 1;
            serverConta.lastSyncTime = System.currentTimeMillis();
            deletar(localConta);
            return inserir(serverConta);
        }
        Conta conta = new Conta();
        conta.id = serverId;
        conta.nome = nome;
        conta.tipo = tipo;
        conta.saldoInicial = saldo;
        conta.saldoAtual = saldo;
        conta.usuarioId = usuarioId;
        conta.syncStatus = 1;
        conta.lastSyncTime = System.currentTimeMillis();
        return inserir(conta);
    }
}