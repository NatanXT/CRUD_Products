
package com.natanxt.crudprodutos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.natanxt.crudprodutos.dto.ProdutoEntrada;
import com.natanxt.crudprodutos.model.Produto;
import com.natanxt.crudprodutos.service.ProdutoService;

/**
 * Disponibiliza os endpoints REST para
 * gerenciamento de produtos.
 */
@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    /**
     * GET /api/produtos
     * Lista todos os produtos.
     */
    @GetMapping
    public List<Produto> listar() {
        return produtoService.listar();
    }

    /**
     * GET /api/produtos/{id}
     * Consulta um produto pelo identificador.
     */
    @GetMapping("/{id}")
    public Produto buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id);
    }

    /**
     * POST /api/produtos
     * Cadastra um novo produto.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produto cadastrar(@RequestBody ProdutoEntrada entrada) {
        return produtoService.cadastrar(
                entrada.nome(),
                entrada.preco(),
                entrada.quantidade()
        );
    }

    /**
     * PUT /api/produtos/{id}
     * Atualiza um produto existente.
     */
    @PutMapping("/{id}")
    public Produto atualizar(
            @PathVariable Long id,
            @RequestBody ProdutoEntrada entrada
    ) {
        return produtoService.atualizar(
                id,
                entrada.nome(),
                entrada.preco(),
                entrada.quantidade()
        );
    }

    /**
     * DELETE /api/produtos/{id}
     * Exclui um produto existente.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        produtoService.excluir(id);
    }
}
