package com.br.curso.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.br.curso.domain.Cliente;
import com.br.curso.domain.ItemPedido;
import com.br.curso.domain.PagamentoComBoleto;
import com.br.curso.domain.Pedido;
import com.br.curso.domain.enums.EstadoPagamento;
import com.br.curso.repositories.ClienteRepository;
import com.br.curso.repositories.ItemPedidoRepository;
import com.br.curso.repositories.PagamentoRepository;
import com.br.curso.repositories.PedidoRepository;
import com.br.curso.repositories.ProdutoRepository;
import com.br.curso.services.exception.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private ItemPedidoRepository itemRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EmailService emailServer;
	
	
	
	public Pedido findById(Integer id) {
		
		Pedido pedido = pedidoRepository.findById(id)
				.orElseThrow(() -> new ObjectNotFoundException("Pedido não encontrado"));
		
		return pedido;
	}
	
	@Transactional
	public Pedido insertPedido(Pedido obj) {
		obj.setId(null);
		obj.setCliente(clienteRepository.findById(obj.getCliente().getId())
				.orElseThrow(() -> new ObjectNotFoundException("Pedido não encontrado")));
		obj.setInstante(new Date());
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if(obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pgto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pgto, obj.getInstante());
		}
		obj = pedidoRepository.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		
		for(ItemPedido ip: obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setProduto(produtoService.findById(ip.getProduto().getId()));
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);
		}
		
		itemRepository.saveAll(obj.getItens());
		
		emailServer.sendOrderConfirmationEmail(obj);
		
		return obj;
	}
	
	
}
