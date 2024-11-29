package com.tech_challenge.ms_pedido.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;


public record OrdemVendaMercadoPagoDTO(String description,
                                       String external_reference,
                                       List<ItemOrdemVendaDTO> items,
                                       String title,
                                       BigDecimal total_amount) {
}
