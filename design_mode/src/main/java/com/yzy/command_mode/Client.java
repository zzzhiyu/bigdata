package com.yzy.command_mode;

import com.yzy.observer_mode.School;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

interface Command {
    void execute();
}

class Order {
    private int diningTable;

    private Map<String, Integer> foodDic = new HashMap<>();

    public int getDiningTable() {
        return diningTable;
    }

    public void setDiningTable(int diningTable) {
        this.diningTable = diningTable;
    }

    public Map<String, Integer> getFoodDic() {
        return foodDic;
    }

    public void setFoodDic(String name, int num) {
        this.foodDic.put(name, num);
    }
}

class SeniorChef {
    public void makeFood(int num, String foodName){
        System.out.println(num + "份" + foodName);
    }
}

class OrderCommad implements Command{
    private SeniorChef receiver;
    private Order order;

    public OrderCommad(SeniorChef receiver, Order order) {
        this.receiver = receiver;
        this.order = order;
    }

    @Override
    public void execute() {
        System.out.println(order.getDiningTable() + "桌的订单：");
        Set<String> keys = order.getFoodDic().keySet();
        for (String key : keys) {
            receiver.makeFood(order.getFoodDic().get(key),key);
        }

        try {
            Thread.sleep(100);//停顿一下 模拟做饭的过程
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(order.getDiningTable() + "桌的饭弄好了");
    }
}

class Waitor {
    private ArrayList<Command> commands;

    public Waitor() {
        commands = new ArrayList<>();
    }

    public void setCommands(Command command) {
        commands.add(command);
    }

    public void orderUp() {
        System.out.println("美女服务员：叮咚，大厨，新订单来了.......");
        for (int i = 0; i < commands.size(); i++) {
            Command cmd = commands.get(i);
            if (cmd != null) {
                cmd.execute();
            }
        }
    }
}


public class Client {
    public static void main(String[] args) {
        Order order1 = new Order();
        order1.setDiningTable(1);
        order1.getFoodDic().put("西红柿鸡蛋面", 1);
        order1.getFoodDic().put("小杯可乐", 1);

        SeniorChef seniorChef = new SeniorChef();

        OrderCommad orderCommad = new OrderCommad(seniorChef, order1);

        Waitor waitor = new Waitor();
        waitor.setCommands(orderCommad);

        waitor.orderUp();

    }
}


