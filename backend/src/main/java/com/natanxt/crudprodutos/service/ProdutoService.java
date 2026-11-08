
package com.natanxt.crudprodutos.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.natanxt.crudprodutos.model.Produto;
import com.natanxt.crudprodutos.repository.ProdutoRepository;

/**
 * Serviço para gerenciar operações relacionadas a produtos.
 */
@Service
public class ProdutoService {

    private static final int TAMANHO_MAXIMO_NOME = 255;

    private static final BigDecimal PRECO_MAXIMO = new BigDecimal("99999999.99");

    private final ProdutoRepository produtoRepository;

    /**
     * Recebe o repositório de produtos via injeção de dependência.
     */
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    /**
     * Lista todos os produtos cadastrados.
     */
    @Transactional(readOnly = true)
    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    /**
     * Busca um produto pelo ID.
     */
    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Produto não encontrado: " + id));
    }

    /**
     * Valida e cadastra um novo produto.
     */
    @Transactional
    public Produto cadastrar(
            String nome,
            BigDecimal preco,
            Integer quantidade) {

        validarDados(nome, preco, quantidade);

        Produto produto = new Produto(
                nome.strip(),
                preco,
                quantidade);

        return produtoRepository.save(produto);
    }

    /**
     * Atualiza um produto existente.
     */
    @Transactional
    public Produto atualizar(
            Long id,
            String nome,
            BigDecimal preco,
            Integer quantidade) {

        validarDados(nome, preco, quantidade);

        Produto produto = buscarPorId(id);

        produto.setNome(nome.strip());
        produto.setPreco(preco);
        produto.setQuantidade(quantidade);

        return produtoRepository.save(produto);
    }

    /**
     * Exclui um produto pelo ID.
     */
    @Transactional
    public void excluir(Long id) {
        Produto produto = buscarPorId(id);
        produtoRepository.delete(produto);
    }

    /**
     * Valida os dados antes de enviar ao banco.
     */
    private void validarDados(
            String nome,
            BigDecimal preco,
            Integer quantidade) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do produto é obrigatório.");
        }

        if (nome.strip().length() > TAMANHO_MAXIMO_NOME) {
            throw new IllegalArgumentException(
                    "O nome deve ter no máximo 255 caracteres.");
        }

        if (preco == null || preco.signum() < 0) {
            throw new IllegalArgumentException(
                    "O preço deve ser maior ou igual a zero.");
        }

        if (preco.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException(
                    "O preço deve ter no máximo duas casas decimais.");
        }

        if (preco.compareTo(PRECO_MAXIMO) > 0) {
            throw new IllegalArgumentException(
                    "O preço excede o valor máximo permitido.");
        }

        if (quantidade == null || quantidade < 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior ou igual a zero.");
        }
    }
}
