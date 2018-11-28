package com.algaworks.brewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Vendas;

@Controller
public class DashboardController {

	@Autowired
	private Vendas vendas;
	
	@Autowired
	private Cervejas cervejas;
	
	@GetMapping("/")
	public ModelAndView dashboard() {
		ModelAndView mv = new ModelAndView("Dashboard");
	
		mv.addObject("vendasNoAno", vendas.valorTotalNoAno());
		
		mv.addObject("vendasNoMes", vendas.valorTotalNoMes());
		
		mv.addObject("ticketMedio", vendas.valorTicketMedio());
		
		mv.addObject("quantidadeEstoque", cervejas.quantidadeItensNoEstoque());
		
		mv.addObject("valorTotalDoEstoque", cervejas.valorTotalDoEstoque());
		
		return mv;
	}
}
