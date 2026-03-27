# 🛒 E-Commerce Backend Application

## 📌 Overview
This project is a Spring Boot-based backend application for an E-Commerce platform.  
It implements core functionalities like authentication, product management, cart system, order processing, and role-based access control using a clean and scalable architecture.

---

## 🚀 Features

### 🔐 Authentication & Authorization
- User Signup & Login APIs  
- JWT-based authentication  
- Role-based access control (User/Admin)  
- Spring Security integration  

---

### 🛍️ Product & Category Management
- CRUD operations for products  
- Category-based product organization  
- Structured API responses using DTOs  

---

### 🛒 Cart Management
- Add items to cart  
- Update/remove cart items  
- User-specific cart handling  

---

### 📦 Order Management
- Create orders from cart  
- Manage order items  
- Basic order workflow  

---

### 💳 Payment (Foundation)
- Payment entity & DTOs implemented  
- Ready for integration with payment gateways  

---

### 📍 Address Management
- Add and manage user addresses  
- Used during checkout process  

---

### ⚠️ Exception Handling
- Global exception handling  
- Custom exceptions:
  - APIException  
  - ResourceNotFoundException  
- Consistent API responses  

---

## 🧠 Architecture

Controller → Service → Repository → Database

- Controller Layer → Handles HTTP requests  
- Service Layer → Business logic  
- Repository Layer → Database operations (JPA)  
- DTO Layer → Data abstraction  
- Security Layer → JWT + Spring Security  

---

## 🛠️ Tech Stack

- Backend: Java, Spring Boot  
- Security: Spring Security, JWT  
- Database: JPA / Hibernate  
- Build Tool: Maven / Gradle  
- Architecture: RESTful APIs  

---

## 📂 Project Structure

com.ecommerce.project
│
├── config  
├── controller  
├── exceptions  
├── model  
├── payload (DTOs)  
├── repositories  
├── security  
│   ├── jwt  
│   ├── request  
│   ├── response  
│   ├── services  
│
├── service  
├── util  
└── EcomApplication  
