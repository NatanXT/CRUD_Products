
# CRUD Products

Aplicação Full Stack para gerenciamento de produtos, desenvolvida
com Spring Boot, Angular e PostgreSQL.

## Tecnologias

- VsCode
- InteliJ IDE

### Backend

- Java 17
- Spring Boot 4.1.2
- Spring Data JPA e Hibernate
- PostgreSQL 18
- Maven Wrapper
- Springdoc OpenAPI (Swagger)
- JUnit e Mockito

### Frontend

- Angular 21
- TypeScript
- Reactive Forms
- Angular Signals
- HttpClient
- HTML e CSS

## Funcionalidades

- Cadastrar produtos
- Listar produtos
- Consultar produtos por ID
- Editar produtos
- Excluir produtos com confirmação
- Validar nome, preço e quantidade
- Tratar erros de negócio
- Persistir dados no PostgreSQL
- Consultar a documentação Swagger
- Utilizar a interface em desktop e dispositivos móveis

## Arquitetura

O sistema está dividido em duas aplicações:

- backend: API REST desenvolvida com Spring Boot.
- frontend: interface web desenvolvida com Angular.

O Controller recebe as requisições HTTP.
O Service centraliza as regras de negócio e validações.
O Repository realiza as operações de persistência utilizando JPA.
O frontend utiliza Reactive Forms, Signals e HttpClient.

## Pré-requisitos

- JDK 17
- Node.js compatível com Angular 21
- npm
- PostgreSQL
- Git

Não é necessário instalar o Maven globalmente, pois o projeto
utiliza o Maven Wrapper.

## Configuração do banco de dados

Criar no PostgreSQL:

- Banco: `crud_produtos`
- Usuário da aplicação: `crud_produtos_app`

O usuário deve possuir uma senha definida localmente e
permissões sobre o banco.

Após criar o banco, executar o script:

`backend/database/schema.sql`

Exemplo com o psql, na raiz do repositório:

psql -h localhost -p 5432 -U crud_produtos_app -d crud_produtos -f .\backend\database\schema.sql

A aplicação utiliza `spring.jpa.hibernate.ddl-auto=none`.
Portanto, a tabela deve existir antes de iniciar o backend.

## Executar o backend

Acessar a pasta:

cd backend

Configurar a senha por variável de ambiente no PowerShell:

$senhaSegura = Read-Host "Senha do PostgreSQL" -AsSecureString

$credencial = [System.Net.NetworkCredential]::new(
    "crud_produtos_app",
    $senhaSegura
)

$env:DB_PASSWORD = $credencial.Password

Remove-Variable senhaSegura, credencial

Iniciar o servidor no mesmo terminal:

.\mvnw.cmd spring-boot:run

A API estará disponível em:

http://localhost:8080/api/produtos

## Executar o frontend

Em outro terminal:

cd frontend
npm ci
npm start

Acessar:

http://localhost:4200

Para gerar o build de produção:

npm run build

O backend deve estar rodando na porta 8080.

## Endpoints REST

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/produtos` | Listar produtos |
| GET | `/api/produtos/{id}` | Buscar por ID |
| POST | `/api/produtos` | Cadastrar |
| PUT | `/api/produtos/{id}` | Atualizar |
| DELETE | `/api/produtos/{id}` | Excluir |

### Exemplo de cadastro
json
{
  "nome": "Mouse Gamer",
  "preco": 149.90,
  "quantidade": 10
}

### Respostas HTTP

- `200 OK`: consulta ou atualização bem-sucedida.
- `201 Created`: produto cadastrado.
- `204 No Content`: produto excluído.
- `400 Bad Request`: dados rejeitados pelas regras de negócio.
- `404 Not Found`: produto inexistente.

## Swagger

Com a API iniciada, acessar:

http://localhost:8080/swagger-ui.html

Especificação OpenAPI:

http://localhost:8080/v3/api-docs

## Decisões técnicas

- Separação de responsabilidades entre as camadas do backend.
- Utilização de DTO para receber dados da API.
- Utilização de BigDecimal para preços no Java.
- Validação de regras de negócio na camada Service.
- Restrições de integridade no PostgreSQL.
- Senha do banco informada por variável de ambiente.
- CORS habilitado para o Angular local em localhost:4200.
- Reactive Forms para cadastro e edição.
- Angular Signals para atualizar os estados da interface.
- Tema escuro como padrão visual.

## Git Flow

O repositório utiliza as seguintes branches:

- `main`: versão estável.
- `develop`: integração das funcionalidades.
- `feature/*`: desenvolvimento de funcionalidades e documentação.

As alterações são integradas por Pull Requests.

## Autor

NatanXT

Projeto desenvolvido para prática de desenvolvimento Full Stack.
