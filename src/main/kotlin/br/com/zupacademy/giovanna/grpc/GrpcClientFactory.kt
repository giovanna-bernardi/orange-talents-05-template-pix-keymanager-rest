package br.com.zupacademy.giovanna.grpc

import br.com.zupacademy.giovanna.PixKeyDetailManagerServiceGrpc
import br.com.zupacademy.giovanna.PixKeyExclusionManagerServiceGrpc
import br.com.zupacademy.giovanna.PixKeyListManagerServiceGrpc
import br.com.zupacademy.giovanna.PixKeyRegistrationManagerServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory(@GrpcChannel("keyManager") val channel: ManagedChannel)  {

    @Singleton
    fun cadastraChave() = PixKeyRegistrationManagerServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun removeChave() = PixKeyExclusionManagerServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun consultaChave() = PixKeyDetailManagerServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun listaChave() = PixKeyListManagerServiceGrpc.newBlockingStub(channel)

}