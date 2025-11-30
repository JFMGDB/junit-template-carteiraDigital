import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.example.DigitalWallet;

class SaldoInicial {
    
    private static final double DELTA = 0.001;
    
    @Test
    void deveConfigurarSaldoInicialCorreto() {
        DigitalWallet wallet = new DigitalWallet("João Silva", 100.0);
        assertEquals(100.0, wallet.getBalance(), DELTA);
        assertEquals("João Silva", wallet.getOwner());
    }
    
    @Test
    void deveLancarExcecaoParaSaldoInicialNegativo() {
        assertThrows(IllegalArgumentException.class, 
            () -> new DigitalWallet("Maria Santos", -50.0),
            "Saldo inicial negativo deve lançar IllegalArgumentException");
    }
}