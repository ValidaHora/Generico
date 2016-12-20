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
      log.error("Exce��o para instanciar ConexaoDB()", e);
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
   * Inicia e retorna uma conex�o.
   * 
   * @throws SQLException
   * @throws NamingException
   */
  private static boolean recuperandoConexao = false;
  public Connection getConn() throws SQLException
  {
    log.trace("Obtendo conex�o com o BD.");
    if (ds == null)
      return null;

    if (conn != null)
      return conn;

    try
    {
      long msIni = System.currentTimeMillis();
      conn = ds.getConnection();
      long msFim = System.currentTimeMillis();
      log.trace("Conex�o obtida em {} milisegundos.", msFim - msIni);
      conn.setAutoCommit(false);
    }
    catch (SQLException e1)
    {
      if (!recuperandoConexao)
      {
        boolean recConMetodo = false;
        //  Tentando se recuperar de exce��o
        log.error("Tentando se recuperar de exce��o: ", e1);
        synchronized (envContext)
        {
          log.debug("Entrando no bloco sincronizado.");
          if (!recuperandoConexao)
          {
            recuperandoConexao = true;
            recConMetodo = true;  //  Marca como m�todo que est� tentando resolver o problema de conex�o.
            log.debug("Processo de recupera��o iniciado.");
            try
            {
              log.debug("Recupera��o de conex�o: Chamando init()...");
              init();
              log.debug("Recupera��o de conex�o: Retorno do init()");
            }
            catch (NamingException | SQLException einit)
            {
              log.fatal("Exce��o n�o recuperada: ", einit);
              einit.printStackTrace();
            }
          }
        }
        
        if (recConMetodo)
        {
          // Aguarda 5 segundos para religar o flag, liberando as threads que eventualmente estivessem bloqueadas no synchronized.  
          try {Thread.sleep(5 * 1000);} catch (Exception t){ /* Nada a fazer */}
          log.debug("Fim da recupera��o da conex�o.");
          recuperandoConexao = false;
        }
      }
      else
      {
        //  Se outra thread estiver tentando recuperar, aguarda 10 segundos.
        log.info("Aguardando recupera��o de conex�o...");
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
        log.error("Problema na 2a conex�o com o BD", e2);
        e2.printStackTrace();
        throw e2;
      }
    }
    
    return conn;
  }

  /**
   * Para ser usado no "finalize" para fechar conex�o.
   * Fecha uma conex�o sem se preocupar com as mensagens de exce��o que possam ocorrer.
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
      log.trace("Executando rollback() para fechar conex�o.");
      conn.rollback();
    }
    catch (SQLException e)
    {
      log.info("Erro durante o fechamento de conex�o. Erro ignorado.", e);
      //  N�o faz nada!
    }
    try
    {
      log.trace("Chamando o close() para fechar conex�o.");
      conn.close();
    }
    catch (SQLException e)
    {
      log.info("Erro durante o fechamento de conex�o. Erro ignorado.", e);
      //  N�o faz nada!
    }
  }
}
