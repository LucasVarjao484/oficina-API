CREATE TABLE dono (
    id UUID PRIMARY KEY,
    nome varchar(100) NOT NULL,
    cpf varchar(11) UNIQUE NOT NULL,
    endereco varchar(150) NOT NULL,
    data_nascimento DATE
);

CREATE TABLE veiculo (
    id UUID PRIMARY KEY,
    marca varchar(100) NOT NULL,
    modelo varchar(150) NOT NULL,
    ano int NOT NULL,
    dono_id UUID NOT NULL,

    CONSTRAINT fk_veiculo_dono FOREIGN KEY (dono_id) REFERENCES dono(id)
);

CREATE TABLE atendimento (
    id UUID PRIMARY KEY,
    data_atendimento DATE NOT NULL,
    total_centavos INT NOT NULL,
    status VARCHAR(20) NOT NULL,

    CONSTRAINT chk_status CHECK ( status IN ('ABERTO', 'FINALIZADO', 'CANCELADO'))
);

CREATE TABLE servico (
    id UUID PRIMARY KEY,
    nome varchar(200) NOT NULL,
    preco_padrao_centavos INT NOT NULL
);

CREATE TABLE item_atendimento (
    id UUID PRIMARY KEY,
    veiculo_id UUID NOT NULL,
    atendimento_id UUID NOT NULL,
    servico_id UUID NOT NULL,
    quantidade INT NOT NULL,
    preco_final_centavos INT NOT NULL,

    CONSTRAINT fk_item_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculo(id),
    CONSTRAINT fk_item_atendimento FOREIGN KEY (atendimento_id) REFERENCES atendimento(id),
    CONSTRAINT fk_item_servico FOREIGN KEY (servico_id) REFERENCES servico(id)
);
