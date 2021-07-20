package br.com.zupacademy.giovanna.pix

import br.com.zupacademy.giovanna.CadastraChavePixResponse
import br.com.zupacademy.giovanna.PixKeyRegistrationManagerServiceGrpc
import br.com.zupacademy.giovanna.grpc.GrpcClientFactory
import br.com.zupacademy.giovanna.pix.registration.NovaChavePixRequest
import br.com.zupacademy.giovanna.pix.registration.TipoChaveRequest
import br.com.zupacademy.giovanna.pix.registration.TipoContaRequest
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class NovaChavePixControllerTest(private val grpcClient: PixKeyRegistrationManagerServiceGrpc.PixKeyRegistrationManagerServiceBlockingStub) {

    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Factory
    @Replaces(factory = GrpcClientFactory::class) // substitui a fábrica da aplicação por essa mockada
    internal class ClientsStubFactory {

        @Singleton
        fun stubMock() =
            Mockito.mock(PixKeyRegistrationManagerServiceGrpc.PixKeyRegistrationManagerServiceBlockingStub::class.java)
    }

    @Test
    internal fun `deve cadastrar uma nova chave pix`() {
        // cenário
        val pixId = UUID.randomUUID().toString()
        val clienteId = UUID.randomUUID().toString()

        val grpcResponse = CadastraChavePixResponse.newBuilder()
            .setPixId(pixId)
            .build()

        given(grpcClient.cadastra(Mockito.any())).willReturn(grpcResponse)

        val novaChavePix = NovaChavePixRequest(
            tipoChave = TipoChaveRequest.EMAIL,
            valorChave = "teste@teste.com",
            tipoConta = TipoContaRequest.CONTA_CORRENTE
        )

        // ação
        val request = HttpRequest.POST("/api/v1/clientes/${clienteId}/pix", novaChavePix)
        val response = httpClient.toBlocking().exchange(request, NovaChavePixRequest::class.java)

        // validação
        assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.contains(pixId))
    }
    @Test
    internal fun `nao deve cadastrar quando valor da chave for nula`() {
        // cenário
        val clienteId = UUID.randomUUID().toString()

        val grpcResponse = CadastraChavePixResponse.newBuilder()
            .build()

        given(grpcClient.cadastra(Mockito.any())).willReturn(grpcResponse)

        val novaChavePix = NovaChavePixRequest(
            tipoChave = null,
            valorChave = "qualquer",
            tipoConta = TipoContaRequest.CONTA_CORRENTE
        )

        // ação
        val request = HttpRequest.POST("/api/v1/clientes/${clienteId}/pix", novaChavePix)
        val response = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, NovaChavePixRequest::class.java)
        }


        // validação
        with(response) {
            assertEquals(HttpStatus.BAD_REQUEST, status)
       }
    }
}