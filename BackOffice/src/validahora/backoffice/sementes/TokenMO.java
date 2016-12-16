package validahora.backoffice.sementes;

public class TokenMO
{

  private int    IdToken;
  private int    codCriptografia;
  private String Semente;
  private int    codAlgoritmo;

  public int getIdToken()
  {
    return IdToken;
  }

  public void setIdToken(int idToken)
  {
    IdToken = idToken;
  }

  public int getCodCriptografia()
  {
    return codCriptografia;
  }

  public void setCodCriptografia(int codCriptografia)
  {
    this.codCriptografia = codCriptografia;
  }

  public String getSemente()
  {
    return Semente;
  }

  public void setSemente(String semente)
  {
    Semente = semente;
  }

  public int getCodAlgoritmo()
  {
    return codAlgoritmo;
  }

  public void setCodAlgoritmo(int codAlgoritmo)
  {
    this.codAlgoritmo = codAlgoritmo;
  }
}
