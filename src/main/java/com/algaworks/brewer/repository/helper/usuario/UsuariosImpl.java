package com.algaworks.brewer.repository.helper.usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Grupo;
import com.algaworks.brewer.model.Permissao;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.model.Usuario_;
import com.algaworks.brewer.repository.filter.UsuarioFilter;

public class UsuariosImpl implements UsuariosQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Override
	public Optional<Usuario> porEmailEAtivo(String email) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Usuario> criteria = builder.createQuery(Usuario.class);
		Root<Usuario> root = criteria.from(Usuario.class);
		
		criteria.where(builder.equal(root.get("email"), email)
				, builder.equal(root.get("ativo"), true));
		
		TypedQuery<Usuario> query = manager.createQuery(criteria);
		
		return Optional.of(query.getSingleResult());
		
//		return manager
//				.createQuery("from Usuario where lower(email) = lower(:email) and ativo = true", Usuario.class)
//					.setParameter("email", email).getResultList().stream().findFirst(); 
	}

	@Override
	public List<String> permissoes(Usuario usuario) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<String> criteria  = builder.createQuery(String.class);
		Root<Usuario> root = criteria.from(Usuario.class);
		Join<Usuario, Grupo> rootGrupo = root.join("grupos");
		Join<Grupo, Permissao> rootPermissao = rootGrupo.join("permissoes");
		
		criteria.where(builder.equal(root.get(Usuario_.email), usuario.getEmail()));
		
		Expression<String> permissoes = rootPermissao.get("nome").as(String.class);
		
		CriteriaQuery<String> select = criteria.multiselect(permissoes);
		select.distinct(true);
		
		TypedQuery<String> query = manager.createQuery(select);
		
		return query.getResultList();
		
//		return manager.createQuery(
//				"select distinct p.nome from Usuario u inner join u.grupos g inner join g.permissoes p where u = :usuario", String.class)
//				.setParameter("usuario", usuario)
//				.getResultList();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Usuario> filtrar(UsuarioFilter filtro, Pageable pageable) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Usuario> criteria = builder.createQuery(Usuario.class);
		Root<Usuario> root = criteria.from(Usuario.class);
		
		Predicate[] predicates = criarRestricoes(filtro, builder, root);
		
		criteria.where(predicates);
		
		
		TypedQuery<Usuario> query = manager.createQuery(criteria);
		adicionarRestricoesDePagina(query, pageable);
		
		return new PageImpl<>(query.getResultList(), pageable, total(filtro));
	}

	private Long total(UsuarioFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<Usuario> root = criteria.from(Usuario.class);
		
		Predicate[] predicates = criarRestricoes(filtro, builder, root);
		criteria.where(predicates);
		
		criteria.select(builder.count(root));
		
		return manager.createQuery(criteria).getSingleResult();
	}

	private void adicionarRestricoesDePagina(TypedQuery<Usuario> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistros = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistros;
		
		query.setFirstResult(primeiroRegistro);
		query.setMaxResults(totalRegistros);
		
	}

	private Predicate[] criarRestricoes(UsuarioFilter filtro, CriteriaBuilder builder, Root<Usuario> root) {

		List<Predicate> predicates = new ArrayList<>();
		
		if (filtro != null) {
			
			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicates.add(builder.like(root.get("nome"), "%" + filtro.getNome() + "%"));
			}
			
			if (!StringUtils.isEmpty(filtro.getEmail())) {
				predicates.add(builder.like(root.get("email"), "%" + filtro.getEmail() + "%"));
			}
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}

}
