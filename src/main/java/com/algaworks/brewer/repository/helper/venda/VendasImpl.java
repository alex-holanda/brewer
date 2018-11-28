package com.algaworks.brewer.repository.helper.venda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.filter.VendaFilter;

public class VendasImpl implements VendasQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Override
	public BigDecimal valorTotalNoAno() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<BigDecimal> criteria = builder.createQuery(BigDecimal.class);
		Root<Venda> root = criteria.from(Venda.class);
		
		criteria.where(builder.equal(builder.function("YEAR", Integer.class, root.get("dataCriacao")), Year.now().getValue()));
		
		criteria.select(builder.sum(root.get("valorTotal")));
		
		TypedQuery<BigDecimal> query = manager.createQuery(criteria);
		
		return Optional.ofNullable(query.getSingleResult()).orElse(BigDecimal.ZERO);
	}
	
	@Override
	public BigDecimal valorTicketMedio() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<BigDecimal> criteria = builder.createQuery(BigDecimal.class);
		Root<Venda> root = criteria.from(Venda.class);
		
		criteria.where(builder.equal(builder.function("YEAR", Integer.class, root.get("dataCriacao")), Year.now().getValue()));
		
		criteria.multiselect(builder.avg(root.get("valorTotal")));
		
		TypedQuery<BigDecimal> query = manager.createQuery(criteria);
		
		return Optional.ofNullable(query.getSingleResult()).orElse(BigDecimal.ZERO);
	}
	
	@Override
	public BigDecimal valorTotalNoMes() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<BigDecimal> criteria = builder.createQuery(BigDecimal.class);
		Root<Venda> root = criteria.from(Venda.class);
		
		criteria.where(builder.equal(builder.function("MONTH", Integer.class, root.get("dataCriacao")), MonthDay.now().getMonthValue()));
		
		criteria.select(builder.sum(root.get("valorTotal")));
		
		TypedQuery<BigDecimal> query = manager.createQuery(criteria);
		
		return Optional.ofNullable(query.getSingleResult()).orElse(BigDecimal.ZERO);
	}
	
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

	@Override
	public List<VendaMes> totalPorMes() {
		List<VendaMes> vendaMes = manager.createNamedQuery("Vendas.totalPorMes", VendaMes.class).getResultList();
	
		LocalDate hoje = LocalDate.now();
		
		for (int i = 1; i <= 6; i++) {
			String mesIdeal = String.format("%d/%02d", hoje.getYear(), hoje.getMonthValue());
			
			boolean possuiMes = vendaMes.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			
			if (!possuiMes) {
				vendaMes.add(i -1, new VendaMes(mesIdeal, 0));
			}
			
			hoje = hoje.minusMonths(1);
		}
		
		return vendaMes;
	}
	
}
