package br.com.zupacademy.giovanna.pix

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoChaveRequestTest {

    @Nested
    inner class CPFTest {
        // deve ser obrigatório esse formato (ex: 12345678901)
        @Test
        fun `deve ser valido se tipo CPF e chave tiver CPF valido`() {
            assertTrue(TipoChaveRequest.CPF.valida("86135457004"))

        }

        @Test
        fun `nao deve ser valido se tipo CPF e chave for nula ou vazia`() {
            with(TipoChaveRequest.CPF) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }

        @Test
        fun `nao deve ser valido se tipo CPF e chave tiver algo diferente de numeros`() {
            with(TipoChaveRequest.CPF) {
                assertFalse(valida("861.354.570-04"))
                assertFalse(valida("861A354b570-04"))
            }
        }

        @Test
        fun `nao deve ser valido se tipo CPF e chave tiver mais que onze numeros`() {
            with(TipoChaveRequest.CPF) {
                assertFalse(valida("861354570049"))
                assertFalse(valida("11111111111"))
            }
        }

        @Test
        fun `nao deve ser valido se tipo CPF e chave for um CPF invalido`() {
            assertFalse(TipoChaveRequest.CPF.valida("11111111111"))
        }
    }


    @Nested
    inner class CelularTest {
        // deve ser obrigatório esse formato (ex: +5585988714077)
        // e usar o regex passado: ^\+[1-9][0-9]\d{1,14}$ (mas ele aceita só +5535, por exemplo)

        @Test
        fun `deve ser valido se tipo CELULAR e chave tiver numero valido`() {
            assertTrue(TipoChaveRequest.CELULAR.valida("+5535999999999"))
        }

        @Test
        fun `nao deve ser valido se tipo CELULAR e chave for nula ou vazia`() {
            with(TipoChaveRequest.CELULAR) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }

        @Test
        fun `nao deve ser valido se tipo CELULAR e chave tiver valor invalido`() {
            with(TipoChaveRequest.CELULAR) {
                assertFalse(valida("35999999999"))
                assertFalse(valida("5535999999999"))
                assertFalse(valida("+55a3599999999"))
//                assertFalse(valida("+5535"))
            }
        }
    }

    @Nested
    inner class EmailTest {
        @Test
        fun `deve ser valido se tipo EMAIL e chave for valida`() {
            assertTrue(TipoChaveRequest.EMAIL.valida("teste@teste.com"))
        }

        @Test
        fun `nao deve ser valido se tipo EMAIL e chave for nula ou vazia`() {
            with(TipoChaveRequest.EMAIL) {
                assertFalse(valida(null))
                assertFalse(valida(""))
            }
        }

        @Test
        fun `nao deve ser valido se tipo EMAIL e chave for endereco invalido`() {
            with(TipoChaveRequest.EMAIL) {
                assertFalse(valida("teste"))
                assertFalse(valida("teste.teste.com.br"))
                assertFalse(valida("teste.teste@com."))
                assertFalse(valida("teste.@teste.com"))
            }
        }
    }


    @Nested
    inner class AleatoriaTest {
        @Test
        fun `deve ser valido se tipo ALEATORIA e chave for nula ou vazia`() {
            with(TipoChaveRequest.ALEATORIA) {
                assertTrue(valida(null))
                assertTrue(valida(""))
            }
        }

        @Test
        fun `nao deve ser valido se tipo ALEATORIA e chave preenchida`() {
            assertFalse(TipoChaveRequest.ALEATORIA.valida("teste"))
        }
    }
}