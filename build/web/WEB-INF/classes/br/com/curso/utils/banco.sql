Create table estado (
	idestado serial primary key,
	nomeestado varchar (100) not null,
	siglaestado varchar (2) not null
	

);

insert into estado (nomeestado,siglaestado) values ('São Paulo','SP');

create table cidade(
    idcidade serial primary key,
    nomecidade varchar(100) not null,
    situacao varchar(1) not null,
    idestado int not null,
   constraint fk_estado foreign key (idestado) references estado(idestado)
);

insert into cidade(nomecidade,situacao,idestado) values ('Fernandópolis', 'A', 1);
insert into cidade(nomecidade,situacao,idestado) values ('Jales', 'A', 1);

create table pessoa(
	idpessoa serial primary key,
	nome varchar(100) not null,
	cpfcnpj varchar(14) not null unique,
	datanascimento date,
	idcidade int,
	login varchar(20),
	senha varchar(20),
	foto text,
	constraint fk_cidade foreign key (idcidade) references cidade
);
insert into pessoa (idpessoa,nome,cpfcnpj,idcidade,login,senha) values (1,'Alyson',12312312312,1,'adm','123');

create table administrador(
	idadministrador serial primary key,
	idpessoa int unique,
	situacao varchar(1),
	permitelogin varchar(1),
	constraint fk_pessoa foreign key (idpessoa) references pessoa
);

insert into administrador (idadministrador,idpessoa,situacao,permitelogin) values (1,1,'A','S');

create table cliente(
	idcliente serial primary key,
	idpessoa int unique,
	observacao varchar(100),
	situacao varchar(1),
	permitelogin varchar(1),
	constraint fk_pessoa foreign key (idpessoa) references pessoa
);

create table fornecedor(
	idfornecedor serial primary key,
	idpessoa int unique,
	enderecoweb varchar(100),
	situacao varchar(1),
	permitelogin varchar(1),
	constraint fk_pessoa foreign key (idpessoa) references pessoa
);

create or replace view usuario as
select p.idpessoa,p.nome,p.cpfcnpj,p.login,p.senha,
c.idcliente as id,'Cliente' as tipo
from pessoa p,cliente c
where c.idpessoa = p.idpessoa and c.situacao = 'A' and
c.permitelogin = 'S'
union 
select p.idpessoa,p.nome,p.cpfcnpj,p.login,p.senha,
f.idfornecedor as id ,'Fornecedor' as tipo 
from pessoa p, fornecedor f
where f.idpessoa = p.idpessoa and f.situacao = 'A' and
f.permitelogin = 'S'
union
select p.idpessoa,p.nome,p.cpfcnpj,p.login,p.senha,
a.idadministrador as id,'Administrador' as tipo
from pessoa p, administrador a
where a.idpessoa = p.idpessoa and a.situacao = 'A' and
a.permitelogin = 'S'
select * from usuario;

