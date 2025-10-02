package Restaurante.example.Restaurante.repository;

import Restaurante.example.Restaurante.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// A interface JpaRepository recebe dois tipos:
// 1. A Entidade (Restaurante)
// 2. O tipo da Chave Primária (Long)
@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

    // Derived Query: Busca em Nome OU Endereço (usando 'Containing' para 'LIKE %termo%')
    List<Restaurante> findByNomeContainingIgnoreCaseOrEnderecoContainingIgnoreCase(String nomeBusca, String enderecoBusca);
}