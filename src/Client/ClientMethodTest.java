package Client;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ClientMethodTest {

    @Test
    //join: A client attempts to join the server
    //Expected result: The client should be able to join the server, and return true
    public void testJoin() {
        Client client = new Client();
        boolean joinResult = client.join("127.0.0.1", 1099);
        assertTrue(joinResult);
        System.out.println("Test Case 1: Join - PASSED");
        client.leave("127.0.0.1", 1099);
    }

    @Test
    //leave: A client attempts to leave the server after join the server
    //Expected result: The client should be able to leave the server, and return true
    public void testLeave() {
        Client client = new Client();
        client.join("127.0.0.1", 1099);
        client.leave("127.0.0.1", 1099);
        System.out.println("Test Case 2: Leave - PASSED");
    }

    @Test
    //subscribe: A client attempts to subscribe to a topic
    //Expected result: The client should be able to subscribe to a topic, and return true
    public void testSubscribe() {
        Client client = new Client();
        client.join("127.0.0.1", 1099);
        boolean subscribeResult = client.subscribe("127.0.0.1", 1099, "Sports;;;");
        assertTrue(subscribeResult);
        System.out.println("Test Case 3: Subscribe - PASSED");
        client.leave("127.0.0.1", 1099);
    }

    @Test
    //unsubscribe: A client attempts to unsubscribe to a topic after subscribe to a topic
    //Expected result: The client should be able to unsubscribe to a topic, and return true
    public void testUnsubscribe() {
        Client client = new Client();
        client.join("127.0.0.1", 1099);
        client.subscribe("127.0.0.1", 1099, "Sports;;;");
        boolean unsubscribeResult = client.unsubscribe("127.0.0.1", 1099, "Sports;;;");
        assertTrue(unsubscribeResult);
        System.out.println("Test Case 4: Unsubscribe - PASSED");
        client.leave("127.0.0.1", 1099);
    }

    @Test
    //unsubscribeAll: A client attempts to unsubscribe to all topics after subscribe to all topic
    //Expected result: The client should be able to unsubscribe to all topics, and return true
    public void testUnsubscribeAll() {
        Client client = new Client();
        client.join("127.0.0.1", 1099);
        String[] types = new String[]{"Sports", "Lifestyle", "Entertainment", "Business", "Technology", "Science", "Politics", "Health"};
        client.subscribe("127.0.0.1", 1099, types[0]);
        client.subscribe("127.0.0.1", 1099, types[1]);
        client.subscribe("127.0.0.1", 1099, types[2]);
        client.subscribe("127.0.0.1", 1099, types[3]);
        client.subscribe("127.0.0.1", 1099, types[4]);
        client.subscribe("127.0.0.1", 1099, types[5]);
        client.subscribe("127.0.0.1", 1099, types[6]);
        client.subscribe("127.0.0.1", 1099, types[7]);
        client.publish(types[0] + ";;UMN;contents0", "127.0.0.1", 1099);
        client.publish(types[1] + ";;UMN;contents1", "127.0.0.1", 1099);
        client.publish(types[2] + ";;UMN;contents2", "127.0.0.1", 1099);
        client.publish(types[3] + ";;UMN;contents3", "127.0.0.1", 1099);
        client.publish(types[4] + ";;UMN;contents4", "127.0.0.1", 1099);
        client.publish(types[5] + ";;UMN;contents5", "127.0.0.1", 1099);
        client.publish(types[6] + ";;UMN;contents6", "127.0.0.1", 1099);
        client.publish(types[7] + ";;UMN;contents7", "127.0.0.1", 1099);
        assertTrue(client.unsubscribeAll("127.0.0.1", 1099));
        System.out.println("Test Case 5: Unsubscribe All - PASSED");
        client.leave("127.0.0.1", 1099);
    }

    @Test
    //publish: A client attempts to publish a message to a topic
    //Expected result: The client should be able to publish a message to a topic, and return true
    public void testPublish() {
        Client client = new Client();
        client.join("127.0.0.1", 1099);
        boolean publishResult = client.publish("Sports;Someone;UMN;contents", "127.0.0.1", 1099);
        assertTrue(publishResult);
        System.out.println("Test Case 6: Publish - PASSED");
        client.leave("127.0.0.1", 1099);
    }

    @Test
    //greeting: A client attempts to get the greeting message from the server
    //Expected result: The client should be able to get the greeting message from the server, and return true
    public void testGreeting() {
        Client client = new Client();
        String greeting = client.greeting();
        assertNotNull(greeting);
        System.out.println("Test Case 7: Greeting - PASSED");
    }

    @Test
    //ping: A client attempts to ping the server
    //expected result: the server responds and the client receives return true from the ping method
    public void testPing() {
        Client client = new Client();
        client.ping(1000, 3);
        System.out.println("Test Case 8: Ping - PASSED");
    }

    @Test
    //invalid subscribe: A client attempts to subscribe to a topic that does not exist
    //Expected result: The client should not be able to subscribe to a topic, and return false
    public void invalidSubscribe() {
        Client client = new Client();
        client.join("127.0.0.1", 1099);
        boolean subscribeResult = client.subscribe("127.0.0.1", 1099, "Finance;;;");
        assertFalse(subscribeResult);
        System.out.println("Test Case 9: Invalid Subscribe - PASSED");
        client.leave("127.0.0.1", 1099);
    }

    @Test
    //invalid subscribe: A client attempts to subscribe to a topic but with wrong order of parameters
    //Expected result: The client should not be able to subscribe to a topic, and return false
    public void invalidSubscribe2() {
        Client client = new Client();
        client.join("127.0.0.1", 1099);
        boolean subscribeResult = client.subscribe("127.0.0.1", 1099, "UMN;Sports;;contents");
        assertFalse(subscribeResult);
        System.out.println("Test Case 10: Invalid Subscribe - PASSED");
        client.leave("127.0.0.1", 1099);
    }

    @Test
    //invalid unsubscribe: A client attempts to unsubscribe from a topic that it is not subscribed to
    //Expected result: The client should not be able to unsubscribe from a topic, and return false
    public void invalidUnsubscribe() {
        Client client = new Client();
        client.join("127.0.0.1", 1099);
        boolean unsubscribeResult = client.unsubscribe("127.0.0.1", 1099, "Sports;;;");
        assertFalse(unsubscribeResult);
        System.out.println("Test Case 11: Invalid Unsubscribe - PASSED");
        client.leave("127.0.0.1", 1099);
    }

    @Test
    //valid publish: A client attempts to publish to a topic without originator field
    //Expected result: The client should be able to publish to a topic without originator field, and return true
    public void validPublish2() {
        Client client = new Client();
        client.join("127.0.0.1", 1099);
        boolean publishResult = client.publish("Sports;;UMN;contents", "127.0.0.1", 1099);
        assertTrue(publishResult);
        System.out.println("Test Case 12: valid Publish 2 - PASSED");
        client.leave("127.0.0.1", 1099);
    }

    @Test
    //invalid publish 2: A client attempts to publish to a topic that does not exist
    //Expected result: The client should not be able to publish to a topic, and return false
    public void invalidPublish2() {
        Client client = new Client();
        client.join("127.0.0.1", 1099);
        boolean publishResult = client.publish("Finance;;UMN;contents", "127.0.0.1", 1099);
        assertFalse(publishResult);
        System.out.println("Test Case 13: Invalid Publish 2 - PASSED");
        client.leave("127.0.0.1", 1099);
    }
    @Test
    //invalid publish 3: A client attempts to publish with wrong order of fields for the article
    //Expected result: The client should not be able to publish to a topic, and return false
    public void invalidPublish3() {
        Client client = new Client();
        client.join("127.0.0.1", 1099);
        boolean publishResult = client.publish("UMN;Sports;;contents", "127.0.0.1", 1099);
        assertFalse(publishResult);
        System.out.println("Test Case 14: Invalid Publish 3 - PASSED");
        client.leave("127.0.0.1", 1099);
    }

    @Test
    //invalid publish 4: A client attempts to publish without the content field
    //Expected result: The client should not be able to publish to a topic, and return false
    public void invalidPublish4() {
        Client client = new Client();
        client.join("127.0.0.1", 1099);
        boolean publishResult = client.publish("Sports;;UMN;", "127.0.0.1", 1099);
        assertFalse(publishResult);
        System.out.println("Test Case 14: Invalid Publish 4 - PASSED");
        client.leave("127.0.0.1", 1099);
    }

    @Test
    //A client publish a article and server send it to the subscriber right after it is published.
    //Expected result: The client who subscribe this article should receive it.
    public void publishandSend() {
        Client client1 = new Client();
        Client client2 = new Client();
        client1.join("127.0.0.1", 1089);
        client2.join("127.0.0.1", 1088);
        client1.setUDPCount(0);
        client1.receiveUDP(1089,"127.0.0.1",client1);
        client1.subscribe("127.0.0.1", 1089, "Technology;;;");
        client2.publish("Technology;;;contents","127.0.0.1",1088);
        String msg = client1.getCurrentMessage();
        int udpcount = client1.getUDPCount();
        assertEquals("Technology;;;contents", msg);
        assertEquals(1, udpcount);
        client1.leave("127.0.0.1", 1089);
        client2.leave("127.0.0.1", 1088);
    }

    @Test
    //A client subscribe a article type and then server send a article meeting the requirements.
    //Expected result: The client receive the related articles.
    public void subscribeandSend() throws InterruptedException {
        Client client1 = new Client();
        Client client2 = new Client();
        client1.join("127.0.0.1", 1087);
        client2.join("127.0.0.1", 1086);
        client1.setUDPCount(0);
        client1.receiveUDP(1087,"127.0.0.1",client1);
        client2.publish("Business;;;contents","127.0.0.1", 1086);
        client2.publish("Business;;;contents2","127.0.0.1", 1086);
        client1.subscribe("127.0.0.1", 1087, "Business;;;");
        Thread.sleep(200);
        String msg = client1.getCurrentMessage();
        int udpcount = client1.getUDPCount();
        assertEquals("Business;;;contents2", msg);
        assertEquals(2, udpcount);
        client1.leave("127.0.0.1", 1087);
        client2.leave("127.0.0.1", 1086);
    }

    @Test
    //A client subscribe a article type and then server send a article meeting the requirements.
    //The client unsubscribe the ariticle type and then the client do not receive such kinds of article.
    //Expected result: The client do not receive the related articles after unsubscribing.
    public void unsubscribeandnotSend() {
        Client client1 = new Client();
        Client client2 = new Client();
        client1.join("127.0.0.1", 1085);
        client2.join("127.0.0.1", 1084);
        client1.setUDPCount(0);
        client1.receiveUDP(1085,"127.0.0.1",client1);
        client2.publish("Health;;;contents","127.0.0.1", 1084);
        client1.subscribe("127.0.0.1", 1085, "Health;;;");
        String msg = client1.getCurrentMessage();
        int udpcount = client1.getUDPCount();
        assertEquals("Health;;;contents", msg);
        assertEquals(1, udpcount);
        client1.unsubscribe("127.0.0.1", 1085, "Health;;;");
        client2.publish("Health;;;contents2","127.0.0.1", 1084);
        String msg2 = client1.getCurrentMessage();
        int udpcount2 = client1.getUDPCount();
        assertEquals("Health;;;contents", msg2);
        assertEquals(1, udpcount2);
    }

}

