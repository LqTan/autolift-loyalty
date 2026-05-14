# Sandbox Module

Module `sandbox` dùng để test technical foundation.

## Mục tiêu test

- Spring Boot chạy được
- Spring Modulith nhận module được
- PostgreSQL connection chạy được
- Flyway migration chạy được
- schema sandbox tạo được
- command/query flow chạy được
- Modulith verification test chạy được

## Cấu trúc chuẩn

```
sandbox
├── package-info.java
├── api
│   ├── command
│   │   ├── SandboxCommandController.java
│   │   └── CreateSandboxRequest.java
│   └── query
│       ├── SandboxQueryController.java
│       └── SandboxResponse.java
├── application
│   ├── command
│   │   ├── CreateSandboxCommand.java
│   │   ├── CreateSandboxHandler.java
│   │   └── SandboxCreatedResult.java
│   └── query
│       ├── GetSandboxQuery.java
│       ├── GetSandboxHandler.java
│       └── SandboxView.java
├── domain
│   ├── model
│   │   └── Sandbox.java
│   ├── valueobject
│   │   └── SandboxId.java
│   ├── repository
│   │   └── SandboxRepository.java
│   └── exception
│       └── SandboxNotFoundException.java
├── events
│   ├── package-info.java
│   └── SandboxCreatedEvent.java
└── infrastructure
    └── persistence
        ├── entity
        │   └── SandboxJpaEntity.java
        ├── mapper
        │   └── SandboxPersistenceMapper.java
        ├── repository
        │   ├── SandboxJpaRepository.java
        │   └── SandboxRepositoryAdapter.java
        └── readmodel
            └── SandboxReadRepository.java
```

## SandboxId as @Embeddable

```java
@Embeddable
public class SandboxId {
    private UUID id;

    private SandboxId(UUID id) {
        this.id = id;
    }

    public static SandboxId of(UUID id) { ... }
    public static SandboxId of(String id) { ... }
    public static SandboxId random() { ... }
}
```

## Sandbox Entity

```java
@Entity
@Table(name = "sandboxes", schema = "sandbox")
public class Sandbox {
    @EmbeddedId
    private SandboxId id;

    private String name;
}
```