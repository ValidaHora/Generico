package validahora.backoffice.sementes;

import haroldo.util.sql.ConexaoDB;
import haroldo.util.sql.SqlDB;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TokenDB extends SqlDB
{
  /**
   * Construtor.
   * 
   * @param conn
   */
  public TokenDB(ConexaoDB conn)
  {
    super(conn);
  }

  /**
   * Lê todos os tokens cujas sementes não estão criptografadas.
   * 
   * @param idCliente
   * @return
   * @throws SQLException
   */
  public ArrayList<TokenMO> leTokensSemCriptografia() throws SQLException
  {
    String query = "SELECT IdToken, CodCriptografia, Semente, CodAlgoritmo "
              + " FROM validahora.token "
              + " WHERE CodCriptografia = 0 ";
    PreparedStatement stmt = connDB.getConn().prepareStatement(query);

    //  Executa a query
    ResultSet rs = stmt.executeQuery();

    ArrayList<TokenMO> tokensMO = new ArrayList<TokenMO>();

    while (rs.next())
      tokensMO.add(preencheResultSet(rs));

    return tokensMO;
  }

  /**
   * Atualiza semente com o valor criptografado.
   * 
   * @param idToken
   * @param codCriptografia
   * @param sementeCriptografada
   * @return
   * @throws SQLException
   */
  public int atualizaSemente(int idToken, int codCriptografia, String sementeCriptografada) throws SQLException
  {
    String query = "UPDATE validahora.token SET CODCRIPTOGRAFIA = ?, SEMENTE = ? WHERE IDTOKEN = ?";

    PreparedStatement stmt = connDB.getConn().prepareStatement(query);
    stmt.setInt(1, codCriptografia);
    stmt.setString(2, sementeCriptografada);
    stmt.setInt(3, idToken);

    return stmt.executeUpdate();
  }

  /**
   * Preenche o TokenMO com os valores restornados no resultset.
   * 
   * @param rs
   * @return
   * @throws SQLException
   */
  private TokenMO preencheResultSet(ResultSet rs) throws SQLException
  {
    TokenMO tokenMO = new TokenMO();

    tokenMO.setIdToken(rs.getInt("IdToken"));
    tokenMO.setCodCriptografia(rs.getInt("CodCriptografia"));
    tokenMO.setSemente(rs.getString("Semente"));
    tokenMO.setCodAlgoritmo(rs.getInt("CodAlgoritmo"));

    return tokenMO;
  }
}
