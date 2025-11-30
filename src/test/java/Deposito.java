import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.DigitalWallet;

class Deposito {
    
    private static final String OWNER_PADRAO = "Teste";
    private static final double DELTA = 0.001;
    
    @ParameterizedTest
    @ValueSource(doubles = {10.0, 0.01, 999.99, 1.0, 500.50})
    void deveDepositarValoresValidos(double amount) {
        DigitalWallet wallet = new DigitalWallet(OWNER_PADRAO, 0.0);
        double saldoInicial = wallet.getBalance();
        
        wallet.deposit(amount);
        
        assertEquals(saldoInicial + amount, wallet.getBalance(), DELTA);
    }
    
    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, -0.01, -100.0})
    void deveLancarExcecaoParaDepositoInvalido(double amount) {
        DigitalWallet wallet = new DigitalWallet(OWNER_PADRAO, 100.0);
        double saldoOriginal = wallet.getBalance();
        
        assertThrows(IllegalArgumentException.class, 
            () -> wallet.deposit(amount),
            "Depósito com valor <= 0 deve lançar IllegalArgumentException");
        
        assertEquals(saldoOriginal, wallet.getBalance(), DELTA, 
            "Saldo não deve ser alterado após tentativa de depósito inválido");
    }
}