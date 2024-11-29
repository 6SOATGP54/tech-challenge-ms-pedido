package com.tech_challenge.ms_pedido.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ItemOrdemVendaDTO(
                                @JsonProperty(access = JsonProperty.Access.READ_ONLY)
                                String sku_number,
                                String category,
                                String title,
                                String description,
                                BigDecimal unit_price,
                                int quantity,
                                String unit_measure,
                                BigDecimal total_amount) {
}