import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.DigitalWallet;

class Pagamento {
    
    private static final String OWNER_PADRAO = "Teste";
    private static final double DELTA = 0.001;
    
    @ParameterizedTest
    @CsvSource({
        "100.0, 30.0, true",
        "50.0, 80.0, false",
        "10.0, 10.0, true",
        "200.0, 50.0, true",
        "15.0, 20.0, false",
        "0.0, 5.0, false"
    })
    void pagamentoComCarteiraVerificadaENaoBloqueada(double inicial, double valor, boolean esperado) {
        assumeTrue(valor > 0, "Valor do pagamento deve ser positivo");
        
        DigitalWallet wallet = criarCarteiraVerificada(inicial);
        assumeTrue(wallet.isVerified() && !wallet.isLocked(), 
            "Carteira deve estar verificada e não bloqueada");
        
        double saldoInicial = wallet.getBalance();
        boolean resultado = wallet.pay(valor);
        
        assertEquals(esperado, resultado, 
            "Resultado do pagamento deve corresponder ao esperado");
        
        if (esperado) {
            assertEquals(saldoInicial - valor, wallet.getBalance(), DELTA,
                "Saldo deve ser debitado quando pagamento é bem-sucedido");
        } else {
            assertEquals(saldoInicial, wallet.getBalance(), DELTA,
                "Saldo não deve ser alterado quando pagamento falha por saldo insuficiente");
        }
    }
    
    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, -0.01, -100.0})
    void deveLancarExcecaoParaPagamentoInvalido(double valor) {
        DigitalWallet wallet = criarCarteiraVerificada(100.0);
        assumeTrue(wallet.isVerified() && !wallet.isLocked());
        
        double saldoOriginal = wallet.getBalance();
        
        assertThrows(IllegalArgumentException.class, 
            () -> wallet.pay(valor),
            "Pagamento com valor <= 0 deve lançar IllegalArgumentException");
        
        verificarSaldoNaoAlterado(wallet, saldoOriginal);
    }
    
    @Test
    void deveLancarSeNaoVerificadaOuBloqueada() {
        DigitalWallet walletNaoVerificada = new DigitalWallet(OWNER_PADRAO, 100.0);
        assumeFalse(walletNaoVerificada.isVerified(), 
            "Carteira não deve estar verificada");
        
        assertThrows(IllegalStateException.class, 
            () -> walletNaoVerificada.pay(10.0),
            "Pagamento com carteira não verificada deve lançar IllegalStateException");
        
        DigitalWallet walletBloqueada = criarCarteiraVerificada(100.0);
        walletBloqueada.lock();
        assumeTrue(walletBloqueada.isLocked(), 
            "Carteira deve estar bloqueada");
        
        assertThrows(IllegalStateException.class, 
            () -> walletBloqueada.pay(10.0),
            "Pagamento com carteira bloqueada deve lançar IllegalStateException");
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
