package haroldo.util.seguranca;

import java.util.zip.CRC32;

public class SegUtil
{
  public static String calculaCheckSum(String hashCode)
  {
    CRC32 crc = new CRC32();
    crc.update(hashCode.getBytes());
    
    String sCrc = Long.toHexString(crc.getValue());
    String checkSum = "00000000".substring(0, 8 - sCrc.length()) + sCrc;

    return checkSum;
  }

}
