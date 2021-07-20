package br.com.zupacademy.giovanna.pix.exclusion

import br.com.zupacademy.giovanna.PixKeyExclusionManagerServiceGrpc
import br.com.zupacademy.giovanna.RemoveChavePixRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import org.slf4j.LoggerFactory
import java.util.*

@Controller("/api/v1/clientes/{clienteId}")
class RemoveChavePixController(private val grpcClient: PixKeyExclusionManagerServiceGrpc.PixKeyExclusionManagerServiceBlockingStub) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Delete("/pix/{pixId}")
    fun remove (clienteId: UUID, pixId: UUID) : HttpResponse<Any> {

        LOGGER.info("[$clienteId] removendo uma chave pix com $pixId")

        grpcClient.remove(RemoveChavePixRequest.newBuilder()
            .setPixId(pixId.toString())
            .setClienteId(clienteId.toString())
            .build())
        return HttpResponse.ok()
    }
}