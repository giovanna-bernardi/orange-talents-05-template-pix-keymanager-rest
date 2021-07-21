package br.com.zupacademy.giovanna.pix.util

import br.com.zupacademy.giovanna.ListaChavePixResponse
import br.com.zupacademy.giovanna.TipoChave
import br.com.zupacademy.giovanna.TipoConta
import com.google.protobuf.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

data class PixKeyGenerator(
    var clienteId: UUID = CLIENTE_ID,
    var tipoChave: TipoChave = TipoChave.EMAIL,
    var valorChave: String = "teste@teste.com.br",
    var tipoConta: TipoConta = TipoConta.CONTA_CORRENTE
) {
    companion object {
        val CLIENTE_ID = UUID.randomUUID()
    }

    fun generateKey(): ListaChavePixResponse.ChavePix {
        return ListaChavePixResponse.ChavePix.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setTipoChave(tipoChave)
            .setValorChave(valorChave)
            .setTipoConta(tipoConta)
            .setDataCadastro(LocalDateTime.now().let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()
    }
}