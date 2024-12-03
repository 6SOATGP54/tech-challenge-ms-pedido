package com.tech_challenge.ms_pedido;

import com.tech_challenge.ms_pedido.conf.PagamentoAMQPConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PagamentoAMQPConfigurationTest {

    PagamentoAMQPConfiguration configuration = new PagamentoAMQPConfiguration();

    @Test
    void deveCriarRabbitAdmin() {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        RabbitAdmin rabbitAdmin = configuration.criaRabbitAdmin(connectionFactory);

        assertNotNull(rabbitAdmin);
        assertTrue(rabbitAdmin instanceof AmqpAdmin);
    }

    @Test
    void deveInicializarAdminAposAplicacaoEstarPronta() {
        RabbitAdmin rabbitAdmin = mock(RabbitAdmin.class);
        ApplicationListener<ApplicationReadyEvent> listener = configuration.inicializaAdmin(rabbitAdmin);

        assertNotNull(listener);
        ApplicationReadyEvent event = mock(ApplicationReadyEvent.class);
        listener.onApplicationEvent(event);

        verify(rabbitAdmin, times(1)).initialize();
    }

    @Test
    void deveCriarJackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter messageConverter = configuration.messageConverter();

        assertNotNull(messageConverter);
        assertTrue(messageConverter instanceof Jackson2JsonMessageConverter);
    }

    @Test
    void deveConfigurarRabbitTemplateComMessageConverter() {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Jackson2JsonMessageConverter messageConverter = mock(Jackson2JsonMessageConverter.class);

        RabbitTemplate rabbitTemplate = configuration.rabbitTemplate(connectionFactory, messageConverter);

        assertNotNull(rabbitTemplate);
        assertEquals(messageConverter, rabbitTemplate.getMessageConverter());
    }
}
