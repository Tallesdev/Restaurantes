package Restaurante.example.Restaurante.controller;

import Restaurante.example.Restaurante.model.Cardapio;
import Restaurante.example.Restaurante.repository.CardapioRepository;
import Restaurante.example.Restaurante.repository.ProdutoRepository;
import Restaurante.example.Restaurante.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.List;

@Controller
@RequestMapping("/cardapio") // Mapeamento base: /cardapio/...
public class CardapioController {

    @Autowired
    private CardapioRepository cardapioRepository;

    // Repositórios necessários para carregar as opções nos formulários (Restaurante e Produto)
    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    // Método auxiliar para carregar listas nos formulários
    private void carregarRecursosFormulario(Model model) {
        model.addAttribute("restaurantes", restauranteRepository.findAll());
        model.addAttribute("produtos", produtoRepository.findAll());
    }


    // 1. READ: Listar todos os itens do cardápio (URL: /cardapio/listar)
    @GetMapping("/listar")
    public String listar(
            Model model,
            @RequestParam(value = "restauranteId", required = false) Long restauranteId,
            @RequestParam(value = "disponivel", required = false) Boolean disponivel,
            // *** NOVO PARÂMETRO PARA O FILTRO JPQL ***
            @RequestParam(value = "produtoNome", required = false, defaultValue = "") String produtoNome
    ) {
        List<Cardapio> itensCardapio;

        // 1. Lógica JPQL 1 (Restaurante E Disponível = TRUE)
        if (restauranteId != null && disponivel != null && disponivel == true) {
            itensCardapio = cardapioRepository.buscarItensDisponiveisPorRestaurante(restauranteId);

            // 2. Lógica AND (Restaurante E Disponível = FALSE) - Derived Query
        } else if (restauranteId != null && disponivel != null && disponivel == false) {
            itensCardapio = cardapioRepository.findByRestauranteIdAndDisponivel(restauranteId, disponivel);

            //  3. Lógica FILTRO: Apenas por Nome de Produto ***
        } else if (!produtoNome.isEmpty()) {
            // Usa o novo método Derived Query
            itensCardapio = cardapioRepository.findByProdutoNome(produtoNome);

            // 4. Lógica OU: Apenas Restaurante preenchido
        } else if (restauranteId != null) {
            itensCardapio = cardapioRepository.findByRestauranteId(restauranteId);

            // 5. Lógica OU: Apenas Disponibilidade preenchida
        } else if (disponivel != null) {
            itensCardapio = cardapioRepository.findByDisponivel(disponivel);

            // 6. Sem filtros, lista todos
        } else {
            itensCardapio = cardapioRepository.findAll();
        }

        model.addAttribute("itensCardapio", itensCardapio);

        // USO DO JPQL 2  PARA POPULAR O FILTRO
        List<String> nomesDistintos = cardapioRepository.buscarNomesDistintosDeProdutosNoCardapio();
        model.addAttribute("nomesDistintos", nomesDistintos);

        // Adicionar listas para os filtros:
        model.addAttribute("restaurantes", restauranteRepository.findAll());

        // Adicionar valores atuais para manter o filtro após a pesquisa:
        model.addAttribute("restauranteIdAtual", restauranteId);
        model.addAttribute("disponivelAtual", disponivel);
        //VALOR ATUAL
        model.addAttribute("produtoNomeAtual", produtoNome);

        return "cardapio/lista";
    }

    // 2. CREATE (Form): Exibir o formulário vazio para um novo item
    @GetMapping("/novo")
    public String exibirFormulario(Model model) {
        model.addAttribute("cardapio", new Cardapio());
        carregarRecursosFormulario(model);
        // View: src/main/resources/templates/cardapio/formulario.html
        return "cardapio/formulario";
    }

    // 3. UPDATE (Form): Exibir o formulário preenchido para edição
    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicao(@PathVariable("id") Long id, Model model) {
        Optional<Cardapio> cardapio = cardapioRepository.findById(id);

        if (cardapio.isPresent()) {
            model.addAttribute("cardapio", cardapio.get());
            carregarRecursosFormulario(model); // Carrega as listas para o <select>
            return "cardapio/formulario";
        }
        return "redirect:/cardapio/listar";
    }

    // 4. CREATE/UPDATE (Persistência): Recebe os dados e salva no BD
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Cardapio cardapio, RedirectAttributes attributes) {
        cardapioRepository.save(cardapio);
        attributes.addFlashAttribute("mensagem", "Item do cardápio salvo com sucesso!");
        return "redirect:/cardapio/listar";
    }

    // 5. DELETE: Excluir um item do cardápio
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes attributes) {
        cardapioRepository.deleteById(id);
        attributes.addFlashAttribute("mensagem", "Item do cardápio excluído com sucesso!");
        return "redirect:/cardapio/listar";
    }
}