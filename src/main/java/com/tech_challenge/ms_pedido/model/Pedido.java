package com.tech_challenge.ms_pedido.model;

import com.fasterxml.jackson.databind.annotation.EnumNaming;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document
public class Pedido {

    private String id;

    private List<PedidoProduto> pedidoProdutos = new ArrayList<>();

    private Status pagamento;

    private LocalDateTime criacao;

    private List<Preparacao> preparacao = new ArrayList<>();

    public enum Status {
        PENDENTE,
        PAGO,
    }

 public enum Preparacao {
     RECEBIDO,
     PREPARACAO,
     PRONTO,
     FINALIZADO
 }

}
