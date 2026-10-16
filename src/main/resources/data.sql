-- Moldes de exemplo para testar a API logo ao subir a aplicação.
-- O limite de ciclos para preventiva varia conforme o tamanho e a complexidade do molde.
INSERT INTO molde (codigo, descricao, cavidades, ciclos_desde_preventiva, limite_ciclos_preventiva, status)
VALUES ('MD-0142', 'Tampa flip-top 28mm', 8, 92000, 100000, 'DISPONIVEL');

INSERT INTO molde (codigo, descricao, cavidades, ciclos_desde_preventiva, limite_ciclos_preventiva, status)
VALUES ('MD-0207', 'Pote 500ml com alça', 2, 31000, 60000, 'DISPONIVEL');

INSERT INTO molde (codigo, descricao, cavidades, ciclos_desde_preventiva, limite_ciclos_preventiva, status)
VALUES ('MD-0315', 'Conector elétrico 4 vias', 16, 151000, 150000, 'DISPONIVEL');
