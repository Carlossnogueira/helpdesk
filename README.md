# 🎫 HelpDesk — Sistema de Chamados

Sistema de suporte técnico (help desk) desenvolvido com **Spring Boot 4** e **Java 17**, que centraliza e organiza solicitações internas. Resolve um problema recorrente em empresas: pedidos perdidos em e-mails, falta de histórico e ausência de responsáveis definidos.

Cada solicitação vira um **chamado (ticket)** com status, prioridade, responsável e histórico de comentários — tudo rastreável e controlado.

---

## 📋 Índice

- [Tecnologias](#-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Configuração](#-configuração)
- [Como rodar](#-como-rodar)
- [Papéis (Roles)](#-papéis-roles)
- [Rotas da API](#-rotas-da-api)
- [Fluxo de uso](#-fluxo-de-uso)
- [Testes](#-testes)

---

## 🛠 Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 4.0.5 |
| PostgreSQL | 15 |
| JWT (auth0 java-jwt) | 4.4.0 |
| Hibernate / JPA | via Spring Boot |
| Lombok | via Spring Boot |
| Springdoc OpenAPI (Swagger) | 3.0.3 |

---

## ✅ Pré-requisitos

- **Java 17** instalado
- **Docker** e **Docker Compose** instalados (para o banco de dados)
- **Gradle** (ou usar o wrapper `./gradlew` incluso)

---

## ⚙️ Configuração

### 1. Banco de Dados

O banco de dados é um **PostgreSQL 15** gerenciado via Docker. Suba o container com:

```bash
docker compose up -d
```

Isso cria automaticamente:

| Propriedade | Valor |
|---|---|
| Host | `localhost` |
| Porta | `5432` |
| Database | `helpdesk` |
| Usuário | `admin` |
| Senha | `admin` |

### 2. `application.properties`

Localização: `src/main/resources/application.properties`

```properties
spring.application.name=helpdesk

# Banco de dados
spring.datasource.url=jdbc:postgresql://localhost:5432/helpdesk
spring.datasource.username=admin
spring.datasource.password=admin
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=your_secret_key
jwt.expiration=3600000
```

> ⚠️ **`jwt.secret`** — troque por uma string segura em produção.  
> **`jwt.expiration`** — valor em milissegundos (padrão: `3600000` = 1 hora).  
> **`ddl-auto=update`** — o Hibernate cria e atualiza as tabelas automaticamente.

---

## ▶️ Como rodar

```bash
# 1. Suba o banco de dados
docker compose up -d

# 2. Execute a aplicação
./gradlew bootRun
```

A aplicação sobe em `http://localhost:8080`.

**Swagger UI** (documentação interativa):
```
http://localhost:8080/swagger-ui.html
```

---

## 👥 Papéis (Roles)

O sistema possui três papéis com permissões distintas:

| Role | Descrição |
|---|---|
| `USER` | Usuário comum. Abre chamados, comenta nos próprios chamados e acompanha o histórico. |
| `SUPPORT` | Suporte técnico. Visualiza todos os chamados, comenta, muda status e define prioridade. |
| `ADMIN` | Administrador. Acesso total: edita, deleta, muda status, define prioridade e comenta. |

> **Obs:** novos usuários são registrados com role `USER` por padrão. A alteração de role deve ser feita diretamente no banco de dados.

---

## 🔀 Rotas da API

### 🔓 Autenticação (pública)

#### `POST /api/users` — Cadastrar usuário
```json
{
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "senha123"
}
```

#### `POST /api/auth` — Login (gera token JWT)
```json
{
  "email": "joao@email.com",
  "password": "senha123"
}
```
**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

> Todas as rotas abaixo exigem o header: `Authorization: Bearer <token>`

---

### 👤 Usuário autenticado

#### `GET /api/me` — Dados do usuário logado
```json
{
  "id": 1,
  "name": "João Silva",
  "role": "USER"
}
```

---

### 🎫 Chamados (Tickets)

| Método | Rota | Roles | Descrição |
|---|---|---|---|
| `POST` | `/api/tickets` | USER, SUPPORT, ADMIN | Abre um novo chamado |
| `GET` | `/api/tickets?page=0` | USER, SUPPORT, ADMIN | Lista chamados com paginação |
| `GET` | `/api/tickets/{id}` | USER, SUPPORT, ADMIN | Detalhes de um chamado |
| `PUT` | `/api/tickets/{id}` | ADMIN | Edita título e descrição |
| `PATCH` | `/api/tickets/{id}/status` | SUPPORT, ADMIN | Atualiza o status |
| `PATCH` | `/api/tickets/{id}/priority` | SUPPORT, ADMIN | Define a prioridade |
| `DELETE` | `/api/tickets/{id}` | SUPPORT, ADMIN | Remove o chamado |

> **USER** só visualiza os próprios chamados.

#### Criar chamado — corpo:
```json
{
  "title": "Impressora não funciona",
  "description": "A impressora do setor financeiro parou de funcionar desde segunda-feira."
}
```

#### Atualizar status — corpo:
```json
{ "status": "CLOSED" }
```
Valores aceitos: `OPEN`, `CLOSED`
> `IN_PROGRESS` é definido automaticamente pelo sistema quando SUPPORT ou ADMIN comentam no chamado.

#### Atualizar prioridade — corpo:
```json
{ "priority": "HIGH" }
```
Valores aceitos: `LOW`, `MEDIUM`, `HIGH`

---

### 💬 Comentários

| Método | Rota | Roles | Descrição |
|---|---|---|---|
| `POST` | `/api/tickets/{ticketId}/comments` | USER, SUPPORT, ADMIN | Adiciona comentário no chamado |
| `GET` | `/api/tickets/{ticketId}/comments` | USER, SUPPORT, ADMIN | Lista todos os comentários do chamado |

#### Adicionar comentário — corpo:
```json
{ "text": "Já verificamos e a impressora precisa de manutenção." }
```

**Regras dos comentários:**
- `USER` só comenta e lê comentários de **chamados que ele abriu**
- `SUPPORT` e `ADMIN` podem comentar em qualquer chamado
- Quando `SUPPORT` ou `ADMIN` comentam em um chamado `OPEN`, o status muda automaticamente para `IN_PROGRESS`
- Chamados com status `CLOSED` **não aceitam novos comentários**

---

## 🔄 Fluxo de uso típico

```
1. Usuário se cadastra        → POST /api/users
2. Usuário faz login          → POST /api/auth  →  recebe token JWT
3. Usuário abre um chamado    → POST /api/tickets
4. Suporte vê os chamados     → GET  /api/tickets
5. Suporte comenta            → POST /api/tickets/{id}/comments  (status → IN_PROGRESS)
6. Suporte define prioridade  → PATCH /api/tickets/{id}/priority
7. Usuário acompanha          → GET  /api/tickets/{id}/comments
8. Suporte fecha o chamado    → PATCH /api/tickets/{id}/status  { "status": "CLOSED" }
```

---

## 🧪 Testes

Os testes são unitários com **Mockito** (sem contexto Spring).

```bash
./gradlew test
```

Cobertura por módulo:

| Módulo | Classes testadas |
|---|---|
| User | `RegisterUserService`, `AuthenticateUserService` |
| Ticket | `RegisterTicketService`, `GetTicketByIdService`, `ListTicketsService`, `UpdateTicketService`, `DeleteTicketService`, `UpdateTicketStatusService`, `SetTicketPriorityService` |
| Comment | `AddCommentService`, `ListCommentsService` |

---

## 📁 Estrutura do projeto

```
src/main/java/.../helpdesk/
├── controller/          # Endpoints REST
├── business/
│   ├── ticket/          # Um service por caso de uso
│   ├── comment/
│   ├── user/
│   └── dto/             # Objetos de entrada e saída
└── infrastructure/
    ├── entity/          # Entidades JPA (User, Ticket, Comment)
    ├── repository/      # Interfaces Spring Data
    ├── security/        # JWT, SecurityFilter, SecurityConfig
    ├── exception/       # Exceções customizadas + GlobalExceptionHandler
    └── config/          # OpenAPI / Swagger config
```

