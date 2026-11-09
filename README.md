# ShowTicket PDV 🎟️ (Java 21 + Spring Boot 3)

Sistema completo de **Ponto de Venda (PDV)** especializado em bilheteria física de shows e eventos, com controle de turnos de caixa, gestão de lotes por setor, regras de meia-entrada, emissão térmica com QR Code e portaria de check-in com prevenção de dupla entrada.

---

## 🌐 Aplicação Online (Produção)

Acesse a demonstração online em produção:  
👉 **[https://showticket.onrender.com/](https://showticket.onrender.com/)**

---

## 🚀 Como Executar Localmente

### Pré-requisitos
- **Java 21 LTS**
- **Maven 3.9+**

### Iniciar Aplicação
```bash
cd pdv-show-ticketing
mvn spring-boot:run
```

Acesse o terminal do PDV no navegador:
👉 **`http://localhost:8080`**

Console do Banco H2:
👉 **`http://localhost:8080/h2-console`** *(JDBC URL: `jdbc:h2:file:./data/ticketing_pdv`, User: `sa`, Password: em branco)*

---

## ⌨️ Atalhos de Teclado no PDV (F-Keys)

| Tecla | Ação |
| :--- | :--- |
| **`F2`** | Novo Atendimento / Limpar Carrinho de Ingressos |
| **`F7`** | Gestão de Caixa (Sangrias, Suprimentos e Fechamento com Balanço) |
| **`F8`** | Abrir Modal de Pagamento & Checkout (Dinheiro, Cartão, PIX) |
| **`F10`** | Confirmar Venda e Emitir Ingressos Térmicos |
| **`F12`** | Validador de Acesso / Portaria de Catraca |
| **`Esc`** | Fechar modais ativos |

---

## 🏗️ Arquitetura do Sistema

```text
pdv-show-ticketing/
├── src/main/java/com/showticketing/pdv/
│   ├── config/
│   │   ├── DataLoader.java             # Carga inicial com shows, setores e lotes
│   │   └── GlobalExceptionHandler.java # Tratamento global de erros HTTP
│   ├── controller/
│   │   ├── CashSessionController.java  # Endpoints /api/cash-session
│   │   ├── EventController.java        # Endpoints /api/events
│   │   ├── SaleController.java         # Endpoints /api/sales
│   │   └── TicketController.java       # Endpoints /api/tickets (check-in/portaria)
│   ├── dto/                            # DTOs de Request & Response
│   ├── entity/                         # Entidades JPA (Event, Sector, Batch, CashSession, SaleOrder, Ticket, etc.)
│   ├── enums/                          # TicketType, PaymentMethod, TicketStatus, CashSessionStatus
│   ├── exception/                      # BusinessException
│   ├── repository/                     # Repositórios Spring Data JPA
│   ├── service/
│   │   ├── CashSessionService.java     # Lógica financeira e turnos de caixa
│   │   ├── EventService.java           # Catálogo e consultas de eventos
│   │   ├── QrCodeService.java          # Geração de QR Code Base64 nativo (ZXing)
│   │   ├── SaleService.java            # Transação de venda, baixa de estoque e geração de tickets
│   │   └── TicketService.java          # Validação de catraca / portaria anti-duplicidade
│   └── PdvApplication.java             # Entrypoint da aplicação Spring Boot
│
└── src/main/resources/
    ├── application.properties          # Configurações do H2 e porta 8080
    └── static/
        ├── index.html                  # Interface SPA do PDV de Bilheteria
        ├── styles.css                  # Estilo Dark Mode moderno de terminal POS
        └── app.js                      # Lógica reativa, atalhos e sons da catraca
```

---

## 🛣️ Rotas Principais da API REST

### Eventos
- `GET /api/events` - Lista eventos ativos com setores e lotes
- `GET /api/events/{eventId}` - Detalhes do evento

### Caixa do PDV
- `GET /api/cash-session/current` - Retorna a sessão de caixa aberta
- `POST /api/cash-session/open` - Abertura de caixa com fundo de troco
- `POST /api/cash-session/{sessionId}/movement` - Suprimento ou Sangria
- `POST /api/cash-session/{sessionId}/close` - Fechamento de turno e balanço
- `GET /api/cash-session/{sessionId}/summary` - Relatório financeiro em tempo real

### Vendas & Ingressos
- `POST /api/sales` - Efetua venda de balcão e emite os ingressos com QR Code
- `POST /api/tickets/check-in` - Validação na portaria (impede uso duplo de ingressos)
- `GET /api/tickets/{ticketCode}` - Consulta dados de um ingresso emitido
