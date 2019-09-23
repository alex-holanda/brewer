package com.algaworks.brewer.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Grupo.class)
public abstract class Grupo_ {

	public static volatile ListAttribute<Grupo, Permissao> permissoes;
	public static volatile SingularAttribute<Grupo, Long> codigo;
	public static volatile SingularAttribute<Grupo, String> nome;

	public static final String PERMISSOES = "permissoes";
	public static final String CODIGO = "codigo";
	public static final String NOME = "nome";

}

