package com.algaworks.brewer.repository.helper.venda;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.filter.VendaFilter;

public class VendasImpl implements VendasQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Override
	@Transactional(readOnly = true)
	public Page<Venda> filtrar(VendaFilter filtro, Pageable pageable) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Venda> criteria = builder.createQuery(Venda.class);
		Root<Venda> root = criteria.from(Venda.class);
		root.fetch("cliente");
		root.fetch("usuario");
		
		Predicate[] predicates = criarRestricoes(filtro, builder, root);
		criteria.where(predicates);
		
		Sort sort = pageable.getSort();
		if (sort != null) {
			Sort.Order order = sort.iterator().next();
			
			String property = order.getProperty();
			
			criteria.orderBy(order.isAscending() ? builder.asc(root.get(property)) : builder.desc(root.get(property)));
		}
		
		TypedQuery<Venda> query = manager.createQuery(criteria);
		adicionarRestricoesDePagina(query, pageable);
		
		
		
		return new PageImpl<>(query.getResultList(), pageable, total(filtro));
	}
	
	@Override
	@Transactional(readOnly = true)
	public Venda buscarComItens(Long codigo) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Venda> criteria = builder.createQuery(Venda.class);
		Root<Venda> root = criteria.from(Venda.class);
		
		root.fetch("itens");
		
		criteria.where(builder.equal(root.get("codigo"), codigo));
		
		TypedQuery<Venda> query = manager.createQuery(criteria);
		
		return query.getSingleResult();
	}
	
	private Long total(VendaFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Venda> root = criteria.from(Venda.class);
		
		Predicate[] predicates = criarRestricoes(filtro, builder, root);
		criteria.where(predicates);
		
		criteria.select(builder.count(root));
		
		return manager.createQuery(criteria).getSingleResult();
	}
	
	private void adicionarRestricoesDePagina(TypedQuery<Venda> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistros = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistros;
		
		query.setFirstResult(primeiroRegistro);
		query.setMaxResults(totalRegistros);
		
	}

	private Predicate[] criarRestricoes(VendaFilter filtro, CriteriaBuilder builder, Root<Venda> root) {

		List<Predicate> predicates = new ArrayList<>();
		
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getNomeCliente())) {
				Join<Venda, Cliente> rootCliente = root.join("cliente");
				predicates.add(builder.like(rootCliente.get("nome"), "%" + filtro.getNomeCliente() + "%"));
			}
			
			if (filtro.getCodigo() != null) {
				predicates.add(builder.equal(root.get("codigo"), filtro.getCodigo()));
			}
			
			if (filtro.getStatus() != null) {
				predicates.add(builder.equal(root.get("status"), filtro.getStatus()));
			}
			
			if (!StringUtils.isEmpty(filtro.getCpfOuCnpjCliente())) {
				Join<Venda, Cliente> rootCliente = root.join("cliente");
				predicates.add(builder.equal(rootCliente.get("cpfOuCnpj"), TipoPessoa.removerFormatacao(filtro.getCpfOuCnpjCliente())));
			}
			
			if (filtro.getValorMinimo() != null) {
				predicates.add(builder.ge(root.get("valorTotal"), filtro.getValorMinimo()));
			}
			
			if (filtro.getValorMaximo() != null) {
				predicates.add(builder.le(root.get("valorTotal"), filtro.getValorMinimo()));
			}
			
			if (filtro.getDesde() != null && filtro.getAte() != null) {
				predicates.add(builder.between(root.get("dataCriacao"), filtro.getDesde(), filtro.getAte()));
			}
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}

}
