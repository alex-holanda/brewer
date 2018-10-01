var Brewer = Brewer || {};
Brewer.MascaraCep = (function() {
	
	function MascaraCep() {
		this.inputCep = $('.js-cep-mask');
	}
	
	MascaraCep.prototype.iniciar = function() {
		this.inputCep.on('change', onCepMask.bind(this));
	}
	
	function onCepMask() {
		this.inputCep.mask('00.000-000');
	}
	
	return MascaraCep;
	
}());

$(function() {
	var mascaraCep = new Brewer.MascaraCep();
	mascaraCep.iniciar();
});