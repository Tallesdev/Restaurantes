package Restaurante.example.Restaurante.controller;

import Restaurante.example.Restaurante.model.Restaurante;
import Restaurante.example.Restaurante.repository.RestauranteRepository;
import Restaurante.example.Restaurante.repository.CardapioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.List;

@Controller
@RequestMapping("/restaurantes") // Define o caminho base (ex: /restaurantes/listar)
public class RestauranteController {

    private final RestauranteRepository restauranteRepository;
    private final CardapioRepository cardapioRepository; // NOVO

    @Autowired
    public RestauranteController(RestauranteRepository restauranteRepository, CardapioRepository cardapioRepository) { // NOVO
        this.restauranteRepository = restauranteRepository;
        this.cardapioRepository = cardapioRepository; // NOVO
    }
    // 1. READ: Listar todos os restaurantes (URL: /restaurantes/listar)
    @GetMapping("/listar")
    public String listar(
            Model model,
            // Parâmetro opcional de busca
            @RequestParam(value = "busca", required = false, defaultValue = "") String busca
    ) {
        List<Restaurante> restaurantes;

        if (!busca.isEmpty()) {
            restaurantes = restauranteRepository.findByNomeContainingIgnoreCaseOrEnderecoContainingIgnoreCase(busca, busca);
        } else {
            restaurantes = restauranteRepository.findAll();
        }

        model.addAttribute("restaurantes", restaurantes);
        model.addAttribute("buscaAtual", busca);

        // *** CUMPRIMENTO DO REQUISITO 5: CHAMANDO A NATIVE QUERY ***
        List<Object[]> relatorioContagem = cardapioRepository.contarItensPorRestaurante();
        model.addAttribute("relatorioContagem", relatorioContagem);

        return "restaurantes/lista";
    }

    // 2. CREATE (Form): Exibir o formulário vazio para um novo cadastro (URL: /restaurantes/novo)
    @GetMapping("/novo")
    public String exibirFormulario(Model model) {
        model.addAttribute("restaurante", new Restaurante());
        // Retorna a view do formulário
        return "restaurantes/formulario";
    }

    // 3. UPDATE (Form): Exibir o formulário preenchido para edição (URL: /restaurantes/editar/1)
    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicao(@PathVariable("id") Long id, Model model) {
        // Busca o restaurante pelo ID. 'Optional' é usado para evitar NullPointerException.
        Optional<Restaurante> restaurante = restauranteRepository.findById(id);

        if (restaurante.isPresent()) {
            model.addAttribute("restaurante", restaurante.get());
            return "restaurantes/formulario";
        }
        // Se não encontrar, redireciona para a lista
        return "redirect:/restaurantes/listar";
    }

    // 4. CREATE/UPDATE (Persistência): Recebe os dados do formulário e salva no BD
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Restaurante restaurante, RedirectAttributes attributes) {
        // O método save() do JpaRepository faz o INSERT (se o ID for null) ou o UPDATE (se o ID existir)
        restauranteRepository.save(restaurante);
        
        attributes.addFlashAttribute("mensagem", "Restaurante salvo com sucesso!");
        // Redireciona para a lista após salvar
        return "redirect:/restaurantes/listar";
    }

    // 5. DELETE: Excluir um restaurante (URL: /restaurantes/excluir/1)
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes attributes) {
        try {
            restauranteRepository.deleteById(id);
            attributes.addFlashAttribute("mensagem", "Restaurante excluído com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("erro", "Erro ao excluir: o restaurante pode ter itens no cardápio.");
        }
        return "redirect:/restaurantes/listar";
    }
}