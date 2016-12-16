package haroldo.util.sql;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConexaoDB
{
  private static final Logger log = LogManager.getLogger();

  private static Context envContext = null;
  static {
    try
    {
      init();
    }
    catch (NamingException|SQLException e)
    {
      e.printStackTrace();
    }
  }
  

  private DataSource ds = null;
  private Connection conn = null;

  /**
   * 
   * @throws SQLException
   * @throws NamingException
   */
  public ConexaoDB(String nomeJndi) throws SQLException, NamingException
  {
    log.trace("Construtor objeto ConexaoDB()");
    try
    {
      if (envContext == null)
        init();

      log.trace("Lookup do datasource");
      ds = (DataSource) envContext.lookup(nomeJndi);
    }
    catch (SQLException | NamingException e)
    {
      log.error("Exceção para instanciar ConexaoDB()", e);
      e.printStackTrace();
      throw e;
    }
  }
  
  /**
   * Inicializa a classe.
   */
  private static void init() throws SQLException, NamingException
  {
    log.trace("Chamando ConexaoDB.init()");
    try
    {
      Context initContext = new InitialContext();
      envContext = (Context) initContext.lookup("java:/comp/env");
    }
    catch (Exception e)
    {
      log.error("Problema em ConexaoDB.init(): ", e);
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Inicia e retorna uma conexão.
   * 
   * @throws SQLException
   * @throws NamingException
   */
  private static boolean recuperandoConexao = false;
  public Connection getConn() throws SQLException
  {
    log.trace("Obtendo conexão com o BD.");
    if (ds == null)
      return null;

    if (conn != null)
      return conn;

    try
    {
      long msIni = System.currentTimeMillis();
      conn = ds.getConnection();
      long msFim = System.currentTimeMillis();
      log.trace("Conexão obtida em {} milisegundos.", msFim - msIni);
      conn.setAutoCommit(false);
    }
    catch (SQLException e1)
    {
      if (!recuperandoConexao)
      {
        boolean recConMetodo = false;
        //  Tentando se recuperar de exceção
        log.error("Tentando se recuperar de exceção: ", e1);
        synchronized (envContext)
        {
          log.debug("Entrando no bloco sincronizado.");
          if (!recuperandoConexao)
          {
            recuperandoConexao = true;
            recConMetodo = true;  //  Marca como método que está tentando resolver o problema de conexão.
            log.debug("Processo de recuperação iniciado.");
            try
            {
              log.debug("Recuperação de conexão: Chamando init()...");
              init();
              log.debug("Recuperação de conexão: Retorno do init()");
            }
            catch (NamingException | SQLException einit)
            {
              log.fatal("Exceção não recuperada: ", einit);
              einit.printStackTrace();
            }
          }
        }
        
        if (recConMetodo)
        {
          // Aguarda 5 segundos para religar o flag, liberando as threads que eventualmente estivessem bloqueadas no synchronized.  
          try {Thread.sleep(5 * 1000);} catch (Exception t){ /* Nada a fazer */}
          log.debug("Fim da recuperação da conexão.");
          recuperandoConexao = false;
        }
      }
      else
      {
        //  Se outra thread estiver tentando recuperar, aguarda 10 segundos.
        log.info("Aguardando recuperação de conexão...");
        try {Thread.sleep(10 * 1000);} catch (Exception t){ /* Nada a fazer */}
      }
      
      //
      //  Segunda tentativa de conectar.
      try
      {
        log.info("Segunda tentativa de conexao.");
        conn = ds.getConnection();
        conn.setAutoCommit(false);
        log.info("Conexao recuperada!");
      }
      catch (SQLException e2)
      {
        log.error("Problema na 2a conexão com o BD", e2);
        e2.printStackTrace();
        throw e2;
      }
    }
    
    return conn;
  }

  /**
   * Para ser usado no "finalize" para fechar conexão.
   * Fecha uma conexão sem se preocupar com as mensagens de exceção que possam ocorrer.
   * 
   * @param conexao
   */
  public void fechaConexao()
  {
    log.trace("Fechando conexao com o BD.");
    if (conn == null)
      return;
    
    try
    {
      log.trace("Executando rollback() para fechar conexão.");
      conn.rollback();
    }
    catch (SQLException e)
    {
      log.info("Erro durante o fechamento de conexão. Erro ignorado.", e);
      //  Não faz nada!
    }
    try
    {
      log.trace("Chamando o close() para fechar conexão.");
      conn.close();
    }
    catch (SQLException e)
    {
      log.info("Erro durante o fechamento de conexão. Erro ignorado.", e);
      //  Não faz nada!
    }
  }
}
