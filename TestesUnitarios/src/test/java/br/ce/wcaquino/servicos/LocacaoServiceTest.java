 package br.ce.wcaquino.servicos;


import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeDataDiferencaDias;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.runners.ParallelRunner;
import br.ce.wcaquino.utils.DataUtils;

//@RunWith(ParallelRunner.class)
@RunWith(BlockJUnit4ClassRunner.class)
public class LocacaoServiceTest {
	
	@InjectMocks @Spy
	private LocacaoService service;
	
	@Mock
	private SPCService spc;
	@Mock
	private LocacaoDAO dao;
	@Mock
	private EmailService email;
		
	@Rule
	public ErrorCollector error = new  ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
		public void setup() {
		MockitoAnnotations.initMocks(this);
		System.out.println("Inicializando 2 ");
	}
	
	@After
	public void tearDown() {
		System.out.println("Finalizando");
	}
	
	

	@Test	
	public void deveAlugarFilme () throws Exception{
		
		//cenario
		
		Usuario usuario = umUsuario().agora();
		List<Filme>  filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
		Mockito.doReturn(DataUtils.obterData(10, 11, 2021)).when(service).obterData();
		
		//acao
		
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		// verificacao
		
		error.checkThat(locacao.getValor(), is(equalTo (5.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(10, 11, 2021)), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(11, 11, 2021)), is(false));
			
	}
	
	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception{
		//cenario
		
		Usuario usuario = umUsuario().agora();
		List<Filme>  filmes = Arrays.asList(FilmeBuilder.umFilmeSemEstoque().agora());
		
		//acao
		
		service.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		//cenario
		
		List<Filme>  filmes = Arrays.asList(FilmeBuilder.umFilme().agora());
		
		//acao
		
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario Vazio"));
		}
	}
	
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		
		Usuario usuario = umUsuario().agora();
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
	//acao
		service.alugarFilme(usuario, null);	
	}
	
	@Test
	public void devePagar75pctnoFilme3() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0),new Filme("Filme 2", 2, 4.0),new Filme("Filme 3", 2, 4.0));
		
		//
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(11.0)); 
	
		
	}
	@Test
	public void devePagar50pctnoFilme4() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora(),umFilme().agora(),umFilme().agora(),umFilme().agora());
		
		//
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(13.0)); 
	
		
	}
	
	@Test
	public void devePagar25pctnoFilme5() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora(),FilmeBuilder.umFilme().agora(),FilmeBuilder.umFilme().agora(),FilmeBuilder.umFilme().agora(),FilmeBuilder.umFilme().agora());
		
		//
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(14.0)); 
	}
	
	@Test
	public void devePagar0pctnoFilme6() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora(),FilmeBuilder.umFilme().agora(),FilmeBuilder.umFilme().agora(),FilmeBuilder.umFilme().agora(),FilmeBuilder.umFilme().agora(),FilmeBuilder.umFilme().agora());
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(14.0)); 
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		
		
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Mockito.doReturn(DataUtils.obterData(15, 11, 2021)).when(service).obterData();
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		 //verificacao
		
		assertThat(retorno.getDataRetorno(),  caiNumaSegunda());

	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSpc() throws Exception  {
		//cenario 
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(usuario)).thenReturn(true);
		
			//acao
		try {
			service.alugarFilme(usuario, filmes);
			
			//verificacao
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuario Negativado"));
		}
			
		verify(spc).possuiNegativacao(usuario);	
	}
	
	@Test
	
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		//cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 =umUsuario().comNome("Usuario em dia").agora();
		Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();
		
		List<Locacao> locacoes = Arrays.asList(
						umLocacao().atrasado().comUsuario(usuario).agora(),
						umLocacao().comUsuario(usuario2).agora(),	
						umLocacao().atrasado().comUsuario(usuario3).agora(),
						umLocacao().atrasado().comUsuario(usuario3).agora());
		when(dao.obterLocacaoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();
		
		//verificacao
				verify(email, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class));
				verify(email).notificarAtraso(usuario);
				verify(email, Mockito.atLeastOnce()).notificarAtraso(usuario3);
				verify(email, Mockito.never()).notificarAtraso(usuario2);
				Mockito.verifyNoMoreInteractions(email);
				
	}
	
	@Test
	public void deveTratarErronoSpc() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastr?fica"));
		
		//verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");
		
		
		//acao
		service.alugarFilme(usuario, filmes);
		
		
		
	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		//cenario
		Locacao locacao = umLocacao().agora();
		
		//acao
		service.prorrogarLocacao(locacao, 3);
		
		//vericacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeDataDiferencaDias(3));
	}
		
		@Test
		
		public void deveCalcularValorLocacao() throws Exception {
			//cenario
			List<Filme> filmes = Arrays.asList(umFilme().agora());
			
			//acao
			Class<LocacaoService> clazz = LocacaoService.class;
			Method motodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
			motodo.setAccessible(true);
			motodo.invoke(service, filmes);
			Double valor =(Double) motodo.invoke(service, filmes);
			
			//verificacao
			
			Assert.assertThat(valor, is (4.0));
								
		}






}

