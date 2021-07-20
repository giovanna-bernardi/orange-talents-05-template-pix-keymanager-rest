package br.com.zupacademy.giovanna.pix.registration

import br.com.zupacademy.giovanna.PixKeyRegistrationManagerServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import java.util.*
import javax.validation.Valid

@Validated
@Controller("/api/v1/clientes/{clienteId}")
class NovaChavePixController(private val grpcClient: PixKeyRegistrationManagerServiceGrpc.PixKeyRegistrationManagerServiceBlockingStub) {

    @Post("/pix")
    fun cadastra(
        @Body @Valid request: NovaChavePixRequest,
        clienteId: UUID
    ): HttpResponse<Any> {
        val grpcResponse = grpcClient.cadastra(request.toGrpcNovaChavePixRequest(clienteId))
        val location = HttpResponse.uri("/api/v1/clientes/$clienteId/pix/${grpcResponse.pixId}")
        return HttpResponse.created(location)
    }
}