package br.com.zupacademy.giovanna.handler

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.hateoas.JsonError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GlobalExceptionHandlerTest {

    val request = HttpRequest.GET<Any>("/")

    // um teste para cada uma das possibilidades do when

    @Test
    fun `deve retornar 400 quando argumentos forem invalidos`() {
        // Cenário
        val description = "Existem dados inválidos na requisição"
        val invalidArgumentException = StatusRuntimeException(Status.INVALID_ARGUMENT
            .withDescription(description))

        // Ação
        val response = GlobalExceptionHandler().handle(request, invalidArgumentException)

        //Validação
        with(response) {
            assertEquals(HttpStatus.BAD_REQUEST.code, status.code)
            assertNotNull(body())
            assertEquals(description, (body() as JsonError).message)
        }

    }

    @Test
    fun `deve retornar 404 quando StatusRuntimeException for NOT_FOUND`() {
        // Cenário
        val description = "não encontrado"
        val notFoundException = StatusRuntimeException(Status.NOT_FOUND
            .withDescription(description))

        // Ação
        val response = GlobalExceptionHandler().handle(request, notFoundException)

        //Validação
        with(response) {
            assertEquals(HttpStatus.NOT_FOUND.code, status.code)
            assertNotNull(body())
            assertEquals(description, (body() as JsonError).message)
        }
    }

    @Test
    fun `deve retornar 422 quando chave já existe`() {
        // Cenário
        val description = "Chave já existe"
        val alreadyExistsException = StatusRuntimeException(Status.ALREADY_EXISTS
            .withDescription(description))

        // Ação
        val response = GlobalExceptionHandler().handle(request, alreadyExistsException)

        //Validação
        with(response) {
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.code, status.code)
            assertNotNull(body())
            assertEquals(description, (body() as JsonError).message)
        }
    }

    @Test
    fun `deve retornar 500 quando qualquer outro erro for lancado`() {
        // Cenário
        val internalException = StatusRuntimeException(Status.INTERNAL)

        // Ação
        val response = GlobalExceptionHandler().handle(request, internalException)

        //Validação
        with(response) {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, status.code)
            assertNotNull(body())
            assertTrue((body() as JsonError).message.contains("INTERNAL"))
        }
    }
}