
package com.natanxt.crudprodutos.dto;

import java.math.BigDecimal;

/**
 * Representa os dados recebidos
 * de cadastro e atualizacao de produtos.
 */
public record ProdutoEntrada(
        String nome,
        BigDecimal preco,
        Integer quantidade) {
}
