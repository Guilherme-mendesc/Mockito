package br.ce.wcaquino.servicos;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.JUnit4;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;
import br.ce.wcaquino.runners.ParallelRunner;
import junit.framework.Assert;

//@RunWith(ParallelRunner.class)
@RunWith(BlockJUnit4ClassRunner.class)
public class CalculadoraTest {
	
	private Calculadora calc;
	
	@Before
	public void setup() {
		calc = new Calculadora();
		System.out.println("inicializando...");
	}
	
	@After
	public void tearDown() {
		System.out.println("finalizando...");
	}

	@Test
	public void deveSomarDoisValores() {
		//cenario
		
		int a = 5;
		int b = 3;
		
		Calculadora calc = new Calculadora();
		
		
		//acao
		
		int resultado = calc.somar(a, b);
		
		//verificacao
		
		Assert.assertEquals(8, resultado);
	}
	
	@Test
	public void deveSubtrairDoisValores() {
		//cenario
		int a = 8;
		int b = 5;
		Calculadora calc = new Calculadora();
		
		//acao
		
		int resultado = calc.subtrair(a, b);
		
		
		//verificação
		
		Assert.assertEquals(3, resultado);
	}
	
	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
		//cenario
		
		int a = 10;
		int b = 2;
		Calculadora calc = new Calculadora();
		
		//acao
		
		int resultado = calc.dividir(a, b);
		
		// verificação
		
		Assert.assertEquals(5, resultado);
		
		
	}
	
	@Test(expected = NaoPodeDividirPorZeroException.class)
	public void deveLancarExcecaoAoDivirPorZero() throws NaoPodeDividirPorZeroException {
		//cenario
		int a = 10;
		int b = 0;
		Calculadora calc = new Calculadora();
		//acao
		calc.dividir(a, b);
		
	}
	
	@Test
	public void deveDividir() {
		String a = "6";
		String b = "3";
		
		int resultado = calc.divide(a, b);
		
		Assert.assertEquals(2, resultado);
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
