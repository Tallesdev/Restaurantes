package Restaurante.example.Restaurante.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal; // IMPORTANTE: Adicionar essa importação
import jakarta.persistence.Column; // Necessário se quiser usar @Column

@Entity
@Table(name = "cardapios")
public class Cardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    //Preço específico do item no Cardápio
    // campo do tipo BigDecimal
    // Usamos nullable=true para que seja opcional (se for nulo, usamos o preço do Produto)
    @Column(nullable = true)
    private BigDecimal preco;

    private Boolean disponivel;

    // Construtor padrão
    public Cardapio() {
    }

    // Construtor com campos (OPCIONAL, mas bom ter o novo campo)
    public Cardapio(Restaurante restaurante, Produto produto, BigDecimal preco, Boolean disponivel) {
        this.restaurante = restaurante;
        this.produto = produto;
        this.preco = preco; // Adicionado
        this.disponivel = disponivel;
    }

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Boolean getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(Boolean disponivel) {
        this.disponivel = disponivel;
    }
}