package me.prettyprint.cassandra.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import me.prettyprint.cassandra.model.Keyspace;
import me.prettyprint.cassandra.testutils.EmbeddedServerHelper;

import org.apache.cassandra.service.ConsistencyLevel;
import org.apache.cassandra.service.NotFoundException;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Ran Tavory (rantav@gmail.com)
 *
 */
public class CassandraClientTest {

  private static EmbeddedServerHelper embedded;

  private CassandraClient client;

  /**
   * Set embedded cassandra up and spawn it in a new thread.
   *
   * @throws TTransportException
   * @throws IOException
   * @throws InterruptedException
   */
  @BeforeClass
  public static void setup() throws TTransportException, IOException, InterruptedException {
    embedded = new EmbeddedServerHelper();
    embedded.setup();
  }

  @AfterClass
  public static void teardown() throws IOException {
    embedded.teardown();
  }

  @Before
  public void setupCase() throws TTransportException, TException {
    client = new CassandraClientFactory().create("localhost", 9170);
  }

  @Test
  public void testGetKeySpaceString()
      throws IllegalArgumentException, NotFoundException, TException {
    Keyspace k = client.getKeySpace("Keyspace1");
    assertNotNull(k);
    assertEquals(CassandraClient.DEFAULT_CONSISTENCY_LEVEL, k.getConsistencyLevel());

    // negative path
    try {
      k = client.getKeySpace("KeyspaceDoesntExist");
      fail("Should have thrown an exception IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // good
    }
  }

  @Test
  public void testGetKeySpaceStringConsistencyLevel()
      throws IllegalArgumentException, NotFoundException, TException {
    Keyspace k = client.getKeySpace("Keyspace1", ConsistencyLevel.ALL);
    assertNotNull(k);
    assertEquals(ConsistencyLevel.ALL, k.getConsistencyLevel());

    k = client.getKeySpace("Keyspace1", ConsistencyLevel.ZERO);
    assertNotNull(k);
    assertEquals(ConsistencyLevel.ZERO, k.getConsistencyLevel());
  }

  @Test
  public void testGetStringProperty() throws TException {
    String prop = client.getStringProperty("cluster name");
    assertEquals("Test Cluster", prop);
  }

  @Test
  public void testGetKeyspaces() throws TException {
    List<String> spaces = client.getKeyspaces();
    assertNotNull(spaces);
    // There should be two spaces: Keyspace1 and system
    assertEquals(2, spaces.size());
    assertTrue("Keyspace1".equals(spaces.get(0)) || "Keyspace1".equals(spaces.get(1)));
  }

  @Test
  public void testGetClusterName() throws TException {
    String name = client.getClusterName();
    assertEquals("Test Cluster", name);
  }

  @Test
  public void testGetTokenMap() throws TException {
    String map = client.getTokenMap();
    assertNotNull(map);
    assertTrue(map.indexOf("127.0.0.1") > 0);
  }

  @Test
  public void testGetConfigFile() throws TException {
    String config = client.getConfigFile();
    assertNotNull(config);
    assertTrue(config.length() > 0);
  }
}