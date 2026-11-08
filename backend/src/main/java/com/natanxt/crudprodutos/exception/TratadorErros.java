
package com.natanxt.crudprodutos.exception;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centraliza o tratamento de erros de negocio
 * e converte excecoes em respostas HTTP.
 */
@RestControllerAdvice
public class TratadorErros {

    /**
     * Retorna HTTP 404 quando o produto nao existe.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> tratarNaoEncontrado(
            NoSuchElementException excecao
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", excecao.getMessage()));
    }

    /**
     * Retorna HTTP 400 quando os dados sao invalidos.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarDadosInvalidos(
            IllegalArgumentException excecao
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("erro", excecao.getMessage()));
    }
}
