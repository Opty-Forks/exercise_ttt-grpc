# gRPC Tic Tac Toe exercise

This is the starting point for the gRPC Tic Tac Toe exercise.
The project is composed by three modules:
- [contract](contract) - protocol buffers definition
- [server](server) - implementation of service
- [client](client) - invocation of service

See the README for each module.
Start at [contract](contract/README.md), then go to [server](server/README.md), and finally go to
the [client](client/README.md).

---

# Functionality

New remote procedure called **lastPlay**, with no arguments, which returns information about the last successful play.
This procedure is called when the user indicates "10" as the play number.

---

# To Run:

- In the [contract](contract) folder: `mvn install`;
- In the [server](server) folder: `mvn compile exec:java`;
- In the [client](client) folder: `mvn compile exec:java`.

---

## Authors

**Group T18**

- 86923 [Sara Machado](mailto:sara.f.machado@tecnico.ulisboa.pt)
- 90770 [Rafael Figueiredo](mailto:rafael.alexandre.roberto.figueiredo@tecnico.ulisboa.pt)
- 90774 [Ricardo Grade](mailto:ricardo.grade@tecnico.ulisboa.pt)

---

[SD Faculty](mailto:leic-sod@disciplinas.tecnico.ulisboa.pt)
