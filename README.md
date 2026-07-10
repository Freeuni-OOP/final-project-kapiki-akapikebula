[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/skmUAHf8)
# Final-Project
OOP ფინალური პროექტი


# 🚀 კაპიკი აკაპიკებულა (Kapiki akapikebula) - გაჯეტების ფასების აგრეგატორი

**კაპიკი აკაპიკებულა** არის ვებ-პლატფორმა (ფასების აგრეგატორი), რომელიც მომხმარებელს საშუალებას აძლევს სხვადასხვა ონლაინ მაღაზიიდან შეკრებილი ელექტრონიკისა და გაჯეტების ფასები ერთ სივრცეში შეადაროს. სისტემა ავტომატურად ითვლის თითოეული პროდუქტის მინიმალურ და მაქსიმალურ ფასებს ბაზარზე და მომხმარებელს აჩვენებს საუკეთესო შემოთავაზებებს.


---

## 🛠️ ტექნოლოგიური სტეკი (Tech Stack)

* **Backend:** Java 17, Spring Boot (Data JPA, Spring Security, Web)
* **Frontend:** React, JavaScript (Vite)
* **Database:** SQL / PostgreSQL (ან MySQL, რაც გაქვს ჩაწერე)
* **Authentication:** JWT (JSON Web Tokens)

---

## 🧩 გამოყენებული OOP პრინციპები (Object-Oriented Programming)

რადგან პროექტი წარმოადგენს ობიექტზე ორიენტირებული პროგრამირების ფინალურ ნამუშევარს, მასში გამოყენებულია შემდეგი კონცეფციები:
1. **Encapsulation (ინკაფსულაცია):** მონაცემთა დაცვა ენტების (Entities) და DTO-ების (მაგ. `LoginRequest`, `AuthResponse`) დონეზე, სადაც ველები არის `private` და წვდომა ხდება `getters/setters` მეთოდებით.
2. **Abstraction & Interfaces (აბსტრაქცია):** გამოყენებულია ინტერფეისები რეპოზიტორებისთვის (`UserRepository`, `ProductRepository`) და მონაცემთა ოპტიმიზირებული პროექციისთვის (`MatchedProductDTO`).
3. **Separation of Concerns:** კოდი დაყოფილია მკაფიო შრეებად: `Controller` (HTTP რექვესტები), `Service` (ბიზნეს ლოგიკა), `Repository` (მონაცემთა ბაზა) და `Security` (ფილტრები).

---

## 🚀 როგორ გავუშვათ პროექტი (How to Run)

პროექტის ლოკალურად გასაშვებად მიყევით ამ ნაბიჯებს:

### 1. მონაცემთა ბაზის მომზადება (Database)
1. შექმენით ახალი SQL ბაზა სახელით: `kapiki_db`
2. გახსენით ბექენდის ფაილი `src/main/resources/application.properties` და მიუთითეთ თქვენი ბაზის მონაცემები:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5123/kapiki_db
   spring.datasource.username=თქვენი_იუზერი
   spring.datasource.password=თქვენი_პაროლი