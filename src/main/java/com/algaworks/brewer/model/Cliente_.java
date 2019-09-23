package com.algaworks.brewer.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Cliente.class)
public abstract class Cliente_ {

	public static volatile SingularAttribute<Cliente, String> cpfOuCnpj;
	public static volatile SingularAttribute<Cliente, Long> codigo;
	public static volatile SingularAttribute<Cliente, TipoPessoa> tipoPessoa;
	public static volatile SingularAttribute<Cliente, String> telefone;
	public static volatile SingularAttribute<Cliente, Endereco> endereco;
	public static volatile SingularAttribute<Cliente, String> nome;
	public static volatile SingularAttribute<Cliente, String> email;

	public static final String CPF_OU_CNPJ = "cpfOuCnpj";
	public static final String CODIGO = "codigo";
	public static final String TIPO_PESSOA = "tipoPessoa";
	public static final String TELEFONE = "telefone";
	public static final String ENDERECO = "endereco";
	public static final String NOME = "nome";
	public static final String EMAIL = "email";

}

