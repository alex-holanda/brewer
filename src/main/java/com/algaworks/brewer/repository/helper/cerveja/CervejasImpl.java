package com.algaworks.brewer.repository.helper.cerveja;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.algaworks.brewer.dto.CervejaDTO;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Cerveja_;
import com.algaworks.brewer.repository.filter.CervejaFilter;

public class CervejasImpl implements CervejasQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Override
	public Integer quantidadeItensNoEstoque() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Integer> criteria = builder.createQuery(Integer.class);
		Root<Cerveja> root = criteria.from(Cerveja.class);
		
		criteria.select(builder.sum(root.get(Cerveja_.QUANTIDADE_ESTOQUE)));
		
		TypedQuery<Integer> query = manager.createQuery(criteria);
		
		return Optional.ofNullable(query.getSingleResult()).orElse(0);
	}
	
	@Override
	public BigDecimal valorTotalDoEstoque() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<BigDecimal> criteria = builder.createQuery(BigDecimal.class);
		Root<Cerveja> root = criteria.from(Cerveja.class);
		
		criteria.select(builder.sum(builder.prod(root.get(Cerveja_.QUANTIDADE_ESTOQUE), root.get(Cerveja_.VALOR))));
		
		TypedQuery<BigDecimal> query = manager.createQuery(criteria);
		
		return Optional.ofNullable(query.getSingleResult()).orElse(BigDecimal.ZERO);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<Cerveja> filtrar(CervejaFilter filtro, Pageable pageable) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Cerveja> criteria = builder.createQuery(Cerveja.class);
		Root<Cerveja> root = criteria.from(Cerveja.class);
		
//		Sort sort = pageable.getSort();
//		if (sort != null) {
//			Sort.Order order = sort.iterator().next();
//			
//			String property = order.getProperty();
//			
//			criteria.orderBy(order.isAscending() ? builder.asc(root.get(property)) : builder.desc(root.get(property)));
//		}
		
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
				predicates.add(builder.equal(root.get(Cerveja_.SKU), filtro.getSku()));
			}
			
			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicates.add(builder.like(root.get(Cerveja_.NOME), "%" + filtro.getNome() + "%"));
			}
			
			if (isEstiloPresente(filtro)) {
				predicates.add(builder.equal(root.get(Cerveja_.ESTILO), filtro.getEstilo()));
			}
			
			if (filtro.getSabor() != null) {
				predicates.add(builder.equal(root.get(Cerveja_.SABOR), filtro.getSabor()));
			}
			
			if (filtro.getOrigem() != null) {
				predicates.add(builder.equal(root.get(Cerveja_.ORIGEM), filtro.getOrigem()));
			}
			
			if (filtro.getValorDe() != null) {
				predicates.add(builder.ge(root.get(Cerveja_.VALOR), filtro.getValorDe()));
			}
			
			if (filtro.getValorAte() != null) {
				predicates.add(builder.le(root.get(Cerveja_.VALOR) , filtro.getValorAte()));
			}
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}
	
	private boolean isEstiloPresente(CervejaFilter filtro) {
		return filtro.getEstilo() != null && filtro.getEstilo().getCodigo() != null;
	}

	@Override
	public List<CervejaDTO> porSkuOuNome(String skuOuNome) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<CervejaDTO> criteria = builder.createQuery(CervejaDTO.class);
		Root<Cerveja> root = criteria.from(Cerveja.class);
		
		criteria.select(builder.construct(CervejaDTO.class, 
				root.get(Cerveja_.CODIGO), root.get(Cerveja_.SKU), root.get(Cerveja_.NOME), root.get(Cerveja_.ORIGEM),
				root.get(Cerveja_.VALOR), root.get(Cerveja_.FOTO)));
		
		criteria.where(builder.or(builder.like(root.get(Cerveja_.SKU), "%" + skuOuNome + "%")
				, builder.like(root.get(Cerveja_.NOME), "%" + skuOuNome + "%")));
		
		TypedQuery<CervejaDTO> query = manager.createQuery(criteria);
		
		return query.getResultList();
		
//		String jpql = "select new com.algaworks.brewer.dto.CervejaDTO(codigo, sku, nome, origem, valor, foto) "
//				+ "from Cerveja where sku like :skuOuNome or nome like :skuOuNome";
//		
//		List<CervejaDTO> cervejasFiltradas = manager.createQuery(jpql, CervejaDTO.class)
//				.setParameter("skuOuNome", skuOuNome + "%")
//				.getResultList();
//		
//		
//		return cervejasFiltradas;
	}
}
