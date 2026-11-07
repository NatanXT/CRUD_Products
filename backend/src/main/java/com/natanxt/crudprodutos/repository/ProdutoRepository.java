package com.natanxt.crudprodutos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.natanxt.crudprodutos.model.Produto;

/**
 * Repositório para gerenciar operações de persistência de produtos.
 */
public interface ProdutoRepository extends
        JpaRepository<Produto, Long> {
    // Aqui você adiciona métodos de consulta, se necessário.
}
