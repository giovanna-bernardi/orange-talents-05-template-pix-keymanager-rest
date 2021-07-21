package br.com.zupacademy.giovanna.pix.list

import br.com.zupacademy.giovanna.ListaChavePixRequest
import br.com.zupacademy.giovanna.ListaChavePixResponse
import br.com.zupacademy.giovanna.PixKeyListManagerServiceGrpc.PixKeyListManagerServiceBlockingStub
import br.com.zupacademy.giovanna.validation.ValidUUID
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.validation.Validated
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Validated
@Controller("/api/v1/clientes/{clienteId}")
class ListaChavePixController(private val grpcClient: PixKeyListManagerServiceBlockingStub) {

    @Get("/pix")
    fun lista (@ValidUUID clienteId: String) : HttpResponse<Any> {
        val response = grpcClient.lista(ListaChavePixRequest.newBuilder()
            .setClienteId(clienteId)
            .build())
        return HttpResponse.ok(response.chavesList.map { ChaveResponse(it) })
    }
}

class ChaveResponse(chave: ListaChavePixResponse.ChavePix) {
    val id = chave.pixId
    val valorChave = chave.valorChave
    val tipoChave = chave.tipoChave
    val tipoConta = chave.tipoConta
    val criadaEm = chave.dataCadastro.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }
}