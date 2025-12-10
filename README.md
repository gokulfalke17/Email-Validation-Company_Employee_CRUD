Questions | Answers :: [DTO]

1️⃣ What is DTO and why is it used?

DTO stands for Data Transfer Object. It is used to transfer data between layers without exposing internal Entity structure. It improves security, stability, and API design.

2️⃣ Why should we not expose Entity in Controller?

Entity contains database structure

Exposing entity breaks encapsulation

Entity change → API breaks

Entity may contain restricted fields

DTO allows you to hide sensitive fields

3️⃣ What is the difference between Entity and DTO?
Entity	DTO
Mapped to database table	Not mapped to DB
Contains JPA annotations	Contains validation annotations
Internal representation	External API representation
Should not be exposed	Should be exposed
4️⃣ How many DTOs should we create?

Usually:

RequestDTO → For create/update

ResponseDTO → For responses

ListDTO / SummaryDTO (optional)

NestedDTO for relational data

5️⃣ What is nested mapping?

When DTO contains other DTOs inside it, like:

class OrderDTO {
   CustomerDTO customer;
   List<ItemDTO> items;
}


MapStruct supports nested mapping very well.

6️⃣ Can ModelMapper handle nested fields automatically?

Yes, but it becomes slow and unpredictable in large systems.

7️⃣ Which mapper is best for performance?

✔ MapStruct (compile-time, fastest)
❌ ModelMapper (reflection, slow)

8️⃣ Can we use DTO for validation?

Yes, use:

@NotBlank

@Email

@NotNull

@Size

Entity must NOT contain validation.

9️⃣ Should Service return Entity or DTO?

Return DTO always.
Service → Controller should exchange DTO only.

🔟 Is DTO part of MVC?

Yes.
DTO sits between Controller and Service.
