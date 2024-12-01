package com.tech_challenge.ms_pedido.controller;


import com.tech_challenge.ms_pedido.model.Pedido;
import com.tech_challenge.ms_pedido.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping()
public class PedidoController {

    @Autowired
    PedidoService pedidoService;

    @PostMapping("/efetuarPedido")
    public ResponseEntity<String> efetuarPedido(@Validated @RequestBody Pedido pedido) {
        return ResponseEntity.ok(pedidoService.efetuarPedido(pedido));
    }


    @GetMapping("/listarPedidos")
    public ResponseEntity<List<Pedido>> listarPedidos(@RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fim) {
        return ResponseEntity.ok(pedidoService.listarPedidos(inicio, fim));
    }

    @PostMapping("/atualizarPedido")
    public ResponseEntity<Pedido> preparoPronto(@RequestParam String idPedido) {
        return ResponseEntity.ok(pedidoService.statusPedido(idPedido));
    }


}
