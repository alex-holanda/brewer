package com.algaworks.brewer.repository.helper.cerveja;

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
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.filter.CervejaFilter;

public class CervejasImpl implements CervejasQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Override
	@Transactional(readOnly = true)
	public Page<Cerveja> filtrar(CervejaFilter filtro, Pageable pageable) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Cerveja> criteria = builder.createQuery(Cerveja.class);
		Root<Cerveja> root = criteria.from(Cerveja.class);
		
		Sort sort = pageable.getSort();
		if (sort != null) {
			Sort.Order order = sort.iterator().next();
			
			String property = order.getProperty();
			
			criteria.orderBy(order.isAscending() ? builder.asc(root.get(property)) : builder.desc(root.get(property)));
		}
		
		Predicate[] predicates = criarRestricoes(filtro, builder, root);
		criteria.where(predicates);
		
		TypedQuery<Cerveja> query = manager.createQuery(criteria);
		
		adicionarRestricoesDePaginacao(query, pageable);
		
		return new PageImpl<>(query.getResultList(), pageable, total(filtro));
	}


	private void adicionarRestricoesDePaginacao(TypedQuery<Cerveja> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistros = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistros;
		
		query.setFirstResult(primeiroRegistro);
		query.setMaxResults(totalRegistros);
	}


	private Long total(CervejaFilter filtro) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Cerveja> root = criteria.from(Cerveja.class);
		
		Predicate[] predicates = criarRestricoes(filtro, builder, root);
		criteria.where(predicates);

		criteria.select(builder.count(root));
		
		return manager.createQuery(criteria).getSingleResult();
	}


	private Predicate[] criarRestricoes(CervejaFilter filtro, CriteriaBuilder builder, Root<Cerveja> root) {
		
		List<Predicate> predicates = new ArrayList<>();
		if(filtro != null) {
			if (!StringUtils.isEmpty(filtro.getSku())) {
				predicates.add(builder.equal(root.get("sku"), filtro.getSku()));
			}
			
			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicates.add(builder.like(root.get("nome"), "%" + filtro.getNome() + "%"));
			}
			
			if (isEstiloPresente(filtro)) {
				predicates.add(builder.equal(root.get("estilo"), filtro.getEstilo()));
			}
			
			if (filtro.getSabor() != null) {
				predicates.add(builder.equal(root.get("sabor"), filtro.getSabor()));
			}
			
			if (filtro.getOrigem() != null) {
				predicates.add(builder.equal(root.get("origem"), filtro.getOrigem()));
			}
			
			if (filtro.getValorDe() != null) {
				predicates.add(builder.ge(root.get("valor"), filtro.getValorDe()));
			}
			
			if (filtro.getValorAte() != null) {
				predicates.add(builder.le(root.get("valor") , filtro.getValorAte()));
			}
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}
	
	private boolean isEstiloPresente(CervejaFilter filtro) {
		return filtro.getEstilo() != null && filtro.getEstilo().getCodigo() != null;
	}

}
