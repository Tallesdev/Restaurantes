package Restaurante.example.Restaurante.repository;

import Restaurante.example.Restaurante.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.math.BigDecimal;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Lógica AND
    // Busca produtos que CONTÊM o nome E que tem o preço MAIOR OU IGUAL ao mínimo
    List<Produto> findByNomeContainingIgnoreCaseAndPrecoGreaterThanEqual(String nome, BigDecimal precoMinimo);

    // Métodos úteis para busca individual, mas o cenário AND é o principal
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    List<Produto> findByPrecoGreaterThanEqual(BigDecimal preco);
}