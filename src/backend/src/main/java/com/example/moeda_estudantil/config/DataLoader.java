package com.example.moeda_estudantil.config;

import com.example.moeda_estudantil.model.Curso;
import com.example.moeda_estudantil.model.Departamento;
import com.example.moeda_estudantil.model.Instituicao;
import com.example.moeda_estudantil.repository.CursoRepository;
import com.example.moeda_estudantil.repository.DepartamentoRepository;
import com.example.moeda_estudantil.repository.InstituicaoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner initData(InstituicaoRepository instituicaoRepository,
                                      CursoRepository cursoRepository,
                                      DepartamentoRepository departamentoRepository) {
        return args -> {
            if (instituicaoRepository.count() == 0) {
                Instituicao pucMinas = instituicaoRepository.save(
                        Instituicao.builder().nome("PUC Minas").build()
                );

                departamentoRepository.save(
                        Departamento.builder().nome("Instituto de Ciências Exatas e Informática").instituicao(pucMinas).build()
                );

                cursoRepository.save(
                        Curso.builder().nome("Engenharia de Software").instituicao(pucMinas).build()
                );
                cursoRepository.save(
                        Curso.builder().nome("Ciência da Computação").instituicao(pucMinas).build()
                );
            }
        };
    }
}

