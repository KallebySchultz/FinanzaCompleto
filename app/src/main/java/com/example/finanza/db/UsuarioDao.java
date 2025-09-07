package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Transaction;
import com.example.finanza.model.Usuario;
import java.util.List;

/**
 * UsuarioDao - Data Access Object para Usuários
 * 
 * Interface que define todas as operações de banco de dados relacionadas
 * à entidade Usuario. Utiliza Room Database com suporte completo para:
 * 
 * - Operações CRUD básicas (Create, Read, Update, Delete)
 * - Autenticação de usuários (login por email/senha)
 * - Sincronização bidirecional com servidor desktop
 * - Controle de metadados de sincronização
 * - Resolução de conflitos por timestamp
 * - Operações em lote para otimização
 * 
 * Funcionalidades de Sincronização:
 * - Busca por UUID para sincronização cross-platform
 * - Controle de status de sincronização
 * - Sincronização incremental por timestamp
 * - Inserção/atualização baseada em UUID
 * 
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
@Dao
public interface UsuarioDao {
    
    // ================== OPERAÇÕES CRUD BÁSICAS ==================
    
    /**
     * Insere um novo usuário no banco de dados
     * 
     * @param usuario Objeto usuário a ser inserido
     * @return ID do usuário inserido (auto-gerado)
     */
    @Insert
    long inserir(Usuario usuario);

    /**
     * Atualiza dados de um usuário existente
     * 
     * @param usuario Objeto usuário com dados atualizados
     */
    @Update
    void atualizar(Usuario usuario);
    
    /**
     * Remove um usuário do banco de dados
     * 
     * @param usuario Objeto usuário a ser removido
     */
    @Delete
    void deletar(Usuario usuario);

    // ================== CONSULTAS BÁSICAS ==================
    
    /**
     * Busca usuário por ID único local
     * 
     * @param id ID local do usuário
     * @return Objeto Usuario ou null se não encontrado
     */
    @Query("SELECT * FROM Usuario WHERE id = :id")
    Usuario buscarPorId(int id);

    /**
     * Lista todos os usuários cadastrados
     * 
     * @return Lista de todos os usuários
     */
    @Query("SELECT * FROM Usuario")
    List<Usuario> listarTodos();
    
    /**
     * Busca usuário por email
     * 
     * @param email Email do usuário
     * @return Objeto Usuario ou null se não encontrado
     */
    @Query("SELECT * FROM Usuario WHERE email = :email")
    Usuario buscarPorEmail(String email);
    
    // ================== AUTENTICAÇÃO ==================
    
    /**
     * Realiza login verificando email e senha
     * 
     * @param email Email do usuário
     * @param senha Senha do usuário
     * @return Objeto Usuario se credenciais válidas, null caso contrário
     */
    @Query("SELECT * FROM Usuario WHERE email = :email AND senha = :senha")
    Usuario login(String email, String senha);
    
    // ================== MÉTODOS DE SINCRONIZAÇÃO ==================
    
    /**
     * Busca usuário por UUID universal
     * 
     * Utilizado para sincronização cross-platform, permitindo
     * identificar o mesmo usuário entre mobile e desktop.
     * 
     * @param uuid UUID universal do usuário
     * @return Objeto Usuario ou null se não encontrado
     */
    @Query("SELECT * FROM Usuario WHERE uuid = :uuid LIMIT 1")
    Usuario buscarPorUuid(String uuid);
    
    /**
     * Obtém usuários que precisam ser sincronizados
     * 
     * Retorna usuários com status NEEDS_SYNC (2) ou CONFLICT (3).
     * 
     * @return Lista de usuários pendentes de sincronização
     */
    @Query("SELECT * FROM Usuario WHERE syncStatus = 2 OR syncStatus = 3")
    List<Usuario> obterPendentesSync();
    
    /**
     * Obtém usuários modificados após timestamp específico
     * 
     * Utilizado para sincronização incremental, buscando apenas
     * registros modificados desde a última sincronização.
     * 
     * @param timestamp Timestamp de referência
     * @return Lista de usuários modificados após o timestamp
     */
    @Query("SELECT * FROM Usuario WHERE lastModified > :timestamp")
    List<Usuario> obterModificadosApos(long timestamp);
    
    // ================== CONTROLE DE METADADOS DE SYNC ==================
    
    /**
     * Marca usuário como sincronizado
     * 
     * @param id ID do usuário
     * @param syncTime Timestamp da sincronização
     */
    @Query("UPDATE Usuario SET syncStatus = 1, lastSyncTime = :syncTime WHERE id = :id")
    void marcarComoSincronizado(int id, long syncTime);
    
    /**
     * Marca usuário para sincronização
     * 
     * @param id ID do usuário
     * @param timestamp Timestamp da modificação
     */
    @Query("UPDATE Usuario SET syncStatus = 2, lastModified = :timestamp WHERE id = :id")
    void marcarParaSync(int id, long timestamp);
    
    /**
     * Atualiza metadados de sincronização por UUID
     * 
     * @param uuid UUID do usuário
     * @param status Novo status de sincronização
     * @param syncTime Timestamp da sincronização
     */
    @Query("UPDATE Usuario SET syncStatus = :status, lastSyncTime = :syncTime WHERE uuid = :uuid")
    void atualizarMetadataSync(String uuid, int status, long syncTime);
    
    /**
     * Atualiza status de sincronização em lote
     * 
     * @param uuids Lista de UUIDs dos usuários
     * @param status Novo status de sincronização
     */
    @Query("UPDATE Usuario SET syncStatus = :status WHERE uuid IN (:uuids)")
    void atualizarStatusSync(List<String> uuids, int status);
    
    /**
     * Obtém timestamp da última sincronização bem-sucedida
     * 
     * @return Timestamp da última sincronização ou null se nunca sincronizado
     */
    @Query("SELECT MAX(lastSyncTime) FROM Usuario WHERE syncStatus = 1")
    Long obterUltimoTempoSync();
    
    // ================== OPERAÇÕES TRANSACIONAIS ==================
    
    /**
     * Insere ou atualiza usuário baseado no UUID
     * 
     * Método transacional que verifica se já existe um usuário
     * com o mesmo UUID. Se existir, atualiza; senão, insere novo.
     * Ideal para sincronização de dados vindos do servidor.
     * 
     * @param usuario Objeto usuário a ser inserido ou atualizado
     * @return ID do usuário (existente ou novo)
     */
    @Transaction
    default long inserirOuAtualizar(Usuario usuario) {
        Usuario existente = buscarPorUuid(usuario.uuid);
        if (existente != null) {
            // Preserva o ID local ao atualizar
            usuario.id = existente.id;
            atualizar(usuario);
            return existente.id;
        } else {
            return inserir(usuario);
        }
    }
}