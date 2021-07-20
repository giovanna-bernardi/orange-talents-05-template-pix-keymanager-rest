package br.com.zupacademy.giovanna.pix.registration

import br.com.zupacademy.giovanna.CadastraChavePixRequest
import br.com.zupacademy.giovanna.TipoChave
import br.com.zupacademy.giovanna.TipoConta
import br.com.zupacademy.giovanna.validation.ValidPixKey
import io.micronaut.core.annotation.Introspected
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import br.com.caelum.stella.validation.CPFValidator
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

// Necessário ter na request
/*
  TipoChave tipoChave = 2;
  string valorChave = 3;
  TipoConta tipoConta = 4;
* */

@ValidPixKey
@Introspected
data class NovaChavePixRequest(
    @field:NotNull val tipoChave: TipoChaveRequest?,
    @field:Size(max = 77) val valorChave: String?,
    @field:NotNull val tipoConta: TipoContaRequest?
) {

    fun toGrpcNovaChavePixRequest(clienteId: UUID): CadastraChavePixRequest {
        return CadastraChavePixRequest.newBuilder()
            .setClienteId(clienteId.toString())
            .setTipoChave(tipoChave?.tipoGrpc ?: TipoChave.UNKNOWN_CHAVE)
            .setValorChave(valorChave ?: "")
            .setTipoConta(tipoConta?.tipoGrpc ?: TipoConta.UNKNOWN_CONTA)
            .build()
        // dificilmente vai acontecer de passar um tipo UNKNOWN, já que validamos a chave com ValidPixKey
    }
}

enum class TipoChaveRequest(val tipoGrpc: TipoChave) {
    CPF(TipoChave.CPF){
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank()) {
                return false
            }

            // deve ser obrigatório esse formato (ex: 12345678901)
            if (!chave.matches("^[0-9]{11}\$".toRegex())) {
                return false
            }

            return CPFValidator(false).invalidMessagesFor(chave).isEmpty()

        }
    },

    CELULAR(TipoChave.CELULAR){
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank()) {
                return false
            }

            // deve ser obrigatório esse formato (ex: +5585988714077)
            return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },

    EMAIL(TipoChave.EMAIL){
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank()) {
                return false
            }

            return EmailValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }
    },

    ALEATORIA(TipoChave.ALEATORIA){
        override fun valida(chave: String?): Boolean = chave.isNullOrBlank() // deve estar vazia, o sistema deve gerar um UUID
    };

    abstract fun valida(chave: String?): Boolean

}


enum class TipoContaRequest(val tipoGrpc: TipoConta) {
    CONTA_CORRENTE(TipoConta.CONTA_CORRENTE),
    CONTA_POUPANCA(TipoConta.CONTA_POUPANCA)
}
