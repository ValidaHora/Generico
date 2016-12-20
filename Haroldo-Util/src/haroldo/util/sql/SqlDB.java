package haroldo.util.sql;

public class SqlDB
{
  protected ConexaoDB connDB = null;

  /**
   * Construtor que recebe o objeto de conex�o.
   * 
   * @param conexao
   */
  public SqlDB(ConexaoDB conn)
  {
    this.connDB = conn;
  }

  public final ConexaoDB getConn()
  {
    return connDB;
  }
}
