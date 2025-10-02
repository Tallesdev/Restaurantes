package Restaurante.example.Restaurante.repository;

import Restaurante.example.Restaurante.model.Cardapio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface CardapioRepository extends JpaRepository<Cardapio, Long> {

    // --- MÉTODOS DE FILTRO PARA O LISTAR (DO CONTROLLER) ---

    // 1. Lógica AND (findByRestauranteIdAndDisponivel)
    List<Cardapio> findByRestauranteIdAndDisponivel(Long restauranteId, boolean disponivel);

    // 2. Filtro por ID (findByRestauranteId) - Deve estar funcionando.
    List<Cardapio> findByRestauranteId(Long restauranteId);

    // 3. Busca os disponiveis
    List<Cardapio> findByDisponivel(boolean disponivel);

    // Adicionado para permitir o filtro por nome do produto
    // Busca itens de cardápio onde o nome do Produto seja exatamente igual ao nome fornecido
    List<Cardapio> findByProdutoNome(String nome);

    // --- MÉTODOS JPQL/NATIVE QUERY (REQUISITOS DO PROJETO) ---

    // JPQL 1 (Ex: buscarItensDisponiveisPorRestaurante)
    @Query("SELECT c FROM Cardapio c WHERE c.restaurante.id = :restauranteId AND c.disponivel = true")
    List<Cardapio> buscarItensDisponiveisPorRestaurante(Long restauranteId);

    // JPQL 2 (Ex: buscarNomesDistintosDeProdutosNoCardapio)
    @Query("SELECT DISTINCT c.produto.nome FROM Cardapio c")
    List<String> buscarNomesDistintosDeProdutosNoCardapio();

    // Native Query (Ex: contarItensPorRestaurante)
    @Query(
            value = "SELECT r.nome, COUNT(c.id) AS total_itens " +
                    "FROM restaurantes r " +
                    "JOIN cardapios c ON r.id = c.restaurante_id " +
                    "GROUP BY r.nome " +
                    "ORDER BY total_itens DESC",
            nativeQuery = true
    )
    List<Object[]> contarItensPorRestaurante();


}