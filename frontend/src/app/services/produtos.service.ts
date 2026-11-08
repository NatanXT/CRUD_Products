
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

/**
 * Produto retornado pelo backend.
 */
export interface Produto {
    id: number;
    nome: string;
    preco: number;
    quantidade: number;
}

/**
 * Dados enviados para cadastro ou atualização.
 */
export interface ProdutoEntrada {
    nome: string;
    preco: number;
    quantidade: number;
}

/**
 * Serviço responsável pela comunicação HTTP com a API.
 */
@Injectable({
    providedIn: 'root'
})
export class ProdutosService {
    private readonly http = inject(HttpClient);
    private readonly url = 'http://localhost:8080/api/produtos';

    listar(): Observable<Produto[]> {
        return this.http.get<Produto[]>(this.url);
    }

    cadastrar(dados: ProdutoEntrada): Observable<Produto> {
        return this.http.post<Produto>(this.url, dados);
    }

    atualizar(id: number, dados: ProdutoEntrada): Observable<Produto> {
        return this.http.put<Produto>(`${this.url}/${id}`, dados);
    }

    excluir(id: number): Observable<void> {
        return this.http.delete<void>(`${this.url}/${id}`);
    }
}
