package Restaurante.example.Restaurante.controller;

import Restaurante.example.Restaurante.model.Produto;
import Restaurante.example.Restaurante.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;


@Controller
@RequestMapping("/produtos") // Mapeamento base: /produtos/...
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    // 1. READ: Listar todos os produtos (URL: /produtos/listar)
    @GetMapping("/listar")
    public String listar(
            Model model,
            @RequestParam(value = "nomeBusca", required = false, defaultValue = "") String nomeBusca,
            @RequestParam(value = "precoMin", required = false) BigDecimal precoMin
    ) {

        List<Produto> produtos;

        // 1. Lógica AND: Nome E Preço Mínimo preenchidos
        if (!nomeBusca.isEmpty() && precoMin != null) {
            // Usa o novo método combinado: findByNome...AndPreco...
            produtos = produtoRepository.findByNomeContainingIgnoreCaseAndPrecoGreaterThanEqual(nomeBusca, precoMin);
        }
        // 2. Lógica OU: Apenas Nome preenchido
        else if (!nomeBusca.isEmpty()) {
            produtos = produtoRepository.findByNomeContainingIgnoreCase(nomeBusca);
        }
        // 3. Lógica OU: Apenas Preço Mínimo preenchido
        else if (precoMin != null) {
            produtos = produtoRepository.findByPrecoGreaterThanEqual(precoMin);
        }
        // 4. Lógica Padrão: Sem filtros
        else {
            produtos = produtoRepository.findAll();
        }

        model.addAttribute("produtos", produtos);
        // Adiciona os termos de busca atuais para preencher o formulário
        model.addAttribute("nomeBuscaAtual", nomeBusca);
        model.addAttribute("precoMinAtual", precoMin);

        return "produtos/lista";
    }

    // 2. CREATE (Form): Exibir o formulário vazio (URL: /produtos/novo)
    @GetMapping("/novo")
    public String exibirFormulario(Model model) {
        model.addAttribute("produto", new Produto());
        // View: src/main/resources/templates/produtos/formulario.html
        return "produtos/formulario";
    }

    // 3. UPDATE (Form): Exibir o formulário preenchido para edição (URL: /produtos/editar/1)
    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicao(@PathVariable("id") Long id, Model model) {
        Optional<Produto> produto = produtoRepository.findById(id);

        if (produto.isPresent()) {
            model.addAttribute("produto", produto.get());
            return "produtos/formulario";
        }
        return "redirect:/produtos/listar";
    }

    // 4. CREATE/UPDATE (Persistência): Recebe os dados e salva no BD
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Produto produto, RedirectAttributes attributes) {
        produtoRepository.save(produto);

        attributes.addFlashAttribute("mensagem", "Produto salvo com sucesso!");
        return "redirect:/produtos/listar";
    }

    // 5. DELETE: Excluir um produto (URL: /produtos/excluir/1)
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes attributes) {
        try {
            produtoRepository.deleteById(id);
            attributes.addFlashAttribute("mensagem", "Produto excluído com sucesso!");
        } catch (Exception e) {
            attributes.addFlashAttribute("erro", "Erro ao excluir: o produto pode estar em algum cardápio.");
        }
        return "redirect:/produtos/listar";
    }
}