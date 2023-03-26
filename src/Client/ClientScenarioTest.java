package Client;

import static org.junit.Assert.*;

import org.junit.Test;

public class ClientScenarioTest {
    /**
     * Test case 1: Join
     * Two clients join, one subscribe "Sports",
     * Another one subscribe to "Entertainment", one client publishes an "Entertainment" article
     * How To Run: Test case 1 will run in Java main method.
     * In client #1 object, test will run "join(127.0.0.1, 1099)" method.
     * In client #1 object, test will run "subscribe("127.0.0.1", 1098, "Sports;;;");" method.
     * In client #2 object, test will run "join(127.0.0.1, 1099)" method.
     * In client #2 object, test will run "subscribe("127.0.0.1", 1098, "Entertainment;;;");" method.
     * In client #1 object, test will run "publish("Entertainment;;UMN;contents2","127.0.0.1", 1099);". method
     * Observe the results in both client #1 and client #2 terminals.
     * Expected results:
     * Client #2 should receive the published article.
     * Client #1 should not receive the article they published.
     * Test passed if the above steps are executed successfully, and expected results are observed in terminal.
     */
    @Test
    public void testCase1()  {
        Client client1 = new Client();
        Client client2 = new Client();
        boolean join_status = client1.join("127.0.0.1", 1099);
        boolean join_status2 = client2.join("127.0.0.1", 1098);
        client1.subscribe("127.0.0.1", 1099, "Sports;Jim;;");
        client2.subscribe("127.0.0.1", 1098, "Entertainment;Jim;;");
        client1.receiveUDP(1099,"127.0.0.1",client1);
        client2.receiveUDP(1098,"127.0.0.1",client2);
        boolean publish_status = client1.publish("Entertainment;Jim;UMN;contents2","127.0.0.1", 1099);
        System.out.println("publish status is " + publish_status);
        String msg1 = client1.getCurrentMessage();
        String msg2 = client2.getCurrentMessage();
        System.out.println("msg1 is " + msg1);
        System.out.println("msg2 is " + msg2);
        assertNull(msg1);
        assertEquals("Entertainment;Jim;UMN;contents2", msg2);
        client1.leave("127.0.0.1", 1099);
        client2.leave("127.0.0.1", 1098);

    }

    /**
        * Test case 2: Leave
        * Two clients join, both subscribe "Sports",
        * one client publishes an "Sports" article,
        * one client leaves the group,
        * one client publishes another "Sports" article,
        * Observe the results in both client #1 and client #2 terminals.
        * Expected results:
        * Client #2 should receive the first published article.
        * Client #1 should not receive the second published article.
        * Test Passed if the above steps are executed successfully, and expected results are observed in terminal.
        */
    @Test
    public void testCase2() {
        Client client1 = new Client();
        Client client2 = new Client();
        boolean join_status = client1.join("127.0.0.1", 1097);
        boolean join_status2 = client2.join("127.0.0.1", 1096);
        client1.subscribe("127.0.0.1",1097,"Sports;;;");
        client2.subscribe("127.0.0.1",1096,"Sports;;;");
        client1.receiveUDP(1097,"127.0.0.1",client1);
        client2.receiveUDP(1096,"127.0.0.1",client2);
        boolean publish_status = client1.publish("Sports;;UMN;contents1","127.0.0.1", 1097);
        System.out.println("publish status is " + publish_status);
        String msg1 = client1.getCurrentMessage();
        String msg2 = client2.getCurrentMessage();
        System.out.println("msg1 is " + msg1);
        System.out.println("msg2 is " + msg2);
        assertEquals("Sports;;UMN;contents1", msg1);
        assertEquals("Sports;;UMN;contents1", msg2);
        client1.leave("127.0.0.1", 1097);
        boolean publish_status2 = client2.publish("Sports;;UMN;contents2","127.0.0.1", 1096);
        System.out.println("publish status is " + publish_status2);
        String msg3 = client1.getCurrentMessage();
        String msg4 = client2.getCurrentMessage();
        System.out.println("msg3 is " + msg3);
        System.out.println("msg4 is " + msg4);
        assertEquals("Sports;;UMN;contents1", msg3);
        assertEquals("Sports;;UMN;contents2", msg4);
        client2.leave("127.0.0.1", 1096);
        client1.leave("127.0.0.1", 1097);
    }

    /**
     * Test case 3: client subscribe to the multiple topics
     * One client join, subscribe "Sports",
     * then subscribe "Sports and UMN" article,
     * Observe the number of UDP that client #1 received.
     * Expected results:
     * Client #1 should receive the UPD only 1 time.
     * Test Passed if the above steps are executed successfully, and expected results are observed in terminal.
     */
    @Test
    public void testCase3() throws InterruptedException {
        Client client1 = new Client();
        boolean join_status = client1.join("127.0.0.1", 1095);
        client1.subscribe("127.0.0.1", 1095,"Sports;;;");
        client1.subscribe("127.0.0.1", 1095,"Sports;;UMN;");
        client1.receiveUDP(1095, "127.0.0.1", client1);
        client1.publish("Sports;;UMN;UMN wins", "127.0.0.1", 1095);
        Thread.sleep(200);
        assertEquals(1, client1.getUDPCount());
        client1.leave("127.0.0.1", 1095);
    }

    /**
     * Test case 4: client get articles in accord with his subscribe
     * client1 join, client2 join,
     * client1 subscribe "Politics", 
     * then client2 publish "Politics;;UMN;UMN wins",
     * Observe the number of UDP of client #1 and the received msg.
     * client1 unsubscribe "Politics",
     * then client1 subscribe "Politics;;UMN;",
     * Observe the number of UDP of client #1 and the received msg.
     * client2 publish "Politics;;;UMN wins wins 2",
     * Observe the number of UDP of client #1 and the received msg.
     * Expected results:
     * Client #1 first receive msg "Politics;;UMN;UMN wins", and UDP count of Client #1 is 1.
     * Then after second subscribe, Client #1 first receive msg "Politics;;UMN;UMN wins", and UDP count of Client #1 is 2.
     * Then after second publish, Client #1 first does not receive msg ""Politics;;;UMN wins wins 2"", and UDP count of Client #1 keeps 2.
     * Test Passed if the above steps are executed successfully, and expected results are observed in terminal.
     */
    @Test
    public void testCase4() throws InterruptedException {
        Client client1 = new Client();
        Client client2 = new Client();
        boolean join_status = client1.join("127.0.0.1", 1094);
        boolean join_status2 = client2.join("127.0.0.1", 1093);
        client1.setUDPCount(0);
        client1.subscribe("127.0.0.1", 1094,"Politics;Tony;;"); // subscribe "Sports"
        client1.receiveUDP(1094, "127.0.0.1", client1);
        client2.publish("Politics;Tony;UMN;UMN wins", "127.0.0.1", 1093);
        Thread.sleep(200);
        assertEquals(1, client1.getUDPCount());
        assertEquals("Politics;Tony;UMN;UMN wins", client1.getCurrentMessage());
        client1.unsubscribe("127.0.0.1", 1094,"Politics;Tony;;"); // unsubscribe "Sports
        client1.subscribe("127.0.0.1", 1094,"Politics;Tony;UMN;"); // subscribe "Sports and UMN"
        assertEquals(2, client1.getUDPCount());
        assertEquals("Politics;Tony;UMN;UMN wins", client1.getCurrentMessage());
        client2.publish("Politics;Tony;;UMN wins wins 2", "127.0.0.1", 1093);
        assertEquals(2, client1.getUDPCount());
        assertEquals("Politics;Tony;UMN;UMN wins", client1.getCurrentMessage());
    }

}
