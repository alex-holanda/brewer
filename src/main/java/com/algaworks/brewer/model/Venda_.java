package com.algaworks.brewer.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Venda.class)
public abstract class Venda_ {

	public static volatile SingularAttribute<Venda, Cliente> cliente;
	public static volatile SingularAttribute<Venda, Long> codigo;
	public static volatile SingularAttribute<Venda, String> observacao;
	public static volatile ListAttribute<Venda, ItemVenda> itens;
	public static volatile SingularAttribute<Venda, BigDecimal> valorDesconto;
	public static volatile SingularAttribute<Venda, BigDecimal> valorFrete;
	public static volatile SingularAttribute<Venda, LocalDateTime> dataHoraEntrega;
	public static volatile SingularAttribute<Venda, BigDecimal> valorTotal;
	public static volatile SingularAttribute<Venda, Usuario> usuario;
	public static volatile SingularAttribute<Venda, LocalDateTime> dataCriacao;
	public static volatile SingularAttribute<Venda, StatusVenda> status;

	public static final String CLIENTE = "cliente";
	public static final String CODIGO = "codigo";
	public static final String OBSERVACAO = "observacao";
	public static final String ITENS = "itens";
	public static final String VALOR_DESCONTO = "valorDesconto";
	public static final String VALOR_FRETE = "valorFrete";
	public static final String DATA_HORA_ENTREGA = "dataHoraEntrega";
	public static final String VALOR_TOTAL = "valorTotal";
	public static final String USUARIO = "usuario";
	public static final String DATA_CRIACAO = "dataCriacao";
	public static final String STATUS = "status";

}

