package validahora.backoffice.sementes;

import validahora.token.Semente;
import validahora.token.SementeException;
import validahora.token.criptografia.CriptografiaException;

/**
 * Classe que extende a classe Semente para implementar o método de criptografia de sementes.
 * 
 * @author Haroldo
 *
 */
public class CriptografaSemente extends Semente
{

  public CriptografaSemente(int idToken, int codCriptografia, String sementeAberta) throws SementeException, CriptografiaException
  {
    super(idToken, codCriptografia, "");
    setSementeCripto(criptografia.criptografa(sementeAberta));
  }
}
