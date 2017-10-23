package jenkins.plugins.logstash.persistence;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;

import org.apache.http.impl.client.HttpClientBuilder;


public class LogstashDao extends AbstractLogstashIndexerDao {

  final String logstashHost;
  final int logstashPort;
  Socket logstashClientSocket;

  public LogstashDao(String logstashHostString, int logstashPortInt, String indexKey, String username, String password, String proxyHost, int proxyPort) {
    this(null, logstashHostString, logstashPortInt, indexKey, username, password, proxyHost, proxyPort);
  }

  public LogstashDao(HttpClientBuilder factory, String logstashHostString, int logstashPortInt, String indexKey, String username, String password, String proxyHost, int proxyPort) {
    super(logstashHostString, logstashPortInt, indexKey, username, password, proxyHost, proxyPort);
    this.logstashHost = logstashHostString;
    this.logstashPort = logstashPortInt;
  }

  @Override
  public void push(String data) throws IOException {

    try
    {
      logstashClientSocket = new Socket(logstashHost, logstashPort);
      DataOutputStream outToServer = new DataOutputStream(logstashClientSocket.getOutputStream());
      BufferedReader inFromServer = new BufferedReader(new InputStreamReader(logstashClientSocket.getInputStream()));
      outToServer.writeBytes(data);
      logstashClientSocket.setSoTimeout(10000);
      logstashClientSocket.close();
      outToServer.close();
      inFromServer.close();
    }
    catch (Exception exc)
    {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      exc.printStackTrace(pw);
      throw new IOException(sw.toString());
    }

  }

  @Override
  public IndexerType getIndexerType() { return IndexerType.LOGSTASH; }
}
