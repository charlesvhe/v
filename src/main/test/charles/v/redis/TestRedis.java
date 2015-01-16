package charles.v.redis;

import org.junit.Test;
import redis.clients.jedis.Jedis;

public class TestRedis {
    @Test
    public void testRedis(){
        Jedis jedis = new Jedis("localhost");
//        jedis.set("foo", "bar");
        String value = jedis.get("foo");
        System.out.println(value);
    }
}
