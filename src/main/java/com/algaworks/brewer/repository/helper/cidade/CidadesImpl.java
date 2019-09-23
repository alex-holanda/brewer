package com.algaworks.brewer.repository.helper.cidade;

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

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Cidade_;
import com.algaworks.brewer.repository.filter.CidadeFilter;

public class CidadesImpl implements CidadesQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Override
	@Transactional(readOnly = true)
	public Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Cidade> criteria = builder.createQuery(Cidade.class);
		Root<Cidade> root = criteria.from(Cidade.class);
		
		Predicate[] predicates = criarRestricoes(filtro, builder, root);
		
		root.fetch(Cidade_.ESTADO);
		
		criteria.where(predicates);
		
		TypedQuery<Cidade> query = manager.createQuery(criteria);
		
		adicionarRestricoesDePagina(query, pageable);
		
		return new PageImpl<>(query.getResultList(), pageable, total(filtro));
	}

	private Long total(CidadeFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Cidade> root = criteria.from(Cidade.class);
		
		Predicate[] predicates = criarRestricoes(filtro, builder, root);
		criteria.where(predicates);
		
		criteria.select(builder.count(root));
		
		return manager.createQuery(criteria).getSingleResult();
	}

	private void adicionarRestricoesDePagina(TypedQuery<Cidade> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistros = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistros;
		
		query.setFirstResult(primeiroRegistro);
		query.setMaxResults(totalRegistros);
		
	}

	private Predicate[] criarRestricoes(CidadeFilter filtro, CriteriaBuilder builder, Root<Cidade> root) {
		List<Predicate> predicates = new ArrayList<>();
		
		if ( filtro != null ) {
			
			if ( !StringUtils.isEmpty(filtro.getNome())) {
				predicates.add(builder.like(root.get(Cidade_.NOME), "%" + filtro.getNome() + "%"));
			}
			
			if ( filtro.getEstado() != null ) {
				predicates.add(builder.equal(root.get(Cidade_.ESTADO), filtro.getEstado()));
			}
			
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}

}
