package com.tech_challenge.ms_pedido.repository;

import com.tech_challenge.ms_pedido.model.Pedido;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends MongoRepository<Pedido, String> {
    List<Pedido> findByCriacaoBetween(LocalDateTime criacaoStart, LocalDateTime criacaoEnd);
}
