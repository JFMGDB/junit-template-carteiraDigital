import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.DigitalWallet;

class Estorno {
    
    private static final String OWNER_PADRAO = "Teste";
    private static final double DELTA = 0.001;
    
    static Stream<Arguments> valoresEstorno() {
        return Stream.of(
            Arguments.of(100.0, 10.0, 110.0),
            Arguments.of(0.0,   5.0,   5.0),
            Arguments.of(50.0,  0.01, 50.01),
            Arguments.of(200.0, 50.0, 250.0),
            Arguments.of(10.5,  2.3,  12.8)
        );
    }
    
    @ParameterizedTest
    @MethodSource("valoresEstorno")
    void estornoComCarteiraValida(double inicial, double valor, double saldoEsperado) {
        assumeTrue(valor > 0, "Valor do estorno deve ser positivo");
        
        DigitalWallet wallet = criarCarteiraVerificada(inicial);
        assumeTrue(wallet.isVerified() && !wallet.isLocked(), 
            "Carteira deve estar verificada e não bloqueada");
        
        wallet.refund(valor);
        
        assertEquals(saldoEsperado, wallet.getBalance(), DELTA,
            "Saldo deve ser atualizado corretamente após estorno");
    }
    
    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, -0.01, -100.0})
    void deveLancarExcecaoParaRefundInvalido(double valor) {
        DigitalWallet wallet = criarCarteiraVerificada(100.0);
        assumeTrue(wallet.isVerified() && !wallet.isLocked());
        
        double saldoOriginal = wallet.getBalance();
        
        assertThrows(IllegalArgumentException.class, 
            () -> wallet.refund(valor),
            "Estorno com valor <= 0 deve lançar IllegalArgumentException");
        
        verificarSaldoNaoAlterado(wallet, saldoOriginal);
    }
    
    @Test
    void deveLancarSeNaoVerificadaOuBloqueada() {
        DigitalWallet walletNaoVerificada = new DigitalWallet(OWNER_PADRAO, 100.0);
        assumeFalse(walletNaoVerificada.isVerified(), 
            "Carteira não deve estar verificada");
        
        assertThrows(IllegalStateException.class, 
            () -> walletNaoVerificada.refund(10.0),
            "Estorno com carteira não verificada deve lançar IllegalStateException");
        
        DigitalWallet walletBloqueada = criarCarteiraVerificada(100.0);
        walletBloqueada.lock();
        assumeTrue(walletBloqueada.isLocked(), 
            "Carteira deve estar bloqueada");
        
        assertThrows(IllegalStateException.class, 
            () -> walletBloqueada.refund(10.0),
            "Estorno com carteira bloqueada deve lançar IllegalStateException");
    }
    
    private DigitalWallet criarCarteiraVerificada(double saldoInicial) {
        DigitalWallet wallet = new DigitalWallet(OWNER_PADRAO, saldoInicial);
        wallet.verify();
        return wallet;
    }
    
    private void verificarSaldoNaoAlterado(DigitalWallet wallet, double saldoEsperado) {
        assertEquals(saldoEsperado, wallet.getBalance(), DELTA,
            "Saldo não deve ser alterado após operação inválida");
    }
}
