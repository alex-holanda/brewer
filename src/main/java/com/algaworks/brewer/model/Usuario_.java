package com.algaworks.brewer.model;

import java.time.LocalDate;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Usuario.class)
public class Usuario_ {

	public static volatile SingularAttribute<Usuario, Long> codigo;
	public static volatile SingularAttribute<Usuario, String> nome;
	public static volatile SingularAttribute<Usuario, String> email;
	public static volatile SingularAttribute<Usuario, String> senha;
	public static volatile SingularAttribute<Usuario, Boolean> ativo;
	public static volatile SingularAttribute<Usuario, LocalDate> dataNascimento;
	public static volatile SingularAttribute<Usuario, Grupo> grupos;
	
}
