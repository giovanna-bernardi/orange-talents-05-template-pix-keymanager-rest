package br.com.zupacademy.giovanna.pix.detail

import br.com.zupacademy.giovanna.DetalheChavePixRequest
import br.com.zupacademy.giovanna.DetalheChavePixResponse
import br.com.zupacademy.giovanna.PixKeyDetailManagerServiceGrpc.PixKeyDetailManagerServiceBlockingStub
import br.com.zupacademy.giovanna.TipoConta
import br.com.zupacademy.giovanna.validation.ValidUUID
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.validation.Validated
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Validated
@Controller("api/v1/clientes/{clienteId}")
class DetalheChavePixController(private val grpcClient: PixKeyDetailManagerServiceBlockingStub) {

    @Get("/pix/{pixId}")
    fun consulta(@ValidUUID clienteId: String, @ValidUUID pixId: String): HttpResponse<Any> {

       val response = grpcClient.consulta(DetalheChavePixRequest.newBuilder()
            .setSistemaInterno(DetalheChavePixRequest.KeyManager.newBuilder()
                .setPixId(pixId)
                .setClienteId(clienteId)
                .build())
            .build())

        return HttpResponse.ok(DetalheChaveResponse(response))
    }
}

// detalheChave não tem val então não vira propriedade
class DetalheChaveResponse(detalheChave: DetalheChavePixResponse) {
    val pixId = detalheChave.pixId
    val clienteId = detalheChave.clienteId
    val tipoChave = detalheChave.chave.tipoChave
    val valorChave = detalheChave.chave.valorChave

    val tipoConta = when (detalheChave.chave.conta.tipoConta) {
        TipoConta.CONTA_CORRENTE -> "CONTA_CORRENTE"
        TipoConta.CONTA_POUPANCA -> "CONTA_POUPANCA"
        else -> "NAO_RECONHECIDA" // só está aqui por causo do when. Não deveria chegar até aqui.
    }

    // para não precisar criar um outro objeto do tipo Conta
    val conta = mapOf(Pair("tipoConta", tipoConta),
        Pair("instituicao", detalheChave.chave.conta.instituicao),
        Pair("nomeTitular", detalheChave.chave.conta.nomeTitular),
        Pair("cpfTitular", detalheChave.chave.conta.cpfTitular),
        Pair("agencia", detalheChave.chave.conta.agencia),
        Pair("numeroConta", detalheChave.chave.conta.numeroConta))

    val criadaEm = detalheChave.chave.dataCadastro.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }
}