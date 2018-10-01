package com.algaworks.brewer.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Estado;
import com.algaworks.brewer.repository.Cidades;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.service.exception.CidadeJaCadastradaException;

@Service
public class CadastroCidadeService {

	@Autowired
	private Cidades cidades;
	
	@Autowired
	private Estados estados;
	
	@Transactional
	public Cidade salvar(Cidade cidade) {
		
		Optional<Cidade> cidadeOptional = cidades.findByNomeIgnoreCaseAndEstado(cidade.getNome(), cidade.getEstado());
		
		if (cidadeOptional.isPresent()) {
			throw new CidadeJaCadastradaException("Cidade já cadastrada");
		}
		
		return cidades.saveAndFlush(cidade);
	}
	
	public List<Estado> estados() {
		return estados.findAll();
	}

	public List<Cidade> estadoByCodigo(Long codigoEstado) {
		return cidades.findByEstadoCodigo(codigoEstado);
	}

	public Page<Cidade> filtrar(CidadeFilter cidadeFilter, Pageable pageable) {
		return cidades.filtrar(cidadeFilter, pageable);
	}
}
