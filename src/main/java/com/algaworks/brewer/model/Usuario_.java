package com.algaworks.brewer.model;

import java.time.LocalDate;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Usuario.class)
public abstract class Usuario_ {

	public static volatile SingularAttribute<Usuario, String> senha;
	public static volatile SingularAttribute<Usuario, Long> codigo;
	public static volatile SingularAttribute<Usuario, Boolean> ativo;
	public static volatile SingularAttribute<Usuario, String> nome;
	public static volatile ListAttribute<Usuario, Grupo> grupos;
	public static volatile SingularAttribute<Usuario, LocalDate> dataNascimento;
	public static volatile SingularAttribute<Usuario, String> email;

	public static final String SENHA = "senha";
	public static final String CODIGO = "codigo";
	public static final String ATIVO = "ativo";
	public static final String NOME = "nome";
	public static final String GRUPOS = "grupos";
	public static final String DATA_NASCIMENTO = "dataNascimento";
	public static final String EMAIL = "email";

}

