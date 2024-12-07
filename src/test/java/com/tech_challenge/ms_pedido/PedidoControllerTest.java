package com.tech_challenge.ms_pedido;

import com.tech_challenge.ms_pedido.controller.PedidoController;
import com.tech_challenge.ms_pedido.model.Pedido;
import com.tech_challenge.ms_pedido.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PedidoControllerTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoController).build();
    }

    @Test
    void deveEfetuarPedidoComSucesso() throws Exception {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setId("12345");
        when(pedidoService.efetuarPedido(pedido)).thenReturn("QR_CODE");

        // Act & Assert
//        mockMvc.perform(post("/efetuarPedido")
//                        .contentType("application/json")
//                        .content("{\"id\": \"12345\"}")) // Conteúdo JSON para o pedido
//                .andExpect(status().isOk())
//                .andExpect(content().string("QR_CODE"));
//
//        verify(pedidoService).efetuarPedido(pedido); // Verifica se o metodo foi chamado
    }

    @Test
    void deveListarPedidosComSucesso() throws Exception {
        // Arrange
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();
        Pedido pedido1 = new Pedido();
        pedido1.setId("12345");
        Pedido pedido2 = new Pedido();
        pedido2.setId("67890");
        List<Pedido> pedidos = Arrays.asList(pedido1, pedido2);
        when(pedidoService.listarPedidos(inicio, fim)).thenReturn(pedidos);

        // Act & Assert
        mockMvc.perform(get("/listarPedidos")
                        .param("inicio", inicio.toString())
                        .param("fim", fim.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("12345"))
                .andExpect(jsonPath("$[1].id").value("67890"));

        verify(pedidoService).listarPedidos(inicio, fim); // Verifica se o metodo foi chamado
    }

    @Test
    void deveAtualizarStatusPedidoComSucesso() throws Exception {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setId("12345");
        when(pedidoService.statusPedido("12345")).thenReturn(pedido);

        // Act & Assert
        mockMvc.perform(post("/atualizarPedido")
                        .param("idPedido", "12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("12345"));

        verify(pedidoService).statusPedido("12345"); // Verifica se o metodo foi chamado
    }


}
