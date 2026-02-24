> ### ⚠️ **Este projeto ainda está em desenvolvimento.** Funcionalidades, estruturas e documentação estão sujeitas a alterações.

---

# SafeBox — Smart Locker Management System

## Visão Geral

SafeBox é um sistema para gerenciamento de armários inteligentes (lockers) em condomínios residenciais. O sistema resolve o problema logístico de recebimento de encomendas, eliminando a dependência de porteiros e a necessidade de o morador estar presente no momento da entrega: uma empresa entregadora deposita o pacote em um locker físico, e o sistema gerencia todo o ciclo de vida dessa operação — da reserva do locker à retirada pelo morador.

A comunicação com os lockers físicos ocorre via protocolo **MQTT** sobre TLS, tornando o sistema um ponto de integração entre uma API REST convencional e dispositivos IoT em tempo real.

## Arquitetura e Design

O projeto segue uma arquitetura **monolítica por camadas**, organizada por domínio de negócio. Cada domínio possui seu próprio conjunto de entidades, repositórios, serviços e controllers:

```
com.senai.safebox
│
├── domains/
│   ├── enterprise/     → Empresas entregadoras
│   ├── house/          → Unidades habitacionais
│   ├── locker/         → Armários físicos
│   ├── reserve/        → Reservas de locker
│   ├── resident/       → Moradores
│   └── user/           → Usuários do sistema (auth)
│
├── brokerMQTT/         → Comunicação com hardware IoT
│   ├── config/         → Configuração de canais Spring Integration
│   ├── controller/     → Endpoint REST para operações de box
│   ├── dto/            → Payloads MQTT
│   └── service/        → Orquestração publish/subscribe
│
├── security/           → JWT + Spring Security
│   ├── authentication/ → UserDetails, login, registro
│   ├── config/         → SecurityFilterChain
│   └── jwt/            → Encoder, Decoder, Service
│
├── wapAPI/             → Integração WhatsApp (notificações)
│   ├── client/         → RestClient para API externa
│   ├── domain/         → Entidade de mensagens
│   ├── dtos/           → Request/Response DTOs
│   └── service/        → Orquestração de envio
│
└── config/
    └── initializers/   → Seeds de banco de dados
```

**Separação de responsabilidades:** cada camada tem fronteira clara. O `Controller` apenas delega ao `Service`, que orquestra a lógica de negócio e aciona o `Repository` (Spring Data JPA). DTOs são usados nas bordas da API, nunca expondo entidades diretamente no payload de criação.

**Projeções tipadas:** em vez de buscar entidades completas com joins implícitos, o sistema usa **record-based projections** (`LockerDashboardProjection`, `ReserveDetailsProjection`) construídas via JPQL, evitando over-fetching e expondo exatamente o shape necessário para cada contexto de leitura.

##  Principais Fluxos

### Criação de Reserva

Quando uma entrega é registrada, o sistema associa um locker disponível a um morador e a uma empresa entregadora. O locker tem seu status atualizado para ocupado, e o hardware embarcado é notificado via MQTT para configurar o acesso físico ao armário. A reserva nasce com um status de sincronização que rastreia se a comunicação com o hardware foi bem-sucedida.

### Retirada de Encomenda

Após o morador retirar a encomenda, o operador confirma o pickup pelo sistema. A reserva é encerrada, o horário de retirada é registrado e o locker retorna automaticamente ao estado disponível para novas entregas.

### Desbloqueio Remoto

O sistema permite abrir um locker remotamente pelo dashboard, sem necessidade de acesso físico. O comando é enviado diretamente ao hardware via MQTT. Se a comunicação falhar, o estado do locker não é alterado no banco de dados, garantindo consistência entre o sistema e o mundo físico.

### Recepção de Status do Hardware

O backend escuta mensagens publicadas pelo hardware em tempo real. Quando o ESP32 confirma uma entrega ou responde a uma consulta de status, o sistema processa a informação e atualiza o estado correspondente. Há suporte a comunicação síncrona, onde a API aguarda a resposta do hardware por um tempo configurável antes de retornar ao cliente.

### Autenticação e Autorização

O acesso ao sistema é protegido por tokens JWT. Após o login com credenciais válidas, o sistema emite um token que carrega a identidade e o papel do usuário. Rotas administrativas exigem papel específico, enquanto os endpoints do hardware permanecem públicos para viabilizar a comunicação com o ESP32.
