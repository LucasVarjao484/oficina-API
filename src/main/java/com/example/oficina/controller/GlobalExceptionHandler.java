package com.example.oficina.controller;

import com.example.oficina.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(CadastroDuplicadoException.class)
    public ErroResposta handlerCadastroDuplicadoException(CadastroDuplicadoException e){
        return ErroResposta.conflito(e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(RecursoDesativadoException.class)
    public ErroResposta handlerDonoDesativadoException(RecursoDesativadoException e){
        return ErroResposta.conflito(e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(AcaoNaoPermitidaException.class)
    public ErroResposta handlerAcaoNaoPermitidaException(AcaoNaoPermitidaException e){
        return ErroResposta.conflito(e.getMessage());
    }

    @ExceptionHandler(CampoInvalidoException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroResposta handlerCampoInvalidoException(CampoInvalidoException e){
        return new ErroResposta(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Erro de validação! ",
                List.of(new ErroCampo(e.getCampo(), e.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroResposta handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<ErroCampo> listaErros = fieldErrors
                .stream()
                .map(fe -> new ErroCampo(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ErroResposta(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Erro de validação! ",
                listaErros);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErroResposta handlerErroInesperadoException(RuntimeException e){
        return new ErroResposta(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro inesperado!",
                List.of()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResposta handlerMaRequisicaoException(IllegalArgumentException e){
        return new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição mal formada!",
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResposta handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ErroCampo erroCampo = new ErroCampo(
                e.getParameter().getParameterName(),
                "O parâmetro deve ser do tipo: " + e.getRequiredType().getSimpleName());
        return new ErroResposta(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição mal formada!",
                List.of(erroCampo));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResposta handlerDonoNotFoundException(RecursoNaoEncontradoException e){
        return new ErroResposta(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                List.of()
        );
    }
}
