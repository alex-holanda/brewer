package com.algaworks.brewer.repository.helper.cliente;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.Cliente_;
import com.algaworks.brewer.repository.filter.ClienteFilter;

public class ClientesImpl implements ClientesQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Override
	@Transactional(readOnly=true)
	public Page<Cliente> filtrar(ClienteFilter filtro, Pageable pageable) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Cliente> criteria = builder.createQuery(Cliente.class);
		Root<Cliente> root = criteria.from(Cliente.class);
		
		root.fetch(Cliente_.ENDERECO);
		
		Predicate[] predicates = criarRestricoes(filtro, builder, root);
		
		criteria.where(predicates);
		
//		criteria.createAlias("endereco.cidade", "c", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias("c.estado", "e", JoinType.LEFT_OUTER_JOIN);
		
		TypedQuery<Cliente> query = manager.createQuery(criteria);
		
		adicionarRestricoesDePagina(query, pageable);
		
		return new PageImpl<>(query.getResultList(), pageable, total(filtro));
	}

	private Long total(ClienteFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Cliente> root = criteria.from(Cliente.class);
		
		Predicate[] predicates = criarRestricoes(filtro, builder, root);
		
		criteria.where(predicates);
		criteria.select(builder.count(root));

		return manager.createQuery(criteria).getSingleResult();
	}
	
	private void adicionarRestricoesDePagina(TypedQuery<Cliente> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistros = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistros;
		
		query.setFirstResult(primeiroRegistro);
		query.setMaxResults(totalRegistros);
	}

	private Predicate[] criarRestricoes(ClienteFilter filtro, CriteriaBuilder builder, Root<Cliente> root) {
		
		List<Predicate> predicates = new ArrayList<>();
		
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicates.add(builder.like(root.get(Cliente_.nome), "%" + filtro.getNome() + "%"));
			}
			
			if (!StringUtils.isEmpty(filtro.getCpfOuCnpj())) {
				predicates.add(builder.like(root.get(Cliente_.CPF_OU_CNPJ), "%"+ filtro.getCpfOuCnpj() +"%"));
			}
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}
}
