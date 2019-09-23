package com.algaworks.brewer.model;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ItemVenda.class)
public abstract class ItemVenda_ {

	public static volatile SingularAttribute<ItemVenda, Long> codigo;
	public static volatile SingularAttribute<ItemVenda, Venda> venda;
	public static volatile SingularAttribute<ItemVenda, Cerveja> cerveja;
	public static volatile SingularAttribute<ItemVenda, Integer> quantidade;
	public static volatile SingularAttribute<ItemVenda, BigDecimal> valorUnitario;

	public static final String CODIGO = "codigo";
	public static final String VENDA = "venda";
	public static final String CERVEJA = "cerveja";
	public static final String QUANTIDADE = "quantidade";
	public static final String VALOR_UNITARIO = "valorUnitario";

}

