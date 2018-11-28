package com.algaworks.brewer.dto;

public class VendaMes {

	private String mes;
	private Integer total;

	public VendaMes() {
		
	}
	
	public VendaMes(String mes, Integer total) {
		this.mes = mes;
		this.total = total;
	}

	public String getMes() {
		return mes;
	}

	public Integer getTotal() {
		return total;
	}
	
}
