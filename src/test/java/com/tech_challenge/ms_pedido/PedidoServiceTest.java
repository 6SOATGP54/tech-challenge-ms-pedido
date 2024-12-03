package com.tech_challenge.ms_pedido;
import com.tech_challenge.ms_pedido.dto.OrdemVendaMercadoPagoDTO;
import com.tech_challenge.ms_pedido.model.Pedido;
import com.tech_challenge.ms_pedido.model.PedidoProduto;
import com.tech_challenge.ms_pedido.repository.PedidoRepository;
import com.tech_challenge.ms_pedido.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private PedidoRepository pedidoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveEfetuarPedidoERetornarQRCode() {
        Pedido pedido = criarPedidoExemplo();

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(rabbitTemplate.receiveAndConvert(eq("pagamento.qrcode"), anyLong()))
                .thenReturn("00020101021243650016COM.MERCADOLIBRE020130636e425f93b-fd59-4e37-9d54-447b71a654aa5204000053039865802BR5909Test Test6009SAO PAULO62070503***6304FA29");

        String qrcode = pedidoService.efetuarPedido(pedido);

        assertNotNull(qrcode);
        assertEquals("00020101021243650016COM.MERCADOLIBRE020130636e425f93b-fd59-4e37-9d54-447b71a654aa5204000053039865802BR5909Test Test6009SAO PAULO62070503***6304FA29", qrcode);
        verify(rabbitTemplate, times(1)).convertAndSend(eq(PedidoService.PEDIDO_EFETUADO), any(OrdemVendaMercadoPagoDTO.class));
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void deveListarPedidosNoPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        Pedido pedido1 = new Pedido();
        Pedido pedido2 = new Pedido();

        when(pedidoRepository.findByCriacaoBetween(inicio, fim)).thenReturn(Arrays.asList(pedido1, pedido2));

        List<Pedido> pedidos = pedidoService.listarPedidos(inicio, fim);

        assertNotNull(pedidos);
        assertEquals(2, pedidos.size());
        verify(pedidoRepository, times(1)).findByCriacaoBetween(inicio, fim);
    }

    @Test
    void deveRetornarStatusPedidoEFinalizarPreparacaoSeNecessario() {
        Pedido pedido = criarPedidoExemplo();
        pedido.setId("pedido123");
        pedido.getPreparacao().addAll(Arrays.asList(Pedido.Preparacao.PREPARACAO, Pedido.Preparacao.PRONTO));

        when(pedidoRepository.findById("pedido123")).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.statusPedido("pedido123");

        assertNotNull(resultado);
        assertTrue(resultado.getPreparacao().contains(Pedido.Preparacao.FINALIZADO));
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void deveRetornarStatusPedidoPronto() {
        Pedido pedido = criarPedidoExemplo();
        pedido.setId("pedido123");
        pedido.getPreparacao().addAll(Arrays.asList(Pedido.Preparacao.RECEBIDO));

        when(pedidoRepository.findById("pedido123")).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.statusPedido("pedido123");

        assertNotNull(resultado);
        assertTrue(resultado.getPreparacao().contains(Pedido.Preparacao.PRONTO));
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void naoDeveFazerNadaQuandoPedidoNaoEncontradoAoConsultarStatus() {
        when(pedidoRepository.findById("pedido123")).thenReturn(Optional.empty());

        Pedido resultado = pedidoService.statusPedido("pedido123");

        assertNull(resultado);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }


    private Pedido criarPedidoExemplo() {
        ArrayList<PedidoProduto> pedidoProdutos = new ArrayList<PedidoProduto>();
        ArrayList<Pedido.Preparacao> preparacao = new ArrayList<Pedido.Preparacao>();

        PedidoProduto pedidoProduto = PedidoProduto.builder()
                .nome("Chá Gelado")
                .preco(new BigDecimal(25.4))
                .categoria(PedidoProduto.Categoria.BEBIDA)
                .quantidade(1)
                .build();

        preparacao.add(Pedido.Preparacao.RECEBIDO);
        pedidoProdutos.add(pedidoProduto);

        Pedido pedido = Pedido.builder()
                .id("1")
                .criacao(LocalDateTime.now())
                .pagamento(Pedido.Status.PENDENTE)
                .preparacao(preparacao)
                .pedidoProdutos(pedidoProdutos)
                .build();

        return pedido;
    }
}
