package com.example.finanza.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Transaction;
import com.example.finanza.model.Lancamento;
import java.util.List;

/**
 * LancamentoDao - Data Access Object para Lançamentos Financeiros
 *
 * Interface que define todas as operações de banco de dados relacionadas
 * à entidade Lancamento (transações financeiras). Utiliza Room Database
 * com suporte completo para:
 *
 * - Operações CRUD básicas (Create, Read, Update, Delete)
 * - Consultas por usuário, conta, categoria e período
 * - Cálculos de soma por tipo, conta e categoria
 * - Busca textual por descrição ou valor
 * - Soft delete (marcação lógica de exclusão)
 * - Sincronização bidirecional com servidor desktop
 * - Resolução de conflitos por timestamp
 * - Detecção de duplicatas e transações similares
 * - Métodos transacionais para operações complexas
 *
 * Tipos de lançamento:
 * - Receita: Entrada de dinheiro
 * - Despesa: Saída de dinheiro
 *
 * Sincronização:
 * Os lançamentos podem estar em diferentes estados:
 * - SYNCED (1): Sincronizado com servidor
 * - NEEDS_SYNC (2): Modificado localmente, aguardando sincronização
 * - CONFLICT (3): Conflito detectado durante sincronização
 * - isDeleted: Marca lógica de exclusão (soft delete)
 *
 * @author Finanza Team
 * @version 1.0
 * @since 2024
 */
@Dao
public interface LancamentoDao {
    
    // ================== OPERAÇÕES CRUD BÁSICAS ==================
    
    /**
     * Insere um novo lançamento no banco de dados
     *
     * @param lancamento Lançamento a ser inserido
     * @return ID do lançamento inserido
     */
    @Insert
    long inserir(Lancamento lancamento);

    /**
     * Atualiza um lançamento existente
     *
     * @param lancamento Lançamento com dados atualizados
     */
    @Update
    void atualizar(Lancamento lancamento);

    /**
     * Remove um lançamento do banco de dados (hard delete)
     *
     * @param lancamento Lançamento a ser removido
     */
    @Delete
    void deletar(Lancamento lancamento);

    /**
     * Remove todos os lançamentos de um usuário específico
     *
     * @param usuarioId ID do usuário
     */
    @Query("DELETE FROM Lancamento WHERE usuarioId = :usuarioId")
    void excluirPorUsuario(int usuarioId);

    /**
     * Remove todos os lançamentos de um usuário (alias para excluirPorUsuario)
     *
     * @param usuarioId ID do usuário
     */
    @Query("DELETE FROM Lancamento WHERE usuarioId = :usuarioId")
    void deletarTodosDoUsuario(int usuarioId);

    // ================== CONSULTAS POR USUÁRIO ==================

    /**
     * Lista todos os lançamentos de um usuário ordenados por data (mais recentes primeiro)
     *
     * @param usuarioId ID do usuário
     * @return Lista de lançamentos do usuário
     */
    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId ORDER BY data DESC")
    List<Lancamento> listarPorUsuario(int usuarioId);

    /**
     * Lista apenas lançamentos ativos (não deletados) de um usuário
     *
     * @param usuarioId ID do usuário
     * @return Lista de lançamentos ativos
     */
    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId AND isDeleted = 0 ORDER BY data DESC")
    List<Lancamento> listarAtivosPorUsuario(int usuarioId);

    /**
     * Lista os últimos N lançamentos de um usuário
     *
     * @param usuarioId ID do usuário
     * @param limit Número de lançamentos a retornar
     * @return Lista com os lançamentos mais recentes
     */
    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId ORDER BY data DESC LIMIT :limit")
    List<Lancamento> listarUltimasPorUsuario(int usuarioId, int limit);

    /**
     * Lista lançamentos de um usuário em um período específico
     *
     * @param usuarioId ID do usuário
     * @param inicio Timestamp de início do período
     * @param fim Timestamp de fim do período
     * @return Lista de lançamentos no período
     */
    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId AND data >= :inicio AND data <= :fim ORDER BY data DESC")
    List<Lancamento> listarPorUsuarioPeriodo(int usuarioId, long inicio, long fim);

    // ================== CONSULTAS POR CONTA E CATEGORIA ==================

    /**
     * Busca um lançamento pelo ID
     *
     * @param id ID do lançamento
     * @return Lançamento encontrado ou null
     */
    @Query("SELECT * FROM Lancamento WHERE id = :id")
    Lancamento buscarPorId(int id);

    /**
     * Lista todos os lançamentos de uma conta específica
     *
     * @param contaId ID da conta
     * @return Lista de lançamentos da conta
     */
    @Query("SELECT * FROM Lancamento WHERE contaId = :contaId ORDER BY data DESC")
    List<Lancamento> listarPorConta(int contaId);

    /**
     * Busca lançamentos de uma conta (alias para listarPorConta)
     *
     * @param contaId ID da conta
     * @return Lista de lançamentos da conta
     */
    @Query("SELECT * FROM Lancamento WHERE contaId = :contaId ORDER BY data DESC")
    List<Lancamento> buscarPorConta(int contaId);

    /**
     * Lista todos os lançamentos de uma categoria específica
     *
     * @param categoriaId ID da categoria
     * @return Lista de lançamentos da categoria
     */
    @Query("SELECT * FROM Lancamento WHERE categoriaId = :categoriaId ORDER BY data DESC")
    List<Lancamento> listarPorCategoria(int categoriaId);

    // ================== CONSULTAS DE SOMA E CÁLCULOS ==================

    /**
     * Calcula a soma total de lançamentos por tipo (receita ou despesa)
     *
     * @param tipo Tipo do lançamento ("receita" ou "despesa")
     * @param usuarioId ID do usuário
     * @return Soma total dos lançamentos do tipo ou null se vazio
     */
    @Query("SELECT SUM(valor) FROM Lancamento WHERE tipo = :tipo AND usuarioId = :usuarioId")
    Double somaPorTipo(String tipo, int usuarioId);

    /**
     * Calcula a soma de lançamentos por tipo e conta
     *
     * @param tipo Tipo do lançamento
     * @param usuarioId ID do usuário
     * @param contaId ID da conta
     * @return Soma dos lançamentos ou null
     */
    @Query("SELECT SUM(valor) FROM Lancamento WHERE tipo = :tipo AND usuarioId = :usuarioId AND contaId = :contaId")
    Double somaPorTipoConta(String tipo, int usuarioId, int contaId);

    /**
     * Calcula o saldo total de uma conta (soma de todos os lançamentos)
     *
     * @param contaId ID da conta
     * @param usuarioId ID do usuário
     * @return Saldo da conta ou null
     */
    @Query("SELECT SUM(valor) FROM Lancamento WHERE contaId = :contaId AND usuarioId = :usuarioId")
    Double saldoPorConta(int contaId, int usuarioId);

    /**
     * Calcula a soma de lançamentos por conta e tipo
     *
     * @param contaId ID da conta
     * @param tipo Tipo do lançamento
     * @return Soma dos lançamentos ou null
     */
    @Query("SELECT SUM(valor) FROM Lancamento WHERE contaId = :contaId AND tipo = :tipo")
    Double somaPorContaETipo(int contaId, String tipo);

    /**
     * Calcula a soma total de lançamentos de uma categoria
     *
     * @param categoriaId ID da categoria
     * @param usuarioId ID do usuário
     * @return Soma dos lançamentos da categoria ou null
     */
    @Query("SELECT SUM(valor) FROM Lancamento WHERE categoriaId = :categoriaId AND usuarioId = :usuarioId")
    Double somaPorCategoria(int categoriaId, int usuarioId);

    // ================== BUSCA E DETECÇÃO DE DUPLICATAS ==================

    /**
     * Busca lançamentos por descrição ou valor (busca textual)
     *
     * @param usuarioId ID do usuário
     * @param searchTerm Termo de busca (usar com % para wildcards)
     * @return Lista de lançamentos encontrados
     */
    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId AND (descricao LIKE :searchTerm OR CAST(valor AS TEXT) LIKE :searchTerm) ORDER BY data DESC")
    List<Lancamento> buscarPorDescricaoOuValor(int usuarioId, String searchTerm);

    /**
     * Busca lançamento duplicado exato
     *
     * Verifica se existe lançamento com mesmo valor, data, descrição,
     * conta e usuário, excluindo um UUID específico.
     *
     * @param valor Valor do lançamento
     * @param data Data do lançamento
     * @param descricao Descrição do lançamento
     * @param contaId ID da conta
     * @param usuarioId ID do usuário
     * @param excludeUuid UUID a ser excluído da busca
     * @return Lançamento duplicado ou null
     */
    @Query("SELECT * FROM Lancamento WHERE valor = :valor AND data = :data AND descricao = :descricao AND contaId = :contaId AND usuarioId = :usuarioId AND uuid != :excludeUuid AND isDeleted = 0 LIMIT 1")
    Lancamento buscarDuplicata(double valor, long data, String descricao, int contaId, int usuarioId, String excludeUuid);

    /**
     * Busca lançamentos similares baseado em valor, data próxima e conta
     *
     * Útil para detecção de possíveis duplicatas com pequenas variações de timestamp.
     *
     * @param valor Valor do lançamento
     * @param data Data do lançamento
     * @param timeWindow Janela de tempo para considerar similaridade (em ms)
     * @param contaId ID da conta
     * @param usuarioId ID do usuário
     * @return Lista de lançamentos similares
     */
    @Query("SELECT * FROM Lancamento WHERE valor = :valor AND ABS(data - :data) < :timeWindow AND contaId = :contaId AND usuarioId = :usuarioId AND isDeleted = 0")
    List<Lancamento> buscarSimilares(double valor, long data, long timeWindow, int contaId, int usuarioId);

    // ================== MÉTODOS DE SINCRONIZAÇÃO ==================

    /**
     * Busca um lançamento pelo UUID universal
     *
     * @param uuid UUID do lançamento
     * @return Lançamento encontrado ou null
     */
    @Query("SELECT * FROM Lancamento WHERE uuid = :uuid LIMIT 1")
    Lancamento buscarPorUuid(String uuid);

    /**
     * Obtém todos os lançamentos que precisam ser sincronizados
     *
     * @return Lista de lançamentos com status NEEDS_SYNC ou CONFLICT
     */
    @Query("SELECT * FROM Lancamento WHERE syncStatus = 2 OR syncStatus = 3")
    List<Lancamento> obterPendentesSync();

    /**
     * Obtém lançamentos pendentes de sincronização de um usuário específico
     *
     * @param usuarioId ID do usuário
     * @return Lista de lançamentos pendentes do usuário
     */
    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId AND (syncStatus = 2 OR syncStatus = 3)")
    List<Lancamento> obterPendentesSyncPorUsuario(int usuarioId);

    /**
     * Obtém lançamentos ativos modificados após um timestamp
     *
     * @param timestamp Timestamp de referência
     * @return Lista de lançamentos modificados (não deletados)
     */
    @Query("SELECT * FROM Lancamento WHERE lastModified > :timestamp AND isDeleted = 0")
    List<Lancamento> obterModificadosApos(long timestamp);

    /**
     * Obtém lançamentos de um usuário modificados após um timestamp
     *
     * @param usuarioId ID do usuário
     * @param timestamp Timestamp de referência
     * @return Lista de lançamentos modificados do usuário
     */
    @Query("SELECT * FROM Lancamento WHERE usuarioId = :usuarioId AND lastModified > :timestamp AND isDeleted = 0")
    List<Lancamento> obterModificadosAposPorUsuario(int usuarioId, long timestamp);

    /**
     * Obtém lançamentos deletados após um timestamp
     *
     * Usado para sincronizar exclusões com o servidor.
     *
     * @param timestamp Timestamp de referência
     * @return Lista de lançamentos marcados como deletados
     */
    @Query("SELECT * FROM Lancamento WHERE isDeleted = 1 AND lastModified > :timestamp")
    List<Lancamento> obterDeletadosApos(long timestamp);

    /**
     * Marca um lançamento como sincronizado
     *
     * @param id ID do lançamento
     * @param syncTime Timestamp da sincronização
     */
    @Query("UPDATE Lancamento SET syncStatus = 1, lastSyncTime = :syncTime WHERE id = :id")
    void marcarComoSincronizado(int id, long syncTime);

    /**
     * Marca um lançamento para sincronização
     *
     * @param id ID do lançamento
     * @param timestamp Timestamp da modificação
     */
    @Query("UPDATE Lancamento SET syncStatus = 2, lastModified = :timestamp WHERE id = :id")
    void marcarParaSync(int id, long timestamp);

    /**
     * Marca um lançamento como excluído (soft delete)
     *
     * Define isDeleted=1 e marca para sincronização ao invés de
     * remover fisicamente do banco.
     *
     * @param id ID do lançamento
     * @param timestamp Timestamp da exclusão
     */
    @Query("UPDATE Lancamento SET isDeleted = 1, syncStatus = 2, lastModified = :timestamp WHERE id = :id")
    void marcarComoExcluido(int id, long timestamp);

    /**
     * Atualiza metadados de sincronização
     *
     * @param uuid UUID do lançamento
     * @param status Novo status de sincronização
     * @param syncTime Timestamp da sincronização
     * @param hash Hash do servidor para detecção de mudanças
     */
    @Query("UPDATE Lancamento SET syncStatus = :status, lastSyncTime = :syncTime, serverHash = :hash WHERE uuid = :uuid")
    void atualizarMetadataSync(String uuid, int status, long syncTime, String hash);

    /**
     * Atualiza status de sincronização de múltiplos lançamentos
     *
     * @param uuids Lista de UUIDs dos lançamentos
     * @param status Novo status de sincronização
     */
    @Query("UPDATE Lancamento SET syncStatus = :status WHERE uuid IN (:uuids)")
    void atualizarStatusSync(List<String> uuids, int status);

    /**
     * Obtém o timestamp da última sincronização bem-sucedida
     *
     * @return Timestamp da última sincronização ou null
     */
    @Query("SELECT MAX(lastSyncTime) FROM Lancamento WHERE syncStatus = 1")
    Long obterUltimoTempoSync();

    // ================== ATUALIZAÇÃO DE IDs DE RELACIONAMENTO ==================

    /**
     * Atualiza o ID da categoria em lançamentos
     *
     * Usado durante sincronização para alinhar IDs com servidor.
     *
     * @param antigoId ID antigo da categoria
     * @param novoId Novo ID da categoria
     */
    @Query("UPDATE Lancamento SET categoriaId = :novoId WHERE categoriaId = :antigoId")
    void atualizarCategoriaId(int antigoId, int novoId);

    /**
     * Atualiza o ID da conta em lançamentos
     *
     * Usado durante sincronização para alinhar IDs com servidor.
     *
     * @param antigoId ID antigo da conta
     * @param novoId Novo ID da conta
     */
    @Query("UPDATE Lancamento SET contaId = :novoId WHERE contaId = :antigoId")
    void atualizarContaId(int antigoId, int novoId);

    // ================== OPERAÇÕES TRANSACIONAIS ==================

    /**
     * Insere ou atualiza um lançamento baseado no UUID e timestamp
     *
     * Lógica de resolução de conflitos:
     * - Se UUID existe e timestamp é mais recente: atualiza
     * - Se UUID existe e timestamp é mais antigo: marca conflito
     * - Se UUID não existe mas há duplicata: atualiza se mais recente
     * - Caso contrário: insere novo lançamento
     *
     * @param lancamento Lançamento a ser inserido ou atualizado
     * @return ID do lançamento resultante
     */
    @Transaction
    default long inserirOuAtualizar(Lancamento lancamento) {
        Lancamento existente = buscarPorUuid(lancamento.uuid);
        if (existente != null) {
            if (lancamento.lastModified > existente.lastModified) {
                lancamento.id = existente.id;
                atualizar(lancamento);
                return existente.id;
            } else {
                existente.syncStatus = 3; // conflito
                atualizar(existente);
                return existente.id;
            }
        } else {
            Lancamento duplicata = buscarDuplicata(
                    lancamento.valor,
                    lancamento.data,
                    lancamento.descricao != null ? lancamento.descricao : "",
                    lancamento.contaId,
                    lancamento.usuarioId,
                    lancamento.uuid
            );
            if (duplicata != null) {
                if (lancamento.lastModified > duplicata.lastModified) {
                    lancamento.id = duplicata.id;
                    lancamento.uuid = duplicata.uuid;
                    atualizar(lancamento);
                }
                return duplicata.id;
            } else {
                return inserir(lancamento);
            }
        }
    }

    /**
     * Insere um lançamento de forma segura com detecção inteligente de duplicatas
     *
     * Implementa algoritmo de detecção de duplicatas em múltiplas camadas:
     * 1. Verifica duplicata por UUID
     * 2. Verifica duplicata exata (valor, data, descrição, conta)
     * 3. Busca lançamentos similares em janela de 1 hora
     * 4. Compara descrição e proximidade de timestamp (5 min)
     *
     * @param lancamento Lançamento a ser inserido
     * @return ID do lançamento inserido ou duplicata encontrada
     */
    @Transaction
    default long inserirSeguro(Lancamento lancamento) {
        Lancamento existenteUuid = buscarPorUuid(lancamento.uuid);
        if (existenteUuid != null) {
            return existenteUuid.id;
        }
        Lancamento duplicataExata = buscarDuplicata(
                lancamento.valor,
                lancamento.data,
                lancamento.descricao != null ? lancamento.descricao : "",
                lancamento.contaId,
                lancamento.usuarioId,
                lancamento.uuid
        );
        if (duplicataExata != null) {
            return duplicataExata.id;
        }
        // Janela de tempo de 1 hora para buscar similares
        long timeWindow = 60 * 60 * 1000;
        List<Lancamento> similares = buscarSimilares(
                lancamento.valor,
                lancamento.data,
                timeWindow,
                lancamento.contaId,
                lancamento.usuarioId
        );
        for (Lancamento similar : similares) {
            // Verifica se descrições são iguais (case-insensitive)
            if (similar.descricao != null && lancamento.descricao != null &&
                    similar.descricao.trim().equalsIgnoreCase(lancamento.descricao.trim())) {
                return similar.id;
            }
            // Verifica se datas estão muito próximas (dentro de 5 minutos)
            if (Math.abs(similar.data - lancamento.data) < 5 * 60 * 1000) {
                return similar.id;
            }
        }
        lancamento.markAsModified();
        return inserir(lancamento);
    }

    /**
     * Exclui um lançamento de forma segura usando soft delete
     *
     * Marca o lançamento como excluído ao invés de removê-lo fisicamente.
     * Isso permite sincronizar a exclusão com o servidor e manter histórico.
     *
     * @param id ID do lançamento a ser excluído
     */
    @Transaction
    default void excluirSeguro(int id) {
        Lancamento lancamento = buscarPorId(id);
        if (lancamento != null) {
            marcarComoExcluido(id, System.currentTimeMillis());
        }
    }
}