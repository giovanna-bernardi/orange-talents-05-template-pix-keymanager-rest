package br.com.zupacademy.giovanna.pix.detail

import br.com.zupacademy.giovanna.*
import br.com.zupacademy.giovanna.PixKeyDetailManagerServiceGrpc.*
import br.com.zupacademy.giovanna.grpc.GrpcClientFactory
import com.google.protobuf.Timestamp
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito
import org.mockito.BDDMockito.*
import org.mockito.Mockito
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@MicronautTest
internal class DetalheChavePixControllerTest(
    private val grpcClient: PixKeyDetailManagerServiceBlockingStub
) {

    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Test
    fun `deve retornar detalhes da chave existente do cliente`() {
        // Cenário
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        // Ação
        val grpcResponse = DetalheChavePixResponse.newBuilder()
            .setPixId(pixId)
            .setClienteId(clienteId)
            .setChave(
                DetalheChavePixResponse.ChaveInfo.newBuilder()
                    .setTipoChave(TipoChave.ALEATORIA)
                    .setValorChave("86fa138-b66b-4804-ad7c-8fd9ce05bc19")
                    .setConta(
                        DetalheChavePixResponse.ChaveInfo.ContaInfo.newBuilder()
                            .setTipoConta(TipoConta.CONTA_CORRENTE)
                            .setInstituicao("UNIBANCO ITAU SA")
                            .setNomeTitular("Yuri Matheu")
                            .setCpfTitular("86135457004")
                            .setAgencia("0001")
                            .setNumeroConta("123455")
                            .build()
                    )
                    .setDataCadastro(LocalDateTime.now().let {
                        val criadaEm = it.atZone(ZoneId.of("UTC")).toInstant()
                        Timestamp.newBuilder()
                            .setSeconds(criadaEm.epochSecond)
                            .setNanos(criadaEm.nano)
                            .build()
                    })
                    .build()
            )
            .build()

        given(grpcClient.consulta(any())).willReturn(grpcResponse)

        val request = HttpRequest.GET<Any>("/api/v1/clientes/$clienteId/pix/$pixId")
        val response = httpClient.toBlocking().exchange(request, Any::class.java)

        // validação
        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class ClientsStubFactory {

        @Singleton
        fun consultaChave() =
            Mockito.mock(PixKeyDetailManagerServiceBlockingStub::class.java)
    }
}