import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;

import com.example.DigitalWallet;

class EstadosCarteira {
    
    private static final String OWNER_PADRAO = "Teste";
    private static final double SALDO_INICIAL = 100.0;
    private static final double VALOR_OPERACAO = 10.0;
    
    @Test
    void pagamentoDeveLancarExcecaoQuandoCarteiraNaoVerificada() {
        DigitalWallet wallet = new DigitalWallet(OWNER_PADRAO, SALDO_INICIAL);
        assumeFalse(wallet.isVerified(), 
            "Carteira não deve estar verificada inicialmente");
        
        assertThrows(IllegalStateException.class, 
            () -> wallet.pay(VALOR_OPERACAO),
            "Pagamento deve lançar IllegalStateException quando carteira não está verificada");
    }
    
    @Test
    void pagamentoDeveLancarExcecaoQuandoCarteiraBloqueada() {
        DigitalWallet wallet = criarCarteiraVerificada(SALDO_INICIAL);
        wallet.lock();
        assumeTrue(wallet.isLocked(), 
            "Carteira deve estar bloqueada");
        assumeTrue(wallet.isVerified(), 
            "Carteira deve estar verificada");
        
        assertThrows(IllegalStateException.class, 
            () -> wallet.pay(VALOR_OPERACAO),
            "Pagamento deve lançar IllegalStateException quando carteira está bloqueada");
    }
    
    @Test
    void estornoDeveLancarExcecaoQuandoCarteiraNaoVerificada() {
        DigitalWallet wallet = new DigitalWallet(OWNER_PADRAO, SALDO_INICIAL);
        assumeFalse(wallet.isVerified(), 
            "Carteira não deve estar verificada inicialmente");
        
        assertThrows(IllegalStateException.class, 
            () -> wallet.refund(VALOR_OPERACAO),
            "Estorno deve lançar IllegalStateException quando carteira não está verificada");
    }
    
    @Test
    void estornoDeveLancarExcecaoQuandoCarteiraBloqueada() {
        DigitalWallet wallet = criarCarteiraVerificada(SALDO_INICIAL);
        wallet.lock();
        assumeTrue(wallet.isLocked(), 
            "Carteira deve estar bloqueada");
        assumeTrue(wallet.isVerified(), 
            "Carteira deve estar verificada");
        
        assertThrows(IllegalStateException.class, 
            () -> wallet.refund(VALOR_OPERACAO),
            "Estorno deve lançar IllegalStateException quando carteira está bloqueada");
    }
    
    @Test
    void pagamentoDeveFuncionarQuandoCarteiraVerificadaENaoBloqueada() {
        DigitalWallet wallet = criarCarteiraVerificada(SALDO_INICIAL);
        assumeTrue(wallet.isVerified() && !wallet.isLocked(), 
            "Carteira deve estar verificada e não bloqueada");
        
        boolean resultado = wallet.pay(50.0);
        
        assertTrue(resultado, 
            "Pagamento deve ser bem-sucedido quando carteira está ativa");
    }
    
    @Test
    void estornoDeveFuncionarQuandoCarteiraVerificadaENaoBloqueada() {
        DigitalWallet wallet = criarCarteiraVerificada(SALDO_INICIAL);
        assumeTrue(wallet.isVerified() && !wallet.isLocked(), 
            "Carteira deve estar verificada e não bloqueada");
        
        double saldoInicial = wallet.getBalance();
        wallet.refund(25.0);
        
        assertTrue(wallet.getBalance() > saldoInicial, 
            "Estorno deve aumentar o saldo quando carteira está ativa");
    }
    
    private DigitalWallet criarCarteiraVerificada(double saldoInicial) {
        DigitalWallet wallet = new DigitalWallet(OWNER_PADRAO, saldoInicial);
        wallet.verify();
        return wallet;
    }
}

