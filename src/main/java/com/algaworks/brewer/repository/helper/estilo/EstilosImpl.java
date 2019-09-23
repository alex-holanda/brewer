package com.algaworks.brewer.repository.helper.estilo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.model.Estilo_;
import com.algaworks.brewer.repository.filter.EstiloFilter;

public class EstilosImpl implements EstilosQueries {

	@Autowired
	private EntityManager manager;
	
	@Override
	@Transactional(readOnly=true)
	public Page<Estilo> filtrar(EstiloFilter filtro, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Estilo> criteria = builder.createQuery(Estilo.class);
		Root<Estilo> root = criteria.from(Estilo.class);
		
		Predicate[] predicates = criarRestricoes(filtro, builder, root);
		
		criteria.where(predicates);
		
		TypedQuery<Estilo> query = manager.createQuery(criteria);
		
		adicionarRestricoesDePaginas(query, pageable);
		
		return new PageImpl<>(query.getResultList(), pageable, total(filtro));
	}

	private void adicionarRestricoesDePaginas(TypedQuery<Estilo> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistros = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistros;
		
		query.setFirstResult(primeiroRegistro);
		query.setMaxResults(totalRegistros); 
	}

	private Long total(EstiloFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Estilo> root = criteria.from(Estilo.class);
		
		Predicate[] predicates = criarRestricoes(filtro, builder, root);
		
		criteria.where(predicates);
		criteria.select(builder.count(root));
		
		return manager.createQuery(criteria).getSingleResult();
	}

	private Predicate[] criarRestricoes(EstiloFilter filtro, CriteriaBuilder builder, Root<Estilo> root) {
		List<Predicate> predicates  = new ArrayList<>();
		
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicates.add(builder.like(root.get(Estilo_.NOME), "%" + filtro.getNome() + "%"));
			}
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}
}
