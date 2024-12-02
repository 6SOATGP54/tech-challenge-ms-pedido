package com.tech_challenge.ms_pedido.model;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PedidoProduto {

    private String nome;

    private BigDecimal preco;

    private Categoria categoria;

    private int quantidade;

    public enum Categoria {
        LANCHE,
        ACOMPANHAMENTO,
        BEBIDA,
        SOBREMESA

    }



}
