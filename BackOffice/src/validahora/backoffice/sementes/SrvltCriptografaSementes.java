package validahora.backoffice.sementes;

import haroldo.util.sql.ConexaoDB;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import validahora.token.SementeException;
import validahora.token.criptografia.CriptografiaException;

/**
 * Servlet que criptografa todas as sementes que estão com texto em aberto.
 */
@WebServlet(description = "Criptografa as sementes do BD.", urlPatterns = { "/SrvltCriptografaSementesCodigo1" })
public class SrvltCriptografaSementes extends HttpServlet
{
  private static final long serialVersionUID = -5487542893142193397L;
  private static final Logger log = LogManager.getLogger();
  private static final int CODCRIPTOGRAFIASENHA = 1;
  
  String htmlIni = "<html><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />"
              + "<title>Criptografando Sementes</title></head><body>";
  String htmlFim = "</body></html>";


  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    log.info("SrvltCriptografaSementes: Iniciando");
    response.getWriter().println(htmlIni);
    response.getWriter().println("Iniciando a criptografia das sementes<BR>");
    response.flushBuffer();
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");

    ConexaoDB connDB = null;
    try
    {
      connDB = new ConexaoDB("jdbc/ValidaHora");
      TokenDB tokenDB = new TokenDB(connDB);
      
      ArrayList<TokenMO> tokensMO = tokenDB.leTokensSemCriptografia();
      
      int cnt = 0;
      for (TokenMO tokenMO : tokensMO)
      {
        if (tokenMO.getCodAlgoritmo() == 1000)
          continue;
        
        log.debug("Criptografando semente do token id = {}", tokenMO.getIdToken());
        if (cnt % 10 == 0)
          response.getWriter().print(".");
        if (cnt % 200 == 0)
          response.getWriter().println("<BR>");
        response.flushBuffer();
        
        CriptografaSemente sementeCripto = new CriptografaSemente(tokenMO.getIdToken(), CODCRIPTOGRAFIASENHA, tokenMO.getSemente());
        tokenDB.atualizaSemente(tokenMO.getIdToken(), CODCRIPTOGRAFIASENHA, sementeCripto.toString());
      }
      
      connDB.getConn().commit();
    }
    catch (SQLException e)
    {
      log.error("SQLException: " + e.getMessage());
      e.printStackTrace();
    }
    catch (NamingException e)
    {
      log.error("NamingException: " + e.getMessage());
      e.printStackTrace();
    }
    catch (SementeException e)
    {
      log.error("SementeException: " + e.getMessage());
      e.printStackTrace();
    }
    catch (CriptografiaException e)
    {
      log.error("CriptografiaException: " + e.getMessage());
      e.printStackTrace();
    }
//  catch (Throwable t)
//  {
//    log.fatal(t.getMessage());
//    t.printStackTrace();
//  }
    finally
    {
      connDB.fechaConexao();
    }

    //  Retorna a mensagem de resposta.
    log.info("SrvltCriptografaSementes: Fim");
    
    response.getWriter().println("<BR>Criptografia das sementes finalizada");
    response.getWriter().println(htmlFim);
  }
}
