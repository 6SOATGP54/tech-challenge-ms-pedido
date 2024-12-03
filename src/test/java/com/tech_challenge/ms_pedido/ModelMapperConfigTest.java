package com.tech_challenge.ms_pedido;

import com.tech_challenge.ms_pedido.conf.ModelMapperConfig;
import org.modelmapper.ModelMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@Import(ModelMapperConfig.class) // Especifica que a configuração do ModelMapper será carregada
class ModelMapperConfigTest {

    @Autowired
    private ModelMapper modelMapper;

    @Test
    void deveRetornarBeanModelMapper() {
        // Verifica se o bean ModelMapper foi injetado corretamente
        assertThat(modelMapper).isNotNull();
    }
}
