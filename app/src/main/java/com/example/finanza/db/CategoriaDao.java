package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Transaction;
import com.example.finanza.model.Categoria;
import java.util.List;

/**
 * CategoriaDao - Data Access Object para Categorias
 *
 * Interface que define todas as operações de banco de dados relacionadas
 * à entidade Categoria. Utiliza Room Database com suporte completo para:
 *
 * - Operações CRUD básicas (Create, Read, Update, Delete)
 * - Consultas por tipo (receita/despesa)
 * - Sincronização bidirecional com servidor desktop
 * - Resolução de conflitos por timestamp
 * - Detecção de duplicatas
 * - Métodos transacionais para operações complexas
 *
 * Sincronização:
 * As categorias podem estar em diferentes estados de sincronização:
 * - SYNCED (1): Sincronizado com servidor
 * - NEEDS_SYNC (2): Modificado localmente, aguardando sincronização
 * - CONFLICT (3): Conflito detectado durante sincronização
 *
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
@Dao
public interface CategoriaDao {
    
    // ================== OPERAÇÕES CRUD BÁSICAS ==================
    
    /**
     * Insere uma nova categoria no banco de dados
     *
     * @param categoria Categoria a ser inserida
     * @return ID da categoria inserida
     */
    @Insert
    long inserir(Categoria categoria);

    /**
     * Atualiza uma categoria existente
     *
     * @param categoria Categoria com dados atualizados
     */
    @Update
    void atualizar(Categoria categoria);

    /**
     * Remove uma categoria do banco de dados
     *
     * @param categoria Categoria a ser removida
     */
    @Delete
    void deletar(Categoria categoria);

    /**
     * Lista todas as categorias de um tipo específico
     *
     * @param tipo Tipo da categoria ("receita" ou "despesa")
     * @return Lista de categorias do tipo especificado
     */
    @Query("SELECT * FROM Categoria WHERE tipo = :tipo")
    List<Categoria> listarPorTipo(String tipo);

    /**
     * Lista todas as categorias cadastradas
     *
     * @return Lista completa de categorias
     */
    @Query("SELECT * FROM Categoria")
    List<Categoria> listarTodas();

    /**
     * Busca uma categoria pelo ID
     *
     * @param id ID da categoria
     * @return Categoria encontrada ou null
     */
    @Query("SELECT * FROM Categoria WHERE id = :id")
    Categoria buscarPorId(int id);

    // ================== MÉTODOS DE SINCRONIZAÇÃO ==================

    /**
     * Busca uma categoria pelo UUID universal
     *
     * @param uuid UUID da categoria
     * @return Categoria encontrada ou null
     */
    @Query("SELECT * FROM Categoria WHERE uuid = :uuid LIMIT 1")
    Categoria buscarPorUuid(String uuid);

    /**
     * Obtém categorias que precisam ser sincronizadas
     *
     * @return Lista de categorias com status NEEDS_SYNC ou CONFLICT
     */
    @Query("SELECT * FROM Categoria WHERE syncStatus = 2 OR syncStatus = 3")
    List<Categoria> obterPendentesSync();

    /**
     * Obtém categorias modificadas após um timestamp
     *
     * @param timestamp Timestamp de referência
     * @return Lista de categorias modificadas
     */
    @Query("SELECT * FROM Categoria WHERE lastModified > :timestamp")
    List<Categoria> obterModificadosApos(long timestamp);

    /**
     * Marca uma categoria como sincronizada
     *
     * @param id ID da categoria
     * @param syncTime Timestamp da sincronização
     */
    @Query("UPDATE Categoria SET syncStatus = 1, lastSyncTime = :syncTime WHERE id = :id")
    void marcarComoSincronizado(int id, long syncTime);

    /**
     * Marca uma categoria para sincronização
     *
     * @param id ID da categoria
     * @param timestamp Timestamp da modificação
     */
    @Query("UPDATE Categoria SET syncStatus = 2, lastModified = :timestamp WHERE id = :id")
    void marcarParaSync(int id, long timestamp);

    /**
     * Atualiza metadados de sincronização
     *
     * @param uuid UUID da categoria
     * @param status Novo status de sincronização
     * @param syncTime Timestamp da sincronização
     * @param hash Hash do servidor para detecção de mudanças
     */
    @Query("UPDATE Categoria SET syncStatus = :status, lastSyncTime = :syncTime, serverHash = :hash WHERE uuid = :uuid")
    void atualizarMetadataSync(String uuid, int status, long syncTime, String hash);

    /**
     * Atualiza status de sincronização de múltiplas categorias
     *
     * @param uuids Lista de UUIDs das categorias
     * @param status Novo status de sincronização
     */
    @Query("UPDATE Categoria SET syncStatus = :status WHERE uuid IN (:uuids)")
    void atualizarStatusSync(List<String> uuids, int status);

    /**
     * Obtém o timestamp da última sincronização bem-sucedida
     *
     * @return Timestamp da última sincronização ou null
     */
    @Query("SELECT MAX(lastSyncTime) FROM Categoria WHERE syncStatus = 1")
    Long obterUltimoTempoSync();

    // ================== DETECÇÃO DE DUPLICATAS ==================

    /**
     * Busca categoria duplicada por nome e tipo, excluindo um UUID
     *
     * @param nome Nome da categoria
     * @param tipo Tipo da categoria
     * @param excludeUuid UUID a ser excluído da busca
     * @return Categoria duplicada ou null
     */
    @Query("SELECT * FROM Categoria WHERE nome = :nome AND tipo = :tipo AND uuid != :excludeUuid LIMIT 1")
    Categoria buscarDuplicataPorNomeETipo(String nome, String tipo, String excludeUuid);

    /**
     * Busca categoria por nome e tipo exatos
     *
     * @param nome Nome da categoria
     * @param tipo Tipo da categoria
     * @return Categoria encontrada ou null
     */
    @Query("SELECT * FROM Categoria WHERE nome = :nome AND tipo = :tipo LIMIT 1")
    Categoria buscarPorNomeETipo(String nome, String tipo);

    // ================== OPERAÇÕES TRANSACIONAIS ==================

    /**
     * Insere ou atualiza uma categoria baseado no UUID e timestamp
     *
     * Lógica de resolução de conflitos:
     * - Se UUID existe e timestamp é mais recente: atualiza
     * - Se UUID existe e timestamp é mais antigo: marca conflito
     * - Se UUID não existe mas há duplicata: atualiza a duplicata
     * - Caso contrário: insere nova categoria
     *
     * @param categoria Categoria a ser inserida ou atualizada
     * @return ID da categoria resultante
     */
    @Transaction
    default long inserirOuAtualizar(Categoria categoria) {
        Categoria existente = buscarPorUuid(categoria.uuid);
        if (existente != null) {
            if (categoria.lastModified > existente.lastModified) {
                categoria.id = existente.id;
                atualizar(categoria);
                return existente.id;
            } else {
                existente.syncStatus = 3; // conflito
                atualizar(existente);
                return existente.id;
            }
        } else {
            Categoria duplicata = buscarPorNomeETipo(categoria.nome, categoria.tipo);
            if (duplicata != null) {
                categoria.id = duplicata.id;
                categoria.uuid = duplicata.uuid;
                atualizar(categoria);
                return duplicata.id;
            } else {
                return inserir(categoria);
            }
        }
    }

    /**
     * Insere uma categoria de forma segura, evitando duplicatas
     *
     * Verifica duplicatas por UUID e por nome+tipo antes de inserir.
     * Retorna ID da categoria existente se já cadastrada.
     *
     * @param categoria Categoria a ser inserida
     * @return ID da categoria inserida ou existente
     */
    @Transaction
    default long inserirSeguro(Categoria categoria) {
        Categoria existenteUuid = buscarPorUuid(categoria.uuid);
        if (existenteUuid != null) {
            return existenteUuid.id;
        }
        Categoria duplicata = buscarPorNomeETipo(categoria.nome, categoria.tipo);
        if (duplicata != null) {
            return duplicata.id;
        }
        categoria.markAsModified();
        return inserir(categoria);
    }

    /**
     * Atualiza o ID de uma categoria
     *
     * Usado durante sincronização para alinhar IDs locais com servidor.
     *
     * @param antigoId ID atual da categoria
     * @param novoId Novo ID a ser atribuído
     */
    @Query("UPDATE Categoria SET id = :novoId WHERE id = :antigoId")
    void atualizarId(int antigoId, int novoId);

    /**
     * Sincroniza categoria do servidor para o banco local
     *
     * Método transacional que garante integridade ao sincronizar dados
     * do servidor. Trata casos de:
     * - Categoria já existente no servidor
     * - Duplicata local que precisa ser alinhada
     * - Nova categoria vinda do servidor
     *
     * @param serverId ID da categoria no servidor
     * @param nome Nome da categoria
     * @param tipo Tipo da categoria
     * @param corHex Cor em hexadecimal
     * @return ID da categoria sincronizada
     */
    @Transaction
    default long sincronizarDoServidor(int serverId, String nome, String tipo, String corHex) {
        Categoria existenteServerId = buscarPorId(serverId);
        if (existenteServerId != null) {
            return serverId;
        }
        Categoria localCategory = buscarPorNomeETipo(nome, tipo);
        if (localCategory != null && localCategory.id != serverId) {
            Categoria serverCategoria = new Categoria();
            serverCategoria.id = serverId;
            serverCategoria.nome = nome;
            serverCategoria.tipo = tipo;
            serverCategoria.corHex = corHex != null ? corHex : "#666666";
            serverCategoria.syncStatus = 1;
            serverCategoria.lastSyncTime = System.currentTimeMillis();
            deletar(localCategory);
            return inserir(serverCategoria);
        }
        Categoria categoria = new Categoria();
        categoria.id = serverId;
        categoria.nome = nome;
        categoria.tipo = tipo;
        categoria.corHex = corHex != null ? corHex : "#666666";
        categoria.syncStatus = 1;
        categoria.lastSyncTime = System.currentTimeMillis();
        return inserir(categoria);
    }
}