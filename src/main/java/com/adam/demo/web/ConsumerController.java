package com.adam.demo.web;

import com.adam.demo.entity.User;
import com.adam.demo.service.UserService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequestMapping("/consumer")
@RestController
public class ConsumerController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String product="MoonCake5";

    public static final String SERVICEID = "eureka-provider-demo-my";


    @HystrixCommand(fallbackMethod = "getUserInfofallback")
    @RequestMapping(value = "/getUserInfoByRestTemplate",method = RequestMethod.POST)
    public String getUserInfo() {

        String result = restTemplate.postForObject("http://" + SERVICEID + "/provider/getUserInfo/444", null, String.class);
        System.out.println("==========调用结果为："+result.toString()+"==========");
        return result;
    }

    public String getUserInfofallback() {
        return "getUserInfoByRestTemplate方法熔断:触发降级方法 ";
    }


    @RequestMapping(value = "/getUserInfoByFeign",method = RequestMethod.POST)
    public String getUserInfoByFeign(String id) {

        User user = userService.getUserInfo(id);
        System.out.println("==========调用结果为："+user.toString()+"==========");
        return "调用user信息成功！";
    }



    /**
     * 使用线程池定义了20个线程
     * 模拟1000个线程同时执行业务，修改资源
     * 模拟了1000个线程，通过线程池方式提交，每次20个线程抢占分布式锁，抢到分布式锁的执行代码，没抢到的等待
     * @return
     */
    @GetMapping("/submitOrder")
    public String submitOrder(){
        //使用线程池定义了20个线程
        try {
//            stringRedisTemplate.opsForValue().set("stock5", String.valueOf(100));
            ExecutorService service = Executors.newFixedThreadPool(20);
            for (int i=0;i<1000;i++){
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        RLock lock = redissonClient.getLock(product);
                        lock.lock();//加锁，阻塞
//                      boolean b = lock.tryLock();//非阻塞
                        int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock5"));
                        if (stock>0){
                            //下单
                            stock-=1;
                            stringRedisTemplate.opsForValue().set("stock5", String.valueOf(stock));
                            System.err.println("=============当前线程============"+Thread.currentThread().getName()+"订单提交，扣减成功，stock5："+stock);
                        }else {
                            //没库存
                            System.out.println("=============当前线程============"+Thread.currentThread().getName()+"人太多了，已被买光，订单提交失败了!");
                        }
                        lock.unlock();//释放锁
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
//            lock.unlock();//释放锁
        }
        return "end";
    }
}
