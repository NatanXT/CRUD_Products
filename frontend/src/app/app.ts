
import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnInit,
  signal
} from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { finalize } from 'rxjs';

import {
  Produto,
  ProdutoEntrada,
  ProdutosService
} from './services/produtos.service';

/**
 * Componente principal do CRUD de produtos.
 *
 * Utiliza Reactive Forms para os campos e Signals
 * para atualizar a interface no Angular 21.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ReactiveFormsModule, CurrencyPipe],
  templateUrl: './app.html',
  styleUrl: './app.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class App implements OnInit {

  private readonly produtosService = inject(ProdutosService);
  private readonly formBuilder = inject(FormBuilder);

  // Estados reativos da interface.
  readonly produtos = signal<Produto[]>([]);
  readonly carregando = signal(false);
  readonly salvando = signal(false);
  readonly excluindo = signal(false);

  readonly produtoEmEdicao = signal<number | null>(null);

  readonly erro = signal('');
  readonly sucesso = signal('');

  /**
   * Formulário reativo utilizado no cadastro e edição.
   */
  readonly formulario = this.formBuilder.nonNullable.group({
    nome: ['', [
      Validators.required,
      Validators.maxLength(255)
    ]],
    preco: [0, [
      Validators.required,
      Validators.min(0),
      Validators.max(99999999.99)
    ]],
    quantidade: [0, [
      Validators.required,
      Validators.min(0)
    ]]
  });

  /**
   * Carrega os produtos quando a tela é iniciada.
   */
  ngOnInit(): void {
    this.listarProdutos();
  }

  /**
   * Consulta os produtos cadastrados na API.
   */
  listarProdutos(): void {
    this.carregando.set(true);
    this.erro.set('');

    this.produtosService.listar()
      .pipe(
        finalize(() => this.carregando.set(false))
      )
      .subscribe({
        next: (produtos) => {
          this.produtos.set(produtos);
        },
        error: (erro: unknown) => {
          this.erro.set(this.tratarErro(erro));
        }
      });
  }

  /**
   * Cadastra ou atualiza um produto.
   */
  salvarProduto(): void {
    if (this.salvando()) {
      return;
    }

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.erro.set('Preencha os campos corretamente.');
      return;
    }

    const valores = this.formulario.getRawValue();

    const dados: ProdutoEntrada = {
      nome: valores.nome.trim(),
      preco: valores.preco,
      quantidade: valores.quantidade
    };

    // Verifica se o preço possui até duas casas decimais.
    const precoValido =
      typeof dados.preco === 'number' &&
      Number.isFinite(dados.preco) &&
      /^\d+(\.\d{1,2})?$/.test(String(dados.preco)) &&
      dados.preco <= 99999999.99;

    // Quantidade precisa ser um inteiro não negativo.
    const quantidadeValida =
      typeof dados.quantidade === 'number' &&
      Number.isInteger(dados.quantidade) &&
      dados.quantidade >= 0;

    if (
      !dados.nome ||
      dados.nome.length > 255 ||
      !precoValido ||
      !quantidadeValida
    ) {
      this.erro.set(
        'Informe um nome, preço válido e quantidade inteira.'
      );
      return;
    }

    this.salvando.set(true);
    this.erro.set('');
    this.sucesso.set('');

    const id = this.produtoEmEdicao();

    const requisicao = id === null
      ? this.produtosService.cadastrar(dados)
      : this.produtosService.atualizar(id, dados);

    requisicao
      .pipe(
        finalize(() => this.salvando.set(false))
      )
      .subscribe({
        next: () => {
          this.limparFormulario();
          this.sucesso.set('Produto salvo com sucesso!');
          this.listarProdutos();
        },
        error: (erro: unknown) => {
          this.erro.set(this.tratarErro(erro));
        }
      });
  }

  /**
   * Preenche o formulário com os dados do produto.
   */
  editarProduto(produto: Produto): void {
    this.produtoEmEdicao.set(produto.id);

    this.formulario.setValue({
      nome: produto.nome,
      preco: produto.preco,
      quantidade: produto.quantidade
    });

    this.erro.set('');
    this.sucesso.set('');
  }

  /**
   * Exclui um produto após a confirmação do usuário.
   */
  excluirProduto(produto: Produto): void {
    if (this.excluindo()) {
      return;
    }

    const confirmado = window.confirm(
      `Deseja excluir o produto "${produto.nome}"?`
    );

    if (!confirmado) {
      return;
    }

    this.excluindo.set(true);
    this.erro.set('');
    this.sucesso.set('');

    this.produtosService.excluir(produto.id)
      .pipe(
        finalize(() => this.excluindo.set(false))
      )
      .subscribe({
        next: () => {
          if (this.produtoEmEdicao() === produto.id) {
            this.limparFormulario();
          }

          this.sucesso.set('Produto excluído com sucesso!');
          this.listarProdutos();
        },
        error: (erro: unknown) => {
          this.erro.set(this.tratarErro(erro));
        }
      });
  }

  /**
   * Limpa os campos e encerra o modo de edição.
   */
  limparFormulario(): void {
    this.produtoEmEdicao.set(null);

    this.formulario.reset({
      nome: '',
      preco: 0,
      quantidade: 0
    });
  }

  /**
   * Converte erros HTTP em mensagens compreensíveis.
   */
  private tratarErro(erro: unknown): string {
    if (erro instanceof HttpErrorResponse) {
      if (erro.status === 0) {
        return 'Não foi possível conectar à API Spring Boot. Verifique o backend e o CORS.';
      }

      if (typeof erro.error?.erro === 'string') {
        return erro.error.erro;
      }

      return `Erro na requisição: HTTP ${erro.status}.`;
    }

    return 'Ocorreu um erro inesperado.';
  }
}
