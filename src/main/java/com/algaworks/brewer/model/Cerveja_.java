package com.algaworks.brewer.model;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Cerveja.class)
public abstract class Cerveja_ {

	public static volatile SingularAttribute<Cerveja, Long> codigo;
	public static volatile SingularAttribute<Cerveja, Estilo> estilo;
	public static volatile SingularAttribute<Cerveja, BigDecimal> valor;
	public static volatile SingularAttribute<Cerveja, Origem> origem;
	public static volatile SingularAttribute<Cerveja, String> nome;
	public static volatile SingularAttribute<Cerveja, String> descricao;
	public static volatile SingularAttribute<Cerveja, BigDecimal> comissao;
	public static volatile SingularAttribute<Cerveja, String> foto;
	public static volatile SingularAttribute<Cerveja, Sabor> sabor;
	public static volatile SingularAttribute<Cerveja, Integer> quantidadeEstoque;
	public static volatile SingularAttribute<Cerveja, String> sku;
	public static volatile SingularAttribute<Cerveja, String> contentType;
	public static volatile SingularAttribute<Cerveja, BigDecimal> teorAlcoolico;

	public static final String CODIGO = "codigo";
	public static final String ESTILO = "estilo";
	public static final String VALOR = "valor";
	public static final String ORIGEM = "origem";
	public static final String NOME = "nome";
	public static final String DESCRICAO = "descricao";
	public static final String COMISSAO = "comissao";
	public static final String FOTO = "foto";
	public static final String SABOR = "sabor";
	public static final String QUANTIDADE_ESTOQUE = "quantidadeEstoque";
	public static final String SKU = "sku";
	public static final String CONTENT_TYPE = "contentType";
	public static final String TEOR_ALCOOLICO = "teorAlcoolico";

}

