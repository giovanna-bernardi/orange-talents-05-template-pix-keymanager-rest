package br.com.zupacademy.giovanna.pix.exclusion

import br.com.zupacademy.giovanna.CadastraChavePixResponse
import br.com.zupacademy.giovanna.PixKeyExclusionManagerServiceGrpc.PixKeyExclusionManagerServiceBlockingStub
import br.com.zupacademy.giovanna.RemoveChavePixRequest
import br.com.zupacademy.giovanna.grpc.GrpcClientFactory
import br.com.zupacademy.giovanna.pix.registration.NovaChavePixRequest
import br.com.zupacademy.giovanna.pix.registration.TipoContaRequest
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RemoveChavePixControllerTest(private val grpcClient: PixKeyExclusionManagerServiceBlockingStub) {

    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Factory
    @Replaces(factory = GrpcClientFactory::class) // substitui a fábrica da aplicação por essa mockada
    internal class ClientsStubFactory {

        @Singleton
        fun stubMock() =
            Mockito.mock(PixKeyExclusionManagerServiceBlockingStub::class.java)
    }

    @Test
    internal fun `deve remover chave existente do cliente`() {

        // Cenário
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        // Ação
        val grpcResponse = grpcClient.remove(RemoveChavePixRequest.newBuilder()
            .setPixId(pixId)
            .setClienteId(clienteId)
            .build())

        given(grpcClient.remove(any())).willReturn(grpcResponse)

        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$clienteId/pix/$pixId")
        val response = httpClient.toBlocking().exchange(request, RemoveChavePixRequest::class.java)

        // validação
        assertEquals(HttpStatus.OK, response.status)
    }
}