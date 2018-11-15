package com.spring.demo.entity;

/*
@author:zhengzhao
@time: 2018/08/20 
*/
import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Cart {
    public Cart(){
        goods = new HashMap<Items,Integer>();
		totalPrice = 0.0;
    }
    private HashMap<Items,Integer> goods;//商品信息作为key,商品数量作为value
    private double totalPrice;

    public HashMap<Items, Integer> getGoods() {
        return goods;
    }

    public void setGoods(HashMap<Items, Integer> goods) {
        this.goods = goods;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * @param "商品的信息集合"
     * @param "添加的商品数量"
     * @return "是否添加成功"
     */
    public boolean addGoodsInCart(Items item, int num){
        if (goods.containsKey(item)){
            goods.put(item,goods.get(item)+num);
        }
        else {
            goods.put(item,num);
        }

        calTotalPrice();
        return true;
    }

    /**
     * @param "要删除的商品信息集合"
     * @return "是否删除成功"
     */
    public boolean removeGoodsInCart(Items item){
        if(goods.get(item)>0){
            goods.remove(item);
            calTotalPrice();
            return true;
        }else {
            return false;
        }
    }

    /**
     * @param "要减少的商品信息集合"
     * @return "是否减少成功"
     */
    public boolean subGoodsInCart(Items item){
        if(goods.get(item)-1>0){
            goods.put(item,goods.get(item)-1);
            calTotalPrice();
            return true;
        }else {
            return false;
        }
    }

    public double calTotalPrice(){
        double sum = 0.0;
        Set<Items> keys = this.goods.keySet();//获得所有商品信息集合
        Iterator<Items> it = keys.iterator();
        while (it.hasNext()){
            Items i = it.next();
            sum += i.getPrice() * goods.get(i);
        }
        setTotalPrice(sum);
        return getTotalPrice();
    }

    public static void main(String[] args) throws IOException {
//        Items id1 = new Items(1,"李宁男鞋","江苏",300,50,"/images/0001.jpg");
//
        Cart cart = new Cart();
//        cart.addGoodsInCart(id1,2);
//        cart.addGoodsInCart(id1,2);
//
        Set<Map.Entry<Items,Integer>> item = cart.getGoods().entrySet();
//        for (Map.Entry<Items,Integer> obj :item){
//            System.out.println(obj.toString());
//        }
//        System.out.println("总价格为  "+cart.calTotalPrice());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error","dadsadas");
    }
}
