package haroldo.util.sql;

import java.sql.Date;
import java.sql.Timestamp;
import org.joda.time.DateTime;

/**
 * Classe que implementa métodos estáticos para auxílio no uso do SQL.
 * <P>
 * Métodos como transformação de org.joda.time.DateTime para java.sql.Date
 * 
 * @author Haroldo
 *
 */
public class SqlUtil
{
  /**
   * Transforma do formato org.joda.time.DateTime para o formato de data do SQL.
   * 
   * @param dt - Data no formato org.joda.time.DateTime
   * @return - Data no formato java.sql.Date
   */
  public static Date toSqlDate(DateTime dt)
  {
    if (dt == null)
      return null;
    
    return new Date(dt.getMillis());
  }
  
  /**
   * Transforma do formato org.joda.time.DateTime para o formato de timestamp do SQL.
   * 
   * @param dt - Data no formato org.joda.time.DateTime
   * @return - Data no formato java.sql.Timestamp
   */
  public static Timestamp toSqlTimestamp(DateTime dt)
  {
    if (dt == null)
      return null;
    
    return new Timestamp(dt.getMillis());
  }

  /**
   * Transforma do formato Date do SQL para o formato org.joda.time.DateTime
   * 
   * @param dt - Data no formato java.sql.Date.
   * @return Data no formato org.joda.time.DateTime.
   */
  public static DateTime fromSqlDate(Date dt)
  {
    if (dt == null)
      return null;

    return new DateTime(dt);
  }

  /**
   * Transforma do formato Timestamp do SQL para o formato org.joda.time.DateTime
   * 
   * @param dt - Data no formato java.sql.Timestamp.
   * @return Data no formato org.joda.time.DateTime.
   */
  public static DateTime fromSqlTimestamp(Timestamp  dt)
  {
    if (dt == null)
      return null;

    return new DateTime(dt);
  }

//  public static void main(String[] args)
//  {
//
//    System.out.println("" + DateTimeZone.UTC);
//    System.out.println("" + SqlUtil.toSqlTimeZone(DateTimeZone.UTC));
//    System.out.println("" + DateTime.now().getZone());
//    System.out.println("" + SqlUtil.toSqlTimeZone(DateTime.now().getZone()));
//
//    System.out.println("" + SqlUtil.fromSqlTimeZone("-0300"));
//    System.out.println("" + SqlUtil.fromSqlTimeZone("+0330"));
//    System.out.println("" + SqlUtil.fromSqlTimeZone("America/Sao_Paulo"));
//  }
}
