# Distributed Chat System (Java)

This project implements a distributed chat application with coordinator election and fault tolerance, developed collaboratively as part of the COMP1549 Advanced Programming module.

## 📌 Project Overview

The system simulates a peer-to-peer chat network where:
- Each participant (Member) can send and receive messages
- One node is elected as a **Coordinator**
- The system handles node failures and recovers with re-election
- GUI is implemented using Java Swing

## 🗂️ Project Structure

- `DistributedChatClientGUI.java` – GUI for chat clients  
- `DistributedChatServer.java` – Manages server-side logic  
- `Coordinator.java` – Handles coordinator logic and election  
- `Member.java` – Represents a participant in the distributed system  
- `Test...` files – Unit and integration tests

## 💻 How to Run

1. Compile Java files:
   ```bash
   javac src/*.java

java src.DistributedChatServer
java src.DistributedChatClientGUI
