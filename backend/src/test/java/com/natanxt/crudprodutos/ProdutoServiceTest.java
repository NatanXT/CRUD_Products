
package com.natanxt.crudprodutos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.natanxt.crudprodutos.model.Produto;
import com.natanxt.crudprodutos.repository.ProdutoRepository;
import com.natanxt.crudprodutos.service.ProdutoService;

/**
 * Testes unitarios para as regras de negocio de ProdutoService.
 *
 * O Repository e simulado pelo Mockito, permitindo validar
 * o comportamento do Service sem acessar o PostgreSQL.
 */
@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

        @Mock
        private ProdutoRepository produtoRepository;

        @InjectMocks
        private ProdutoService produtoService;

        /**
         * Confirma que a listagem retorna os produtos do Repository.
         */
        @Test
        void deveListarProdutos() {
                Produto produto = new Produto(
                                "Teclado",
                                new BigDecimal("150.00"),
                                5);

                when(produtoRepository.findAll())
                                .thenReturn(List.of(produto));

                List<Produto> resultado = produtoService.listar();

                assertEquals(1, resultado.size());
                assertSame(produto, resultado.get(0));

                verify(produtoRepository).findAll();
        }

        /**
         * Confirma a busca de um produto existente.
         */
        @Test
        void deveBuscarProdutoExistente() {
                Produto produto = new Produto(
                                "Mouse",
                                new BigDecimal("80.00"),
                                10);

                when(produtoRepository.findById(1L))
                                .thenReturn(Optional.of(produto));

                Produto resultado = produtoService.buscarPorId(1L);

                assertSame(produto, resultado);

                verify(produtoRepository).findById(1L);
        }

        /**
         * Confirma o erro ao buscar um produto inexistente.
         */
        @Test
        void deveRejeitarBuscaDeProdutoInexistente() {
                when(produtoRepository.findById(99L))
                                .thenReturn(Optional.empty());

                NoSuchElementException erro = assertThrows(
                                NoSuchElementException.class,
                                () -> produtoService.buscarPorId(99L));

                assertTrue(erro.getMessage().contains("99"));
        }

        /**
         * Confirma que o cadastro remove espacos externos
         * do nome e utiliza o Repository para salvar.
         */
        @Test
        void deveCadastrarProdutoValido() {
                when(produtoRepository.save(any(Produto.class)))
                                .thenAnswer(chamada -> chamada.getArgument(0));

                Produto resultado = produtoService.cadastrar(
                                "  Monitor  ",
                                new BigDecimal("899.90"),
                                3);

                assertEquals("Monitor", resultado.getNome());
                assertEquals(
                                new BigDecimal("899.90"),
                                resultado.getPreco());
                assertEquals(3, resultado.getQuantidade());

                verify(produtoRepository).save(any(Produto.class));
        }

        /**
         * Impede o cadastro de produtos sem nome.
         */
        @Test
        void deveRejeitarNomeVazio() {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> produtoService.cadastrar(
                                                "   ",
                                                new BigDecimal("50.00"),
                                                1));

                verifyNoInteractions(produtoRepository);
        }

        /**
         * Impede precos negativos.
         */
        @Test
        void deveRejeitarPrecoNegativo() {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> produtoService.cadastrar(
                                                "Mouse",
                                                new BigDecimal("-10.00"),
                                                2));

                verifyNoInteractions(produtoRepository);
        }

        /**
         * Impede precos com mais de duas casas decimais
         * significativas.
         */
        @Test
        void deveRejeitarPrecoComTresCasasDecimais() {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> produtoService.cadastrar(
                                                "Teclado",
                                                new BigDecimal("25.999"),
                                                2));

                verifyNoInteractions(produtoRepository);
        }

        /**
         * Impede precos superiores ao limite NUMERIC(10,2).
         */
        @Test
        void deveRejeitarPrecoAcimaDoLimite() {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> produtoService.cadastrar(
                                                "Computador",
                                                new BigDecimal("100000000.00"),
                                                1));

                verifyNoInteractions(produtoRepository);
        }

        /**
         * Impede quantidades negativas.
         */
        @Test
        void deveRejeitarQuantidadeNegativa() {
                assertThrows(
                                IllegalArgumentException.class,
                                () -> produtoService.cadastrar(
                                                "Mouse",
                                                new BigDecimal("50.00"),
                                                -1));

                verifyNoInteractions(produtoRepository);
        }

        /**
         * Confirma que um produto existente pode ser atualizado.
         */
        @Test
        void deveAtualizarProdutoExistente() {
                Produto produto = new Produto(
                                "Mouse Antigo",
                                new BigDecimal("50.00"),
                                2);

                when(produtoRepository.findById(1L))
                                .thenReturn(Optional.of(produto));

                when(produtoRepository.save(produto))
                                .thenReturn(produto);

                Produto resultado = produtoService.atualizar(
                                1L,
                                "Mouse Novo",
                                new BigDecimal("75.00"),
                                8);

                assertEquals("Mouse Novo", resultado.getNome());
                assertEquals(
                                new BigDecimal("75.00"),
                                resultado.getPreco());
                assertEquals(8, resultado.getQuantidade());

                verify(produtoRepository).save(produto);
        }

        /**
         * Impede atualizacao de um produto inexistente.
         */
        @Test
        void deveRejeitarAtualizacaoDeProdutoInexistente() {
                when(produtoRepository.findById(99L))
                                .thenReturn(Optional.empty());

                assertThrows(
                                NoSuchElementException.class,
                                () -> produtoService.atualizar(
                                                99L,
                                                "Mouse",
                                                new BigDecimal("50.00"),
                                                1));

                verify(produtoRepository, never())
                                .save(any(Produto.class));
        }

        /**
         * Confirma que um produto existente pode ser excluido.
         */
        @Test
        void deveExcluirProdutoExistente() {
                Produto produto = new Produto(
                                "Mouse",
                                new BigDecimal("50.00"),
                                2);

                when(produtoRepository.findById(1L))
                                .thenReturn(Optional.of(produto));

                produtoService.excluir(1L);

                verify(produtoRepository).delete(produto);
        }

        /**
         * Impede a exclusao de um produto inexistente.
         */
        @Test
        void deveRejeitarExclusaoDeProdutoInexistente() {
                when(produtoRepository.findById(99L))
                                .thenReturn(Optional.empty());

                assertThrows(
                                NoSuchElementException.class,
                                () -> produtoService.excluir(99L));

                verify(produtoRepository, never())
                                .delete(any(Produto.class));
        }
}
