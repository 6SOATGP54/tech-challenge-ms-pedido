package com.tech_challenge.ms_pedido.service;

import com.tech_challenge.ms_pedido.dto.ItemOrdemVendaDTO;
import com.tech_challenge.ms_pedido.dto.OrdemVendaMercadoPagoDTO;
import com.tech_challenge.ms_pedido.dto.StatusPagamentoDTO;
import com.tech_challenge.ms_pedido.model.Pedido;
import com.tech_challenge.ms_pedido.model.PedidoProduto;
import com.tech_challenge.ms_pedido.repository.PedidoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

@Service
public class PedidoService {

    public static final String PEDIDO_EX = "pedido.ex";
    public static final String PEDIDO_EFETUADO = "pedido.efetuado";

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    PedidoRepository pedidoRepository;

    public String efetuarPedido(Pedido pedido) {

        String identificacaoPedido = UUID.randomUUID().toString();

        pedido.setId(identificacaoPedido);
        pedido.setCriacao(LocalDateTime.now());
        pedido.getPreparacao().add(Pedido.Preparacao.RECEBIDO);

        pedidoRepository.save(pedido);


        OrdemVendaMercadoPagoDTO ordemVendaMercadoPagoDTO = gerarOrdemVenda(pedido, identificacaoPedido);

        rabbitTemplate.convertAndSend(PEDIDO_EFETUADO, ordemVendaMercadoPagoDTO);
        Object o = rabbitTemplate.receiveAndConvert("pagamento.qrcode", 10000);

        String qrcode = (String) o;
        System.out.println(qrcode);

        return qrcode;
    }

    private static OrdemVendaMercadoPagoDTO gerarOrdemVenda(Pedido pedido, String identificacaoPedido) {

        List<ItemOrdemVendaDTO> itemOrdemVendaDTOS = new ArrayList<>();

        pedido.getPedidoProdutos().forEach(produto -> {
            itemOrdemVendaDTOS.add(new ItemOrdemVendaDTO(
                    null,
                    produto.getCategoria().name(),
                    produto.getNome(),
                    produto.getNome().concat(produto.getCategoria().name()),
                    produto.getPreco(),
                    produto.getQuantidade(),
                    "unit",
                    produto.getPreco().multiply(BigDecimal.valueOf(produto.getQuantidade()))));

        });

        BigDecimal total = itemOrdemVendaDTOS.stream()
                .map(ItemOrdemVendaDTO::total_amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrdemVendaMercadoPagoDTO ordemVendaMercadoPagoDTO = new OrdemVendaMercadoPagoDTO(
                "Pedido " + identificacaoPedido,
                identificacaoPedido,
                itemOrdemVendaDTOS,
                "Pedido " + identificacaoPedido,
                total);

        return ordemVendaMercadoPagoDTO;
    }

    public List<Pedido> listarPedidos(LocalDateTime inicio, LocalDateTime fim) {
        return pedidoRepository.findByCriacaoBetween(inicio, fim);
    }

    @RabbitListener(queues = "pagamento.concluido")
    private void iniciarPreparoPedido(StatusPagamentoDTO statusPagamentoDTO) {
        pedidoRepository.findById(statusPagamentoDTO.getIdPedido()).ifPresent(pedido -> {
            pedido.getPreparacao().add(Pedido.Preparacao.PREPARACAO);
            pedido.setPagamento(Pedido.Status.PAGO);
            pedidoRepository.save(pedido);
        });
    }

    public Pedido statusPedido(String idPedido) {
        AtomicReference<Pedido> pedidoPronto = new AtomicReference<>();
        pedidoRepository.findById(idPedido).ifPresent(pedido -> {

            if (pedido.getPreparacao().contains(Pedido.Preparacao.PREPARACAO) &&
                    pedido.getPreparacao().contains(Pedido.Preparacao.PRONTO)) {
                pedido.getPreparacao().add(Pedido.Preparacao.FINALIZADO);
            } else {
                pedido.getPreparacao().add(Pedido.Preparacao.PRONTO);
            }

            pedidoPronto.set(pedidoRepository.save(pedido));
        });

        return pedidoPronto.get();
    }

}
