package com.algaworks.brewer.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.filter.UsuarioFilter;
import com.algaworks.brewer.service.CadastroUsuarioService;
import com.algaworks.brewer.service.StatusUsuario;
import com.algaworks.brewer.service.exception.EmailJaCadastradoException;
import com.algaworks.brewer.service.exception.SenhaObrigatoriaUsuarioException;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController {
	
	@Autowired
	private CadastroUsuarioService service;
	
	@RequestMapping("/novo")
	public ModelAndView novo(Usuario usuario) {
		ModelAndView mv = new ModelAndView("usuario/CadastroUsuario");
		
		mv.addObject("grupos", service.grupos());
		
		return mv;
	}
	
	@GetMapping
	public ModelAndView pesquisar(UsuarioFilter usuarioFilter, BindingResult result
			, @PageableDefault(size=2) Pageable pageable, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("/usuario/PesquisaUsuarios");
		
		mv.addObject("grupos", service.grupos());
		
		PageWrapper<Usuario> pageWrapper = new PageWrapper<>(service.filtrar(usuarioFilter, pageable), request);
		
		mv.addObject("pagina", pageWrapper);
		
		return mv;
	}
	
	@PostMapping("/novo")
	public ModelAndView cadastrar(@Valid Usuario usuario, BindingResult result, RedirectAttributes attributes) {
		
		if (result.hasErrors()) {
			return novo(usuario);
		}
		
		try {
			service.salvar(usuario);
		} catch (EmailJaCadastradoException e) {
			result.rejectValue("email", e.getMessage(), e.getMessage());
			return novo(usuario);
		} catch (SenhaObrigatoriaUsuarioException e) {
			result.rejectValue("senha", e.getMessage(), e.getMessage());
			return novo(usuario);
		}
		
		attributes.addFlashAttribute("mensagem", "Usuário salvo com sucesso");
	
		return new ModelAndView("redirect:/usuarios/novo");
	}

	@PutMapping("/status")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarStatus(@RequestParam("codigos[]") Long[] codigos, @RequestParam("status") StatusUsuario statusUsuarios) {
		Arrays.asList(codigos).forEach(System.out::println);
		
		service.alterarStatus(codigos, statusUsuarios);
	}
}
