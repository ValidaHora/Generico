package haroldo.util.sql;

import haroldo.util.sql.ConexaoDB;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.joda.time.DateTime;

public class SqlUtilMySql 
{
  /**
   * Retorna o último id que foi usado no insert de uma tabela com auto incremento.
   * 
   * @param connDB
   * @return
   * @throws SQLException
   */
  public static long leUltimoIdAutoIncremento(ConexaoDB connDB) throws SQLException
  {
    PreparedStatement stmt = connDB.getConn().prepareStatement("SELECT LAST_INSERT_ID()");
    
    //  Executa a query
    ResultSet rs = stmt.executeQuery();

    //  Lê os resultados.
    rs.next();
    return rs.getLong(1);
  }

  /**
   * Retorna a hora do servidor.
   * 
   * @param connDB
   * @return
   * @throws SQLException
   */
  public static DateTime leHoraServidor(ConexaoDB connDB) throws SQLException
  {
    PreparedStatement stmt = connDB.getConn().prepareStatement("SELECT CURRENT_TIMESTAMP");
    
    //  Executa a query
    ResultSet rs = stmt.executeQuery();

    //  Lê os resultados.
    rs.next();
    return SqlUtil.fromSqlTimestamp(rs.getTimestamp(1));
  }
}
