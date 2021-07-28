package br.com.zupacademy.giovanna.pix.list

import br.com.zupacademy.giovanna.ListaChavePixResponse
import br.com.zupacademy.giovanna.PixKeyListManagerServiceGrpc.PixKeyListManagerServiceBlockingStub
import br.com.zupacademy.giovanna.TipoChave
import br.com.zupacademy.giovanna.grpc.GrpcClientFactory
import br.com.zupacademy.giovanna.pix.util.PixKeyGenerator
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class ListaChavePixControllerTest(private val grpcClient: PixKeyListManagerServiceBlockingStub) {

    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Test
    fun `deve listar as chaves do cliente quando existir`() {
        // Cenário
        val clienteId = UUID.randomUUID().toString()

        // Ação
        val grpcResponse = listaChavePixResponse()

        given(grpcClient.lista(any())).willReturn(grpcResponse)

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$clienteId/pix")
        val response = httpClient.toBlocking().exchange(request, List::class.java)

        // validação
        with(response) {
            Assertions.assertEquals(HttpStatus.OK, status)
            Assertions.assertNotNull(body())
            Assertions.assertEquals(body()!!.size, 2)
        }
    }

    @Test
    fun `deve retorna lista vazia quando cliente nao tiver chaves registradas`() {
// Cenário
        val clienteId = UUID.randomUUID().toString()

        // Ação
        val grpcResponse = ListaChavePixResponse.newBuilder()
            .setClienteId(PixKeyGenerator.CLIENTE_ID.toString())
            .build()

        given(grpcClient.lista(any())).willReturn(grpcResponse)

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$clienteId/pix")
        val response = httpClient.toBlocking().exchange(request, List::class.java)

        // validação
        with(response) {
            Assertions.assertEquals(HttpStatus.OK, status)
            Assertions.assertNotNull(body())
            Assertions.assertEquals(body()!!.size, 0)
        }
    }

    private fun listaChavePixResponse(): ListaChavePixResponse {
        val chaveEmail = PixKeyGenerator().generateKey()

        val chaveCelular = PixKeyGenerator(tipoChave = TipoChave.CELULAR, valorChave = "+5535999999999").generateKey()

        return ListaChavePixResponse.newBuilder()
            .setClienteId(PixKeyGenerator.CLIENTE_ID.toString())
            .addAllChaves(listOf(chaveEmail, chaveCelular))
            .build()

    }


    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class ClientsStubFactory {

        @Singleton
        fun listaChave() =
            Mockito.mock(PixKeyListManagerServiceBlockingStub::class.java)
    }

}